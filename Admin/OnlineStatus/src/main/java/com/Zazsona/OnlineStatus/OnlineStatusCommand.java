package com.Zazsona.OnlineStatus;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class OnlineStatusCommand extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            String presence = parameters[1].toLowerCase();
            switch (presence)
            {
                case "online":
                    msgEvent.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
                    break;
                case "away":
                case "idle":
                    msgEvent.getJDA().getPresence().setStatus(OnlineStatus.IDLE);
                    break;
                case "dnd":
                case "do not disturb":
                    msgEvent.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "offline":
                case "invisible":
                    msgEvent.getJDA().getPresence().setStatus(OnlineStatus.INVISIBLE);
                    break;
            }

        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }

    }
}
