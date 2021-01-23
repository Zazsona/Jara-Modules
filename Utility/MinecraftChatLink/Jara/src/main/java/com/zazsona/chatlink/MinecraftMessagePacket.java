package com.zazsona.chatlink;

import java.io.Serializable;

public class MinecraftMessagePacket extends MessagePacket implements Serializable
{
    private String minecraftUsername;
    private String messageContent;
    private String uuid;

    public MinecraftMessagePacket(String chatLinkUUID, String uuid, String minecraftUsername, String messageContent)
    {
        super(chatLinkUUID);
        this.minecraftUsername = minecraftUsername;
        this.messageContent = messageContent;
        this.uuid = uuid;
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

    /**
     * Gets uuid
     * @return uuid
     */
    public String getUuid()
    {
        return uuid;
    }
}
