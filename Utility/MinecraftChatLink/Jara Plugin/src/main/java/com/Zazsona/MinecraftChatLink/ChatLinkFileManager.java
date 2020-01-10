package com.Zazsona.MinecraftChatLink;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;

public class ChatLinkFileManager
{
    private static HashMap<String, String> guildToChannelMap;
    private transient static Logger logger = LoggerFactory.getLogger(ChatLinkFileManager.class);

    private static String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/MinecraftChatLinkChannels.jara";
    }

    public static synchronized void save()
    {
        try
        {
            File saveFile = new File(getSavePath());
            if (!saveFile.exists())
            {
                saveFile.getParentFile().mkdirs();
                saveFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(guildToChannelMap);
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

    public static synchronized void restore()
    {
        try
        {
            File saveFile = new File(getSavePath());
            if (saveFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(saveFile.toPath()));
                TypeToken<HashMap<String, String>> token = new TypeToken<HashMap<String, String>>() {};
                guildToChannelMap = gson.fromJson(json, token.getType());
            }
            else
            {
                guildToChannelMap = new HashMap<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }

    /**
     * Gets a HashMap, mapping guilds to the TextChannel ID Minecraft messages are sent to and from.
     * @return the HashMap.
     */
    public static HashMap<String, String> getGuildToChannelMap()
    {
        if (guildToChannelMap == null)
        {
            restore();
        }
        return guildToChannelMap;
    }

    /**
     * Gets the ID of the TextChannel to send/receive Minecraft messages to
     * @param guildID the guild to checks id
     * @return the channel, or null if none has been set
     */
    public static String getChannelIDForGuild(String guildID)
    {
        getGuildToChannelMap();
        return guildToChannelMap.get(guildID);
    }

    /**
     * Sets the TextChannel to send/receive Minecraft messages to
     * @param guildID the ID of the guild the channel is in
     * @param channelID the ID of the channel
     */
    public static void setChannelForGuild(String guildID, String channelID)
    {
        getGuildToChannelMap();
        guildToChannelMap.put(guildID, channelID);
        save();
    }

    /**
     * Removes the TextChannel to send/receive Minecraft messages to
     * @param guildID the guild to remove the channel from
     */
    public static void resetChannelForGuild(String guildID)
    {
        getGuildToChannelMap();
        guildToChannelMap.remove(guildID);
        save();
    }
}
