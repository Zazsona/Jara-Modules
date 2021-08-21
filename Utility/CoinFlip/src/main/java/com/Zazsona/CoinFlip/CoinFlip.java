package com.zazsona.coinflip;

import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class CoinFlip extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Random r = new Random();
        boolean isHeads = (r.nextBoolean());
        if (isHeads)
        {
            msgEvent.getChannel().sendMessage("It's heads!").queue();
        }
        else
        {
            msgEvent.getChannel().sendMessage("It's tails!").queue();
        }
    }
}
