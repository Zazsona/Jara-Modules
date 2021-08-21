package com.Zazsona.TopTrumps;

import com.Zazsona.TopTrumps.cards.Deck;
import com.Zazsona.TopTrumps.cards.Team;
import commands.CmdUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.LinkedList;
import java.util.List;

public class Setup
{
    private Category gameCategory;
    private Message joinMessage;
    private GameReactionListener grl;
    private boolean useAI;

    public Setup(boolean useAI)
    {
        this.useAI = useAI;
    }

    public boolean checkPermissions(Member selfMember)
    {
        return (selfMember.getPermissions().contains(Permission.MANAGE_CHANNEL) && selfMember.getPermissions().contains(Permission.MANAGE_PERMISSIONS) && selfMember.getPermissions().contains(Permission.MESSAGE_MANAGE));
    }

    public List<Team> setupTeams(TextChannel commandChannel, Member player, Deck deck, int teamCount)
    {
        int aiTeams = (useAI) ? 1 : 0;
        int handSize = (deck.getCards().size())/(teamCount+aiTeams);
        List<TextChannel> teamChannels = setupChannels(commandChannel.getGuild(), player, teamCount);
        LinkedList<Team> teams = new LinkedList<>();
        for (int i = 0; i<teamCount; i++)
        {
            teams.add(new Team(teamChannels.get(i), deck.getCards().subList(handSize*i, handSize*(i+1))));
        }
        teams.get(0).addTeamMember(player);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(player.getGuild().getSelfMember()));
        embed.setTitle("== Top Trumps ==");
        embed.setDescription(player.getEffectiveName()+" has started a game of Top Trumps!\nSelect a number to join that team.");
        joinMessage = commandChannel.sendMessage(embed.build()).complete();
        for (int i = 0; i<teamCount; i++)
        {
            joinMessage.addReaction(((char) (0x31+i))+"\u20E3").queue();
        }
            /*if (joinMessage.getReactions().size() < 9)
            {
                joinMessage.addReaction("\u25B6").queue();
            }*/

        grl = new GameReactionListener();
        grl.teams = teams;
        player.getGuild().getJDA().addEventListener(grl);
        if (useAI)
        {
            teams.add(new Team(null, deck.getCards().subList(handSize*(teamCount+aiTeams-1), handSize*(teamCount+aiTeams))));
        }
        return teams;

    }

    private List<TextChannel> setupChannels(Guild guild, Member player, int teamCount)
    {
        gameCategory = (Category) guild.createCategory("Top Trumps").complete();
        gameCategory.createPermissionOverride(guild.getSelfMember()).setAllow(Permission.MANAGE_PERMISSIONS).queue();
        for (int i = 0; i<teamCount; i++)
        {
            TextChannel teamChannel = (TextChannel) gameCategory.createTextChannel("Team-"+(i+1)).complete();
            teamChannel.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.MESSAGE_READ).queue();
        }
        gameCategory.getTextChannels().get(0).createPermissionOverride(player).setAllow(Permission.MESSAGE_READ).queue();
        return gameCategory.getTextChannels();
    }

    public void destroySetup()
    {
        gameCategory.getJDA().removeEventListener(grl);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(gameCategory.getGuild().getSelfMember()));
        embed.setDescription("This game has ended.");
        joinMessage.editMessage(embed.build()).queue();
        joinMessage.clearReactions().queue();
        for (Team team : grl.teams)
        {
            if (team.getChannel() != null)
            {
                team.getChannel().delete().queue();
            }
        }
        gameCategory.delete().queue();
    }

    private class GameReactionListener extends ListenerAdapter
    {
        public List<Team> teams;
        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent mra)
        {
            if (!mra.getMember().getUser().isBot())
            {
                int teamIndex = -1;
                switch (mra.getReactionEmote().getName())
                {
                    case "\u0031\u20E3":
                        teamIndex = 0;
                        break;
                    case "\u0032\u20E3":
                        teamIndex = 1;
                        break;
                    case "\u0033\u20E3":
                        teamIndex = 2;
                        break;
                    case "\u0034\u20E3":
                        teamIndex = 3;
                        break;
                    case "\u0035\u20E3":
                        teamIndex = 4;
                        break;
                    case "\u0036\u20E3":
                        teamIndex = 5;
                        break;
                    case "\u0037\u20E3":
                        teamIndex = 6;
                        break;
                    case "\u0038\u20E3":
                        teamIndex = 7;
                        break;
                    case "\u0039\u20E3":
                        teamIndex = 8;
                        break;
                    /*case "\u25B6":
                        teamIndex = teams.size(); //If the team doesn't get added, this will fail on validation.
                        addTeam(mra.getMember(), teams.size());
                        break;*/
                }
                if (teamIndex != -1 && teamIndex < teams.size())
                {
                    Team targetTeam = teams.get(teamIndex);
                    for (Team team : teams)
                    {
                        if (!team.equals(targetTeam) && team.isPastOrPresentTeamMember(mra.getMember()))
                        {
                            mra.getMember().getUser().openPrivateChannel().complete().sendMessage("You can't join a different team to one you've joined previously!").queue();
                            mra.getReaction().removeReaction(mra.getUser()).queue();
                            return;
                        }
                    }
                    try
                    {
                        teams.get(teamIndex).getChannel().createPermissionOverride(mra.getMember()).setAllow(Permission.MESSAGE_READ).queue();
                    }
                    catch (IllegalStateException e)
                    {
                        teams.get(teamIndex).getChannel().putPermissionOverride(mra.getMember()).setAllow(Permission.MESSAGE_READ).queue();
                    }
                }
                else
                {
                    mra.getReaction().removeReaction(mra.getUser()).queue();
                }
            }
        }
    }
}
