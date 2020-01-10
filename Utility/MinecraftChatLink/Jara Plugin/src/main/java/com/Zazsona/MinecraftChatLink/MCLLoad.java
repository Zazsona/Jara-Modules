package com.Zazsona.MinecraftChatLink;

import com.Zazsona.minecraftCommon.FileManager;
import jara.Core;
import module.ModuleLoad;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class MCLLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        HashMap<String, String> guildToChannelMap = ChatLinkFileManager.getGuildToChannelMap();
        for (Map.Entry<String, String> entry : guildToChannelMap.entrySet())
        {
            if (FileManager.getIpForGuild(entry.getKey()) != null && ChatLinkFileManager.getChannelIDForGuild(entry.getKey()) != null)
            {
                new Thread(() ->
                           {
                               Guild guild = Core.getShardManagerNotNull().getGuildById(entry.getKey());
                               MinecraftMessageManager mmm = MinecraftMessageManager.getInstance(guild);
                               mmm.startConnection();
                           }).start();
            }
        }
    }
}
