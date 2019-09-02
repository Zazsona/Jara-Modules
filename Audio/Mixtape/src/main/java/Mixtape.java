import audio.Audio;
import commands.CmdUtil;
import commands.Command;
import configuration.SettingsUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;

public class Mixtape extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 1)
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
            embed.setTitle("Mixtape");

            if (SaveManager.isMixtapeNameTaken(msgEvent.getGuild().getIdLong(), parameters[1]))
            {
                Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
                ArrayList<String> tracks = SaveManager.getTracks(msgEvent.getGuild().getIdLong(), parameters[1]);
                Collections.shuffle(tracks);
                Audio.RequestResult rr = null;
                String lastTrack = "";
                int playLimit = getPlayLimit(msgEvent.getMember(), audio, tracks.size());
                for (int i = 0; i<playLimit; i++)
                {
                    rr = audio.play(msgEvent.getMember(), tracks.get(i));
                    lastTrack = tracks.get(i);
                    if (!(rr == Audio.RequestResult.REQUEST_NOW_PLAYING || rr == Audio.RequestResult.REQUEST_ADDED_TO_QUEUE))
                    {
                        break;
                    }
                }
                sendFeedback(msgEvent, embed, rr, lastTrack);
            }
            else
            {
                embed.setDescription("Mixtape not found.");
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getClass());
        }
    }

    private void sendFeedback(GuildMessageReceivedEvent msgEvent, EmbedBuilder embed, Audio.RequestResult rr, String lastTrack)
    {
        switch (rr)
        {
            case REQUEST_ADDED_TO_QUEUE:
            case REQUEST_NOW_PLAYING:
                embed.setDescription("Mixtape queued!");
                break;
            case REQUEST_CHANNEL_FULL:
                embed.setTitle("Channel Full");
                embed.setDescription("There's no space for me in that channel.");
                break;
            case REQUEST_IS_BAD:
                embed.setTitle("No Track Found");
                embed.setDescription(lastTrack+" is not a supported URL.");
                break;
            case REQUEST_NO_LINK:
                embed.setTitle("No Tracks");
                embed.setDescription("This mixtape is empty.");
                break;
            case REQUEST_RESULTED_IN_ERROR:
                embed.setTitle("Error");
                embed.setDescription("An unexpected error occurred. Please try again. If the error persists, please notify your server owner.");
                break;
            case REQUEST_USER_NOT_IN_VOICE:
                embed.setTitle("No Voice Channel Found");
                embed.setDescription("I can't find you in any voice channels! Please make sure you're in one I have access to.");
                break;
            case REQUEST_CHANNEL_PERMISSION_DENIED:
                embed.setTitle("Permission Denied");
                embed.setDescription("I can't find you in any voice channels! Please make sure you're in one I have access to.");
                break;
            case REQUEST_USER_LIMITED:
                embed.setDescription("Mixtape queued! (Your queue is full, though, so only some tracks will play)");
                break;
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getPlayLimit(Member member, Audio audio, int trackCount)
    {
        /*
                Here we limit the queuing to the user's remaining queue space if it is smaller than the amount specified.
                While this may seem redundant, as Jara blocks additional queue requests, it isn't.

                This is because if hundreds of replays are requested, it may take multiple minutes to load the tracks, long enough for the first track to end.
                When this track does end, then the user has a spot free queue spot, and this will instantly be filled if tracks are still being loaded in, effectively allowing them to block the queue.
         */
        int remainingQueue = SettingsUtil.getGuildSettings(member.getGuild().getId()).getAudioQueueLimit(member)-audio.getUserQueueQuantity().get(member.getUser().getId());
        int tracksToPlay = (trackCount < remainingQueue) ? trackCount : remainingQueue;
        return tracksToPlay;
    }
}
