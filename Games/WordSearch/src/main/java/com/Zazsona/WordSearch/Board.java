package com.Zazsona.WordSearch;

import com.Zazsona.WordSearch.exception.InsufficientLengthException;
import com.Zazsona.WordSearch.exception.SpaceReservedException;
import commands.CmdUtil;

import java.io.IOException;
import java.util.Random;

public class Board
{
    private String[][] letters;
    private Word[] words;

    public Board() throws IOException
    {
        letters = new String[9][9];
        words = new Word[6];

        generateWords();
        fillEmptySpaces();
    }

    public int getBoardWidth()
    {
        return letters.length;
    }

    public int getBoardHeight()
    {
        return letters[0].length;
    }

    public String getLetter(int x, int y)
    {
        return letters[x][y];
    }

    public Word[] getWords()
    {
        return words;
    }

    public Word getWord(int startX, int startY, int endX, int endY)
    {
        for (Word word : words)
        {
            if ((word.getStartX() == startX && word.getStartY() == startY && word.getEndX() == endX && word.getEndY() == endY)
                    || (word.getStartX() == endX && word.getStartY() == endY && word.getEndX() == startX && word.getEndY() == startY)) //Reverse check
            {
                return word;
            }
        }
        return null;
    }

    public boolean isComplete()
    {
        for (Word word : words)
        {
            if (!word.isFound())
                return false;
        }
        return true;
    }


    private void generateWords() throws IOException
    {
        for (int i = 0; i<words.length; i++)
        {
            String wordString = null;
            Word word = null;
            while (wordString == null || word == null ||  wordString.length() > getBoardWidth()-2)
            {
                wordString = CmdUtil.getRandomWord(true).toUpperCase();
                word = placeWord(wordString);
            }
            words[i] = word;
        }
    }

    private Word placeWord(String word)
    {
        Random r = new Random();
        int[] angles = {0, 45, 90, 135, 180, 225, 270, 315};
        int anglesIndex = r.nextInt(angles.length);
        int startX = r.nextInt(getBoardWidth());
        int startY = r.nextInt(getBoardHeight());

        for (int attempt = 0; attempt<angles.length; attempt++)
        {
            try
            {
                anglesIndex++;
                anglesIndex = (anglesIndex >= angles.length) ? 0 : anglesIndex;
                return setWordOnBoard(word, angles[anglesIndex], startX, startY);
            }
            catch (InsufficientLengthException | SpaceReservedException e)
            {
                continue;
            }
        }
        return null; //Word not on board
    }

    private Word setWordOnBoard(String word, int angle, int startX, int startY) throws InsufficientLengthException, SpaceReservedException
    {
        String[][] lettersBackup = letters.clone();
        for (int i = 0; i<letters.length; i++)          //If an exception occurs, we can revert with this.
            lettersBackup[i] = letters[i].clone();

        int wordIndex = 0;
        String[] wordLetters = word.split("");
        while (wordIndex < wordLetters.length)
        {
            int x;
            int y;
            if (angle > 0 && angle < 180)
                x = startX+wordIndex;
            else if (angle > 180)
                x = startX-wordIndex;
            else                                  //Sadly, I can't think of a neater way to do this.
                x = startX;
            if (angle > 90 && angle < 270)
                y = startY+wordIndex;
            else if (angle < 90 || angle > 270)
                y = startY-wordIndex;
            else
                y = startY;

            if (x < 0 || x >= getBoardWidth() || y < 0 || y >= getBoardHeight())
            {
                letters = lettersBackup;
                throw new InsufficientLengthException("The word exceeds board boundaries.");
            }
            if (letters[x][y] != null && !letters[x][y].equals(wordLetters[wordIndex]))
            {
                letters = lettersBackup;
                throw new SpaceReservedException("This word crosses a used space.");
            }
            else
            {
                letters[x][y] = wordLetters[wordIndex];
                wordIndex++;
                if (wordIndex == wordLetters.length)
                    return new Word(word, startX, startY, x, y);
            }
        }
        return null; //Should never fire.
    }

    private void fillEmptySpaces()
    {
        Random r = new Random();
        String[] lettersPool = {"A", "A", "A", "B","B","B", "C", "C", "C", "C", "D","D","D", "E","E","E","E", "F","F", "G","G","G", "H", "I", "J", "K", "L","L","L", "M","M","M", "N","N", "O", "P","P", "Q", "R","R","R", "S", "S", "S", "S", "S", "S", "S", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        for (int x = 0; x<letters.length; x++)
        {
            for (int y = 0; y<letters[0].length; y++)
            {

                if (letters[x][y] == null)
                {
                    letters[x][y] = lettersPool[r.nextInt(lettersPool.length)];
                }
            }
        }
    }

}
