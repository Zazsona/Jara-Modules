package com.zazsona.beatthecores;

public class CoreChallengeBet
{
    private int coreCount;
    private int coreTime;
    private int playerTime;
    private int prizeCash;

    public CoreChallengeBet(int coreCount, int coreTime, int playerTime, int prizeCash)
    {
        this.coreCount = coreCount;
        this.coreTime = coreTime;
        this.playerTime = playerTime;
        this.prizeCash = prizeCash;
    }

    /**
     * Gets coreCount
     *
     * @return coreCount
     */
    public int getCoreCount()
    {
        return coreCount;
    }

    /**
     * Gets coreTime
     *
     * @return coreTime
     */
    public int getCoreTime()
    {
        return coreTime;
    }

    /**
     * Gets playerTime
     *
     * @return playerTime
     */
    public int getPlayerTime()
    {
        return playerTime;
    }

    /**
     * Gets prizeCash
     *
     * @return prizeCash
     */
    public int getPrizeCash()
    {
        return prizeCash;
    }
}
