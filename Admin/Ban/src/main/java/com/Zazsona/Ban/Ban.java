package com.Zazsona.Ban;

import commands.CmdUtil;
import module.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import java.util.List;

public class Ban extends Command
{
    @Override
    public void run(GuildMessageReceivedEvent msgEvent, String... parameters)
    {
        if (msgEvent.getMember().getPermissions().contains(Permission.BAN_MEMBERS))
        {
            List<Member> membersToBan = msgEvent.getMessage().getMentionedMembers();
            parameters = msgEvent.getMessage().getContentRaw().split(" ");
            String banMessage = null;
            int deleteDays = 0;
            if (parameters.length > membersToBan.size()+1)
            {
                deleteDays = getDeleteDays(parameters, membersToBan.size());
                banMessage = getBanReason(parameters, membersToBan.size());
            }
            if (membersToBan.size() >= 1)
            {
                StringBuilder feedbackMessage = new StringBuilder();
                int banCount = executeBans(msgEvent, membersToBan, banMessage, deleteDays, feedbackMessage);
                if (feedbackMessage.length() > 0)
                {
                    feedbackMessage.insert(0, "**Unable to ban members:**\n");
                    if (banCount > 0)
                    {
                        feedbackMessage.insert(0, "Successfully banned "+banCount+" members and deleted their last "+deleteDays+" day(s) of messages.\n\n");
                    }
                }
                else
                {
                    feedbackMessage.append("Members banned successfully, with their last "+deleteDays+" day(s) of messages deleted.");
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
                embed.setDescription(feedbackMessage.toString());
                msgEvent.getChannel().sendMessage(embed.build()).queue();
            }
            else
            {
                CmdUtil.sendHelpInfo(msgEvent, getClass());
            }
        }
        else
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(CmdUtil.getHighlightColour(msgEvent.getGuild().getSelfMember()));
            embed.setDescription("This bot does not have permission to ban members.");
            msgEvent.getChannel().sendMessage(embed.build()).queue();
        }

    }

    private int executeBans(GuildMessageReceivedEvent msgEvent, List<Member> membersToBan, String banMessage, int delDays, StringBuilder feedbackMessage)
    {
        int banCount = 0;
        for (Member member : membersToBan)
        {
            if (!member.equals(msgEvent.getGuild().getSelfMember()))
            {
                try
                {
                    if (banMessage == null)
                    {
                        msgEvent.getGuild().getController().ban(member, delDays).queue();
                        banCount++;
                    }
                    else
                    {
                        msgEvent.getGuild().getController().ban(member, delDays, banMessage).queue();
                        banCount++;
                    }
                }
                catch (PermissionException e)
                {
                    getBanErrorMessage(feedbackMessage, member, "Insufficient Permissions");
                }
                catch (IllegalArgumentException e)
                {
                    getBanErrorMessage(feedbackMessage, member, "Unknown Member");
                }
            }
            else
            {
                getBanErrorMessage(feedbackMessage, member, "It's me");
            }
        }
        return banCount;
    }

    private void getBanErrorMessage(StringBuilder feedbackMessage, Member member, String reasonForError)
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

    private String getBanReason(String[] parameters, int membersCount)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = (membersCount+1); i<parameters.length; i++)
        {
            if (i == membersCount+1)
            {
                if (parameters[i].matches("[0-9]+"))
                {
                    continue;                   //If this is the parameter to specify the delete days, don't include it in the ban reason.
                }
            }
            sb.append(parameters[i]).append(" ");
        }
        return sb.toString().trim();
    }

    private int getDeleteDays(String[] parameters, int membersCount)
    {
        try
        {
            return Integer.parseInt(parameters[membersCount+1]);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }
}
