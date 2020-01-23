package com.Zazsona.Blockbusters.game.objects;

import java.io.Serializable;

public class Question implements Serializable
{
    private String question;
    private String answer;

    public Question(String questionText, String questionAnswer)
    {
        this.question = questionText;
        this.answer = questionAnswer;
    }

    /**
     * Gets questionText
     * @return questionText
     */
    public String getQuestionText()
    {
        return question;
    }

    /**
     * Gets questionAnswer
     * @return questionAnswer
     */
    public String getQuestionAnswer()
    {
        return answer;
    }
}
