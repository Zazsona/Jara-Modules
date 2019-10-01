package com.Zazsona.Pin;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.util.List;

public class Pin extends Command
{

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 1)
            {
                Message messageToPin = null;
                try
                {
                    messageToPin = getMessageByID(msgEvent.getChannel(), parameters[1]);
                }
                catch (IllegalArgumentException e)
                {
                    if (msgEvent.getMessage().getMentionedMembers().size() > 0)
                    {
                        messageToPin = getMessageByMember(msgEvent.getChannel(), msgEvent.getMessage().getMentionedMembers());
                    }
                    else
                    {
                        messageToPin = getMessageByUsername(msgEvent.getChannel(), compileParameters(parameters));
                    }
                }
                if (messageToPin != null)
                {
                    messageToPin.pin().queue();
                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                    embed.setDescription("Could not find any valid message in this channel.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        catch (InsufficientPermissionException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("Bot has insufficient Discord permissions to be able to find and pin messages.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    private String compileParameters(String[] parameters)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i<parameters.length; i++)
        {
            sb.append(parameters[i]).append(" ");
        }
        return sb.toString().trim();
    }

    private Message getMessageByID(TextChannel channel, String messageID)
    {
        return channel.getMessageById(messageID).complete();
    }

    private Message getMessageByUsername(TextChannel channel, String name)
    {
        List<Member> members = channel.getGuild().getMembersByEffectiveName(name, true);
        return getMessageByMember(channel, members);
    }

    private Message getMessageByMember(TextChannel channel, List<Member> members)
    {
        List<Message> messages = channel.getHistory().retrievePast(51).complete();
        messages.remove(0); //Removes the command invocation message.
        for (Message message : messages)
        {
            if (members.contains(message.getMember()))
            {
                return message;
            }
        }
        return null;
    }
}
