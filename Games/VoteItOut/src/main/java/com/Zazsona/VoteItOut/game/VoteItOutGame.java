package com.Zazsona.VoteItOut.game;

import configuration.SettingsUtil;
import jara.Core;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteItOutGame
{
    private static final int MAX_PLAYERS = 6;
    private static final String[] NUMBER_EMOTES = new String[]{"\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3"};

    String gameUUID;
    private TextChannel channel;
    private Player[] players;
    private Quips quips = new Quips();
    private GraphicsRenderer graphicsRenderer;
    private int failedRoundsInARow = 0;
    private QuitListener quitListener;

    public VoteItOutGame(TextChannel channel, Member... members) throws IOException
    {
        this.gameUUID = UUID.randomUUID().toString();
        this.channel = channel;
        this.players = new Player[members.length];
        for (int i = 0; i<members.length; i++)
        {
            players[i] = new Player((i+1), members[i]);
        }
        graphicsRenderer = new GraphicsRenderer(gameUUID, players);
        quitListener = new QuitListener();
        Core.getShardManagerNotNull().addEventListener(quitListener);
    }

    public Player runGame()
    {
        Message votingMessage = sendQuip();
        VoteListener voteListener = getVotes(votingMessage);
        Player winner = getWinner(voteListener);
        if (winner != null)
        {
            winner.addPoint();
            if (winner.getPoints() >= 5)
                return endGame(winner);
        }
        if (failedRoundsInARow == 3 || quitListener.isQuit())
            return endGame(null);

        return runGame();
    }

    private Player getWinner(VoteListener voteListener)
    {
        HashMap<Player, Integer> playerToVoteMap = voteListener.getVotes();
        HashMap<Player, Integer> playerToReceivedVotesMap = new HashMap<>();
        Player roundWinner = null;
        if (playerToVoteMap.size() > 0)
        {
            for (Map.Entry<Player, Integer> entry : playerToVoteMap.entrySet())
            {
                Player votedPlayer = getPlayerFromNo(entry.getValue());
                if (!playerToReceivedVotesMap.containsKey(votedPlayer))
                    playerToReceivedVotesMap.put(votedPlayer, 1);
                else
                {
                    int existingVotes = playerToReceivedVotesMap.get(votedPlayer);
                    playerToReceivedVotesMap.put(votedPlayer, existingVotes + 1);
                }
                if (roundWinner == null && playerToReceivedVotesMap.get(votedPlayer) >= playerToVoteMap.size()/2) //We only set round winner if there isn't one, so that the person who got the most votes first wins
                {
                    roundWinner = votedPlayer;
                }
            }
        }
        sendVotesMessage(playerToReceivedVotesMap, roundWinner);
        return roundWinner;
    }

    private void sendVotesMessage(HashMap<Player, Integer> playerToReceivedVotesMap, Player winner)
    {
        if (playerToReceivedVotesMap.size() > 0)
        {
            StringBuilder voteList = new StringBuilder();
            for (Map.Entry<Player, Integer> entry : playerToReceivedVotesMap.entrySet())
            {
                if (!entry.getKey().equals(winner))
                    voteList.append(entry.getKey().getMember().getEffectiveName()).append(": ").append(entry.getValue()).append("\n");
                else
                    voteList.append("**").append(entry.getKey().getMember().getEffectiveName()).append("**: ").append(entry.getValue()).append("\n");
            }
            channel.sendMessage(getVoteEmbedStyle(voteList.toString())).queue();
            failedRoundsInARow = 0;
        }
        else
        {
            channel.sendMessage(getVoteEmbedStyle("There were no votes this round!")).queue();
            failedRoundsInARow++;
        }
    }

    @NotNull
    private VoteItOutGame.VoteListener getVotes(Message votingMessage)
    {
        int voteTime = 1000*30;
        VoteListener voteListener = new VoteListener(votingMessage);
        Core.getShardManagerNotNull().addEventListener(voteListener);
        while (voteListener.getVotes().size() != players.length && voteTime > 0 && !quitListener.isQuit())
        {
            try
            {
                Thread.sleep(500);
                voteTime -= 500;
            }
            catch (InterruptedException e)
            {
                voteTime -= 250;
            }
        }
        ;
        Core.getShardManagerNotNull().removeEventListener(voteListener);
        return voteListener;
    }


    private Message sendQuip()
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.BLACK)
                .setTitle(quips.getQuip(channel.isNSFW()))
                .setFooter("30 Seconds!");
        channel.sendMessage(embed.build()).complete();
        Message votingMessage = channel.sendMessage("Voting:").addFile(graphicsRenderer.getBoardFile()).complete();
        for (int i = 0; i<players.length; i++)
            votingMessage.addReaction(NUMBER_EMOTES[i]).queue();
        return votingMessage;
    }

    public static int getMaxPlayers()
    {
        return MAX_PLAYERS;
    }

    private Player getPlayerFromNo(int playerNo)
    {
        return players[playerNo-1];
    }

    private Player getPlayerFromMember(Member member)
    {
        for (Player player : players)
        {
            if (player.getMember().equals(member))
                return player;
        }
        return null;
    }

    private Player endGame(Player winner)
    {
        Core.getShardManagerNotNull().removeEventListener(quitListener);
        graphicsRenderer.dispose();
        if (winner != null)
            channel.sendMessage(getVoteEmbedStyle(winner.getMember().getEffectiveName()+" wins! ...If this is really winning.")).queue();
        else
            channel.sendMessage(getVoteEmbedStyle("The game has been cancelled.")).queue();
        return winner;
    }

    private MessageEmbed getVoteEmbedStyle(String description)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.white);
        embed.setTitle("Votes:");
        embed.setDescription(description);
        return embed.build();
    }

    private class VoteListener extends ListenerAdapter
    {
        private HashMap<Player, Integer> playerToVoteMap = new HashMap<>();
        private Message voteMessage;

        public VoteListener(Message voteMessage)
        {
            this.voteMessage = voteMessage;
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
        {
            super.onMessageReactionAdd(event);
            if (!event.getMember().getUser().isBot() && event.getMessageId().equals(voteMessage.getId()))
            {
                Player player = getPlayerFromMember(event.getMember());
                if (player != null)
                {
                    playerToVoteMap.put(player, getReactionNumber(event.getReactionEmote().getName()));
                }
            }
            if (!event.getMember().equals(event.getGuild().getSelfMember()))
            {
                event.getReaction().removeReaction(event.getUser()).queue();
            }
        }

        public HashMap<Player, Integer> getVotes()
        {
            return playerToVoteMap;
        }

        private int getReactionNumber(String reaction)
        {
            for (int i = 0; i<NUMBER_EMOTES.length; i++)
            {
                if (reaction.equals(NUMBER_EMOTES[i]))
                    return (i+1);
            }
            return -1;
        }
    }

    private class QuitListener extends ListenerAdapter
    {
        private boolean quit;

        protected boolean isQuit()
        {
            return quit;
        }

        @Override
        public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event)
        {
            super.onGuildMessageReceived(event);
            if (event.getChannel().equals(channel) && !event.getMember().getUser().isBot())
            {
                String messageContent = event.getMessage().getContentDisplay();
                if (messageContent.equalsIgnoreCase("quit") || messageContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(event.getGuild().getId())+"quit"))
                {
                    Member member = event.getMember();
                    for (Player player : players)
                    {
                        if (member.equals(player.getMember()))
                        {
                            quit = true;
                            break;
                        }
                    }
                }
            }
        }
    }
}
