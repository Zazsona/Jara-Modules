package com.Zazsona.QuizNight;

import com.Zazsona.QuizNight.json.QuizSettings;
import com.Zazsona.QuizNight.quiz.Quiz;
import com.Zazsona.QuizNight.system.SettingsManager;
import module.Command;
import commands.CmdUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Start extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            String operation = parameters[1].toLowerCase().trim();
            if (operation.equals("start"))
            {
                Quiz qn = new Quiz();
                qn.startQuiz(msgEvent.getGuild(), true);
            }
            else if (operation.equals("days"))
            {
                updateGuildDays(msgEvent.getGuild().getIdLong(), msgEvent.getChannel(), parameters);
            }
            else if (operation.equals("time"))
            {
                updateQuizTime(msgEvent.getGuild().getIdLong(), msgEvent.getChannel(), parameters);
            }
            else if (operation.equals("roles"))
            {
                updateAllowedRoles(msgEvent.getGuild().getIdLong(), msgEvent.getChannel(), parameters);
            }
            else if (operation.equals("ping"))
            {
                updatePingAnnouncement(msgEvent.getGuild().getIdLong(), msgEvent.getChannel(), parameters);
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        catch (ArrayIndexOutOfBoundsException | InvalidParameterException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    /**
     * Toggles whether to send an @everyone ping when a quiz is starting.
     * @param guildId the guild of the quiz
     * @param channel the current channel
     * @param parameters the parameters
     */
    private void updatePingAnnouncement(long guildId, TextChannel channel, String... parameters) throws InvalidParameterException
    {
        if (parameters.length >= 3)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            if (parameters[2].equalsIgnoreCase("disable"))
            {
                SettingsManager.updateAnnouncementPing(guildId, false);
                embed.setDescription("Quiz pings are now disabled. Members will no longer receive a ping notice when a quiz is about to start.");
            }
            else if (parameters[2].equalsIgnoreCase("enable"))
            {
                SettingsManager.updateAnnouncementPing(guildId, true);
                embed.setDescription("Quiz pings have now been enabled.\nA ping to the everyone role will be sent out at the 5 minute notice.");
            }
            else
            {
                embed.setDescription("Please specify whether you would like to enable or disable pings.\nE.g: QuizNight Ping Enable");
            }
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            throw new InvalidParameterException();
        }
    }
    /**
     * Modifies the days at which quizzes will run in this guild
     * @param channel the current channel
     * @param parameters the parameters
     */
    private void updateGuildDays(long guildID, TextChannel channel, String... parameters)
    {
        boolean changeOccured = false;

        for (String param : parameters)
        {
            String dayOfWeek = param.toLowerCase();
            switch (dayOfWeek)
            {
                case "monday":
                    SettingsManager.setGuildQuizDay(guildID, 1, !SettingsManager.isGuildQuizDay(guildID, 1));
                    changeOccured = true;
                    break;
                case "tuesday":
                    SettingsManager.setGuildQuizDay(guildID, 2, !SettingsManager.isGuildQuizDay(guildID, 2));
                    changeOccured = true;
                    break;
                case "wednesday":
                    SettingsManager.setGuildQuizDay(guildID, 3, !SettingsManager.isGuildQuizDay(guildID, 3));
                    changeOccured = true;
                    break;
                case "thursday":
                    SettingsManager.setGuildQuizDay(guildID, 4, !SettingsManager.isGuildQuizDay(guildID, 4));
                    changeOccured = true;
                    break;
                case "friday":
                    SettingsManager.setGuildQuizDay(guildID, 5, !SettingsManager.isGuildQuizDay(guildID, 5));
                    changeOccured = true;
                    break;
                case "saturday":
                    SettingsManager.setGuildQuizDay(guildID, 6, !SettingsManager.isGuildQuizDay(guildID, 6));
                    changeOccured = true;
                    break;
                case "sunday":
                    SettingsManager.setGuildQuizDay(guildID, 7, !SettingsManager.isGuildQuizDay(guildID, 7));
                    changeOccured = true;
                    break;
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embed.addField("Monday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 1))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Tuesday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 2))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Wednesday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 3))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Thursday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 4))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Friday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 5))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Saturday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 6))
                .replace("true", "Yes").replace("false", "No"), false);
        embed.addField("Sunday", String.valueOf(SettingsManager.isGuildQuizDay(guildID, 7))
                .replace("true", "Yes").replace("false", "No") , false);
        if (changeOccured)
        {
            embed.setDescription("Updated quiz days.\n\nCurrent settings:");
            channel.sendMessage(embed.build()).queue();
        }
        else
        {
            embed.setDescription("**Could not find any valid days.**\n\nCurrent settings:");
            channel.sendMessage(embed.build()).queue();
        }
    }

    /**
     * Modifies the time at which the quizzes run in this guild
     * @param guildID the guild quiz config
     * @param channel the current channel
     * @param parameters the parameters
     * @throws InvalidParameterException provided parameters were useless.
     */
    private void updateQuizTime(long guildID, TextChannel channel, String... parameters) throws InvalidParameterException
    {
        boolean changeOccured = false;
        int hour = 0;
        int minute = 0;
        for (String param : parameters)
        {
            if (param.contains(":"))
            {
                String time = param.toLowerCase().replace("am", "").replace("pm", "");
                String[] clock = time.split(":");
                hour = Integer.parseInt(clock[0]);
                if (clock.length > 1)
                    minute = Integer.parseInt(clock[1]);

                if (param.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-24 : hour;
                }
                else if (param.toLowerCase().contains("am"))
                {
                    hour = (hour >= 12) ? hour-12 : hour;
                }
                changeOccured = true;
            }
            else if (param.toLowerCase().matches("[0-9]+") || param.toLowerCase().matches("[0-9]+am") || param.toLowerCase().matches("[0-9]+pm"))
            {
                hour = Integer.parseInt(param.toLowerCase().replace("pm", "").replace("am", ""));
                minute = 0;
                if (param.toLowerCase().contains("pm"))
                {
                    hour += 12;
                    hour = (hour >= 24) ? hour-12 : hour;
                }
                else if (param.toLowerCase().contains("am"))
                {
                    hour = (hour >= 12) ? hour-12 : hour;
                }
                changeOccured = true;
            }
            else if (param.equalsIgnoreCase("pm")) //This allows for both 12:00PM and 12:00 PM.
            {
                hour += 12;
                hour = (hour >= 24) ? hour-24 : hour;
            }
            else if (param.equalsIgnoreCase("am")) //This allows for both 12:00PM and 12:00 PM.
            {
                hour = (hour >= 12) ? hour-12 : hour;
            }
        }
        if (changeOccured)
        {
            int startMinute = (hour*60)+minute;
            SettingsManager.setGuildQuizTime(guildID, startMinute);
            String strHour = (hour < 10) ? "0"+hour : ""+hour;
            String strMinute = (minute < 10) ? "0"+minute : ""+minute;
            channel.sendMessage("Quiz time set to "+strHour+":"+strMinute+" (UTC)").queue();
        }
        else
        {
            throw new InvalidParameterException();
        }
    }

    /**
     * Modifies the roles that can join quizzes
     * @param guildID the guild quiz config
     * @param channel the current channel
     * @param parameters role names
     */
    private void updateAllowedRoles(long guildID, TextChannel channel, String... parameters)
    {
        Guild guild = channel.getGuild();
        ArrayList<String> roleIDs = new ArrayList<>();
        for (String roleName : parameters)
        {
            Role role = null;
            if (roleName.equalsIgnoreCase("everyone"))
            {
                role = guild.getPublicRole();
            }
            else
            {
                List<Role> roles = guild.getRolesByName(roleName, true);
                if (roles != null && roles.size() > 0)
                {
                    role = roles.get(0);
                }
            }
            if (role != null)
            {
                roleIDs.add(role.getId());
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        StringBuilder descBuilder = new StringBuilder();
        QuizSettings.GuildQuizConfig gqc;
        if (roleIDs.size() > 0)
        {
            gqc = SettingsManager.toggleAllowedRoles(guildID, roleIDs.toArray(new String[0]));
            descBuilder.append("Roles update successfully.\n\nRoles allowed to join quizzes:\n");
        }
        else
        {
            gqc = SettingsManager.getGuildQuizSettings(guildID);
            descBuilder.append("Could not identify any roles.\n\nRoles allowed to join quizzes:\n");
        }
        if (gqc.AllowedRoles.length > 0)
        {
            for (String roleID : gqc.AllowedRoles)
            {
                descBuilder.append(guild.getRoleById(roleID).getName()).append("\n");
            }
        }
        else
        {
            descBuilder.append("everyone\n");
        }
        embed.setDescription(descBuilder.toString());
        channel.sendMessage(embed.build()).queue();
    }

}
