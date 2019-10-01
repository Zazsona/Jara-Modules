package com.Zazsona.QuoteRandom;

import module.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Random;

public class RandomQuote extends Command
{

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        FileManager fm = new FileManager(msgEvent.getGuild().getId());
        ArrayList<Quote> quotes = fm.getQuotes();
        if (quotes.size() > 0)
        {
            new RecallQuote().run(msgEvent, new String[]{"", quotes.get(new Random().nextInt(quotes.size())).name});
        }
        else
        {
            msgEvent.getChannel().sendMessage("You don't have any quotes!").queue();
        }
    }
}
