package com.zazsona.toptrumps.cards;

import java.io.Serializable;

public class Card implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String imageURL;
    private double[] stats;

    public Card(String name, String imageURL, double... stats)
    {
        this.name = name;
        this.imageURL = imageURL;
        this.stats = stats;
    }

    /**
     * Gets name
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets imageURL
     *
     * @return imageURL
     */
    public String getImageURL()
    {
        return imageURL;
    }

    public double[] getStats()
    {
        return stats;
    }
}
