import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CalendarEventManager implements Serializable
{
    private static final long serialVersionUID = 1L;
    private HashMap<Long, HashMap<Integer, ArrayList<CalendarEvent>>> eventMap;
    private HashMap<Long, Integer> guildToBCastTime;
    private transient Logger logger = LoggerFactory.getLogger(CalendarEventManager.class);
    private transient boolean dayEventCacheOutdated = false;
    private transient ArrayList<CalendarEvent> dayEventCache;

    public CalendarEventManager()
    {
        restore();
    }

    private String getConfigPath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/CalendarEvents.jara";
    }
    private synchronized void save()
    {
        dayEventCacheOutdated = true;
        try
        {
            File configFile = new File(getConfigPath());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getConfigPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private synchronized void restore()
    {
        dayEventCacheOutdated = true;
        try 
        {
            if (new File(getConfigPath()).exists())
            {
                FileInputStream fis = new FileInputStream(getConfigPath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                CalendarEventManager cem = (CalendarEventManager) ois.readObject();
                this.eventMap = cem.eventMap;
                this.guildToBCastTime = cem.guildToBCastTime;
                ois.close();
                fis.close();
            }
            else
            {
                eventMap = new HashMap<>();
                guildToBCastTime = new HashMap<>();
            }

        } 
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        } 
        catch (ClassNotFoundException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }

    public HashMap<Integer, ArrayList<CalendarEvent>> getGuildEvents(long guildID)
    {
        return eventMap.get(guildID);
    }

    public ArrayList<CalendarEvent> getGuildDayEvents(long guildID, int leapDayOfYear)
    {
        try
        {
            return eventMap.get(guildID).get(leapDayOfYear);
        }
        catch (NullPointerException e)
        {
            return null;
        }
    }

    public ArrayList<CalendarEvent> getAllDayEvents(int leapDayOfYear)
    {
        if (dayEventCacheOutdated)
        {
            dayEventCache = new ArrayList<>();
            for (long guildID : eventMap.keySet())
            {
                ArrayList<CalendarEvent> guildEvents = eventMap.get(guildID).get(leapDayOfYear);
                if (guildEvents != null)
                {
                    dayEventCache.addAll(guildEvents);
                }
            }
            dayEventCacheOutdated = false;
        }
        return dayEventCache;
    }

    public synchronized boolean addEvent(CalendarEvent ce, int leapDayOfYear)
    {
        if (!eventMap.containsKey(ce.getGuildID()))
        {
            eventMap.put(ce.getGuildID(), new HashMap<>());
        }
        if (!eventMap.get(ce.getGuildID()).containsKey(leapDayOfYear))
        {
            eventMap.get(ce.getGuildID()).put(leapDayOfYear, new ArrayList<>());
        }
        for (int existingCeKey : eventMap.get(ce.getGuildID()).keySet())        //Check to see if name already is registered.
        {
            for (CalendarEvent existingCe : eventMap.get(ce.getGuildID()).get(existingCeKey))
            {
                if (existingCe.getName().equalsIgnoreCase(ce.getName()))
                {
                    return false;
                }
            }
        }
        eventMap.get(ce.getGuildID()).get(leapDayOfYear).add(ce);


        if (!guildToBCastTime.containsKey(ce.getGuildID()))
        {
            guildToBCastTime.put(ce.getGuildID(), 0);
        }
        save();
        return true;
    }

    public synchronized boolean removeEvent(long guildID, String eventName)
    {
        if (eventMap.containsKey(guildID))
        {
            for (ArrayList<CalendarEvent> ces : eventMap.get(guildID).values())
            {
                for (CalendarEvent ce : ces)
                {
                    if (ce.getName().equalsIgnoreCase(eventName))
                    {
                        ces.remove(ce);
                        save();
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public synchronized void setGuildEventTime(long guildID, int minuteOfDay)
    {
        guildToBCastTime.put(guildID, minuteOfDay);
        save();
    }
    public int getGuildEventTime(long guildID)
    {
        return guildToBCastTime.get(guildID);
    }
    public HashMap<Long, Integer> getGuildEventTimes()
    {
        return guildToBCastTime;
    }

    public CalendarEvent getEventByName(long guildID, String name)
    {
        if (eventMap.containsKey(guildID))
        {
            for (ArrayList<CalendarEvent> ces : eventMap.get(guildID).values())
            {
                for (CalendarEvent ce : ces)
                {
                    if (ce.getName().equalsIgnoreCase(name))
                    {
                        return ce;
                    }
                }
            }
        }
        return null;
    }
}
