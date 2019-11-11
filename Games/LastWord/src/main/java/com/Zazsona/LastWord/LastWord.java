package com.Zazsona.LastWord;

import commands.CmdUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.*;

public class LastWord extends ModuleGameCommand
{
    private static Logger logger = LoggerFactory.getLogger(LastWord.class);
    private TextChannel channel;

    private enum GameMode
    {
        LETTER,
        COLOUR
    }

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-lastword");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Last Word");
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/hvphthX.png");
            GameMode gm = getGameModeSelection(parameters);
            switch (gm)
            {
                case LETTER:
                    new LettersGame().runGame(this, msgEvent, embed);
                    break;
                case COLOUR:
                    new ColoursGame().runGame(this, msgEvent, embed);
                    break;
                default:
                    embed.setDescription("Gamemode not recognised.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        catch (IOException e)
        {
            logger.error("The topics file is missing or unavailable.");
            msgEvent.getChannel().sendMessage("An error occurred when starting the game.").queue();
        }
    }

    private GameMode getGameModeSelection(String[] parameters)
    {
        if (parameters.length > 1)
        {
            switch (parameters[1].toLowerCase())
            {
                case "letters":
                case "letter":
                case "character":
                    return GameMode.LETTER;
                case "color":
                case "colour":
                case "colors":
                case "colours":
                    return GameMode.COLOUR;
                    default:
                        return null;
            }
        }
        return GameMode.LETTER;
    }

    protected Collection<Message> getMessages(TextChannel channel)
    {
        Random r = new Random();
        MessageManager mm = new MessageManager();
        Message[] messages = mm.getNextMessages(channel, (r.nextInt(23)+7)*1000, Integer.MAX_VALUE); //7-30 seconds, as many messages as we can take.
        if (messages != null && messages.length > 0)
        {
            LinkedList<Message> messagesSet = new LinkedList<>();
            boolean isCopiedAnswer = false;
            for (Message message : messages)
            {
                isCopiedAnswer = false;
                for (Message messagesSetMessage : messagesSet)
                {
                    if (messagesSetMessage.getContentDisplay().equalsIgnoreCase(message.getContentDisplay()))
                    {
                        isCopiedAnswer = true;
                        break;
                    }
                }
                if (!isCopiedAnswer)
                {
                    messagesSet.add(message);
                }
            }
            return messagesSet;
        }
        else
        {
            return null;
        }
    }

    protected void endGame(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed, Message winningMessage)
    {
        if (winningMessage == null)
        {
            embed.setDescription("Nobody got anything? Oh dear.\nBetter luck next time!");
        }
        else
        {
            embed.setDescription("The victory goes to **"+winningMessage.getMember().getEffectiveName()+"** with "+winningMessage.getContentDisplay()+"! Congratulations.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        deleteGameChannel();
    }


}
