import audio.Audio;
import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Play extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        TextChannel channel = msgEvent.getChannel();

        if (parameters.length >= 2)
        {
            audio.playWithFeedback(msgEvent.getMember(), parameters[1], channel);
        }
        else
        {
            audio.playWithFeedback(msgEvent.getMember(), "", channel);
        }
    }
}
