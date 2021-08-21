package com.zazsona.blockbusters.game.objects;

public class Tile
{
    private TileState tileState;
    private String tileChar;
    private int tileX;
    private int tileY;

    protected Tile(int x, int y, String letter, TileState state)
    {
        this.tileState = state;
        this.tileChar = letter;
        this.tileX = x;
        this.tileY = y;
    }


    /**
     * Gets tileState
     *
     * @return tileState
     */
    public TileState getTileState()
    {
        return tileState;
    }

    /**
     * Gets tileChar
     *
     * @return tileChar
     */
    public String getTileChar()
    {
        return tileChar;
    }

    /**
     * Gets tileX
     *
     * @return tileX
     */
    public int getTileX()
    {
        return tileX;
    }

    /**
     * Gets tileY
     *
     * @return tileY
     */
    public int getTileY()
    {
        return tileY;
    }

    /**
     * Sets the value of tileState
     *
     * @param tileState  the value to set
     */
    public void setTileState(TileState tileState)
    {
        this.tileState = tileState;
    }

    protected Tile clone()
    {
        return new Tile(tileX, tileY, tileChar, tileState);
    }
}
