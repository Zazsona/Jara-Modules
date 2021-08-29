package com.zazsona.beatthecores;

import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BeatTheCoresCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... strings)
    {
        GameDriver gameDriver = new GameDriver(msgEvent.getChannel(), msgEvent.getGuild().getSelfMember(), msgEvent.getMember());
        gameDriver.start();
    }
}
