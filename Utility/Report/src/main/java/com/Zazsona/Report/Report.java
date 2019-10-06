package com.Zazsona.Report;

import commands.CmdUtil;
import configuration.GuildSettings;
import configuration.SettingsUtil;
import module.Command;
import jara.Core;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Report extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        GuildSettings guildSettings = SettingsUtil.getGuildSettings(msgEvent.getGuild().getId());
        StringBuilder reportSB = new StringBuilder();
        reportSB.append("Bot User: ").append(msgEvent.getJDA().getSelfUser().getName()).append("#").append(msgEvent.getJDA().getSelfUser().getDiscriminator()).append("\n");
        reportSB.append("Status: Online\n");
        reportSB.append("Version: ").append(Core.getVersion()).append("\n");
        reportSB.append("Uptime: ").append(CmdUtil.formatMillisecondsToHhMmSs(ManagementFactory.getRuntimeMXBean().getUptime())).append("\n");
        reportSB.append("DateTime: ").append(OffsetDateTime.now(guildSettings.getTimeZoneId()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).append(" (").append(guildSettings.getTimeZoneId().getDisplayName(TextStyle.NARROW_STANDALONE, Locale.ENGLISH)).append(")\n");
        reportSB.append("Shard: ").append(msgEvent.getJDA().getShardInfo().getShardId()).append("/").append(msgEvent.getJDA().getShardInfo().getShardTotal()).append("\n");
        reportSB.append("Server: ").append(msgEvent.getGuild().getName()).append("\n");
        reportSB.append("Channel: #").append(msgEvent.getChannel().getName()).append("\n");
        reportSB.append("Ping: ").append(msgEvent.getJDA().getPing()).append("ms\n");
        reportSB.append("Command Author: ").append(msgEvent.getAuthor().getName()).append("#").append(msgEvent.getAuthor().getDiscriminator());
        if (msgEvent.getMember().getNickname() != null)
        {
            reportSB.append(" (").append(msgEvent.getMember().getNickname()).append(")");   //Display guild specific nickname, too.
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(msgEvent.getJDA().getSelfUser().getName()+" Report:");
        embed.setDescription(reportSB.toString());
        embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));

        msgEvent.getChannel().sendMessage(embed.build()).queue();
    }
}
