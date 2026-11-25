package com.java.shannieann.wyrmroost.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;


public class ItemWildFireFlower extends BlockItem {

    // =============
    //  PARAMETERS
    // =============

    //BURNING
    private static final int IGNITE_SECONDS = 4;

    public ItemWildFireFlower(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && !entity.fireImmune()) {
            entity.igniteForSeconds(IGNITE_SECONDS);
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
