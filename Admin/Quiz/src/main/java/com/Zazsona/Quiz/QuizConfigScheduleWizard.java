package com.Zazsona.Quiz;

import com.Zazsona.Quiz.json.DayJson;
import com.Zazsona.Quiz.json.GuildQuizConfig;
import com.Zazsona.Quiz.system.QuizScheduler;
import com.Zazsona.Quiz.system.SettingsManager;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Locale;

public class QuizConfigScheduleWizard
{
    private MessageManager mm = new MessageManager();
    private GuildQuizConfig gqc;

    public void run(GuildMessageReceivedEvent msgEvent, TextChannel textChannel, GuildSettings guildSettings, GuildQuizConfig gqc, EmbedBuilder embed) throws IOException
    {
        this.gqc = gqc;
        while (true)
        {
            embed.setDescription("Configure quiz scheduling. Use this to run quizzes regularly in your server.\n\n**List**\n**Add** [Day] [Time]\n**Remove** [Day] [Time]\n**Quit**");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
            Message input = getInput(msgEvent, textChannel);
            if (input == null)
            {
                return;
            }
            else if (input.getContentDisplay().equalsIgnoreCase("list"))
            {
                listQuizzes(msgEvent, guildSettings, embed);
            }
            else if (input.getContentDisplay().toLowerCase().startsWith("add") || input.getContentDisplay().toLowerCase().startsWith("remove"))
            {
                try
                {
                    boolean add = input.getContentDisplay().toLowerCase().startsWith("add");
                    String[] elements = input.getContentDisplay().split(" ");
                    ZonedDateTime zdt = ZonedDateTime.now(guildSettings.getTimeZoneId());
                    if (elements.length > 2)
                    {
                        zdt = getWeekDay(elements[1], zdt);
                        zdt = getTime(elements[2], zdt);
                        if (add)
                            addScheduledQuiz(gqc, embed, zdt);
                        else
                            deleteScheduledQuiz(gqc, embed, zdt);
                    }
                    else if (elements.length > 1)
                    {
                        zdt = getTime(elements[1], zdt);
                        if (add)
                            addScheduledQuiz(gqc, embed, zdt);
                        else
                            deleteScheduledQuiz(gqc, embed, zdt);
                    }
                    else
                    {
                        embed.setDescription("No day/time specified.");
                    }
                }
                catch (IllegalArgumentException e)
                {
                    embed.setDescription(e.getMessage());

                }
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
    }

    private void addScheduledQuiz(GuildQuizConfig gqc, EmbedBuilder embed, ZonedDateTime zdt)
    {
        ZonedDateTime utc;
        utc = ZonedDateTime.ofInstant(Instant.ofEpochSecond(zdt.toEpochSecond()), ZoneOffset.UTC);
        long startOfDay = utc.withHour(0).withMinute(0).withSecond(0).toEpochSecond();
        long quizStart = zdt.toEpochSecond()-startOfDay;
        gqc.getDay(utc.getDayOfWeek().getValue()).addStartSecond(quizStart);
        QuizScheduler.tryQueueQuizForCurrentExecution(gqc);
        SettingsManager.getInstance().updateGuildConfig(gqc);
        ZonedDateTime currentTimeUTC = ZonedDateTime.now(ZoneOffset.UTC);
        if (zdt.isAfter(currentTimeUTC.minusMinutes(Long.valueOf(5))) && zdt.isBefore(currentTimeUTC.plusMinutes(Long.valueOf(5))))
        {
            embed.setDescription("Weekly Quiz Added.\n" + zdt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s - " + getFormattedTime(zdt.getHour(), zdt.getMinute(), zdt.getSecond()) +
                    "\n\nNote: As this is in less than five minutes, it will not run today.\nTo run quizzes instantly, use "+SettingsUtil.getGuildCommandPrefix(gqc.getGuildID())+"Quiz Start");
        }
        else
        {
            embed.setDescription("Weekly Quiz Added.\n" + zdt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s - " + getFormattedTime(zdt.getHour(), zdt.getMinute(), zdt.getSecond()));
        }

    }

    private void deleteScheduledQuiz(GuildQuizConfig gqc, EmbedBuilder embed, ZonedDateTime zdt)
    {
        ZonedDateTime utc;
        utc = ZonedDateTime.ofInstant(Instant.ofEpochSecond(zdt.toEpochSecond()), ZoneOffset.UTC);
        long startOfDay = utc.withHour(0).withMinute(0).withSecond(0).toEpochSecond();
        long quizStart = zdt.toEpochSecond()-startOfDay;
        if (gqc.getDay(utc.getDayOfWeek().getValue()).isStartSecond(quizStart))
        {
            gqc.getDay(utc.getDayOfWeek().getValue()).removeStartSecond(quizStart);
            SettingsManager.getInstance().updateGuildConfig(gqc);
            QuizScheduler.removeQuizFromQueue(gqc.getGuildID(), quizStart);
            embed.setDescription("Weekly Quiz Removed.\n" + zdt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "s - " + getFormattedTime(zdt.getHour(), zdt.getMinute(), zdt.getSecond()));
        }
        else
        {
            embed.setDescription("No quiz was scheduled at this time.");
        }
    }

    public void parseAsParameters(GuildMessageReceivedEvent msgEvent, Collection<String> collection, TextChannel textChannel, GuildQuizConfig gqc, EmbedBuilder embed) throws IOException
    {

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

    private void listQuizzes(GuildMessageReceivedEvent msgEvent, GuildSettings guildSettings, EmbedBuilder embed)
    {
        EmbedBuilder listEmbed = new EmbedBuilder(embed);
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC).withSecond(0).withMinute(0).withHour(0);
        long secondOfTodayStart = utc.toEpochSecond();
        int utcDayValue = utc.getDayOfWeek().getValue();
        long secondOfWeekStart = (secondOfTodayStart-((60*60*24)*(utcDayValue-1)));

        StringBuilder[] fieldBuilders = new StringBuilder[7];
        for (int i = 0; i<fieldBuilders.length; i++)
        {
            fieldBuilders[i] = new StringBuilder();
        }
        for (int i = 1; i<8; i++)
        {
            DayJson day = gqc.getDay(i);
            for (Long startTime : day.getStartSeconds())
            {
                long epochStartTime = startTime+secondOfWeekStart+(60*60*24*(i-1));
                ZonedDateTime localStartTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochStartTime), guildSettings.getTimeZoneId());
                fieldBuilders[localStartTime.getDayOfWeek().getValue()-1].append(getFormattedTime(localStartTime.getHour(), localStartTime.getMinute(), localStartTime.getSecond())).append("\n");
            }
        }
        for (int i = 0; i<fieldBuilders.length; i++) //This is done after as we can loop back around. That is, we could start on a Sunday afternoon, and end on a Sunday morning, so we need to traverse all quizzes before building output for a day.
        {
            if (fieldBuilders[i].length() > 0)
            {
                listEmbed.addField(getDayOfWeekName(i+1), fieldBuilders[i].toString(), true);
            }
            else
            {
                listEmbed.addField(getDayOfWeekName(i+1), "No quizzes.", true);
            }

        }
        listEmbed.setDescription("Quizzes scheduled for the week.");
        msgEvent.getChannel().sendMessage(listEmbed.build()).queue();
    }

    private String getDayOfWeekName(int dayValue)
    {
        switch (dayValue)
        {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "Unknown Day";
        }
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


    private ZonedDateTime getTime(String timeInput, ZonedDateTime zdt) throws NumberFormatException
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

    private ZonedDateTime getWeekDay(String weekDayInput, ZonedDateTime zdt) throws IllegalArgumentException
    {
        int weekDayValue = getWeekdayNameValue(weekDayInput);
        if (weekDayValue == 0)
            throw new IllegalArgumentException(weekDayInput+" is not a valid weekday.\nMonday-Sunday is expected.");


        for (int i = 0; i<7; i++)
        {
            ZonedDateTime weekLongZDT = zdt.plusDays(i);
            if (weekLongZDT.getDayOfWeek().getValue() == weekDayValue)
            {
                return weekLongZDT;
            }
        }
        throw new IllegalArgumentException("Unable to find a "+weekDayInput+" within the year/month specified.");
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

    private String getFormattedTime(int hour, int minute, int second)
    {
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(((hour < 10) ? "0"+hour : hour)).append(":");
        timeBuilder.append(((minute < 10) ? "0"+minute : minute)).append(":");
        timeBuilder.append(((second < 10) ? "0"+second : second));
        return timeBuilder.toString();
    }
}
