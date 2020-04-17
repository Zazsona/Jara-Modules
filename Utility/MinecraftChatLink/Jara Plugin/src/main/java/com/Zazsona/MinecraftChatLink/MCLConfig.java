package com.Zazsona.MinecraftChatLink;

import com.Zazsona.minecraftCommon.FileManager;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.Collection;

public class MCLConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        String ip = FileManager.getIpForGuild(msgEvent.getGuild().getId());
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        MessageManager mm = new MessageManager();
        if (isSetup && ip == null || !isSetup)
        {
            runIPMenu(msgEvent, isSetup, ip, embed, mm);
        }
        runChannelMenu(msgEvent, isSetup, embed, mm);
        if (isSetup)
        {
            MinecraftChatLinkCommand.sendChatLinkUUID(msgEvent.getGuild(), msgEvent.getMember().getUser());
        }
        MinecraftMessageManager.getInstance(msgEvent.getGuild()).stopConnection();
        new Thread(() -> MinecraftMessageManager.getInstance(msgEvent.getGuild()).startConnection()).start();
    }

    private void runChannelMenu(GuildMessageReceivedEvent msgEvent, boolean isSetup, EmbedBuilder embed, MessageManager mm)
    {
        TextChannel savedChannel = getSavedChannel(msgEvent.getGuild());
        embed.setDescription("Current Channel: "+((savedChannel == null) ? "None" : savedChannel.getName())+"\nPlease mention the channel to link Minecraft chat messages with, or `reset` to disable.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        while (true)
        {
            Message msg = mm.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
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
                ChatLinkFileManager.resetChannelForGuild(msgEvent.getGuild().getId());
                ChatLinkFileManager.save();
                embed.setDescription("Channel reset.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                MinecraftMessageManager.getInstance(msgEvent.getGuild()).stopConnection();
                return;
            }
            else
            {
                if (msg.getMentionedChannels().size() > 0)
                {
                    TextChannel channel = msg.getMentionedChannels().get(0);
                    setChannel(msgEvent, channel);
                    return;
                }
            }
        }
    }

    private void runIPMenu(GuildMessageReceivedEvent msgEvent, boolean isSetup, String ip, EmbedBuilder embed, MessageManager mm)
    {
        embed.setDescription("Current IP: "+((ip == null) ? "None" : ip)+"\n\nPlease set a default IP for Minecraft server commands, use reset to remove, or quit to cancel.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();

        while (true)
        {
            Message msg = mm.getNextMessage(msgEvent.getChannel(), msgEvent.getMember());
            String content = msg.getContentDisplay();
            if (content.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit") || content.equalsIgnoreCase("quit"))
            {
                if (!isSetup)
                {
                    embed.setDescription("Menu quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                break;
            }
            else if (content.equalsIgnoreCase("reset"))
            {
                FileManager.resetIpForGuild(msgEvent.getGuild().getId());
                FileManager.save();
                embed.setDescription("IP reset.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                break;
            }
            boolean success = setNewIpAddress(msgEvent, content);
            if (success)
            {
                break;
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> parameters, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        run(msgEvent, guildSettings, textChannel, false);
    }

    /**
     * Sets the Minecraft server IP to negotiate with.
     * @param msgEvent the context
     * @param ip the ip
     * @return boolean on success.
     */
    private boolean setNewIpAddress(GuildMessageReceivedEvent msgEvent, String ip)
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        if (ip.contains("."))
        {
            FileManager.setIpForGuild(msgEvent.getGuild().getId(), ip);
            FileManager.save();
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

    /**
     * Sets the channel to send and receive Minecraft messages from.
     * @param msgEvent the context
     * @param channelToSet the channel to send/receive from
     */
    private void setChannel(GuildMessageReceivedEvent msgEvent, TextChannel channelToSet)
    {
        ChatLinkFileManager.setChannelForGuild(channelToSet.getGuild().getId(), channelToSet.getId());
        ChatLinkFileManager.save();
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Channel set.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    /**
     * Gets the channel to send and receive Minecraft messages from.
     * @param guild the guild to query
     * @return the channel
     */
    private TextChannel getSavedChannel(Guild guild)
    {
        try
        {
            String channelID = ChatLinkFileManager.getChannelIDForGuild(guild.getId());
            return guild.getTextChannelById(channelID);
        }
        catch (NullPointerException e)
        {
            return null;
        }
    }
}
