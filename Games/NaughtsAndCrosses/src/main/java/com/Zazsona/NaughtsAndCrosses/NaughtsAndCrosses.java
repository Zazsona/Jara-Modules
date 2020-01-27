package com.Zazsona.NaughtsAndCrosses;

import com.Zazsona.NaughtsAndCrosses.AI.AIPlayer;
import com.Zazsona.NaughtsAndCrosses.game.Board;
import com.Zazsona.NaughtsAndCrosses.game.Counter;
import commands.CmdUtil;
import configuration.SettingsUtil;
import exceptions.QuitException;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class NaughtsAndCrosses extends ModuleGameCommand
{
    private TextChannel channel;
    private Member player1;
    private Member player2;
    private AIPlayer aiPlayer;
    private Board board;
    private EmbedBuilder embed = new EmbedBuilder();
    private MessageManager mm = new MessageManager();


    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

        Member activePlayer = setup(msgEvent, parameters);
        takeTurn(activePlayer);
    }

    private Member setup(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        board = new Board();
        player1 = msgEvent.getMember();
        if (parameters.length > 1)
        {
            List<Member> membersMentioned = msgEvent.getMessage().getMentionedMembers();
            if (membersMentioned.size() > 0)
            {
                player2 = membersMentioned.get(0);
                channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TicTacToe", player1, player2);
            }
            else if (parameters[1].equalsIgnoreCase("ai"))
            {
                aiPlayer = new AIPlayer(board, false);
                player2 = msgEvent.getGuild().getSelfMember();
                channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TicTacToe");
            }
            else
            {
                channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TicTacToe");
            }
        }
        else
        {
            channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TicTacToe");
        }
        Random r = new Random();
        if (r.nextBoolean())
        {
            return player1;
        }
        else
        {
            return player2;
        }
    }

    private void takeTurn(Member activePlayer)
    {
        try
        {
            if (activePlayer != null)
                embed.setDescription("**"+activePlayer.getEffectiveName()+", you're up!**\n\n"+drawBoard());
            else
                embed.setDescription("**Player 2, you're up!**\n\n"+drawBoard());
            channel.sendMessage(embed.build()).queue();
            if (aiPlayer != null && activePlayer.equals(player2))
            {
                aiPlayer.takeTurn();
            }
            else
            {
                boolean placementSuccessful = false;
                while (!placementSuccessful)
                {
                    Message msg = mm.getNextMessage(channel);
                    if (msg != null && !msg.getMember().getUser().isBot())
                    {
                        String msgContent = msg.getContentDisplay();
                        activePlayer = assignPlayer(activePlayer, msg);

                        if (msgContent.equalsIgnoreCase("quit") || msgContent.equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
                            throw new QuitException();
                        else
                            placementSuccessful = placeCounter(msgContent, getPlayerCounter(activePlayer));
                    }
                }
            }
            Counter winningCounter = board.getWinner();
            if (winningCounter == null)
                takeTurn((activePlayer.equals(player1)) ? player2 : player1);
            else
                endGame(winningCounter);
        }
        catch (QuitException e)
        {
            endGame((player1.equals(activePlayer)) ? getPlayerCounter(player2) : getPlayerCounter(player1));
        }
    }

    @Nullable
    private Member assignPlayer(Member activePlayer, Message msg)
    {
        if (activePlayer == null)
        {
            player2 = (msg.getMember().equals(player1)) ? null : msg.getMember();
            activePlayer = player2;
        }
        return activePlayer;
    }

    private String drawBoard()
    {
        StringBuilder boardBuilder = new StringBuilder();
        boardBuilder.append("```");
        boardBuilder.append("  +-----------+\n");
        for (int row = 0; row<board.getBoardHeight(); row++)
        {
            boardBuilder.append((row+1));
            boardBuilder.append(" | ");
            boardBuilder.append(getCounterDrawable(board.getCounterAtPosition(0, row)));
            boardBuilder.append(" | ");
            boardBuilder.append(getCounterDrawable(board.getCounterAtPosition(1, row)));
            boardBuilder.append(" | ");
            boardBuilder.append(getCounterDrawable(board.getCounterAtPosition(2, row)));
            boardBuilder.append(" |\n");
            boardBuilder.append("  +-----------+\n");
        }
        boardBuilder.append("    A   B   C  ");
        boardBuilder.append("```");
        return boardBuilder.toString();
    }

    private String getCounterDrawable(Counter counter)
    {
        switch (counter)
        {
            case NAUGHT:
                return "O";
            case CROSS:
                return "X";
            case NONE:
                return " ";
        }
        return "";
    }

    private boolean placeCounter(String input, Counter counter)
    {
        input = input.toUpperCase();
        if (input.matches("[A-C][1-3]") || input.matches("[1-3][A-C]"))
        {
            String firstToken = input.substring(0, 1);
            String secondToken = input.substring(1, 2);
            String columnToken = (firstToken.matches("[A-C]")) ? firstToken : secondToken;
            String rowToken = (columnToken.equals(firstToken)) ? secondToken : firstToken;
            int column = getTokenValue(columnToken);
            int row = getTokenValue(rowToken);

            if (!board.isPositionOccupied(column, row))
                return board.placeCounter(column, row, counter);
            else
                channel.sendMessage(embed.setDescription("That space is taken!").build()).queue();
        }
        return false;
    }

    private int getTokenValue(String token)
    {
        token = token.toUpperCase();
        switch (token)
        {
            case "A":
            case "1":
                return 0;
            case "B":
            case "2":
                return 1;
            case "C":
            case "3":
                return 2;
        }
        return -1;
    }

    private Counter getPlayerCounter(Member player)
    {
        if (player1.equals(player))
        {
            return Counter.NAUGHT;
        }
        else
        {
            return Counter.CROSS;
        }
    }

    public static Counter getPlayerCounter(boolean isPlayer1)
    {
        if (isPlayer1)
        {
            return Counter.NAUGHT;
        }
        else
        {
            return Counter.CROSS;
        }
    }

    private Member getCounterPlayer(Counter counter)
    {
        if (counter == Counter.NAUGHT)
        {
            return player1;
        }
        else if (counter == Counter.CROSS)
        {
             return player2;
        }
        return null;
    }

    private void endGame(Counter counter)
    {
        if (counter == null || counter == Counter.NONE)
        {
            embed.setDescription(drawBoard()+"\n\nAnd that's a wrap, but no winner!");
        }
        else
        {
            Member winner = getCounterPlayer(counter);
            embed.setDescription(drawBoard()+"\n\n**"+winner.getEffectiveName()+"** wins! Congratulations!");
        }
        channel.sendMessage(embed.build()).queue();
        super.deleteGameChannel();
    }
}
