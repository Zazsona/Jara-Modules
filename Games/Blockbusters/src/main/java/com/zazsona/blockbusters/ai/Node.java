package com.zazsona.blockbusters.ai;

import java.util.ArrayList;
import java.util.List;

public class Node
{
    private int value;
    private int column;
    private int row;
    private ArrayList<Node> children;

    public Node(int column, int row, int value)
    {
        this.value = value;
        this.column = column;
        this.row = row;
        children = new ArrayList<>();
    }

    public int getColumn()
    {
        return column;
    }

    public int getRow()
    {
        return row;
    }

    public int getValue()
    {
        return value;
    }

    public boolean isTerminal()
    {
        return children.size() == 0;
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public void addChild(Node child)
    {
        children.add(child);
    }
}
