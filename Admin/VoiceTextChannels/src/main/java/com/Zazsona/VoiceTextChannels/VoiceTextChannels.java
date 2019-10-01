package com.Zazsona.VoiceTextChannels;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class VoiceTextChannels extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        FileManager fm = VoiceTextChannelsLoader.getFileManager();
        toggleGuild(msgEvent, fm);
    }

    private void toggleGuild(GuildMessageReceivedEvent msgEvent, FileManager fm)
    {
        boolean isGuildEnabled = fm.isGuildEnabled(msgEvent.getGuild().getId());
        if (isGuildEnabled)
        {
            fm.disableGuild(msgEvent.getGuild().getId());
        }
        else
        {
            fm.enableGuild(msgEvent.getGuild().getId());
        }

        isGuildEnabled = fm.isGuildEnabled(msgEvent.getGuild().getId());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        if (isGuildEnabled)
        {
            embed.setDescription("Enabled Voice Text Channels.");
        }
        else
        {
            embed.setDescription("Disabled Voice Test Channels.");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
