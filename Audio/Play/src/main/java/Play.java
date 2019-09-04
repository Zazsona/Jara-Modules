import audio.Audio;
import commands.CmdUtil;
import configuration.SettingsUtil;
import module.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;

public class Play extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        TextChannel channel = msgEvent.getChannel();

        if (parameters.length >= 2)
        {
            queueTrackForMultiPlay(msgEvent.getMember(), channel, audio, parameters[1], parameters);
        }
        else
        {
            audio.playWithFeedback(msgEvent.getMember(), "", channel);
        }
    }

    private void queueTrackForMultiPlay(Member member, TextChannel channel, Audio audio, String query, String[] parameters)
    {
        /*
                Here we limit the queuing to the user's remaining queue space if it is smaller than the amount specified.
                While this may seem redundant, as Jara blocks additional queue requests, it isn't.

                This is because if hundreds of replays are requested, it may take multiple minutes to load the tracks, long enough for the first track to end.
                When this track does end, then the user has a spot free queue spot, and this will instantly be filled if tracks are still being loaded in, effectively allowing them to block the queue.
         */
        int replayCount = getReplayCount(parameters);
        HashMap<String, Integer> queuedItems = audio.getUserQueueQuantity();
        int userMaxQueueSize = SettingsUtil.getGuildSettings(channel.getGuild().getId()).getAudioQueueLimit(member);
        int remainingQueue = (queuedItems.containsKey(member.getUser().getId())) ? userMaxQueueSize-queuedItems.get(member.getUser().getId()) : userMaxQueueSize;
        int replaysToQueue = (replayCount < remainingQueue) ? replayCount : remainingQueue;
        audio.playWithFeedback(member, query, channel);
        for (int i = 1; i <replaysToQueue ; i++)
        {
            audio.play(member, query);
        }
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
