import audio.Audio;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.CmdUtil;
import configuration.SettingsUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Replay extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        AudioPlayer player = audio.getPlayer();
        if (parameters.length > 1)
        {
            if (parameters[1].equalsIgnoreCase("current") || parameters[1].equalsIgnoreCase("this"))
            {
                if (player.getPlayingTrack() != null)
                {
                    AudioTrack track = player.getPlayingTrack();
                    queueTrackForReplay(msgEvent.getMember(), msgEvent.getChannel(), audio, player, track, parameters);
                }
                else
                {
                    sendReplayMessage(msgEvent, "There is no currently playing track to replay.");
                }
                return;
            }
            else if (parameters[1].equalsIgnoreCase("last") || parameters[1].equalsIgnoreCase("previous"))
            {
                if (audio.getTrackHistory().size() > 0)
                {
                    int indexOffset = 1;
                    AudioTrack track = audio.getTrackHistory().get(audio.getTrackHistory().size()-indexOffset).getAudioTrack();
                    queueTrackForReplay(msgEvent.getMember(), msgEvent.getChannel(), audio, player, track, parameters);
                }
                else
                {
                    sendReplayMessage(msgEvent, "There is no previous track to replay.");
                }
                return;
            }
        }
        if (audio.getTrackHistory().size() > 0 || audio.isAudioPlayingInGuild())
        {
            AudioTrack track = (audio.isAudioPlayingInGuild()) ? player.getPlayingTrack() : audio.getTrackHistory().get(audio.getTrackHistory().size()-1).getAudioTrack();
            queueTrackForReplay(msgEvent.getMember(), msgEvent.getChannel(), audio, player, track, parameters);
        }
        else
        {
            sendReplayMessage(msgEvent, "There is no track to replay.");
        }

    }

    private void queueTrackForReplay(Member member, TextChannel channel, Audio audio, AudioPlayer player, AudioTrack track, String[] parameters)
    {
        /*
                Here we limit the queuing to the user's remaining queue space if it is smaller than the amount specified.
                While this may seem redundant, as Jara blocks additional queue requests, it isn't.

                This is because if hundreds of replays are requested, it may take multiple minutes to load the tracks, long enough for the first track to end.
                When this track does end, then the user has a spot free queue spot, and this will instantly be filled if tracks are still being loaded in, effectively allowing them to block the queue.
         */
        int replayCount = getReplayCount(parameters);
        int remainingQueue = SettingsUtil.getGuildSettings(channel.getGuild().getId()).getAudioQueueLimit(member)-audio.getUserQueueQuantity().get(member.getUser().getId());
        int replaysToQueue = (replayCount < remainingQueue) ? replayCount : remainingQueue;
        audio.playWithFeedback(member, track.getInfo().uri, channel);
        for (int i = 1; i <replaysToQueue ; i++)
        {
            audio.play(member, player.getPlayingTrack().getInfo().uri);
        }
    }

    private void sendReplayMessage(GuildMessageReceivedEvent msgEvent, String s)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        embed.setTitle("Now Playing...");
        embed.setDescription(s);
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getReplayCount(String[] parameters)
    {
        if (parameters[parameters.length-1].matches("[0-9]+"))
        {
            return Integer.parseInt(parameters[parameters.length-1]);
        }
        else
        {
            return 1;
        }
    }
}
