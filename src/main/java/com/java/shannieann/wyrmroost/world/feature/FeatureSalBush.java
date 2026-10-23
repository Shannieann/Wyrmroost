package com.java.shannieann.wyrmroost.world.feature;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;


public class FeatureSalBush implements Feature {
    //TODO: Confirm with Shan
    public static final FeatureSalBush INSTANCE = new FeatureSalBush();
    public static final MapCodec<FeatureSalBush> CODEC = MapCodec.unit(INSTANCE);

    // =============
    //  PARAMETERS
    // =============

    private static final BlockState LOG = Blocks.IRON_BLOCK.defaultBlockState();
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

    // =============
    //  PROCEDURAL GENERATION
    // =============
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        //TODO: Actual Placement
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

        for (int i = 0; i < arches; i++) {
            double angle = rotation + step * i + angleVariation(random, ARCH_ANGLE_RANGE_DEG);
            generateArchChain(level, random, centre, centre, angle, scale);
        }
        return true;
    }


    private static void generateArchChain(WorldGenLevel level, RandomSource random, BlockPos centre,
                                          BlockPos start, double angle, float scale) {
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
                    start.getZ() + (int) Math.round(dz * width), MAX_REACH - 1)) {
                width--;
            }

            height = Math.min(height, (int) Math.floor(width * MAX_HEIGHT_TO_WIDTH_RATIO));

            if (width < MIN_WIDTH || height < MIN_HEIGHT) {
                return;
            }

            int endX = start.getX() + (int) Math.round(dx * width);
            int endZ = start.getZ() + (int) Math.round(dz * width);
            BlockPos end = new BlockPos(endX, getSurfaceY(level, endX, endZ), endZ);

            drawArch(level, centre, start, end, height);

            start = end;
            size *= GENERATIONAL_SHRINK_FACTOR;
        }
    }

    private static void drawArch(WorldGenLevel level, BlockPos centre, BlockPos start, BlockPos end, int height) {
        int dx = end.getX() - start.getX();
        int dz = end.getZ() - start.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        int samples = (int) Math.ceil((horizontal + 2.0 * height) * 2.0) + 1;

        BlockPos previous = start;
        placeLogBlock(level, centre, previous);

        for (int s = 1; s <= samples; s++) {
            double t = (double) s / samples;
            int x = (int) Math.round(start.getX() + dx * t);
            int z = (int) Math.round(start.getZ() + dz * t);
            int y = (int) Math.round(start.getY() + (end.getY() - start.getY()) * t + height * Math.sin(Math.PI * t));
            BlockPos next = new BlockPos(x, y, z);

            connectBlocks(level, centre, previous, next);
            previous = next;
        }
    }

    private static void connectBlocks(WorldGenLevel level, BlockPos centre, BlockPos from, BlockPos to) {
        int currentX = from.getX();
        int currentY = from.getY();
        int currentZ = from.getZ();

        while (currentX != to.getX() || currentY != to.getY() || currentZ != to.getZ()) {
            int distanceX = Math.abs(to.getX() - currentX);
            int distanceY = Math.abs(to.getY() - currentY);
            int distanceZ = Math.abs(to.getZ() - currentZ);

            if (distanceY >= distanceX && distanceY >= distanceZ) {
                currentY += (to.getY() > currentY) ? 1 : -1;
            } else if (distanceX >= distanceZ) {
                currentX += (to.getX() > currentX) ? 1 : -1;
            } else {
                currentZ += (to.getZ() > currentZ) ? 1 : -1;
            }

            placeLogBlock(level, centre, new BlockPos(currentX, currentY, currentZ));
        }
    }


    //TODO: Currently replaces any block at targetPos
    private static void placeLogBlock(WorldGenLevel level, BlockPos centre, BlockPos pos) {
        if (isWithinReach(centre, pos.getX(), pos.getZ(), MAX_REACH)) {
            level.setBlock(pos, LOG, 2);
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