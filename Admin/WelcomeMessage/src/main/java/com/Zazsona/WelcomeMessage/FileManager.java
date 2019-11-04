package com.Zazsona.WelcomeMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager implements Serializable
{
    private static transient FileManager fm;
    private static long serialVersionUID = 1L;
    private HashMap<String, String> guildToMessageMap; //GuildID : Message
    private ArrayList<String> enabledGuilds;
    private static transient Logger logger = LoggerFactory.getLogger("WelcomeMessageModifier");

    private FileManager()
    {
    }

    public static FileManager getInstance()
    {
        if (fm == null)
        {
            restore();
        }
        return fm;
    }

    private static String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/WelcomeMessages.jara";
    }

    public static synchronized void save()
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
            String json = gson.toJson(fm);
            FileOutputStream fos = new FileOutputStream(getSavePath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
    }

    private static synchronized void restore()
    {
        try
        {
            File saveFile = new File(getSavePath());
            if (saveFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(saveFile.toPath()));
                fm = gson.fromJson(json, FileManager.class);
            }
            else
            {
                fm = new FileManager();
                fm.guildToMessageMap = new HashMap<>();
                fm.enabledGuilds = new ArrayList<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.toString());
            return;
        }
    }

    public void modifyGuildState(String guildID, boolean enable)
    {
        try
        {
            fm.enabledGuilds.remove(guildID);
            if (enable)
            {
                fm.enabledGuilds.add(guildID);
            }
            save();
        }
        catch (NullPointerException e)
        {
            restore();
            modifyGuildState(guildID, enable);
        }
    }

    public boolean isGuildEnabled(String guildID)
    {
        try
        {
            return fm.enabledGuilds.contains(guildID);
        }
        catch (NullPointerException e)
        {
            restore();
            return isGuildEnabled(guildID);
        }
    }

    public void setWelcomeMessage(String guildID, String message)
    {
        try
        {
            fm.guildToMessageMap.put(guildID, message);
            save();
        }
        catch (NullPointerException e)
        {
            restore();
            setWelcomeMessage(guildID, message);
        }
    }

    public String getWelcomeMessage(String guildID)
    {
        try
        {
            String message = fm.guildToMessageMap.get(guildID);
            return message;
        }
        catch (NullPointerException e)
        {
            restore();
            return getWelcomeMessage(guildID);
        }
    }
}
