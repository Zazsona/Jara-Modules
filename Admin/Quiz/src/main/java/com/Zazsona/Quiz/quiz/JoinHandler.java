package com.Zazsona.Quiz.quiz;

import com.Zazsona.Quiz.json.GuildQuizConfig;
import com.Zazsona.Quiz.system.SettingsManager;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class JoinHandler
{
    private HashMap<String, QuizTeam> quizTeams;
    private Category quizCategory;
    private TextChannel questionsChannel;
    private boolean quizStarted;

    private JoinMessageHandler jmh;

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
        questionsChannel.getJDA().removeEventListener(jmh);
    }

    /**
     * Starts listening for join commands, and managing them.
     * @param guild the guild for the quiz
     */
    public void acceptJoins(Guild guild)
    {
        GuildQuizConfig gqc = SettingsManager.getInstance().getGuildQuizSettings(guild.getId());
        jmh = new JoinMessageHandler(gqc);
        guild.getJDA().addEventListener(jmh);
    }

    /**
     * Checks if the user has at least one role enabling them to join
     * @param member the member trying to join
     * @param gqc the guild's quiz config
     * @return true/false on whether they can join
     */
    private boolean isAllowedToJoin(Member member, GuildQuizConfig gqc)
    {
        if (!gqc.isRoleAllowedToJoin(member.getGuild().getPublicRole().getId()))
        {
            for (Role role : member.getRoles())
            {
                boolean success = gqc.isRoleAllowedToJoin(role.getId());
                if (success)
                {
                    return true;
                }
            }
            return false;
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
        return getMemberTeam(member) != null;
    }

    private QuizTeam getMemberTeam(Member member)
    {
        for (QuizTeam quizTeam : quizTeams.values())
        {
            if (quizTeam.isTeamMember(member))
            {
                return quizTeam;
            }
        }
        return null;
    }

    private class JoinMessageHandler extends ListenerAdapter
    {
        private GuildQuizConfig gqc;

        public JoinMessageHandler(GuildQuizConfig gqc)
        {
            this.gqc = gqc;
        }

        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            if (!quizStarted) //Second check in case quiz started while waiting for message
            {
                Message message = event.getMessage();
                String msgContent = message.getContentDisplay();
                if (msgContent.startsWith(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"join") && isAllowedToJoin(message.getMember(), gqc))
                {
                    if (!isMemberInTeam(message.getMember()))
                    {
                        parseMessage(message, msgContent);
                    }
                    else
                    {
                        QuizTeam oldTeam = getMemberTeam(message.getMember());
                        oldTeam.removeTeamMember(message.getMember());
                        if (!oldTeam.hasMembers())
                        {
                            oldTeam.getTeamChannel().delete().queue();
                            quizTeams.remove(oldTeam.getTeamName().toUpperCase());
                        }
                        parseMessage(message, msgContent);
                    }
                }
            }
        }

        private void parseMessage(Message message, String msgContent)
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
    }
}
