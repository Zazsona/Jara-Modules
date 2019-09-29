package reminderCore;

import module.Load;
import org.slf4j.LoggerFactory;
import reminderCore.enums.TimeType;

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
        long secondsSinceEpoch = Instant.now().getEpochSecond();
        long startTime = secondsSinceEpoch+((reminder.getFirstExecutionTime().getMinute()-utc.getMinute())*60)+reminder.getFirstExecutionTime().getSecond()-utc.getSecond();
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
        long maxStartTime = minStartTime+utc.plusHours(1).withSecond(0).toEpochSecond();
        if (reminderStartSecond > minStartTime && reminderStartSecond < maxStartTime)
        {
            queueReminderForCurrentExecution(reminder, utc);
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
            long secondsSinceEpoch = Instant.now().getEpochSecond();
            long resetSecond = secondsSinceEpoch+utc.plusHours(1).withSecond(0).toEpochSecond();
            ArrayList<Reminder> reminders = new ArrayList<>(ReminderManager.getReminders(TimeType.HOUR, utc));
            if (reminders.size() > 0)
            {
                reminders.forEach((v) -> queueReminderForCurrentExecution(v, utc));
            }
            while (Instant.now().getEpochSecond() < resetSecond)
            {
                try
                {
                    Thread.sleep(((Instant.now().getEpochSecond()+1)*1000)-Instant.now().toEpochMilli());
                    long epochSecond = Instant.now().getEpochSecond();
                    if (remindersMap.containsKey(epochSecond))
                    {
                        for (Reminder reminder : remindersMap.get(epochSecond))
                        {
                            reminder.execute();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    LoggerFactory.getLogger(getClass()).error("Reminder scheduler got interrupted! Reminders will not run at this time.");
                }
            }
            ReminderManager.tidyReminders(reminders);
            remindersMap.clear();
        }
    }
}
