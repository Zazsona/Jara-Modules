package com.Zazsona.Blockbusters.game.objects;

import java.io.Serializable;

public class Question implements Serializable
{
    private String question;
    private String[] answers;

    public Question(String questionText, String... questionAnswers)
    {
        this.question = questionText;
        this.answers = questionAnswers;
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
     * Gets questionAnswers
     * @return questionAnswers
     */
    public String[] getQuestionAnswer()
    {
        return answers;
    }

    /**
     * Gets if the provided answer matches any of the registered ones
     * @param playerAnswer the answer provided by the player
     * @return true on correct
     */
    public boolean isAnswerCorrect(String playerAnswer)
    {
        for (String validAnswer : answers)
        {
            if (validAnswer.equalsIgnoreCase(playerAnswer))
            {
                return true;
            }
        }
        return false;
    }
}
