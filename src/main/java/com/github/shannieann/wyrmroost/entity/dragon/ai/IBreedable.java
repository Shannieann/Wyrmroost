package com.github.shannieann.wyrmroost.entity.dragon.ai;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IBreedable {
    InteractionResult breedLogic (Player tamer, ItemStack stack);

    int hatchTime();
}
