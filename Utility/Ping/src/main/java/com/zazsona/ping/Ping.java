package com.zazsona.ping;

import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ping extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        msgEvent.getChannel().sendMessage("Pong!").queue();
    }
}
