package com.zazsona.wwtbam;

import com.google.gson.Gson;
import com.zazsona.jara.commands.CmdUtil;
import com.zazsona.wwtbam.Trivia;
import com.zazsona.wwtbam.api.TokenResponse;
import com.zazsona.wwtbam.api.TriviaResponse;

import java.util.ArrayList;
import java.util.Random;

public class TriviaDatabase
{
    public static final transient int[] CATEGORY_IDs = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
    private String token;
    private ArrayList<Integer> availableCategories;
    private Random r;

    public TriviaDatabase()
    {
        Gson gson = new Gson();
        r = new Random();
        String tokenJson = CmdUtil.sendHTTPRequest("https://opentdb.com/api_token.php?command=request");
        TokenResponse tokenResponse = gson.fromJson(tokenJson, TokenResponse.class);
        token = tokenResponse.getToken();
        availableCategories = new ArrayList<>();
        for (int id : CATEGORY_IDs)
            availableCategories.add(id);
    }

    public Trivia getTrivia(int questionNo)
    {
        Gson gson = new Gson();
        int questionQuantity = 1;
        String difficulty = parseDifficulty(questionNo);
        Integer categoryId = availableCategories.get(r.nextInt(availableCategories.size()));
        availableCategories.remove(categoryId);
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

    private String parseDifficulty(int questionNo)
    {
        if (questionNo <= 5)
            return "easy";
        else if (questionNo > 5 && questionNo <= 10)
            return "medium";
        else if (questionNo > 10 && questionNo <= 15)
            return "hard";

        return "N/A";
    }
}