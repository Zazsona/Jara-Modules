package com.zazsona.beatthecores.api;

public class TokenResponse
{
    private int response_code;
    private String response_message;
    private String token;

    /**
     * Gets response_code
     *
     * @return response_code
     */
    public int getResponseCode()
    {
        return response_code;
    }

    /**
     * Gets response_message
     *
     * @return response_message
     */
    public String getResponseMessage()
    {
        return response_message;
    }

    /**
     * Gets token
     *
     * @return token
     */
    public String getToken()
    {
        return token;
    }
}
