package com.java.shannieann.wyrmroost.block.plant.water;

import com.java.shannieann.wyrmroost.block.plant.attached.BlockAttachedPlant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;


public final class CoralDeath {

    //TIMING
    private static final int MIN_DEATH_DELAY = 60;
    private static final int MAX_DEATH_DELAY = 100;

    public static boolean isWet(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            return true;
        }
        for (Direction direction : Direction.values()) {
            if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    public static void scheduleCheck(ScheduledTickAccess tickAccess, BlockPos pos, Block block, RandomSource random) {
        tickAccess.scheduleTick(pos, block, MIN_DEATH_DELAY + random.nextInt(MAX_DEATH_DELAY - MIN_DEATH_DELAY + 1));
    }

    public static int deathDelay(RandomSource random) {
        return MIN_DEATH_DELAY + random.nextInt(MAX_DEATH_DELAY - MIN_DEATH_DELAY + 1);
    }

    public static void dieIfDry(BlockState state, ServerLevel level, BlockPos pos, Block dead) {
        if (dead == null || isWet(state, level, pos)) {
            return;
        }
        BlockState deadState = dead.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false);
        if (state.hasProperty(BlockAttachedPlant.FACING) && deadState.hasProperty(BlockAttachedPlant.FACING)) {
            deadState = deadState.setValue(BlockAttachedPlant.FACING, state.getValue(BlockAttachedPlant.FACING));
        }
        level.setBlock(pos, deadState, 2);
    }

}
