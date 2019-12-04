package com.Zazsona.MobileApp.requests;

import jara.Core;

import java.io.Serializable;

public class Request implements Serializable
{
    private String jaraVersion;
    private RequestType requestType;

    public Request(RequestType requestType)
    {
        this.jaraVersion = Core.getVersion();
        this.requestType = requestType;
    }

    public String getJaraVersion()
    {
        return jaraVersion;
    }

    public RequestType getRequestType()
    {
        return requestType;
    }
}
