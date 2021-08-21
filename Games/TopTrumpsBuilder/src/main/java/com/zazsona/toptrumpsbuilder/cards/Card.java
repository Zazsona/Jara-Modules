package com.Zazsona.TopTrumpsBuilder.cards;

import java.io.Serializable;
import java.security.InvalidParameterException;

public class Card implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String imageURL;
    private double[] stats;

    public Card()
    {
        name = "None";
        imageURL = "None";
        stats = new double[6];
    }


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

    /**
     * Gets stats
     * @return stats
     */
    public double[] getStats()
    {
        return stats;
    }

    /**
     * Sets the name.
     * @param newName the name to set
     * @throws InvalidParameterException name is below 1 or above 25 characters.
     */
    public void setName(String newName) throws InvalidParameterException
    {
        if (name.length() >= 1 && name.length() <= 25)
        {
            name = newName;
        }
        else
        {
            throw new InvalidParameterException("Names must be between 1 and 25 characters.");
        }
    }

    /**
     * Sets the image via the provided URL
     * @param newImageURL the url of the image to set
     * @return true on success.
     */
    public boolean setImageURL(String newImageURL)
    {
        imageURL = newImageURL;
        return true;
    }

    /**
     * Sets the stats.
     * @param newStats the stats to set.
     * @return true on success
     */
    public boolean setStats(double... newStats)
    {
        this.stats = newStats; //I could check the size, but if they've added an additional category, that would break. Size checks will have to be forced by the editor.
        return true;
    }

}
