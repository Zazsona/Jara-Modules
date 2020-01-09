package com.Zazsona.ChatLinkCommon;

import java.io.Serializable;

public class MinecraftMessagePacket implements Serializable
{
    private String minecraftUsername;
    private String messageContent;

    public MinecraftMessagePacket(String minecraftUsername, String messageContent)
    {
        this.minecraftUsername = minecraftUsername;
        this.messageContent = messageContent;
    }

    /**
     * Gets minecraftUsername
     * @return minecraftUsername
     */
    public String getMinecraftUsername()
    {
        return minecraftUsername;
    }

    /**
     * Gets messageContent
     * @return messageContent
     */
    public String getMessageContent()
    {
        return messageContent;
    }
}
