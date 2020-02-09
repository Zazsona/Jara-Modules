package com.Zazsona.VoteItOut.game;

import net.dv8tion.jda.api.entities.Member;
import java.awt.image.BufferedImage;

public class Player
{
    private int playerNo;
    private Member member;
    private BufferedImage playerCard;
    private int points;

    public Player(int playerNo, Member member)
    {
        this.playerNo = playerNo;
        this.member = member;
        this.points = 0;
    }

    /**
     * Gets playerNo
     *
     * @return playerNo
     */
    public int getPlayerNo()
    {
        return playerNo;
    }

    /**
     * Gets member
     *
     * @return member
     */
    public Member getMember()
    {
        return member;
    }

    /**
     * Gets playerCard
     *
     * @return playerCard
     */
    public BufferedImage getPlayerCard()
    {
        return playerCard;
    }

    protected void setPlayerCard(BufferedImage playerCard)
    {
        this.playerCard = playerCard;
    }

    public void addPoint()
    {
        points++;
    }

    public int getPoints()
    {
        return points;
    }


}
