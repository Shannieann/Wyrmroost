package com.java.shannieann.wyrmroost.world.feature.plant.water;

import com.java.shannieann.wyrmroost.block.plant.water.BlockVarishCoral;
import com.java.shannieann.wyrmroost.world.feature.plant.patch.FeaturePlantPatch;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public abstract class FeatureUnderwaterPlant extends FeaturePlantPatch {

    protected FeatureUnderwaterPlant(Codec<NoneFeatureConfiguration> codec, int tries, int spreadXZ, int spreadY) {
        super(codec, tries, spreadXZ, spreadY);
    }

    // =============
    //  PROCEDURAL GENERATION
    // =============
    protected abstract BlockState pickState(RandomSource random);

    protected static boolean isWater(WorldGenLevel level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        return fluid.is(FluidTags.WATER) && fluid.isSource() && level.getBlockState(pos).is(Blocks.WATER);
    }

    @Override
    protected boolean placeAt(WorldGenLevel level, BlockPos pos, RandomSource random, BlockPos corner) {
        if (!isWater(level, pos)) {
            return false;
        }
        BlockState state = pickState(random);
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }
        if (state.getBlock() instanceof BlockVarishCoral) {
            if (!isWater(level, pos.above())) {
                return false;
            }
            BlockState lower = state.setValue(BlockVarishCoral.HALF, DoubleBlockHalf.LOWER);
            if (!lower.canSurvive(level, pos)) {
                return false;
            }
            BlockVarishCoral.placeAt(level, state, pos, 2);
            return true;
        }
        if (!state.canSurvive(level, pos)) {
            return false;
        }
        level.setBlock(pos, state, 2);
        return true;
    }
}
