import module.Load;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CalendarEventScheduler extends Load
{
    private static CalendarEventManager cem;
    @Override
    public void load()
    {
        try
        {
            cem = new CalendarEventManager();
            OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
            int dayIndex = getDayIndex(utc);
            while (dayIndex == getDayIndex(utc))
            {
                utc = OffsetDateTime.now(ZoneOffset.UTC);
                int minute = (utc.getHour()*60)+utc.getMinute();
                for (CalendarEvent ce : cem.getAllDayEvents(dayIndex))
                {
                    if (cem.getGuildEventTime(ce.getGuildID()) == minute)
                    {
                        ce.execute();
                    }
                }
                Thread.sleep((60*1000)-(utc.getSecond()*1000)); //Align to minute.
            }
            load();
        }
        catch (InterruptedException e)
        {
            LoggerFactory.getLogger(CalendarEventScheduler.class).error("CalendarEventScheduler interrupted. Calendar events will not run.");
        }
    }

    public static CalendarEventManager getCalendarEventManager()
    {
        return cem;
    }

    private int getDayIndex(OffsetDateTime utc)
    {
        int dayIndex = utc.getDayOfYear();
        boolean leapYear = isLeapYear(utc.getYear());
        if (!leapYear)
        {
            if (utc.getDayOfYear() >= 60)
                dayIndex++;
        }
        return dayIndex;
    }

    public static boolean isLeapYear(int year)
    {
        if (year % 4 != 0)
        {
            return false;
        }
        else if (year % 400 == 0)
        {
            return true;
        }
        else if (year % 100 == 0)
        {
            return false;
        }
        return true;
    }
}
