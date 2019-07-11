package cards;

import java.util.ArrayList;

public class Deck
{
    private String name;
    private String[] statNames;
    private ArrayList<Card> cards;

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
}
