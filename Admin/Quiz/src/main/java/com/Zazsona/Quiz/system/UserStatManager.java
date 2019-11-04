package com.Zazsona.Quiz.system;

import com.Zazsona.Quiz.json.TriviaJson;
import com.Zazsona.Quiz.json.UserStats;
import com.Zazsona.Quiz.quiz.QuizTeam;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
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
            logger.error("Unable to create Quiz Night user stats file.\n"+e.toString());
            return null;
        }
    }

    private static synchronized HashMap<String, UserStats> restore()
    {
        try
        {
            File file = getQuizUserStatsFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = new String(Files.readAllBytes(file.toPath()));
            TypeToken<HashMap<String, UserStats>> token = new TypeToken<HashMap<String, UserStats>>() {};
            userStatMap = gson.fromJson(json, token.getType());
        }
        catch (IOException e)
        {
            logger.error("Unable to read Quiz Night user stats file.\n"+e.toString());
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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(userStatMap);
            FileOutputStream fos = new FileOutputStream(configFile.getPath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.toString());
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
        //There is no save here, as a save is conducted at the end of a quiz night.
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
            switch (questions[i].difficulty)
            {
                case "easy":
                    easyQuestions++;
                    if (correctAnswers[i])
                    {
                        easyQuestionsCorrect++;
                    }
                    break;
                case "medium":
                    mediumQuestions++;
                    if (correctAnswers[i])
                    {
                        mediumQuestionsCorrect++;
                    }
                    break;
                case "hard":
                    hardQuestions++;
                    if (correctAnswers[i])
                    {
                        hardQuestionsCorrect++;
                    }
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