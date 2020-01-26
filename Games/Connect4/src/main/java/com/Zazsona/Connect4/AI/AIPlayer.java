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
        int predictionDepth = getPredictionDepthLimit();
        Node root = new Node(null, 0, 0, false);
        addChildren(board.clone(), root, true, predictionDepth);

        minimax(root, predictionDepth, true);
    }

    private int minimax(Node parent, int depth, boolean isMaxPlayer)
    {
        if (depth == 0 || parent.isTerminal() || parent.getValue() != 0)
            return parent.getValue();

        if (isMaxPlayer)
        {
            ArrayList<Node> bestChildren = new ArrayList<>();
            int maxEval = Integer.MIN_VALUE;
            for (Node child : parent.getChildren())
            {
                if (child != null)
                {
                    int eval = minimax(child, depth-1, false);
                    if (eval >= maxEval)
                    {
                        if (eval > maxEval)
                        {
                            maxEval = eval;
                            bestChildren.clear();
                        }
                        bestChildren.add(child);
                    }
                }
            }
            if (depth == getPredictionDepthLimit())
            {
                int childIndex = new Random().nextInt(bestChildren.size());
                board.placeCounter(bestChildren.get(childIndex).getColumn(), Connect4.getPlayerCounter(isPlayer1));
            }
            return maxEval;
        }
        else
        {
            int minEval = Integer.MAX_VALUE;
            for (Node child : parent.getChildren())
            {
                if (child != null)
                {
                    int eval = minimax(child, depth-1, false);
                    if (eval < minEval)
                    {
                        minEval = eval;
                    }

                }
            }
            return minEval;
        }
    }

    private void addChildren(Board childBoard, Node parent, boolean isMaxPlayer, int predictionDepth)
    {
        if (parent.getValue() == 0) //If it has a value, then it's a win/loss scenario (game over), so no point going further in the tree
        {
            Counter maxCounter = Connect4.getPlayerCounter(isPlayer1);
            Counter minCounter = Connect4.getPlayerCounter(!isPlayer1);
            for (int column = 0; column<board.getBoardWidth(); column++)
            {
                if (!childBoard.isColumnFull(column))
                {
                    childBoard.placeCounter(column, (isMaxPlayer) ? maxCounter : minCounter);
                    Node childNode = new Node(parent, getMoveValue(childBoard, maxCounter, predictionDepth), column, isMaxPlayer);
                    parent.addChild(column, childNode);
                    if (predictionDepth > 0)
                    {
                        addChildren(childBoard.clone(), childNode, !isMaxPlayer, predictionDepth-1);
                    }
                    childBoard.removeCounter(column);
                }
            }
        }
    }

    private int getMoveValue(Board board, Counter maxCounter, int depth)
    {
        Counter winState = board.getWinner();
        if (winState == null)
        {
            return 0;
        }
        else if (winState == maxCounter) //If it's this player's win
        {
            return 1*depth;
        }
        else //If it's the opponent's win
        {
            return -1*depth;
        }
    }

    private int getPredictionDepthLimit()
    {
        switch (difficulty)
        {
            case EASY:
                return 1;
            case STANDARD:
                return 3;
            case HARD:
                return 5;
        }
        return 3;
    }
}
