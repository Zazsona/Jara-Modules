import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.List;

public class Kick extends Command
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
                CmdUtil.sendHelpInfo(msgEvent, getClass());
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
                        msgEvent.getGuild().getController().kick(member).queue();
                        kickCount++;
                    }
                    else
                    {
                        msgEvent.getGuild().getController().kick(member, kickMessage).queue();
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
