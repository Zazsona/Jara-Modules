package cards;

import java.io.Serializable;
import java.util.ArrayList;

public class Deck implements Serializable
{
    private static long serialVersionUID = 1L;
    private String name;
    private String[] statNames;
    private ArrayList<Card> cards;

    public Deck(String name)
    {
        this.name = name;
        statNames = new String[0];
        cards = new ArrayList<>();
    }

    public Deck(String name, ArrayList<Card> cards, String... statNames)
    {
        this.name = name;
        this.cards = cards;
        this.statNames = statNames;
    }

    /**
     * Gets name
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets statNames
     *
     * @return statNames
     */
    public String[] getStatNames()
    {
        return statNames;
    }

    /**
     * Gets cards
     *
     * @return cards
     */
    public ArrayList<Card> getCards()
    {
        return cards;
    }

    public boolean setName(String newName)
    {
        if (name.length() > 1 && name.length() <= 25)
        {
            name = newName.replace(" ", "");
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setStatNames(String... newStatNames)
    {
        if (statNames.length > 6 || statNames.length <= 1)
        {
            return false;
        }
        else
        {
            for (String statName : statNames)
            {
                if (statName.length() <= 1 || statName.length() > 25)
                {
                    return false;
                }
            }
        }
        this.statNames = statNames;
        return true;
    }

    public boolean addCard(Card card)
    {
        for (Card existingCard : cards)
        {
            if (existingCard.getName().equalsIgnoreCase(card.getName()))
            {
                return false;
            }
        }
        return cards.add(card);
    }

    public boolean removeCard(Card card)
    {
        return cards.remove(card);
    }

    public Card getCard(String cardName)
    {
        for (Card card : cards)
        {
            if (card.getName().equalsIgnoreCase(cardName))
            {
                return card;
            }
        }
        return null;
    }



}
