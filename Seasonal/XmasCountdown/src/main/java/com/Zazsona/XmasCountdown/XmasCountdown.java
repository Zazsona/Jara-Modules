package com.Zazsona.XmasCountdown;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class XmasCountdown extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
        if (CmdUtil.getSeason().equals(CmdUtil.Season.WINTER))
        {
            OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
            OffsetDateTime Xmas = OffsetDateTime.of(utc.getYear(), 12, 25, 0, 0, 0, 0, ZoneOffset.UTC);
            int count = Xmas.getDayOfYear() - utc.getDayOfYear();
            if (count >= 30 && count < 200)
            {
                embed.setDescription(":snowflake: "+count+" days to go! ...Don't get too excited. :snowflake:");
            }
            else if (count >= 20 && count < 30)
            {
                embed.setDescription(":snowflake: "+count+" days to go! Less than a month! :snowflake:");
            }
            else if (count >= 15 && count < 20)
            {
                embed.setDescription(":snowflake: Just "+count+" days to go! I think I can hear jingle bells in the distance. :snowflake:");
            }
            else if (count >= 10 && count < 15)
            {
                embed.setDescription(":snowflake: Just "+count+" days to go! Time to get the tree up if you haven't already! :snowflake:");
            }
            else if (count >= 7 && count < 10)
            {
                embed.setDescription(":snowflake: Only "+count+" days to go! That's only just over a week! WhoopWhoop! :snowflake:");
            }
            else if (count >= 4 && count < 7)
            {
                embed.setDescription(":snowflake: Only "+count+" days to go! Getting real close now! :snowflake:");
            }
            else if (count >= 2 && count < 4)
            {
                embed.setDescription(":snowflake: Only "+count+" days to go! Ho ho ho! :snowflake:");
            }
            else if (count == 1)
            {
                embed.setDescription(":santa: Only "+count+" day to go! Make sure you get everything wrapped up! :santa:");
            }
            else if (count == 0)
            {
                embed.setDescription(":santa: Merry Christmas, you filthy animals. :santa:");
            }
            else
            {
                embed.setDescription("It's a long wait yet.");
            }
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
