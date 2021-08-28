package com.zazsona.beatthecores;

import java.util.ArrayList;

public class CashBuilderPerformance
{
    private int questionsCorrect;
    private int cash;
    private ArrayList<Long> secondsToAnswer;

    public CashBuilderPerformance(int questionsCorrect, int cash, ArrayList<Long> secondsToAnswer)
    {
        this.questionsCorrect = questionsCorrect;
        this.cash = cash;
        this.secondsToAnswer = secondsToAnswer;
    }

    public int getQuestionsCorrectCount()
    {
        return questionsCorrect;
    }

    public int getCash()
    {
        return cash;
    }

    public long GetAnswerTime(int questionNo)
    {
        int index = questionNo - 1;
        return secondsToAnswer.get(index);
    }

    public long GetTotalAnswerTime()
    {
        long totalSeconds = 0;
        for (long questionTime : secondsToAnswer)
        {
            totalSeconds += questionTime;
        }
        return totalSeconds;
    }
}
