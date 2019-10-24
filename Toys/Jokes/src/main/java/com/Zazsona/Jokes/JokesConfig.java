package com.Zazsona.Jokes;

import commands.admin.config.ConfigMain;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JokesConfig extends ModuleConfig
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, TextChannel textChannel, boolean isSetup) throws IOException
    {
        if (!isSetup || isSetup && guildSettings.isCommandEnabled("Jokes"))
        {
            Boolean currentStatus = isNSFWFilterEnabled(msgEvent.getGuild().getId());
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            embed.setDescription("Would you like to enable the Jokes NSFW channel filter? (Y/N)\nCurrent Status: "+((currentStatus) ? "Enabled." : "Disabled."));
            textChannel.sendMessage(embed.build()).queue();
            executeAnswer(textChannel, embed);
        }

    }

    @Override
    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> parameters, GuildSettings guildSettings, TextChannel textChannel) throws IOException
    {
        if (parameters.size() == 0)
        {
            run(msgEvent, guildSettings, textChannel, false);
        }
        else
        {
            EmbedBuilder embed = getDefaultEmbedStyle(msgEvent);
            String userInput = new ArrayList<>(parameters).get(0);
            setValue(msgEvent.getGuild().getId(), userInput, embed, textChannel);
        }
    }

    private void executeAnswer(TextChannel channel, EmbedBuilder embed) throws IOException
    {
        GuildSettings guildSettings = SettingsUtil.getGuildSettings(channel.getGuild().getId());
        MessageManager mm = new MessageManager();
        while (true)
        {
            Message confirmMessage = mm.getNextMessage(channel);
            if (guildSettings.isPermitted(confirmMessage.getMember(), ConfigMain.class)) //If the message is from someone with config permissions
            {
                String msgContent = confirmMessage.getContentDisplay();
                Boolean result = setValue(channel.getGuild().getId(), msgContent, embed, channel);
                if (result == true)
                {
                    return;
                }
            }
        }
    }

    private Boolean setValue(String guildID, String userInput, EmbedBuilder embed, TextChannel channel) throws IOException
    {
        GuildSettings guildSettings = SettingsUtil.getGuildSettings(guildID);
        if (userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("yes") || userInput.equalsIgnoreCase("enable"))
        {
            guildSettings.setCustomModuleSetting(Jokes.NSFW_FILTER_KEY, true);
            embed.setDescription("Jokes NSFW channel filter is now enabled.");
            channel.sendMessage(embed.build()).queue();
            return true;
        }
        else if (userInput.equalsIgnoreCase("n") || userInput.equalsIgnoreCase("no") || userInput.equalsIgnoreCase("disable"))
        {
            guildSettings.setCustomModuleSetting(Jokes.NSFW_FILTER_KEY, false);
            embed.setDescription("Jokes NSFW channel filter is now disabled.");
            channel.sendMessage(embed.build()).queue();
            return true;
        }
        else if (userInput.equalsIgnoreCase("quit") || userInput.equalsIgnoreCase(guildSettings.getCommandPrefix()+"quit"))
        {
            embed.setDescription("Menu closed.");
            channel.sendMessage(embed.build()).queue();
            return false;
        }
        else
        {
            embed.setDescription("Unknown response. Please enter yes, no, or quit.");
            channel.sendMessage(embed.build()).queue();
            return false;
        }
    }


    protected static boolean isNSFWFilterEnabled(String guildID)
    {
        Boolean filterEnabled = (Boolean) SettingsUtil.getGuildSettings(guildID).getCustomModuleSetting(Jokes.NSFW_FILTER_KEY);
        if (filterEnabled == null)
            filterEnabled = true;

        return filterEnabled;
    }
}
