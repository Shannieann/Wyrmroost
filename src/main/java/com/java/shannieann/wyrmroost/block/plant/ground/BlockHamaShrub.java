package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmTallPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.lang.Math;


public class BlockHamaShrub extends BlockWyrmTallPlant {
    public static final MapCodec<BlockHamaShrub> CODEC = simpleCodec(BlockHamaShrub::new);

    //WARMTH
    private static final int THAW_PER_TICK = 3;

    //TODO: Hide from Glacial Skulkers
    public BlockHamaShrub(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockHamaShrub> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        int frozen = entity.getTicksFrozen();
        if (frozen > 0) {
            entity.setTicksFrozen(Math.max(0, frozen - THAW_PER_TICK));
        }
    }
}
