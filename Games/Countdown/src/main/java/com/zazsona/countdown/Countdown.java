package com.zazsona.countdown;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Countdown extends ModuleGameCommand
{
    /**
     * The channel to send & receive messages with
     */
    private TextChannel channel;
    /**
     * The letters players can use
     */
    private String letters = "";
    /**
     * The message manager
     */
    private final MessageManager mm = new MessageManager();

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-countdown");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription(("**Welcome to Countdown!**\nTo get started, type 'c' or 'v' into chat to select either a consonant(s) or vowel(s)."));
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/KwjqNkH.png");
            channel.sendMessage(embed.build()).queue();
            generateLetters(parameters);
            Message[] answers = getAnswers(msgEvent);
            if (answers != null)
            {
                generateResults(answers);
            }
            else
            {
                embed.setDescription("Looks like nobody got anything. Game over!");
                channel.sendMessage(embed.build()).queue();
            }
        }
        catch (GameQuitException e)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/KwjqNkH.png");
            embed.setDescription("The game has been quit.");
            channel.sendMessage(embed.build()).queue();
        }
        finally
        {
            super.deleteGameChannel();
        }
    }

    /**
     * Creates the list of characters which players have to make a word from.
     * @param parameters the command parameters to search for entries
     * @return String - The letters to guess from
     */
    private String generateLetters(String...parameters) throws GameQuitException
    {
        //=============================================================================================
        //                                            Validation
        if (parameters.length > 1)		//If there are several parameters...
        {
            StringBuilder rebuiltParams = new StringBuilder();
            for (String parameter : parameters)
            {
                if (parameter.matches("[cv]+"))                //Take only the "C" or "V" ones (i.e, ignore /countdown and any other params that may be added)
                {
                    rebuiltParams.append(parameter);
                }
                if (rebuiltParams.length() == 9 - letters.length())
                {
                    break;                                        //Do not allow any more than 9 selections
                }
            }
            return generateLetters(rebuiltParams.toString());	//Recall the method, now with only one, valid, parameter.
        }
        //================================================================================================
        StringBuilder lettersBuilder = new StringBuilder();
        parameters[0] = parameters[0].toLowerCase().trim();
        if (parameters[0].matches("[cv]+"))
        {
            ArrayList<Character> consonants = new ArrayList<>(Arrays.asList('B', 'B', 'C', 'C', 'C', 'D', 'D', 'D', 'D', 'D', 'D', 'F', 'F', 'G', 'G', 'G', 'H', 'H', 'J', 'K', 'L', 'L', 'L', 'L', 'L', 'M', 'M', 'M', 'M', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'P', 'P', 'P', 'P', 'Q', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'V', 'W', 'X', 'Y', 'Z'));
            //By having duplicate letters here we can influence the odds of one coming up.
            ArrayList<Character> vowels = new ArrayList<>(Arrays.asList('A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'E', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'O', 'U', 'U', 'U', 'U', 'U'));
            Random r = new Random();
            char[] selections = parameters[0].toCharArray();
            lettersBuilder.append(letters); //Get previous progress.
            for (char selection : selections)
            {
                if (lettersBuilder.length() < 9)
                {
                    if (selection == 'v')
                    {
                        int index = r.nextInt(vowels.size());
                        lettersBuilder.append(vowels.get(index));		//We remove the chars as this changes the odds, as with the cards on the show.
                        vowels.remove(index);
                    }
                    else if (selection == 'c')
                    {
                        int index = r.nextInt(consonants.size());
                        lettersBuilder.append(consonants.get(index));
                        consonants.remove(index);
                    }
                }
                else
                {
                    break;
                }
            }
            letters = lettersBuilder.toString();
            if (letters.length() == 9)
            {
                return letters;		//All done here!
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription(createBoard());
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            channel.sendMessage(embed.build()).queue();
        }
        else if (parameters[0].equalsIgnoreCase(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
        {
            throw new GameQuitException();
        }
        return generateLetters(new MessageManager().getNextMessage(channel).getContentDisplay()); //If there are still selections missing, get 'em.
    }

    /**
     * Starts the clock and begins collecting answers from players. Holds the thread for 35 seconds.
     * @param msgEvent the context to gather messages from
     * @return Message[] - The messages received during the clock period
     */
    private Message[] getAnswers(GuildMessageReceivedEvent msgEvent) throws GameQuitException
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setDescription("You've got 30 seconds - On your marks, get set, go!\n\n**"+createBoard()+"**");
        embed.setThumbnail("https://i.imgur.com/0uNRZWG.png");
        Message embedMsg = channel.sendMessage(embed.build()).complete();
        Thread gameThread  = Thread.currentThread();
        Thread quitThread = new Thread(() -> {checkForQuit(mm, gameThread, channel);});
        quitThread.start();
        boolean clockComplete = runClock(embed, embedMsg);
        if (clockComplete)
        {
            embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/3SUuzD1.png");
            embed.setDescription("Time's up! You've got 5 seconds to state your word!");
            channel.sendMessage(embed.build()).queue();
            return mm.getNextMessages(channel, 5*1000, Integer.MAX_VALUE);
        }
        else
        {
            throw new GameQuitException();
        }

    }

    /**
     * Generates a winner from the answers provided.
     * @param answers the players' answers
     */
    private void generateResults(Message[] answers)
    {
        if (answers == null || answers.length == 0)
        {
            channel.sendMessage("Looks like nobody got anything! Better luck next time.").queue();
            return;
        }
        else
        {
            String[] winnerData = {"", ""}; //Name, Word - These are blank so that length checks do not throw a null exception for the first message.
            for (Message answer : answers)
            {
                String content = answer.getContentDisplay();
                if (content.length() > 9 || answer.getAuthor().isBot())
                {
                    continue;
                }
                List<Character> answerLetters = content.toUpperCase().chars().mapToObj((letter) -> (char) letter).collect(Collectors.toList()); //Converting each Int returns from chars() into a Character, as char[] cannot be converted into Character[]
                for (char letter : letters.toCharArray())
                {
                    answerLetters.remove(Character.valueOf(letter));
                }

                if (answerLetters.size() == 0)
                {
                    if (content.length() > winnerData[1].length()) //The result of all this, is that the first person with the longest word wins.
                    {
                        try
                        {
                            if (CmdUtil.getWordList().contains(content.toLowerCase()))
                            {
                                winnerData[0] = answer.getMember().getEffectiveName();
                                winnerData[1] = content;
                            }
                        }
                        catch (IOException e) //If the file is unavailable, we can't perform checks.
                        {
                            e.printStackTrace();
                            winnerData[0] = answer.getMember().getEffectiveName();
                            winnerData[1] = content;
                        }


                    }
                }
            }
            if (!winnerData[0].equals(""))
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(answers[0].getGuild().getSelfMember()));
                winnerData[1] = winnerData[1].toLowerCase();
                if (isRude(winnerData[1]))
                {
                    embed.setDescription("The scores are in, and this game's *dirty minded* Countdown winner is...\n\n**"+winnerData[0]+"** with their **"+winnerData[1].length()+"** letter word from the deepest of gutters, **"+winnerData[1]+"!**");
                    embed.setThumbnail("https://i.imgur.com/A9QBiiR.png");
                }
                else
                {
                    embed.setDescription("The scores are in, and this game's Countdown winner is...\n\n**"+winnerData[0]+"** with their **"+winnerData[1].length()+"** letter word, **"+winnerData[1]+"!**");
                    embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
                }
                channel.sendMessage(embed.build()).queue();
            }
            else
            {
                channel.sendMessage("Looks like nobody got it quite right. Better luck next time!").queue(); //The muppets didn't give any proper answers.
            }

        }

    }

    /**
     * Displays the letters players can use in a pretty format.
     * @return String - the letter board
     */
    private String createBoard()
    {
        StringBuilder boardBuilder = new StringBuilder();
        for (char letter : letters.toLowerCase().toCharArray())
        {
            boardBuilder.append(":regional_indicator_").append(letter).append(":");
        }
        return boardBuilder.toString();
    }

    /**
     * Checks whether the supplied answer is rude or not.
     * @param answer the winning answer
     * @return
     * true - The answer is vulgar<br>
     * false - The answer is clean
     */
    private boolean isRude(String answer)
    {
        return answer.contains("piss") || answer.equals("poo") || answer.contains("poop") || answer.equals("pee") || answer.equals("butt") || answer.contains("butts") || answer.contains("fuck") || answer.contains("shit") || answer.contains("arse") || answer.contains("bollocks") || answer.contains("bugger") || answer.contains("ass") || answer.contains("crap") || answer.contains("bitch") || answer.contains("bastard") || answer.contains("cunt") || answer.contains("twat") || answer.contains("boobs") || answer.equals("tits") || answer.equals("tit") || answer.contains("bellend") || answer.contains("cock") || answer.contains("clunge") || answer.contains("minge") || answer.contains("prick") || answer.contains("dildo") || answer.contains("jizz") || answer.contains("slag") || answer.contains("slut") || answer.contains("whore") || answer.contains("shag") || answer.equals("sex") || answer.contains("knob") || answer.contains("wank");
    }

    private boolean runClock(EmbedBuilder embed, Message embedMessage)
    {
        try
        {
            String[] clockURLs = {"https://i.imgur.com/llmbGHa.png", "https://i.imgur.com/JpwwNrY.png", "https://i.imgur.com/F2deeEY.png", "https://i.imgur.com/eMrORg8.png"};
            for (int i = 0; i<4; i++)
            {
                Thread.sleep(7500);
                embed.setThumbnail(clockURLs[i]);
                embedMessage.editMessage(embed.build()).queue();
            }
            return true; //Clock has successfully run its course.
        }
        catch (InterruptedException e)
        {
            //Game has been quit
            return false; //Clock was interrupted.
        }
    }

    private void checkForQuit(MessageManager mm, Thread gameThread, TextChannel channel)
    {
        while (!Thread.interrupted())
        {
            Message msg = mm.getNextMessage(channel);
            if (msg != null && msg.getContentDisplay().equals(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                gameThread.interrupt();
                Thread.currentThread().interrupt();
            }
        }
    }
}
