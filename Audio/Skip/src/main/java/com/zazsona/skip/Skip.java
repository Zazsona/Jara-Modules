package com.zazsona.skip;

import com.zazsona.jara.audio.Audio;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Skip extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        VoiceChannel vChannel = msgEvent.getMember().getVoiceState().getChannel();
        TextChannel tChannel = msgEvent.getChannel();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        embed.setTitle("Skip Track");

        if (audio.isAudioPlayingInGuild() && vChannel != null)
        {
            StringBuilder descBuilder = new StringBuilder();

            boolean skipRequestAdded = audio.registerSkipVote(msgEvent.getMember().getUser().getId());
            if (skipRequestAdded)
            {
                descBuilder.append("You have voted to skip.\n");
            }
            else
            {
                descBuilder.append("Skip vote cancelled.\n");
            }
            /*
                Let me spell the below operation out, because it's calling a lot of values from various places.
                1. Get the percentage of people who need to vote, as defined in the guild config
                2. Convert it to decimal format
                3. Get the total number of people in the room, ignoring the bot (As it can't vote)
                4. Multiply these together, to get the total number of people who need to vote for a track skip to happen
                5. Round this to a valid integer.
             */
            int skipVotesRequired = Math.round((SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).getTrackSkipPercent()/100.0f)*(vChannel.getMembers().size()-1.0f));
            if (skipVotesRequired == 0) {skipVotesRequired++;} //This is just for formatting, as otherwise it'd appear as "1/0 skips". It has no bearing on functionality.

            descBuilder.append("\n");
            descBuilder.append("Skip Progress: ").append(audio.getSkipVotes()).append("/").append(skipVotesRequired);

            if (audio.getSkipVotes() >= skipVotesRequired)
            {
                descBuilder.append(" (Now skipping)");
                audio.getPlayer().stopTrack();
                audio.getPlayer().setPaused(false);
            }
            embed.setDescription(descBuilder.toString());
        }
        else if (!audio.isAudioPlayingInGuild())
        {
            embed.setDescription("No track is currently playing.");
        }
        else if (vChannel == null)
        {
            embed.setDescription("You must be in the channel to skip the track.");
        }

        tChannel.sendMessage(embed.build()).queue();
    }
}
