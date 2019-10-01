package com.Zazsona.QuizNight.system;

import com.Zazsona.QuizNight.json.QuizSettings;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class SettingsManager
{
    /*
    ====================================================================================================================
                                                    Boilerplate

     */
    private static File quizSettingsFile;
    private transient static Logger logger = LoggerFactory.getLogger(SettingsManager.class);
    private static QuizSettings quizSettings;

    private static File getQuizSettingsFile()
    {
        try
        {
            if (quizSettingsFile == null)
            {
                quizSettingsFile = new File(SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/QuizSettings.jara");
                if (!quizSettingsFile.exists())
                {
                    quizSettingsFile.createNewFile();
                    quizSettings = new QuizSettings();
                    save();
                }
            }
            return quizSettingsFile;
        }
        catch (IOException e)
        {
            logger.error("Unable to create Quiz Night settings file.\n"+e.toString());
            return null;
        }
    }

    public static synchronized QuizSettings restore()
    {
        try
        {
            FileInputStream fis = new FileInputStream(getQuizSettingsFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            quizSettings = (QuizSettings) ois.readObject();
            ois.close();
            fis.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.error("Unable to read Quiz Night settings file.\n"+e.toString());
            quizSettings = new QuizSettings();
        }
        return quizSettings;
    }

    public static synchronized void save()
    {
        try
        {
            File configFile = (getQuizSettingsFile());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getQuizSettingsFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(quizSettings);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
    }

    public static QuizSettings.GuildQuizConfig getGuildQuizSettings(long guildID)
    {
        int index = getGuildQuizSettingsIndex(guildID);
        if (index > -1)
        {
            return quizSettings.GuildQuizConfigs[index];
        }
        else
        {
            return addGuildQuizConfig(guildID);
        }

    }

    public static int getGuildQuizSettingsIndex(long guildID)
    {
        int min = 0;
        int max = quizSettings.GuildQuizConfigs.length;
        while (min != max)
        {
            int mid = (max+min)/2;
            long midGuild = Long.parseLong(quizSettings.GuildQuizConfigs[mid].GuildID);
            if (midGuild == guildID)
            {
                return mid;
            }
            else if (midGuild < guildID)
            {
                min = mid;
            }
            else if (midGuild > guildID)
            {
                max = mid;
            }
        }
        return -1;
    }

    /**
     * Adds a guild to the database with default settings.<br>
     * Overall, this is quite an expensive operation. Only call if the guild config doesn't exist at all.
     * @param guildID
     * @return
     */
    private static QuizSettings.GuildQuizConfig addGuildQuizConfig(long guildID)
    {
        if (quizSettings.GuildQuizConfigs.length > 0)
        {
            Boolean isLastGreater = null;
            int min = 0;
            int max = quizSettings.GuildQuizConfigs.length;
            while (min != max)
            {
                int mid = (max+min)/2;
                long midGuild = Long.parseLong(quizSettings.GuildQuizConfigs[mid].GuildID);
                if (midGuild < guildID)
                {
                    min = mid;
                    if (isLastGreater)
                    {
                        break;
                    }
                    else
                    {
                        isLastGreater = false;
                    }
                }
                else if (midGuild > guildID)                            //Quick binary search to find the general area it will fit.
                {
                    max = mid;
                    if (!isLastGreater)
                    {
                        break;
                    }
                    else
                    {
                        isLastGreater = true;
                    }
                }
            }

            int location = 0;
            for (int i = min; i<(max+1); i++)
            {
                long currentGuild = Long.parseLong(quizSettings.GuildQuizConfigs[i].GuildID);
                if (currentGuild > guildID)                                                         //Less efficient linear search on the small area found by binary search
                {
                    location = i;
                }
            }

            QuizSettings.GuildQuizConfig[] newGuildQuizConfigs = new QuizSettings.GuildQuizConfig[quizSettings.GuildQuizConfigs.length+1];
            for (int i = 0; i<quizSettings.GuildQuizConfigs.length; i++)
            {
                if (i<location)
                {
                    newGuildQuizConfigs[i] = quizSettings.GuildQuizConfigs[i];
                }
                else if (i == location)
                {
                    QuizSettings.GuildQuizConfig newGuildQuizConfig = new QuizSettings.GuildQuizConfig();
                    newGuildQuizConfig.GuildID = String.valueOf(guildID);
                    newGuildQuizConfigs[i] = newGuildQuizConfig;
                }
                else
                {
                    newGuildQuizConfigs[i+1] = quizSettings.GuildQuizConfigs[i];
                }
            }
            quizSettings.GuildQuizConfigs = newGuildQuizConfigs;
            save();
            Scheduler.resetScheduling(quizSettings.GuildQuizConfigs[location]);
            return quizSettings.GuildQuizConfigs[location];
        }
        else
        {
            quizSettings.GuildQuizConfigs = new QuizSettings.GuildQuizConfig[1];
            quizSettings.GuildQuizConfigs[0] = new QuizSettings.GuildQuizConfig();
            quizSettings.GuildQuizConfigs[0].GuildID = String.valueOf(guildID);
            save();
            Scheduler.resetScheduling(quizSettings.GuildQuizConfigs[0]);
            return quizSettings.GuildQuizConfigs[0];
        }


    }

    //==================================================================================================================

    /**
     * Gets the guild ids and times for the specified day.<br>
     * Days are set by ISO-8601 (1 is Monday, 7 is Sunday).
     * @param dayOfWeek
     * @return
     */
    public static ArrayList<QuizSettings.GuildQuizConfig> getDayQuizzes(int dayOfWeek)
    {
        restore();
        ArrayList<QuizSettings.GuildQuizConfig> todaysQuizzes = new ArrayList<>();
        for (QuizSettings.GuildQuizConfig guildQuizConfig : quizSettings.GuildQuizConfigs)
        {
            if (guildQuizConfig.Days[(dayOfWeek-1)])
            {
                todaysQuizzes.add(guildQuizConfig);
            }
        }
        return todaysQuizzes;
    }

    /**
     * Sets whether to run a quiz on the specified day. All days are disabled by default.<br>
     * Days are set by ISO-8601 (1 is Monday, 7 is Sunday).
     * @param guildID the guild to modify
     * @param dayOfWeek the day of the week
     * @param runQuiz whether to run a quiz on this day
     */
    public static QuizSettings.GuildQuizConfig setGuildQuizDay(long guildID, int dayOfWeek, boolean runQuiz)
    {
        restore();
        QuizSettings.GuildQuizConfig gqc = getGuildQuizSettings(guildID);
        gqc.Days[dayOfWeek-1] = runQuiz;
        save();
        Scheduler.resetScheduling(gqc);
        return gqc;
    }

    /**
     * The minute of the day with which to start the quiz.<br>
     * @param guildID the guild to edit
     * @param startMinute
     */
    public static QuizSettings.GuildQuizConfig setGuildQuizTime(long guildID, int startMinute)
    {
        restore();
        QuizSettings.GuildQuizConfig gqc = getGuildQuizSettings(guildID);
        gqc.StartMinute = startMinute-5; //This launches the quiz 5 minutes before it's scheduled, allowing us to prompt players to join with a 5 minute window.
        save();
        Scheduler.resetScheduling(gqc);
        return gqc;
    }

    /**
     * Sets whether or not to ping when a quiz starts.
     * @param guildId
     * @param ping
     * @return
     */
    public static QuizSettings.GuildQuizConfig updateAnnouncementPing(long guildId, boolean ping)
    {
        restore();
        QuizSettings.GuildQuizConfig gqc = getGuildQuizSettings(guildId);
        gqc.PingQuizAnnouncement = ping;
        save();
        return gqc;
    }

    /**
     * Returns true if a quiz will run of the specified day.
     * @param guildID the guild config
     * @param dayOfWeek the day of the week
     * @return
     */
    public static boolean isGuildQuizDay(long guildID, int dayOfWeek)
    {
        QuizSettings.GuildQuizConfig gqc = getGuildQuizSettings(guildID);
        return gqc.Days[dayOfWeek-1];
    }

    public static QuizSettings.GuildQuizConfig toggleAllowedRoles(long guildID, String... roleIDs)
    {
        restore();
        ArrayList<String> allowedRoles = new ArrayList<>();
        int index = getGuildQuizSettingsIndex(guildID);
        for (String role : roleIDs)
        {
            allowedRoles.add(role);
        }
        for (String role : quizSettings.GuildQuizConfigs[index].AllowedRoles)
        {
            if (allowedRoles.contains(role))
            {
                allowedRoles.remove(role);
            }
            else
            {
                allowedRoles.add(role);
            }
        }
        quizSettings.GuildQuizConfigs[index].AllowedRoles = allowedRoles.toArray(new String[0]);
        save();
        return quizSettings.GuildQuizConfigs[index];
    }

}