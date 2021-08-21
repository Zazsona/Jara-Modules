package com.zazsona.blockbusters.game;

import com.zazsona.blockbusters.game.objects.Team;

public class BlockbustersQuitException extends Exception
{
    private Team quittingTeam;

    public BlockbustersQuitException(Team quittingTeam)
    {
        this.quittingTeam = quittingTeam;
    }

    /**
     * Gets quittingTeam
     *
     * @return quittingTeam
     */
    public Team getQuittingTeam()
    {
        return quittingTeam;
    }
}
