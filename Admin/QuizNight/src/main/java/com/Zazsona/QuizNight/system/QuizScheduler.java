package com.Zazsona.QuizNight.system;

import com.Zazsona.QuizNight.json.GuildQuizConfig;
import com.Zazsona.QuizNight.quiz.Quiz;
import commands.CmdUtil;
import module.Load;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class QuizScheduler extends Load
{
    private static HashMap<Long, ArrayList<String>> quizMap;
    private static transient Logger logger = LoggerFactory.getLogger(QuizScheduler.class);

    @Override
    public void load()
    {
        quizMap = new HashMap<>();
        runQuizScheduler();
    }

    private static void queueQuizForCurrentExecution(String guildID, long startTime, long dayStartSecond)
    {
        long epochStartTime = (dayStartSecond+startTime-(5*60)); //Subtract 5 minutes, as this starts the 5 minute start notice.
        if (quizMap.containsKey(epochStartTime))
        {
            if (!quizMap.get(epochStartTime).contains(guildID))
                quizMap.get(epochStartTime).add(guildID);
        }
        else
        {
            quizMap.put(epochStartTime, new ArrayList<>());
            quizMap.get(epochStartTime).add(guildID);
        }
    }

    public static boolean tryQueueQuizForCurrentExecution(GuildQuizConfig gqc)
    {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long dayStartSecond = utc.withSecond(0).withMinute(0).withHour(0).toEpochSecond();
        long minStartTime = Instant.now().getEpochSecond();
        long maxStartTime = utc.withMinute(0).withSecond(0).withHour(0).plusDays(Long.valueOf(1)).toEpochSecond();
        boolean hasChangeOccurred = false;
        for (Long startTime : gqc.getDay(utc.getDayOfWeek().getValue()).getStartSeconds())
        {
            long epochStartTime = (dayStartSecond+startTime-(5*60)); //Subtract 5 minutes, as this starts the 5 minute start notice.
            if (epochStartTime > minStartTime && epochStartTime < maxStartTime)
            {
                queueQuizForCurrentExecution(gqc.getGuildID(), startTime, dayStartSecond);
                hasChangeOccurred = true;
            }
        }
        return hasChangeOccurred;
    }

    public static void removeQuizFromQueue(String guildID, long startTime)
    {
        long key = -1;
        int index = -1;
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long dayStartSecond = utc.withSecond(0).withMinute(0).withHour(0).toEpochSecond();
        long epochStartTime = (dayStartSecond+startTime-(5*60)); //Subtract 5 minutes, as this starts the 5 minute start notice.
        if (quizMap.containsKey(epochStartTime))
        {
            for (String mappedGuildID : quizMap.get(epochStartTime))
            {
                if (mappedGuildID.equals(guildID))
                {
                    key = epochStartTime;
                    index = quizMap.get(epochStartTime).indexOf(mappedGuildID);
                    break;
                }
            }
            if (key != -1)
            {
                quizMap.get(key).remove(index);
                if (quizMap.get(key).size() == 0)
                    quizMap.remove(key);
            }
        }
    }

    private void runQuizScheduler()
    {
        try
        {
            while (true)
            {
                ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            ArrayList<GuildQuizConfig> quizConfigs = SettingsManager.getInstance().getDayQuizzes(utc.getDayOfWeek().getValue());
                long dayStartSecond = utc.withSecond(0).withMinute(0).withHour(0).toEpochSecond();
                long resetSecond = utc.withMinute(0).withSecond(0).withHour(0).plusDays(Long.valueOf(1)).toEpochSecond();
                int dayValue = utc.getDayOfWeek().getValue();
                if (quizConfigs.size() > 0)
                {
                    quizConfigs.forEach((v) ->
                    v.getDay(dayValue).getStartSeconds().forEach((v2) ->
                    queueQuizForCurrentExecution(v.getGuildID(), v2, dayStartSecond)));
                }
                while (Instant.now().getEpochSecond() < resetSecond)
                {
                    try
                    {
                        long epochSecond = Instant.now().getEpochSecond();
                        if (quizMap.containsKey(epochSecond))
                        {
                            for (String guildID : quizMap.get(epochSecond))
                            {
                                Quiz qn = new Quiz();
                                Thread quizThread = new Thread(() -> qn.startQuiz(CmdUtil.getJDA().getGuildById(guildID), false));
                                quizThread.setName(guildID+"-Quiz");
                                quizThread.start();
                            }
                        }
                        Thread.sleep(((epochSecond+1)*1000)-Instant.now().toEpochMilli());
                    }
                    catch (InterruptedException e)
                    {
                        logger.error("Quiz scheduler got interrupted! Quizzes will not run at this time.");
                    }
                }
                quizMap.clear();
            }
        }
        catch (Exception e)
        {
            logger.error("The Quiz scheduler has stopped. Restarting...", e);
            load();
        }
    }
}
