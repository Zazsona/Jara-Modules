package com.Zazsona.time;

import commands.CmdUtil;
import configuration.SettingsUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Time extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            ZonedDateTime time;
            if (parameters.length <= 1)
                time = ZonedDateTime.now(SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).getTimeZoneId());
            else
                time = ZonedDateTime.now(ZoneId.of(parameters[1]));

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("==== Timezone ====");
            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append("**ID:** UTC (").append(time.getOffset()).append(")\n");
            descBuilder.append("**Time:** ").append(getTime(time)).append("\n");
            descBuilder.append("**Date:** ").append(time.getYear()).append("/").append(time.getMonthValue()).append("/").append(time.getDayOfMonth()).append("\n");
            descBuilder.append("**Weekday:** ").append(getDayOfWeekPretty(time.getDayOfWeek().getValue()));
            embed.setDescription(descBuilder.toString());
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        catch (DateTimeException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("==== Timezone ====");
            embed.setDescription("Invalid timezone.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }

    }

    private String getTime(ZonedDateTime zdt)
    {
        StringBuilder clockBuilder = new StringBuilder();
        String hour = (zdt.getHour() < 10) ? "0"+zdt.getHour() : String.valueOf(zdt.getHour());
        String minute = (zdt.getMinute() < 10) ? "0"+zdt.getMinute() : String.valueOf(zdt.getMinute());

        int ampmHour = (zdt.getHour() < 12) ? zdt.getHour() : zdt.getHour()-12;
        ampmHour = (ampmHour == 0) ? 12 : ampmHour;
        String ampm = (zdt.getHour() < 12) ? "AM" : "PM";

        clockBuilder.append(hour).append(":").append(minute).append(" (").append(ampmHour).append(":").append(minute).append(ampm).append(")");
        return clockBuilder.toString();
    }

    private String getDayOfWeekPretty(int dayOfWeek)
    {
        switch (dayOfWeek)
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
        }
        return "";
    }

}
