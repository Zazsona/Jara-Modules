package com.zazsona.minecraftchatlink.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import commands.CmdUtil;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ChatLinkData
{
    private static transient ChatLinkData instance;
    private static HashMap<String, GuildChatLink> guildToLinkMap;
    private transient static Logger logger = LoggerFactory.getLogger(ChatLinkData.class);

    private String filePath;
    private Object lock = new Object();

    public static ChatLinkData getInstance() throws IOException
    {
        if (instance == null)
            instance = new ChatLinkData();
        return instance;
    }

    private ChatLinkData() throws IOException
    {
        filePath = SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/MinecraftChatLinkChannels.jara";
        restore();
    }

    /**
     * Writes the current data in memory to file
     * @throws IOException unable to save file
     */
    public void save() throws IOException
    {
        synchronized (lock)
        {
            File saveFile = new File(filePath);
            if (!saveFile.exists())
            {
                saveFile.getParentFile().mkdirs();
                saveFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(guildToLinkMap);
            FileOutputStream fos = new FileOutputStream(saveFile.getPath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
    }

    /**
     * Loads the saved data from file
     * @throws IOException unable to load file
     */
    public void restore() throws IOException
    {
        synchronized (lock)
        {
            File saveFile = new File(filePath);
            if (saveFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(saveFile.toPath()));
                TypeToken<HashMap<String, GuildChatLink>> token = new TypeToken<HashMap<String, GuildChatLink>>() {};
                guildToLinkMap = gson.fromJson(json, token.getType());
            }
            else
            {
                guildToLinkMap = new HashMap<>();
            }
        }
    }

    /**
     * Registers a new guild, with the default channel set for messages.
     * @param guildId the guild to add
     */
    public void addGuild(String guildId)
    {
        String channelId = CmdUtil.getJDA(0).getGuildById(guildId).getDefaultChannel().getId();
        addGuild(guildId, channelId);
    }

    /**
     * Registers a new guild, with messages directed towards the provided channel. This will overwrite any existing registry for existing guilds.
     * @param guildId the guild to add
     * @param textChannelId the channel to send messages to
     */
    public void addGuild(String guildId, String textChannelId)
    {
        GuildChatLink guildChatLink = new GuildChatLink(UUID.randomUUID().toString(), guildId, textChannelId);
        guildToLinkMap.put(guildId, guildChatLink);
    }

    /**
     * Updates the registration for the guild.
     * @param guildChatLink the registration to set.
     */
    public void updateGuild(GuildChatLink guildChatLink)
    {
        guildToLinkMap.put(guildChatLink.getGuildId(), guildChatLink);
    }

    /**
     * Gets the chat link data for the specified guild
     * @param guildId the guild to fetch data for
     * @return the link data
     */
    public GuildChatLink getGuild(String guildId)
    {
        return guildToLinkMap.get(guildId);
    }

    /**
     * Removes all data associated with the guild
     * @param guildId the guild to remove
     */
    public void removeGuild(String guildId)
    {
        guildToLinkMap.remove(guildId);
    }

    /**
     * Gets the ids for all registered guilds.
     * @return set of guild ids.
     */
    public Set<String> getRegisteredGuilds()
    {
        return guildToLinkMap.keySet();
    }
}
