package com.Zazsona.QuizNight.quiz;

import com.Zazsona.QuizNight.system.SettingsManager;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class JoinHandler
{
    private HashMap<String, QuizTeam> quizTeams;
    private Category quizCategory;
    private TextChannel questionsChannel;
    private boolean quizStarted;

    public JoinHandler(Category quizCategory, TextChannel questionsChannel, HashMap<String, QuizTeam> quizTeams)
    {
        this.quizCategory = quizCategory;
        this.quizTeams = quizTeams;
        this.questionsChannel = questionsChannel;
    }

    /**
     * Stops accepting joins. After this has been called, any running cases of acceptJoins() will stop.
     */
    public void stopAcceptingJoins()
    {
        quizStarted = true;
    }

    /**
     * Starts listening for join commands, and managing them.
     * @param guild the guild for the quiz
     */
    public void acceptJoins(Guild guild)
    {
        String[] allowedRoleIDs = SettingsManager.getGuildQuizSettings(guild.getIdLong()).AllowedRoles;
        ArrayList<Role> allowedRoles = new ArrayList<>();
        for (String roleID : allowedRoleIDs)
        {
            Role role = guild.getRoleById(roleID);              //Converting roleIDs to roles for more efficient checking.
            if (role != null)
                allowedRoles.add(role);
        }

        MessageManager mm = new MessageManager();
        while (!quizStarted)
        {
            Message message = mm.getNextMessage(guild);
            if (!quizStarted) //Second check in case quiz started while waiting for message
            {
                String msgContent = message.getContentDisplay();
                if (msgContent.startsWith(SettingsUtil.getGuildCommandPrefix(guild.getId())+"join") && isAllowedToJoin(message.getMember(), allowedRoles))
                {
                    if (!isMemberInTeam(message.getMember()))
                    {
                        String teamname = "";
                        String params[] = msgContent.trim().split(" ");
                        if (params.length > 1)
                        {
                            for (int i = 1; i<params.length; i++)
                            {
                                teamname += params[i]+" ";
                            }
                            teamname = teamname.trim();
                        }
                        else
                        {
                            teamname = message.getMember().getEffectiveName()+"'s Team";
                        }
                        if (quizTeams.containsKey(teamname.toUpperCase()))
                        {
                            joinTeam(teamname, message.getMember());
                        }
                        else
                        {
                            if (teamname.length() < 2 || teamname.length() > 99)
                            {
                                message.getChannel().sendMessage("Team names must be between 2 and 99 characters.").queue();
                            }
                            else
                            {
                                addTeam(teamname, message.getMember());
                            }
                        }
                    }
                    else
                    {
                        message.getChannel().sendMessage("You've already joined a team for this quiz.").queue();
                    }

                }
            }
        }
    }

    /**
     * Checks if the user has at least one role enabling them to join
     * @param member the member trying to join
     * @param allowedRoles the roles allowed to join
     * @return true/false on whether they can join
     */
    private boolean isAllowedToJoin(Member member, Collection<Role> allowedRoles)
    {
        if (allowedRoles != null && allowedRoles.size() > 0 && !allowedRoles.contains(member.getGuild().getPublicRole()))
        {
            return !Collections.disjoint(member.getRoles(), allowedRoles);
        }
        else
        {
            return true;
        }
    }

    /**
     * Creates a new team.
     * @param teamname the name of the team
     * @param leader the founding team member
     * @return true/false on whether team already exists.
     */
    private boolean addTeam(String teamname, Member leader)
    {
        if (!quizTeams.containsKey(teamname.toUpperCase()))
        {
            TextChannel teamChannel = (TextChannel) quizCategory.createTextChannel(teamname).complete();
            teamChannel.createPermissionOverride(leader).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
            questionsChannel.createPermissionOverride(leader).setAllow(Permission.MESSAGE_READ).queue();
            QuizTeam quizTeam = new QuizTeam(teamname, leader, teamChannel);
            quizTeams.put(teamname.toUpperCase(), quizTeam);
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * Adds a player to a team
     * @param teamname the team to join
     * @param player the player joining
     * @return true/false on whether team exists.
     */
    private boolean joinTeam(String teamname, Member player)
    {
        QuizTeam quizTeam = quizTeams.get(teamname.toUpperCase());
        if (quizTeam != null)
        {
            quizTeam.addTeamMember(player);
            quizTeam.getTeamChannel().createPermissionOverride(player).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
            questionsChannel.createPermissionOverride(player).setAllow(Permission.MESSAGE_READ).queue();
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isMemberInTeam(Member member)
    {
        for (QuizTeam quizTeam : quizTeams.values())
        {
            if (quizTeam.isTeamMember(member))
            {
                return true;
            }
        }
        return false;
    }
}
