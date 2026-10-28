package com.java.shannieann.wyrmroost.world.feature;

import java.util.ArrayList;
import java.util.List;

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


public class FeatureSuren extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float TREE_CHANCE = 0.4F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;
    private static final int MAX_FLUID_DEPTH = 3;

    //TRUNK
    private static final int MIN_RAISE = 2;
    private static final int MAX_RAISE = 4;
    private static final int MIN_TRUNK_HEIGHT = 5;
    private static final int MAX_TRUNK_HEIGHT = 8;

    //ROOTS
    private static final int MIN_ROOTS = 5;
    private static final int MAX_ROOTS = 8;
    private static final float ROOT_ANGLE_RANGE_DEG = 15.0F;
    private static final float ROOT_LEVEL_CHANCE = 0.3F;
    private static final int MAX_ROOT_STEPS = 6;
    private static final int ROOT_DEPTH = 4;

    //SPLIT
    private static final float SPLIT_CHANCE = 0.5F;
    private static final int MIN_SPLIT_LENGTH = 2;
    private static final int MAX_SPLIT_LENGTH = 4;
    private static final float SPLIT_ANGLE_RANGE_DEG = 25.0F;

    //CANOPY
    private static final int MIN_CANOPY_RADIUS = 5;
    private static final int MAX_CANOPY_RADIUS = 6;
    private static final float LEAF_EDGE_CHANCE = 0.7F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureSuren(Codec<NoneFeatureConfiguration> codec) {
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
        if (!TreeRegions.isRegionFor(chunkX, chunkZ, TreeRegions.SUREN)) {
            return false;
        }
        if (random.nextFloat() >= TREE_CHANCE) {
            return false;
        }

        BlockPos corner = new BlockPos((chunkX << 4) - 16, 0, (chunkZ << 4) - 16);
        int x = (chunkX << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        int z = (chunkZ << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        BlockPos surface = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), z);

        BlockPos below = surface.below();
        for (int depth = 0; !level.getBlockState(below).getFluidState().isEmpty(); depth++) {
            if (depth >= MAX_FLUID_DEPTH) {
                return false;
            }
            below = below.below();
        }

        BlockPos trunkBase = surface.above(randomBetween(random, MIN_RAISE, MAX_RAISE));
        int trunkHeight = randomBetween(random, MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
        List<BlockPos> trunk = new ArrayList<>();
        trunk.add(trunkBase);
        trunk.add(trunkBase.east());
        trunk.add(trunkBase.south());
        trunk.add(trunkBase.east().south());

        for (BlockPos column : trunk) {
            for (int y = 0; y < trunkHeight; y++) {
                placeLog(level, corner, column.above(y), Direction.Axis.Y);
            }
        }

        int roots = randomBetween(random, MIN_ROOTS, MAX_ROOTS);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / roots;
        for (int i = 0; i < roots; i++) {
            double angle = rotation + step * i + angleVariation(random, ROOT_ANGLE_RANGE_DEG);
            BlockPos start = trunk.get(random.nextInt(trunk.size())).above(random.nextInt(2));
            growRoot(level, random, corner, start, angle);
        }

        BlockPos top = trunkBase.above(trunkHeight - 1);
        if (random.nextFloat() < SPLIT_CHANCE) {
            double angle = random.nextFloat() * Math.PI * 2.0;
            for (int side = 0; side < 2; side++) {
                double limbAngle = angle + Math.PI * side + angleVariation(random, SPLIT_ANGLE_RANGE_DEG);
                BlockPos end = growLimb(level, corner, top, limbAngle, randomBetween(random, MIN_SPLIT_LENGTH, MAX_SPLIT_LENGTH));
                placeCanopy(level, random, corner, end.above(), randomBetween(random, MIN_CANOPY_RADIUS - 1, MAX_CANOPY_RADIUS - 1));
            }
        } else {
            placeCanopy(level, random, corner, top.above(), randomBetween(random, MIN_CANOPY_RADIUS, MAX_CANOPY_RADIUS));
        }
        return true;
    }


    private static void growRoot(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start, double angle) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;

        for (int i = 0; i < MAX_ROOT_STEPS; i++) {
            x += dx;
            z += dz;
            if (i > 0 || random.nextFloat() >= ROOT_LEVEL_CHANCE) {
                y--;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            previous = next;
            if (isSolid(level, next.below())) {
                return;
            }
        }

        BlockPos pos = previous.below();
        for (int i = 0; i < ROOT_DEPTH && !isSolid(level, pos); i++) {
            placeLog(level, corner, pos, Direction.Axis.Y);
            pos = pos.below();
        }
    }

    private static BlockPos growLimb(WorldGenLevel level, BlockPos corner, BlockPos start, double angle, int length) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            x += dx;
            z += dz;
            BlockPos next = new BlockPos((int) Math.floor(x), start.getY() + i, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            previous = next;
        }
        return previous;
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

    private static void placeCanopy(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, int radius) {
        BlockState leaves = WyrmroostBlockRegistry.SUREN_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int radiusY = Math.max(2, radius - 1);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radiusY; y <= radiusY; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = (double) (x * x + z * z) / (radius * radius) + (double) (y * y) / (radiusY * radiusY);
                    if (distance > 1.2 || (distance > 0.8 && random.nextFloat() >= LEAF_EDGE_CHANCE)) {
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
        if (!current.isAir() && current.getFluidState().isEmpty() && !current.is(WyrmroostBlockRegistry.SUREN_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.SUREN_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        level.setBlock(pos, log, 2);
    }

    private static boolean isSolid(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getFluidState().isEmpty() && !state.is(WyrmroostBlockRegistry.SUREN_LEAVES.get());
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
