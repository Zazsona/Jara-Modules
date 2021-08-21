package com.zazsona.reminder;

import com.Zazsona.ReminderCore.Reminder;
import com.Zazsona.ReminderCore.ReminderManager;
import com.Zazsona.ReminderCore.enums.GroupType;
import com.Zazsona.ReminderCore.enums.RepetitionType;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class ReminderCommand extends ModuleCommand
{

    private int lastIndex = 0;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            boolean channelReminder = (parameters[0].toLowerCase().contains("group") || parameters[0].toLowerCase().contains("channel") || parameters[0].toLowerCase().contains("server") || parameters[0].toLowerCase().contains("us") || parameters[0].toLowerCase().contains("all"));
            boolean memberReminder = (parameters[0].toLowerCase().contains("member") || parameters[0].toLowerCase().contains("user"));
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setFooter("Reminders", null);
            embed.setTitle("New Reminder");
            try
            {
                RepetitionType rt = getRepetitionType(parameters[1]);
                ZonedDateTime executionTime = new TimeParser(this).getExecutionTime(msgEvent.getGuild().getId(), rt, parameters);
                if (rt != RepetitionType.SINGLE || (rt == RepetitionType.SINGLE && executionTime.isAfter(ZonedDateTime.now(executionTime.getZone()))))
                {
                    buildReminder(msgEvent, channelReminder, memberReminder, embed, rt, executionTime, parameters);
                }
                else
                {
                    embed.setDescription("You can't set a single reminder for the past!");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
            }
            catch (IllegalArgumentException e)
            {
                embed.setDescription(e.getMessage());
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            catch (IOException e)
            {
                embed.setDescription("An error occurred while saving reminder.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
                LoggerFactory.getLogger(getClass()).error(e.toString());
            }
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }

    private void createReminder(GuildMessageReceivedEvent msgEvent, boolean channelReminder, EmbedBuilder embed, RepetitionType rt, ZonedDateTime executionTime, String message, TextChannel channel, Member member) throws IOException
    {
        Reminder reminder = new Reminder(member.getUser().getId(), String.valueOf(msgEvent.getMessage().getTimeCreated().toInstant().toEpochMilli()), msgEvent.getGuild().getId(), (channelReminder) ? channel.getId() : null, (channelReminder) ? GroupType.CHANNEL : GroupType.USER, rt, message, executionTime);
        ReminderManager.addReminder(reminder);
        embed.setDescription("**Reminder Set!**\n" +
                                     "Repeat: "+ rt.name()+"\n" +
                                     "Year: " + executionTime.getYear() + "\n" +
                                     "Month: " + executionTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "\n" +
                                     "Day: " + executionTime.getDayOfMonth() + " (" + executionTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ")\n" +
                                     "Time: " + ((executionTime.getHour() < 10) ? "0"+executionTime.getHour() : executionTime.getHour()) + ":" + ((executionTime.getMinute() < 10) ? "0"+executionTime.getMinute() : executionTime.getMinute()) + ":" + ((executionTime.getSecond() < 10) ? "0"+executionTime.getSecond() : executionTime.getSecond()) + " (UTC " + executionTime.getOffset().getId() + ")\n" +
                                     "Message: " + reminder.getMessage());
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }


    private RepetitionType getRepetitionType(String repetitionInput)
    {
        for (RepetitionType repetitionType : RepetitionType.values())
        {
            if (repetitionType.name().equalsIgnoreCase(repetitionInput))
            {
                return repetitionType;
            }
        }
        return RepetitionType.SINGLE;
    }

    private boolean buildReminder(GuildMessageReceivedEvent msgEvent, boolean channelReminder, boolean memberReminder, EmbedBuilder embed, RepetitionType rt, ZonedDateTime executionTime, String[] parameters) throws IOException
    {
        ReminderCustomisation rc = new ReminderCustomisation(this);
        Message message = msgEvent.getMessage();
        TextChannel channel = null;
        Member member = msgEvent.getMember();
        String reminderContent = null;
        if (channelReminder)
        {
            channel = rc.getTextChannel(msgEvent, embed, message);
            if (channel == null) return false;
        }
        if (memberReminder)
        {
            member = rc.getMember(msgEvent, embed, message);
            if (member == null) return false;
        }
        reminderContent = rc.getMessage(msgEvent, embed, message);
        if (reminderContent == null) return false;

        createReminder(msgEvent, channelReminder, embed, rt, executionTime, reminderContent, channel, member);
        return true;
    }


    public void setLastIndex(int lastIndex)
    {
        this.lastIndex = lastIndex;
    }

    public int getLastIndex()
    {
        return lastIndex;
    }
}
