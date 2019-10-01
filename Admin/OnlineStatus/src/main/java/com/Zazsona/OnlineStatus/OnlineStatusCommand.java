package com.Zazsona.OnlineStatus;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class OnlineStatusCommand extends Command
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
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(net.dv8tion.jda.core.OnlineStatus.ONLINE);
                    break;
                case "away":
                case "idle":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(net.dv8tion.jda.core.OnlineStatus.IDLE);
                    break;
                case "dnd":
                case "do not disturb":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(net.dv8tion.jda.core.OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "offline":
                case "invisible":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(net.dv8tion.jda.core.OnlineStatus.INVISIBLE);
                    break;
            }

        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }

    }
}
