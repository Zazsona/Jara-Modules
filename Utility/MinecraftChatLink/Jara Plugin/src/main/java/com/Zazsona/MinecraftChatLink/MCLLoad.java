package com.Zazsona.MinecraftChatLink;

import jara.Core;
import module.ModuleLoad;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Set;

public class MCLLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        Set<String> registeredGuilds = ChatLinkFileManager.getGuildToDataMap().keySet();
        for (String guildID : registeredGuilds)
        {
            if (ChatLinkFileManager.isChatLinkConfigurationComplete(guildID))
            {
                new Thread(() ->
                           {
                               Guild guild = Core.getShardManagerNotNull().getGuildById(guildID);
                               MinecraftMessageManager mmm = MinecraftMessageManager.getInstance(guild);
                               mmm.startConnection();
                           }).start();
            }
        }
    }
}
