package com.zazsona.quotedelete;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.quote.FileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

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
