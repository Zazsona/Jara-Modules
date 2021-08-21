package com.zazsona.toptrumps.cards;

import java.io.Serializable;
import java.util.ArrayList;

public class Deck implements Serializable
{
    private static final long serialVersionUID = 1L;
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
