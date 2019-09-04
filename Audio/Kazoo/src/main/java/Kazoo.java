import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Random;

public class Kazoo extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        String[] links = { "https://youtu.be/z__AXH7GUd0", "https://youtu.be/F_-fd1SL1Mw", "https://youtu.be/E3z6rROsZig", "https://youtu.be/_JSg3izBC3w", "https://www.youtube.com/watch?v=kmHXRkBnSdc", "https://youtu.be/wMyqQWcAuKM", "https://youtu.be/2B8lYWtxjOI", "https://youtu.be/UhCVJIZ9e2g", "https://youtu.be/vmO6-nZQvpE", "https://youtu.be/RvGc79960wQ", "https://youtu.be/Tk9PtssZe6o", "https://youtu.be/YZPPTJzXw2s" };
        Random r = new Random();
        String trackToPlay = links[r.nextInt(links.length)];
        CmdUtil.getGuildAudio(msgEvent.getGuild().getId()).playWithFeedback(msgEvent.getMember(), trackToPlay, msgEvent.getChannel());
    }
}
