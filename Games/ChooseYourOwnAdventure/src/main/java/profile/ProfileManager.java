package profile;

import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            FileOutputStream fos = new FileOutputStream(getProfilePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(profiles);
            oos.close();
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
            if (new File(getProfilePath()).exists())
            {
                FileInputStream fis = new FileInputStream(getProfilePath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                profiles = (HashMap<String, Integer>) ois.readObject();
                ois.close();
                fis.close();
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
        catch (ClassNotFoundException e)
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
