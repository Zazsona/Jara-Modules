package com.Zazsona.ChatLinkCommon;

import java.io.Serializable;

public class DiscordMessagePacket implements Serializable
{
    private String chatLinkUUID;
    private String discordName;
    private String discordMessage;

    public DiscordMessagePacket(String chatLinkUUID, String discordName, String discordMessage)
    {
        this.chatLinkUUID = chatLinkUUID;
        this.discordName = discordName;
        this.discordMessage = discordMessage;
    }

    /**
     * Gets discordName
     *
     * @return discordName
     */
    public String getDiscordName()
    {
        return discordName;
    }

    /**
     * Gets discordMessage
     *
     * @return discordMessage
     */
    public String getDiscordMessage()
    {
        return discordMessage;
    }

    /**
     * Gets ChatLinkUUID
     * @return ChatLinkUUID
     */
    public String getChatLinkUUID()
    {
        return chatLinkUUID;
    }

    /**
     * Returns a formatted message ready for printing.
     * @return
     */
    public String getMessageDisplay()
    {
        return "<"+discordName+"> "+discordMessage;
    }
}
