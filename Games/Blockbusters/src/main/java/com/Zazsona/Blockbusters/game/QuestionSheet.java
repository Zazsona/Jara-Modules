package com.Zazsona.Blockbusters.game;

import com.Zazsona.Blockbusters.BlockbustersCommand;
import com.Zazsona.Blockbusters.game.objects.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jara.ModuleResourceLoader;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class QuestionSheet
{
    private static QuestionSheet questionSheetInstance;
    private HashMap<String, Question[]> questionMap;

    public static QuestionSheet getInstance()
    {
        if (questionSheetInstance == null)
        {
            questionSheetInstance = new QuestionSheet();
        }
        return questionSheetInstance;
    }

    private QuestionSheet()
    {
        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Scanner scanner = new Scanner(ModuleResourceLoader.getResourceStream(new BlockbustersCommand().getModuleAttributes().getKey(), "com/zazsona/blockbusters/QuestionSheet.json"));
            StringBuilder jsonStringBuilder = new StringBuilder();
            while (scanner.hasNextLine())
            {
                jsonStringBuilder.append(scanner.nextLine());
            }
            TypeToken<HashMap<String, Question[]>> token = new TypeToken<HashMap<String, Question[]>>() {};
            questionMap = gson.fromJson(jsonStringBuilder.toString(), token.getType());
        }
        catch (Exception e)
        {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
            return;
        }
    }

    public Question getRandomQuestion(String questionLetter)
    {
        Random r = new Random();
        Question[] letterQuestions = questionMap.get(questionLetter.toUpperCase());
        return letterQuestions[r.nextInt(letterQuestions.length)];
    }

}
