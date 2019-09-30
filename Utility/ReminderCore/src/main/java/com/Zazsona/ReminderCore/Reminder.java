package com.Zazsona.ReminderCore;

import com.Zazsona.ReminderCore.enums.GroupType;
import com.Zazsona.ReminderCore.enums.RepetitionType;
import commands.CmdUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Reminder implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String userID;
    private String creationTimeStamp;
    private String guildID;
    private String channelID;
    private RepetitionType repetitionType;
    private GroupType groupType;
    private String message;
    private ZonedDateTime firstExecutionTimeUTC;

    private transient Guild guild;
    private transient User user;

    public Reminder(String userID, String creationTimeStamp, String guildID, String channelID, GroupType groupType, RepetitionType repetitionType, String message, ZonedDateTime firstExecutionTime)
    {
        this.userID = userID;
        this.creationTimeStamp = creationTimeStamp;
        this.guildID = guildID;
        this.channelID = channelID;
        this.repetitionType = repetitionType;
        this.groupType = groupType;
        this.message = message;
        this.firstExecutionTimeUTC = ZonedDateTime.ofInstant(firstExecutionTime.toInstant(), ZoneOffset.UTC); //Convert to UTC

    }

    public String getUserID()
    {
        return userID;
    }

    public String getCreationTimeStamp()
    {
        return creationTimeStamp;
    }

    public String getUUID()
    {
        return userID+"-"+creationTimeStamp;
    }

    public String getGuildID()
    {
        return guildID;
    }

    public Guild getGuild()
    {
        if (guild == null)
        {
            guild = CmdUtil.getJDA().getGuildById(guildID);
        }
        return guild;
    }

    public String getChannelID()
    {
        return channelID;
    }

    public TextChannel getChannel()
    {
        Guild guild = getGuild();
        if (guild != null)
        {
            TextChannel channel = guild.getTextChannelById(channelID);
            channel = (channel == null) ? guild.getDefaultChannel() : channel;
            return channel;
        }
        else
        {
            return null;
        }
    }

    public User getUser()
    {
        if (user == null)
        {
            Guild guild = getGuild();
            if (guild != null)
            {
                user = guild.getMemberById(userID).getUser();
            }
            else
            {
                user = CmdUtil.getJDA().getUserById(userID);
            }
        }
        return user;
    }

    public PrivateChannel getPrivateChannel()
    {
        User user = getUser();
        if (user != null)
        {
            return user.openPrivateChannel().complete();
        }
        return null;
    }

    public void setChannelID(String channelID)
    {
        this.channelID = channelID;
    }

    public RepetitionType getRepetitionType()
    {
        return repetitionType;
    }

    public String getMessage()
    {
        return message;
    }

    public GroupType getGroupType()
    {
        return groupType;
    }

    public ZonedDateTime getFirstExecutionTime()
    {
        return firstExecutionTimeUTC;
    }

    public void execute()
    {
        //TODO: Confirm the user has permissions to set/remove reminders before sending
        //Don't want some pratt admin seeing them set a daily reminder, then locking them into it forever.
        Guild guild = getGuild();
        if (guild != null)
        {
            Member member = guild.getMemberById(userID);
            if (member != null)
            {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(CmdUtil.getHighlightColour(guild.getSelfMember()));
                embed.setDescription(message);
                if (groupType == GroupType.CHANNEL)
                {
                    embed.setFooter("Set by: "+member.getEffectiveName(), null);
                    TextChannel channel = getChannel();
                    if (channel != null)
                    {
                        channel.sendMessage(embed.build()).queue();
                    }
                }
                else if (groupType == GroupType.USER)
                {

                    PrivateChannel channel = getPrivateChannel();
                    if (channel != null)
                    {
                        channel.sendMessage(embed.build()).queue();
                    }
                }

            }
        }
    }

}
