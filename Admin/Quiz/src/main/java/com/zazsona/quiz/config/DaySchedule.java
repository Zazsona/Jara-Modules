package com.zazsona.quiz.config;

import java.io.Serializable;
import java.util.ArrayList;

public class DaySchedule implements Serializable
{
    private ArrayList<Long> startSeconds;

    public DaySchedule()
    {
        startSeconds = new ArrayList<>();
    }

    private DaySchedule(DaySchedule daySchedule)
    {
        this.startSeconds = daySchedule.startSeconds;
    }

    public ArrayList<Long> getStartSeconds()
    {
        return startSeconds;
    }

    public void addStartSecond(long secondOfDay)
    {
        startSeconds.add(secondOfDay);
    }

    public void removeStartSecond(long secondOfDay)
    {
        startSeconds.remove(secondOfDay);
    }

    public boolean isStartSecond(long secondOfDay)
    {
        return (startSeconds.contains(secondOfDay));
    }

    public boolean hasQuiz()
    {
        return (startSeconds.size() > 0);
    }

    public DaySchedule clone()
    {
        return new DaySchedule(this);
    }
}
