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
    private int fiftyFiftyIndex = -1;
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

    public boolean isAnswerValid(String answer) //TODO: Reject 50/50 ruleouts
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
                .setAuthor("Question "+(questionNo)+" - "+getCategory() + " || "+GameDriver.POUND_SIGN+getQuestionValue())
                .setDescription(getQuestion());
        switch (difficulty)
        {
            case 1:
                embed.setTitle("Easy");
                embed.setThumbnail("https://i.imgur.com/M0axget.png");
                embed.setColor(Color.decode("#38BC23"));
                break;
            case 2:
                embed.setTitle("Medium");
                embed.setThumbnail("https://i.imgur.com/IlYb9PC.png");
                embed.setColor(Color.decode("#247AAF"));
                break;
            case 3:
                embed.setTitle("Hard");
                embed.setThumbnail("https://i.imgur.com/0hdxuiX.png");
                embed.setColor(Color.decode("#FF2626"));
                break;
        }
        for (int i = 0; i<answers.length; i++)
        {
            if (fiftyFiftyIndex > -1 && (i != correctAnswerIndex && i != fiftyFiftyIndex))
                embed.addField("~~Option "+(i+1)+"~~", "~~"+answers[i]+"~~", true);
            else
                embed.addField("Option "+(i+1), answers[i], true);
        }
        return embed;
    }

    public void activateFiftyFifty()
    {
        Random r = new Random();
        int remainingWrongAnswerIndex = r.nextInt(answers.length);
        if (remainingWrongAnswerIndex != correctAnswerIndex)
        {
            fiftyFiftyIndex = remainingWrongAnswerIndex;
        }
        else
        {
            activateFiftyFifty();
        }
    }

    public int getQuestionNo()
    {
        return questionNo;
    }

    public int getQuestionValue()
    {
        switch (questionNo)
        {
            case 1:
                return 100;
            case 2:
                return 200;
            case 3:
                return 300;
            case 4:
                return 500;
            case 5:
                return 1000;
            case 6:
                return 2000;
            case 7:
                return 4000;
            case 8:
                return 8000;
            case 9:
                return 16000;
            case 10:
                return 32000;
            case 11:
                return 64000;
            case 12:
                return 125000;
            case 13:
                return 250000;
            case 14:
                return 500000;
            case 15:
                return 1000000;
        }
        return 0;
    }
}
