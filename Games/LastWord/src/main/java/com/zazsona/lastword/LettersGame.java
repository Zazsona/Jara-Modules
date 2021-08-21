package com.zazsona.lastword;

import com.zazsona.jara.commands.CmdUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class LettersGame
{
    private String letter;

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
        String[] letters = {"A", "A", "A", "B","B","B", "C", "C", "C", "C", "D","D","D", "E","E","E","E", "F","F", "G","G","G", "H", "I", "J", "K", "L","L","L", "M","M","M", "N","N", "O", "P","P", "Q", "R","R","R", "S", "S", "S", "S", "S", "S", "S", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        letter = letters[new Random().nextInt(letters.length)];
        embed.setDescription(":game_die: Welcome to The Last Word! :game_die:\nI'm looking for **"+ CmdUtil.getRandomTopic()+"** starting with **"+letter+"**");
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private Message getWinner(ArrayList<Message> messages)
    {
        if (messages != null && messages.size() > 0)
        {
            for (int i = messages.size() - 1; i > -1; i--)
            {
                if (messages.get(i).getContentDisplay().toUpperCase().startsWith(letter))
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
                        LoggerFactory.getLogger(LastWord.class).error(e.toString());
                        return messages.get(i);
                    }

                }
            }
        }
        return null;
    }

}
