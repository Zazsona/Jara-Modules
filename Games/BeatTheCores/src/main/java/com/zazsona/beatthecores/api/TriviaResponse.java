package com.zazsona.beatthecores.api;

import java.util.Random;

public class TriviaResponse
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
         * Gets type
         *
         * @return type
         */
        public String getType()
        {
            return type;
        }

        /**
         * Gets difficulty
         *
         * @return difficulty
         */
        public String getDifficulty()
        {
            return difficulty;
        }

        /**
         * Gets correct_answer
         *
         * @return correct_answer
         */
        public String getCorrectAnswer()
        {
            return correct_answer;
        }

        /**
         * Gets incorrect_answers
         *
         * @return incorrect_answers
         */
        public String[] getIncorrectAnswers()
        {
            return incorrect_answers;
        }
    }

}
