import commands.CmdUtil;
import commands.GameCommand;
import jara.MessageManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class SeasonalTrivia extends GameCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        Random r = new Random();
        String answer = null;
        if (CmdUtil.getSeason().equals(CmdUtil.Season.SPRING))
        {
            String[][] questions = {{"True or false, does Easter sell the most sweets 'n' chocolate than any other holiday?", "false"},
                    {"What country did the Easter Bunny originate in?", "germany"},
                    {"What animal is used instead of a Bunny in Australia?", "bilby"},
                    {"What Easter treat is most popular in America?", "peeps"},
                    {"What British bake is associated with Easter?", "hot cross"},
                    {"What is an Easter bonnet?", "hat"},
                    {"True or false, an annual egg hunt happens at the White House every year?", "true"},
                    {"Which came first, the chicken or the egg?", ""+r.nextInt(5000)+r.nextInt(5000)+r.nextInt(5000)}, //Basically, they're probably not going to be right.
                    {"What notable day can Easter overlap with?", "fools"}};
            embed.setColor(Color.decode("#5de527"));
            int questionId = r.nextInt(questions.length);
            embed.setDescription(questions[questionId][0]);
            answer = questions[questionId][1];
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.SUMMER))
        {
            String[][] questions = {{"The most traditional birthstone for the month of August is what?", "peridot"},
                    {"In the U.S. what is the most popular selling grilling meat throughout the summer?", "hotdog"},
                    {"What day of June does Summer officially begin?", "21"},
                    {"What do you call the longest day?", "solstice"},
                    {"True or false, Summer is the second most thundery time of year?", "false"},
                    {"True or false, in the Summer heat, the Big Ben clock tower grows 27cm taller?", "false"},
                    {"What year were the first Olympic games held?", "1896"},
                    {"Where does the term 'June' come from?", "juno"}};
            embed.setColor(Color.decode("#e1ff00"));
            int questionId = r.nextInt(questions.length);
            embed.setDescription(questions[questionId][0]);
            answer = questions[questionId][1];
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.AUTUMN))
        {
            String[][] questions = {{"What do trees stop producing to make their leaves go orange?", "chlorophyll"},
                    {"In what year will Autumn/Fall come a day late?", "2303"},
                    {"How many hours of daylight are there at the autumnal equinox?", "12"},
                    {"What does the word \"hallow\" mean in relation to Halloween?", "saint"},
                    {"Which colour other than orange is traditionally associated with Halloween?", "black"},
                    {"What does the orange of Halloween represent?", "harvest"},
                    {"What is the fear of Halloween called?", "Samhainophobia"},
                    {"Other than pumpkins, what did people use to carve?", "turnips"},
                    {"True or false, pumpkins can be blue?", "true"}};
            embed.setColor(Color.decode("#f77b00"));
            int questionId = r.nextInt(questions.length);
            embed.setDescription(questions[questionId][0]);
            answer = questions[questionId][1];
        }
        else if (CmdUtil.getSeason().equals(CmdUtil.Season.WINTER))
        {
            String[][] questions = {{"What color is Rudolph's nose?", "red"},
                    {"Where does Santa Claus live?", "north pole"},
                    {"What green character attempted to steal Christmas?", "grinch"},
                    {"What is the best-selling Christmas single of all time?", "do they know its christmas"},
                    {"Who wrote 'A Christmas Carol'?", "dickens"},
                    {"What does Santa give to people on the naughty list?", "coal"},
                    {"What is the name of Bart Simpson's dog?", "santas little helper"},
                    {"The Three Kings bore gifts of myrrh, frankincense, and what else?", "gold"},
                    {"Who did the Ghost of Christmas Past haunt?", "scrooge"},
                    {"The Three Kings' names are Kasper, Melchior, and what's the third?", "magi"},
                    {"What Christmas carol contains the lyrics 'Fa-la-la-la-la-la-la-la-la'?", "deck the halls"},
                    {"What was used for Frosty the Snowman's nose?", "button"},
                    {"What holiday drink contains sugar, milk, and eggs?", "eggnog"},
                    {"Finish the song name. O Come All...", "ye faithful"},
                    {"Finish the song name. God Rest...", "ye merry gentlemen"},
                    {"Finish the song name. Santa Claus is...", "coming to town"},
                    {"What's the gift for the 2nd day of Christmas?", "doves"},
                    {"What's the gift for the 6th day of Christmas?", "geese"},
                    {"What's the gift for the 11th day of Christmas?", "pipers"},
                    {"Macaulay Culkin stars in what Christmas Movie?", "home alone"},
                    {"What Christmas classic features Will Ferrel?", "elf"},
                    {"Based on a Sesame Street character, what was the 'must have' Christmas toy of 1996?", "elmo"},
                    {"Discovered on December 25, 1643, Christmas Island is an external territory of what country?", "australia"},
                    {"What red & green species of plant is native to Mexico and widely used in Christmas floral displays?", "poinsettia"},
                    {"What 17th century English physicist & mathematician was born on Christmas in 1642?", "newton"}
            };
            embed.setColor(Color.decode("#FFFFFF"));
            int questionId = r.nextInt(questions.length);
            embed.setDescription(questions[questionId][0]);
            answer = questions[questionId][1];
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
        boolean isCorrect = getAnswer(msgEvent.getChannel(), answer);
        embed.setDescription(getResultMessage(isCorrect));
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private boolean getAnswer(TextChannel channel, String answer)
    {
        MessageManager mm = new MessageManager();
        Message msg = mm.getNextMessage(channel);
        String msgContent = msg.getContentDisplay().toLowerCase().replaceAll("[.!?\\-]", "");
        return (msgContent.contains(answer.toLowerCase()));
    }

    private String getResultMessage(boolean isCorrect)
    {
        if (isCorrect)
        {
            if (CmdUtil.getSeason().equals(CmdUtil.Season.SPRING))
            {
                return ":hatching_chick: You got it! Nice one. :hatching_chick:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.SUMMER))
            {
                return ":sunny: You're hot on this. Well done. :sunny:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.AUTUMN))
            {
                return ":fallen_leaf: I can't be-leaf it! Well done. :fallen_leaf:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.WINTER))
            {
                return ":snowflake: Ho-Ho-Ho. Nice one. :snowflake:";
            }
        }
        else
        {
            if (CmdUtil.getSeason().equals(CmdUtil.Season.SPRING))
            {
                return ":hatching_chick: Nope! Might need to Spring clean your trivia. :hatching_chick:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.SUMMER))
            {
                return ":sunny: Nope! Heat got you bogged down? :sunny:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.AUTUMN))
            {
                return ":fallen_leaf: Nope! You flopped like a leaf there. :fallen_leaf:";
            }
            else if (CmdUtil.getSeason().equals(CmdUtil.Season.WINTER))
            {
                return ":snowflake: Nope! You're going on the idiot list. :snowflake:";
            }
        }
        return "";
    }

}
