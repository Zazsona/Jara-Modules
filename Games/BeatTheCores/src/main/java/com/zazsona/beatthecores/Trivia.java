package com.zazsona.beatthecores;

import com.zazsona.beatthecores.api.TriviaResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.text.StringEscapeUtils;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Random;

public class Trivia
{
    private String question;
    private String category;
    private String[] answers;
    private int correctAnswerIndex;
    private int difficulty;
    private int questionNo;

    public Trivia(TriviaResponse.TriviaQuestion response, int questionNo)
    {
        this.question = StringEscapeUtils.unescapeHtml4(response.getQuestion());
        this.questionNo = questionNo;
        this.category = response.getCategory();
        Random r = new Random();
        answers = new String[response.getIncorrectAnswers().length+1];
        correctAnswerIndex = r.nextInt(answers.length);
        for (int i = 0; i<answers.length; i++)
        {
            if (i < correctAnswerIndex)
                answers[i] = StringEscapeUtils.unescapeHtml4(response.getIncorrectAnswers()[i]);
            else if (i == correctAnswerIndex)
                answers[i] = StringEscapeUtils.unescapeHtml4(response.getCorrectAnswer());
            else if (i > correctAnswerIndex)
                answers[i] = StringEscapeUtils.unescapeHtml4(response.getIncorrectAnswers()[i-1]);
        }
        switch (response.getDifficulty())
        {
            case "hard":
                difficulty = 3;
                break;
            case "medium":
                difficulty = 2;
                break;
            case "easy":
                difficulty = 1;
                break;
        }
    }

    /**
     * Gets question
     * @return question
     */
    public String getQuestion()
    {
        return question;
    }

    /**
     * Gets category
     * @return category
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Gets answers
     * @return answers
     */
    public String[] getAnswers()
    {
        return answers;
    }

    public String getCorrectAnswer()
    {
        return answers[correctAnswerIndex];
    }

    public int getCorrectAnswerIndex()
    {
        return correctAnswerIndex;
    }

    /**
     * Gets points
     * @return points
     */
    public int getDifficulty()
    {
        return difficulty;
    }

    public boolean isAnswerCorrect(String answer)
    {
        String correctAnswer = answers[correctAnswerIndex];
        if (answer.equalsIgnoreCase(correctAnswer) || answer.equalsIgnoreCase("option "+(correctAnswerIndex+1)))
            return true;
        else
            return false;
    }

    public boolean isAnswerValid(String answer)
    {
        if (answer.toLowerCase().matches("option [1-"+answers.length+"]"))
        {
            return true;
        }
        else
        {
            for (String validAnswer : answers)
            {
                if (validAnswer.equalsIgnoreCase(answer))
                    return true;
            }
        }
        return false;
    }

    public int getAnswerIndex(String answer) throws NoSuchElementException
    {
        try
        {
            if (answer.toLowerCase().matches("option [1-"+answers.length+"]"))
            {
                return (Integer.parseInt(answer.toLowerCase().replace("option ", ""))-1);
            }
            else
            {
                for (int i = 0; i<answers.length; i++)
                {
                    if (answers[i].equalsIgnoreCase(answer))
                        return i;
                }
            }
            throw new NoSuchElementException();
        }
        catch (IndexOutOfBoundsException | NumberFormatException e)
        {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    public EmbedBuilder getEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Question "+(questionNo)+" - "+getCategory())
                .setDescription(getQuestion());
        switch (difficulty)
        {
            case 1:
                embed.setThumbnail("https://i.imgur.com/M0axget.png");
                embed.setColor(Color.decode("#38BC23"));
                break;
            case 2:
                embed.setThumbnail("https://i.imgur.com/IlYb9PC.png");
                embed.setColor(Color.decode("#247AAF"));
                break;
            case 3:
                embed.setThumbnail("https://i.imgur.com/0hdxuiX.png");
                embed.setColor(Color.decode("#FF2626"));
                break;
        }
        for (int i = 0; i<answers.length; i++)
        {
            embed.addField("Option "+(i+1), answers[i], true);
        }
        return embed;
    }

    public int getQuestionNo()
    {
        return questionNo;
    }
}
