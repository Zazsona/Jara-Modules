package com.Zazsona.NaughtsAndCrosses.game;

public class Board
{
    private Counter[][] board;

    public Board()
    {
        board = new Counter[3][3];
        for (int i = 0; i<board.length; i++)
        {
            board[i] = new Counter[]{Counter.NONE, Counter.NONE, Counter.NONE};
        }
    }

    private Board(Counter[][] originalBoard)
    {
        board = new Counter[3][3];
        for (int i = 0; i<board.length; i++)
        {
            board[i] = originalBoard[i];
        }
    }

    public boolean placeCounter(int x, int y, Counter counter)
    {
        if (!isPositionOccupied(x, y))
        {
            board[x][y] = counter;
            return true;
        }
        return false;
    }

    public boolean isPositionOccupied(int x, int y)
    {
        return board[x][y] != Counter.NONE;
    }

    public Counter getCounterAtPosition(int x, int y)
    {
        return board[x][y];
    }

    public boolean isFull()
    {
        for (int column = 0; column<getBoardWidth(); column++)
        {
            for (int row = 0; row<getBoardHeight(); row++)
            {
                if (board[column][row] == Counter.NONE)
                    return false;
            }
        }
        return true;
    }

    public int getBoardWidth()
    {
        return board.length;
    }

    public int getBoardHeight()
    {
        return board[0].length;
    }

    public Counter getWinner()
    {
        if (isFull())
            return Counter.NONE;
        if (isHorizontalWin(Counter.NAUGHT) || isVerticalWin(Counter.NAUGHT) || isDiagonalWin(Counter.NAUGHT))
            return Counter.NAUGHT;
        if (isHorizontalWin(Counter.CROSS) || isVerticalWin(Counter.CROSS) || isDiagonalWin(Counter.CROSS))
            return Counter.CROSS;

        return null;
    }

    private boolean isHorizontalWin(Counter counter)
    {
        int comboSize = 0;
        for (int row = 0; row<getBoardHeight(); row++)
        {
            for (int column = 0; column<getBoardWidth(); column++)
            {
                if (board[column][row] == counter)
                    comboSize++;
                else
                    break;

                if (comboSize == getBoardWidth())
                    return true;
            }
            comboSize = 0;
        }
        return false;
    }

    private boolean isVerticalWin(Counter counter)
    {
        int comboSize = 0;
        for (int column = 0; column<getBoardWidth(); column++)
        {
            for (int row = 0; row<getBoardHeight(); row++)
            {
                if (board[column][row] == counter)
                    comboSize++;
                else
                    break;

                if (comboSize == getBoardHeight())
                    return true;
            }
            comboSize = 0;
        }
        return false;
    }

    private boolean isDiagonalWin(Counter counter)
    {
        int comboSize = 0;
        for (int offset = 0; offset<getBoardWidth(); offset++)
        {
            if (board[offset][offset] == counter)
                comboSize++;
            else
                break;

            if (comboSize == getBoardWidth())
                return true;
        }
        comboSize = 0;
        int lastIndex = getBoardWidth()-1;
        for (int offset = 0; offset<getBoardWidth(); offset++)
        {
            if (board[lastIndex-offset][offset] == counter)
                comboSize++;
            else
                break;

            if (comboSize == getBoardWidth())
                return true;
        }
        return false;
    }

    public Board clone()
    {
        return new Board(board);
    }
}
