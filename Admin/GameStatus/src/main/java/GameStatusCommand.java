import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class GameStatusCommand extends Command
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
                CmdUtil.getJDA().asBot().getShardManager().setGame(Game.playing(message.toString()));
            }
            else
            {
                CmdUtil.getJDA().asBot().getShardManager().setGame(null);
            }
        }
        catch (IllegalArgumentException e)
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }

    }
}
