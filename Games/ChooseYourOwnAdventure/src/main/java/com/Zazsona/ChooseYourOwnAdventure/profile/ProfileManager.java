package com.Zazsona.ChooseYourOwnAdventure.profile;

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

public class ProfileManager
{
    private static HashMap<String, Integer> profiles;
    private static Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    private static String getProfilePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/CYOAProfiles.jara";
    }

    private static synchronized void save()
    {
        try
        {
            File configFile = new File(getProfilePath());
            if (!configFile.exists())
            {
                configFile.createNewFile();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(profiles);
            FileOutputStream fos = new FileOutputStream(getProfilePath());
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private static synchronized void restore()
    {
        try
        {
            File profileFile = new File(getProfilePath());
            if (profileFile.exists())
            {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = new String(Files.readAllBytes(profileFile.toPath()));
                TypeToken<HashMap<String, Integer>> token = new TypeToken<HashMap<String, Integer>>() {};
                profiles = gson.fromJson(json, token.getType());
            }
            else
            {
                profiles = new HashMap<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
            return;
        }
    }
    private static synchronized HashMap<String, Integer> getProfiles()
    {
        if (profiles == null)
        {
            restore();
        }
        return profiles;
    }
    
    public static synchronized void addProfile(String userID, int nodeID)
    {
        getProfiles().put(userID, nodeID);
        save();
    }

    public static synchronized void removeProfile(String userID)
    {
        getProfiles().remove(userID);
        save();
    }

    public static int getProfileProgress(String userID)
    {
        getProfiles();
        if (profiles.containsKey(userID))
        {
            return profiles.get(userID);
        }
        else
        {
            return 0;
        }
    }
}
