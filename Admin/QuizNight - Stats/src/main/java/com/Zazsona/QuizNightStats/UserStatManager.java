package com.Zazsona.QuizNightStats;

import com.Zazsona.QuizNightStats.json.UserStats;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
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

    public static synchronized HashMap<String, UserStats> restore()
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


}