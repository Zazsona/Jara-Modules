package com.zazsona.quiz.system;

import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleLoad;
import com.zazsona.quiz.config.QuizBuilder;
import com.zazsona.quiz.config.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class QuizScheduler extends ModuleLoad
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
        long localDayStartSecond = ZonedDateTime.now(SettingsUtil.getGuildSettings(guildID).getTimeZoneId()).withHour(0).withMinute(0).withSecond(0).toEpochSecond();
        long epochStartTime = (localDayStartSecond+startTime-(5*60)); //Subtract 5 minutes, as this starts the 5 minute start notice.
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

    public static boolean tryQueueQuizForCurrentExecution(QuizBuilder quizBuilder)
    {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long dayStartSecond = utc.withSecond(0).withMinute(0).withHour(0).toEpochSecond();
        long minStartTime = Instant.now().getEpochSecond();
        long maxStartTime = utc.withMinute(0).withSecond(0).withHour(0).plusDays(Long.valueOf(1)).toEpochSecond();
        boolean hasChangeOccurred = false;
        for (Long startTime : quizBuilder.getDay(utc.getDayOfWeek().getValue()).getStartSeconds())
        {
            long epochStartTime = (dayStartSecond+startTime-(5*60)); //Subtract 5 minutes, as this starts the 5 minute start notice.
            if (epochStartTime > minStartTime && epochStartTime < maxStartTime)
            {
                queueQuizForCurrentExecution(quizBuilder.getGuildID(), startTime, dayStartSecond);
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
                ArrayList<QuizBuilder> quizBuilders = SettingsManager.getInstance().getDayQuizzes(utc.getDayOfWeek().getValue());
                long dayStartSecond = utc.withSecond(0).withMinute(0).withHour(0).toEpochSecond();
                long resetSecond = utc.withMinute(0).withSecond(0).withHour(0).plusDays(Long.valueOf(1)).toEpochSecond();
                int dayValue = utc.getDayOfWeek().getValue();
                if (quizBuilders.size() > 0)
                {
                    quizBuilders.forEach((v) ->
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
                                QuizBuilder quizBuilder = SettingsManager.getInstance().getGuildQuizBuilder(guildID);
                                Thread quizThread = new Thread(() -> quizBuilder.build().runQuiz()); //Build in the new thread, as the build process can take over a second, which could miss other scheduled items.
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
