package com.Zazsona.wwtbam.lifelines;

import com.Zazsona.wwtbam.TriviaDatabase;
import com.Zazsona.wwtbam.Trivia;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Random;

public class AskTheHost
{
    public static void useAskTheHost(TextChannel channel, TriviaDatabase tdb, Trivia trivia)
    {
        Random r = new Random();
        int randomValue = r.nextInt(100);
        int categoryId = getCategoryID(trivia.getCategory());
        int targetValue = getCorrectPercent(categoryId);
        int answerIndex = trivia.getCorrectAnswerIndex();
        if (randomValue >= targetValue)
        {
            while (answerIndex == trivia.getCorrectAnswerIndex())
                answerIndex = r.nextInt(trivia.getAnswers().length);
        }
        channel.sendMessage(getCategoryText(categoryId)+trivia.getAnswers()[answerIndex]+"? Final answer.").queue();
    }

    public static int getCorrectPercent(int categoryId)
    {
        switch (categoryId)
        {
            case 9: //General Knowledge
                return 40;
            case 10: //Books
                return 15;
            case 11: //Film
                return 20;
            case 12: //Music
                return 20;
            case 13: //Musicals and Theatre
                return 7;
            case 14: //Telly
                return 30;
            case 15: //Video Games
                return 50;
            case 16: //Board Games
                return 30;
            case 17: //Science
                return 50;
            case 18: //Computers
                return 85;
            case 19: //Maths
                return 85;
            case 20: //Mythology
                return 10;
            case 21: //Sports
                return 5;
            case 22: //Geography
                return 15;
            case 23: //History
                return 20;
            case 24: //Politics
                return 10;
            case 25: //Art
                return 15;
            case 26: //Celebrities
                return 7;
            case 27: //Animals
                return 25;
            case 28: //Vehicles
                return 35;
            case 29: //Comics
                return 35;
            case 30: //Gadgets
                return 85;
            case 31: //Anime
                return 40;
            case 32: //Cartoons
                return 40;
        }
        return 0;
    }

    public static String getCategoryText(int categoryId)
    {
        switch (categoryId)
        {
            case 9: //General Knowledge
                return "Well, my general knowledge is *generally* okay. I'm not too certain on this though. It might be ";
            case 10: //Books
                return "Most of the reading I do is on the internet, and that's mostly memes. I honestly barely have a clue, but maybe ";
            case 11: //Film
                return "Don't think I've seen this one. Best guess would be ";
            case 12: //Music
                return "Why are you asking me a music question? I'm not geared with a microphone! I don't know, how about ";
            case 13: //Musicals and Theatre
                return "Absolutely no bloody idea. Sorry. ";
            case 14: //Telly
                return "Hmm. I'm only about 30% sure, but I think it's ";
            case 15: //Video Games
                return "Hoho! I know this one. Well, mostly. It's ";
            case 16: //Board Games
                return "Hmm. This sounds familiar. I reckon it's ";
            case 17: //Science
                return "Oooh, now this is an interesting one. I've definitely looked into it before. If RAM serves, it's ";
            case 18: //Computers
                return "You've got the right chap for the job on this one! Bot is my last name. It's ";
            case 19: //Maths
                return "Easy as 1+1. It's ";
            case 20: //Mythology
                return "I'm not old enough for any of this. You'd be just as lucky winning the lottery with my answer. Best guess? ";
            case 21: //Sports
                return "Do I look like the sort of bot who goes running about to you? Absolutely no clue. ";
            case 22: //Geography
                return "I don't get out much. Perhaps ";
            case 23: //History
                return "Not finding much about this on my hard drive, since it's not ancient. Maybe ";
            case 24: //Politics
                return "The internet's too wild west for any of this politics lark. Best guess would be ";
            case 25: //Art
                return "Who? What? All I see are ones and zeros, haven't the foggiest about any of this art business. Let's guess ";
            case 26: //Celebrities
                return "Who? ";
            case 27: //Animals
                return "I'm not too sure here, 25% of my processing power wants to say ";
            case 28: //Vehicles
                return "Vroom vroom. I'm mostly about the technology in cars and that, so I'm really not sure. I'd hazard a guess with ";
            case 29: //Comics
                return "Pow! Punch! Wham! That's most of what I know about comics. Though, I could give a fair guess of ";
            case 30: //Gadgets
                return "Oh! Oh! I'm a gadget! Pick me! Just like I'm going to pick ";
            case 31: //Anime
                return "アヒル型ヘアブラシ, ";
            case 32: //Cartoons
                return "Ah, weekend mornings. It's all not coming back to me because I had no childhood. Internet says ";
        }
        return "Absolutely no idea what any of this is.";
    }

    public static int getCategoryID(String categoryName)
    {
        switch (categoryName.toUpperCase())
        {

            case "GENERAL KNOWLEDGE":
            case "GENERAL":
                return 9;
            case "SCIENCE AND NATURE":
            case "SCIENCE":
            case "NATURE":
                return 17;
            case "SCIENCE: COMPUTERS":
            case "COMPUTERS":
                return 18;
            case "SCIENCE: MATHEMATICS":
            case "MATHEMATICS":
            case "MATHS":
            case "MATH":
                return 19;
            case "SCIENCE: GADGETS":
            case "GADGETS":
                return 30;
            case "SPORTS":
                return 21;
            case "GEOGRAPHY":
                return 22;
            case "HISTORY":
                return 23;
            case "POLITICS":
                return 24;
            case "ART":
                return 25;
            case "CELEBRITIES":
            case "CELEBS":
                return 26;
            case "ANIMALS":
                return 27;
            case "VEHICLES":
                return 28;
            case "ENTERTAINMENT: BOOKS":
            case "BOOKS":
                return 10;
            case "MYTHOLOGY":
                return 20;
            case "ENTERTAINMENT: FILM":
            case "FILM":
                return 11;
            case "ENTERTAINMENT: TELEVISION":
            case "TELEVISION":
            case "TV":
                return 14;
            case "ENTERTAINMENT: MUSIC":
            case "MUSIC":
                return 12;
            case "ENTERTAINMENT: MUSICALS & THEATRES":
            case "MUSICALS":
            case "THEATRES":
            case "MUSICALS & THEATRES":
                return 13;
            case "ENTERTAINMENT: VIDEO GAMES":
            case "VIDEO GAMES":
                return 15;
            case "ENTERTAINMENT: BOARD GAMES":
            case "BOARD GAMES":
                return 16;
            case "ENTERTAINMENT: COMICS":
            case "COMICS":
                return 29;
            case "ENTERTAINMENT: CARTOON & ANIMATIONS":
            case "ENTERTAINMENT: CARTOONS & ANIMATIONS":
            case "ENTERTAINMENT: CARTOONS & ANIMATION":
            case "ENTERTAINMENT: CARTOON & ANIMATION":
            case "CARTOON & ANIMATIONS":
            case "CARTOONS & ANIMATIONS":
            case "CARTOONS & ANIMATION":
            case "CARTOON & ANIMATION":
            case "CARTOONS":
            case "ANIMATIONS":
            case "CARTOON":
            case "ANIMATION":
                return 32;
            case "ENTERTAINMENT: JAPANESE ANIME & MANGA":
            case "JAPANESE ANIME & MANGA":
            case "ANIME":
            case "MANGA":
                return 31;
            default:
                return -1;
        }
    }
}
