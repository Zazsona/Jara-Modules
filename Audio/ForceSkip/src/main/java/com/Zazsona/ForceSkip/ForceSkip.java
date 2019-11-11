package com.Zazsona.ForceSkip;

import audio.Audio;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ForceSkip extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        TextChannel tChannel = msgEvent.getChannel();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        embed.setTitle("Skip Track");

        if (audio.isAudioPlayingInGuild())
        {
            if (parameters.length > 1)
            {
                if (parameters[1].equalsIgnoreCase("all"))
                {
                    audio.getTrackQueue().clear();
                }
            }
            embed.setDescription("Forcibly skipping track.");
            audio.getPlayer().stopTrack();
            audio.getPlayer().setPaused(false);
            audio.resetSkipVotes();
        }
        else if(!audio.isAudioPlayingInGuild())
        {
            embed.setDescription("No track is currently playing.");
        }

        tChannel.sendMessage(embed.build()).queue();
    }
}
