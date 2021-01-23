package com.zazsona.chatlink;

import java.io.Serializable;

public class MessagePacket implements Serializable
{
    private String chatLinkUUID;

    public MessagePacket(String chatLinkUUID)
    {
        this.chatLinkUUID = chatLinkUUID;
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
