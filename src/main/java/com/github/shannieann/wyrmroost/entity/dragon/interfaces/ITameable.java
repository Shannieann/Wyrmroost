package com.github.shannieann.wyrmroost.entity.dragon.interfaces;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITameable {
    InteractionResult tameLogic (Player tamer, ItemStack stack);
}
