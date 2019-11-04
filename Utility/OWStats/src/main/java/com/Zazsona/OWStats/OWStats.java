package com.Zazsona.OWStats;

import com.google.gson.Gson;
import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class OWStats extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        Message msg = sendLoadingMessage(msgEvent.getChannel());
        try
        {
            if (parameters.length > 1)
            {
                if (parameters.length > 2)
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 1; i<parameters.length; i++)
                        stringBuilder.append(parameters[i]);
                    parameters[1] = stringBuilder.toString();
                }
                parameters[1] = parameters[1].replace("#", "-");
                String json = CmdUtil.sendHTTPRequest("https://owapi.net/api/v3/u/"+parameters[1]+"/stats");
                Gson gson = new Gson();
                PlayerStats ps = gson.fromJson(json, PlayerStats.class);
                MessageEmbed embed = buildEmbed(msgEvent.getGuild().getSelfMember(), parameters[1], ps);
                msg.editMessage(embed).queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        catch (Exception e)
        {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embedBuilder.setFooter("Overwatch Archives", "http://i.imgur.com/YZ4w2ey.png");
            embedBuilder.setDescription("No record found for this user, or their hero profile is private.");
            msg.editMessage(embedBuilder.build()).queue();
        }
    }

    private MessageEmbed buildEmbed(Member selfMember, String user, PlayerStats ps)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(CmdUtil.getHighlightColour(selfMember));
        embedBuilder.setFooter("Overwatch Archives", "http://i.imgur.com/YZ4w2ey.png");

        if (ps.hasPlayedQuickplay())
        {
            embedBuilder.setAuthor(user.replace("-", "#"), null, ps.getLevelBorder());
            embedBuilder.setThumbnail(ps.getProfileIcon());
            embedBuilder.setDescription("**Profile**");

            embedBuilder.addField("Prestige", ps.getPrestige(), true);
            embedBuilder.addField("Level", String.valueOf(ps.getLevel()), true);
            embedBuilder.addField("K/D", String.format("%,.2f", ps.getKPD()), true);
            embedBuilder.addField("Top Kill Streak", String.valueOf(ps.getBestKillStreak()), true);
            embedBuilder.addField("Hero Damage Done", String.valueOf(ps.getHeroDamage()), true);
            embedBuilder.addField("Healing Done", String.valueOf(ps.getHealingDone()), true);
            embedBuilder.addField("Medals", String.valueOf(ps.getMedals()), true);
            embedBuilder.addField("Cards", String.valueOf(ps.getCards()), true);
            embedBuilder.addField("Playtime", String.valueOf(ps.getTimePlayed()), true);
            embedBuilder.addField("Rank", String.valueOf(ps.getCompRank()), true);
        }
        else
        {
            embedBuilder.setDescription("No record found for this user, or their hero profile is private.");
        }
        return embedBuilder.build();
    }

    private Message sendLoadingMessage(TextChannel channel)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(CmdUtil.getHighlightColour(channel.getGuild().getSelfMember()));
        embedBuilder.setDescription("Loading...");
        return channel.sendMessage(embedBuilder.build()).complete();
    }

}
