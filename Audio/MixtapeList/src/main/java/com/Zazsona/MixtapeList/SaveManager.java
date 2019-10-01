package com.Zazsona.MixtapeList;

import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveManager
{
    private static HashMap<Long, HashMap<String, ArrayList<String>>> mixtapes;
    private static Logger logger = LoggerFactory.getLogger(SaveManager.class);

    private static String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/Mixtapes.jara";
    }
    private static synchronized void save()
    {
        try
        {
            File configFile = new File(getSavePath());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getSavePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mixtapes);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private static synchronized void restore()
    {
        try
        {
            if (new File(getSavePath()).exists())
            {
                FileInputStream fis = new FileInputStream(getSavePath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                mixtapes = (HashMap<Long, HashMap<String, ArrayList<String>>>) ois.readObject();
                ois.close();
                fis.close();
            }
            else
            {
                mixtapes = new HashMap<>();
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

    private static synchronized HashMap<Long, HashMap<String, ArrayList<String>>> getMixtapes()
    {
        restore();
        return mixtapes;
    }

    public static synchronized boolean createMixtape(long guildID, String name, String... tracks)
    {
        getGuildMixtapes(guildID);
        if (!mixtapes.get(guildID).containsKey(name))
        {
            ArrayList<String> tracksList = new ArrayList<>();
            for (String track : tracks)
            {
                tracksList.add(track);
            }
            mixtapes.get(guildID).put(name, tracksList);
            save();
            return true;
        }
        return false;
    }

    public static synchronized void removeMixtape(long guildID, String name)
    {
        getGuildMixtapes(guildID);
        mixtapes.get(guildID).remove(name);
        save();
    }

    public static synchronized void addTracks(long guildID, String name, String... tracks)
    {
        getGuildMixtapes(guildID);
        if (mixtapes.get(guildID).containsKey(name))
        {
            ArrayList<String> tracksList = new ArrayList<>();
            tracksList.addAll(getGuildMixtapes(guildID).get(name));
            for (String track : tracks)
            {
                if (!tracksList.contains(track))
                    tracksList.add(track);
            }
            mixtapes.get(guildID).get(name).clear();
            mixtapes.get(guildID).get(name).addAll(tracksList);
            save();
        }

    }

    public static synchronized void removeTracks(long guildID, String name, String... tracks)
    {
        getGuildMixtapes(guildID);
        if (mixtapes.get(guildID).containsKey(name))
        {
            for (String track : tracks)
            {
                mixtapes.get(guildID).get(name).remove(track);
            }
            save();
        }
    }

    public static synchronized ArrayList<String> getTracks(long guildID, String name)
    {
        return getGuildMixtapes(guildID).get(name);
    }

    public static synchronized HashMap<String, ArrayList<String>> getGuildMixtapes(long guildID)
    {
        if (!getMixtapes().containsKey(guildID))
        {
            mixtapes.put(guildID, new HashMap<String, ArrayList<String>>());
        }
        return mixtapes.get(guildID);
    }

    public static synchronized boolean isMixtapeNameTaken(long guildID, String name)
    {
        return getGuildMixtapes(guildID).containsKey(name);
    }

}
