package com.Zazsona.wwtbam;

import module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class WWTBAMCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        GameDriver gameDriver = new GameDriver(msgEvent.getChannel(), msgEvent.getGuild().getSelfMember(), msgEvent.getMember());
        gameDriver.Start();
    }
}
