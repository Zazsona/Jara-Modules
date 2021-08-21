package com.zazsona.connect4.game;

import com.zazsona.connect4.exceptions.ColumnFullException;

import static com.zazsona.connect4.game.Counter.NONE;

public class Board
{
    private Counter[][] counters;

    public Board()
    {
        counters = new Counter[7][6];
        for (int column = 0; column<counters.length; column++)
        {
            for (int row = 0; row < counters[column].length; row++)
            {
                counters[column][row] = NONE;
            }
        }
    }

    private Board(Counter[][] originalCounters)
    {
        this.counters = new Counter[7][6];
        for (int column = 0; column<counters.length; column++)
        {
            for (int row = 0; row < counters[column].length; row++)
            {
                counters[column][row] = originalCounters[column][row];
            }
        }
    }


    /**
     * Gets the width of the board
     * @return the width value
     */
    public int getBoardWidth()
    {
        return counters.length;
    }

    /**
     * Gets the height of the board
     * @return the height value
     */
    public int getBoardHeight()
    {
        return counters[0].length;
    }

    /**
     * Checks if the specified column is full
     * @param column the column index
     * @return true if full
     * @throws IndexOutOfBoundsException invalid column index
     */
    public boolean isColumnFull(int column) throws IndexOutOfBoundsException
    {
        return (counters[column][counters[0].length-1] != Counter.NONE);
    }

    /**
     * Checks if the specified column is full
     * @param column the column index, A-G
     * @return true if full
     * @throws IndexOutOfBoundsException invalid column index
     */
    public boolean isColumnFull(String column) throws IndexOutOfBoundsException
    {
        return (counters[getColumnIndex(column)][counters[0].length-1] != Counter.NONE);
    }

    /**
     * Gets the counter type at the specified position
     * @param column the column index, A-G
     * @param row the row index
     * @return the counter type
     * @throws IndexOutOfBoundsException invalid indexes
     */
    public Counter getCounter(String column, int row) throws IndexOutOfBoundsException
    {
        return counters[getColumnIndex(column)][row];
    }

    /**
     * Gets the counter type at the specified position
     * @param column the column index
     * @param row the row index
     * @return the counter type
     * @throws IndexOutOfBoundsException invalid indexes
     */
    public Counter getCounter(int column, int row) throws IndexOutOfBoundsException
    {
        return counters[column][row];
    }

    /**
     * Places the counter in the specified column
     * @param column the column letter, A-G
     * @param counter the counter
     * @throws IndexOutOfBoundsException invalid column
     * @throws ColumnFullException no spaces in the column
     */
    public void placeCounter(String column, Counter counter) throws IndexOutOfBoundsException, ColumnFullException
    {
        int positionIndex = getColumnIndex(column);
        placeCounter(positionIndex, counter);
    }

    /**
     * Places the counter in the specified column
     * @param columnIndex the column
     * @param counter the counter
     * @throws IndexOutOfBoundsException invalid column
     * @throws ColumnFullException no spaces in the column
     */
    public void placeCounter(int columnIndex, Counter counter) throws IndexOutOfBoundsException, ColumnFullException
    {
        for (int i = 0; i < counters[columnIndex].length; i++)
        {
            if (counters[columnIndex][i].equals(Counter.NONE))
            {
                counters[columnIndex][i] = counter;
                return;
            }
        }
        throw new ColumnFullException();
    }

    public void removeCounter(int columnIndex)
    {
        for (int i = 0; i < counters[columnIndex].length; i++)
        {
            if (counters[columnIndex][i].equals(Counter.NONE) || i == getBoardHeight()-1)
            {
                counters[columnIndex][i-1] = NONE;
                return;
            }
        }
    }

    /**
     * Gets the winner of this game.
     * @return the winner's counter, the empty counter is the board is full, or null if there is no winner and the board has spaces.
     */
    public Counter getWinner()
    {
        Counter winningCounter = null;
        if (winningCounter == null)
            winningCounter = getHorizontalWinner();
        if (winningCounter == null)
            winningCounter = getVerticalWinner();
        if (winningCounter == null)
            winningCounter = getDiagonalWinner();
        if (winningCounter == null && isBoardFull())
            winningCounter = Counter.NONE;

        return winningCounter;
    }

    /**
     * Checks if this board is full
     * @return true if full
     */
    public boolean isBoardFull()
    {
        for (int column = 0; column<counters.length; column++)
        {
            if (counters[column][counters[column].length-1].equals(Counter.NONE))
            {
                return false;
            }
        }
        return true;
    }

    private Counter getDiagonalWinner()
    {
        for (int column = 0; column < counters.length; column++)
        {
            for (int row = 0; row<counters[0].length; row++)
            {
                Counter counter = getCounter(column, row);
                if (counter != Counter.NONE)
                {
                    for (int combo = 1; combo<5; combo++)
                    {
                        int offsetColumn = column+combo;
                        int offsetRow = row+combo;
                        if (combo == 4)
                            return counter;
                        if ((offsetColumn < 0 || offsetColumn >= getBoardWidth()) || (offsetRow < 0 || offsetRow >= getBoardHeight()) || getCounter(offsetColumn, offsetRow) != counter)
                            break;
                    }
                    for (int combo = 1; combo<5; combo++)
                    {
                        int offsetColumn = column-combo;
                        int offsetRow = row+combo;
                        if (combo == 4)
                            return counter;
                        if ((offsetColumn < 0 || offsetColumn >= getBoardWidth()) || (offsetRow < 0 || offsetRow >= getBoardHeight()) || getCounter(offsetColumn, offsetRow) != counter)
                            break;
                    }
                }
            }
        }
        return null;
    }

    private Counter getHorizontalWinner()
    {
        int comboSize;
        Counter comboCounter;
        for (int row = 0; row<counters[0].length; row++)
        {
            comboSize = 1;
            comboCounter = Counter.NONE;
            for (int column = 0; column < counters.length; column++)
            {
                if (counters[column][row].equals(comboCounter) && !counters[column][row].equals(Counter.NONE))
                {
                    comboSize++;
                    if (comboSize == 4)
                    {
                        return comboCounter;
                    }
                }
                else
                {
                    comboCounter = counters[column][row];
                    comboSize = 1;
                }
            }
        }
        return null;
    }

    private Counter getVerticalWinner()
    {
        int comboSize;
        Counter comboCounter;
        for (int column = 0; column<counters.length; column++)
        {
            comboSize = 1;
            comboCounter = Counter.NONE;
            for (int row = 0; row<counters[column].length; row++)
            {
                if (counters[column][row].equals(comboCounter) && !counters[column][row].equals(Counter.NONE))
                {
                    comboSize++;
                    if (comboSize == 4)
                    {
                        return comboCounter;
                    }
                }
                else
                {
                    comboCounter = counters[column][row];
                    comboSize = 1;
                }
            }
        }
        return null;
    }

    private int getColumnIndex(String column)
    {
        int columnIndex = -1;
        switch (column.toUpperCase())
        {
            case "A":
                columnIndex = 0;
                break;
            case "B":
                columnIndex = 1;
                break;
            case "C":
                columnIndex = 2;
                break;
            case "D":
                columnIndex = 3;
                break;
            case "E":
                columnIndex = 4;
                break;
            case "F":
                columnIndex = 5;
                break;
            case "G":
                columnIndex = 6;
                break;
        }
        return columnIndex;
    }

    public Board clone()
    {
        Board board = new Board(counters);
        return board;
    }
}
