package com.Zazsona.jaraMinecraftProfile.data;

import java.io.Serializable;

public class ResponseData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private StatusCode statusCode;
    private PlayerData playerData;

    public ResponseData(StatusCode statusCode, PlayerData playerData)
    {
        this.statusCode = statusCode;
        this.playerData = playerData;
    }

    public StatusCode getStatusCode()
    {
        return statusCode;
    }

    public PlayerData getPlayerData()
    {
        return playerData;
    }
}
