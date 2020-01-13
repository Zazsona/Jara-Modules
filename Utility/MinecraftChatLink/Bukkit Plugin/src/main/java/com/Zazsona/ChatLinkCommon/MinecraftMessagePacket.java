package com.Zazsona.ChatLinkCommon;

import java.io.Serializable;

public class MinecraftMessagePacket implements Serializable
{
    private String minecraftUsername;
    private String messageContent;
    private String chatLinkUUID;

    public MinecraftMessagePacket(String chatLinkUUID, String minecraftUsername, String messageContent)
    {
        this.chatLinkUUID = chatLinkUUID;
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


    /**
     * Gets ChatLinkUUID
     * @return ChatLinkUUID
     */
    public String getChatLinkUUID()
    {
        return chatLinkUUID;
    }
}
