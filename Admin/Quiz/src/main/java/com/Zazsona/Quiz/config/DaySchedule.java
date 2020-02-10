package com.Zazsona.Quiz.config;

import java.io.Serializable;
import java.util.ArrayList;

public class DayJson implements Serializable
{
    private ArrayList<Long> startSeconds;

    public DayJson()
    {
        startSeconds = new ArrayList<>();
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
}
