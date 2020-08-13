package com.Zazsona.LastCommand;

import jara.CommandHandler;
import jara.Core;
import module.ModuleCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class LastCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        HistoricCommand historicCommand = LastCommandListener.getInstance().getLastCommand(msgEvent.getAuthor().getId());
        Core.getCommandHandler().execute(msgEvent, historicCommand.getAttributes(), historicCommand.getParameters()); //We pass the new msgEvent to get the current channel and contexts.
    }
}
