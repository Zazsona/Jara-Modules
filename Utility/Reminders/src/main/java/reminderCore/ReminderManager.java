package reminderCore;

import reminderCore.enums.RepetitionType;
import reminderCore.enums.TimeType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class ReminderManager
{
    private ReminderDateTree rdt;
    private HashMap<String, Integer> reminderIDToFutureYearMap;

    public void addReminder(Reminder reminder)
    {
        //TODO: Write to reminders file
        RepetitionType rt = reminder.getRepetitionType();
        OffsetDateTime execution = reminder.getFirstExecutionTimeUTC();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminder(reminder.getUUID());
        }
        else if (rt == RepetitionType.MONTHLY)
        {
            for (int i = 1; i<13; i++)
            {
                if (rdt.getYear().getMonth(i).getDaysInMonth() >= execution.getDayOfMonth())
                {
                    rdt.getYear().getMonth(i).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminder(reminder.getUUID());
                }
            }
        }
        else if (rt == RepetitionType.DAILY)
        {
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).daysInMonth+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminder(reminder.getUUID());
                }
            }
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).addReminder(reminder.getUUID());
            }
            else
            {
                addFutureReminder(reminder);
            }
        }
        //TODO: Save rdt
    }

    private void addFutureReminder(Reminder reminder)
    {
        if (reminderIDToFutureYearMap == null)
        {
            reminderIDToFutureYearMap = new HashMap<>();
        }
        reminderIDToFutureYearMap.put(reminder.getUUID(), reminder.getFirstExecutionTimeUTC().getYear());
        //TODO: Add to years file
    }

    public void deleteReminder(Reminder reminder)
    {
        //TODO: Remove from reminders file
        RepetitionType rt = reminder.getRepetitionType();
        OffsetDateTime execution = reminder.getFirstExecutionTimeUTC();
        if (rt == RepetitionType.ANNUALLY)
        {
            rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminder(reminder.getUUID());
        }
        else if (rt == RepetitionType.MONTHLY)
        {
            for (int i = 1; i<13; i++)
            {
                if (rdt.getYear().getMonth(i).getDaysInMonth() >= execution.getDayOfMonth())
                {
                    rdt.getYear().getMonth(i).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminder(reminder.getUUID());
                }
            }
        }
        else if (rt == RepetitionType.DAILY)
        {
            for (int monthValue = 1; monthValue<13; monthValue++)
            {
                for (int dayOfMonth = 1; dayOfMonth<(rdt.getYear().getMonth(monthValue).daysInMonth+1); dayOfMonth++)
                {
                    rdt.getYear().getMonth(monthValue).getDayOfMonth(dayOfMonth).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminder(reminder.getUUID());
                }
            }
        }
        else if (rt == RepetitionType.SINGLE)
        {
            if (execution.getYear() == ZonedDateTime.now(ZoneOffset.UTC).getYear())
            {
                rdt.getYear().getMonth(execution.getMonthValue()).getDayOfMonth(execution.getDayOfMonth()).getHour(execution.getHour()).getMinute(execution.getMinute()).getSecond(execution.getSecond()).removeReminder(reminder.getUUID());
            }
            else
            {
                deleteFutureReminder(reminder);
            }
        }
    }

    private void deleteFutureReminder(Reminder reminder)
    {
        if (reminderIDToFutureYearMap != null)
        {
            reminderIDToFutureYearMap.remove(reminder.getUUID());
        }
        //TODO: delete from reminder data file
    }

    public Collection<String> getReminderIds(TimeType tt, OffsetDateTime utc)
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

    public Collection<Reminder> getReminders(TimeType tt, OffsetDateTime utc)
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

    public Reminder getReminderById(String UUID)
    {
        //TODO: Get reminder from file
    }

    protected void tidyReminders(Reminder... reminders)
    {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        for (Reminder reminder : reminders)
        {
            if (reminder.getRepetitionType() == RepetitionType.SINGLE && reminder.getFirstExecutionTimeUTC().toEpochSecond() < utc.toEpochSecond())
            {
                //TODO: Delete reminder
            }
        }
    }

    protected void loadFutureReminders()
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
}
