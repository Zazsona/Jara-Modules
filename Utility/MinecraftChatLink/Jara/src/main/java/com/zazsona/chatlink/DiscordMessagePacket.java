package com.zazsona.chatlink;

import java.io.Serializable;

public class DiscordMessagePacket extends MessagePacket implements Serializable
{
    private String discordName;
    private String discordMessage;

    public DiscordMessagePacket(String chatLinkUUID, String discordName, String discordMessage)
    {
        super(chatLinkUUID);
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
     * Returns a formatted message ready for printing.
     * @return
     */
    public String getMessageDisplay()
    {
        return "<"+discordName+"> "+discordMessage;
    }
}
