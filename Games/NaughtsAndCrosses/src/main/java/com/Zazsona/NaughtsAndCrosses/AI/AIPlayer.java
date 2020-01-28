package com.Zazsona.NaughtsAndCrosses.AI;

import com.Zazsona.NaughtsAndCrosses.NaughtsAndCrosses;
import com.Zazsona.NaughtsAndCrosses.game.Board;
import com.Zazsona.NaughtsAndCrosses.game.Counter;

public class AIPlayer
{
    private Board board;
    private boolean isPlayer1;
    private AIDifficulty difficulty;

    public AIPlayer(Board board, boolean isPlayer1, AIDifficulty difficulty)
    {
        this.board = board;
        this.isPlayer1 = isPlayer1;
        this.difficulty = difficulty;
    }

    public void takeTurn()
    {
        Node root = new Node(0, 0, 0);
        addChildren(board.clone(), root, true, getDifficultyDepth());

        minimax(root, getDifficultyDepth(), true);
    }

    private int minimax(Node parent, int depth, boolean isMaxPlayer)
    {
        if (depth == 0 || parent.isTerminal())
            return parent.getValue();

        if (isMaxPlayer)
        {
            Node bestChild = null;
            int maxEval = Integer.MIN_VALUE;
            for (Node child : parent.getChildren())
            {
                int eval = minimax(child, depth-1, false);
                if (eval > maxEval)
                {
                    maxEval = eval;
                    bestChild = child;
                }
            }
            if (depth == getDifficultyDepth())
            {
                board.placeCounter(bestChild.getColumn(), bestChild.getRow(), NaughtsAndCrosses.getPlayerCounter(isPlayer1));
            }
            return maxEval;
        }
        else
        {
            int minEval = Integer.MAX_VALUE;
            for (Node child : parent.getChildren())
            {
                int eval = minimax(child, depth-1, true);
                minEval = Math.min(eval, minEval);
            }
            return minEval;
        }
    }

    private void addChildren(Board childBoard, Node parent, boolean isMaxPlayer, int predictionDepth)
    {
        if (parent.getValue() == 0)
        {
            Counter maxCounter = NaughtsAndCrosses.getPlayerCounter(isPlayer1);
            Counter minCounter = NaughtsAndCrosses.getPlayerCounter(!isPlayer1);
            for (int column = 0; column<board.getBoardWidth(); column++)
            {
                for (int row = 0; row<board.getBoardHeight(); row++)
                {
                    if (!childBoard.isPositionOccupied(column, row))
                    {
                        childBoard.placeCounter(column, row, (isMaxPlayer) ? maxCounter : minCounter);
                        int moveValue = getMoveValue(childBoard, maxCounter, predictionDepth);
                        Node childNode = new Node(column, row, moveValue);
                        parent.addChild(childNode);
                        if (predictionDepth > 0)
                        {
                            addChildren(childBoard.clone(), childNode, !isMaxPlayer, predictionDepth-1);
                        }
                        childBoard.removeCounter(column, row);
                    }
                }
            }
        }
    }

    private int getMoveValue(Board board, Counter maxCounter, int depth)
    {
        Counter winState = board.getWinner();
        if (winState == null || winState == Counter.NONE)
        {
            return 0;
        }
        else if (winState == maxCounter) //If it's this AI's win
        {
            return 1*(depth+1); //Multiply by depth, so that sooner moves have a larger score
        }
        else //If it's the opponent's win
        {
            return -1*(depth+1);
        }
    }

    private int getDifficultyDepth()
    {
        switch (difficulty)
        {

            case EASY:
                return 2;
            case STANDARD:
                return 5;
            case HARD:
                return 9;
        }
        return 5;
    }
}
