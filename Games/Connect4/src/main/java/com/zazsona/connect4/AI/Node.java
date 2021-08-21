package com.zazsona.connect4.AI;

public class Node
{
    private Node parent;
    private Node[] children;
    private int value;
    private int column;

    public Node(Node parent, int value, int column)
    {
        this.parent = parent;
        this.value = value;
        this.column = column;
        children = new Node[7];
    }

    public int getColumn()
    {
        return column;
    }

    public int getValue()
    {
        return value;
    }

    public boolean isTerminal()
    {
        if (children != null)
        {
            for (int i = 0; i<children.length; i++)
            {
                if (children[i] != null)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public Node[] getChildren()
    {
        return children;
    }

    public void addChild(int column, Node child)
    {
        children[column] = child;
    }
}
