package com.Zazsona.Connect4;

import com.Zazsona.Connect4.AI.AIDifficulty;
import com.Zazsona.Connect4.AI.AIPlayer;
import com.Zazsona.Connect4.game.Board;
import com.Zazsona.Connect4.game.Counter;
import commands.CmdUtil;
import configuration.SettingsUtil;
import exceptions.QuitException;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import static com.Zazsona.Connect4.game.Counter.*;

public class Connect4 extends ModuleGameCommand
{
    private TextChannel channel;
    private Member player1;
    private Member player2;
    private AIPlayer aiPlayer;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Board board = new Board();
        channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-connect4");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("Connect 4");

        configureAI(msgEvent, board, parameters);
        runGame(msgEvent, board, embed);
    }

    public static Counter getPlayerCounter(boolean isPlayer1)
    {
        return (isPlayer1) ? BLUE : RED;
    }

    private void runGame(GuildMessageReceivedEvent msgEvent, Board board, EmbedBuilder embed)
    {
        try
        {
            MessageManager mm = new MessageManager();
            boolean isPlayer1Turn = true;
            Counter winnerCounter = null;

            while (winnerCounter == null)
            {
                sendBoard("", embed, board);
                String selection = null;
                if (aiPlayer != null && aiPlayer.isPlayer1() == isPlayer1Turn)
                {
                    aiPlayer.takeTurn();
                }
                else
                {
                    while (selection == null || board.isColumnFull(selection))
                    {
                        selection = getInput(msgEvent, embed, mm, isPlayer1Turn, board);
                        if (board.isColumnFull(selection))
                            msgEvent.getChannel().sendMessage("That column is full! Please pick another.").queue();
                    }
                    board.placeCounter(selection, getPlayerCounter(isPlayer1Turn));
                }
                winnerCounter = board.getWinner();
                isPlayer1Turn = !isPlayer1Turn;
            }
            endGame(board, embed, winnerCounter);
        }
        catch (QuitException e)
        {
            return;
        }
    }

    private void endGame(Board board, EmbedBuilder embed, Counter winner)
    {
        try
        {
            embed.setThumbnail("https://i.imgur.com/rIBssX0.png");
            if (winner.equals(NONE))
            {
                sendBoard("**The board is full. Game over!**", embed, board);
            }
            else if (winner.equals(RED))
            {
                sendBoard("**"+player2.getEffectiveName()+" is the winner! Congratulations.**", embed, board);
            }
            else if (winner.equals(BLUE))
            {
                sendBoard("**"+player1.getEffectiveName()+" is the winner! Congratulations.**", embed, board);
            }
        }
        catch (NullPointerException e) //This happens if the game is exited before players are even set up
        {
            embed.setDescription("Game cancelled.");
            channel.sendMessage(embed.build()).queue();
        }
        super.deleteGameChannel();
    }

    /**
     * Converts the board into a graphical interface for Discord, and sends it using embed.
     */
    public void sendBoard(String customMessage, EmbedBuilder embed, Board board)
    {
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append(customMessage).append("\n\n");
        descBuilder.append(":regional_indicator_a: :regional_indicator_b: :regional_indicator_c: :regional_indicator_d: :regional_indicator_e: :regional_indicator_f: :regional_indicator_g:\n\n");

        for (int row = board.getBoardHeight()-1; row>-1; row--)
        {
            for (int column = 0; column<board.getBoardWidth(); column++)
            {

                switch (board.getCounter(column, row))
                {
                    case NONE:
                        descBuilder.append(":white_circle: ");
                        break;
                    case RED:
                        descBuilder.append(":red_circle: ");
                        break;
                    case BLUE:
                        descBuilder.append(":blue_circle: ");
                        break;
                }
            }
            descBuilder.append("\n");
        }
        embed.setDescription(descBuilder.toString());
        channel.sendMessage(embed.build()).queue();
    }

    /**
     * Takes input from the current player.
     * @param cmdMsgEvent the context to take input from
     */
    private String getInput(GuildMessageReceivedEvent cmdMsgEvent, EmbedBuilder embed, MessageManager mm, boolean isPlayer1Turn, Board board) throws QuitException
    {
        while (true) //Wait for return statement
        {
            Message msg = mm.getNextMessage(channel);
            if (msg.getContentDisplay().matches("[A-Ga-g]") || msg.getContentDisplay().equalsIgnoreCase("quit") || msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(cmdMsgEvent.getGuild().getId()) + "quit")) //If it is a valid input
            {
                assignPlayers(cmdMsgEvent.getMember(), msg.getMember(), isPlayer1Turn);
                if ((msg.getMember().equals(player1)) || (msg.getMember().equals(player2)) || msg.getMember().hasPermission(Permission.ADMINISTRATOR))                  //Run player-only code (+ admin for the sake of ending the game if need be)
                {
                    if (msg.getContentDisplay().equalsIgnoreCase("quit") || msg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(cmdMsgEvent.getGuild().getId()) + "quit"))
                    {
                        endGame(board, embed, getPlayerCounter(!msg.getMember().equals(player1))); //Invert the counter, so the winner is the opposition to the quitter
                        throw new QuitException();
                    }
                }
                if ((msg.getMember().equals(player1) && isPlayer1Turn) || (msg.getMember().equals(player2) && !isPlayer1Turn))
                {
                    return msg.getContentDisplay().toUpperCase();
                }
            }
        }
    }

    /**
     * Sets the players, if players aren't registered
     * @param host the game's host. They will always be a player.
     * @param memberToSet the member to register
     * @param setPlayer1 whether to register this player as player1
     */
    private void assignPlayers(Member host, Member memberToSet, boolean setPlayer1)
    {
        if (player1 == null && setPlayer1)
        {
            player1 = memberToSet;
            if (!player1.equals(host))
            {
                player2 = host;
            }
        }
        else if (player2 == null && !setPlayer1 && !memberToSet.equals(player1))
        {
            player2 = memberToSet;
        }
    }


    private void configureAI(GuildMessageReceivedEvent msgEvent, Board board, String[] parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("AI"))
            {
                if (parameters.length > 2)
                {
                    switch (parameters[2].toUpperCase())
                    {
                        case "EASY":
                            aiPlayer = new AIPlayer(board, false, AIDifficulty.EASY);
                            break;
                        case "MEDIUM":
                        case "STANDARD":
                        case "NORMAL":
                            aiPlayer = new AIPlayer(board, false, AIDifficulty.STANDARD);
                            break;
                        case "HARD":
                        case "PROUD":
                        case "CRITICAL":
                            aiPlayer = new AIPlayer(board, false, AIDifficulty.HARD);
                            break;
                    }
                }
                else
                    aiPlayer = new AIPlayer(board, false);

                player1 = msgEvent.getMember();
                player2 = msgEvent.getGuild().getSelfMember();
            }
        }
    }
}
