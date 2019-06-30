import commands.CmdUtil;
import commands.GameCommand;
import configuration.SettingsUtil;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class NaughtsAndCrosses extends GameCommand
{
    private TextChannel channel;
    private Member player1;
    private Member player2;
    private String[][] board = new String[3][3];
    private EmbedBuilder embed = new EmbedBuilder();
    private MessageManager mm = new MessageManager();


    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        for (int i = 0; i<board.length; i++)
        {
            board[i] = new String[]{" ", " ", " "};
        }

        Member activePlayer = setupPlayers(msgEvent, parameters);
        takeTurn(activePlayer);
    }

    private Member setupPlayers(GuildMessageReceivedEvent msgEvent, String[] parameters)
    {
        player1 = msgEvent.getMember();

        if (parameters.length > 1)
        {
            List<Member> membersWithName = msgEvent.getGuild().getMembersByEffectiveName(parameters[1], true);
            if (membersWithName.size() > 0)
            {
                player2 = membersWithName.get(0);
                channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-TicTacToe", player1, player2);
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
        if (activePlayer != null)
        {
            embed.setDescription("**"+activePlayer.getEffectiveName()+", you're up!**\n\n"+drawBoard());
        }
        else
        {
            embed.setDescription("**Waiting for Player2 to join by selecting a spot...**\n\n"+drawBoard());
        }

        channel.sendMessage(embed.build()).queue();

        Message posMsg = null;
        boolean successfulPlacement = false;
        String counter = getPlayerCounter(activePlayer);
        while (posMsg == null || !posMsg.getMember().equals(activePlayer) || !successfulPlacement)
        {
            posMsg = mm.getNextMessage(channel);
            if (posMsg.getContentDisplay().equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                endGame(null);
            }
            if (activePlayer == null && !posMsg.getMember().equals(player1))     //Finally fill in P2, if they have not yet been set.
            {
                player2 = posMsg.getMember();
                activePlayer = player2;
            }
            successfulPlacement = applyPosition(posMsg.getContentDisplay(), counter);
        }
        Member winner = checkForWin();
        if (winner == null)
        {
            Member nextPlayer = (activePlayer.equals(player1)) ? player2 : player1;
            takeTurn(nextPlayer);
        }
        else
        {
            endGame(winner);
        }

    }

    private String drawBoard()
    {
        StringBuilder boardBuilder = new StringBuilder();
        boardBuilder.append("```");
        boardBuilder.append("  +-----------+\n");
        for (int i = 3; i>0; i--)
        {
            boardBuilder.append(i);
            boardBuilder.append(" | ");
            boardBuilder.append(board[0][i-1]);
            boardBuilder.append(" | ");
            boardBuilder.append(board[1][i-1]);
            boardBuilder.append(" | ");
            boardBuilder.append(board[2][i-1]);
            boardBuilder.append(" |\n");
            boardBuilder.append("  +-----------+\n");
        }
        boardBuilder.append("    A   B   C  ");
        boardBuilder.append("```");
        return boardBuilder.toString();
    }

    private boolean applyPosition(String position, String counter)
    {
        position = position.toLowerCase();
        if (position.matches("[1-3][a-c]"))
        {
            char[] swapArray = position.toCharArray();
            char temp = swapArray[0];
            swapArray[0] = swapArray[1];
            swapArray[1] = temp;
            position = String.valueOf(swapArray);
        }
        if (position.matches("[a-c][1-3]"))
        {
            char[] positionChars = position.toCharArray();
            int column = 0;
            int row = 0;
            switch (positionChars[0])
            {
                case 'a':
                    column = 0;
                    break;
                case 'b':
                    column = 1;
                    break;
                case 'c':
                    column = 2;
            }
            switch (positionChars[1])
            {
                case '1':
                    row = 0;
                    break;
                case '2':
                    row = 1;
                    break;
                case '3':
                    row = 2;
                    break;
            }
            if (board[column][row].equals(" "))
            {
                board[column][row] = counter;
                return true;
            }
            else
            {
                channel.sendMessage("That space is taken! Choose another.").queue();
            }
        }
        return false;
    }

    private String getPlayerCounter(Member player)
    {
        if (player1.equals(player))
        {
            return "O";
        }
        else
        {
            return "X";
        }
    }

    private Member getCounterPlayer(String counter)
    {
        if (counter.equals("O"))
        {
            return player1;
        }
        else if (counter.equals("X"))
        {
             return player2;
        }
        return null;
    }

    private Member checkForWin()
    {
        for (int column = 0; column<board.length; column++)
        {
            if (board[column][0].equals(board[column][1]) && board[column][1].equals(board[column][2]))
            {
                return getCounterPlayer(board[column][0]);
            }
        }
        for (int row = 0; row<board[0].length; row++)
        {
            if (board[0][row].equals(board[1][row]) && board[1][row].equals(board[2][row]))
            {
                return getCounterPlayer(board[0][row]);
            }
        }
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
        {
            return getCounterPlayer(board[0][0]);
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]))
        {
            return getCounterPlayer(board[0][2]);
        }
        if (!areMoreSpacesAvailable())
        {
            return (getCounterPlayer("O").getGuild().getSelfMember()); //TODO: HACK HACK HACK, change to endGame(null) when API supports deleteGameChannel() with just guild.
        }
        return null;
    }

    private boolean areMoreSpacesAvailable()
    {
        for (int column = 0; column<board.length; column++)
        {
            for (int row = 0; row<board[0].length; row++)
            {
                if (board[column][row].equals(" "))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void endGame(Member winner)
    {
        if (winner == null)
        {
            embed.setDescription(drawBoard()+"\n\nAnd that's a wrap, but no winner!");
        }
        else
        {
            embed.setDescription(drawBoard()+"\n\n**"+winner.getEffectiveName()+"** wins! Congratulations!");
        }
        channel.sendMessage(embed.build()).queue();
        super.deleteGameChannel();
    }
}
