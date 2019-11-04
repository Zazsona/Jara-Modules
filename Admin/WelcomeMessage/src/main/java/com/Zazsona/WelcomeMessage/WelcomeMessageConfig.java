package com.Zazsona.WelcomeMessage;

import commands.CmdUtil;
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

public class WelcomeMessageConfig extends ModuleConfig
{
    private MessageManager mm;
    private FileManager fm = FileManager.getInstance();

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        mm = new MessageManager();
        EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
        while (true)
        {
            embed.setDescription("This is the Welcome Message config, for configuring the message sent to users when joining the server.\n\n**Toggle**\n**Message**\n**Quit**");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            Message input = getInput(msgEvent, textChannel);
            if (input == null)
            {
                if (!isSetup)
                {
                    embed.setDescription("Exited.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
                return;
            }
            else if (input.getContentDisplay().equalsIgnoreCase("toggle"))
            {
                toggle(msgEvent, textChannel, embed);
            }
            else if (input.getContentDisplay().equalsIgnoreCase("message"))
            {
                setWelcomeMessage(msgEvent, textChannel, embed);
            }
        }
    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (collection.size() > 0)
        {
            mm = new MessageManager();
            ArrayList<String> parameters = new ArrayList(collection);
            if (parameters.get(0).equalsIgnoreCase("toggle"))
            {
                if (parameters.size() > 1)
                {
                    String toggleValue = parameters.get(1);
                    if (toggleValue.equals("y") || toggleValue.equals("enable") || toggleValue.equals("yes"))
                    {
                        fm.modifyGuildState(msgEvent.getGuild().getId(), true);
                        return;
                    }
                    else if (toggleValue.equals("n") || toggleValue.equals("disable") || toggleValue.equals("no"))
                    {
                        fm.modifyGuildState(msgEvent.getGuild().getId(), false);
                        return;
                    }
                }
                toggle(msgEvent, textChannel, getDefaultEmbedStyle(msgEvent));
            }
            else if (parameters.get(0).equalsIgnoreCase("message"))
            {
                setWelcomeMessage(msgEvent, textChannel, getDefaultEmbedStyle(msgEvent));
            }
            else
            {
                EmbedBuilder embed = getDefaultEmbedStyle(msgEvent).setDescription("Unknown Welcome Message menu. Please try again.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        else
        {
            run(msgEvent, guildSettings, textChannel, false);
        }
    }

    private void toggle(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed)
    {
        embed.setDescription("Would you like to enable welcome messages? (Y/N)\nCurrent status: "+(fm.isGuildEnabled(msgEvent.getGuild().getId()) ? "Enabled" : "Disabled"));
        textChannel.sendMessage(embed.build()).queue();
        Message input = getInput(msgEvent, textChannel);
        if (input != null)
        {
            String response = input.getContentDisplay().toLowerCase();
            response = response.toLowerCase();
            if (response.equals("y") || response.equals("enable") || response.equals("yes"))
            {
                fm.modifyGuildState(msgEvent.getGuild().getId(), true);
                embed.setDescription("Welcome Message enabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else if (response.equals("n") || response.equals("disable") || response.equals("no"))
            {
                fm.modifyGuildState(msgEvent.getGuild().getId(), false);
                embed.setDescription("Welcome Message disabled.");
                textChannel.sendMessage(embed.build()).queue();
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        else
        {
            return;
        }
    }

    private void setWelcomeMessage(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, EmbedBuilder embed)
    {
        String existingWelcomeMessage = fm.getWelcomeMessage(msgEvent.getGuild().getId());
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setDescription("**Please enter a new welcome message.**\n\n"+((existingWelcomeMessage == null) ? "Existing Message: None." : "You can view the existing message using the WelcomeMessage command."));
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        Message message = getInput(msgEvent, textChannel);
        if (message != null)
        {
            String messageContent = message.getContentRaw();
            fm.setWelcomeMessage(msgEvent.getGuild().getId(), messageContent);
            embed.setDescription("**Welcome message set.**\n\n"+messageContent);
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            if (!fm.isGuildEnabled(msgEvent.getGuild().getId()))
            {
                toggle(msgEvent, textChannel, embed);
            }
        }
    }

    private Message getInput(GuildMessageReceivedEvent msgEvent, TextChannel textChannel) throws InvalidParameterException
    {
        Message message = null;
        while (true)
        {
            message = mm.getNextMessage(textChannel);
            if (message.getMember().equals(msgEvent.getMember()))
            {
                if (message.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"quit") || message.getContentDisplay().equalsIgnoreCase("quit"))
                {
                    return null;
                }
                else if (!message.getContentDisplay().startsWith(String.valueOf(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId()))))
                {
                    return message;
                }
            }
        }
    }
}
