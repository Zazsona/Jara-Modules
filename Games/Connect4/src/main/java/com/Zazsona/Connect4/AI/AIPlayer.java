package com.Zazsona.Connect4.AI;

import com.Zazsona.Connect4.Connect4;
import com.Zazsona.Connect4.game.Board;
import com.Zazsona.Connect4.game.Counter;

import java.util.ArrayList;
import java.util.Random;

public class AIPlayer
{
    private Board board;
    private AIDifficulty difficulty;
    private boolean isPlayer1;

    public AIPlayer(Board board, boolean isPlayer1)
    {
        this.board = board;
        this.difficulty = AIDifficulty.STANDARD;
        this.isPlayer1 = isPlayer1;
    }

    public AIPlayer(Board board, boolean isPlayer1, AIDifficulty difficulty)
    {
        this.board = board;
        this.isPlayer1 = isPlayer1;
        this.difficulty = difficulty;
    }

    public void setBoard(Board board)
    {
        this.board = board;
    }

    public void setDifficulty(AIDifficulty difficulty)
    {
        this.difficulty = difficulty;
    }

    public boolean isPlayer1()
    {
        return isPlayer1;
    }

    public void takeTurn()
    {
        Counter myCounter = Connect4.getPlayerCounter(isPlayer1);
        int column = getBestMove(board, 0, myCounter);
        board.placeCounter(column, myCounter);
    }

    private int getBestMove(Board board, int predictionDepth, Counter targetCounter)
    {
        int[] values = getMoveValues(board, predictionDepth, targetCounter);
        ArrayList<Integer> bestValueList = new ArrayList<>();
        int bestColumnValue = Integer.MIN_VALUE;
        for (int column = 0; column<values.length; column++)
        {
            if (!board.isColumnFull(column) && values[column] >= bestColumnValue)
            {
                if (values[column] > bestColumnValue)
                {
                    bestColumnValue = values[column];
                    bestValueList.clear();
                }
                bestValueList.add(column);
            }
        }
        return bestValueList.get(new Random().nextInt(bestValueList.size()));
    }

    private int[] getMoveValues(Board board, int predictionDepth, Counter targetCounter)
    {
        int[] values = new int[board.getBoardWidth()];
        for (int column = 0; column<board.getBoardWidth(); column++)
        {
            values[column] = getMoveValue(board, column, predictionDepth, targetCounter);
        }
        if (predictionDepth == 0)
            System.out.println(targetCounter.name()+": A:"+values[0]+" B:"+values[1]+" C:"+values[2]+" D:"+values[3]+" E:"+values[4]+" F:"+values[5]+" G:"+values[6]);
        return values;
    }

    private int getMoveValue(Board board, int column, int predictionDepth, Counter targetCounter)
    {
        if (!board.isColumnFull(column))
        {
            Board moveBoard = board.clone();
            moveBoard.placeCounter(column, targetCounter);
            Counter winState = moveBoard.getWinner();
            if (winState == null)
            {
                predictionDepth++;
                if (predictionDepth < getPredictionDepthLimit() && !moveBoard.isBoardFull())
                {
                    Counter enemyCounter = (targetCounter == Counter.RED) ? Counter.BLUE : Counter.RED;
                    int enemyColumn = getBestMove(moveBoard, predictionDepth, enemyCounter);
                    moveBoard.placeCounter(enemyColumn, enemyCounter);
                    int nextColumn = getBestMove(moveBoard, predictionDepth, targetCounter);
                    return getMoveValue(moveBoard, nextColumn, predictionDepth, targetCounter);
                }
                return 0;
            }
            else if (winState == targetCounter) //If it's this player's win
            {
                return 100*(getPredictionDepthLimit()-predictionDepth);
            }
            else //If it's the opponent's win
            {
                return -(100*((getPredictionDepthLimit()+1)-predictionDepth));
            }
        }
        else
        {
            return -(100*((getPredictionDepthLimit()+1)-predictionDepth)); //Column is full with no winner, bad result.
        }
    }

    private int getPredictionDepthLimit()
    {
        switch (difficulty)
        {
            case EASY:
                return 2;
            case STANDARD:
                return 3;
            case HARD:
                return 4;
        }
        return 2;
    }
}
