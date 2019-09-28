package reminderCore;

import reminderCore.enums.RepetitionType;
import reminderCore.enums.TimeType;

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


    public static void addReminder(Reminder reminder)
    {
        idToReminderMap.put(reminder.getUUID(), reminder);
        RepetitionType rt = reminder.getRepetitionType();
        ZonedDateTime execution = reminder.getFirstExecutionTimeUTC();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
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
        }
        else if (rt == RepetitionType.DAILY)
        {
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).daysInMonth+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
                }
            }
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminderToTime(reminder.getUUID());
            }
            else
            {
                addFutureReminder(reminder);
            }
        }
        //TODO: Save
    }

    private static void addFutureReminder(Reminder reminder)
    {
        reminderIDToFutureYearMap.put(reminder.getUUID(), reminder.getFirstExecutionTimeUTC().getYear());
    }

    public static void deleteReminder(Reminder reminder)
    {
        idToReminderMap.remove(reminder.getUUID());
        RepetitionType rt = reminder.getRepetitionType();
        ZonedDateTime execution = reminder.getFirstExecutionTimeUTC();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
            cleanTree(reminder.getFirstExecutionTimeUTC());
        }
        else if (rt == RepetitionType.MONTHLY)
        {
            ZonedDateTime timeToClean = reminder.getFirstExecutionTimeUTC();
            for (int i = 1; i<13; i++)
            {
                if (rdt.getYear().getMonth(i).getDaysInMonth() >= execution.getDayOfMonth())
                {
                    rdt.getYear().getMonth(i).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                    cleanTree(timeToClean.withMonth(i));
                }
            }
        }
        else if (rt == RepetitionType.DAILY)
        {
            ZonedDateTime timeToClean = reminder.getFirstExecutionTimeUTC();
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).daysInMonth+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                    cleanTree(timeToClean.withMonth(monthValue).withDayOfMonth(dayOfMonth));
                }
            }
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminderFromTime(reminder.getUUID());
                cleanTree(reminder.getFirstExecutionTimeUTC());
            }
            else
            {
                deleteFutureReminder(reminder);
            }
        }
        //TODO: Save
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
                return rdt.getYear().getReminders().values();
            case MONTH:
                return rdt.getYear().getMonth(utc.getMonthValue()).getReminders().values();
            case DAY:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getReminders().values();
            case HOUR:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getReminders().values();
            case MINUTE:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getReminders().values();
            case SECOND:
            default:
                return rdt.getYear().getMonth(utc.getMonthValue()).getDayOfMonth(utc.getDayOfMonth()).getHour(utc.getHour()).getMinute(utc.getMinute()).getSecond(utc.getSecond()).getReminders().values();
        }
    }

    public static Reminder getReminderById(String UUID)
    {
        return idToReminderMap.get(UUID);
    }

    protected static void tidyReminders(ArrayList<Reminder> reminders)
    {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        for (Reminder reminder : reminders)
        {
            if (reminder.getRepetitionType() == RepetitionType.SINGLE && reminder.getFirstExecutionTimeUTC().toEpochSecond() <= utc.toEpochSecond())
            {
                deleteReminder(reminder);
            }
        }
    }

    protected static void loadFutureReminders()
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

    public Collection<Reminder> getRemindersForUser(String userID)
    {
        LinkedList<Reminder> reminders = new LinkedList<>();
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
