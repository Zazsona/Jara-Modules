package com.zazsona.jaramobileapp.responses;

import com.zazsona.jara.Core;

import java.io.Serializable;

public class Response implements Serializable
{
    private String jaraVersion;

    public Response()
    {
        this.jaraVersion = Core.getVersion();
    }

    public String getJaraVersion()
    {
        return jaraVersion;
    }
}
