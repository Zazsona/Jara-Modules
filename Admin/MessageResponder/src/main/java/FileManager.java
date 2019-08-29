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
import java.util.LinkedHashMap;

public class FileManager implements Serializable
{
    private static long serialVersionUID = 1L;
    private LinkedHashMap<String, HashMap<String, String>> guildToMessagesMap; //GuildID : ReceiveMessage, ResponseMessage
    private static transient Logger logger = LoggerFactory.getLogger("MessageResponseLoader");

    public FileManager()
    {
        restore();
    }


    private String getSavePath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/MessageResponses.jara";
    }

    public synchronized void save()
    {
        try
        {
            File file = new File(getSavePath());
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
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
                this.guildToMessagesMap = fm.guildToMessagesMap;
                ois.close();
                fis.close();
            }
            else
            {
                guildToMessagesMap = new LinkedHashMap<>();

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

    public void addMessageResponse(String guildID, String receivedMessage, String responseMessage)
    {
        if (!guildToMessagesMap.containsKey(guildID))
        {
            guildToMessagesMap.put(guildID, new HashMap<>());
        }
        guildToMessagesMap.get(guildID).put(receivedMessage.toLowerCase(), responseMessage);
        save();
    }

    public void removeMessageResponse(String guildID, String receivedMessage)
    {
        guildToMessagesMap.get(guildID).remove(receivedMessage.toLowerCase());
        save();
    }

    public HashMap<String, String> getMessageResponses(String guildID)
    {
        return guildToMessagesMap.get(guildID);
    }

    public boolean doesGuildHaveResponses(String guildID)
    {
        return (guildToMessagesMap.get(guildID) != null && guildToMessagesMap.get(guildID).size() > 0);
    }


}
