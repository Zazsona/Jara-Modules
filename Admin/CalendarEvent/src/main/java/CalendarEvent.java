import commands.CmdUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.io.Serializable;

public class CalendarEvent implements Serializable
{
    private String name;
    private long guildID;
    private String message;
    private long associatedUserID;
    private String emoji;

    public CalendarEvent(long guildID, String name, String message)
    {
        this.name = name;
        this.guildID = guildID;
        this.message = message;
    }

    public CalendarEvent(long guildID, String name, String message, long associatedUserID)
    {
        this.name = name;
        this.guildID = guildID;
        this.message = message;
        this.associatedUserID = associatedUserID;
    }

    public CalendarEvent(long guildID, String name, String message, long associatedUserID, String emoji)
    {
        this.name = name;
        this.guildID = guildID;
        this.message = message;
        this.associatedUserID = associatedUserID;
        this.emoji = emoji;
    }

    public long getGuildID()
    {
        return guildID;
    }
    public String getName()
    {
        return name;
    }
    public String getMessage()
    {
        return message;
    }
    public long getAssociatedUserID()
    {
        return associatedUserID;
    }
    public String getEmoji()
    {
        return emoji;
    }

    public void execute()
    {
        Guild guild = CmdUtil.getJDA().getGuildById(guildID);
        if (associatedUserID != 0)
        {
            Member member = guild.getMemberById(associatedUserID);
            if (member != null)
            {
                message = message.replace("%USER%", member.getAsMention());
            }
            else
            {
                message = message.replace("%USER%", "someone who left this server");
            }

            if (emoji != null)
            {
                guild.getController().setNickname(member, member.getEffectiveName()+" "+emoji).queue();
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
        embed.setDescription(message);
        guild.getDefaultChannel().sendMessage(embed.build()).queue();
    }
}
