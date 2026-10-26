package com.java.shannieann.wyrmroost.world.feature;

import java.util.ArrayList;
import java.util.List;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureDragonwood extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float GROVE_CHANCE = 0.5F;
    private static final int MIN_TREES = 2;
    private static final int MAX_TREES = 6;
    private static final int TREE_SPACING = 3;
    private static final int PLACEMENT_ATTEMPTS = 12;
    private static final int AREA_SIZE = 48;

    //SIZE
    private static final int MIN_HEIGHT = 9;
    private static final int MAX_HEIGHT = 15;

    //CANOPY
    private static final int MIN_CANOPY_RADIUS = 2;
    private static final int MAX_CANOPY_RADIUS = 3;
    private static final float EDGE_CHANCE = 0.5F;

    //STALK LEAVES
    private static final int LARGE_LEAF_BLOCKS = 2;
    private static final float SMALL_LEAF_START = 0.4F;
    private static final float SMALL_LEAF_CHANCE = 0.8F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureDragonwood(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        //TODO: Actual Placement
        if (random.nextFloat() >= GROVE_CHANCE) {
            return false;
        }

        BlockPos corner = new BlockPos((chunkX << 4) - 16, 0, (chunkZ << 4) - 16);
        int trees = randomBetween(random, MIN_TREES, MAX_TREES);
        List<BlockPos> trunks = new ArrayList<>();

        for (int attempt = 0; attempt < PLACEMENT_ATTEMPTS && trunks.size() < trees; attempt++) {
            int x = (chunkX << 4) + random.nextInt(16);
            int z = (chunkZ << 4) + random.nextInt(16);
            BlockPos base = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);

            if (!level.getBlockState(base.below()).getFluidState().isEmpty() || isTooClose(base, trunks)) {
                continue;
            }
            placeTree(level, random, corner, base);
            trunks.add(base);
        }
        return !trunks.isEmpty();
    }


    private static void placeTree(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos base) {
        int height = randomBetween(random, MIN_HEIGHT, MAX_HEIGHT);
        int topY = base.getY() + height - 1;
        int radius = randomBetween(random, MIN_CANOPY_RADIUS, MAX_CANOPY_RADIUS);
        int canopyBottom = topY - radius;
        int largeLeavesFrom = canopyBottom - LARGE_LEAF_BLOCKS;
        int smallLeavesFrom = base.getY() + Math.round(height * SMALL_LEAF_START);

        for (int y = base.getY(); y <= topY; y++) {
            BambooLeaves leaves = BambooLeaves.NONE;
            if (y >= largeLeavesFrom && y < canopyBottom) {
                leaves = BambooLeaves.LARGE;
            } else if (y >= smallLeavesFrom && y < largeLeavesFrom && random.nextFloat() < SMALL_LEAF_CHANCE) {
                leaves = BambooLeaves.SMALL;
            }
            placeLog(level, corner, base.atY(y), leaves);
        }

        placeCanopy(level, random, corner, base.atY(topY), radius);
    }

    private static void placeCanopy(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, int radius) {
        BlockState leaves = WyrmroostBlockRegistry.DRAGONWOOD_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int distance = x * x + y * y + z * z;
                    if (distance > radius * radius + radius) {
                        continue;
                    }
                    if (distance > radius * radius && random.nextFloat() >= EDGE_CHANCE) {
                        continue;
                    }
                    BlockPos pos = centre.offset(x, y, z);
                    if (isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, leaves, 2);
                    }
                }
            }
        }
    }

    private static void placeLog(WorldGenLevel level, BlockPos corner, BlockPos pos, BambooLeaves leaves) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.DRAGONWOOD_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.DRAGONWOOD_LOG.get().defaultBlockState().setValue(BambooStalkBlock.LEAVES, leaves);
        level.setBlock(pos, log, 2);
    }

    private static boolean isTooClose(BlockPos base, List<BlockPos> trunks) {
        for (BlockPos trunk : trunks) {
            if (Math.abs(trunk.getX() - base.getX()) < TREE_SPACING && Math.abs(trunk.getZ() - base.getZ()) < TREE_SPACING) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int dx = pos.getX() - corner.getX();
        int dz = pos.getZ() - corner.getZ();
        return dx >= 0 && dx < AREA_SIZE && dz >= 0 && dz < AREA_SIZE;
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}