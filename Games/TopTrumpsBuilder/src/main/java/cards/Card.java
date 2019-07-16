package cards;

import java.io.Serializable;

public class Card implements Serializable
{
    private static long serialVersionUID = 1L;
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

    public double[] getStats()
    {
        return stats;
    }

    public boolean setName(String newName)
    {
        if (name.length() >= 1 && name.length() <= 25)
        {
            name = newName;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setImageURL(String newImageURL)
    {
        imageURL = newImageURL;
        return true;
    }

    public boolean setStats(double... newStats)
    {
        this.stats = newStats; //I could check the size, but if they've added an additional category, that would break. Size checks will have to be forced by the editor.
        return true;
    }

}
