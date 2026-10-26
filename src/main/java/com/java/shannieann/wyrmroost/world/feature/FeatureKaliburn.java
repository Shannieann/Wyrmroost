package com.java.shannieann.wyrmroost.world.feature;

import java.util.ArrayList;
import java.util.List;

import com.java.shannieann.wyrmroost.block.BlockKaliburnLeaves;
import com.java.shannieann.wyrmroost.block.BlockKaliburnLog;
import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureKaliburn extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final int CHUNKS = 2;
    private static final float TREE_CHANCE = 0.75F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //SIZE
    private static final int MIN_HEIGHT = 8;
    private static final int MAX_HEIGHT = 20;
    private static final int MIN_SPREAD = 5;
    private static final int MAX_SPREAD = 15;

    //TRUNK
    private static final int THICK_TRUNK_MIN_HEIGHT = 13;
    private static final float THICK_TRUNK_PART = 0.5F;
    private static final int MAX_FLARE_RADIUS = 3;
    private static final float FLARE_DENSITY = 0.7F;
    private static final int ROOT_DEPTH = 4;

    //BRANCHES
    private static final int MIN_BRANCHES = 3;
    private static final int MAX_BRANCHES = 6;
    private static final float BRANCH_ANGLE_RANGE_DEG = 25.0F;
    private static final float LOWEST_BRANCH = 0.2F;
    private static final float HIGHEST_BRANCH = 0.5F;
    private static final float FLAT_STEP_CHANCE = 0.3F;
    private static final float STEEP_STEP_CHANCE = 0.2F;
    private static final int MIN_TIP = 1;
    private static final int MAX_TIP = 4;

    //FORKS
    private static final float FORK_CHANCE = 0.4F;
    private static final int FORK_MIN_ANGLE_DEG = 25;
    private static final int FORK_MAX_ANGLE_DEG = 50;
    private static final float FORK_SHRINK_FACTOR = 0.6F;

    //HAIR
    private static final float LEAF_CHANCE = 0.90F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureKaliburn(Codec<NoneFeatureConfiguration> codec) {
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
        if (Math.floorMod(chunkX, CHUNKS) != 0 || Math.floorMod(chunkZ, CHUNKS) != 0 || random.nextFloat() >= TREE_CHANCE) {
            return false;
        }

        BlockPos corner = new BlockPos((chunkX << 4) - 16, 0, (chunkZ << 4) - 16);

        int centreX = (chunkX << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        int centreZ = (chunkZ << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
        int groundY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centreX, centreZ);
        BlockPos centre = new BlockPos(centreX, groundY, centreZ);

        if (!level.getBlockState(centre.below()).getFluidState().isEmpty()) {
            return false;
        }

        int height = randomBetween(random, MIN_HEIGHT, MAX_HEIGHT);
        float size = (float) (height - MIN_HEIGHT) / (MAX_HEIGHT - MIN_HEIGHT);
        int spread = MIN_SPREAD + Math.round(size * (MAX_SPREAD - MIN_SPREAD));
        int topY = groundY + height - 1;
        boolean thick = height >= THICK_TRUNK_MIN_HEIGHT;

        List<BlockPos> logs = new ArrayList<>();

        List<BlockPos> footprint = new ArrayList<>();
        footprint.add(centre);
        if (thick) {
            footprint.add(centre.east());
            footprint.add(centre.south());
            footprint.add(centre.east().south());
        }

        int flareRadius = 1 + Math.round(size * (MAX_FLARE_RADIUS - 1));
        int thickTopY = thick ? groundY + Math.round(height * THICK_TRUNK_PART) : groundY;
        placeTrunk(level, random, corner, centre, footprint, topY, thickTopY, flareRadius, logs);

        int branches = MIN_BRANCHES + Math.round(size * (MAX_BRANCHES - MIN_BRANCHES - 1)) + random.nextInt(2);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / branches;
        int lowest = groundY + Math.round(height * LOWEST_BRANCH);
        int highest = groundY + Math.round(height * HIGHEST_BRANCH);

        for (int i = 0; i < branches; i++) {
            double angle = rotation + step * i + angleVariation(random, BRANCH_ANGLE_RANGE_DEG);
            BlockPos start = centre.atY(randomBetween(random, lowest, highest));
            int length = randomBetween(random, (spread + 1) / 2, spread);
            growBranch(level, random, corner, centre, start, angle, length, topY, spread, thick, true, logs);
        }

        int leafStartY = groundY + flareRadius;
        for (BlockPos log : logs) {
            if (log.getY() >= leafStartY) {
                placeLeaves(level, random, corner, log);
            }
        }
        return true;
    }


    private static void placeTrunk(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, List<BlockPos> footprint,
                                   int topY, int thickTopY, int flareRadius, List<BlockPos> logs) {
        for (int y = centre.getY(); y <= topY; y++) {
            if (y <= thickTopY) {
                for (BlockPos foot : footprint) {
                    placeLog(level, corner, foot.atY(y), Direction.Axis.Y, logs);
                }
            } else {
                placeLog(level, corner, centre.atY(y), Direction.Axis.Y, logs);
            }
        }

        for (int layer = 0; layer < flareRadius; layer++) {
            int radius = flareRadius - layer;
            for (int x = -radius - 1; x <= radius + 1; x++) {
                for (int z = -radius - 1; z <= radius + 1; z++) {
                    BlockPos pos = centre.offset(x, layer, z);
                    int distance = distanceToFootprint(pos, footprint);
                    if (distance == 0 || distance > radius) {
                        continue;
                    }
                    if (distance == radius && random.nextFloat() >= FLARE_DENSITY) {
                        continue;
                    }
                    placeLog(level, corner, pos, Direction.Axis.Y, logs);
                    if (layer == 0) {
                        placeRoot(level, corner, pos, logs);
                    }
                }
            }
        }
        for (BlockPos foot : footprint) {
            placeRoot(level, corner, foot, logs);
        }
    }

    private static void placeRoot(WorldGenLevel level, BlockPos corner, BlockPos base, List<BlockPos> logs) {
        BlockPos pos = base.below();
        for (int i = 0; i < ROOT_DEPTH && level.getBlockState(pos).isAir(); i++) {
            placeLog(level, corner, pos, Direction.Axis.Y, logs);
            pos = pos.below();
        }
    }

    private static void growBranch(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, BlockPos start,
                                   double angle, int length, int topY, int spread, boolean thick, boolean canFork, List<BlockPos> logs) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;
        boolean forked = false;

        for (int i = 1; i <= length; i++) {
            x += dx;
            z += dz;
            float rise = random.nextFloat();
            if (rise >= FLAT_STEP_CHANCE) {
                y += rise < FLAT_STEP_CHANCE + STEEP_STEP_CHANCE ? 2 : 1;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));

            if (y > topY - MIN_TIP || horizontalDistance(centre, next) > spread) {
                break;
            }

            boolean doubled = thick && canFork && i <= length / 2;
            connectBlocks(level, corner, previous, next, doubled, logs);

            if (canFork && !forked && i >= 2 && random.nextFloat() < FORK_CHANCE) {
                forked = true;
                double side = random.nextBoolean() ? 1.0 : -1.0;
                double forkAngle = angle + side * Math.toRadians(randomBetween(random, FORK_MIN_ANGLE_DEG, FORK_MAX_ANGLE_DEG));
                int forkLength = Math.max(2, Math.round((length - i) * FORK_SHRINK_FACTOR));
                growBranch(level, random, corner, centre, next, forkAngle, forkLength, topY, spread, thick, false, logs);
            }
            previous = next;
        }

        int tip = Math.min(randomBetween(random, MIN_TIP, MAX_TIP), topY - previous.getY());
        for (int i = 1; i <= tip; i++) {
            placeLog(level, corner, previous.above(i), Direction.Axis.Y, logs);
        }
    }

    private static void connectBlocks(WorldGenLevel level, BlockPos corner, BlockPos from, BlockPos to, boolean doubled, List<BlockPos> logs) {
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

            BlockPos pos = new BlockPos(currentX, currentY, currentZ);
            placeLog(level, corner, pos, axis, logs);
            if (doubled) {
                placeLog(level, corner, pos.below(), axis, logs);
            }
        }
    }

    private static void placeLeaves(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos log) {
        for (Direction side : Direction.values()) {
            if (random.nextFloat() >= LEAF_CHANCE) {
                continue;
            }
            BlockPos pos = log.relative(side);
            if (isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
                BlockState leaves = WyrmroostBlockRegistry.KALIBURN_LEAVES.get().defaultBlockState()
                        .setValue(BlockKaliburnLeaves.FACING, side);
                level.setBlock(pos, leaves, 2);
            }
        }
    }

    private static void placeLog(WorldGenLevel level, BlockPos corner, BlockPos pos, Direction.Axis axis, List<BlockPos> logs) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.KALIBURN_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.KALIBURN_LOG.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, axis)
                .setValue(BlockKaliburnLog.NATURAL, true);
        level.setBlock(pos, log, 2);
        logs.add(pos);
    }

    private static int distanceToFootprint(BlockPos pos, List<BlockPos> footprint) {
        int best = Integer.MAX_VALUE;
        for (BlockPos foot : footprint) {
            best = Math.min(best, Math.abs(pos.getX() - foot.getX()) + Math.abs(pos.getZ() - foot.getZ()));
        }
        return best;
    }

    private static double horizontalDistance(BlockPos centre, BlockPos pos) {
        int dx = pos.getX() - centre.getX();
        int dz = pos.getZ() - centre.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double angleVariation(RandomSource random, float maxDegrees) {
        return Math.toRadians((random.nextFloat() * 2.0F - 1.0F) * maxDegrees);
    }

    private static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int dx = pos.getX() - corner.getX();
        int dz = pos.getZ() - corner.getZ();
        return dx >= 0 && dx < AREA_SIZE && dz >= 0 && dz < AREA_SIZE;
    }
}