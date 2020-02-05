package com.Zazsona.WordSearch;

public class Word
{
    private String word;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private boolean found;

    public Word(String word, int startX, int startY, int endX, int endY)
    {
        this.word = word;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.found = false;
    }

    /**
     * Gets word
     * @return word
     */
    public String getWord()
    {
        return word;
    }

    /**
     * Gets startX
     * @return startX
     */
    public int getStartX()
    {
        return startX;
    }

    /**
     * Gets startY
     * @return startY
     */
    public int getStartY()
    {
        return startY;
    }

    /**
     * Gets endX
     * @return endX
     */
    public int getEndX()
    {
        return endX;
    }

    /**
     * Gets endY
     * @return endY
     */
    public int getEndY()
    {
        return endY;
    }

    /**
     * Gets found
     *
     * @return found
     */
    public boolean isFound()
    {
        return found;
    }

    /**
     * Sets the value of found
     *
     * @param found the value to set
     */
    public void setFound(boolean found)
    {
        this.found = found;
    }
}
