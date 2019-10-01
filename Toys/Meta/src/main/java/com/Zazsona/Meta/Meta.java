package com.Zazsona.Meta;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class Meta extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("```M  E  T  A\r\n\r\n" +
                                            "E\r\n\r\n" +
                                            "T\r\n\r\n" +
                                            "A```");
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
