package com.zazsona.voicetextchannels;

import com.zazsona.jara.Core;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.jara.module.ModuleLoad;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class VoiceTextChannelsLoader extends ModuleLoad
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        fm.restore();
        ChannelJoinListener cjl = new ChannelJoinListener();
        Core.getShardManagerNotNull().addEventListener(cjl);
    }

    public class ChannelJoinListener extends ListenerAdapter
    {
        private HashMap<String, TextChannel> voiceToTextChannel = new HashMap<>();
        @Override
        public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
        {
            if (fm.isGuildEnabled(event.getGuild().getId()) && !voiceToTextChannel.containsKey(event.getChannelJoined().getId()))
            {
                if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || event.getChannelJoined().getParent().getPermissionOverride(event.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
                {
                    TextChannel channel = (TextChannel) event.getChannelJoined().getParent().createTextChannel("Text-"+event.getChannelJoined().getName()).complete();
                    for (Member member : event.getChannelJoined().getMembers())
                    {
                        if (channel.getPermissionOverride(member) == null)
                        {
                            channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                        }
                    }
                    sendWelcomeMessage(event.getGuild(), event.getChannelJoined(), channel);
                    voiceToTextChannel.put(event.getChannelJoined().getId(), channel);
                }
            }
        }

        @Override
        public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
        {
            if (fm.isGuildEnabled(event.getGuild().getId()))
            {
                if (event.getChannelLeft().getMembers().size() > 0)
                {
                    if (voiceToTextChannel.containsKey(event.getChannelLeft().getId()))
                    {
                        PermissionOverride po = voiceToTextChannel.get(event.getChannelLeft().getId()).getPermissionOverride(event.getMember());
                        if (po != null)
                        {
                            po.delete().queue();
                        }
                    }
                }
                else
                {
                    if (voiceToTextChannel.containsKey(event.getChannelLeft().getId()))
                    {
                        voiceToTextChannel.get(event.getChannelLeft().getId()).delete().queue();
                        voiceToTextChannel.remove(event.getChannelLeft().getId());
                    }
                }
            }
        }

        @Override
        public void onGuildVoiceMove(GuildVoiceMoveEvent event)
        {
            if (fm.isGuildEnabled(event.getGuild().getId()))
            {
                if (event.getChannelLeft().getMembers().size() > 0)
                {
                    if (voiceToTextChannel.containsKey(event.getChannelLeft().getId()))
                    {
                        PermissionOverride po = voiceToTextChannel.get(event.getChannelLeft().getId()).getPermissionOverride(event.getMember());
                        if (po != null)
                        {
                            po.delete().queue();
                        }
                    }
                }
                else
                {
                    if (voiceToTextChannel.containsKey(event.getChannelLeft().getId()))
                    {
                        voiceToTextChannel.get(event.getChannelLeft().getId()).delete().queue();
                        voiceToTextChannel.remove(event.getChannelLeft().getId());
                    }
                }
            }

            if (fm.isGuildEnabled(event.getGuild().getId()) && !voiceToTextChannel.containsKey(event.getChannelJoined().getId()))
            {
                if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || event.getChannelJoined().getParent().getPermissionOverride(event.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
                {
                    TextChannel channel = (TextChannel) event.getChannelJoined().getParent().createTextChannel("Text-"+event.getChannelJoined().getName()).complete();
                    for (Member member : event.getChannelJoined().getMembers())
                    {
                        if (channel.getPermissionOverride(member) == null)
                        {
                            channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                        }
                    }
                    sendWelcomeMessage(event.getGuild(), event.getChannelJoined(), channel);
                    voiceToTextChannel.put(event.getChannelJoined().getId(), channel);
                }
            }
        }

        /*@Override
        public void onShutdown(ShutdownEvent event)
        {
            for (TextChannel channel : voiceToTextChannel.values())
            {
                channel.delete().queue();
            }
            voiceToTextChannel = new HashMap<>();
        }*/
    }

    private void sendWelcomeMessage(Guild guild, VoiceChannel vc, TextChannel tc)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/hvphthX.png");
        embed.setDescription("Welcome to the text channel for **"+vc.getName()+"**!\nAnyone joining the voice channel will automatically be added, and this channel will be deleted once everyone leaves.");
        tc.sendMessage(embed.build()).queue();
    }

    public static FileManager getFileManager()
    {
        return fm;
    }
}
