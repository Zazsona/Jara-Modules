package com.Zazsona.jaraMinecraftProfile.data;

import java.io.Serializable;

public class RequestData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String uuid;
    private StatusCode statusCode;

    public RequestData(String uuid, StatusCode statusCode)
    {
        this.uuid = uuid;
        this.statusCode = statusCode;
    }

    public String getUuid()
    {
        return uuid;
    }

    public StatusCode getStatusCode()
    {
        return statusCode;
    }
}
