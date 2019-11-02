package com.Zazsona.Quote;

import java.io.Serializable;

public class Quote implements Serializable
{
    private static final long serialVersionUID = 1L;
    public String name;
    public String user;
    public String message;
    public long timestamp;
    public String attachmentUrl;

    public Quote(String name, String user, String message, long timestamp)
    {
        this.name = name;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Quote(String name, String user, String message, String attachmentUrl, long timestamp)
    {
        this.name = name;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
        this.attachmentUrl = attachmentUrl;
    }
}
