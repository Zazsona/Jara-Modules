package com.Zazsona.MonthlyUsage;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

public class MonthlyUsage extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        FileManager fm = MonthlyUsageRecorder.getFileManager();
        EmbedBuilder embed = buildEmbed(fm, msgEvent.getGuild(), false);
        msgEvent.getChannel().sendMessage(embed.build()).queue();

    }

    public EmbedBuilder buildEmbed(FileManager fm, Guild guild, boolean isAuto)
    {
        HashMap<String, Integer> guildCommandUsage = fm.getGuildCommandUsage(guild.getId());
        if (guildCommandUsage != null)
        {
            OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
            String[] topThree = getTopThree(guildCommandUsage);
            StringBuilder descBuilder = new StringBuilder();
            if (!isAuto)
            {
                descBuilder.append("**").append(utc.getMonth().name()).append(" ").append(utc.getYear()).append("**\n\n");
            }
            else
            {
                descBuilder.append("**").append(utc.getMonth().minus(1).name()).append(" ");
                if (utc.getMonthValue() != 1)
                    descBuilder.append(utc.getYear()).append("\n\n");
                else
                    descBuilder.append((utc.getYear()-1)).append("\n\n");
                descBuilder.append("**");
            }

            descBuilder.append("Total Commands Used: ").append(getTotal(guildCommandUsage)).append("\n");
            if (!topThree[0].equalsIgnoreCase(""))
                descBuilder.append("1st. ").append(guild.getMemberById(topThree[0]).getEffectiveName()).append(" (").append(guildCommandUsage.get(topThree[0])).append(")\n");
            if (!topThree[1].equalsIgnoreCase(""))
                descBuilder.append("2nd. ").append(guild.getMemberById(topThree[1]).getEffectiveName()).append(" (").append(guildCommandUsage.get(topThree[1])).append(")\n");
            if (!topThree[2].equalsIgnoreCase(""))
                descBuilder.append("3rd. ").append(guild.getMemberById(topThree[2]).getEffectiveName()).append(" (").append(guildCommandUsage.get(topThree[2])).append(")\n");

            descBuilder.append("\n");
            descBuilder.append(getIndividualValues(guildCommandUsage, guild));

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("=========Monthly Usage Report=========");
            embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
            embed.setDescription(descBuilder.toString());
            return embed;
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("=========Monthly Usage Report=========");
            embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
            embed.setDescription("Nobody's used any commands!");
            return embed;
        }

    }

    private int getTotal(HashMap<String, Integer> guildCommandUsage)
    {
        int total = 0;
        for (int usage : guildCommandUsage.values())
        {
            total += usage;
        }
        return total;
    }

    private String getIndividualValues(HashMap<String, Integer> guildCommandUsage, Guild guild)
    {
        if (guildCommandUsage.size() <= 20)
        {
            StringBuilder listBuilder = new StringBuilder();
            listBuilder.append("**==========Individual Values===========**").append("\n");
            for (String userID : guildCommandUsage.keySet())
            {
                listBuilder.append(guild.getMemberById(userID).getEffectiveName()).append(": ").append(guildCommandUsage.get(userID)).append("\n");
            }
            return listBuilder.toString();
        }
        return "";
    }

    private String[] getTopThree(HashMap<String, Integer> guildCommandUsage)
    {
        String firstPlaceId = "";
        int firstPlaceUsage = 0;
        String secondPlaceId = "";
        int secondPlaceUsage = -1;
        String thirdPlaceId = "";
        int thirdPlaceUsage = -2;

        for (String userID : guildCommandUsage.keySet())
        {
            int commandUsage = guildCommandUsage.get(userID);
            if (commandUsage > firstPlaceUsage)
            {
                thirdPlaceId = secondPlaceId;
                thirdPlaceUsage = secondPlaceUsage;
                secondPlaceId = firstPlaceId;
                secondPlaceUsage = firstPlaceUsage;
                firstPlaceId = userID;
                firstPlaceUsage = commandUsage;
            }
            else if (commandUsage < firstPlaceUsage && commandUsage > secondPlaceUsage)
            {
                thirdPlaceId = secondPlaceId;
                thirdPlaceUsage = secondPlaceUsage;
                secondPlaceId = userID;
                secondPlaceUsage = commandUsage;
            }
            else if (commandUsage < secondPlaceUsage && commandUsage > thirdPlaceUsage)
            {
                thirdPlaceId = userID;
                thirdPlaceUsage = commandUsage;
            }
        }
        String[] tops = new String[3];
        tops[0] = firstPlaceId;
        tops[1] = secondPlaceId;
        tops[2] = thirdPlaceId;
        return tops;
    }
}
