package com.Zazsona.TopTrumps;

import com.Zazsona.TopTrumps.cards.Card;
import com.Zazsona.TopTrumps.cards.Deck;

public class AI
{
    private double[] categoryAverages;

    public AI(Deck deck)
    {
        categoryAverages = new double[deck.getStatNames().length];
        for (Card card : deck.getCards())
        {
            for (int i = 0; i<categoryAverages.length; i++)
            {
                categoryAverages[i] += card.getStats()[i];
            }
        }
        for (int i = 0; i<categoryAverages.length; i++)
        {
            categoryAverages[i] = categoryAverages[i]/deck.getCards().size();
        }
    }

    public int getStatSelection(Card card)
    {
        int highestIndex = 0;
        double highestOffset = 0;
        for (int i = 0; i<card.getStats().length; i++)
        {
            double offset = card.getStats()[i]-categoryAverages[i];
            if (offset > highestOffset)
            {
                highestIndex = i;
                highestOffset = offset;
            }
        }
        return highestIndex;
    }
}
