package system;

import configuration.SettingsUtil;
import json.TriviaJson;
import json.UserStats;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiz.QuizTeam;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class UserStatManager
{
    /*
    ====================================================================================================================
                                                    Boilerplate

     */
    private static File quizUserStatsFile;
    private transient static Logger logger = LoggerFactory.getLogger(UserStatManager.class);
    private static HashMap<String, UserStats> userStatMap;

    private static File getQuizUserStatsFile()
    {
        try
        {
            if (quizUserStatsFile == null)
            {
                quizUserStatsFile = new File(SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/QuizUserStats.jara");
                if (!quizUserStatsFile.exists())
                {
                    quizUserStatsFile.createNewFile();
                    userStatMap = new HashMap<>();
                    save();
                }
            }
            return quizUserStatsFile;
        }
        catch (IOException e)
        {
            logger.error("Unable to create Quiz Night user stats file.\n"+e.getMessage());
            return null;
        }
    }

    private static synchronized HashMap<String, UserStats> restore()
    {
        try
        {
            FileInputStream fis = new FileInputStream(getQuizUserStatsFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            userStatMap = (HashMap<String, UserStats>) ois.readObject();
            ois.close();
            fis.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.error("Unable to read Quiz Night user stats file.\n"+e.getMessage());
        }
        return userStatMap;
    }

    private static synchronized void save()
    {
        try
        {
            File configFile = (getQuizUserStatsFile());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getQuizUserStatsFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userStatMap);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    public static void saveQuizNightStats(String winningTeamName, TriviaJson.TriviaQuestion[] questions, Collection<QuizTeam> teams)
    {
        restore();
        for (QuizTeam team : teams)
        {
            boolean isWinningTeam = team.getTeamName().equalsIgnoreCase(winningTeamName);
            for (Member member : team.getTeamMembers())
            {
                recordMemberStats(member.getUser(), team.getCorrectAnswers(), questions, isWinningTeam);
            }
        }
        save();
    }

    private static void recordMemberStats(User player, boolean[] correctAnswers, TriviaJson.TriviaQuestion[] questions, boolean winner)
    {
        recordMemberStats(player, correctAnswers, questions, questions.length, winner);
    }

    public static void recordMemberStats(User player, boolean[] correctAnswers, TriviaJson.TriviaQuestion[] questions, int currentQuestion, boolean winner)
    {
        int easyQuestions = 0;
        int easyQuestionsCorrect = 0;
        int mediumQuestions = 0;
        int mediumQuestionsCorrect = 0;
        int hardQuestions = 0;
        int hardQuestionsCorrect = 0;

        for (int i = 0; i<currentQuestion; i++)
        {
            switch (questions[i].difficulty)        //Bloody hell this is ugly.
            {
                case "easy":
                    easyQuestions++;
                    if (correctAnswers[i])
                        easyQuestionsCorrect++;
                    break;
                case "medium":
                    mediumQuestions++;
                    if (correctAnswers[i])
                        mediumQuestionsCorrect++;
                    break;
                case "hard":
                    hardQuestions++;
                    if (correctAnswers[i])
                        hardQuestionsCorrect++;
                    break;
            }
        }

        if (userStatMap.containsKey(player.getId()))
        {
            userStatMap.get(player.getId()).update(winner, easyQuestions, mediumQuestions, hardQuestions, easyQuestionsCorrect, mediumQuestionsCorrect, hardQuestionsCorrect);
        }
        else
        {
            int winCount = (winner) ? 1 : 0;
            userStatMap.put(player.getId(), new UserStats(1, easyQuestions, mediumQuestions, hardQuestions, winCount, easyQuestionsCorrect, mediumQuestionsCorrect, hardQuestionsCorrect));
        }
        //There is no save here, as a save is conducted at the end of a quiz night.
    }


}