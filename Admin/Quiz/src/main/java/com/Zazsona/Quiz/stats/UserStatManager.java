package com.Zazsona.Quiz.stats;

import com.Zazsona.Quiz.api.TriviaResponse;
import com.Zazsona.Quiz.quiz.QuizTeam;
import com.Zazsona.Quiz.quiz.Trivia;
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
            logger.error("Unable to create Quiz user stats file.\n"+e.toString());
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
            logger.error("Unable to read Quiz user stats file.\n"+e.toString());
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

    public static void saveQuizStats(QuizTeam winningTeam, Trivia[] trivia, Collection<QuizTeam> teams)
    {
        restore();
        for (QuizTeam team : teams)
        {
            boolean isWinningTeam = team.equals(winningTeam);
            for (Member member : team.getMembers())
            {
                recordMemberStats(member.getUser(), team.getCorrectAnswers(), trivia, isWinningTeam);
            }
        }
        save();
    }

    public static void recordMemberStats(User player, boolean[] correctAnswers, Trivia[] trivia, boolean winner)
    {
        int easyQuestions = 0;
        int easyQuestionsCorrect = 0;
        int mediumQuestions = 0;
        int mediumQuestionsCorrect = 0;
        int hardQuestions = 0;
        int hardQuestionsCorrect = 0;

        for (int i = 0; i<trivia.length; i++)
        {
            switch (trivia[i].getPoints())
            {
                case 1:
                    easyQuestions++;
                    if (correctAnswers[i])
                    {
                        easyQuestionsCorrect++;
                    }
                    break;
                case 2:
                    mediumQuestions++;
                    if (correctAnswers[i])
                    {
                        mediumQuestionsCorrect++;
                    }
                    break;
                case 3:
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
    }


}