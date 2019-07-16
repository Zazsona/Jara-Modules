import cards.Deck;
import configuration.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class FileManager
{
    private HashMap<String, Deck> deckMap;
    private String guildID;
    private static Logger logger = LoggerFactory.getLogger("TopTrumps Deck Builder");

    public FileManager(String guildID) throws IOException
    {
        deckMap = restore().get(guildID);
        this.guildID = guildID;
    }

    private static String getDecksPath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/TopTrumpDecks.jara";
    }

    private synchronized void save()
    {
        try
        {
            File deckFile = new File(getDecksPath());
            if (!deckFile.exists())
            {
                deckFile.createNewFile();
            }
            HashMap<String, HashMap<String, Deck>> guildDeckMap = restore(); //This ensures we're always working with the newest version of the file, so we don't discard other thread's changes.
            guildDeckMap.put(guildID, deckMap);
            FileOutputStream fos = new FileOutputStream(getDecksPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(guildDeckMap);
            oos.close();
            fos.close();
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }

    private synchronized HashMap<String, HashMap<String, Deck>> restore() throws IOException
    {
        Logger logger = LoggerFactory.getLogger(FileManager.class);
        try
        {
            HashMap<String, HashMap<String, Deck>> guildDeckMap = new HashMap<>();
            if (new File(getDecksPath()).exists())
            {
                FileInputStream fis = new FileInputStream(getDecksPath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                guildDeckMap = (HashMap<String, HashMap<String, Deck>>) ois.readObject();
                ois.close();
                fis.close();
            }
            else
            {
                guildDeckMap = new HashMap<>();
            }

            if (!guildDeckMap.containsKey(guildID))
            {
                guildDeckMap.put(guildID, new HashMap<>());
            }
            return guildDeckMap;

        }
        catch (ClassNotFoundException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public Set<String> getDeckNames()
    {
        return deckMap.keySet();
    }

    public Set<Deck> getDecks()
    {
        return (Set<Deck>) deckMap.values();
    }
    
    public Deck getDeck(String deckName) throws IOException
    {
        return deckMap.get(deckName.toLowerCase());
    }

    public boolean deleteDeck(String deckName)
    {
        Deck result = deckMap.remove(deckName.toLowerCase());
        if (result == null)
        {
            save();
            return true;
        }
        return false;
    }

    public boolean saveDeck(Deck deck)
    {
        //if (deck.getCards().size() % 2 == 0)
        //{
            deckMap.put(deck.getName().toLowerCase(), deck);
            save();
            return true;
        //}
        //else
        //{
        //    return false;
        //}
    }
}
