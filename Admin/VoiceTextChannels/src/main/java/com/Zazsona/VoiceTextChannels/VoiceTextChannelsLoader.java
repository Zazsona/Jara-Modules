package com.Zazsona.VoiceTextChannels;

import commands.CmdUtil;
import module.Load;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class VoiceTextChannelsLoader extends Load
{
    private static FileManager fm;

    @Override
    public void load()
    {
        fm = new FileManager();
        ChannelJoinListener cjl = new ChannelJoinListener();
        CmdUtil.getJDA().addEventListener(cjl);
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
                    sendScreenshareURL(event.getGuild(), event.getChannelJoined(), channel);
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
                    sendScreenshareURL(event.getGuild(), event.getChannelJoined(), channel);
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

    private void sendScreenshareURL(Guild guild, VoiceChannel vc, TextChannel tc)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
        embed.setThumbnail("https://i.imgur.com/hvphthX.png");
        embed.setDescription("Screenshare URL: https://discordapp.com/channels/"+guild.getId()+"/"+vc.getId());
        Message ssMsg = tc.sendMessage(embed.build()).complete();
        if (guild.getSelfMember().getPermissions().contains(Permission.MESSAGE_MANAGE) || tc.getPermissionOverride(guild.getSelfMember()).getAllowed().contains(Permission.MESSAGE_MANAGE))
        {
            ssMsg.pin().queue();
        }
    }

    public static FileManager getFileManager()
    {
        return fm;
    }
}
