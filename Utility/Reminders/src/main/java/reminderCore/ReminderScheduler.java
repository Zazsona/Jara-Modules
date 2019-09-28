package reminderCore;

import module.Load;
import org.slf4j.LoggerFactory;
import reminderCore.enums.TimeType;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ReminderScheduler extends Load
{
    @Override
    public void load()
    {
        try
        {
            HashMap<Long, ArrayList<Reminder>> remindersMap = new HashMap<>();
            Thread.sleep(((Instant.now().getEpochSecond()+1)*1000)-Instant.now().toEpochMilli());
            while (true)
            {
                long secondsSinceEpoch = Instant.now().getEpochSecond();
                long resetTime = secondsSinceEpoch+3600;
                ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
                ArrayList<Reminder> reminders = new ArrayList<>(ReminderManager.getReminders(TimeType.HOUR, utc));
                if (reminders.size() > 0)
                {
                    reminders.forEach((v) ->
                                      {
                                          long startTime = secondsSinceEpoch+((v.getFirstExecutionTimeUTC().getMinute()-utc.getMinute())*60)+v.getFirstExecutionTimeUTC().getSecond()-utc.getSecond();
                                          if (remindersMap.containsKey(startTime))
                                          {
                                              remindersMap.get(startTime).add(v);
                                          }
                                          else
                                          {
                                              remindersMap.put(startTime, new ArrayList<>());
                                              remindersMap.get(startTime).add(v);
                                          }
                                      });
                }

                while (Instant.now().getEpochSecond() < resetTime)
                {
                    try
                    {
                        Thread.sleep(1000);
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
        catch (InterruptedException e)
        {
            LoggerFactory.getLogger(getClass()).error("Reminder scheduler was interrupted when synchronising. Reminders will not run.");
        }
    }
}
