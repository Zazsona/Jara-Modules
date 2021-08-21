package com.zazsona.wwtbam.lifelines;

import com.zazsona.wwtbam.Trivia;
import com.zazsona.wwtbam.TriviaDatabase;
import net.dv8tion.jda.api.entities.TextChannel;

public class FiftyFifty
{
    public static void useFiftyFifty(TextChannel channel, TriviaDatabase tdb, Trivia trivia)
    {
        channel.sendMessage("Computer, take away two wrong answers.").queue();
        trivia.activateFiftyFifty();
        channel.sendMessage(trivia.getEmbed().build()).queue();
    }
}
