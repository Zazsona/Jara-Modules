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
        private Month[] months;

        public Year()
        {
            if (yearValue == 0)
            {
                yearValue = OffsetDateTime.now(ZoneOffset.UTC).getYear();
            }
            months = new Month[12];
            for (int i = 0; i<months.length; i++)
            {
                months[i] = new Month(daysInLeapYearMonths[i]);
            }
        }

        public Month getMonth(int monthOfYear)
        {
            monthOfYear = monthOfYear--;
            return months[monthOfYear];
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
            for (Month month : months)
            {
                reminders.putAll(month.getReminders());
            }
            return reminders;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Month month : months)
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
        private Day[] days;
        public Month(int daysInMonth)
        {
            days = new Day[daysInMonth];
            for (int i = 0; i<daysInMonth; i++)
            {
                days[i] = new Day();
            }
        }

        public Day getDayOfMonth(int dayOfMonth) throws IndexOutOfBoundsException
        {
            if (dayOfMonth > 0 && dayOfMonth <= daysInMonth)
            {
                return days[dayOfMonth-1];
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
            for (Day day : days)
            {
                reminders.putAll(day.getReminders());
            }
            return reminders;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Day day : days)
            {
                reminderIds.addAll(day.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Day
    {
        private Hour[] hours;
        public Day()
        {
            hours = new Hour[24];
            for (int i = 0; i<hours.length; i++)
            {
                hours[i] = new Hour();
            }
        }

        public Hour getHour(int hour)
        {
            if (hour >= 0 && hour <= 23)
            {
                return hours[hour];
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only "+hours.length+" hours in a day!");
            }
        }

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Hour hour : hours)
            {
                reminders.putAll(hour.getReminders());
            }
            return reminders;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Hour hour : hours)
            {
                reminderIds.addAll(hour.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Hour
    {
        private Minute[] minutes;
        public Hour()
        {
            minutes = new Minute[60];
            for (int i = 0; i<minutes.length; i++)
            {
                minutes[i] = new Minute();
            }
        }

        public Minute getMinute(int minute)
        {
            if (minute >= 0 && minute <= 59)
            {
                return minutes[minute];
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only "+minutes.length+" minutes in an hour!");
            }
        }

        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Minute minute : minutes)
            {
                reminders.putAll(minute.getReminders());
            }
            return reminders;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Minute minute : minutes)
            {
                reminderIds.addAll(minute.getReminderIDs());
            }
            return reminderIds;
        }
    }

    public class Minute
    {
        private Second[] seconds;
        public Minute()
        {
            seconds = new Second[60];
            for (int i = 0; i<seconds.length; i++)
            {
                seconds[i] = new Second();
            }
        }
        public HashMap<String, Reminder> getReminders()
        {
            HashMap<String, Reminder> reminders = new HashMap<>();
            for (Second second : seconds)
            {
                reminders.putAll(second.getReminders());
            }
            return reminders;
        }

        public ArrayList<String> getReminderIDs()
        {
            ArrayList<String> reminderIds = new ArrayList<>();
            for (Second second : seconds)
            {
                reminderIds.addAll(second.getReminderIDs());
            }
            return reminderIds;
        }

        public Second getSecond(int second)
        {
            if (second >= 0 && second <= 59)
            {
                return seconds[second];
            }
            else
            {
                throw new IndexOutOfBoundsException("There are only "+seconds.length+" seconds in a minute!");
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
            //TODO
            return reminders;
        }

        public void addReminder(String reminderUUID)
        {
            reminderIDs.add(reminderUUID);
        }

        public Reminder getReminder(String UUID)
        {
            //TODO:
        }

        public void removeReminder(String UUID)
        {
            reminderIDs.remove(UUID);
        }
    }
}
