package com.Zazsona.jaraMinecraftProfile.data;

import java.io.Serializable;

public class PlayerData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private String uuid;
    private boolean online;
    private int hp;
    private int hunger;
    private int level;
    private int[] location;
    private InventoryData inventoryData;

    public PlayerData(String name, String uuid, boolean online, int hp, int hunger, int level, int[] location, InventoryData inventoryData)
    {
        this.name = name;
        this.uuid = uuid;
        this.online = online;
        this.hp = hp;
        this.hunger = hunger;
        this.level = level;
        this.location = location;
        this.inventoryData = inventoryData;
    }

    public String getName()
    {
        return name;
    }

    public String getUuid()
    {
        return uuid;
    }

    public int getHp()
    {
        return hp;
    }

    public int getHunger()
    {
        return hunger;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isOnline()
    {
        return online;
    }

    public int[] getLocation()
    {
        return location;
    }

    public InventoryData getInventoryData()
    {
        return inventoryData;
    }
}
