package com.zazsona.countdownconundrum;

import com.zazsona.jara.MessageManager;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class CountdownConundrum extends ModuleGameCommand
{
    private static Logger logger = LoggerFactory.getLogger(CountdownConundrum.class);
    /**
     * The time it took to guess the conundrum
     */
    private int seconds;
    /**
     * The embed
     */
    private EmbedBuilder embed;
    /**
     * The message containing the embed
     */
    private Message embedMsg;

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName() + "s-countdown-conundrum");
        try
        {
            String winner = "";
            String[] conundrum = getConundrum();
            Random r = new Random();
            embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setTitle("Countdown Conundrum");
            embed.setThumbnail("https://i.imgur.com/0uNRZWG.png");
            embed.setDescription("Find the anagram before time runs out!\n\n***"+conundrum[1].toUpperCase()+"***");
            embedMsg = channel.sendMessage(embed.build()).complete();

            Runnable timer = () ->
            {
                seconds = 0;
                while (seconds <= 30)
                {
                    try
                    {
                        switch (seconds)
                        {
                            case 30:
                                embed.setThumbnail("https://i.imgur.com/eMrORg8.png");
                                embedMsg.editMessage(embed.build()).queue();
                                break;
                            case 23:
                                embed.setThumbnail("https://i.imgur.com/F2deeEY.png");
                                embedMsg.editMessage(embed.build()).queue();
                                break;
                            case 15:
                                embed.setThumbnail("https://i.imgur.com/JpwwNrY.png");
                                embedMsg.editMessage(embed.build()).queue();
                                break;
                            case 8:
                                embed.setThumbnail("https://i.imgur.com/llmbGHa.png");
                                embedMsg.editMessage(embed.build()).queue();
                                break;

                        }
                        Thread.sleep(1000);
                        seconds++;
                    }
                    catch (InterruptedException e)
                    {
                        //Game end.
                        return;
                    }

                }
            };
            Thread timerThread = new Thread(timer);
            timerThread.start();
            MessageManager msgManager = new MessageManager();
            while (seconds <= 30)
            {
                Message message = msgManager.getNextMessage(channel, 1000);
                if (message != null)
                {
                    String answer = message.getContentDisplay();

                    if (answer.length() == 5 && answer.toLowerCase().endsWith("quit"))
                    {
                        timerThread.interrupt();
                    }

                    if (answer.equalsIgnoreCase(conundrum[0]))
                    {
                        winner = message.getMember().getEffectiveName();
                        timerThread.interrupt();
                        break;
                    }
                    else if (answer.length() == 9)
                    {
                        channel.sendMessage("Good guess, but that's not it.").queue();
                    }
                }
            }

            if (!winner.equals(""))
            {
                embed.setThumbnail("https://i.imgur.com/scKHMRb.png");
                embed.setDescription("Congratulations! The word was **"+conundrum[0]+"**.\n**"+winner+"** got it in just "+seconds+" seconds!");
                channel.sendMessage(embed.build()).queue();
            }
            else
            {
                embed.setThumbnail("https://i.imgur.com/KwjqNkH.png");
                embed.setDescription("You were beaten by the clock! The word was **"+conundrum[0]+"**. Better luck next time!");
                channel.sendMessage(embed.build()).queue();
            }
        }
        catch (IOException e)
        {
            channel.sendMessage("An unexpected error occured.").queue();
            e.printStackTrace();
        }
        super.deleteGameChannel();
    }

    /**
     * Gets the list of conundrum, where the first array index is the answer, and the second is the anagram.
     * @return String[][] - The conundrums
     */
    private String[] getConundrum() throws IOException
    {
        Random r = new Random();
        String[] conundrum = new String[2];
        conundrum[0] = Conundrums.conundrums[r.nextInt(Conundrums.conundrums.length)];

        byte[] conundrumChars = conundrum[0].getBytes();
        for (int i = conundrumChars.length - 1; i > 0; i--)
        {
            int charIndex = r.nextInt(i + 1);       //This will eventually go to between 0 or 1, however those values (most likely) will have already been swapped.
            byte temp = conundrumChars[charIndex];
            conundrumChars[charIndex] = conundrumChars[i];
            conundrumChars[i] = temp;
        }
        conundrum[1] = new String(conundrumChars, StandardCharsets.UTF_8);

        return conundrum;
    }

}
