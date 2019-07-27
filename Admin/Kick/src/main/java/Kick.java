import commands.CmdUtil;
import commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
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
        List<Member> membersToKick = msgEvent.getMessage().getMentionedMembers();
        if (membersToKick.size() >= 1)
        {
            StringBuilder feedbackMessage = new StringBuilder();
            for (Member member : membersToKick)
            {
                if (!member.equals(msgEvent.getGuild().getSelfMember()))
                {
                    try
                    {
                        msgEvent.getGuild().getController().kick(member).queue();
                    }
                    catch (PermissionException e)
                    {
                        buildKickErrorMessage(feedbackMessage, member, "Insufficient Permissions");
                    }
                    catch (IllegalArgumentException e)
                    {
                        buildKickErrorMessage(feedbackMessage, member, "Unknown Member");
                    }
                }
                else
                {
                    buildKickErrorMessage(feedbackMessage, member, "It's me");
                }

            }
            if (feedbackMessage.length() > 0)
            {
                feedbackMessage.insert(0, "**Unable to kick users:**\n");
            }
            else
            {
                feedbackMessage.append("Members kicked successfully.");
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

    private void buildKickErrorMessage(StringBuilder feedbackMessage, Member member, String reasonForError)
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
}
