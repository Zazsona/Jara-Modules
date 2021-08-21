package com.zazsona.voicetextchannels;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zazsona.jara.configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileManager implements Serializable
{
    private ArrayList<String> enabledGuilds;
    private static transient Logger logger = LoggerFactory.getLogger("VoiceTextChannels");

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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(enabledGuilds);
            FileOutputStream fos = new FileOutputStream(getSavePath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    public synchronized void restore()
    {
        try
        {
            File configFile = new File(getSavePath());
            if (configFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(configFile.toPath()));
                TypeToken<ArrayList<String>> token = new TypeToken<ArrayList<String>>() {};
                enabledGuilds = gson.fromJson(json, token.getType());
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
