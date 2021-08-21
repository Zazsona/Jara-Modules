package com.zazsona.wordsearch;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.exceptions.QuitException;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class WordSearch extends ModuleGameCommand
{
    private TextChannel channel;
    private Board board;
    private BoardRenderer boardRenderer;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Wordsearch");
            board = new Board();
            boardRenderer = new BoardRenderer(board);
            MessageManager mm = new MessageManager();
            sendWelcomeMessage();
            boolean isInputValid = false;
            while (!board.isComplete())
            {
                channel.sendFile(boardRenderer.getBoardImageFile()).complete();
                while (!isInputValid)
                {
                    Message msg = mm.getNextMessage(channel);
                    isInputValid = checkInput(msg);
                }
                isInputValid = false;
            }
            channel.sendFile(boardRenderer.getBoardImageFile()).complete();
            endGame();
        }
        catch (IOException | FontFormatException e)
        {
            e.printStackTrace();
            LoggerFactory.getLogger(getClass()).error(e.getMessage());
            msgEvent.getChannel().sendMessage("An error occurred when running the game.").queue();
            super.deleteGameChannel();
            if (boardRenderer != null)
                boardRenderer.dispose();
        }
        catch (QuitException e)
        {
            endGame();
        }
    }
    private void sendWelcomeMessage()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("Welcome to Word Search! Simply enter the co-ordinates of the word when you find it, and try to find all "+board.getWords().length+"!\nE.g: B5-F9");
        embedBuilder.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        channel.sendMessage(embedBuilder.build()).queue();
    }

    private void endGame()
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription("That's a wrap! Thanks for playing.");
        embedBuilder.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        channel.sendMessage(embedBuilder.build()).queue();
        boardRenderer.dispose();
        super.deleteGameChannel();
    }

    private boolean checkInput(Message msg) throws IOException, QuitException
    {
        try
        {
            String input = msg.getContentDisplay();
            input = input.toUpperCase().trim();
            if (input.matches("[A-Z][1-9]-[A-Z][1-9]"))
            {
                input = input.replace("-", "").replace(" ", "");
                int startX = getLetterIndex(input.substring(0, 1));
                int startY = Integer.parseInt(input.substring(1, 2))-1;
                int endX = getLetterIndex(input.substring(2, 3));
                int endY = Integer.parseInt(input.substring(3, 4))-1;
                Word word = board.getWord(startX, startY, endX, endY);
                if (word != null)
                {
                    word.setFound(true);
                    boardRenderer.markWord(word);
                }
                else
                {
                    channel.sendMessage("Sorry, that's not it.").queue();
                }
                return true;
            }
            else if (input.equals(SettingsUtil.getGuildCommandPrefix(msg.getGuild().getId()) + "QUIT"))
            {
                throw new QuitException();
            }
            return false;
        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
    }

    private int getLetterIndex(String letter)
    {
        switch (letter)
        {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            case "F":
                return 5;
            case "G":
                return 6;
            case "H":
                return 7;
            case "I":
                return 8;
        }
        throw new IndexOutOfBoundsException();
    }

}
