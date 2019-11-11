package com.Zazsona.Madlibs;

import commands.CmdUtil;
import configuration.SettingsUtil;
import jara.MessageManager;
import module.ModuleGameCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;

public class Madlibs extends ModuleGameCommand
{
    /**
     * A count for how many libs there are. Make sure to update this when adding new madlibs.
     */
    private final int LIBCOUNT = 14;

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        TextChannel channel = super.createGameChannel(msgEvent.getChannel(), msgEvent.getMember().getEffectiveName()+"s-madlib");
        Random r = new Random();
        int madlibID = r.nextInt(LIBCOUNT);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setDescription("Welcome to Madlibs! Time to make a story.\nCan I get a...");
        channel.sendMessage(embed.build()).queue();

        String[] wordSelections = getWordSelections(getWordTypes(madlibID), channel);
        String story = buildStory(madlibID, wordSelections);

        embed.setDescription(story);
        channel.sendMessage(embed.build()).queue();
        super.deleteGameChannel();

    }

    private String[] getWordTypes(int index)
    {
        String[][] wordTypes = new String[][] {
                {"Adjective", "Adjective", "Adjective", "Adverb", "Noun", "Noun", "Noun", "Noun", "Noun", "Noun", "Plural noun"},
                {"Adjective", "Adjective", "Noun",  "Noun", "Noun", "Noun", "Body part", "Plural noun",  "Plural noun", "Plural noun", "Verb", "Verb", "Verb"},
                {"Verb", "Verb", "Adjective", "Adjective", "Adjective", "Adjective", "Adjective", "Plural noun", "Plural noun", "Plural noun", "Plural noun", "Plural noun", "Verb ending in 'ing'", "Number", "Plural relative"},
                {"Noun", "Noun", "Verb", "Body part", "Food"},
                {"Adjective", "Adjective", "Adjective", "Adverb", "Name", "Noun", "Noun", "Noun", "Number", "Number", "Plural noun", "Plural noun", "Verb", "Verb"},
                {"Adjective", "Noun", "Animal", "Noise"},
                {"Adjective", "Noun", "Noun", "Plural noun", "Verb ending in 'ed'"},
                {"Name", "Any word", "Any word", "Illness", "Plural noun", "Adjective", "Adjective", "Adjective", "Num1", "Place"},
                {"Noun", "Special Day", "Adjective"},
                {"Verb", "Noun"},
                {"Noun", "Plural noun", "Period of time", "Noun"},
                {"Verb"},
                {"Person", "Plural noun"},
                {"Meal", "Noun", "Verb ending in ing", "Adjective", "Person"},
                {"Noun", "Place", "Place", "Noun", "Plural Noun"}
        };
        return wordTypes[index];
    }

    private String[] getWordSelections(String[] wordTypes, TextChannel channel)
    {
        MessageManager mm = new MessageManager();
        String[] wordSelections = new String[wordTypes.length];
        for (int i = 0; i<wordTypes.length; i++)
        {
            channel.sendMessage(wordTypes[i]).queue();
            Message message = mm.getNextMessage(channel);
            wordSelections[i] = message.getContentDisplay().replaceAll("'", "''");

            if (wordSelections[i].toLowerCase().equals(SettingsUtil.getGuildCommandPrefix(channel.getGuild().getId())+"quit"))
            {
                channel.sendMessage("I'll try and fill in what's left!").queue();
                return handleQuit(wordSelections, i);
            }
        }
        return wordSelections;
    }

    private String[] handleQuit(String[] wordSelections, int currentIndex)
    {
        for (int i = currentIndex; i<wordSelections.length; i++)
        {
            try
            {
                wordSelections[i] = CmdUtil.getRandomWord(true).replaceAll("'", "''");
            }
            catch (IOException e)
            {
                LoggerFactory.getLogger(getClass()).error(e.getMessage());
                wordSelections[i] = "Thingy";
            }

        }
        return wordSelections;
    }

    private String buildStory(int index, String[] variables)
    {
        String story = "";
        switch (index)
        {
            case 0:
                story = MessageFormat.format("Driving a car can be fun if you follow this **{0}** advice: When approaching a **{4}** on the right, always blow your **{5}**. Before making a **{1}** turn, always stick your **{6}** out of the window. Every 2000 miles, have your **{7}** inspected and your **{8}** checked. When approaching a school, watch out for **{2}** **{10}** Above all, drive **{3}** The **{9}** you save may be your own!".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 1:
                story = MessageFormat.format("I spent last summer on my grandfather's **{0}** farm. He raises oats, wheat, and **{2}**s. Grandfather also grows lettuce, corn, and lima **{7}**. My favorite place to **{10}** on the farm is the **{3}** house where Grandfather keeps his **{2}** chickens. Every day, each hen lays round, smooth **{8}**s. Grandfather sells most of them, but keeps some so the hens can **{11}** on them and hatch cute, fuzzy little **{9}**! I'm looking forward to next year, when my **{6}** is better, and Grandfather is going to show me how to drive his **{4}**, sow the **{5}**, and **{12}** the cow.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 2:
                story = MessageFormat.format("Come **{0}** at Poundland, where you`ll receive **{2}** discounts on all of your favorite brand names **{7}**. Our **{3}** and **{12}** associates are there to **{1}** you **{13}** hours a day. Here you will find **{4}** prices on the **{8}** you need. **{9}** for the moms, **{10}** for the kids and all the latest electronics for the **{14}**. So come on down to your **{5}** **{6}** Poundland where the **{11}** come first.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 3:
                story = MessageFormat.format("According to all known laws of aviation, there is no way a **{0}** should be able to **{2}**. Its **{3}** is too small to get its fat little body off the ground. The **{0}**, of course, **{2}**s anyway because **{0}**s don't care what **{1}** think is impossible.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 4:
                story = MessageFormat.format("I have known **{4}** for **{8}** years and **{3}** recommend him/her for the position of assistant **{5}** in your **{5}** company. I can't **{12}** enough about **{4}**'s **{1}** character and ability to get along with his/her fellow **{10}**. As for educational background, **{4}** is a college **{6}**, is capable of speaking several foreign **{11}**, and has an IQ of **{9}**. You will find **{4}** to be a **{2}** worker who is not only as smart as a **{7}**, but who doesn't know the meaning of the word **{13}**. Unfortunately, this is one of many words **{4}** doesn't know the meaning of.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 5:
                story = MessageFormat.format("**{0}** MacDonald had a **{1}**, E-I-E-I-O\r\n and on that **{1}** he had a **{2}**, E-I-E-I-O\r\n with a **{3}** **{3}** here\r\n and a **{3}** **{3}** there,\r\n here a **{3}**, there a **{3}**,\r\n everywhere a **{3}** **{3}**,\r\n **{0}** MacDonald had a **{1}**, E-I-E-I-O.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 6:
                story = MessageFormat.format("There was an **{0}** woman who lived in a **{1}**. \r\n She had so many **{3}** she didn't know what to do. \r\n She gave them some broth without any **{2}**. \r\n She **{4}** them all soundly and put them to bed.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 7:
                story =  MessageFormat.format("Dear School Nurse,\r\n **{0}** **{1}** will not be attending school today. He has come down with a case of **{3}** and has horrible **{4}** and a **{5}** fever. We have made an appointment with the **{6}** Dr. **{2}**, who studied for many years in **{9}** and has **{8}** degrees in pediatrics. He will send you all the information you need. Thank you!\r\n Sincerely,\r\n Mrs. **{7}**.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 8:
                story =  MessageFormat.format("If you're going to challenge a **{0}** to a dance off at **{1}** make sure they're more **{2}** than you!".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 9:
                story =  MessageFormat.format("My favourite way to flirt is to **{0}** and ask if I can have a bite of their **{1}**.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 10:
                story =  MessageFormat.format("My gym locker stinks because I'm always leaving my **{0}** and **{1}** in there! I haven't been in over **{2}** though. I wonder if they've merged and evolved to become **{3}**!!!".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 11:
                story =  MessageFormat.format("This hot weather makes me want to **{0}** on the beach all day long.".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 12:
                story =  MessageFormat.format("**{0}**'s dirty socks smell like rotten **{1}**!".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 13:
                story =  MessageFormat.format("Oh egads, my **{0}** is ruined! But what if... I were to purchase **{1}** and disguise it as my own **{2}**? Oh ho ho ho ho... delightfully **{3}**, **{4}**!".replaceAll("'", "''"), (Object[]) variables);
                break;
            case 14:
                story =  MessageFormat.format("Welcome to the delayed **{0}** train service to **{1}** calling at **{2}** and a **{3}**. We apologise for the delay, this is due to three **{4}** blocking the tracks.".replaceAll("'", "''"), (Object[]) variables);
                break;

                //TODO: Don't forget to update LIBCOUNT!
        }
        return story;
    }
}
