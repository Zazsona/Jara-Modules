package com.Zazsona.WelcomeMessage;

import commands.CmdUtil;
import jara.Core;
import module.ModuleLoad;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageSender extends ModuleLoad
{
    FileManager fm;

    @Override
    public void load()
    {
        fm = FileManager.getInstance();
        GuildJoinListener gjl = new GuildJoinListener();
        Core.getShardManagerNotNull().addEventListener(gjl);
    }

    private class GuildJoinListener extends ListenerAdapter
    {
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent event)
        {
            if (fm.isGuildEnabled(event.getGuild().getId()) && fm.getWelcomeMessage(event.getGuild().getId()) != null)
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(event.getGuild().getSelfMember()));
                embed.setDescription(fm.getWelcomeMessage(event.getGuild().getId()));
                event.getMember().getUser().openPrivateChannel().complete().sendMessage(embed.build()).queue();
            }
        }
    }
}
