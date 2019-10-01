package com.Zazsona.QuoteDelete;

import java.io.Serializable;

public class Quote implements Serializable
{
    private static final long serialVersionUID = 1L;
    public Quote(String name, String user, String message, String date)
    {
        this.name = name;
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public Quote(String name, String user, String message, String attachmentUrl, String date)
    {
        this.name = name;
        this.user = user;
        this.message = message;
        this.date = date;
        this.attachmentUrl = attachmentUrl;
    }

    public String name;
    public String user;
    public String message;
    public String date;
    public String attachmentUrl;
}
