package com.Zazsona.ReminderCore;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ReminderDateTree implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Year year;

    public Year getYear()
    {
        if (year == null)
            year = new Year();

        return year;
    }

    public class Year implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int yearValue;
        private transient int[] daysInLeapYearMonths = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private HashMap<Integer, Month> months;

        public Year()
        {
            if (yearValue == 0)
            {
                yearValue = OffsetDateTime.now(ZoneOffset.UTC).getYear();
            }
            months = new HashMap<>();
        }

        public Month getMonth(int monthOfYear)
        {
            if (monthOfYear > 0 && monthOfYear <= 12)
            {
                if (!months.containsKey(monthOfYear))
                    months.put(monthOfYear, new Month(daysInLeapYearMonths[monthOfYear-1]));
                return months.get(monthOfYear);
            }
            else
            {
                throw new IndexOutOfBoundsException("There are 12 months in a year!");
            }

        }

        public void removeMonth(int monthOfYear)
        {
            months.remove(monthOfYear);
        }

        public Day getDayOfLeapYear(int dayOfLeapYear)
        {
            int countdown = dayOfLeapYear;
            int month = 1;
            for (int i = 0; i<daysInLeapYearMonths.length; i++)
            {
                if (countdown > daysInLeapYearMonths[i])
                {
                    countdown -= daysInLeapYearMonths[i];
                    month++;
                }
                else
                {
                    break;
                }
            }
            return getMonth(month).getDayOfMonth(countdown);
        }

        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (Month month : months.values())
            {
                remindersList.addAll(month.getReminders());
            }
            return remindersList;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Month month : months.values())
            {
                reminderIds.addAll(month.getReminderIDs());
            }
            return reminderIds;
        }

        public int getYearValue()
        {
            return yearValue;
        }

        protected void setYearValue(int yearValue)
        {
            this.yearValue = yearValue;
        }


    }
    public class Month implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private int daysInMonth;
        private HashMap<Integer, Day> days;
        public Month(int daysInMonth)
        {
            this.daysInMonth = daysInMonth;
            days = new HashMap<>();
        }

        public Day getDayOfMonth(int dayOfMonth) throws IndexOutOfBoundsException
        {
            if (dayOfMonth > 0 && dayOfMonth <= daysInMonth)
            {
                if (!days.containsKey(dayOfMonth))
                    days.put(dayOfMonth, new Day());
                return days.get(dayOfMonth);
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only "+daysInMonth+" days of this month!");
            }
        }

        public void removeDay(int dayOfMonth)
        {
            days.remove(dayOfMonth);
        }

        public int getDaysInMonth()
        {
            return daysInMonth;
        }

        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (Day day : days.values())
            {
                remindersList.addAll(day.getReminders());
            }
            return remindersList;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Day day : days.values())
            {
                reminderIds.addAll(day.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Day implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private HashMap<Integer, Hour> hours;
        public Day()
        {
            hours = new HashMap<>();
        }

        public Hour getHour(int hour)
        {
            if (hour >= 0 && hour <= 23)
            {
                if (!hours.containsKey(hour))
                    hours.put(hour, new Hour());
                return hours.get(hour);
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only 24 hours in a day!");
            }
        }

        public void removeHour(int hourOfDay)
        {
            hours.remove(hourOfDay);
        }

        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (Hour hour : hours.values())
            {
                remindersList.addAll(hour.getReminders());
            }
            return remindersList;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Hour hour : hours.values())
            {
                reminderIds.addAll(hour.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Hour implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private HashMap<Integer, Minute> minutes;
        public Hour()
        {
            minutes = new HashMap<>();
        }

        public Minute getMinute(int minute)
        {
            if (minute >= 0 && minute <= 59)
            {
                if (!minutes.containsKey(minute))
                    minutes.put(minute, new Minute());
                return minutes.get(minute);
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only 60 minutes in an hour!");
            }
        }

        public void removeMinute(int minuteOfHour)
        {
            minutes.remove(minuteOfHour);
        }

        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (Minute minute : minutes.values())
            {
                remindersList.addAll(minute.getReminders());
            }
            return remindersList;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Minute minute : minutes.values())
            {
                reminderIds.addAll(minute.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Minute implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private HashMap<Integer, Second> seconds;
        public Minute()
        {
            seconds = new HashMap<>();
        }
        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (Second second : seconds.values())
            {
                remindersList.addAll(second.getReminders());
            }
            return remindersList;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Second second : seconds.values())
            {
                reminderIds.addAll(second.getReminderIDs());
            }
            return reminderIds;
        }

        public Second getSecond(int second)
        {
            if (second >= 0 && second <= 59)
            {
                if (!seconds.containsKey(second))
                    seconds.put(second, new Second());
                return seconds.get(second);
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only 60 seconds in a minute!");
            }
        }

        public void removeSecond(int secondOfMinute)
        {
            seconds.remove(secondOfMinute);
        }
    }

    public class Second implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private ArrayList<String> reminderIDs;
        public Second()
        {
            reminderIDs = new ArrayList<>();
        }

        public ArrayList<String> getReminderIDs()
        {
            return reminderIDs;
        }

        public LinkedList<Reminder> getReminders()
        {
            LinkedList<Reminder> remindersList = new LinkedList<>();
            for (String UUID : reminderIDs)
            {
                remindersList.add(ReminderManager.getReminderById(UUID));
            }
            return remindersList;
        }

        public void addReminderToTime(String reminderUUID)
        {
            reminderIDs.add(reminderUUID);
        }

        public void removeReminderFromTime(String UUID)
        {
            reminderIDs.remove(UUID);
        }
    }
}
