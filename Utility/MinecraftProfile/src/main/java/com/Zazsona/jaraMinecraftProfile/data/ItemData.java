package com.Zazsona.jaraMinecraftProfile.data;

import java.io.Serializable;

public class ItemData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private int quantity;

    public ItemData(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName()
    {
        return name;
    }

    public int getQuantity()
    {
        return quantity;
    }
}
