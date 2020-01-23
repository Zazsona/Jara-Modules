package com.Zazsona.Blockbusters.game.objects;
import com.Zazsona.Blockbusters.game.BoardRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Board
{
    private static final char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private Tile[][] tiles;
    private ArrayList<String> usedChars;

    private BoardRenderer boardRenderer;

    public Board() throws IOException
    {
        boardRenderer = new BoardRenderer(this);
        int xTiles = 5;
        int yTiles = 4;
        tiles = new Tile[xTiles][yTiles];
        usedChars = new ArrayList<>();
        tiles[0][0] = new Tile(190, 133, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[0][1] = new Tile(190, 221, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[0][2] = new Tile(190, 309, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[0][3] = new Tile(190, 397, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);

        tiles[1][0] = new Tile(286, 177, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[1][1] = new Tile(286, 266, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[1][2] = new Tile(286, 354, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[1][3] = new Tile(286, 442, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);

        tiles[2][0] = new Tile(381, 133, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[2][1] = new Tile(381, 221, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[2][2] = new Tile(381, 309, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[2][3] = new Tile(381, 397, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);

        tiles[3][0] = new Tile(476, 177, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[3][1] = new Tile(476, 266, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[3][2] = new Tile(476, 354, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[3][3] = new Tile(476, 442, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);

        tiles[4][0] = new Tile(571, 133, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[4][1] = new Tile(571, 221, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[4][2] = new Tile(571, 309, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
        tiles[4][3] = new Tile(571, 397, String.valueOf(getUniqueChar()), TileState.UNCLAIMED);
    }

    private char getUniqueChar()
    {
        Random r = new Random();
        char selectedChar = chars[r.nextInt(chars.length)];
        for (String usedChar : usedChars)
        {
            if (usedChar.equals(String.valueOf(selectedChar)))
            {
                return getUniqueChar();
            }
        }
        usedChars.add(String.valueOf(selectedChar));
        return selectedChar;
    }

    /**
     * Gets the tile denoted by the indexes, whereby:<br>
     *     0, 0 is top left<br>
     *         4, 3 is bottom right.
     * @param xIndex x index between 0 and 4 (inclusive)
     * @param yIndex y index between 0 and 3 (inclusive)
     * @return the tile
     */
    public Tile getTile(int xIndex, int yIndex) throws IndexOutOfBoundsException
    {
        return tiles[xIndex][yIndex];
    }

    public Tile getTile(String tileLetter)
    {
        for (int x = 0; x<tiles.length; x++)
        {
            for (int y = 0; y<tiles[x].length; y++)
            {
                if (tiles[x][y].getTileChar().equalsIgnoreCase(tileLetter))
                {
                    return tiles[x][y];
                }
            }
        }
        return null;
    }

    public int getBoardXLength()
    {
        return tiles.length;
    }

    public int getBoardYLength()
    {
        return tiles[0].length;
    }


    public TileState getWinState()
    {
        for (int startY = 0; startY<tiles[0].length; startY++)
        {
            boolean blueWin = checkPath(0, startY, TileState.BLUE, new ArrayList<>());
            if (blueWin)
            {
                return TileState.BLUE;
            }
        }
        for (int startX = 0; startX<tiles.length; startX++)
        {
            boolean whiteWin = checkPath(startX, 0, TileState.WHITE, new ArrayList<>());
            if (whiteWin)
            {
                return TileState.WHITE;
            }
        }
        return TileState.UNCLAIMED;
    }

    private boolean checkPath(int startX, int startY, TileState targetPathType, ArrayList<Tile> visitedTiles)
    {
        Tile tile = getTile(startX, startY);
        if (!visitedTiles.contains(tile))
        {
            visitedTiles.add(tile);
            if (tile.getTileState() == targetPathType)
            {
                if ((targetPathType == TileState.BLUE && startX == 4) || (targetPathType == TileState.WHITE && startY == 3))
                {
                    return true;
                }
                else
                {
                    boolean pathResult = false;
                    if (startY > 0 && pathResult == false) //Up
                    {
                        pathResult = checkPath(startX, startY-1, targetPathType, visitedTiles);
                    }
                    if (startY < tiles[0].length-1 && pathResult == false) //Down
                    {
                        pathResult = checkPath(startX, startY+1, targetPathType, visitedTiles);
                    }
                    if (startX % 2 == 0)  //If even x
                    {
                        if (startY > 0 && startX < tiles.length-1 &&  pathResult == false) //Up-Right
                        {
                            pathResult = checkPath(startX+1, startY-1, targetPathType, visitedTiles);
                        }
                        if (startY > 0 && startX > 0 &&  pathResult == false) //Up-Left
                        {
                            pathResult = checkPath(startX-1, startY-1, targetPathType, visitedTiles);
                        }
                        if (startX < tiles.length-1 && pathResult == false) //Down-Right
                        {
                            pathResult = checkPath(startX+1, startY, targetPathType, visitedTiles);
                        }
                        if (startX > 0 && pathResult == false) //Down-Left
                        {
                            pathResult = checkPath(startX-1, startY, targetPathType, visitedTiles);
                        }
                    }
                    else //If odd x
                    {
                        if (startX < tiles.length-1 && pathResult == false) //Up-Right
                        {
                            pathResult = checkPath(startX+1, startY, targetPathType, visitedTiles);
                        }
                        if (startX > 0 && pathResult == false) //Up-Left
                        {
                            pathResult = checkPath(startX-1, startY, targetPathType, visitedTiles);
                        }
                        if (startY < tiles[0].length-1 && startX < tiles.length-1 && pathResult == false) //Down-Right
                        {
                            pathResult = checkPath(startX+1, startY+1, targetPathType, visitedTiles);
                        }
                        if (startY < tiles[0].length-1 && startX > 0 && pathResult == false) //Down-Left
                        {
                            pathResult = checkPath(startX-1, startY+1, targetPathType, visitedTiles);
                        }
                    }
                    return pathResult;
                }
            }
        }
        return false;
    }

    public boolean isLetterOnBoard(String letter)
    {
        if (letter != null)
        {
            letter = letter.toUpperCase().trim();
            return usedChars.contains(letter);
        }
        return false;
    }

    public BoardRenderer getBoardRenderer()
    {
        return boardRenderer;
    }

}
