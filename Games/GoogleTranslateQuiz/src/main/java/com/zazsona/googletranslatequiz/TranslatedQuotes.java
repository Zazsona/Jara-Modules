package com.zazsona.googletranslatequiz;

public class TranslatedQuotes
{
    private static String[][] quotes;

    public static String[][] getQuotes()
    {
        if (quotes == null)
        {
            quotes = new String[38][2]; //TODO: Remember to increase capacity
            quotes[0][0] = "Frankly, dear, I do not die";
            quotes[0][1] = "gone with the wind";
            quotes[1][0] = "I'll let him be a quote he can not refuse";
            quotes[1][1] = "godfather";
            quotes[2][0] = "The squad can be with you.";
            quotes[2][1] = "star wars";
            quotes[3][0] = "E.T. Mobile home";
            quotes[3][1] = "e.t";
            quotes[4][0] = "The Golden nest is not as good as the kennel in his home.";
            quotes[4][1] = "wizard of oz";
            quotes[5][0] = "You need a great boat deprementis";
            quotes[5][1] = "jaws";
            quotes[6][0] = "Then go back";
            quotes[6][1] = "terminator";
            quotes[7][0] = "Mommy says life is like chocolate";
            quotes[7][1] = "forrest gump";
            quotes[8][0] = "He is still alive and alive";
            quotes[8][1] = "frankenstein";
            quotes[9][0] = "Keep your friends, but the enemies are closer.";
            quotes[9][1] = "godfather";
            quotes[10][0] = "around johnny";
            quotes[10][1] = "shining";
            quotes[11][0] = "See you, baby";
            quotes[11][1] = "terminator";
            quotes[12][0] = "If I let my daughter go now, it's over. I'll never find you, I'll never pursue you. But if you do, I find you, I find you, I kill you.";
            quotes[12][1] = "taken";
            quotes[13][0] = "Chew, we're at home";
            quotes[13][1] = "star wars";
            quotes[14][0] = "Take you from the stinky claws and you are removed by the cursed dirty monkey!";
            quotes[14][1] = "planet of the apes";
            quotes[15][0] = "They call it royal and cheese.";
            quotes[15][1] = "pulp fiction";
            quotes[16][0] = "Mirror on the wall, which is the most appropriate?";
            quotes[16][1] = "snow white";
            quotes[17][0] = "Primary, dear watch";
            quotes[17][1] = "sherlock";
            quotes[18][0] = "That is the pig. This way.";
            quotes[18][1] = "babe";
            quotes[19][0] = "I'm precious";
            quotes[19][1] = "lord of the rings";
            quotes[20][0] = "Hakuna Matata";
            quotes[20][1] = "lion king";
            quotes[21][0] = "But you heard me.";
            quotes[21][1] = "pirates of the caribbean";
            quotes[22][0] = "A friendly day is like a pot without a single honeycomb.";
            quotes[22][1] = "winnie the pooh";
            quotes[23][0] = "As I see, all life is good and bad. Good things are not always bad, so bad, bad things do not even know what is good and make them great.";
            quotes[23][1] = "doctor who";
            quotes[24][0] = "In the end, we are in stories. Just be good.";
            quotes[24][1] = "doctor who";
            quotes[25][0] = "I just tell you: you were fantastic. Totally fantastic. And what do you know? That's what I did.";
            quotes[25][1] = "doctor who";
            quotes[26][0] = "Oh my God! Kenny's dead!";
            quotes[26][1] = "south park";
            quotes[27][0] = "Ruh Roh";
            quotes[27][1] = "scooby";
            quotes[28][0] = "Way? Where do we go, we do not need a way.";
            quotes[28][1] = "back to the future";
            quotes[29][0] = "You should turn them over and I tell them that it means \"peace between the world\". More funny!";
            quotes[29][1] = "rick and morty";
            quotes[30][0] = "Everything we see is for greater benefit.";
            quotes[30][1] = "hot fuzz";
            quotes[31][0] = "David killed the queen!";
            quotes[31][1] = "shaun of the dead";
            quotes[32][0] = "You are off dead chain!";
            quotes[32][1] = "hot fuzz";
            quotes[33][0] = "I am not a psychologist Anderson, a very effective sociologist.";
            quotes[33][1] = "sherlock";
            quotes[34][0] = "Friends are my strength";
            quotes[34][1] = "kingdom hearts";
            quotes[35][0] = "Do not forget that every time you go, I am still with you.";
            quotes[35][1] = "kingdom hearts";
            quotes[36][0] = "The king is a long king.";
            quotes[36][1] = "mario";
            quotes[37][0] = "You're far away speed";
            quotes[37][0] = "sonic"; //Technically also smash.
        }
        return quotes;
    }
}
