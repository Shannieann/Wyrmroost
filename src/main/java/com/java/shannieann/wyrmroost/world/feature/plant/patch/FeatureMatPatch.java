package com.java.shannieann.wyrmroost.world.feature.plant.patch;

import com.java.shannieann.wyrmroost.block.plant.ground.BlockSigantMat;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public abstract class FeatureMatPatch extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //AREA
    private static final int AREA_SIZE = 48;

    //PATCH
    private static final int MIN_SIZE = 3;
    private static final int MAX_SIZE = 6;
    private static final int MAX_STEP = 0;

    protected FeatureMatPatch(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    // =============
    //  PROCEDURAL GENERATION
    // =============
    protected abstract Block getMat();

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos corner = new BlockPos(((origin.getX() >> 4) << 4) - 16, 0, ((origin.getZ() >> 4) << 4) - 16);
        int width = randomBetween(random, MIN_SIZE, MAX_SIZE);
        int depth = randomBetween(random, MIN_SIZE, MAX_SIZE);
        BlockPos start = origin.offset(-width / 2, 0, -depth / 2);
        BlockPos[] cells = new BlockPos[width * depth];
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                int x = start.getX() + dx;
                int z = start.getZ() + dz;
                BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);
                if (!isInsideArea(corner, pos) || Math.abs(pos.getY() - origin.getY()) > MAX_STEP || !level.isEmptyBlock(pos)) {
                    return false;
                }
                if (!getMat().defaultBlockState().canSurvive(level, pos)) {
                    return false;
                }
                cells[dx * depth + dz] = pos;
            }
        }
        BlockState mat = getMat().defaultBlockState();
        for (BlockPos pos : cells) {
            level.setBlock(pos, mat, 2);
        }
        for (BlockPos pos : cells) {
            level.setBlock(pos, BlockSigantMat.connect(mat, level, pos), 2);
        }
        return true;
    }

    private static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int x = pos.getX() - corner.getX();
        int z = pos.getZ() - corner.getZ();
        return x >= 0 && x < AREA_SIZE && z >= 0 && z < AREA_SIZE;
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
