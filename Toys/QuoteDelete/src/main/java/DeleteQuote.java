import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DeleteQuote extends Command
{
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 2)
        {
            for (int i = 2; i<parameters.length; i++)
            {
                parameters[1] += " "+parameters[i];
            }
        }

        if (parameters.length > 1)
        {
            FileManager fm = new FileManager(msgEvent.getGuild().getId());
            boolean success = fm.deleteQuote(msgEvent.getGuild().getId(), parameters[1]);
            if (success)
                msgEvent.getChannel().sendMessage("Quote \""+parameters[1]+"\" deleted.").queue();
            else
                msgEvent.getChannel().sendMessage("Error: That quote doesn't exist.").queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }
}
