package reminderCore;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReminderDateTree
{
    Year year;

    public ReminderDateTree()
    {
        year = new Year();
    }

    public Year getYear()
    {
        return year;
    }

    public class Year
    {
        private int yearValue;
        private int[] daysInLeapYearMonths = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
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
            if (!months.containsKey(monthOfYear))
                months.put(monthOfYear, new Month(daysInLeapYearMonths[monthOfYear-1]));
            return months.get(monthOfYear);
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

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Month month : months.values())
            {
                reminders.putAll(month.getReminders());
            }
            return reminders;
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
    public class Month
    {
        int daysInMonth;
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

        public int getDaysInMonth()
        {
            return daysInMonth;
        }

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Day day : days.values())
            {
                reminders.putAll(day.getReminders());
            }
            return reminders;
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

    public class Day
    {
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

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Hour hour : hours.values())
            {
                reminders.putAll(hour.getReminders());
            }
            return reminders;
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

    public class Hour
    {
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

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Minute minute : minutes.values())
            {
                reminders.putAll(minute.getReminders());
            }
            return reminders;
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

    public class Minute
    {
        private HashMap<Integer, Second> seconds;
        public Minute()
        {
            seconds = new HashMap<>();
        }
        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Second second : seconds.values())
            {
                reminders.putAll(second.getReminders());
            }
            return reminders;
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
    }

    public class Second
    {
        private ArrayList<String> reminderIDs;
        public Second()
        {
            reminderIDs = new ArrayList<>();
        }

        public ArrayList<String> getReminderIDs()
        {
            return reminderIDs;
        }

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminderHashMap = new HashMap<>();
            for (String UUID : reminderIDs)
            {
                reminderHashMap.put(UUID, ReminderManager.getReminderById(UUID));
            }
            return reminderHashMap;
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
