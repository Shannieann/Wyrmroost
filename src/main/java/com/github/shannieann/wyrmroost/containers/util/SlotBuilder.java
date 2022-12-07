package com.github.shannieann.wyrmroost.containers.util;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SlotBuilder extends SlotItemHandler
{
    @Nullable public Vec2 iconUV;
    private int limit = super.getMaxStackSize();
    private BooleanSupplier isEnabled = () -> true;
    private Predicate<ItemStack> isItemValid = super::mayPlace;
    private Predicate<Player> canTakeStack = super::mayPickup;
    private Consumer<SlotBuilder> onSlotUpdate = s ->
    {
    };

    public SlotBuilder(IItemHandler handler, int index, int posX, int posY)
    {
        super(handler, index, posX, posY);
    }

    public SlotBuilder(Container inventory, int index, int x, int y)
    {
        super(new InvWrapper(inventory), index, x, y);
    }

    public SlotBuilder condition(BooleanSupplier isEnabled)
    {
        this.isEnabled = isEnabled;
        return this;
    }

    public SlotBuilder onUpdate(Consumer<SlotBuilder> onUpdate)
    {
        this.onSlotUpdate = onUpdate;
        return this;
    }

    public SlotBuilder only(Predicate<ItemStack> isItemValid)
    {
        this.isItemValid = isItemValid;
        return this;
    }

    public SlotBuilder only(ItemLike item)
    {
        return only(s -> s.getItem() == item);
    }

    public SlotBuilder only(Class<? extends ItemLike> clazz)
    {
        return only(s -> {
            Item item = s.getItem();
            if (Block.class.isAssignableFrom(clazz))
                return item instanceof BlockItem && clazz.isInstance(((BlockItem) item).getBlock());
            return clazz.isInstance(item);
        });
    }

    public SlotBuilder not(ItemLike item)
    {
        return only(s -> s.getItem() != item);
    }

    public SlotBuilder noShulkers()
    {
        return only(s -> !(Block.byItem(s.getItem()) instanceof ShulkerBoxBlock));
    }

    public SlotBuilder canTake(Predicate<Player> canTakeStack)
    {
        this.canTakeStack = canTakeStack;
        return this;
    }

    public SlotBuilder limit(int limit)
    {
        this.limit = limit;
        return this;
    }

    public SlotBuilder iconUV(Vec2 uv)
    {
        this.iconUV = uv;
        return this;
    }

    // ===

    @Override
    public boolean isActive()
    {
        return isEnabled.getAsBoolean();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack)
    {
        return isItemValid.test(stack);
    }

    @Override
    public int getMaxStackSize()
    {
        return limit;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack)
    {
        return limit;
    }

    @Override
    public boolean mayPickup(Player player)
    {
        return canTakeStack.test(player);
    }

    @Override
    public void setChanged()
    {
        onSlotUpdate.accept(this);
    }

    public void blitBackgroundIcon(Screen screen, PoseStack ms, int x, int y)
    {
        if (iconUV != null)
            screen.blit(ms, x, y, (int) iconUV.x, (int) iconUV.y, 16, 16);
    }
}
