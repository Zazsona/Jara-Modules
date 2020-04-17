package com.Zazsona.MinecraftChatLink;

import commands.CmdUtil;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MinecraftChatLinkCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length == 1)
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
        else if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("uuid"))
            {
                if (parameters.length == 2)
                {
                    sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser());
                }
                else if (parameters.length > 2)
                {
                    if (parameters[2].equalsIgnoreCase("reset"))
                    {
                        ChatLinkFileManager.resetUUIDForGuild(msgEvent.getGuild().getId());
                        ChatLinkFileManager.save();
                        sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser());
                    }
                }
            }
        }
    }

    private static EmbedBuilder getEmbedStyle(Member selfMember)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        embed.setFooter("Minecraft");
        return embed;
    }

    public static void sendChatLinkUUID(Guild guild, User user)
    {
        user.openPrivateChannel().complete().sendMessage(getEmbedStyle(guild.getSelfMember()).setDescription(
                "Minecraft UUID for "+guild.getName()+":\n`"+ChatLinkFileManager.getUUIDForGuild(guild.getId())+"`"
        ).build()).queue();
    }
}
