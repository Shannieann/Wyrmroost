package com.java.shannieann.wyrmroost.world.feature.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmTriplePlant;
import com.java.shannieann.wyrmroost.block.plant.TriplePart;
import com.java.shannieann.wyrmroost.world.feature.plant.patch.FeaturePlantPatch;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public abstract class FeatureGroundPlant extends FeaturePlantPatch {

    protected FeatureGroundPlant(Codec<NoneFeatureConfiguration> codec, int tries, int spreadXZ, int spreadY) {
        super(codec, tries, spreadXZ, spreadY);
    }

    // =============
    //  PROCEDURAL GENERATION
    // =============
    protected abstract BlockState pickState(RandomSource random);

    @Override
    protected boolean placeAt(WorldGenLevel level, BlockPos pos, RandomSource random, BlockPos corner) {
        BlockState state = pickState(random);
        if (state == null || !level.isEmptyBlock(pos) || !state.canSurvive(level, pos)) {
            return false;
        }
        if (state.getBlock() instanceof BlockWyrmTriplePlant) {
            if (!level.isEmptyBlock(pos.above()) || !level.isEmptyBlock(pos.above(2))) {
                return false;
            }
            BlockWyrmTriplePlant.placeAt(level, state.setValue(BlockWyrmTriplePlant.PART, TriplePart.LOWER), pos, 2);
            return true;
        }
        if (state.getBlock() instanceof DoublePlantBlock) {
            if (!level.isEmptyBlock(pos.above())) {
                return false;
            }
            DoublePlantBlock.placeAt(level, state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), pos, 2);
            return true;
        }
        level.setBlock(pos, state, 2);
        return true;
    }
}
