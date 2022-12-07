package com.github.shannieann.wyrmroost.containers.util;

import net.minecraft.world.Container;
import net.minecraftforge.items.IItemHandler;

public class DynamicSlot extends SlotBuilder
{
    public final int anchorX;
    public final int anchorY;

    public DynamicSlot(IItemHandler inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
        this.anchorX = x;
        this.anchorY = y;
    }

    public DynamicSlot(Container inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
        this.anchorX = x;
        this.anchorY = y;
    }

    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y)
    {
        this.x = anchorX + x;
        this.y = anchorY + y;
    }
}