package com.Zazsona.Reminder;

import com.Zazsona.ReminderCore.Reminder;
import com.Zazsona.ReminderCore.ReminderManager;
import com.Zazsona.ReminderCore.enums.GroupType;
import com.Zazsona.ReminderCore.enums.RepetitionType;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class ReminderCommand extends Command
{

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setFooter("Reminders", null);
        embed.setTitle("New Reminder");
        try
        {
            RepetitionType rt = getRepetitionType(parameters[1]);
            ZonedDateTime executionTime = getExecutionTime(msgEvent.getGuild().getId(), rt, parameters);
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
                        return;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            Reminder reminder = new Reminder(msgEvent.getMember().getUser().getId(), String.valueOf(msgEvent.getMessage().getCreationTime().toInstant().toEpochMilli()), msgEvent.getGuild().getId(), null, GroupType.USER, rt, message, executionTime);
            ReminderManager.addReminder(reminder);
            embed.setDescription("**Reminder Set!**\nYear: " + executionTime.getYear() + "\nMonth: " + executionTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "\nDay: " + executionTime.getDayOfMonth() + " (" + executionTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ")\n Time:" + executionTime.getHour() + ":" + executionTime.getMinute() + ":" + executionTime.getSecond() + " (" + executionTime.getOffset().getId() + ")\nMessage: " + reminder.getMessage());
            msgEvent.getChannel().sendMessage(embed.build()).queue();
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

    private ZonedDateTime getExecutionTime(String guildId, RepetitionType rt, String... parameters)
    {
        ZonedDateTime firstExecutionTime = ZonedDateTime.now(SettingsUtil.getGuildSettings(guildId).getTimeZoneId());
        int minArgLength = (rt == null) ? 0 : 1;
        for (int i = parameters.length-1; i>minArgLength; i--)
        {
            if (i == parameters.length-1)
            {
                firstExecutionTime = getTime(parameters[i], firstExecutionTime);
            }
            else if (i ==  parameters.length-2)
            {
                firstExecutionTime = (rt == RepetitionType.WEEKLY) ? getWeekDay(parameters[i], firstExecutionTime) : getDay(parameters[i], rt == RepetitionType.MONTHLY, firstExecutionTime);
            }
            else if (i ==  parameters.length-3)
            {
                firstExecutionTime = getMonth(parameters[i], firstExecutionTime);
            }
            else if (i ==  parameters.length-4)
            {
                firstExecutionTime = getYear(parameters[i], firstExecutionTime);
            }
        }
        return firstExecutionTime;
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
        return null;
    }

    private ZonedDateTime getYear(String yearInput, ZonedDateTime zdt) throws NumberFormatException
    {
        int year = Integer.parseInt(yearInput);
        if (year > 2000 && year < Integer.MAX_VALUE)
        {
            return zdt.withYear(year);
        }
        else
        {
            throw new NumberFormatException("Invalid year.");
        }
    }

    private ZonedDateTime getMonth(String monthInput, ZonedDateTime zdt) throws NumberFormatException
    {
        int month = Integer.parseInt(monthInput);
        if (month > 0 && month < 13)
        {
            return zdt.withMonth(month);
        }
        else
        {
            throw new NumberFormatException("There are only 12 months in a year!");
        }
    }

    //Run after month and year
    private ZonedDateTime getDay(String dayInput, boolean isMonthly, ZonedDateTime zdt) throws NumberFormatException
    {
        int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (isLeapYear(zdt)) daysInMonths[2]++;
        int day = Integer.parseInt(dayInput);
        if (day > 0)
        {
            if (!isMonthly && day > daysInMonths[zdt.getMonthValue()-1])
            {
                throw new NumberFormatException("This month doesn't have "+day+ "days!");
            }
            else
            {
                return zdt.withDayOfMonth(day);
            }
        }
        else
        {
            throw new NumberFormatException("Day of month must be positive.");
        }
    }

    private ZonedDateTime getWeekDay(String weekDayInput, ZonedDateTime zdt) throws IllegalArgumentException
    {
        int weekDayValue = getWeekdayNameValue(weekDayInput);
        if (weekDayValue == 0)
            throw new IllegalArgumentException("Invalid day name.");


        for (int i = 0; i<7; i++)
        {
            ZonedDateTime weekLongZDT = zdt.plusDays(i);
            if (weekLongZDT.getDayOfWeek().getValue() == weekDayValue)
            {
                return weekLongZDT;
            }
        }
        throw new IllegalArgumentException("Invalid weekday.");
    }

    private int getWeekdayNameValue(String weekDayName)
    {
        switch (weekDayName.toUpperCase())
        {
            case "MONDAY":
                return 1;
            case "TUESDAY":
                return 2;
            case "WEDNESDAY":
                return 3;
            case "THURSDAY":
                return 4;
            case "FRIDAY":
                return 5;
            case "SATURDAY":
                return 6;
            case "SUNDAY":
                return 7;
        }
        return 0;
    }

    public static boolean isLeapYear(ZonedDateTime zdt)
    {
        int year = zdt.getYear();
        if (year % 4 != 0)
        {
            return false;
        }
        else if (year % 400 == 0)
        {
            return true;
        }
        else if (year % 100 == 0)
        {
            return false;
        }
        return true;
    }

    private ZonedDateTime getTime(String timeInput, ZonedDateTime zdt) throws NumberFormatException
    {
        timeInput = timeInput.toLowerCase();
        String[] splitInput = timeInput.replace("am", "").replace("pm", "").split(":");
        int[] timeValues = new int[splitInput.length];
        for (int i = 0; i<splitInput.length; i++)
        {
            timeValues[i] = Integer.parseInt(splitInput[i]);
        }
        if (timeValues.length == 0)
            throw new NumberFormatException("This time has no values.");

        if (timeInput.contains("am") || timeInput.contains("pm"))
        {
            return parseTwelveHourClock(timeValues, timeInput.contains("am"), zdt);
        }
        else
        {
            return parseTwentyFourHourClock(timeValues, zdt);
        }
    }

    private ZonedDateTime parseTwelveHourClock(int[] timeValues, boolean isAM, ZonedDateTime zdt) throws NumberFormatException
    {
        int hour = 0;
        int minute = 0;
        int second = 0;
        for (int i = 0; i<timeValues.length; i++)
        {
            if (i == 0) //Hour
            {
                if (timeValues[0] > 0 && timeValues[0] <= 12)
                {
                    hour = timeValues[0];
                    hour = (isAM) ? hour : hour+12;
                    hour = (hour >= 24 && !isAM) ? 12 : hour;         //Convert to 24/h clock
                    hour = (hour == 12 && isAM) ? 0 : hour;
                }
                else
                {
                    throw new NumberFormatException("12 Hour clock must have an hour value between 1 and 12!");
                }
            }
            else if (i == 1) //Minute
            {
                if (timeValues[1] >= 0 && timeValues[0] <= 59)
                {
                    minute = timeValues[1];
                }
                else
                {
                    throw new NumberFormatException("Minutes must be between 0 and 59!");
                }
            }
            else if (i == 2)
            {
                if (timeValues[2] >= 0 && timeValues[2] <= 59)
                {
                    second = timeValues[2];
                }
                else
                {
                    throw new NumberFormatException("Seconds must be between 0 and 59!");
                }
            }
        }
        return zdt.withHour(hour).withMinute(minute).withSecond(second);
    }

    private ZonedDateTime parseTwentyFourHourClock(int[] timeValues, ZonedDateTime zdt) throws NumberFormatException
    {
        int hour = 0;
        int minute = 0;
        int second = 0;
        for (int i = 0; i<timeValues.length; i++)
        {
            switch (i)
            {
                case 0:
                    if (timeValues[0] >= 0 && timeValues[0] <= 23)
                    {
                        hour = timeValues[0];
                    }
                    else
                    {
                        throw new NumberFormatException("24-Hour clock means hours must be between 0 and 23!");
                    }
                    break;
                case 1:
                    if (timeValues[1] >= 0 && timeValues[1] <= 59)
                    {
                        minute = timeValues[1];
                    }
                    else
                    {
                        throw new NumberFormatException("Minutes must be between 0 and 59!");
                    }
                    break;
                case 2:
                    if (timeValues[2] >= 0 && timeValues[2] <= 59)
                    {
                        second = timeValues[2];
                    }
                    else
                    {
                        throw new NumberFormatException("Seconds must be between 0 and 59!");
                    }
                    break;
            }
        }
        return zdt.withHour(hour).withMinute(minute).withSecond(second);
    }
}
