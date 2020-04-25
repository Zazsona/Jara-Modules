package com.Zazsona.Quiz.config;

import com.Zazsona.Quiz.api.TokenResponse;
import com.Zazsona.Quiz.api.TriviaResponse;
import com.Zazsona.Quiz.quiz.Quiz;
import com.Zazsona.Quiz.quiz.Trivia;
import com.google.gson.Gson;
import commands.CmdUtil;
import jara.Core;
import net.dv8tion.jda.api.entities.Guild;

import java.io.Serializable;
import java.util.*;

public class QuizBuilder implements Serializable
{
    public static final transient int[] CATEGORY_IDs = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
    private static final long serialVersionUID = 1L;
    private String guildID;
    private ArrayList<Integer> categoryBlacklist;
    private int questionCount;
    private int joinTimeSeconds;
    private boolean pingOnCountdown;
    private ArrayList<String> rolesPermittedToJoin;
    private DaySchedule[] scheduledDays;

    public QuizBuilder(String guildID)
    {
        this.guildID = guildID;
        this.categoryBlacklist = new ArrayList<>();
        this.questionCount = 10;
        this.joinTimeSeconds = (60*5);
        this.rolesPermittedToJoin = new ArrayList<>();
        this.rolesPermittedToJoin.add(guildID); //This adds the everyone role.
        this.scheduledDays = new DaySchedule[7];
        this.pingOnCountdown = false;
    }

    public QuizBuilder(String guildID, ArrayList<Integer> categoryBlacklist, int questionCount, int joinTimeSeconds, boolean pingOnCountdown, ArrayList<String> rolesPermittedToJoin, DaySchedule[] scheduledDays)
    {
        this.guildID = guildID;
        this.categoryBlacklist = categoryBlacklist;
        this.questionCount = questionCount;
        this.joinTimeSeconds = joinTimeSeconds;
        this.pingOnCountdown = pingOnCountdown;
        this.rolesPermittedToJoin = rolesPermittedToJoin;
        this.scheduledDays = scheduledDays;
    }

    private QuizBuilder(QuizBuilder quizBuilder)
    {
        this.guildID = new String(quizBuilder.getGuildID());
        this.categoryBlacklist = (ArrayList<Integer>) quizBuilder.getCategoriesBlacklist().clone();
        this.questionCount = quizBuilder.getQuestionCount();
        this.joinTimeSeconds = quizBuilder.getJoinTimeSeconds();
        this.rolesPermittedToJoin = (ArrayList<String>) quizBuilder.getRolesPermittedToJoin().clone();
        this.pingOnCountdown = quizBuilder.isPingOnCountdown();
        this.scheduledDays = new DaySchedule[quizBuilder.getScheduledDays().length];
        for (int i = 0; i<scheduledDays.length; i++)
        {
            if (quizBuilder.getScheduledDays()[i] != null)
                this.scheduledDays[i] = quizBuilder.getScheduledDays()[i].clone();
        }
    }

    private QuizBuilder()
    {
        //Empty constructor for Gson
    }

    public String getGuildID()
    {
        return guildID;
    }

    public ArrayList<Integer> getCategoriesBlacklist()
    {
        return categoryBlacklist;
    }

    public int getQuestionCount()
    {
        return questionCount;
    }

    public int getJoinTimeSeconds()
    {
        return joinTimeSeconds;
    }

    public ArrayList<String> getRolesPermittedToJoin()
    {
        return rolesPermittedToJoin;
    }

    public DaySchedule[] getScheduledDays()
    {
        return scheduledDays;
    }

    public boolean isPingOnCountdown()
    {
        return pingOnCountdown;
    }

    public QuizBuilder setCategoriesBlacklist(ArrayList<Integer> categoryBlacklist)
    {
        this.categoryBlacklist = categoryBlacklist;
        return this;
    }

    public QuizBuilder setQuestionCount(int questionCount)
    {
        this.questionCount = questionCount;
        return this;
    }

    public QuizBuilder setJoinTimeSeconds(int joinTimeSeconds)
    {
        this.joinTimeSeconds = joinTimeSeconds;
        return this;
    }

    public QuizBuilder setPingOnCountdown(boolean pingOnCountdown)
    {
        this.pingOnCountdown = pingOnCountdown;
        return this;
    }

    public QuizBuilder setRolesPermittedToJoin(ArrayList<String> rolesPermittedToJoin)
    {
        this.rolesPermittedToJoin = rolesPermittedToJoin;
        return this;
    }

    public QuizBuilder setScheduledDays(DaySchedule[] scheduledDays)
    {
        this.scheduledDays = scheduledDays;
        return this;
    }

    public QuizBuilder addAllowedRole(String roleID)
    {
        rolesPermittedToJoin.add(roleID);
        return this;
    }

    public QuizBuilder removeAllowedRole(String roleID)
    {
        rolesPermittedToJoin.remove(roleID);
        return this;
    }

    public QuizBuilder addCategoriesToBlacklist(int... categoryIDsToAdd)
    {
        for (int id : categoryIDsToAdd)
        {
            if (!categoryBlacklist.contains(id))
                categoryBlacklist.add(id);
        }
        return this;
    }

    public QuizBuilder removeCategoriesFromBlacklist(int... categoryIDsToRemove)
    {
        for (int id : categoryIDsToRemove)
        {
            categoryBlacklist.remove(id);
        }
        return this;
    }

    public DaySchedule getDay(int dayValue)
    {
        if (scheduledDays[dayValue-1] == null)
        {
            scheduledDays[dayValue-1] = new DaySchedule();
        }
        return scheduledDays[dayValue-1];
    }

    public Quiz build()
    {
        Trivia[] trivia = getTrivia();
        Guild guild = Core.getShardManagerNotNull().getGuildById(guildID);
        return new Quiz(guild, trivia, this);
    }

    private Trivia[] getTrivia()
    {
        LinkedList<Trivia> triviaList = new LinkedList<>();
        Gson gson = new Gson();
        String tokenJson = CmdUtil.sendHTTPRequest("https://opentdb.com/api_token.php?command=request");
        TokenResponse tokenResponse = gson.fromJson(tokenJson, TokenResponse.class);
        if (categoryBlacklist.size() > 0 && categoryBlacklist.size() < CATEGORY_IDs.length)
        {
            Random r = new Random();
            HashMap<Integer, Integer> categoryQuantityMap = new HashMap<>();
            ArrayList<Integer> validCategories = new ArrayList<Integer>();
            for (int categoryID : CATEGORY_IDs)
                validCategories.add(categoryID);
            validCategories.removeAll(categoryBlacklist);

            for (int i = 0; i<questionCount; i++)
            {
                int categoryID = validCategories.get(r.nextInt(validCategories.size()));
                int baseQuantity = categoryQuantityMap.getOrDefault(categoryID, 0);
                categoryQuantityMap.put(categoryID, baseQuantity+1);
            }
            for (Map.Entry<Integer, Integer> entry : categoryQuantityMap.entrySet())
            {
                String json = CmdUtil.sendHTTPRequest("https://opentdb.com/api.php?amount="+entry.getValue()+"&token="+tokenResponse.getToken()+"&category="+entry.getKey());
                TriviaResponse triviaResponse = gson.fromJson(json, TriviaResponse.class);
                for (TriviaResponse.TriviaQuestion question : triviaResponse.results)
                    triviaList.add(new Trivia(question));
            }
        }
        else
        {
            String json = CmdUtil.sendHTTPRequest("https://opentdb.com/api.php?amount="+questionCount);
            TriviaResponse triviaResponse = gson.fromJson(json, TriviaResponse.class);
            for (TriviaResponse.TriviaQuestion question : triviaResponse.results)
                triviaList.add(new Trivia(question));
        }
        return triviaList.toArray(new Trivia[0]);
    }

    public QuizBuilder clone()
    {
        return new QuizBuilder(this);
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

    public static String getCategoryName(int categoryID)
    {
        switch (categoryID)
        {
            case 9:
                return  "General Knowledge";
            case 17:
                return  "Science & Nature";
            case 18:
                return  "Science: Computers";
            case 19:
                return  "Science: Mathematics";
            case 30:
                return  "Science: Gadgets";
            case 21:
                return  "Sports";
            case 22:
                return  "Geography";
            case 23:
                return  "History";
            case 24:
                return  "Politics";
            case 25:
                return  "Art";
            case 26:
                return  "Celebrities";
            case 27:
                return  "Animals";
            case 28:
                return  "Vehicles";
            case 10:
                return  "Entertainment: Books";
            case 20:
                return  "Mythology";
            case 11:
                return  "Entertainment: Film";
            case 14:
                return  "Entertainment: Television";
            case 12:
                return  "Entertainment: Music";
            case 13:
                return "Entertainment: Musicals & Theatres";
            case 15:
                return "Entertainment: Video Games";
            case 16:
                return "Entertainment: Board Games";
            case 29:
                return "Entertainment: Comics";
            case 32:
                return "Entertainment: Cartoon & Animations";
            case 31:
                return "Entertainment: Japanese Anime & Manga";
            default:
                return null;
        }
    }
}
