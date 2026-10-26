package com.java.shannieann.wyrmroost.world.feature;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureCanar extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float TREE_CHANCE = 0.3F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_TRUNK_HEIGHT = 5;
    private static final int MAX_TRUNK_HEIGHT = 8;

    //LIMBS
    private static final int MIN_LIMB_LENGTH = 4;
    private static final int MAX_LIMB_LENGTH = 6;
    private static final float THREE_WAY_CHANCE = 0.25F;
    private static final float LIMB_ANGLE_RANGE_DEG = 20.0F;
    private static final float STEP_RISE_CHANCE = 0.7F;

    //FORKS
    private static final int MIN_GENERATIONS = 2;
    private static final int MAX_GENERATIONS = 3;
    private static final int MIN_FORK_ANGLE_DEG = 30;
    private static final int MAX_FORK_ANGLE_DEG = 50;
    private static final float FORK_SHRINK_FACTOR = 0.6F;
    private static final int MIN_FORK_LENGTH = 2;

    //LEAVES
    private static final float MIN_LEAF_RADIUS = 1.5F;
    private static final float MAX_LEAF_RADIUS = 2.5F;
    private static final float LEAF_EDGE_CHANCE = 0.5F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureCanar(Codec<NoneFeatureConfiguration> codec) {
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
        if (random.nextFloat() >= TREE_CHANCE) {
            return false;
        }

        BlockPos corner = new BlockPos((chunkX << 4) - 16, 0, (chunkZ << 4) - 16);
        int x = (chunkX << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        int z = (chunkZ << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        BlockPos base = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);

        if (!level.getBlockState(base.below()).getFluidState().isEmpty()) {
            return false;
        }

        int trunkHeight = randomBetween(random, MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
        for (int y = 0; y < trunkHeight; y++) {
            placeLog(level, corner, base.above(y), Direction.Axis.Y);
        }

        BlockPos fork = base.above(trunkHeight - 1);
        int generations = randomBetween(random, MIN_GENERATIONS, MAX_GENERATIONS);
        int limbs = random.nextFloat() < THREE_WAY_CHANCE ? 3 : 2;
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / limbs;

        for (int i = 0; i < limbs; i++) {
            double angle = rotation + step * i + angleVariation(random, LIMB_ANGLE_RANGE_DEG);
            int length = randomBetween(random, MIN_LIMB_LENGTH, MAX_LIMB_LENGTH);
            growLimb(level, random, corner, fork, angle, length, generations);
        }
        return true;
    }


    private static void growLimb(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start,
                                 double angle, int length, int generationsLeft) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            x += dx;
            z += dz;
            if (random.nextFloat() < STEP_RISE_CHANCE) {
                y++;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            previous = next;
        }

        int forkLength = Math.round(length * FORK_SHRINK_FACTOR);
        if (generationsLeft <= 1 || forkLength < MIN_FORK_LENGTH) {
            placeLeaves(level, random, corner, previous.above(), MIN_LEAF_RADIUS + random.nextFloat() * (MAX_LEAF_RADIUS - MIN_LEAF_RADIUS));
            return;
        }

        double spread = Math.toRadians(randomBetween(random, MIN_FORK_ANGLE_DEG, MAX_FORK_ANGLE_DEG));
        growLimb(level, random, corner, previous, angle - spread, forkLength, generationsLeft - 1);
        growLimb(level, random, corner, previous, angle + spread, forkLength, generationsLeft - 1);
    }

    private static void connectBlocks(WorldGenLevel level, BlockPos corner, BlockPos from, BlockPos to) {
        int currentX = from.getX();
        int currentY = from.getY();
        int currentZ = from.getZ();

        while (currentX != to.getX() || currentY != to.getY() || currentZ != to.getZ()) {
            int distanceX = Math.abs(to.getX() - currentX);
            int distanceY = Math.abs(to.getY() - currentY);
            int distanceZ = Math.abs(to.getZ() - currentZ);

            Direction.Axis axis;
            if (distanceY >= distanceX && distanceY >= distanceZ) {
                currentY += (to.getY() > currentY) ? 1 : -1;
                axis = Direction.Axis.Y;
            } else if (distanceX >= distanceZ) {
                currentX += (to.getX() > currentX) ? 1 : -1;
                axis = Direction.Axis.X;
            } else {
                currentZ += (to.getZ() > currentZ) ? 1 : -1;
                axis = Direction.Axis.Z;
            }

            placeLog(level, corner, new BlockPos(currentX, currentY, currentZ), axis);
        }
    }

    private static void placeLeaves(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, float radius) {
        BlockState leaves = WyrmroostBlockRegistry.CANAR_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int reach = (int) Math.ceil(radius);

        for (int x = -reach; x <= reach; x++) {
            for (int y = -reach; y <= reach; y++) {
                for (int z = -reach; z <= reach; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance > radius + 0.5 || (distance > radius - 0.5 && random.nextFloat() >= LEAF_EDGE_CHANCE)) {
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

    private static void placeLog(WorldGenLevel level, BlockPos corner, BlockPos pos, Direction.Axis axis) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.CANAR_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.CANAR_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        level.setBlock(pos, log, 2);
    }

    private static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int dx = pos.getX() - corner.getX();
        int dz = pos.getZ() - corner.getZ();
        return dx >= 0 && dx < AREA_SIZE && dz >= 0 && dz < AREA_SIZE;
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double angleVariation(RandomSource random, float maxDegrees) {
        return Math.toRadians((random.nextFloat() * 2.0F - 1.0F) * maxDegrees);
    }
}
