package com.zazsona.nowplaying;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.zazsona.jara.audio.Audio;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class NowPlaying extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setTitle("Now Playing...");
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());

        AudioTrack currentTrack = audio.getPlayer().getPlayingTrack();
        if (currentTrack == null)
        {
            embed.setDescription("No track is currently playing.");
        }
        else
        {
            if (currentTrack.getInfo().uri.startsWith("https://www.youtube.com"))
            {
                embed.setThumbnail("https://img.youtube.com/vi/"+currentTrack.getInfo().identifier+"/mqdefault.jpg");
            }
            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append("Progress: ").append(CmdUtil.formatMillisecondsToHhMmSs(currentTrack.getPosition())).append("/").append(CmdUtil.formatMillisecondsToHhMmSs(currentTrack.getDuration()));
            descBuilder.append("\n");
            descBuilder.append("URL: ").append(currentTrack.getInfo().uri).append("\n");
            descBuilder.append("=====\n");
            descBuilder.append(CmdUtil.formatAudioTrackDetails(currentTrack));
            embed.setDescription(descBuilder.toString());
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
