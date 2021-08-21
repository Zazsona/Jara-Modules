package com.zazsona.reminder;

import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReminderCustomisation
{
    private ReminderCommand rc;

    public ReminderCustomisation(ReminderCommand reminderCommand)
    {
        this.rc = reminderCommand;
    }

    protected String getMessage(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed, Message message)
    {
        String reminderContent;
        String[] parameters = message.getContentRaw().split(" ");
        if ((rc.getLastIndex()+1) < (parameters.length))
        {
            StringBuilder sb = new StringBuilder();
            for (int i = rc.getLastIndex()+1; i<parameters.length; i++)
            {
                sb.append(parameters[i]).append(" ");
            }
            reminderContent = sb.toString();
            rc.setLastIndex(parameters.length-1);
        }
        else
        {
            reminderContent = runMessageWizard(msgEvent, embed);
            if (message == null)
            {
                embed.setDescription("Reminder cancelled.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        return reminderContent;
    }

    @Nullable
    protected Member getMember(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed, Message message)
    {
        Member member;
        List<Member> members = message.getMentionedMembers();
        if (members.size() > 0)
        {
            rc.setLastIndex(rc.getLastIndex()+1);
            member = members.get(0);
        }
        else
        {
            member = runMemberWizard(msgEvent, embed);
            if (member == null)
            {
                embed.setDescription("Reminder cancelled.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        return member;
    }

    @Nullable
    protected TextChannel getTextChannel(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed, Message message)
    {
        TextChannel channel;
        List<TextChannel> channels = message.getMentionedChannels();
        if (channels.size() > 0)
        {
            rc.setLastIndex(rc.getLastIndex()+1);
            channel = channels.get(0);
        }
        else
        {
            channel = runChannelWizard(msgEvent, embed);
            if (channel == null)
            {
                embed.setDescription("Reminder cancelled.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        return channel;
    }



    private String runMessageWizard(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
    {
        embed.setDescription("Please enter your reminder message.");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        String message;
        while (true)
        {
            Message msg = mm.getNextMessage(msgEvent.getChannel());
            if (msg.getMember().equals(msgEvent.getMember()))
            {
                message = msg.getContentRaw();
                if (message.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit"))
                {
                    return null;
                }
                else
                {
                    return message;
                }
            }
        }
    }

    private TextChannel runChannelWizard(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
    {
        embed.setDescription("Please mention the channel to send the reminder in.\nE.g: "+msgEvent.getGuild().getDefaultChannel().getAsMention());
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        while (true)
        {
            Message msg = mm.getNextMessage(msgEvent.getChannel());
            if (msg.getMember().equals(msgEvent.getMember()))
            {
                if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit"))
                {
                    return null;
                }
                if (msg.getMentionedChannels().size() > 0)
                {
                    return msg.getMentionedChannels().get(0);
                }
                else
                {
                    embed.setDescription("Could not find any channels here. Please try again.\nTo cancel, use "+SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }

            }
        }
    }

    private Member runMemberWizard(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
    {
        embed.setDescription("Please mention the member to send the reminder to.\nE.g: "+msgEvent.getMember().getAsMention());
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        MessageManager mm = new MessageManager();
        while (true)
        {
            Message msg = mm.getNextMessage(msgEvent.getChannel());
            if (msg.getMember().equals(msgEvent.getMember()))
            {
                if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit"))
                {
                    return null;
                }
                if (msg.getMentionedMembers().size() > 0)
                {
                    return msg.getMentionedMembers().get(0);
                }
                else
                {
                    embed.setDescription("Could not find any members here. Please try again.\nTo cancel, use "+SettingsUtil.getGuildCommandPrefix(msgEvent.getGuild().getId()) + "quit.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }

            }
        }
    }
}
