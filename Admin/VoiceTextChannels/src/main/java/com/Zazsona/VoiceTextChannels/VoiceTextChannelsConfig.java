package com.Zazsona.VoiceTextChannels;

import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

public class VoiceTextChannelsConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
        embed.setDescription("Current setting: "+VoiceTextChannelsLoader.getFileManager().isGuildEnabled(msgEvent.getGuild().getId())+"\n\nWould you like to enable Voice Text Channels? (Y/N)\nUse quit to cancel.");
        textChannel.sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        Message message = null;
        while (true)
        {
            try
            {
                while (true)
                {
                    message = mm.getNextMessage(textChannel);
                    if (message.getMember().equals(msgEvent.getMember()))
                    {
                        break;
                    }
                }
                if (message.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"quit") || message.getContentDisplay().equalsIgnoreCase("quit"))
                {
                    if (!isSetup)
                    {
                        embed.setDescription("Operation cancelled.");
                        msgEvent.getChannel().sendMessage(embed.build()).queue();
                        return;
                    }
                }
                else
                {
                    setEnabledState(msgEvent, message.getContentDisplay(), guildSettings, textChannel);
                    return;
                }
            }
            catch (InvalidParameterException e)
            {
                embed.setDescription("Invalid response. Yes or No expected.\nPlease try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (collection.size() > 0)
        {
            try
            {
                ArrayList<String> list = new ArrayList<>(collection);
                setEnabledState(msgEvent, list.get(0), guildSettings, textChannel);
            }
            catch (InvalidParameterException e)
            {
                EmbedBuilder embed = this.getDefaultEmbedStyle(msgEvent);
                embed.setDescription("Invalid response. Yes or No expected.\nPlease try again.");
                textChannel.sendMessage(embed.build()).queue();
            }
        }
        else
        {
            run(msgEvent, guildSettings, textChannel, false);
        }
    }

    private void setEnabledState(GuildMessageReceivedEvent msgEvent, String response, GuildSettings guildSettings, TextChannel textChannel) throws InvalidParameterException
    {
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        response = response.toLowerCase();
        if (response.equals("y") || response.equals("enable") || response.equals("yes"))
        {
            VoiceTextChannelsLoader.getFileManager().enableGuild(msgEvent.getGuild().getId());
            embed.setDescription("Voice Text Channels enabled.");
            textChannel.sendMessage(embed.build()).queue();
        }
        else if (response.equals("n") || response.equals("disable") || response.equals("no"))
        {
            VoiceTextChannelsLoader.getFileManager().disableGuild(msgEvent.getGuild().getId());
            embed.setDescription("Voice Text Channels disabled.");
            textChannel.sendMessage(embed.build()).queue();
        }
        else
        {
            throw new InvalidParameterException();
        }

    }
}
