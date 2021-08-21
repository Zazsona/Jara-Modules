package com.zazsona.minecraftchatlink;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.GuildSettings;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.minecraftchatlink.data.ChatLinkData;
import com.zazsona.minecraftchatlink.data.GuildChatLink;
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
        try
        {
            if (parameters.length == 1)
            {
                runConfig(msgEvent);
            }
            else if (parameters.length > 1)
            {
                GuildChatLink guildChatLink = ChatLinkData.getInstance().getGuild(msgEvent.getGuild().getId());
                if (guildChatLink == null)
                {
                    EmbedBuilder embed = getEmbedStyle(msgEvent.getGuild().getSelfMember());
                    embed.setDescription("There is no config for this guild! Please run the main Chat Link command to get set up.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                else if (parameters[1].equalsIgnoreCase("uuid"))
                {
                    manageChatLinkId(msgEvent, parameters, guildChatLink);
                }
            }
        }
        catch (IOException e)
        {
            EmbedBuilder embed = getEmbedStyle(msgEvent.getGuild().getSelfMember());
            embed.setDescription("Unable to access chat link data.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            LoggerFactory.getLogger(MinecraftChatLinkCommand.class).error("Unable to access chat link data - "+e.getMessage());
        }
    }

    private void manageChatLinkId(GuildMessageReceivedEvent msgEvent, String[] parameters, GuildChatLink guildChatLink)
    {
        try
        {
            if (parameters.length == 2)
            {
                sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser(), guildChatLink.getChatLinkId());
            }
            else if (parameters.length > 2 && parameters[2].equalsIgnoreCase("reset"))
            {
                guildChatLink.resetChatLinkId();
                guildChatLink.save();
                sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser(), guildChatLink.getChatLinkId());
            }
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(this.getClass()).error(e.toString());
            msgEvent.getChannel().sendMessage("An error occurred when saving settings.").queue();
        }
    }

    private void runConfig(GuildMessageReceivedEvent msgEvent)
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

    public static void sendChatLinkUUID(Guild guild, User user, String uuid) throws IOException
    {
        user.openPrivateChannel().complete().sendMessage(getEmbedStyle(guild.getSelfMember())
                                                                 .setDescription("Minecraft UUID for "+guild.getName()+":\n`"+uuid+"`")
                                                                 .build()).queue();
    }

    private static EmbedBuilder getEmbedStyle(Member selfMember)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(selfMember));
        embed.setFooter("Minecraft");
        return embed;
    }
}
