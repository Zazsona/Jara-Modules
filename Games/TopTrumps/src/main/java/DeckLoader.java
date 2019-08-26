import cards.Card;
import cards.Deck;
import configuration.SettingsUtil;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class DeckLoader
{
    public static Deck getDeck(String guildID, String[] parameters)
    {
        try
        {
            if (parameters.length > 2 || parameters.length == 2 && !(parameters[1].equalsIgnoreCase("ai") || parameters[1].equalsIgnoreCase("bot")))
            {
                for (String param : parameters)
                {
                    if (param.equalsIgnoreCase("Simpsons") || param.equalsIgnoreCase("TheSimpsons"))
                    {
                        return getSimpsonsDeck();
                    }
                    else if (param.equalsIgnoreCase("Games") || param.equalsIgnoreCase("GameCharacters") || param.equalsIgnoreCase("Gaming"))
                    {
                        return getGameCharacterDeck();
                    }
                }
                Deck deck = getCustomDeck(guildID, parameters);
                return deck;
            }
            return getRandomDeck(guildID);
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(DeckLoader.class).error(e.getLocalizedMessage());
            return getGameCharacterDeck();
        }
    }

    public static Deck getDeck(String guildID, String deckName)
    {
        try
        {
            if (deckName != null)
            {
                if (deckName.equalsIgnoreCase("Simpsons") || deckName.equalsIgnoreCase("TheSimpsons"))
                {
                    return getSimpsonsDeck();
                }
                else if (deckName.equalsIgnoreCase("Games") || deckName.equalsIgnoreCase("GameCharacters") || deckName.equalsIgnoreCase("Gaming"))
                {
                    return getGameCharacterDeck();
                }
                else
                {
                    Deck deck = getCustomDeck(guildID, deckName);
                    return deck;
                }
            }
            return getRandomDeck(guildID);
        }
        catch (IOException e)
        {
            LoggerFactory.getLogger(DeckLoader.class).error(e.getLocalizedMessage());
            return getGameCharacterDeck();
        }
    }

    public static Deck getRandomDeck(String guildID) throws IOException
    {
        int standardDeckCount = 2;
        Random r = new Random();
        HashMap<String, Deck> customDecksMap = getCustomDeckMap(guildID);
        int deckCount = customDecksMap.size()+standardDeckCount;
        int selection = r.nextInt(deckCount);
        switch (selection)
        {
            case 0:
                return getGameCharacterDeck();
            case 1:
                return getSimpsonsDeck();
            default:
                LinkedList<Deck> deckList = new LinkedList<>();
                deckList.addAll(customDecksMap.values());
                return deckList.get(selection-standardDeckCount);
        }
    }

    public static Deck getCustomDeck(String guildID, String deckName) throws IOException
    {
        HashMap<String, Deck> decks = getCustomDeckMap(guildID);
        return decks.get(deckName.toLowerCase());
    }

    public static Deck getCustomDeck(String guildID, String[] parameters) throws IOException
    {
        HashMap<String, Deck> decks = getCustomDeckMap(guildID);
        for (String param : parameters)
        {
            Deck deck = decks.get(param.toLowerCase());
            if (deck != null)
            {
                return deck;
            }
        }
        return null;
    }

    public static Deck getGameCharacterDeck()
    {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card("Tracer", "https://i.imgur.com/AzlLEh6.png", 1, 7, 118, 8));
        cards.add(new Card("Winston", "https://i.imgur.com/uPvtZ6Z.png", 8, 4, 250, 5));
        cards.add(new Card("Steve", "https://i.imgur.com/ChY8TM9.png", 7, 6, 132, 6));
        cards.add(new Card("Rabbit", "https://i.imgur.com/waX7uAf.png", 2, 1, 60, 9));
        cards.add(new Card("Teemo", "https://i.imgur.com/J2e72mD.png", 2, 9, 85, 6));
        cards.add(new Card("The Guide", "https://i.imgur.com/bpSNbvZ.png", 4, 5, 121, 6));
        cards.add(new Card("Mario", "https://i.imgur.com/ibkhTof.png", 5, 7, 119, 7));
        cards.add(new Card("Sonic", "https://i.imgur.com/hWZEzrF.png", 2, 7, 96, 10));
        cards.add(new Card("Dr. Eggman", "https://i.imgur.com/TIKCkGh.png", 5, 4, 300, 5));
        cards.add(new Card("Bowser", "https://i.imgur.com/OcZQHQK.png", 8, 9, 62, 2));
        cards.add(new Card("Zer0", "https://i.imgur.com/i87geCX.png", 2, 10, 102, 8));
        cards.add(new Card("Link", "https://i.imgur.com/nZfZ7ix.png", 5, 7, 87, 7));
        cards.add(new Card("Pikachu", "https://i.imgur.com/cpzTtln.png", 4, 9, 66, 8));
        cards.add(new Card("Cloud", "https://i.imgur.com/ecOd7H4.png", 7, 7, 112, 7));
        cards.add(new Card("Donkey Kong", "https://i.imgur.com/R4V5jLR.png", 10, 5, 13, 4));
        cards.add(new Card("Kirby", "https://i.imgur.com/etgbPn5.png", 6, 6, 51, 7));
        cards.add(new Card("Red Knight", "https://i.imgur.com/wKASjPB.png", 6, 7, 128, 4));
        cards.add(new Card("The Rocket Ball", "https://i.imgur.com/h6VjUeb.png", 6, 6, 41, 7));
        cards.add(new Card("Heavy", "https://i.imgur.com/mtHKOcE.png", 9, 6, 90, 4));
        cards.add(new Card("Spy", "https://i.imgur.com/LFvgaYc.png", 4, 5, 244, 7));
        cards.add(new Card("Sora", "https://i.imgur.com/3nxegcm.png", 5, 8, 88, 7));
        cards.add(new Card("Mercy", "https://i.imgur.com/4wCKgoB.png", 3, 2, 243, 8));
        cards.add(new Card("Dovahkiin", "https://i.imgur.com/sznPzc3.png", 6, 7, 86, 4));
        cards.add(new Card("Enderman", "https://i.imgur.com/4zHzv7K.png", 7, 4, 120, 6));
        Collections.shuffle(cards);
        Deck gcDeck = new Deck("Game Characters", cards, "HP", "Attack", "IQ", "Speed");
        return gcDeck;
    }

    public static Deck getSimpsonsDeck()
    {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new Card("Artie Ziff", "https://i.imgur.com/4U6SpSn.png", 4, 11, 132, 43, 1, 46));
        cards.add(new Card("Bleeding Gums Murphy", "https://i.imgur.com/OOIO6kW.png", 5, 6, 124, 23, 8, 16));
        cards.add(new Card("Blinky", "https://i.imgur.com/YAMCCHM.png", 5, 15, 10, 41, 1, 2));
        cards.add(new Card("Captain McCallister", "https://i.imgur.com/1oSJYrI.png", 5, 9, 111, 17, 2, 72));
        cards.add(new Card("Carl Carlson", "https://i.imgur.com/BzPfYQo.png", 4, 17, 117, 15, 8, 68));
        cards.add(new Card("Chief Clancy Wiggum", "https://i.imgur.com/FM9MoNY.png", 1, 5, 117, 43, 6, 80));
        cards.add(new Card("Crazy Old Man", "https://i.imgur.com/18WhIL8.png", 1, 3, 103, 46, 0, 82));
        cards.add(new Card("Dolph", "https://i.imgur.com/Rfque7o.png", 2, 5, 91, 40, 3, 78));
        cards.add(new Card("Dr. Julius Hibbert", "https://i.imgur.com/13aklt3.png", 1, 16, 123, 31, 7, 33));
        cards.add(new Card("Fat Tony", "https://i.imgur.com/36m0adW.png", 3, 18, 117, 39, 2, 82));
        cards.add(new Card("Homer Simpson", "https://i.imgur.com/JEwj8Xd.png", 0, 3, 79, 47, 7, 88));
        cards.add(new Card("Itchy", "https://i.imgur.com/XrJdtMy.png", 2, 13, 129, 47, 0, 99));
        cards.add(new Card("Jasper", "https://i.imgur.com/zOx42xi.png", 1, 8, 92, 42, 1, 89));
        cards.add(new Card("Kang & Kodos", "https://i.imgur.com/xILA3Cp.png", 3, 14, 129, 21, 0, 95));
        cards.add(new Card("Kent Brockman", "https://i.imgur.com/qhIgah6.png", 1, 17, 116, 46, 6, 20));
        cards.add(new Card("Maggie Simpson", "https://i.imgur.com/oD5JGA3.png", 3, 2, 120, 41, 10, 24));
        cards.add(new Card("Martin Prince", "https://i.imgur.com/pvCauFP.png", 5, 17, 139, 19, 7, 11));
        cards.add(new Card("Milhouse", "https://i.imgur.com/7kT4iVM.png", 3, 11, 108, 36, 5, 14));
        cards.add(new Card("Moe Szyslack", "https://i.imgur.com/Ln5rDwg.png", 2, 4, 94, 44, 0, 90));
        cards.add(new Card("Principal Skinner", "https://i.imgur.com/UnmfxxF.png", 5, 18, 127, 19, 3, 4));
        cards.add(new Card("Prof. John Frink", "https://i.imgur.com/wvZLPC1.png", 4, 16, 139, 13, 4, 37));
        cards.add(new Card("Rod & Todd Flanders", "https://i.imgur.com/vGkhU0U.png", 5, 18, 109, 11, 4, 8));
        cards.add(new Card("Santa's Little Helper", "https://i.imgur.com/HMrw38Z.png", 2, 7, 61, 35, 8, 93));
        cards.add(new Card("Sideshow Bob", "https://i.imgur.com/Rs5dvkS.png", 2, 17, 137, 33, 1, 91));
        cards.add(new Card("Krusty", "https://i.imgur.com/lHMYikv.png", 1, 7, 109, 48, 8, 77));
        cards.add(new Card("Ned Flanders", "https://i.imgur.com/TuWSUaR.png", 5, 18, 107, 35, 8, 6));
        cards.add(new Card("Marge Simpson", "https://i.imgur.com/NLBT16G.png", 4, 19, 107, 16, 8, 8));
        cards.add(new Card("Bart", "https://i.imgur.com/7dlhv0g.png", 1, 5, 95, 37, 7, 88));
        Collections.shuffle(cards);
        Deck spDeck = new Deck("The Simpsons", cards, "Good Listener", "Hygiene", "IQ", "Shamelessness", "Huggability", "Mayhem");
        return spDeck;
    }

    public static synchronized HashMap<String, Deck> getCustomDeckMap(String guildID) throws IOException
    {
        HashMap<String, HashMap<String, Deck>> guildDeckMap;
        try
        {
            FileInputStream fis = new FileInputStream(SettingsUtil.getModuleDataDirectory().getAbsolutePath()+"/TopTrumpDecks.jara");
            ObjectInputStream ois = new ObjectInputStream(fis);
            guildDeckMap = (HashMap<String, HashMap<String, Deck>>) ois.readObject();
            ois.close();
            fis.close();
            if (!guildDeckMap.containsKey(guildID))
            {
                guildDeckMap.put(guildID, new HashMap<>());
            }
            return guildDeckMap.get(guildID);

        }
        catch (IOException e)
        {
            guildDeckMap = new HashMap<>();
            if (!guildDeckMap.containsKey(guildID))
            {
                guildDeckMap.put(guildID, new HashMap<>());
            }
            return guildDeckMap.get(guildID);
        }
        catch (ClassNotFoundException e)
        {
            throw new IOException(e.getMessage());
        }
    }
}
