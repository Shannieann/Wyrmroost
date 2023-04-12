package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.registry.WRIO;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Collection;

// ngl I had no idea how to do containers or screens so I had to watch a tutorial
// But now I understand them so its fine

public class NewTarragonTomeContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess containerAccess;
    //public final ContainerData data;

    // Client Constructor
    public NewTarragonTomeContainer(int id, Inventory playerInv) {
        this(id, playerInv, new ItemStackHandler(27), BlockPos.ZERO);
    }

    // Server Constructor
    public NewTarragonTomeContainer(int id, Inventory playerInv, IItemHandler slots, BlockPos pos) {
        super(WRIO.TARRAGON_TOME.get(), id);
        this.containerAccess = ContainerLevelAccess.create(playerInv.player.level, pos);
        //this.data = data;

        final int slotSizePlus2 = 40, startX = 43, startY = 205, hotbarY = 341;
        for(int column = 0; column < 9; column++){
            for (int row = 0; row < 3; row++){
                addSlot(new Slot(playerInv, 9 + row * 9 + column, startX + column * slotSizePlus2,
                        startY + row * slotSizePlus2));
            }

            addSlot(new Slot(playerInv, column, startX + column * slotSizePlus2, hotbarY));
        }

        // TODO chest inventory slots


    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static MenuConstructor getServerContainer(WRDragonEntity dragon, BlockPos pos) {
        return (id, playerInv, player) -> new NewTarragonTomeContainer(id, playerInv, dragon.getInventory(), pos);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return this.slots.get(pIndex).getItem(); // Don't allow quick moving for now

    }

}
