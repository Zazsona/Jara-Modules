import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class IdleComments extends Command
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
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(OnlineStatus.ONLINE);
                    break;
                case "away":
                case "idle":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(OnlineStatus.IDLE);
                    break;
                case "dnd":
                case "do not disturb":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "offline":
                case "invisible":
                    CmdUtil.getJDA().asBot().getShardManager().setStatus(OnlineStatus.INVISIBLE);
                    break;
            }

        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }

    }
}
