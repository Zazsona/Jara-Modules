import configuration.SettingsUtil;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager implements Serializable
{
    private static long serialVersionUID = 1L;
    private static HashMap<String, String> guildToMessageMap; //GuildID : Message
    private static ArrayList<String> enabledGuilds;
    private static transient Logger logger = LoggerFactory.getLogger("WelcomeMessageModifier");

    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/WelcomeMessages.jara";
    }

    public synchronized void save()
    {
        try
        {
            File quoteFile = new File(getSavePath());
            if (!quoteFile.exists())
            {
                quoteFile.getParentFile().mkdirs();
                quoteFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(getSavePath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.toString());
        }
    }

    private synchronized void restore()
    {
        try
        {
            if (new File(getSavePath()).exists())
            {
                FileInputStream fis = new FileInputStream(getSavePath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileManager fm = (FileManager) ois.readObject();
                this.guildToMessageMap = fm.guildToMessageMap;
                this.enabledGuilds = fm.enabledGuilds;
                ois.close();
                fis.close();
            }
            else
            {
                guildToMessageMap = new HashMap<>();
                enabledGuilds = new ArrayList<>();
            }

        }
        catch (IOException e)
        {
            logger.error(e.toString());
            return;
        }
        catch (ClassNotFoundException e)
        {
            logger.error(e.toString());
            return;
        }
    }

    public void modifyGuildState(String guildID, boolean enable)
    {
        try
        {
            enabledGuilds.remove(guildID);
            if (enable)
            {
                enabledGuilds.add(guildID);
            }
            save();
        }
        catch (NullPointerException e)
        {
            restore();
            modifyGuildState(guildID, enable);
        }
    }

    public boolean isGuildEnabled(String guildID)
    {
        try
        {
            return enabledGuilds.contains(guildID);
        }
        catch (NullPointerException e)
        {
            restore();
            return isGuildEnabled(guildID);
        }
    }

    public void setWelcomeMessage(String guildID, String message)
    {
        try
        {
            guildToMessageMap.put(guildID, message);
            save();
        }
        catch (NullPointerException e)
        {
            restore();
            setWelcomeMessage(guildID, message);
        }
    }

    public String getWelcomeMessage(String guildID)
    {
        try
        {
            String message = guildToMessageMap.get(guildID);
            if (message == null)
            {
                message = "None.";
            }
            return message;
        }
        catch (NullPointerException e)
        {
            restore();
            return getWelcomeMessage(guildID);
        }
    }
}
