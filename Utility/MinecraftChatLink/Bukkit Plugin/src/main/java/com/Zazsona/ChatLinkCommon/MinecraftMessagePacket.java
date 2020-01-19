package com.Zazsona.ChatLinkCommon;

import java.io.Serializable;

public class MinecraftMessagePacket extends MessagePacket implements Serializable
{
    private String minecraftUsername;
    private String messageContent;

    public MinecraftMessagePacket(String chatLinkUUID, String minecraftUsername, String messageContent)
    {
        super(chatLinkUUID);
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
