package com.Zazsona.Connect4.AI;

public class Node
{
    private boolean isMaxPlayer;
    private Node parent;
    private Node[] children;
    private int value;
    private int column;

    public Node(Node parent, int value, int column, boolean isMaxPlayer)
    {
        this.parent = parent;
        this.value = value;
        this.column = column;
        this.isMaxPlayer = isMaxPlayer;
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

    public void setValue(int value)
    {
        this.value = value;
    }

    public Node getParent()
    {
        return parent;
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
