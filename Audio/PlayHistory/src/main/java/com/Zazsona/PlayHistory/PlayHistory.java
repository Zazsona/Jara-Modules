package com.Zazsona.PlayHistory;

import audio.Audio;
import audio.ScheduledTrack;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayHistory extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Audio audio = CmdUtil.getGuildAudio(msgEvent.getGuild().getId());
        int trackCount = (audio.getTrackHistory().size() > 10) ? 10 : audio.getTrackHistory().size();
        StringBuilder trackListBuilder = new StringBuilder();
        StringBuilder entryBuilder = new StringBuilder();
        if (trackCount > 0)
        {
            for (int i = 1; i<(trackCount+1); i++)
            {
                ScheduledTrack st = audio.getTrackHistory().get(audio.getTrackHistory().size()-i);
                entryBuilder.append("**").append(st.getAudioTrack().getInfo().title).append("**\n");
                entryBuilder.append(msgEvent.getGuild().getMemberById(st.getUserID()).getEffectiveName()).append("\n");
                entryBuilder.append(st.getAudioTrack().getInfo().uri).append("\n");
                if (trackListBuilder.length() + entryBuilder.length() < 1020)
                {
                    trackListBuilder.append(entryBuilder.toString()).append("\n");
                    entryBuilder.setLength(0);
                }
                else
                {
                    break;
                }
            }
        }
        else
        {
            trackListBuilder.append("No audio has been played.");
        }


        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/wHdSqH5.png");
        embed.setDescription(trackListBuilder.toString());
        msgEvent.getChannel().sendMessage(embed.build()).queue();

    }
}
