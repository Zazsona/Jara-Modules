package com.zazsona.beatthecores;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChallengeTimer
{
    private static final int UPDATE_INTERVAL_MS = 10;

    private int initialPlayerTimeMs;
    private int initialCoresTimeMs;
    private ArrayList<TimerCompleteHandler> completeHandlers;

    private Timer timer;
    private boolean isPlayerTimerActive;
    private long playerTimeMs;
    private long coresTimeMs;

    public ChallengeTimer(int playerTimeMs, int coresTimeMs)
    {
        this.initialPlayerTimeMs = playerTimeMs;
        this.initialCoresTimeMs = coresTimeMs;
        this.playerTimeMs = initialPlayerTimeMs;
        this.coresTimeMs = initialCoresTimeMs;
        this.isPlayerTimerActive = true;
        this.completeHandlers = new ArrayList<>();
    }

    public void addTimerCompleteHandler(TimerCompleteHandler handler)
    {
        completeHandlers.add(handler);
    }

    public void setTimerMode(boolean isPlayerTimer)
    {
        this.isPlayerTimerActive = isPlayerTimer;
    }

    public long getRemainingPlayerTimeMs()
    {
        return playerTimeMs;
    }

    public long getRemainingCoresTimeMs()
    {
        return coresTimeMs;
    }

    public void start()
    {
        this.playerTimeMs = initialPlayerTimeMs;
        this.coresTimeMs = initialCoresTimeMs;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (isPlayerTimerActive)
                {
                    playerTimeMs -= UPDATE_INTERVAL_MS;
                    if (playerTimeMs <= 0)
                    {
                        for (TimerCompleteHandler handler : completeHandlers)
                            handler.onTimerComplete(isPlayerTimerActive);
                        timer.cancel();
                    }
                }
                else
                {
                    coresTimeMs -= UPDATE_INTERVAL_MS;
                    if (coresTimeMs <= 0)
                    {
                        for (TimerCompleteHandler handler : completeHandlers)
                            handler.onTimerComplete(isPlayerTimerActive);
                        timer.cancel();
                    }
                }
            }
        }, UPDATE_INTERVAL_MS, UPDATE_INTERVAL_MS);
    }
}
