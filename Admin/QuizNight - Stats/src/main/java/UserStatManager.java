import configuration.SettingsUtil;
import json.UserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    public static synchronized HashMap<String, UserStats> restore()
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

}