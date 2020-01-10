package com.Zazsona.MinecraftChatLink;

import configuration.GuildSettings;
import configuration.SettingsUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MinecraftChatLinkCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            GuildSettings guildSettings = SettingsUtil.getGuildSettings(msgEvent.getGuild().getId());
            if (guildSettings.isPermitted(msgEvent.getMember(), "Config"))
            {
                new MCLConfig().run(msgEvent, guildSettings, msgEvent.getChannel(), false);
            }
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(this.getClass()).error(e.toString());
            msgEvent.getChannel().sendMessage("An error occurred when saving settings.").queue();
        }
    }
}
