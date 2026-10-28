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
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureSalBush extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    private static final int CHUNKS = 3;
    private static final int MAX_REACH = 23;

    //ARCH NUMBER
    private static final int MIN_ARCHES = 3;
    private static final int MAX_ARCHES = 5;

    //ARCH SCALING FACTORS
    private static final float SCALE_AT_MIN_ARCHES = 0.6F;
    private static final float SCALE_AT_MAX_ARCHES = 1.0F;

    //LARGE ARCH PARAMETERS
    private static final int LARGE_ARCH_MIN_WIDTH = 12;
    private static final int LARGE_ARCH_MAX_WIDTH = 16;
    private static final int LARGE_ARCH_MIN_HEIGHT = 8;
    private static final int LARGE_ARCH_MAX_HEIGHT = 9;

    //CHAINING
    private static final int MAXIMUM_GENERATIONS = 5;
    private static final float GENERATIONAL_SHRINK_FACTOR = 0.80F;
    private static final float CHAIN_CHANCE = 0.60F;

    //LIMITS
    private static final int ABSOLUTE_MAX_HEIGHT = 9;
    private static final int MIN_WIDTH = 2;
    private static final int MIN_HEIGHT = 2;
    private static final float MAX_HEIGHT_TO_WIDTH_RATIO = 0.75F;

    //ANGLES
    private static final float ARCH_ANGLE_RANGE_DEG = 15.0F;
    private static final float CHAIN_ANGLE_RANGE_DEG = 25.0F;

    //LEAVES
    private static final int LEAF_MIN_RADIUS = 2;
    private static final int LEAF_MAX_RADIUS = 4;
    private static final int LEAF_MIN_HEIGHT = 1;
    private static final int LEAF_MAX_HEIGHT = 2;
    private static final float LEAF_DENSITY = 0.6F;

    //VINES
    private static final float VINE_CHANCE = 0.50F;
    private static final int VINE_MIN_LENGTH = 1;
    private static final int VINE_MAX_LENGTH = 6;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureSalBush(Codec<NoneFeatureConfiguration> codec) {
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
        if (!TreeRegions.isRegionFor(chunkX, chunkZ, TreeRegions.SAL_BUSH)) {
            return false;
        }
        if (Math.floorMod(chunkX, CHUNKS) != 0 || Math.floorMod(chunkZ, CHUNKS) != 0) {
            return false;
        }

        int centreX = (chunkX << 4) + 8;
        int centreZ = (chunkZ << 4) + 8;
        BlockPos centre = new BlockPos(centreX, getSurfaceY(level, centreX, centreZ), centreZ);

        int arches = MIN_ARCHES + random.nextInt(MAX_ARCHES - MIN_ARCHES + 1);
        float scale = sizeScale(arches);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / arches;

        List<BlockPos> feet = new ArrayList<>();
        List<BlockPos> logs = new ArrayList<>();
        feet.add(centre);

        for (int i = 0; i < arches; i++) {
            double angle = rotation + step * i + angleVariation(random, ARCH_ANGLE_RANGE_DEG);
            generateArchChain(level, random, centre, centre, angle, scale, feet, logs);
        }

        for (BlockPos foot : feet) {
            placeLeafMound(level, random, centre, foot);
        }
        for (BlockPos log : logs) {
            placeVines(level, random, centre, log);
        }
        return true;
    }


    private static void generateArchChain(WorldGenLevel level, RandomSource random, BlockPos centre,
                                          BlockPos start, double angle, float scale, List<BlockPos> feet, List<BlockPos> logs) {
        float size = scale;

        for (int generation = 0; generation < MAXIMUM_GENERATIONS; generation++) {
            if (generation > 0) {
                if (random.nextFloat() >= CHAIN_CHANCE) {
                    return;
                }
                angle += angleVariation(random, CHAIN_ANGLE_RANGE_DEG);
            }

            int width = Math.round(randomBetween(random, LARGE_ARCH_MIN_WIDTH, LARGE_ARCH_MAX_WIDTH) * size);
            int height = Math.round(randomBetween(random, LARGE_ARCH_MIN_HEIGHT, LARGE_ARCH_MAX_HEIGHT) * size);

            height = Math.min(height, ABSOLUTE_MAX_HEIGHT);

            double dx = Math.cos(angle);
            double dz = Math.sin(angle);

            while (width >= MIN_WIDTH && !isWithinReach(centre,
                    start.getX() + (int) Math.round(dx * width),
                    start.getZ() + (int) Math.round(dz * width), MAX_REACH - Math.max(1, LEAF_MAX_RADIUS))) {
                width--;
            }

            height = Math.min(height, (int) Math.floor(width * MAX_HEIGHT_TO_WIDTH_RATIO));

            if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                return;
            }

            int endX = start.getX() + (int) Math.round(dx * width);
            int endZ = start.getZ() + (int) Math.round(dz * width);
            BlockPos end = new BlockPos(endX, getSurfaceY(level, endX, endZ), endZ);

            drawArch(level, centre, start, end, height, logs);
            feet.add(end);

            start = end;
            size *= GENERATIONAL_SHRINK_FACTOR;
        }
    }

    private static void drawArch(WorldGenLevel level, BlockPos centre, BlockPos start, BlockPos end, int height, List<BlockPos> logs) {
        int dx = end.getX() - start.getX();
        int dz = end.getZ() - start.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        int samples = (int) Math.ceil((horizontal + 2.0 * height) * 2.0) + 1;

        BlockPos previous = start;
        placeLogBlock(level, centre, previous, Direction.Axis.Y, logs);

        for (int s = 1; s <= samples; s++) {
            double t = (double) s / samples;
            int x = (int) Math.round(start.getX() + dx * t);
            int z = (int) Math.round(start.getZ() + dz * t);
            int y = (int) Math.round(start.getY() + (end.getY() - start.getY()) * t + height * Math.sin(Math.PI * t));
            BlockPos next = new BlockPos(x, y, z);

            connectBlocks(level, centre, previous, next, logs);
            previous = next;
        }
    }

    private static void connectBlocks(WorldGenLevel level, BlockPos centre, BlockPos from, BlockPos to, List<BlockPos> logs) {
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

            placeLogBlock(level, centre, new BlockPos(currentX, currentY, currentZ), axis, logs);
        }
    }


    //TODO: Currently replaces any block at targetPos
    private static void placeLogBlock(WorldGenLevel level, BlockPos centre, BlockPos pos, Direction.Axis axis, List<BlockPos> logs) {
        if (isWithinReach(centre, pos.getX(), pos.getZ(), MAX_REACH)) {
            BlockState log = WyrmroostBlockRegistry.SAL_BUSH_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
            level.setBlock(pos, log, 2);
            logs.add(pos);
        }
    }

    private static void placeLeafMound(WorldGenLevel level, RandomSource random, BlockPos centre, BlockPos foot) {
        int radius = randomBetween(random, LEAF_MIN_RADIUS, LEAF_MAX_RADIUS);
        int height = randomBetween(random, LEAF_MIN_HEIGHT, LEAF_MAX_HEIGHT);
        BlockState leaves = WyrmroostBlockRegistry.SAL_BUSH_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = 0; y < height; y++) {
                    double horizontal = (double) (x * x + z * z) / (radius * radius);
                    double vertical = (double) (y * y) / (height * height);
                    if (horizontal + vertical > 1.0 || random.nextFloat() >= LEAF_DENSITY) {
                        continue;
                    }
                    BlockPos pos = foot.offset(x, y, z);
                    if (isWithinReach(centre, pos.getX(), pos.getZ(), MAX_REACH) && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, leaves, 2);
                    }
                }
            }
        }
    }

    private static void placeVines(WorldGenLevel level, RandomSource random, BlockPos centre, BlockPos log) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() >= VINE_CHANCE) {
                continue;
            }
            BlockPos pos = log.relative(side);
            BlockState vine = WyrmroostBlockRegistry.SAL_BUSH_VINE.get().defaultBlockState()
                    .setValue(VineBlock.getPropertyForFace(side.getOpposite()), true);

            int length = randomBetween(random, VINE_MIN_LENGTH, VINE_MAX_LENGTH);
            for (int i = 0; i < length && isWithinReach(centre, pos.getX(), pos.getZ(), MAX_REACH) && level.getBlockState(pos).isAir(); i++) {
                level.setBlock(pos, vine, 2);
                pos = pos.below();
            }
        }
    }

    private static int getSurfaceY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
    }

    private static boolean isWithinReach(BlockPos centre, int x, int z, int limit) {
        int dx = x - centre.getX();
        int dz = z - centre.getZ();
        return dx * dx + dz * dz <= limit * limit;
    }

    private static float sizeScale(int arches) {
        float t = (float) (arches - MIN_ARCHES) / (MAX_ARCHES - MIN_ARCHES);
        return SCALE_AT_MIN_ARCHES + (SCALE_AT_MAX_ARCHES - SCALE_AT_MIN_ARCHES) * t;
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double angleVariation(RandomSource random, float maxDegrees) {
        return Math.toRadians((random.nextFloat() * 2.0F - 1.0F) * maxDegrees);
    }
}