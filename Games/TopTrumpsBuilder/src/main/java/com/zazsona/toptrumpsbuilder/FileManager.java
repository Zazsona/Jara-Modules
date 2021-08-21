package com.zazsona.toptrumpsbuilder;

import com.zazsona.jara.configuration.SettingsUtil;
import com.zazsona.toptrumpsbuilder.cards.Deck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

public class FileManager
{
    private HashMap<String, Deck> deckMap;
    private String guildID;
    private static final Logger logger = LoggerFactory.getLogger("TopTrumps Deck Builder");

    public FileManager(String guildID) throws IOException
    {
        this.guildID = guildID;
        this.deckMap = restore().get(this.guildID);
    }

    /**
     * Gets the filepath for decks.
     * @return
     */
    private static String getDecksPath()
    {
        return SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/TopTrumpDecks.jara";
    }

    /**
     * Saves the modifications
     */
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
            logger.error(e.toString());
        }
    }

    /**
     * Loads the current deck data from file
     * @return the deck data
     * @throws IOException unable to load deck data
     */
    private synchronized HashMap<String, HashMap<String, Deck>> restore() throws IOException
    {
        HashMap<String, HashMap<String, Deck>> guildDeckMap;
        try
        {
            FileInputStream fis = new FileInputStream(getDecksPath());
            ObjectInputStream ois = new ObjectInputStream(fis);
            guildDeckMap = (HashMap<String, HashMap<String, Deck>>) ois.readObject();
            ois.close();
            fis.close();
            return guildDeckMap;

        }
        catch (IOException e)
        {
            guildDeckMap = new HashMap<>();
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

    /**
     * Gets the names of all decks for this guild
     * @return
     */
    public Set<String> getDeckNames()
    {
        return deckMap.keySet();
    }

    /**
     * Gets all decks for this guild
     * @return
     */
    public Set<Deck> getDecks()
    {
        return (Set<Deck>) deckMap.values();
    }

    /**
     * Gets a deck by name
     * @param deckName the deck name
     * @return the deck
     */
    public Deck getDeck(String deckName)
    {
        return deckMap.get(deckName.toLowerCase());
    }

    /**
     * Deletes the deck specified by name
     * @param deckName the deck
     * @return true on success
     */
    public boolean deleteDeck(String deckName)
    {
        Deck deckToDelete = deckMap.get(deckName.toLowerCase());
        if (deckToDelete != null)
        {
            deckMap.remove(deckToDelete.getName().toLowerCase());
            save();
            return true;
        }
        return false;
    }

    /**
     * Saves the deck to disk
     * @param deck the deck to save
     */
    public void saveDeck(Deck deck) throws InvalidParameterException
    {
        if (deck.getStatNames().length == 0)
        {
            throw new InvalidParameterException("This deck has no stats.");
        }
        else if (deck.getCards().size() <= 1)
        {
            throw new InvalidParameterException("A deck must have at least two cards.");
        }
        else
        {
            deckMap.put(deck.getName().toLowerCase(), deck);
            save();
        }
    }
}
