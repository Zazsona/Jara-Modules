package com.Zazsona.GameStatus;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GameStatusCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        try
        {
            if (parameters.length > 1)
            {
                StringBuilder message = new StringBuilder();
                for (int i = 1; i<parameters.length; i++)
                {
                    message.append(parameters[i]).append(" ");
                }
                msgEvent.getJDA().getPresence().setActivity(Activity.playing(message.toString()));
            }
            else
            {
                msgEvent.getJDA().getPresence().setActivity(null);
            }
        }
        catch (IllegalArgumentException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }

    }
}
