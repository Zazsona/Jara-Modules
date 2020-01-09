package com.Zazsona.ChatLinkCommon;

import java.io.Serializable;

public class DiscordMessagePacket implements Serializable
{
    private String botID;
    private String discordName;
    private String discordMessage;

    public DiscordMessagePacket(String botID, String discordName, String discordMessage)
    {
        this.botID = botID;
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
     * Gets botID
     * @return botID
     */
    public String getBotID()
    {
        return botID;
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
