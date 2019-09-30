package com.Zazsona.ReminderCore;

import com.Zazsona.ReminderCore.enums.RepetitionType;
import com.Zazsona.ReminderCore.enums.TimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class ReminderManager
{
    private static ReminderDateTree rdt = new ReminderDateTree();
    private static HashMap<String, Integer> reminderIDToFutureYearMap = new HashMap<>();
    //TODO: HashMap means easy code and processing efficiency, but memory costs are pretty rough. Perhaps there's a better solution?
    private static HashMap<String, Reminder> idToReminderMap = new HashMap<>();
    private transient static Logger logger = LoggerFactory.getLogger(ReminderManager.class);

    protected static void initialise(ReminderDateTree rdtArg, HashMap<String, Integer> reminderIDToFutureYearMapArg, HashMap<String, Reminder> idToReminderMapArg)
    {
        rdt = rdtArg;
        reminderIDToFutureYearMap = reminderIDToFutureYearMapArg;
        idToReminderMap = idToReminderMapArg;
        if (rdt.getYear().getYearValue() < ZonedDateTime.now(ZoneOffset.UTC).getYear())
        {
            tidyReminders(new ArrayList(idToReminderMap.values())); //Expensive operation, but it only runs literally once a year.
            loadFutureReminders();
        }
    }

    public static void addReminder(Reminder reminder) throws IOException
    {
        idToReminderMap.put(reminder.getUUID(), reminder);
        RepetitionType rt = reminder.getRepetitionType();
        ZonedDateTime execution = reminder.getFirstExecutionTime();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.MONTHLY)
        {
            for (int i = 1; i<13; i++)
            {
                if (rdt.getYear().getMonth(i).getDaysInMonth() >= execution.getDayOfMonth())
                {
                    rdt.getYear().getMonth(i).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
                }
            }
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.DAILY)
        {
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).getDaysInMonth()+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
                }
            }
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.WEEKLY)
        {
            int dayOfWeek = execution.getDayOfWeek().getValue();
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayValue = 1; dayValue <= rdt.getYear().getMonth(monthValue).getDaysInMonth()+1; dayValue++)
                {
                    if (execution.withMonth(monthValue).withDayOfMonth(dayValue).getDayOfWeek().getValue() == dayOfWeek)
                    {
                        rdt.getYear().getMonth(monthValue).getDayOfMonth(dayValue).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
                    }
                }
            }
            ZonedDateTime futurezdt = execution.plusYears(1).withMonth(1);
            for (int firstWeekOfJanDay = 1; firstWeekOfJanDay<8; firstWeekOfJanDay++)
            {
                if (futurezdt.withDayOfMonth(firstWeekOfJanDay).getDayOfWeek().getValue() == dayOfWeek)
                {
                    futurezdt = futurezdt.withDayOfMonth(firstWeekOfJanDay);
                    break;
                }
            }
            //UUID is intentionally matching, as this is technically the same reminder.
            addFutureReminder(new Reminder(reminder.getUserID(), reminder.getCreationTimeStamp(), reminder.getGuildID(), reminder.getChannelID(), reminder.getGroupType(), reminder.getRepetitionType(), reminder.getMessage(), futurezdt));
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
                FileManager.saveRemindersDateTree(rdt);
            }
            else
            {
                addFutureReminder(reminder);
                FileManager.saveFutureReminders(reminderIDToFutureYearMap);
            }
        }
        ReminderScheduler.tryQueueReminderForCurrentExecution(reminder);
        FileManager.saveReminders(idToReminderMap);
    }

    private static void addFutureReminder(Reminder reminder)
    {
        reminderIDToFutureYearMap.put(reminder.getUUID(), reminder.getFirstExecutionTime().getYear());
    }

    public static void deleteReminder(Reminder reminder) throws IOException
    {
        idToReminderMap.remove(reminder.getUUID());
        RepetitionType rt = reminder.getRepetitionType();
        ZonedDateTime execution = reminder.getFirstExecutionTime();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
            cleanTree(reminder.getFirstExecutionTime());
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.MONTHLY)
        {
            ZonedDateTime timeToClean = reminder.getFirstExecutionTime();
            for (int i = 1; i<13; i++)
            {
                if (rdt.getYear().getMonth(i).getDaysInMonth() >= execution.getDayOfMonth())
                {
                    rdt.getYear().getMonth(i).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                    cleanTree(timeToClean.withMonth(i));
                }
            }
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.DAILY)
        {
            ZonedDateTime timeToClean = reminder.getFirstExecutionTime();
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).getDaysInMonth()+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                    cleanTree(timeToClean.withMonth(monthValue).withDayOfMonth(dayOfMonth));
                }
            }
            FileManager.saveRemindersDateTree(rdt);
        }
        else if (rt == RepetitionType.WEEKLY)
        {
            ZonedDateTime timeToClean = reminder.getFirstExecutionTime();
            int dayOfWeek = execution.getDayOfWeek().getValue();
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayValue = 1; dayValue <= rdt.getYear().getMonth(monthValue).getDaysInMonth()+1; dayValue++)
                {
                    if (execution.withMonth(monthValue).withDayOfMonth(dayValue).getDayOfWeek().getValue() == dayOfWeek)
                    {
                        rdt.getYear().getMonth(monthValue).getDayOfMonth(dayValue).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                        cleanTree(timeToClean.withMonth(monthValue).withDayOfMonth(dayValue));
                    }
                }
            }
            deleteFutureReminder(reminder);
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                cleanTree(reminder.getFirstExecutionTime());
                FileManager.saveRemindersDateTree(rdt);
            }
            else
            {
                deleteFutureReminder(reminder);
                FileManager.saveFutureReminders(reminderIDToFutureYearMap);
            }
        }
        ReminderScheduler.removeReminderFromQueue(reminder);
        FileManager.saveReminders(idToReminderMap);
    }

    private static void cleanTree(ZonedDateTime dateTimeToClean)
    {
        if (rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getHour(dateTimeToClean.getHour()).getMinute(dateTimeToClean.getMinute()).getSecond(dateTimeToClean.getSecond()).getReminderIDs().size() == 0)
        {
            rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getHour(dateTimeToClean.getHour()).getMinute(dateTimeToClean.getMinute()).removeSecond(dateTimeToClean.getSecond());
        }
        if (rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getHour(dateTimeToClean.getHour()).getMinute(dateTimeToClean.getMinute()).getReminderIDs().size() == 0)
        {
            rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getHour(dateTimeToClean.getHour()).removeMinute(dateTimeToClean.getMinute());
        }
        if (rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getHour(dateTimeToClean.getHour()).getReminderIDs().size() == 0)
        {
            rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).removeHour(dateTimeToClean.getHour());
        }
        if (rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getDayOfMonth(dateTimeToClean.getDayOfMonth()).getReminderIDs().size() == 0)
        {
            rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).removeDay(dateTimeToClean.getDayOfMonth());
        }
        if (rdt.getYear().getMonth(dateTimeToClean.getMonthValue()).getReminderIDs().size() == 0)
        {
            rdt.getYear().removeMonth(dateTimeToClean.getMonthValue());
        }
    }

    private static void deleteFutureReminder(Reminder reminder)
    {
        reminderIDToFutureYearMap.remove(reminder.getUUID());
    }

    public static Collection<String> getReminderIds(TimeType tt, ZonedDateTime utc)
    {
        switch (tt)
        {
            case YEAR:
                return rdt.getYear().getReminderIDs();
            case MONTH:
                return rdt.getYear().getMonth(utc.getMonthValue()).getReminderIDs();
            case DAY:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getReminderIDs();
            case HOUR:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getReminderIDs();
            case MINUTE:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getReminderIDs();
            case SECOND:
            default:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getSecond(utc.getSecond()).getReminderIDs();
        }
    }

    public static Collection<Reminder> getReminders(TimeType tt, ZonedDateTime utc)
    {
        switch (tt)
        {
            case YEAR:
                return rdt.getYear().getReminders();
            case MONTH:
                return rdt.getYear().getMonth(utc.getMonthValue()).getReminders();
            case DAY:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getReminders();
            case HOUR:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getReminders();
            case MINUTE:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getReminders();
            case SECOND:
            default:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getSecond(utc.getSecond()).getReminders();
        }
    }

    public static Reminder getReminderById(String UUID)
    {
        return idToReminderMap.get(UUID);
    }

    protected static void tidyReminders(ArrayList<Reminder> reminders)
    {
        try
        {
            OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
            for (Reminder reminder : reminders)
            {
                if (reminder.getRepetitionType() == RepetitionType.SINGLE && reminder.getFirstExecutionTime().toEpochSecond() <= utc.toEpochSecond())
                {
                    deleteReminder(reminder);
                }
            }
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
    }

    protected static void loadFutureReminders()
    {
        try
        {
            int currentYear = OffsetDateTime.now(ZoneOffset.UTC).getYear();
            if (rdt.getYear().getYearValue() != currentYear)
            {
                Iterator<Map.Entry<String, Integer>> iterator = reminderIDToFutureYearMap.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry<String, Integer> entry = iterator.next();
                    if (entry.getValue() == currentYear)
                    {
                        addReminder(getReminderById(entry.getKey()));
                        iterator.remove();
                    }
                }
                rdt.getYear().setYearValue(currentYear);
            }
        }
        catch (IOException e)
        {
            logger.error("Unable to schedule reminders for this year.");
        }

    }

    public static ArrayList<Reminder> getRemindersForUser(String userID)
    {
        ArrayList<Reminder> reminders = new ArrayList<>();
        for (String key : idToReminderMap.keySet())
        {
            if (key.startsWith(userID))
            {
                reminders.add(idToReminderMap.get(key));
            }
        }
        return reminders;
    }
}
