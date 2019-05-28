import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Replay extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        AudioPlayer player = CmdUtil.getGuildAudio(msgEvent.getGuild().getId()).getPlayer();
        if (player.getPlayingTrack() != null)
        {
            CmdUtil.getGuildAudio(msgEvent.getGuild().getId()).playWithFeedback(msgEvent.getMember(), player.getPlayingTrack().getInfo().uri, msgEvent.getChannel());
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
            embed.setTitle("Now Playing...");
            embed.setDescription("No track is currently playing.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
