package com.Zazsona.EasterHunt;

import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.GameCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class EasterEggHunt extends GameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        TextChannel channel = createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Egg-Hunt");
        String[][] grid = new String[5][5];
        for (int i = 0; i<5; i++)
        {
            for (int j = 0; j<5; j++)
            {
                grid[i][j] = " ";
            }
        }
        Random r = new Random();
        int winX = r.nextInt(5);
        int winY = r.nextInt(5);
        embed = getBoardEmbed(channel, grid);
        embed.setDescription(embed.getDescriptionBuilder().append("\nSend co-ordinates such as 'A2' to select a square!").toString());
        msgEvent.getChannel().sendMessage(embed.build()).complete();
        MessageManager mm = new MessageManager();
        boolean eggFound = false;
        while (!eggFound)
        {
            Message msg = mm.getNextMessage(channel);
            eggFound = applyPosition(channel, msg.getContentDisplay(), grid, winX, winY);
        }
        end();
    }

    private boolean applyPosition(TextChannel channel, String position, String[][] grid, int winColumn, int winRow)
    {
        position = position.toLowerCase();
        if (position.equals(SettingsUtil.getGuildSettings(channel.getGuild().getId()).getCommandPrefix()+"quit"))
        {
            channel.sendMessage("The game has been quit.").complete();
            return true;
        }

        if (position.matches("[1-5][a-e]"))
        {
            char[] swapArray = position.toCharArray();
            char temp = swapArray[0];
            swapArray[0] = swapArray[1];
            swapArray[1] = temp;
            position = String.valueOf(swapArray);
        }
        if (position.matches("[a-e][1-5]"))
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
                    break;
                case 'd':
                    column = 3;
                    break;
                case 'e':
                    column = 4;
                    break;
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
                case '4':
                    row = 3;
                    break;
                case '5':
                    row = 4;
                    break;
            }
            if (grid[column][row].equals(" "))
            {
                return plot(channel, grid, column, row, winColumn, winRow);

            }
            else
            {
                channel.sendMessage("That space is taken! Choose another.").queue();
            }
        }
        return false;
    }

    private EmbedBuilder getBoardEmbed(TextChannel channel, String[][] grid)
    {
        String Board = "```  +-------------------+" + "\n" +
                "5 | " + grid[0][4] + " | " + grid[1][4] + " | " + grid[2][4] + " | " + grid[3][4] + " | " + grid[4][4] + " |" + "\n" +
                "  +-------------------+" + "\n" +
                "4 | " + grid[0][3] + " | " + grid[1][3] + " | " + grid[2][3] + " | " + grid[3][3] + " | " + grid[4][3] + " |" + "\n" +
                "  +-------------------+" + "\n" +
                "3 | " + grid[0][2] + " | " + grid[1][2] + " | " + grid[2][2] + " | " + grid[3][2] + " | " + grid[4][2] + " |"+ "\n" +
                "  +-------------------+" + "\n" +
                "2 | " + grid[0][1] + " | " + grid[1][1] + " | " + grid[2][1] + " | " + grid[3][1] + " | " + grid[4][1] + " |"+ "\n" +
                "  +-------------------+" + "\n" +
                "1 | " + grid[0][0] + " | " + grid[1][0] + " | " + grid[2][0] + " | " + grid[3][0] + " | " + grid[4][0] + " |"+ "\n" +
                "  +-------------------+" + "\n" +
                "    A   B   C   D   E  ```";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription(Board);
        embed.setColor(Color.decode("#5de527"));
        return embed;
    }

    private boolean plot(TextChannel channel, String[][] grid, int x, int y, int winX, int winY)
    {
        if (x == winX)
        {
            if (y == winY)
            {
                grid[x][y] = "O";
                channel.sendMessage(getBoardEmbed(channel, grid).build()).queue();
                channel.sendMessage("Bingo! You found it. Nice one.").complete();
                return true;
            }
        }
        channel.sendMessage("No eggs here, I'm afraid!").complete();
        grid[x][y] = "X";
        channel.sendMessage(getBoardEmbed(channel, grid).build()).queue();
        return false;
    }

    private void end()
    {
        deleteGameChannel();
    }
}
