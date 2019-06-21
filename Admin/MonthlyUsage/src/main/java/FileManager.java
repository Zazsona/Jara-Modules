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
    private OffsetDateTime lastReset;
    private HashMap<String, HashMap<String, Integer>> commandUsage; //GuildID : UserID, CommandCount
    private static transient Logger logger = LoggerFactory.getLogger("CommandUsageLoader");

    public FileManager()
    {
        restore();
    }

    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/CommandUsage.jara";
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
            logger.error(e.getMessage());
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
                this.commandUsage = fm.commandUsage;
                this.lastReset = fm.lastReset;
                ois.close();
                fis.close();
            }
            else
            {
                commandUsage = new HashMap<>();
                lastReset = OffsetDateTime.now(ZoneOffset.UTC);
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

    public void addUsage(String guildID, String userID)
    {
        if (commandUsage.get(guildID) == null)
        {
            commandUsage.put(guildID, new HashMap<>());
        }
        if (!commandUsage.get(guildID).containsKey(userID))
        {
            commandUsage.get(guildID).put(userID, 1);
        }
        else
        {
            int recordedNum = commandUsage.get(guildID).get(userID);
            recordedNum++;
            commandUsage.get(guildID).put(userID, recordedNum);
        }
    }

    public void reset()
    {
        commandUsage = new HashMap<>();
        lastReset = OffsetDateTime.now(ZoneOffset.UTC);
        save();
    }

    public HashMap<String, HashMap<String, Integer>> getCommandUsage()
    {
        return commandUsage;
    }

    public HashMap<String, Integer> getGuildCommandUsage(String guildID)
    {
        return commandUsage.get(guildID);
    }

    public OffsetDateTime getLastReset()
    {
        return lastReset;
    }
}
