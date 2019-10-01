package com.Zazsona.VoiceTextChannels;

import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class FileManager implements Serializable
{
    private ArrayList<String> enabledGuilds;
    private static transient Logger logger = LoggerFactory.getLogger("VoiceTextChannels");

    public FileManager()
    {
        restore();
    }

    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/VoiceTextChannelsConfig.jara";
    }

    private synchronized void save()
    {
        try
        {
            File quoteFile = new File(getSavePath());
            if (!quoteFile.exists())
            {
                quoteFile.getParentFile().mkdirs();
                quoteFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getSavePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(enabledGuilds);
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
        try
        {
            if (new File(getSavePath()).exists())
            {
                FileInputStream fis = new FileInputStream(getSavePath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                enabledGuilds = (ArrayList<String>) ois.readObject();
                ois.close();
                fis.close();
            }
            else
            {
                enabledGuilds = new ArrayList<>();
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

    public void enableGuild(String guildID)
    {
        if (!enabledGuilds.contains(guildID))
        {
            enabledGuilds.add(guildID);
            save();
        }
    }

    public void disableGuild(String guildID)
    {
        enabledGuilds.remove(guildID);
        save();
    }

    public boolean isGuildEnabled(String guildID)
    {
        return enabledGuilds.contains(guildID);
    }
}
