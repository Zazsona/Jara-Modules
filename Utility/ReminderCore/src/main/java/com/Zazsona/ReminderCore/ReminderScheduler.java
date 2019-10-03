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
import java.util.Iterator;
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
        long maxStartTime = utc.withMinute(0).withSecond(0).withHour(utc.getHour()+1).toEpochSecond();
        if (reminderStartSecond > minStartTime && reminderStartSecond < maxStartTime)
        {
            queueReminderForCurrentExecution(reminder, utc);
            remindersAddedDuringPeriod = true;
            return true;
        }
        return false;
    }

    protected static void removeReminderFromQueue(Reminder reminder)
    {
        Iterator<Map.Entry<Long, ArrayList<Reminder>>> mapIterator = remindersMap.entrySet().iterator();
        while (mapIterator.hasNext())
        {
            Iterator<Reminder> arrayIterator = mapIterator.next().getValue().iterator();
            while (arrayIterator.hasNext())
            {
                Reminder foundReminder = arrayIterator.next();
                if (foundReminder.getUUID().equals(reminder.getUUID()))
                {
                    arrayIterator.remove();
                    //No break in case it is multi-queued
                }
            }
        }
    }

    private void runReminders()
    {
        while (true)
        {
            ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
            long resetSecond = utc.withMinute(0).withSecond(0).withHour(utc.getHour()+1).toEpochSecond();
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
                    Thread.sleep(((Instant.now().getEpochSecond()+1)*1000)-Instant.now().toEpochMilli());
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


    }
}
