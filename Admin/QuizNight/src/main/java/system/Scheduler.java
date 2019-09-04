package system;

import json.QuizSettings;
import module.Load;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiz.Quiz;
import commands.CmdUtil;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Scheduler extends Load
{
    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private static OffsetDateTime utc;
    private static ArrayList<QuizSettings.GuildQuizConfig> daygqc;
    private static ArrayList<QuizSettings.GuildQuizConfig> hourgqc;

    @Override
    public void load()
    {
        new Thread(() -> {quizLauncher();}).start();
    }

    private void quizLauncher()
    {
        try
        {
            utc = OffsetDateTime.now(ZoneOffset.UTC);
            int dayOfWeek = utc.getDayOfWeek().getValue();
            daygqc = SettingsManager.getDayQuizzes(dayOfWeek);
            hourgqc = new ArrayList<>();
            int currentHour = utc.getHour()-1;

            while (utc.getDayOfWeek().getValue() == dayOfWeek)
            {
                utc = OffsetDateTime.now(ZoneOffset.UTC);
                if (currentHour != utc.getHour())
                {
                    currentHour = utc.getHour();
                    hourgqc = new ArrayList<>();
                    for (QuizSettings.GuildQuizConfig gqc : daygqc)
                    {
                        if (gqc.StartMinute >= (utc.getHour()*60) && gqc.StartMinute <= ((utc.getHour()+1)*60))         //As we check every 'active' config each minute, limiting the pool to just the current hour reduces the impact of the O(n) linear search.
                        {
                            hourgqc.add(gqc);
                        }
                    }
                }
                for (QuizSettings.GuildQuizConfig gqc : hourgqc)
                {
                    if (gqc.StartMinute == (utc.getHour()*60)+utc.getMinute())
                    {
                        Quiz qn = new Quiz();
                        Thread quizThread = new Thread(() -> qn.startQuiz(CmdUtil.getJDA().getGuildById(gqc.GuildID), false));
                        quizThread.setName(gqc.GuildID+"-Quiz");
                        quizThread.start();
                    }
                }
                Thread.sleep((60*1000)-(utc.getSecond()*1000)); //Align to minute.
            }
        }
        catch (InterruptedException e)
        {
            logger.error(e.toString());
            //Ignore, we'll just restart.
        }
        quizLauncher();
    }

    /**
     * This resets any standing scheduling for this guild's quiz night, in order to immediately reflect changes to the configuration.
     * @param gqc the guild quiz config
     */
    public static synchronized void resetScheduling(QuizSettings.GuildQuizConfig gqc)
    {
        long guildID = Long.parseLong(gqc.GuildID);
        removeFromSchedule(gqc);
        if (SettingsManager.isGuildQuizDay(guildID, utc.getDayOfWeek().getValue()))
        {
            daygqc.add(gqc);
            if (gqc.StartMinute >= (utc.getHour()*60) && gqc.StartMinute <= ((utc.getHour()+1)*60))
            {
                hourgqc.add(gqc);
            }
        }
    }

    private static synchronized void removeFromSchedule(QuizSettings.GuildQuizConfig targetgqc)
    {
        try
        {
            for (QuizSettings.GuildQuizConfig gqc : daygqc)
            {
                if (gqc.GuildID.equalsIgnoreCase(targetgqc.GuildID))
                {
                    daygqc.remove(gqc);
                    break;
                }
            }
            for (QuizSettings.GuildQuizConfig gqc : hourgqc)
            {
                if (gqc.GuildID.equalsIgnoreCase(targetgqc.GuildID))
                {
                    hourgqc.remove(gqc);
                    break;
                }
            }
        }
        catch (ConcurrentModificationException e)
        {
            logger.info("Quiz Config was rescheduling when an hourly/daily schedule event occurred. Retrying in 3 seconds...");
            removeFromSchedule(targetgqc);
        }

    }
}
