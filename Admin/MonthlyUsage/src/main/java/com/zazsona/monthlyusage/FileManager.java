package com.zazsona.monthlyusage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zazsona.jara.configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class FileManager implements Serializable
{
    private static long serialVersionUID = 1L;
    private long lastReset;
    private HashMap<String, HashMap<String, Integer>> commandUsage; //GuildID : UserID, CommandCount
    private static transient Logger logger = LoggerFactory.getLogger("CommandUsageLoader");

    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/CommandUsage.jara";
    }

    public void save()
    {
        synchronized (this)
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
                String json = gson.toJson(this);
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

    }

    public void restore()
    {
        synchronized (this)
        {
            try
            {
                File saveFile = new File(getSavePath());
                if (saveFile.exists())
                {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = new String(Files.readAllBytes(saveFile.toPath()));
                    FileManager fm = gson.fromJson(json, FileManager.class);
                    this.commandUsage = (fm.commandUsage == null) ? new HashMap<>() : fm.commandUsage;
                    this.lastReset = (fm.lastReset == 0) ? Instant.now().getEpochSecond() : fm.lastReset;
                }
                else
                {
                    commandUsage = new HashMap<>();
                    lastReset = Instant.now().getEpochSecond();
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage());
                return;
            }
        }
    }

    public void addUsage(String guildID, String userID)
    {
        synchronized (this)
        {
            if (commandUsage.get(guildID) == null)
            {
                commandUsage.put(guildID, new HashMap<>());
            }
            if (!commandUsage.get(guildID).containsKey(userID))
            {
                commandUsage.get(guildID).put(userID, 1);
            }
            else
            {
                int recordedNum = commandUsage.get(guildID).get(userID);
                recordedNum++;
                commandUsage.get(guildID).put(userID, recordedNum);
            }
        }

    }

    public void reset()
    {
        synchronized (this)
        {
            commandUsage = new HashMap<>();
            lastReset = Instant.now().getEpochSecond();
        }
        save();
    }

    public HashMap<String, HashMap<String, Integer>> getCommandUsage()
    {
        return commandUsage;
    }

    public HashMap<String, Integer> getGuildCommandUsage(String guildID)
    {
        return commandUsage.get(guildID);
    }

    public OffsetDateTime getLastReset()
    {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(lastReset), ZoneOffset.UTC);
    }
}
