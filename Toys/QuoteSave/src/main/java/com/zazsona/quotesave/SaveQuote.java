package com.zazsona.quotesave;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.quote.FileManager;
import com.zazsona.quote.Quote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.regex.Pattern;

public class SaveQuote extends ModuleCommand
{
    private String invocationKey;

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        invocationKey = parameters[0];
        if (parameters.length >= 3)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (parameters.length > 3)
            {
                for (int i = 3; i<parameters.length; i++)
                {
                    parameters[2] += " "+parameters[i];
                }
            }
            if (parameters[2].length() > 35)
            {
                msgEvent.getChannel().sendMessage("That name is too long. Limit: 35 characters.").queue();
                return;
            }
            Message message = null;

            if (parameters[1].length() == 18 && Pattern.matches("[0-9]*", parameters[1]))
            {
                message = getMessageToQuoteByID(msgEvent.getChannel(), parameters[1]);
            }

            if (message == null) //By doing a null rather than if the parameter is a name, we can also check for users who have names like 123456789123456789
            {
                message = getMessageToQuoteByUsername(msgEvent.getChannel(), parameters[1]);
            }

            if (message == null) //If we still couldn't find it as a username...
            {
                embed.setDescription("Unable to find message. Please enter a valid user or message ID in the same channel as the message.");
            }
            else
            {
                FileManager fm = new FileManager(msgEvent.getGuild().getId());
                Quote quote = fm.addQuote(message, parameters[2]);
                if (quote != null)
                    embed.setDescription("Successfully saved quote "+quote.name+" by "+message.getMember().getEffectiveName()+"!");
                else
                    embed.setDescription("Error: A quote with that name already exists!");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private Message getMessageToQuoteByID(TextChannel channel, String messageID)
    {
        return channel.retrieveMessageById(messageID).complete();
    }

    private Message getMessageToQuoteByUsername(TextChannel channel, String name)
    {
        List<Member> members = channel.getGuild().getMembersByEffectiveName(name, true);
        List<Message> messages = channel.getHistory().retrievePast(20).complete();
        for (Message message : messages)
        {
            if (message.getContentDisplay().startsWith(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId()).toString()))
            {
                continue;
            }
            if (members.contains(message.getMember()))
            {
                return message;
            }
        }
        return null;
    }
}
