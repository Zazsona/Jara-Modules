package com.Zazsona.Blockbusters.AI;

import com.Zazsona.Blockbusters.game.BlockbustersUI;
import com.Zazsona.Blockbusters.game.objects.*;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class AIPlayer
{
    private Board board;
    private Team team;
    private AIDifficulty difficulty;
    private LinkedList<Thread> generationThreads = new LinkedList<>();

    private Tile selectedTile;

    public AIPlayer(Board board, Team team, AIDifficulty difficulty)
    {
        this.board = board;
        this.team = team;
        this.team.setAITeam(true);
        this.difficulty = difficulty;
    }

    /**
     * Gets team
     * @return team
     */
    public Team getTeam()
    {
        return team;
    }

    /**
     * Gets difficulty
     * @return difficulty
     */
    public AIDifficulty getDifficulty()
    {
        return difficulty;
    }

    public Tile getTile()
    {
        try
        {
            System.out.println("Picking tile...");
            selectedTile = null;
            Node root = new Node(0, 0, 0);
            addChildren(board.clone(), root, true, getDifficultyDepth());
            for (Thread thread : generationThreads)
                thread.join();
            generationThreads.clear();

            new Thread(() -> minimax(root, getDifficultyDepth(), true)).start();
            while (selectedTile == null)
            {
                System.out.println("Still picking...");
                try {Thread.sleep(100);} catch (InterruptedException e) {};
            }
            System.out.println("Got tile: "+selectedTile);
            return selectedTile;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public int getRandomBuzzTimeMillis()
    {
        Random r = new Random();
        switch (difficulty)
        {
            case EASY:
                return 4000+(r.nextInt(6000));
            case HARD:
                return 2000+r.nextInt(4000);
            case STANDARD:
            default:
                return 3000+(r.nextInt(6000));
        }
    }

    public String getQuestionAnswer(BlockbustersUI blockbustersUI, Question question)
    {
        try {Thread.sleep(2000);} catch (InterruptedException e) {};
        Random r = new Random();
        int value = r.nextInt(100);
        String[] failLines = {"Ha! It's...Wait, what was it again?", "Darn, it's slipped my mind.", "Whoops, missclick.", "No, wait, I'm thinking of the wrong thing.", "Actually, nope, I don't know it.", "42!", "Hang on, it's not in my database!", "Too easy! It's obviously my sweet sense of style.", "I dunno, just wanted to push the button."};
        String answer;
        switch (difficulty)
        {
            case EASY:
                answer = (value <= 25) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
            case HARD:
                answer = (value <= 60) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
            case STANDARD:
            default:
                answer = (value <= 40) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
        }
        blockbustersUI.sendAIMessage(answer);
        try {Thread.sleep(1000);} catch (InterruptedException e) {};
        return answer;
    }

    private int minimax(Node parent, int depth, boolean isMaxPlayer)
    {
        if (depth == 0 || parent.isTerminal())
            return parent.getValue();

        if (isMaxPlayer)
        {
            ArrayList<Node> bestChildren = new ArrayList<>();
            int maxEval = Integer.MIN_VALUE;
            for (Node child : parent.getChildren())
            {
                int eval = minimax(child, depth-1, false);
                if (eval > maxEval)
                {
                    maxEval = eval;
                    bestChildren.clear();
                }
                bestChildren.add(child);
            }
            if (depth == getDifficultyDepth())
            {
                Random r = new Random();
                Tile potentialTile = null;
                while (potentialTile == null || potentialTile.getTileState() != TileState.UNCLAIMED) //If all tiles are 0, then it was default to [0][0], which may actually be claimed. So, we have to check that. While doing this, we may as well randomise the tile selection to appear more 'natural'
                {
                    Node potentialNode = bestChildren.get(r.nextInt(bestChildren.size()));
                    potentialTile = board.getTile(potentialNode.getColumn(), potentialNode.getRow());
                }
                System.out.println(potentialTile.getTileState());
                this.selectedTile = potentialTile;
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
            TileState maxTile = (team.isWhiteTeam()) ? TileState.WHITE : TileState.BLUE;
            TileState minTile = (team.isWhiteTeam()) ? TileState.BLUE : TileState.WHITE;
            for (int column = 0; column<board.getBoardXLength(); column++)
            {
                for (int row = 0; row<board.getBoardYLength(); row++)
                {
                    if (childBoard.getTile(column, row).getTileState() == TileState.UNCLAIMED)
                    {
                        childBoard.getTile(column, row).setTileState((isMaxPlayer) ? maxTile : minTile);
                        int moveValue = getMoveValue(childBoard, maxTile, predictionDepth);
                        Node childNode = new Node(column, row, moveValue);
                        parent.addChild(childNode);
                        if (predictionDepth > 0)
                        {
                            if (predictionDepth == getDifficultyDepth())
                            {
                                Thread thread = new Thread(() -> addChildren(childBoard.clone(), childNode, !isMaxPlayer, predictionDepth - 1));
                                generationThreads.add(thread);
                                thread.start();
                            }
                            else
                            {
                                addChildren(childBoard.clone(), childNode, !isMaxPlayer, predictionDepth-1);
                            }
                        }
                        childBoard.getTile(column, row).setTileState(TileState.UNCLAIMED);
                    }
                }
            }
        }
    }

    private int getMoveValue(Board board, TileState maxTile, int depth)
    {
        TileState winState = board.getWinner();
        if (winState == null || winState == TileState.UNCLAIMED)
        {
            return 0;
        }
        else if (winState == maxTile) //If it's this AI's win
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
                return 8;
        }
        return 5;
    }
}
