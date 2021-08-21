package com.zazsona.numberguess;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;
import java.util.regex.Pattern;

public class NumberGuess extends ModuleGameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-Number-Search");
        int target = new Random().nextInt(1000);
        final int startingGuesses = 8;
        int guesses = startingGuesses;
        Message message = null;
        MessageManager mm = new MessageManager();
        channel.sendMessage("I'm thinking of a number below 1000... And you've got **"+startingGuesses+"** guesses!").queue();
        while (guesses > 0)
        {
            try
            {
                message = mm.getNextMessage(channel);
                String messageContent = message.getContentDisplay();
                if (message.getMember().equals(msgEvent.getMember()))
                {
                    int guess = Integer.parseInt(messageContent);
                    guesses--;
                    if (guess < 0)
                    {
                        channel.sendMessage("It's a positive number. I won't count this one as a guess.").queue();
                        guesses++; //Since guesses are removed beforehand, this can't be abused for infinite guesses.
                    }
                    else if (guess > 999)
                    {
                        channel.sendMessage("It's below 1000. I won't count this one as a guess.").queue();
                        guesses++; //Since guesses are removed beforehand, this can't be abused for infinite guesses.
                    }
                    else if (guess == target)
                    {
                        channel.sendMessage("Bingo! It was **"+target+"**! You found it in "+(startingGuesses-guesses)+" guesses.").queue();
                        return;
                    }
                    else if (guess < target)
                    {
                        int offset = (target - guess);
                        if (offset > 300)
                        {
                            channel.sendMessage("Don't quit your day job. You're extremely far below.").queue();
                        }
                        else if (offset > 100 && offset <= 300)
                        {
                            channel.sendMessage("You're far, far too low.").queue();
                        }
                        else if (offset > 50 && offset <= 100)
                        {
                            channel.sendMessage("Too low!").queue();
                        }
                        else if (offset <= 50 && offset > 10)
                        {
                            channel.sendMessage("Lookin' a bit too low there. ").queue();
                        }
                        else if (offset <= 10)
                        {
                            channel.sendMessage("Too low! But you're really close.").queue();
                        }
                    }
                    else if (guess > target)
                    {
                        int offset = (guess - target);
                        if (offset > 300)
                        {
                            channel.sendMessage("That one's a little higher than I was thinking. And by a little, I mean a huge amount higher.").queue();
                        }
                        else if (offset > 100 && offset <= 300)
                        {
                            channel.sendMessage("You're looking really, really high there.").queue();
                        }
                        else if (offset > 50 && offset <= 100)
                        {
                            channel.sendMessage("Too high!").queue();
                        }
                        else if (offset <= 50 && offset > 10)
                        {
                            channel.sendMessage("Think we can drop that one down a bit?").queue();
                        }
                        else if (offset <= 10)
                        {
                            channel.sendMessage("Too high, but not by much! You're almost there.").queue();
                        }
                    }
                }
            }
            catch (NumberFormatException e)
            {
                if (Pattern.matches("[0-9]+", message.getContentDisplay()))
                {
                    channel.sendMessage("...No.\nIt's below 1000. I won't count this one as a guess.").queue();
                }
                else if (message.getContentDisplay().toLowerCase().equals(SettingsUtil.getGuildCommandPrefix(message.getGuild().getId())+"quit"))
                {
                    guesses = Integer.MIN_VALUE;
                }
                //Likely just a chat message otherwise, so ignore it.
            }

        }
        channel.sendMessage("And with that, you're all out of guesses. I was thinking about the number **"+target+"**.").queue();
        super.deleteGameChannel();
    }
}
