package com.zazsona.blockbusters.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zazsona.blockbusters.BlockbustersCommand;
import com.zazsona.blockbusters.game.objects.Question;
import com.zazsona.jara.ModuleResourceLoader;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QuestionSheet
{
    private static HashMap<String, Question[]> questionMap;
    private HashMap<String, ArrayList<Question>> usedQuestionMap;

    public QuestionSheet()
    {
        usedQuestionMap = new HashMap<>();
        if (questionMap == null)
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
    }

    public Question getRandomQuestion(String questionLetter)
    {
        questionLetter = questionLetter.toUpperCase();
        Random r = new Random();
        if (!usedQuestionMap.containsKey(questionLetter))
            usedQuestionMap.put(questionLetter, new ArrayList<>());

        ArrayList<Question> usedLetterQuestions = usedQuestionMap.get(questionLetter);
        Question[] letterQuestions = questionMap.get(questionLetter);
        if (usedLetterQuestions.size() >= letterQuestions.length)
            usedLetterQuestions.clear(); //Reset used questions, since we've run out

        if (usedLetterQuestions.size() >= letterQuestions.length/2) //Majority of questions used, use process more efficient at lower remaining quantity (In theory, should very rarely fire)
        {
            LinkedList<Question> unusedLetterQuestions = new LinkedList<>();
            for (Question question : letterQuestions)
            {
                unusedLetterQuestions.add(question);
            }
            unusedLetterQuestions.removeAll(usedLetterQuestions);
            Question question = unusedLetterQuestions.get(r.nextInt(unusedLetterQuestions.size()));
            usedLetterQuestions.add(question);
            return question;
        }
        else //Less than half of questions used (In theory, should only have to fire up to 2x per question)
        {
            Question question = null;
            while (usedLetterQuestions.contains(question) || question == null)
            {
                question = letterQuestions[r.nextInt(letterQuestions.length)];
            }
            usedLetterQuestions.add(question);
            return question;
        }
    }

}
