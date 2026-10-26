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


public class FeatureShimarin extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float TREE_CHANCE = 0.3F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_HEIGHT = 16;
    private static final int MAX_HEIGHT = 22;
    private static final int MIN_FLARE = 2;
    private static final int MAX_FLARE = 5;
    private static final int BARE_TOP = 2;

    //ROOTS
    private static final int MIN_ROOTS = 3;
    private static final int MAX_ROOTS = 6;
    private static final int MIN_ROOT_LENGTH = 2;
    private static final int MAX_ROOT_LENGTH = 4;
    private static final float ROOT_ANGLE_RANGE_DEG = 20.0F;

    //BRANCHES
    private static final float BRANCH_START = 0.3F;
    private static final int MIN_BRANCH_GAP = 1;
    private static final int MAX_BRANCH_GAP = 2;
    private static final int MIN_BRANCH_LENGTH = 2;
    private static final int MAX_BRANCH_LENGTH = 5;
    private static final float BRANCH_TURN_DEG = 137.5F;
    private static final float BRANCH_ANGLE_RANGE_DEG = 30.0F;
    private static final float STEP_RISE_CHANCE = 0.4F;
    private static final int MIN_HOOK = 1;
    private static final int MAX_HOOK = 2;
    private static final float TWIG_CHANCE = 0.5F;
    private static final float TWIG_ANGLE_DEG = 45.0F;

    //DRAPES
    private static final float DRAPE_CHANCE = 0.6F;
    private static final float DRAPE_COVER_CHANCE = 0.5F;
    private static final int MIN_DRAPE = 2;
    private static final int MAX_DRAPE = 6;
    private static final int DRAPE_GROUND_GAP = 2;

    //LEAF POOL
    private static final float MIN_POOL_RADIUS = 2.5F;
    private static final float MAX_POOL_RADIUS = 4.0F;
    private static final float POOL_DENSITY = 0.6F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureShimarin(Codec<NoneFeatureConfiguration> codec) {
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

        int height = randomBetween(random, MIN_HEIGHT, MAX_HEIGHT);
        for (int y = 0; y < height; y++) {
            placeLog(level, corner, base.above(y), Direction.Axis.Y);
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int flare = randomBetween(random, MIN_FLARE, MAX_FLARE);
            for (int y = 0; y < flare; y++) {
                placeLog(level, corner, base.relative(direction).above(y), Direction.Axis.Y);
            }
            int diagonal = random.nextInt(MIN_FLARE + 1);
            for (int y = 0; y < diagonal; y++) {
                placeLog(level, corner, base.relative(direction).relative(direction.getClockWise()).above(y), Direction.Axis.Y);
            }
        }

        int roots = randomBetween(random, MIN_ROOTS, MAX_ROOTS);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double rootStep = Math.PI * 2.0 / roots;
        for (int i = 0; i < roots; i++) {
            double angle = rotation + rootStep * i + angleVariation(random, ROOT_ANGLE_RANGE_DEG);
            growRoot(level, random, corner, base, angle);
        }

        List<BlockPos> branchBlocks = new ArrayList<>();
        int firstBranch = Math.round(height * BRANCH_START);
        int lastBranch = height - 1 - BARE_TOP;
        double angle = random.nextFloat() * Math.PI * 2.0;
        for (int y = firstBranch; y <= lastBranch; y += randomBetween(random, MIN_BRANCH_GAP, MAX_BRANCH_GAP)) {
            float t = (float) (y - firstBranch) / Math.max(1, lastBranch - firstBranch);
            double shape = Math.sin(Math.PI * (0.2 + 0.8 * t));
            int length = MIN_BRANCH_LENGTH + (int) Math.round((MAX_BRANCH_LENGTH - MIN_BRANCH_LENGTH) * shape) + random.nextInt(2) - 1;
            length = Math.max(1, length);

            angle += Math.toRadians(BRANCH_TURN_DEG) + angleVariation(random, BRANCH_ANGLE_RANGE_DEG);
            BlockPos start = base.above(y);
            BlockPos tip = growBranch(level, random, corner, start, angle, length, branchBlocks);
            hook(level, random, corner, tip);

            if (length >= 3 && random.nextFloat() < TWIG_CHANCE) {
                BlockPos middle = branchBlocks.get(branchBlocks.size() - 1 - length / 2);
                double twigAngle = angle + (random.nextBoolean() ? 1 : -1) * Math.toRadians(TWIG_ANGLE_DEG);
                BlockPos twigTip = growBranch(level, random, corner, middle, twigAngle, randomBetween(random, 1, 2), branchBlocks);
                hook(level, random, corner, twigTip);
            }
        }

        BlockState leaves = WyrmroostBlockRegistry.SHIMARIN_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int drape = randomBetween(random, MIN_DRAPE, MAX_DRAPE);
        for (BlockPos pos : branchBlocks) {
            if (random.nextFloat() >= DRAPE_CHANCE) {
                continue;
            }
            if (random.nextFloat() < DRAPE_COVER_CHANCE) {
                placeLeaves(level, corner, pos.above(), leaves);
            }
            int strand = Math.max(1, drape + random.nextInt(3) - 1);
            for (int i = 1; i <= strand; i++) {
                BlockPos below = pos.below(i);
                if (!level.getBlockState(below).isAir() || !level.getBlockState(below.below(DRAPE_GROUND_GAP)).isAir()) {
                    break;
                }
                placeLeaves(level, corner, below, leaves);
            }
        }

        placePool(level, random, corner, base, leaves);
        return true;
    }


    private static void growRoot(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos base, double angle) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = base.getX() + 0.5 + dx;
        double z = base.getZ() + 0.5 + dz;
        int y = base.getY() + randomBetween(random, 1, 2);
        BlockPos previous = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
        placeLog(level, corner, previous, Direction.Axis.Y);

        int length = randomBetween(random, MIN_ROOT_LENGTH, MAX_ROOT_LENGTH);
        for (int i = 0; i < length; i++) {
            x += dx;
            z += dz;
            if (y > base.getY()) {
                y--;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            previous = next;
        }
    }

    private static BlockPos growBranch(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start,
                                       double angle, int length, List<BlockPos> branchBlocks) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            x += dx;
            z += dz;
            if (i > 1 && random.nextFloat() < STEP_RISE_CHANCE) {
                y++;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            branchBlocks.add(next);
            previous = next;
        }
        return previous;
    }

    private static void hook(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos tip) {
        int hook = randomBetween(random, MIN_HOOK, MAX_HOOK);
        connectBlocks(level, corner, tip, tip.above(hook));
    }

    private static void placePool(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos base, BlockState leaves) {
        float radius = MIN_POOL_RADIUS + random.nextFloat() * (MAX_POOL_RADIUS - MIN_POOL_RADIUS);
        int reach = (int) Math.ceil(radius) + 1;

        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                double distance = Math.sqrt(x * x + z * z) + random.nextFloat() - 0.5;
                float chance = distance <= radius - 1.0 ? POOL_DENSITY : POOL_DENSITY / 2.0F;
                if (distance > radius || random.nextFloat() >= chance) {
                    continue;
                }
                BlockPos pos = base.offset(x, 0, z);
                BlockState ground = level.getBlockState(pos.below());
                if (level.getBlockState(pos).isAir() && !ground.isAir() && ground.getFluidState().isEmpty()) {
                    placeLeaves(level, corner, pos, leaves);
                }
            }
        }
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

    private static void placeLog(WorldGenLevel level, BlockPos corner, BlockPos pos, Direction.Axis axis) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.SHIMARIN_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.SHIMARIN_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        level.setBlock(pos, log, 2);
    }

    private static void placeLeaves(WorldGenLevel level, BlockPos corner, BlockPos pos, BlockState leaves) {
        if (isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
            level.setBlock(pos, leaves, 2);
        }
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
