package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.game.objects.Team;

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
