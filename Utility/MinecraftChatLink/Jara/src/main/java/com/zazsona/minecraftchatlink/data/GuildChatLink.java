package com.zazsona.minecraftchatlink.data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class GuildChatLink
{
    private String chatLinkId;
    private boolean enabled;
    private String guildId;
    private String textChannelId;

    public GuildChatLink(@NotNull String chatLinkId, @NotNull String guildId, @NotNull String textChannelId)
    {
        this.chatLinkId = chatLinkId;
        this.enabled = true;
        this.guildId = guildId;
        this.textChannelId = textChannelId;
    }

    /**
     * Gets the UUID for this guild's Minecraft link
     * @return the UUID
     */
    public String getChatLinkId()
    {
        return chatLinkId;
    }

    /**
     * Sets the value of chatLinkId
     * @param chatLinkId the value to set
     */
    public void setChatLinkId(String chatLinkId)
    {
        this.chatLinkId = chatLinkId;
    }

    /**
     * Generates a new UUID for the guild
     * @return the new UUID
     */
    public String resetChatLinkId()
    {
        String newUUID = UUID.randomUUID().toString();
        setChatLinkId(newUUID);
        return newUUID;
    }

    /**
     * Gets textChannelId
     * @return textChannelID
     */
    public String getTextChannelId()
    {
        return textChannelId;
    }

    /**
     * Sets the TextChannel to send/receive Minecraft messages to.
     * @param textChannelId the ID of the channel
     */
    public void setTextChannelId(String textChannelId)
    {
        this.textChannelId = textChannelId;
    }

    /**
     * Gets guildId
     * @return guildId
     */
    public String getGuildId()
    {
        return guildId;
    }

    /**
     * Sets the value of guildId
     * @param guildId the value to set
     */
    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
    }

    /**
     * Gets enabled
     * @return enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the value of enabled
     * @param enabled the value to set
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void save() throws IOException
    {
        ChatLinkData.getInstance().updateGuild(this);
        ChatLinkData.getInstance().save();
    }
}
