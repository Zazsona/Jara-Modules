import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class VoiceTextChannels extends Command
{
    private static HashMap<String, TextChannel> voiceToTextChannel = new HashMap<>();

    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (msgEvent.getMember().getVoiceState().inVoiceChannel())
        {
            VoiceChannel vc = msgEvent.getMember().getVoiceState().getChannel();
            if (voiceToTextChannel.get(vc.getId()) == null)
            {
                if (vc.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || vc.getParent().getPermissionOverride(vc.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
                {
                    TextChannel tc = (TextChannel) vc.getParent().createTextChannel("Text-"+vc.getName()).complete();
                    tc.putPermissionOverride(msgEvent.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                    sendScreenshareURL(vc.getGuild(), vc, tc);
                    voiceToTextChannel.put(vc.getId(), tc);
                    for (Member member : vc.getMembers())
                    {
                        tc.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                    }
                }
            }
            else
            {
                voiceToTextChannel.get(vc.getId()).delete().queue();
                voiceToTextChannel.remove(vc.getId());
            }
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("You must be in a voice channel to use this command!");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    public static class VoiceChannelListener extends ListenerAdapter
    {
        @Override
        public void onGuildVoiceJoin(GuildVoiceJoinEvent event)
        {
            if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || event.getChannelJoined().getParent().getPermissionOverride(event.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
            {
                if (voiceToTextChannel.containsKey(event.getChannelJoined().getId()))
                {
                    TextChannel channel = voiceToTextChannel.get(event.getChannelJoined().getId());
                    for (Member member : event.getChannelJoined().getMembers())
                    {
                        if (channel.getPermissionOverride(member) == null)
                        {
                            channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                        }
                    }
                }
            }
        }

        @Override
        public void onGuildVoiceLeave(GuildVoiceLeaveEvent event)
        {
            if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || event.getChannelLeft().getParent().getPermissionOverride(event.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
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
            if (event.getGuild().getSelfMember().getPermissions().contains(Permission.MANAGE_CHANNEL) || event.getChannelLeft().getParent().getPermissionOverride(event.getGuild().getSelfMember()).getAllowed().contains(Permission.MANAGE_CHANNEL))
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

                if (voiceToTextChannel.containsKey(event.getChannelJoined().getId()))
                {
                    TextChannel channel = voiceToTextChannel.get(event.getChannelJoined().getId());
                    for (Member member : event.getChannelJoined().getMembers())
                    {
                        if (channel.getPermissionOverride(member) == null)
                        {
                            channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE).queue();
                        }
                    }
                }
            }
        }
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
}
