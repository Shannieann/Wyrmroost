package com.java.shannieann.wyrmroost.world.feature.plant.attached;

import com.java.shannieann.wyrmroost.block.plant.attached.BlockAttachedPlant;
import com.java.shannieann.wyrmroost.world.feature.plant.patch.FeaturePlantPatch;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public abstract class FeatureAttachedPlant extends FeaturePlantPatch {
    private static final Direction[] DIRECTIONS = Direction.values();

    protected FeatureAttachedPlant(Codec<NoneFeatureConfiguration> codec, int tries, int spreadXZ, int spreadY) {
        super(codec, tries, spreadXZ, spreadY);
    }

    // =============
    //  PROCEDURAL GENERATION
    // =============
    protected abstract BlockState pickState(RandomSource random);

    protected boolean underwater() {
        return false;
    }

    @Override
    protected boolean placeAt(WorldGenLevel level, BlockPos pos, RandomSource random, BlockPos corner) {
        FluidState fluid = level.getFluidState(pos);
        boolean water = fluid.is(FluidTags.WATER) && fluid.isSource();
        if (underwater() ? !water : !level.isEmptyBlock(pos)) {
            return false;
        }
        BlockState state = pickState(random).setValue(BlockAttachedPlant.WATERLOGGED, water);
        int start = random.nextInt(DIRECTIONS.length);
        for (int i = 0; i < DIRECTIONS.length; i++) {
            BlockState facing = state.setValue(BlockAttachedPlant.FACING, DIRECTIONS[(start + i) % DIRECTIONS.length]);
            if (facing.canSurvive(level, pos)) {
                level.setBlock(pos, facing, 2);
                return true;
            }
        }
        return false;
    }
}
