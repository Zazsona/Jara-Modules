package com.zazsona.toptrumpsbuilder.cards;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Deck implements Serializable
{
    private static final long serialVersionUID = 1L;
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

    /**
     * Sets the name of the deck
     * @param newName the nam to set
     * @throws InvalidParameterException name is less than 2 or greater than 25 characters
     */
    public void setName(String newName) throws InvalidParameterException
    {
        if (name.length() > 1 && name.length() <= 25)
        {
            name = newName.replace(" ", "");
        }
        else
        {
            throw new InvalidParameterException("Names must be between 2 and 25 characters.");
        }
    }


    /**
     * Sets the stat categories, and their titles.
     * @param newStatNames th stat categories
     * @throws InvalidParameterException Stat total is below 2 or above 6 OR a stat name is below 2 or greater than 25 characters.
     */
    public void setStatNames(String... newStatNames) throws InvalidParameterException
    {
        if (newStatNames.length > 6 || newStatNames.length <= 1)
        {
            throw new InvalidParameterException("There must be between 2 and 6 stats.");
        }
        else
        {
            for (String statName : newStatNames)
            {
                if (statName.length() <= 1 || statName.length() > 25)
                {
                    throw new InvalidParameterException("Stat names must be between 2 and 25 characters.");
                }
            }
        }
        this.statNames = newStatNames;
    }

    /**
     * Adds a card to the deck
     * @param card the card to add
     * @return true on success
     * @throws IllegalArgumentException a card with that name already exists in the deck
     * @throws IndexOutOfBoundsException the deck is full (30 cards)
     */
    public boolean addCard(Card card) throws IllegalArgumentException, IndexOutOfBoundsException
    {
        if (cards.size() >= 30)
        {
            throw new IndexOutOfBoundsException("This deck has the maximum 30 cards.");
        }
        for (Card existingCard : cards)
        {
            if (existingCard.getName().equalsIgnoreCase(card.getName()))
            {
                throw new IllegalArgumentException("A card with this name already exists!");
            }
        }
        return cards.add(card);
    }

    /**
     * Removes a card from the deck
     * @param card the card to remove
     * @return true on success.
     */
    public boolean removeCard(Card card)
    {
        return cards.remove(card);
    }

    /**
     * Gets the specified card by name
     * @param cardName the card to retrieve
     * @return the card, or null if no card with that name exists.
     */
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
