package com.zazsona.wwtbam;

import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WWTBAMCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        GameDriver gameDriver = new GameDriver(msgEvent.getChannel(), msgEvent.getGuild().getSelfMember(), msgEvent.getMember());
        gameDriver.Start();
    }
}
