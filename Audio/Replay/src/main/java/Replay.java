import audio.Audio;
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
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        AudioPlayer player = audio.getPlayer();
        if (parameters.length <= 1)
        {
            if (audio.getTrackHistory().size() > 0)
            {
                CmdUtil.getGuildAudio(msgEvent.getGuild().getId()).playWithFeedback(msgEvent.getMember(), audio.getTrackHistory().get(audio.getTrackHistory().size()-1).getInfo().uri, msgEvent.getChannel());
            }
            else
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
                embed.setTitle("Now Playing...");
                embed.setDescription("There is no track to replay.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        else
        {
            if (parameters[1].equalsIgnoreCase("current") || parameters[1].equalsIgnoreCase("this"))
            {
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
                    embed.setDescription("There is no currently playing track to replay.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
            }
            else if (parameters[1].equalsIgnoreCase("last") || parameters[1].equalsIgnoreCase("previous"))
            {
                if (audio.getTrackHistory().size() >= 2)
                {
                    int indexOffset = (audio.isAudioPlayingInGuild()) ? 2 : 1;
                    CmdUtil.getGuildAudio(msgEvent.getGuild().getId()).playWithFeedback(msgEvent.getMember(), audio.getTrackHistory().get(audio.getTrackHistory().size()-indexOffset).getInfo().uri, msgEvent.getChannel());
                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                    embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
                    embed.setTitle("Now Playing...");
                    embed.setDescription("There is no previous track to replay.");
                    msgEvent.getChannel().sendMessage(embed.build()).queue();
                }
            }
        }

    }
}
