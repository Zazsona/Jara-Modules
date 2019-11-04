package com.Zazsona.MessageResponder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class FileManager implements Serializable
{
    private static long serialVersionUID = 1L;
    private LinkedHashMap<String, HashMap<String, String>> guildToMessagesMap; //GuildID : ReceiveMessage, ResponseMessage
    private static transient Logger logger = LoggerFactory.getLogger("MessageResponseLoader");

    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/MessageResponses.jara";
    }

    public synchronized void save()
    {
        try
        {
            File file = new File(getSavePath());
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
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
            logger.error(e.toString());
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
                FileManager fm = gson.fromJson(json, this.getClass());
                this.guildToMessagesMap = fm.guildToMessagesMap;
            }
            else
            {
                guildToMessagesMap = new LinkedHashMap<>();
            }
        }
        catch (IOException e)
        {
            logger.error(e.toString());
            return;
        }
    }

    public void addMessageResponse(String guildID, String receivedMessage, String responseMessage)
    {
        if (!guildToMessagesMap.containsKey(guildID))
        {
            guildToMessagesMap.put(guildID, new HashMap<>());
        }
        guildToMessagesMap.get(guildID).put(receivedMessage.toLowerCase(), responseMessage);
        save();
    }

    public void removeMessageResponse(String guildID, String receivedMessage)
    {
        guildToMessagesMap.get(guildID).remove(receivedMessage.toLowerCase());
        save();
    }

    public HashMap<String, String> getMessageResponses(String guildID)
    {
        return guildToMessagesMap.get(guildID);
    }

    public boolean doesGuildHaveResponses(String guildID)
    {
        return (guildToMessagesMap.get(guildID) != null && guildToMessagesMap.get(guildID).size() > 0);
    }


}
