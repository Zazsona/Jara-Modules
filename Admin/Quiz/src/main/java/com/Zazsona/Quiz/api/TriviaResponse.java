package com.Zazsona.Quiz.quiz;

import java.util.Random;

public class Trivia
{
    public TriviaQuestion[] results;

    public class TriviaQuestion
    {
        private String category;
        private String type;
        private String difficulty;
        private String question;
        private String correct_answer;
        private String[] incorrect_answers;

        private String[] answers;
        private int correct_answer_id;

        public TriviaQuestion()
        {
            Random r = new Random();
            answers = new String[incorrect_answers.length+1];
            correct_answer_id = r.nextInt(answers.length);
            for (int i = 0; i<answers.length; i++)
            {
                if (i < correct_answer_id)
                    answers[i] = incorrect_answers[i];
                else if (i == correct_answer_id)
                    answers[i] = correct_answer;
                else if (i > correct_answer_id)
                    answers[i] = incorrect_answers[i-1];
            }

        }

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

        /**
         * Gets category
         * @return category
         */
        public String getCategory()
        {
            return category;
        }


        /**
         * Gets question
         * @return question
         */
        public String getQuestion()
        {
            return question;
        }

        /**
         * Gets correct_answer_id
         * @return correct_answer_id
         */
        public int getCorrectAnswerIndex()
        {
            return correct_answer_id;
        }

        /**
         * Gets answers
         * @return answers
         */
        public String[] getAnswers()
        {
            return answers;
        }
    }

}
