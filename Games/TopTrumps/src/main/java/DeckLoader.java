import cards.Card;
import cards.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DeckLoader
{
    public static Deck getRandomDeck()
    {
        Random r = new Random();
        switch (r.nextInt(1))
        {
            case 0:
                return getGameCharacterDeck();

            default:
                return getGameCharacterDeck();
        }
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
}
