package com.zazsona.quoteslist;

import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.jara.module.ModuleCommand;
import com.zazsona.quote.FileManager;
import com.zazsona.quote.Quote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ListQuotes extends ModuleCommand
{

    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        int pageNo = getPageNo(parameters);
        int listingsPerPage = 25;
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        FileManager fm = new FileManager(msgEvent.getGuild().getId());
        ArrayList<Quote> quotes = fm.getQuotes();

        if (quotes.size() > 0)
        {
            ZoneId zoneID = SettingsUtil.getGuildSettings(msgEvent.getGuild().getId()).getTimeZoneId();
            double totalPages = Math.ceil((double) quotes.size()/(double) listingsPerPage);
            int startingIndex = (listingsPerPage*pageNo)-listingsPerPage;
            int endIndex = (quotes.size() < startingIndex+listingsPerPage) ? quotes.size() : (startingIndex+listingsPerPage);

            StringBuilder listBuilder = new StringBuilder();
            for (int i = startingIndex; i<endIndex; i++)
            {
                listBuilder.append("**").append(quotes.get(i).name).append("** - ").append(ZonedDateTime.ofInstant(Instant.ofEpochSecond(quotes.get(i).timestamp), zoneID).format(DateTimeFormatter.ofPattern("YYYY-MM-dd"))).append("\n");
            }
            embed.setDescription(listBuilder.toString());
            embed.setFooter("Page "+pageNo+" / "+(int) totalPages, null);
        }
        else
        {
            embed.setDescription("You have no quotes!");
        }
        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }

    private int getPageNo(String[] parameters)
    {
        if (parameters.length > 1)
        {
            if (parameters[1].matches("[0-9]+"))
            {
                return Integer.parseInt(parameters[1]);
            }
        }
        return 1;
    }
}
