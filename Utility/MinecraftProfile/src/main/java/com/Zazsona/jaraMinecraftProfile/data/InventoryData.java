package com.Zazsona.jaraMinecraftProfile.data;

import java.io.Serializable;

public class InventoryData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ItemData mainHandSlot;
    private ItemData offhandSlot;

    public InventoryData(ItemData mainHandSlot, ItemData offhandSlot)
    {
        this.mainHandSlot = mainHandSlot;
        this.offhandSlot = offhandSlot;
    }

    public ItemData getMainHandSlot()
    {
        return mainHandSlot;
    }

    public ItemData getOffhandSlot()
    {
        return offhandSlot;
    }
}
