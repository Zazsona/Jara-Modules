package com.Zazsona.Kick;

import commands.CmdUtil;
import module.ModuleCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.List;

public class Kick extends ModuleCommand
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (msgEvent.getMember().getPermissions().contains(Permission.KICK_MEMBERS))
        {
            List<Member> membersToKick = msgEvent.getMessage().getMentionedMembers();
            parameters = msgEvent.getMessage().getContentRaw().split(" ");
            String kickMessage = null;
            if (parameters.length > membersToKick.size()+1)
            {
                kickMessage = getKickReason(parameters, membersToKick.size());
            }
            if (membersToKick.size() >= 1)
            {
                StringBuilder feedbackMessage = new StringBuilder();
                int kickCount = executeKicks(msgEvent, membersToKick, kickMessage, feedbackMessage);
                if (feedbackMessage.length() > 0)
                {
                    feedbackMessage.insert(0, "**Unable to kick members:**\n");
                    if (kickCount > 0)
                    {
                        feedbackMessage.insert(0, "Successfully kicked "+kickCount+" members.\n\n");
                    }
                }
                else
                {
                    feedbackMessage.append("Successfully kicked ").append(kickCount).append(" members.");
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setDescription(feedbackMessage.toString());
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getModuleAttributes().getKey());
            }
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("This bot does not have permission to kick members.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }
    }

    private int executeKicks(GuildMessageReceivedEvent msgEvent, List<Member> membersToKick, String kickMessage, StringBuilder feedbackMessage)
    {
        int kickCount = 0;
        for (Member member : membersToKick)
        {
            if (!member.equals(msgEvent.getGuild().getSelfMember()))
            {
                try
                {
                    if (kickMessage == null)
                    {
                        msgEvent.getGuild().kick(member).queue();
                        kickCount++;
                    }
                    else
                    {
                        msgEvent.getGuild().kick(member, kickMessage).queue();
                        kickCount++;
                    }
                }
                catch (PermissionException e)
                {
                    getKickErrorMessage(feedbackMessage, member, "Insufficient Permissions");
                }
                catch (IllegalArgumentException e)
                {
                    getKickErrorMessage(feedbackMessage, member, "Unknown Member");
                }
            }
            else
            {
                getKickErrorMessage(feedbackMessage, member, "It's me");
            }
        }
        return kickCount;
    }

    private void getKickErrorMessage(StringBuilder feedbackMessage, Member member, String reasonForError)
    {
        if (member.getEffectiveName().equalsIgnoreCase(member.getUser().getName()))
        {
            feedbackMessage.append(member.getEffectiveName());
        }
        else
        {
            feedbackMessage.append(member.getUser().getName()).append(" (").append(member.getNickname()).append(")");
        }
        feedbackMessage.append(" - ").append(reasonForError);
        feedbackMessage.append("\n");
    }

    private String getKickReason(String[] parameters, int membersCount)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = (membersCount+1); i<parameters.length; i++)
        {
            sb.append(parameters[i]).append(" ");
        }
        return sb.toString().trim();
    }
}
