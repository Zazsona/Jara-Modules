package com.Zazsona.QuoteDelete;

import com.Zazsona.Quote.FileManager;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DeleteQuote extends ModuleCommand
{
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (parameters.length > 2)
        {
            for (int i = 2; i<parameters.length; i++)
            {
                parameters[1] += " "+parameters[i];
            }
        }

        if (parameters.length > 1)
        {
            FileManager fm = new FileManager(msgEvent.getGuild().getId());
            boolean success = fm.deleteQuote(msgEvent.getGuild().getId(), parameters[1]);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            if (success)
                embed.setDescription("Quote \""+parameters[1]+"\" deleted.");
            else
                embed.setDescription("Error: That quote doesn't exist.");

            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }
    }
}
