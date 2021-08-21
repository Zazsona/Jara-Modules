package com.zazsona.pause;

import com.zazsona.jara.audio.Audio;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Pause extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        embed.setTitle("Pause");

        if (audio.getPlayer().getPlayingTrack() != null)
        {
            boolean paused = audio.getPlayer().isPaused();
            audio.getPlayer().setPaused(!paused);

            if (paused)
                embed.setDescription(audio.getPlayer().getPlayingTrack().getInfo().title+" resumed.");
            else
                embed.setDescription(audio.getPlayer().getPlayingTrack().getInfo().title+" paused.");
        }
        else
        {
            embed.setDescription("No track is currently playing.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();

    }
}
