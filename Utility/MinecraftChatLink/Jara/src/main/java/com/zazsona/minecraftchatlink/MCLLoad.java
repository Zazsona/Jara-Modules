package com.zazsona.minecraftchatlink;

import com.zazsona.minecraftchatlink.data.ChatLinkData;
import com.zazsona.minecraftchatlink.data.GuildChatLink;
import jara.Core;
import module.ModuleLoad;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class MCLLoad extends ModuleLoad
{
    @Override
    public void load()
    {
        try
        {
            Set<String> registeredGuilds = ChatLinkData.getInstance().getRegisteredGuilds();
            for (String guildId : registeredGuilds)
            {
                GuildChatLink guildLink = ChatLinkData.getInstance().getGuild(guildId);
                if (guildLink.isEnabled())
                {
                    ChatLinkClient clc = ChatLinkClient.getInstance(guildLink.getGuildId());
                    new Thread(() -> clc.startClient()).start();
                }
            }
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(MCLLoad.class).error("Unable to load chat link data: "+e.getMessage());
        }
    }
}
