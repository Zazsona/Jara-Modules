package com.Zazsona.MinecraftServerQuery;

import com.Zazsona.minecraftCommon.FileManager;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class MSQConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        String ip = FileManager.getIpForGuild(msgEvent.getGuild().getId());
        if (isSetup && ip == null || !isSetup)
        {
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            embed.setDescription("Current IP: "+((ip == null) ? "None" : ip)+"\n\nPlease set a default IP for Minecraft server commands, use reset to remove, or quit to cancel.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();

            MessageManager mm = new MessageManager();
            while (true)
            {
                Message msg = mm.getNextMessage(msgEvent.getChannel());
                if (msg.getMember().equals(msgEvent.getMember()))
                {
                    String content = msg.getContentDisplay();
                    if (content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit") || content.equalsIgnoreCase("quit"))
                    {
                        if (!isSetup)
                        {
                            embed.setDescription("Menu quit.");
                            msgEvent.getChannel().sendMessage(embed.build()).queue();
                        }
                        return;
                    }
                    else if (content.equalsIgnoreCase("reset"))
                    {
                        FileManager.resetIpForGuild(msgEvent.getGuild().getId());
                        embed.setDescription("IP reset.");
                        msgEvent.getChannel().sendMessage(embed.build()).queue();
                        return;
                    }
                    boolean success = setNewIpAddress(msgEvent, content);
                    if (success)
                    {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> parameters, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (parameters.size() > 0)
        {
            Iterator<String> it = parameters.iterator();
            setNewIpAddress(msgEvent, it.next());
        }
        else
        {
            run(msgEvent , guildSettings, textChannel, false);
        }
    }

    private boolean setNewIpAddress(GuildMessageReceivedEvent msgEvent, String ip)
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        if (ip.contains("."))
        {
            FileManager.setIpForGuild(msgEvent.getGuild().getId(), ip);
            embed.setDescription("Default Minecraft IP set to: "+ip);
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            return true;
        }
        else
        {
            embed.setDescription("Invalid IP: "+ip+"\nPlease try again.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            return false;
        }
    }
}
