package com.Zazsona.MinecraftChatLink;

import com.Zazsona.minecraftCommon.FileManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

public class ChatLinkFileManager
{
    private static HashMap<String, ChatLinkData> guildToDataMap;
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
            String json = gson.toJson(guildToDataMap);
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
                TypeToken<HashMap<String, ChatLinkData>> token = new TypeToken<HashMap<String, ChatLinkData>>() {};
                guildToDataMap = gson.fromJson(json, token.getType());
            }
            else
            {
                guildToDataMap = new HashMap<>();
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
    public static HashMap<String, ChatLinkData> getGuildToDataMap()
    {
        if (guildToDataMap == null)
        {
            restore();
        }
        return guildToDataMap;
    }

    /**
     * Gets the ID of the TextChannel to send/receive Minecraft messages to
     * @param guildID the guild to checks id
     * @return the channel
     * @throws NullPointerException guild has no data
     */
    public static String getChannelIDForGuild(String guildID) throws NullPointerException
    {
        getGuildToDataMap();
        return guildToDataMap.get(guildID).textChannelID;
    }

    /**
     * Sets the TextChannel to send/receive Minecraft messages to. If the guild is not registered, this will also set a default UUID.
     * @param guildID the ID of the guild the channel is in
     * @param channelID the ID of the channel
     */
    public static void setChannelForGuild(String guildID, String channelID)
    {
        getGuildToDataMap();
        ChatLinkData cld = guildToDataMap.get(guildID);
        if (cld != null)
        {
            cld.textChannelID = channelID;
            guildToDataMap.put(guildID, cld);
        }
        else
        {
            guildToDataMap.put(guildID, new ChatLinkData(UUID.randomUUID().toString(), channelID));
        }
        save();
    }

    /**
     * Removes the TextChannel to send/receive Minecraft messages to
     * @param guildID the guild to remove the channel from
     * @throws NullPointerException guild has no data
     */
    public static void resetChannelForGuild(String guildID) throws NullPointerException
    {
        getGuildToDataMap();
        guildToDataMap.get(guildID).textChannelID = null;
        save();
    }

    /**
     * Gets the UUID for this guild's Minecraft link
     * @param guildID the guild to fetch data for
     * @return the UUID
     * @throws NullPointerException guild has no data
     */
    public static String getUUIDForGuild(String guildID) throws NullPointerException
    {
        getGuildToDataMap();
        return guildToDataMap.get(guildID).UUID;
    }

    /**
     * Generates a new UUID for the guild
     * @param guildID the guild to reset
     * @return the new UUID
     */
    public static String resetUUIDForGuild(String guildID)
    {
        getGuildToDataMap();
        String newUUID = UUID.randomUUID().toString();
        if (guildToDataMap.containsKey(guildID))
        {
            guildToDataMap.get(guildID).UUID = newUUID;
        }
        else
        {
            guildToDataMap.put(guildID, new ChatLinkData(UUID.randomUUID().toString(), null));
        }
        return newUUID;
    }

    /**
     * Checks if all required fields have been filled. Note: this does not check if the channel specified by the ID exists.
     * @param guildID the guild to check
     * @return true on full config
     */
    public static boolean isChatLinkConfigurationComplete(String guildID)
    {
        String ip = FileManager.getIpForGuild(guildID);
        if (ip != null)
        {
            getGuildToDataMap();
            ChatLinkData cld = guildToDataMap.get(guildID);
            if (cld != null)
            {
                return (cld.UUID != null && cld.textChannelID != null);
            }
        }
        return false;
    }

    private static class ChatLinkData implements Serializable
    {
        public ChatLinkData(@NotNull String UUID, String textChannelID)
        {
            this.UUID = UUID;
            this.textChannelID = textChannelID;
        }

        String UUID;
        String textChannelID;
    }
}
