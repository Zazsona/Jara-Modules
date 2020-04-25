package com.Zazsona.Quiz.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsManager
{
    private static transient SettingsManager settingsManager;
    private transient static Logger logger = LoggerFactory.getLogger(SettingsManager.class);
    private HashMap<String, QuizBuilder> guildQuizConfigs;

    private SettingsManager()
    {

    }

    public static SettingsManager getInstance()
    {
        if (settingsManager == null)
        {
            settingsManager = new SettingsManager();
            settingsManager.restore();
        }
        return settingsManager;
    }

    private File getQuizSettingsFile()
    {
        return new File(SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/QuizSettings.jara");
    }

    private synchronized void restore()
    {
        try
        {
            if (settingsManager.guildQuizConfigs == null)
            {
                File quizFile = getQuizSettingsFile();
                if (quizFile.exists())
                {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = new String(Files.readAllBytes(quizFile.toPath()));
                    TypeToken<HashMap<String, QuizBuilder>> token = new TypeToken<HashMap<String, QuizBuilder>>() {};
                    settingsManager.guildQuizConfigs = gson.fromJson(json, token.getType());
                }
                else
                {
                    settingsManager.guildQuizConfigs = new HashMap<>();
                }
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }

    public synchronized void save()
    {
        try
        {
            File quizFile = (getQuizSettingsFile());
            if (!quizFile.exists())
            {
                quizFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(settingsManager.guildQuizConfigs);
            FileOutputStream fos = new FileOutputStream(quizFile);
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

    public QuizBuilder getGuildQuizBuilder(String guildID)
    {
        QuizBuilder quizBuilder =  settingsManager.guildQuizConfigs.get(guildID);
        if (quizBuilder == null)
        {
            quizBuilder = new QuizBuilder(guildID);
            settingsManager.guildQuizConfigs.put(guildID, quizBuilder);
        }
        return quizBuilder;
    }

    public void setQuizBuilder(QuizBuilder quizBuilder)
    {
        settingsManager.guildQuizConfigs.replace(quizBuilder.getGuildID(), quizBuilder);
        save();
    }

    public void removeQuizBuilder(String guildID)
    {
        settingsManager.guildQuizConfigs.remove(guildID);
        save();
    }

    //==================================================================================================================

    /**
     * Gets the GuildQuizConfig and times for the specified day.<br>
     * Days are set by ISO-8601 (1 is Monday, 7 is Sunday).
     * @param dayValue
     * @return
     */
    public ArrayList<QuizBuilder> getDayQuizzes(int dayValue)
    {
        restore();
        ArrayList<QuizBuilder> todaysQuizzes = new ArrayList<>();
        for (QuizBuilder quizBuilder : guildQuizConfigs.values())
        {
            if (quizBuilder.getDay(dayValue).hasQuiz())
            {
                todaysQuizzes.add(quizBuilder);
            }
        }
        return todaysQuizzes;
    }

    /**
     * Gets the guild ids and times for the specified day.<br>
     * Days are set by ISO-8601 (1 is Monday, 7 is Sunday).
     * @param dayValue
     * @return
     */
    public ArrayList<String> getDayQuizzesGuildIDs(int dayValue)
    {
        restore();
        ArrayList<String> todaysQuizzes = new ArrayList<>();
        for (QuizBuilder quizBuilder : guildQuizConfigs.values())
        {
            if (quizBuilder.getDay(dayValue).hasQuiz())
            {
                todaysQuizzes.add(quizBuilder.getGuildID());
            }
        }
        return todaysQuizzes;
    }

}