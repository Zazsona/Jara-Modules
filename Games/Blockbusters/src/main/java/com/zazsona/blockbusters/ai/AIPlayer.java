package com.zazsona.blockbusters.ai;

import com.zazsona.blockbusters.game.BlockbustersUI;
import com.zazsona.blockbusters.game.objects.*;

import java.util.LinkedList;
import java.util.Random;

public class AIPlayer
{
    private Board board;
    private Team team;
    private AIDifficulty difficulty;
    private LinkedList<Tile> selectedPath = new LinkedList<>();
    private int selectedPathCost = Integer.MAX_VALUE;

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
        selectedPath.clear();
        selectedPathCost = Integer.MAX_VALUE;

        for (int x = 0; x<board.getBoardXLength(); x++)
            getPath(x, 0, team.isWhiteTeam() ? TileState.WHITE : TileState.BLUE, new LinkedList<Tile>(), 0);

        Tile selectedTile = null;
        for (Tile tile : selectedPath)
        {
            if (tile.getTileState() == TileState.UNCLAIMED)
            {
                selectedTile = tile;
                break;
            }
        }
        return selectedTile;
    }

    public int getRandomBuzzTimeMillis()
    {
        Random r = new Random();
        switch (difficulty)
        {
            case EASY:
                return 4000+(r.nextInt(6000));
            case HARD:
                return 1000+r.nextInt(2000); //1s base so that player can read the question, to some degree.
            case STANDARD:
            default:
                return 2000+r.nextInt(4000);
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
                answer = (value <= 40) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
            case HARD:
                answer = (value <= 90) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
            case STANDARD:
            default:
                answer = (value <= 70) ? question.getQuestionAnswer()[0] : failLines[r.nextInt(failLines.length)];
                break;
        }
        blockbustersUI.sendAIMessage(answer);
        try {Thread.sleep(2000);} catch (InterruptedException e) {}; //These sleeps are fairly arbitrary, but serve to try and emulate human delay. They also aid in API call limits, as the bot is sending a lot of messages.
        return answer;
    }

    private void getPath(int startX, int startY, TileState targetPathType, LinkedList<Tile> visitedTiles, int pathCost)
    {
        Tile tile = board.getTile(startX, startY);
        if (!visitedTiles.contains(tile) && (tile.getTileState() == TileState.UNCLAIMED || tile.getTileState() == targetPathType))
        {
            visitedTiles.add(tile);
            if (tile.getTileState() == TileState.UNCLAIMED)
                pathCost++;
            if (pathCost > selectedPathCost)
                return;

            if ((targetPathType == TileState.BLUE && startX == 4) || (targetPathType == TileState.WHITE && startY == 3))
            {
                if (pathCost <= selectedPathCost)
                {
                    selectedPath = visitedTiles;
                    selectedPathCost = pathCost;
                }
            }
            else
            {
                if (startY > 0) //Up
                {
                    getPath(startX, startY-1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                }
                if (startY < board.getBoardYLength()-1) //Down
                {
                    getPath(startX, startY+1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                }
                if (startX % 2 == 0)  //If even x
                {
                    if (startY > 0 && startX < board.getBoardXLength()-1) //Up-Right
                    {
                        getPath(startX+1, startY-1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startY > 0 && startX > 0) //Up-Left
                    {
                        getPath(startX-1, startY-1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startX < board.getBoardXLength()-1) //Down-Right
                    {
                        getPath(startX+1, startY, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startX > 0) //Down-Left
                    {
                         getPath(startX-1, startY, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                }
                else //If odd x
                {
                    if (startX < board.getBoardXLength()-1) //Up-Right
                    {
                        getPath(startX+1, startY, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startX > 0) //Up-Left
                    {
                        getPath(startX-1, startY, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startY < board.getBoardYLength()-1 && startX < board.getBoardXLength()-1) //Down-Right
                    {
                        getPath(startX+1, startY+1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                    if (startY < board.getBoardYLength()-1 && startX > 0) //Down-Left
                    {
                        getPath(startX-1, startY+1, targetPathType, new LinkedList<>(visitedTiles), pathCost);
                    }
                }
            }
        }
    }
}
