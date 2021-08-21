package com.zazsona.trivia;

public class TriviaJson
{
    TriviaQuestion[] results;
    public class TriviaQuestion
    {
        String category;
        String type;
        String difficulty;
        String question;
        String correct_answer;
        String[] incorrect_answers;
    }

}
