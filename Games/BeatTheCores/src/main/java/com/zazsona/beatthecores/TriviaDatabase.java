package com.zazsona.beatthecores;

import com.google.gson.Gson;
import com.zazsona.beatthecores.api.TokenResponse;
import com.zazsona.beatthecores.api.TriviaResponse;
import com.zazsona.jara.commands.CmdUtil;

import java.util.ArrayList;
import java.util.Random;

public class TriviaDatabase
{
    public static final transient int[] CATEGORY_IDs = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
    private String token;
    private Random r;

    public TriviaDatabase()
    {
        Gson gson = new Gson();
        r = new Random();
        String tokenJson = CmdUtil.sendHTTPRequest("https://opentdb.com/api_token.php?command=request");
        TokenResponse tokenResponse = gson.fromJson(tokenJson, TokenResponse.class);
        token = tokenResponse.getToken();
    }

    public Trivia getTrivia(int questionNo, DifficultyLevel difficultyLevel)
    {
        Gson gson = new Gson();
        int questionQuantity = 1;
        String difficulty = parseDifficulty(difficultyLevel);
        Integer categoryId = CATEGORY_IDs[r.nextInt(CATEGORY_IDs.length)];
        String json = CmdUtil.sendHTTPRequest("https://opentdb.com/api.php?" +
                                                      "amount="+questionQuantity+
                                                      "&token="+token+
                                                      "&category="+categoryId.intValue()+
                                                      "&difficulty="+difficulty+
                                                      "&type=multiple");
        TriviaResponse triviaResponse = gson.fromJson(json, TriviaResponse.class);
        Trivia trivia = new Trivia(triviaResponse.results[0], questionNo);
        return trivia;
    }

    private String parseDifficulty(DifficultyLevel difficultyLevel)
    {
        switch (difficultyLevel)
        {
            case EASY:
                return "easy";
            case MEDIUM:
                return "medium";
            case HARD:
                return "hard";
        }
        return "N/A";
    }
}
