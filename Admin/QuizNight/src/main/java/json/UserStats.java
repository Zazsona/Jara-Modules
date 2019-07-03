package json;

import java.io.Serializable;

public class UserStats implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int quizNightTotal;
    private int easyQuestionsTotal;
    private int mediumQuestionsTotal;
    private int hardQuestionsTotal;
    private int easyQuestionsCorrect;
    private int mediumQuestionsCorrect;
    private int hardQuestionsCorrect;
    private int wins;

    /**
     * Creates a new user stat profile with values of 0.
     */
    public UserStats()
    {
        quizNightTotal = 0;
        easyQuestionsTotal = 0;
        mediumQuestionsTotal = 0;
        hardQuestionsTotal = 0;
        easyQuestionsCorrect = 0;
        mediumQuestionsCorrect = 0;
        hardQuestionsCorrect = 0;
        wins = 0;
    }

    /**
     * Creates a new user stat profile with the values specified.
     * @param quizNightTotal the # of quiz nights participated in
     * @param easyQuestionsTotal the total number of easy questions answered over their career.
     * @param mediumQuestionsTotal the total number of medium questions answered over their career.
     * @param hardQuestionsTotal the total number of hard questions answered over their career.
     * @param wins the total number of Quiz Night wins
     * @param easyQuestionsCorrect the total number of easy questions answered correctly over their career.
     * @param mediumQuestionsCorrect the total number of medium questions answered correctly over their career.
     * @param hardQuestionsCorrect the total number of hard questions answered correctly over their career.
     */
    public UserStats(int quizNightTotal, int easyQuestionsTotal, int mediumQuestionsTotal, int hardQuestionsTotal, int wins, int easyQuestionsCorrect, int mediumQuestionsCorrect, int hardQuestionsCorrect)
    {
        this.quizNightTotal = quizNightTotal;
        this.easyQuestionsTotal = easyQuestionsTotal;
        this.mediumQuestionsTotal = mediumQuestionsTotal;
        this.hardQuestionsTotal = hardQuestionsTotal;
        this.easyQuestionsCorrect = easyQuestionsCorrect;
        this.mediumQuestionsCorrect = mediumQuestionsCorrect;
        this.hardQuestionsCorrect = hardQuestionsCorrect;
        this.wins = wins;
    }

    /**
     * Appends the specified values to the current quiz night records.
     * @param winner whether this user was on the winning team
     * @param easyQuestions the number of easy questions in the quiz
     * @param mediumQuestions the number of medium questions in the quiz
     * @param hardQuestions the number of hard questions in the quiz
     * @param easyQuestionsCorrect how many easy questions they got correct
     * @param mediumQuestionsCorrect how many medium questions they got correct
     * @param hardQuestionsCorrect how many hard questions they got correct
     */
    public void update(boolean winner, int easyQuestions, int mediumQuestions, int hardQuestions, int easyQuestionsCorrect, int mediumQuestionsCorrect, int hardQuestionsCorrect)
    {
        this.quizNightTotal += 1;
        this.easyQuestionsTotal += easyQuestions;
        this.mediumQuestionsTotal += mediumQuestions;
        this.hardQuestionsTotal += hardQuestions;
        this.easyQuestionsCorrect += easyQuestionsCorrect;
        this.mediumQuestionsCorrect += mediumQuestionsCorrect;
        this.hardQuestionsCorrect += hardQuestionsCorrect;
        if (winner)
        {
            wins += 1;
        }
    }

    /**
     * Gets quizNightTotal
     *
     * @return quizNightTotal
     */
    public int getQuizNightTotal()
    {
        return quizNightTotal;
    }

    /**
     * Sets the value of quizNightTotal
     *
     * @param quizNightTotal the value to set
     */
    public void setQuizNightTotal(int quizNightTotal)
    {
        this.quizNightTotal = quizNightTotal;
    }

    /**
     * Gets easyQuestionsTotal
     *
     * @return easyQuestionsTotal
     */
    public int getEasyQuestionsTotal()
    {
        return easyQuestionsTotal;
    }

    /**
     * Sets the value of easyQuestionsTotal
     *
     * @param easyQuestionsTotal the value to set
     */
    public void setEasyQuestionsTotal(int easyQuestionsTotal)
    {
        this.easyQuestionsTotal = easyQuestionsTotal;
    }

    /**
     * Gets mediumQuestionsTotal
     *
     * @return mediumQuestionsTotal
     */
    public int getMediumQuestionsTotal()
    {
        return mediumQuestionsTotal;
    }

    /**
     * Sets the value of mediumQuestionsTotal
     *
     * @param mediumQuestionsTotal the value to set
     */
    public void setMediumQuestionsTotal(int mediumQuestionsTotal)
    {
        this.mediumQuestionsTotal = mediumQuestionsTotal;
    }

    /**
     * Gets hardQuestionsTotal
     *
     * @return hardQuestionsTotal
     */
    public int getHardQuestionsTotal()
    {
        return hardQuestionsTotal;
    }

    /**
     * Sets the value of hardQuestionsTotal
     *
     * @param hardQuestionsTotal the value to set
     */
    public void setHardQuestionsTotal(int hardQuestionsTotal)
    {
        this.hardQuestionsTotal = hardQuestionsTotal;
    }

    /**
     * Gets easyQuestionsCorrect
     *
     * @return easyQuestionsCorrect
     */
    public int getEasyQuestionsCorrect()
    {
        return easyQuestionsCorrect;
    }

    /**
     * Sets the value of easyQuestionsCorrect
     *
     * @param easyQuestionsCorrect the value to set
     */
    public void setEasyQuestionsCorrect(int easyQuestionsCorrect)
    {
        this.easyQuestionsCorrect = easyQuestionsCorrect;
    }

    /**
     * Gets mediumQuestionsCorrect
     *
     * @return mediumQuestionsCorrect
     */
    public int getMediumQuestionsCorrect()
    {
        return mediumQuestionsCorrect;
    }

    /**
     * Sets the value of mediumQuestionsCorrect
     *
     * @param mediumQuestionsCorrect the value to set
     */
    public void setMediumQuestionsCorrect(int mediumQuestionsCorrect)
    {
        this.mediumQuestionsCorrect = mediumQuestionsCorrect;
    }

    /**
     * Gets hardQuestionsCorrect
     *
     * @return hardQuestionsCorrect
     */
    public int getHardQuestionsCorrect()
    {
        return hardQuestionsCorrect;
    }

    /**
     * Sets the value of hardQuestionsCorrect
     *
     * @param hardQuestionsCorrect the value to set
     */
    public void setHardQuestionsCorrect(int hardQuestionsCorrect)
    {
        this.hardQuestionsCorrect = hardQuestionsCorrect;
    }

    /**
     * Gets wins
     *
     * @return wins
     */
    public int getWins()
    {
        return wins;
    }

    /**
     * Sets the value of wins
     *
     * @param wins the value to set
     */
    public void setWins(int wins)
    {
        this.wins = wins;
    }
}
