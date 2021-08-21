package com.Zazsona.TopTrumps;

import com.Zazsona.TopTrumps.cards.Card;
import com.Zazsona.TopTrumps.cards.Deck;
import com.Zazsona.TopTrumps.cards.Team;
import com.Zazsona.TopTrumps.exceptions.GameOverException;
import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class TopTrumps extends ModuleGameCommand
{
    private LinkedList<Card> cardsInHolding = new LinkedList<>();
    private boolean useAI = false;
    private AI ai;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("list"))
            {
                listDecks(msgEvent.getChannel());
                return;
            }
        }

        useAI = checkParametersForAIFlag(parameters);
        Setup setup = new Setup(useAI);
        boolean hasPermissions = setup.checkPermissions(msgEvent.getGuild().getSelfMember());
        if (hasPermissions)
        {
            Deck deck = DeckLoader.getDeck(msgEvent.getGuild().getId(), parameters);
            if (deck == null)
            {
                msgEvent.getChannel().sendMessage("There is no deck by that name.").queue();
                return;
            }
            else
            {
                ArrayList<Team> teams = new ArrayList<>();
                teams.addAll(setup.setupTeams(msgEvent.getChannel(), msgEvent.getMember(), deck, (useAI) ? 1 : 2));

                Team team1 = teams.get(0);
                Team team2 = teams.get(1);

                boolean isTeam1Turn = new Random().nextBoolean();
                MessageManager mm = new MessageManager();

                if (useAI)
                {
                    ai = new AI(deck);
                    team2.addTeamMember(msgEvent.getGuild().getSelfMember());
                }

                runGame(deck, team1, team2, isTeam1Turn, mm, setup);
            }
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("== Top Trumps ==");
            embed.setDescription("Insufficient bot permissions to run this game.\n\nRequired:\n-Manage Messages\n-Manage Channels\n-Manage Channel Permissions");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }

    }

    private void runGame(Deck deck, Team team1, Team team2, boolean isTeam1Turn, MessageManager mm, Setup setup)
    {
        int selection;
        try
        {
            while (team1.hasMoreCards() && team2.hasMoreCards())
            {
                sendCardEmbed(deck, team1, isTeam1Turn);
                sendCardEmbed(deck, team2, !isTeam1Turn);
                selection = (isTeam1Turn) ? getSelection(deck, team1, mm) : getSelection(deck, team2, mm);
                Team winningTeam = getWinner(team1, team2, selection, deck);
                sortCards(team1, team2, winningTeam);

                if (team1.equals(winningTeam))
                {
                    isTeam1Turn = true;
                }
                else if (team2.equals(winningTeam))
                {
                    isTeam1Turn = false;
                }
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("== Top Trumps ==");
            String embedDescription = (team1.hasMoreCards()) ? "**WINNER: TEAM 1**" : "**WINNER: TEAM 2**";
            embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
            embed.setDescription(embedDescription);
            team1.getChannel().sendMessage(embed.build()).queue();
            if (!useAI)
                team2.getChannel().sendMessage(embed.build()).queue();
        }
        catch (GameOverException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("== Top Trumps ==");
            embed.setDescription("The game has been quit.");
            team1.getChannel().sendMessage(embed.build()).queue();
            team2.getChannel().sendMessage(embed.build()).queue();
        }
        finally
        {
            endGame(setup);
        }
    }

    private Team getWinner(Team team1, Team team2, int selection, Deck deck)
    {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        String versusMessage = (deck.getStatNames()[selection]+":\n"+team1.getFrontCard().getName()+" vs "+team2.getFrontCard().getName());

        Card winningCard = getWinningCard(team1.getFrontCard(), team2.getFrontCard(), selection);
        if (team1.getFrontCard().equals(winningCard))
        {
            embed.setDescription("== **VICTORY** == \n\n"+versusMessage);
            embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
            team1.getChannel().sendMessage(embed.build()).queue();
            if (!useAI)
            {
                embed.setDescription("== **LOSS** == \n\n"+versusMessage);
                embed.setThumbnail("https://i.imgur.com/U7JJwRS.png");
                team2.getChannel().sendMessage(embed.build()).queue();
            }
            return team1;
        }
        else if (team2.getFrontCard().equals(winningCard))
        {
            embed.setDescription("== **LOSS** == \n\n"+versusMessage);
            embed.setThumbnail("https://i.imgur.com/U7JJwRS.png");
            team1.getChannel().sendMessage(embed.build()).queue();
            if (!useAI)
            {
                embed.setDescription("== **VICTORY** == \n\n"+versusMessage);
                embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
                team2.getChannel().sendMessage(embed.build()).queue();
            }
            return team2;
        }
        else
        {
            embed.setDescription("== **DRAW** == \n\n"+versusMessage);
            embed.setThumbnail("https://i.imgur.com/4TUoYOM.png");
            team1.getChannel().sendMessage(embed.build()).queue();
            if (!useAI)
                team2.getChannel().sendMessage(embed.build()).queue();
            return null;
        }
    }

    private void sortCards(Team team1, Team team2, Team winningTeam)
    {
        if (team1.equals(winningTeam))
        {
            for (Card card : cardsInHolding)
            {
                team1.addCard(card);
            }
            cardsInHolding.clear();
            team1.addCard(team2.removeCard(0));
            team1.cycleFrontCard();
        }
        else if (team2.equals(winningTeam))
        {
            for (Card card : cardsInHolding)
            {
                team2.addCard(card);
            }
            cardsInHolding.clear();
            team2.addCard(team1.removeCard(0));
            team2.cycleFrontCard();
        }
        else
        {
            cardsInHolding.add(team1.removeCard(0));
            cardsInHolding.add(team2.removeCard(0));
        }
    }

    private Card getWinningCard(Card team1Card, Card team2Card, int selection)
    {
        if (team1Card.getStats()[selection] < team2Card.getStats()[selection])
        {
            return team2Card;
        }
        else if (team1Card.getStats()[selection] > team2Card.getStats()[selection])
        {
            return team1Card;
        }
        else
        {
            return null;
        }
    }

    private int getSelection(Deck deck, Team team, MessageManager mm) throws GameOverException
    {
        if (useAI && team.getChannel() == null)
        {
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e) {}
            finally
            {
                return ai.getStatSelection(team.getFrontCard());
            }
        }
        else
        {
            int selectionIndex = -1;
            TextChannel channel = team.getChannel();
            while (selectionIndex == -1)
            {
                int timeout = Integer.parseInt(SettingsUtil.getGuildSettings(channel.getGuild().getId()).getGameChannelTimeout());
                Message msg = mm.getNextMessage(channel, timeout*60*1000);
                if (msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                {
                    channel.putPermissionOverride(msg.getMember()).setDeny(Permission.MESSAGE_READ).queue();
                    team.removeTeamMember(msg.getMember());
                    if (team.getMemberCount() == 0)
                    {
                        throw new GameOverException();
                    }
                }
                else if (msg == null)
                {
                    throw new GameOverException();
                }
                else
                {
                    selectionIndex = getSelectionIndex(msg.getContentDisplay(), deck);
                }

            }
            return selectionIndex;
        }
    }

    private int getSelectionIndex(String messageContent, Deck deck)
    {
        for (int i = 0; i<deck.getStatNames().length; i++)
        {
            if (messageContent.equalsIgnoreCase(deck.getStatNames()[i]))
            {
                return i;
            }
        }
        return -1;
    }

    private void sendCardEmbed(Deck deck, Team team, boolean activeTeam)
    {
        if (team.getChannel() != null)
        {
            Card card = team.getFrontCard();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Info");
            String turnText = (activeTeam) ? "*It's your turn!*" : "*It's the opponent's turn.*";
            embed.setDescription(turnText+ "\nYour cards: "+team.getCardCount()+"/"+deck.getCards().size()+"\nCards in holding: "+cardsInHolding.size()+"\n\n**"+card.getName()+"**");
            for (int i = 0; i<deck.getStatNames().length; i++)
            {
                embed.addField(deck.getStatNames()[i], String.format("%,.2f", card.getStats()[i]).replace(".00", ""), true);
            }
            try
            {
                embed.setThumbnail(card.getImageURL());
                team.getChannel().sendMessage(embed.build()).queue();
            }
            catch (IllegalArgumentException e)
            {
                embed.setThumbnail(null);
                team.getChannel().sendMessage(embed.build()).queue();
            }
        }
    }

    private void endGame(Setup setup)
    {
        try
        {
            Thread.sleep(30*1000);
            setup.destroySetup();
        }
        catch (InterruptedException e)
        {

        }
    }

    private void listDecks(TextChannel channel)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append("- Gaming\n").append("- TheSimpsons\n");
        try
        {
            for (String deckName : DeckLoader.getCustomDeckMap(channel.getGuild().getId()).keySet())
            {
                descBuilder.append("- ").append(deckName).append("\n");
            }
            embed.setDescription(descBuilder.toString());
        }
        catch (IOException e)
        {
            embed.setDescription("\n**ERROR: Unable to access custom decks.**");
            e.printStackTrace();
        }
        channel.sendMessage(embed.build()).queue();
    }

    private boolean checkParametersForAIFlag(String[] parameters)
    {
        for (String param : parameters)
        {
            if (param.equalsIgnoreCase("ai") || param.equalsIgnoreCase("bot"))
            {
                return true;
            }
        }
        return false;
    }





}
