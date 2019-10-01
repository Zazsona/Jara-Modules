package com.Zazsona.QuizNight.json;

public class TriviaJson
{
    public TriviaQuestion[] results;

    public class TriviaQuestion
    {
        public String category;
        public String type;
        public String difficulty;
        public String question;
        public String correct_answer;
        public int correct_answer_id = -1; //This is a Jara special. It needs to be set.
        public String[] incorrect_answers;

        public int getPoints()
        {
            switch (difficulty)
            {
                case "easy":
                    return 1;
                case "medium":
                    return 2;
                case "hard":
                    return 3;
            }
            return 0;
        }
    }

}
