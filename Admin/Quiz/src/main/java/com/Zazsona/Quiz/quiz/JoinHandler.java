package com.Zazsona.Quiz.quiz;

import configuration.SettingsUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class JoinHandler extends ListenerAdapter
{
    private Quiz quiz;
    private Guild guild;
    private Category quizCategory;
    private ArrayList<QuizTeam> quizTeams;
    private ArrayList<String> rolesPermittedToJoin;

    public JoinHandler(Quiz quiz, Guild guild, Category quizCategory, ArrayList<QuizTeam> quizTeams, ArrayList<String> rolesPermittedToJoin)
    {
        this.quiz = quiz;
        this.guild = guild;
        this.quizCategory = quizCategory;
        this.quizTeams = quizTeams;
        this.rolesPermittedToJoin = rolesPermittedToJoin;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
    {
        if (event.getGuild().equals(guild) && event.getMessage().getContentDisplay().toLowerCase().startsWith(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"join"))
        {
            if (isPermittedToJoin(event.getMember()))
            {
                joinPlayerToTeam(event);
            }
        }
        else if (event.getGuild().equals(guild) && event.getMessage().getContentDisplay().toLowerCase().startsWith(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"quit"))
        {
            removePlayerFromTeam(event);
        }
    }

    private void removePlayerFromTeam(@Nonnull GuildMessageReceivedEvent event)
    {
        synchronized (quizTeams)
        {
            QuizTeam currentTeam = getPlayerTeam(event.getMember());
            if (currentTeam != null && event.getChannel().equals(currentTeam.getTeamChannel()))
            {
                if (!quiz.isStarted())
                {
                    currentTeam.removeTeamMember(event.getMember());
                    if (currentTeam.getTeamSize() == 0)
                    {
                        currentTeam.getTeamChannel().delete().queue();
                        quizTeams.remove(currentTeam);
                    }
                }
                else if (quiz.isStarted())
                {
                    currentTeam.removeMemberFromChannel(event.getMember());
                }
                denyGlobalChannelAccess(event.getMember());
            }
        }
    }

    private void joinPlayerToTeam(@Nonnull GuildMessageReceivedEvent event)
    {
        synchronized (quizTeams)
        {
            QuizTeam currentTeam = getPlayerTeam(event.getMember());
            if (!quiz.isStarted() && currentTeam != null) //Remove from current team if the quiz is still in countdown
            {
                currentTeam.removeTeamMember(event.getMember());
                if (currentTeam.getTeamSize() == 0)
                {
                    currentTeam.getTeamChannel().delete().queue();
                    quizTeams.remove(currentTeam);
                }
                currentTeam = null;
            }
            if (currentTeam == null) //Join team
            {
                String teamName = getTeamName(event.getMessage());
                QuizTeam existingTeamWithName = getTeamByName(teamName);
                if (existingTeamWithName == null)
                {
                    if (teamName.length() <= 40)
                    {
                        QuizTeam quizTeam = new QuizTeam(teamName, event.getMember(), quizCategory, quiz.getQuestionCount());
                        quizTeams.add(quizTeam);
                        allowGlobalChannelAccess(event.getMember());
                        if (quiz.isStarted())
                            quizTeam.getTeamChannel().sendMessage(Quiz.getQuizEmbedStyle(event.getGuild()).setDescription("Please wait for the next question...").build()).queue();
                    }
                    else
                    {
                        event.getChannel().sendMessage(Quiz.getQuizEmbedStyle(event.getGuild()).setDescription("Team names must be between 1 and 40 characters!").build()).queue();
                        return;
                    }
                }
                else
                {
                    existingTeamWithName.addTeamMember(event.getMember());
                    allowGlobalChannelAccess(event.getMember());
                }
            }
            else if (quiz.isStarted() && currentTeam != null) //If a quiz is started and they try to change teams, block it for fairness. If they're rejoining normally, allow it.
            {
                String teamName = getTeamName(event.getMessage());
                if (teamName.equalsIgnoreCase(currentTeam.getTeamName()) || event.getMessage().getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"join"))
                {
                    currentTeam.addMemberToChannel(event.getMember());
                    allowGlobalChannelAccess(event.getMember());
                }
                else
                    event.getChannel().sendMessage(Quiz.getQuizEmbedStyle(guild).setDescription("You can't change teams after the quiz has begun!").build()).queue();
            }
        }
    }

    private String getTeamName(Message message)
    {
        String messageContent = message.getContentDisplay();
        String[] messageTokens = messageContent.split(" ");
        if (messageTokens.length > 1)
        {
            StringBuilder teamNameBuilder = new StringBuilder();
            for (int i = 1; i<messageTokens.length; i++)
            {
                teamNameBuilder.append(messageTokens[i]).append(" ");
            }
            return teamNameBuilder.toString();
        }
        else
        {
            return message.getMember().getEffectiveName()+"s Team";
        }
    }

    private QuizTeam getPlayerTeam(Member member)
    {
        for (QuizTeam quizTeam : quizTeams)
        {
            if (quizTeam.isTeamMember(member))
                return quizTeam;
        }
        return null;
    }

    private QuizTeam getTeamByName(String teamName)
    {
        for (QuizTeam quizTeam : quizTeams)
        {
            if (quizTeam.getTeamName().equalsIgnoreCase(teamName))
                return quizTeam;
        }
        return null;
    }

    private boolean isPermittedToJoin(Member member)
    {
        if (rolesPermittedToJoin.size() == 0 || rolesPermittedToJoin.contains(guild.getPublicRole().getId()))
        {
            return true;
        }
        else
        {
            List<Role> roles = member.getRoles();
            boolean disjoint = Collections.disjoint(roles, rolesPermittedToJoin);
            return !disjoint;
        }
    }

    private void allowGlobalChannelAccess(Member member)
    {
        TextChannel globalChannel = quiz.getGlobalQuizChannel();
        if (globalChannel != null)
            globalChannel.putPermissionOverride(member).setAllow(Permission.MESSAGE_READ).queue();
    }

    private void denyGlobalChannelAccess(Member member)
    {
        TextChannel globalChannel = quiz.getGlobalQuizChannel();
        if (globalChannel != null)
            globalChannel.putPermissionOverride(member).setDeny(Permission.MESSAGE_READ).queue();
    }
}
