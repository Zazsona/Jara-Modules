package com.Zazsona.ReminderCore;

import com.Zazsona.ReminderCore.enums.TimeType;
import module.Load;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderScheduler extends Load
{
    private static HashMap<Long, ArrayList<Reminder>> remindersMap;
    private static transient boolean remindersAddedDuringPeriod = false;
    private static transient Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Override
    public void load()
    {
        remindersMap = new HashMap<>();
        ReminderDateTree rdt = FileManager.getReminderDateTree();
        HashMap<String, Integer> futureReminders = FileManager.getFutureReminders();
        HashMap<String, Reminder> reminders = FileManager.getReminders();
        ReminderManager.initialise(rdt, futureReminders, reminders);
        runReminders();
    }

    private static void queueReminderForCurrentExecution(Reminder reminder, ZonedDateTime utc)
    {
        long startTime = utc.withMinute(reminder.getFirstExecutionTime().getMinute()).withSecond(reminder.getFirstExecutionTime().getSecond()).toEpochSecond();
        if (remindersMap.containsKey(startTime))
        {
            remindersMap.get(startTime).add(reminder);
        }
        else
        {
            remindersMap.put(startTime, new ArrayList<>());
            remindersMap.get(startTime).add(reminder);
        }
    }

    protected static boolean tryQueueReminderForCurrentExecution(Reminder reminder)
    {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long reminderStartSecond = reminder.getFirstExecutionTime().toInstant().getEpochSecond();
        long minStartTime = Instant.now().getEpochSecond();
        long maxStartTime = utc.withMinute(0).withSecond(0).plusHours(Long.valueOf(1)).toEpochSecond();
        if (reminderStartSecond > minStartTime && reminderStartSecond < maxStartTime)
        {
            queueReminderForCurrentExecution(reminder, utc);
            remindersAddedDuringPeriod = true;
            return true;
        }
        return false;
    }

    protected static void removeReminderFromQueue(Reminder reminderToFind)
    {
        long key = -1;
        int index = -1;
        for (Map.Entry<Long, ArrayList<Reminder>> entry : remindersMap.entrySet())
        {
            for (Reminder reminder : entry.getValue())
            {
                if (reminder.getUUID().equals(reminderToFind.getUUID()))
                {
                    key = entry.getKey();
                    index = entry.getValue().indexOf(reminder);
                    break;
                }
            }
        }
        if (key != -1)
        {
            remindersMap.get(key).remove(index);
            if (remindersMap.get(key).size() == 0)
                remindersMap.remove(key);
        }
    }

    private void runReminders()
    {
        try
        {
            while (true)
            {
                ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                long resetSecond = utc.withMinute(0).withSecond(0).plusHours(Long.valueOf(1)).toEpochSecond();
                ArrayList<Reminder> reminders = new ArrayList<>(ReminderManager.getReminders(TimeType.HOUR, utc));
                if (reminders.size() > 0)
                {
                    reminders.forEach((v) -> queueReminderForCurrentExecution(v, utc));
                }
                while (Instant.now().getEpochSecond() < resetSecond)
                {
                    try
                    {
                        long epochSecond = Instant.now().getEpochSecond();
                        if (remindersMap.containsKey(epochSecond))
                        {
                            for (Reminder reminder : remindersMap.get(epochSecond))
                            {
                                reminder.execute();
                            }
                        }
                        Thread.sleep(((epochSecond+1)*1000)-Instant.now().toEpochMilli());
                    }
                    catch (InterruptedException e)
                    {
                        logger.error("Reminder scheduler got interrupted! Reminders will not run at this time.");
                    }
                }
                tidyReminders(reminders);
                remindersMap.clear();
            }
        }
        catch (Exception e)
        {
            logger.error("The reminder scheduler has stopped. Restarting...", e);
            load();
        }
    }

    private void tidyReminders(ArrayList<Reminder> baseReminderQueue)
    {
        if (remindersAddedDuringPeriod)
        {
            ArrayList<Reminder> reminders = new ArrayList<>();
            remindersMap.forEach((k, v) -> reminders.addAll(v));
            ReminderManager.tidyReminders(reminders);
        }
        else
        {
            ReminderManager.tidyReminders(baseReminderQueue);
        }
        remindersAddedDuringPeriod = false;
    }
}
