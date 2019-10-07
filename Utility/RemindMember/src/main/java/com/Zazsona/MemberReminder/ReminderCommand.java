package com.Zazsona.MemberReminder;

import com.Zazsona.ReminderCore.Reminder;
import com.Zazsona.ReminderCore.ReminderManager;
import com.Zazsona.ReminderCore.enums.GroupType;
import com.Zazsona.ReminderCore.enums.RepetitionType;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
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
                ZonedDateTime executionTime = getExecutionTime(msgEvent.getGuild().getId(), rt, parameters);
                if (rt != RepetitionType.SINGLE || (rt == RepetitionType.SINGLE && executionTime.isAfter(ZonedDateTime.now(executionTime.getZone()))))
                {
                    String message = getMessage(msgEvent, embed);
                    if (message == null)
                    {
                        embed.setDescription("Reminder cancelled.");
                        msgEvent.getChannel().sendMessage(embed.build()).queue();
                        return;
                    }
                    TextChannel channel = null;
                    if (channelReminder)
                    {
                        channel = getChannel(msgEvent, embed);
                        if (channel == null)
                        {
                            embed.setDescription("Reminder cancelled.");
                            msgEvent.getChannel().sendMessage(embed.build()).queue();
                            return;
                        }
                    }
                    Member member = msgEvent.getMember();
                    if (memberReminder)
                    {
                        member = getMember(msgEvent, embed);
                        if (member == null)
                        {
                            embed.setDescription("Reminder cancelled.");
                            msgEvent.getChannel().sendMessage(embed.build()).queue();
                            return;
                        }
                    }
                    Reminder reminder = new Reminder(member.getUser().getId(), String.valueOf(msgEvent.getMessage().getCreationTime().toInstant().toEpochMilli()), msgEvent.getGuild().getId(), (channelReminder) ? channel.getId() : null, (channelReminder) ? GroupType.CHANNEL : GroupType.USER, rt, message, executionTime);
                    ReminderManager.addReminder(reminder);
                    embed.setDescription("**Reminder Set!**\n" +
                                                 "Repeat: "+ rt.name()+"\n" +
                                                 "Year: " + executionTime.getYear() + "\n" +
                                                 "Month: " + executionTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "\n" +
                                                 "Day: " + executionTime.getDayOfMonth() + " (" + executionTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ")\n" +
                                                 "Time: " + ((executionTime.getHour() < 10) ? "0"+executionTime.getHour() : executionTime.getHour()) + ":" + ((executionTime.getMinute() < 10) ? "0"+executionTime.getMinute() : executionTime.getMinute()) + ":" + ((executionTime.getSecond() < 10) ? "0"+executionTime.getSecond() : executionTime.getSecond()) + " (" + executionTime.getOffset().getId() + ")\n" +
                                                 "Message: " + reminder.getMessage());
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
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
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    private ZonedDateTime getExecutionTime(String guildId, RepetitionType rt, String... parameters)
    {
        ZonedDateTime firstExecutionTime = ZonedDateTime.now(SettingsUtil.getGuildSettings(guildId).getTimeZoneId());
        ZonedDateTime currentTime = ZonedDateTime.now(firstExecutionTime.getZone());
        boolean isYearSet = false;
        boolean isMonthSet = false;
        boolean isDaySet = false;
        boolean isTimeSet = false;
        int minArgLength = (rt == RepetitionType.SINGLE) ? 1 : 2;
        int maxArgLength = minArgLength+4;
        for (int i = maxArgLength-(parameters.length-minArgLength); i<maxArgLength; i++)
        {
            int index = parameters.length-maxArgLength+i;
            if (i == maxArgLength-1)
            {
                firstExecutionTime = getTime(parameters[index], isDaySet, firstExecutionTime, currentTime);
                isTimeSet = true;
            }
            else if (i == maxArgLength-2)
            {
                firstExecutionTime = parseDay(parameters[index], isMonthSet, isYearSet, firstExecutionTime, currentTime);
                isDaySet = true;
            }
            else if (i == maxArgLength-3)
            {
                firstExecutionTime = getMonth(parameters[index], isYearSet, firstExecutionTime, currentTime);
                isMonthSet = true;
            }
            else if (i == maxArgLength-4)
            {
                firstExecutionTime = getYear(parameters[index], firstExecutionTime);
                isYearSet = true;
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
        return RepetitionType.SINGLE;
    }

    private ZonedDateTime getYear(String yearInput, ZonedDateTime zdt) throws NumberFormatException
    {
        int year;
        if (yearInput.matches("[0-9]+"))
             year = Integer.parseInt(yearInput);
        else
            throw new NumberFormatException(yearInput+" is not a valid year.\nYears must be a number.");

        if (year > 2000 && year < Integer.MAX_VALUE)
        {
            return zdt.withYear(year);
        }
        else
        {
            throw new NumberFormatException(yearInput+" is not a valid year.\nYears must be a number.");
        }
    }

    private ZonedDateTime getMonth(String monthInput, boolean isYearSet, ZonedDateTime zdt, ZonedDateTime currentTime) throws NumberFormatException
    {
        int month;
        if (monthInput.matches("[0-9]+"))
            month = Integer.parseInt(monthInput);
        else
            month = getMonthNameValue(monthInput);

        if (month > 0 && month < 13)
        {
            if (!isYearSet && month < currentTime.getMonthValue())
            {
                return zdt.withMonth(month).plusYears(1);
            }
            else
            {
                return zdt.withMonth(month);
            }
        }
        else
        {
            throw new NumberFormatException("Unknown month: "+monthInput+".\nThere are only 12 months in a year!");
        }
    }

    private ZonedDateTime parseDay(String dayInput, boolean isMonthSet, boolean isYearSet, ZonedDateTime zdt, ZonedDateTime currentTime)
    {
        dayInput = dayInput.toLowerCase();
        if (dayInput.endsWith("st") || dayInput.endsWith("nd") || dayInput.endsWith("rd") || dayInput.endsWith("th"))
        {
            dayInput = dayInput.substring(0, dayInput.length()-2);
        }
        if (dayInput.matches("[0-9]+"))
        {
            return getDay(dayInput, isMonthSet, isYearSet, zdt, currentTime);
        }
        else
        {
            return getWeekDay(dayInput, isMonthSet, isYearSet, zdt);
        }
    }

    //Run after month and year
    private ZonedDateTime getDay(String dayInput, boolean isMonthSet, boolean isYearSet, ZonedDateTime zdt, ZonedDateTime currentTime) throws NumberFormatException
    {
        int day = 0;
        int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (isLeapYear(zdt))
            daysInMonths[1]++;
        if (dayInput.matches("[0-9]+"))
            day = Integer.parseInt(dayInput);

        if (day > 0)
        {
            if (isMonthSet && day > daysInMonths[zdt.getMonthValue()-1])
            {
                throw new NumberFormatException("This month doesn't have "+day+ " days!");
            }
            else
            {
                ZonedDateTime monthZDT;
                int max = (isYearSet && zdt.getYear() == ZonedDateTime.now(zdt.getZone()).getYear()) ? 12-zdt.getMonthValue() : 12;
                int min = (!isMonthSet && !isYearSet && day < currentTime.getDayOfMonth()) ? 1 : 0;
                boolean dayFound = false;
                for (int i = min; i<max; i++)
                {
                    monthZDT = zdt.plusMonths(i);
                    if (daysInMonths[monthZDT.getMonthValue()-1] >= day)
                    {
                        dayFound = true;
                        zdt = monthZDT;
                        break;
                    }
                }
                if (dayFound)
                    return zdt.withDayOfMonth(day);
                else
                    throw new NumberFormatException("Unable to find a month with "+day+" days.");

            }
        }
        else
        {
            throw new NumberFormatException(day+" is not a recognised day of the month.\nDay of month must be positive.");
        }
    }

    private ZonedDateTime getWeekDay(String weekDayInput, boolean isMonthSet, boolean isYearSet, ZonedDateTime zdt) throws IllegalArgumentException
    {
        int yearValue = zdt.getYear();
        int monthValue = zdt.getMonthValue();
        int weekDayValue = getWeekdayNameValue(weekDayInput);
        if (weekDayValue == 0)
            throw new IllegalArgumentException(weekDayInput+" is not a valid weekday.\nMonday-Sunday is expected.");


        for (int i = 0; i<7; i++)
        {
            ZonedDateTime weekLongZDT = zdt.plusDays(i);
            if (weekLongZDT.getDayOfWeek().getValue() == weekDayValue)
            {
                if ((isYearSet && weekLongZDT.getYear() == yearValue) || !isYearSet)
                {
                    if (isMonthSet && weekLongZDT.getMonthValue() == monthValue || !isMonthSet)
                    if (isMonthSet && weekLongZDT.getMonthValue() == monthValue || !isMonthSet)
                    {
                        return weekLongZDT;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Unable to find a "+weekDayInput+" within the year/month specified.");
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

    private int getMonthNameValue(String monthName)
    {
        switch (monthName.toUpperCase())
        {
            case "JANUARY":
            case "JAN":
                return 1;
            case "FEBRUARY":
            case "FEB":
                return 2;
            case "MARCH":
            case "MAR":
                return 3;
            case "APRIL":
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUNE":
            case "JUN":
                return 6;
            case "JULY":
            case "JUL":
                return 7;
            case "AUGUST":
            case "AUG":
                return 8;
            case "SEPTEMBER":
            case "SEP":
            case "SEPT":
                return 9;
            case "OCTOBER":
            case "OCT":
                return 10;
            case "NOVEMBER":
            case "NOV":
                return 11;
            case "DECEMBER":
            case "DEC":
                return 12;
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

    private ZonedDateTime getTime(String timeInput, boolean isDaySet, ZonedDateTime zdt, ZonedDateTime currentTime) throws NumberFormatException
    {
        timeInput = timeInput.toLowerCase();
        String[] splitInput = timeInput.replace("am", "").replace("pm", "").split(":");
        int[] timeValues = new int[splitInput.length];
        for (int i = 0; i<splitInput.length; i++)
        {
            if (splitInput[i].matches("[0-9]+"))
                timeValues[i] = Integer.parseInt(splitInput[i]);
            else
                throw new NumberFormatException(timeInput+" is not a valid clock.");
        }
        if (timeValues.length == 0)
            throw new NumberFormatException(timeInput+" is not a valid clock.");

        if (timeInput.contains("am") || timeInput.contains("pm"))
        {
            zdt = parseTwelveHourClock(timeValues, timeInput.contains("am"), zdt);
        }
        else
        {
            zdt = parseTwentyFourHourClock(timeValues, zdt);
        }
        if (!isDaySet && zdt.isBefore(currentTime))
            zdt = zdt.plusDays(1);
        return zdt;
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
                    throw new NumberFormatException("12 Hour clocks must have an hour value between 1 and 12!");
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

    private String getMessage(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
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

    private TextChannel getChannel(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
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

    private Member getMember(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed)
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
