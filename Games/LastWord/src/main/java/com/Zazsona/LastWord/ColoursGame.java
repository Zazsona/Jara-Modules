package com.Zazsona.LastWord;

import commands.CmdUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ColoursGame
{
    private String colour;

    protected void runGame(LastWord lastWord, GuildMessageReceivedEvent msgEvent, EmbedBuilder embed) throws IOException
    {
        try
        {
            initiateGame(msgEvent, embed);
            ArrayList<Message> messages = new ArrayList<>(lastWord.getMessages(msgEvent.getChannel()));
            Message winningMessage = getWinner(messages);
            lastWord.endGame(msgEvent, embed, winningMessage);
        }
        catch (NullPointerException e)
        {
            lastWord.endGame(msgEvent, embed, null);
        }
    }

    private void initiateGame(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed) throws IOException
    {
        String[] colours = {"Red", "Yellow", "Green", "Blue", "Purple", "Black", "White"};
        colour = colours[new Random().nextInt(colours.length)];
        embed.setDescription(":game_die: Welcome to The Last Word! :game_die:\nI'm looking for **"+ CmdUtil.getRandomTopic()+"** that are/is **"+ colour +"**");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private Message getWinner(ArrayList<Message> messages)
    {
        if (messages != null && messages.size() > 0)
        {
            for (int i = messages.size() - 1; i > -1; i--)
            {
                try
                {
                    if (CmdUtil.getWordList().contains(messages.get(i).getContentDisplay().toLowerCase().split(" ")[0])) //We can't check multiple words, unless we separate them, as anything with spaces isn't a word
                    {
                        return messages.get(i);
                    }
                    else
                    {
                        continue;
                    }
                }
                catch (IOException e)
                {
                    return messages.get(i);
                }
            }
        }
        return null;
    }

}
