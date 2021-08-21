package com.zazsona.quoterecall;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.quote.FileManager;
import com.zazsona.quote.Quote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class RecallQuote extends ModuleCommand
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

        if (parameters.length >= 2)
        {
            FileManager fm = new FileManager(msgEvent.getGuild().getId());
            Quote quote = fm.getQuoteByName(parameters[1]);
            EmbedBuilder embed = new EmbedBuilder();
            if (quote != null)
            {
                embed = formatQuote(msgEvent.getGuild().getSelfMember(), quote);
                if (quote.attachmentUrl != null)
                {
                    embed.setTitle(quote.name, quote.attachmentUrl);
                    if (quote.attachmentUrl.endsWith(".png") || quote.attachmentUrl.endsWith(".jpg") || quote.attachmentUrl.endsWith(".gif"))
                    {
                        embed.setImage(quote.attachmentUrl);
                    }
                    else
                    {
                        embed.getDescriptionBuilder().append("\n\n").append(quote.attachmentUrl);
                    }
                }
            }
            else
            {
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setDescription("Error: Unable to locate quote.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
        else
        {
            CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
        }

    }

    private EmbedBuilder formatQuote(Member selfMember, Quote quote)
    {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(quote.timestamp), SettingsUtil.getGuildSettings(selfMember.getGuild().getId()).getTimeZoneId());
        EmbedBuilder quoteEmbed = new EmbedBuilder();
        quoteEmbed.setColor(CmdUtil.getHighlightColour(selfMember));
        quoteEmbed.setTitle("===== "+quote.name+" =====");
        quoteEmbed.setDescription(quote.message+"\n\n~"+quote.user);
        quoteEmbed.setFooter(zdt.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")), null);
        return quoteEmbed;
    }

}
