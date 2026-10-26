package com.java.shannieann.wyrmroost.world.feature;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureDragonsEye extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final int CHUNKS = 2;
    private static final float TREE_CHANCE = 0.6F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_HEIGHT = 18;
    private static final int MAX_HEIGHT = 24;
    private static final int MIN_BASE_RADIUS = 6;
    private static final int MAX_BASE_RADIUS = 7;
    private static final int TOP_RADIUS = 2;
    private static final double LUMP_SIZE = 1.5;
    private static final double TWIST = 0.15;
    private static final float EDGE_CHANCE = 0.5F;
    private static final int ROOT_DEPTH = 4;

    //BUTTRESSES
    private static final int MIN_BUTTRESSES = 4;
    private static final int MAX_BUTTRESSES = 6;
    private static final int MIN_BUTTRESS_LENGTH = 2;
    private static final int MAX_BUTTRESS_LENGTH = 4;
    private static final int BUTTRESS_HEIGHT = 4;
    private static final float BUTTRESS_ANGLE_RANGE_DEG = 20.0F;

    //DEN
    private static final int DEN_CORE = 5;
    private static final int MIN_DEN_BLOBS = 4;
    private static final int MAX_DEN_BLOBS = 6;
    private static final float MIN_BLOB_RADIUS = 2.5F;
    private static final float MAX_BLOB_RADIUS = 3.5F;
    private static final double DEN_WALL = 2.0;
    private static final int DEN_ROOF = 3;
    private static final int MIN_ENTRANCE_HALF_WIDTH = 2;
    private static final int MAX_ENTRANCE_HALF_WIDTH = 3;
    private static final int MIN_ENTRANCE_HEIGHT = 5;
    private static final int MAX_ENTRANCE_HEIGHT = 6;

    //HOLES
    private static final int MIN_HOLES = 2;
    private static final int MAX_HOLES = 4;
    private static final float MIN_HOLE_RADIUS = 1.0F;
    private static final float MAX_HOLE_RADIUS = 1.8F;
    private static final int HOLE_ENTRANCE_GAP_DEG = 60;

    //BRANCHES
    private static final int MIN_BRANCHES = 3;
    private static final int MAX_BRANCHES = 5;
    private static final int MIN_BRANCH_LENGTH = 4;
    private static final int MAX_BRANCH_LENGTH = 7;
    private static final float BRANCH_ANGLE_RANGE_DEG = 25.0F;
    private static final float BRANCH_RISE_CHANCE = 0.5F;
    private static final int MAX_BRANCH_DROP = 4;

    //LEAVES
    private static final int MIN_LEAF_RADIUS = 2;
    private static final int MAX_LEAF_RADIUS = 3;
    private static final float LEAF_EDGE_CHANCE = 0.5F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureDragonsEye(Codec<NoneFeatureConfiguration> codec) {
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
        BlockPos centre = new BlockPos(centreX, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centreX, centreZ), centreZ);

        if (!level.getBlockState(centre.below()).getFluidState().isEmpty()) {
            return false;
        }

        int height = randomBetween(random, MIN_HEIGHT, MAX_HEIGHT);
        int baseRadius = randomBetween(random, MIN_BASE_RADIUS, MAX_BASE_RADIUS);
        double phaseA = random.nextFloat() * Math.PI * 2.0;
        double phaseB = random.nextFloat() * Math.PI * 2.0;
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        Set<BlockPos> den = new HashSet<>();
        int denTop = carveDen(random, centre, baseRadius, phaseA, phaseB, den);
        carveEntrance(random, centre, baseRadius, facing, den);
        carveHoles(random, centre, baseRadius, phaseA, phaseB, facing, denTop, den);
        int narrowFrom = denTop + DEN_ROOF;

        Set<BlockPos> logs = new HashSet<>();
        placeTrunk(level, random, corner, centre, height, baseRadius, narrowFrom, phaseA, phaseB, den, logs);
        placeButtresses(level, random, corner, centre, baseRadius, phaseA, phaseB, den, logs);

        int topY = centre.getY() + height - 1;
        int branches = randomBetween(random, MIN_BRANCHES, MAX_BRANCHES);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / branches;
        for (int i = 0; i < branches; i++) {
            double angle = rotation + step * i + angleVariation(random, BRANCH_ANGLE_RANGE_DEG);
            int startY = topY - randomBetween(random, 1, MAX_BRANCH_DROP);
            int trunkRadius = (int) Math.round(taperAt(startY - centre.getY(), height, baseRadius, narrowFrom));
            int length = trunkRadius + randomBetween(random, MIN_BRANCH_LENGTH, MAX_BRANCH_LENGTH);
            BlockPos end = growBranch(level, random, corner, centre.atY(startY), angle, length, logs);
            placeLeaves(level, random, corner, end.above(), randomBetween(random, MIN_LEAF_RADIUS, MAX_LEAF_RADIUS));
        }
        placeLeaves(level, random, corner, centre.atY(topY + 1), MAX_LEAF_RADIUS + 1);
        removeLooseLogs(level, centre, logs);
        return true;
    }


    private static int carveDen(RandomSource random, BlockPos centre, int baseRadius, double phaseA, double phaseB, Set<BlockPos> den) {
        int denTop = DEN_CORE - 1;
        int half = DEN_CORE / 2;
        for (int x = -half; x < DEN_CORE - half; x++) {
            for (int y = 0; y < DEN_CORE; y++) {
                for (int z = -half; z < DEN_CORE - half; z++) {
                    den.add(centre.offset(x, y, z));
                }
            }
        }

        int blobs = randomBetween(random, MIN_DEN_BLOBS, MAX_DEN_BLOBS);
        for (int i = 0; i < blobs; i++) {
            double blobX = random.nextFloat() * 4.0 - 2.0;
            double blobY = 2.0 + random.nextFloat() * 3.0;
            double blobZ = random.nextFloat() * 4.0 - 2.0;
            double radiusX = randomRadius(random, MIN_BLOB_RADIUS, MAX_BLOB_RADIUS);
            double radiusY = randomRadius(random, MIN_BLOB_RADIUS, MAX_BLOB_RADIUS);
            double radiusZ = randomRadius(random, MIN_BLOB_RADIUS, MAX_BLOB_RADIUS);
            int reach = (int) Math.ceil(MAX_BLOB_RADIUS) + 2;

            for (int x = -reach; x <= reach; x++) {
                for (int y = 0; y <= (int) Math.ceil(blobY + radiusY); y++) {
                    for (int z = -reach; z <= reach; z++) {
                        double fx = (x + 0.5 - blobX) / radiusX;
                        double fy = (y + 0.5 - blobY) / radiusY;
                        double fz = (z + 0.5 - blobZ) / radiusZ;
                        if (fx * fx + fy * fy + fz * fz > 1.0) {
                            continue;
                        }
                        double limit = surfaceRadius(baseRadius, baseRadius, Math.atan2(z, x), y, phaseA, phaseB) - DEN_WALL;
                        if (Math.sqrt(x * x + z * z) > limit) {
                            continue;
                        }
                        den.add(centre.offset(x, y, z));
                        denTop = Math.max(denTop, y);
                    }
                }
            }
        }
        return denTop;
    }

    private static void carveEntrance(RandomSource random, BlockPos centre, int baseRadius, Direction facing, Set<BlockPos> den) {
        Direction side = facing.getClockWise();
        int halfWidth = randomBetween(random, MIN_ENTRANCE_HALF_WIDTH, MAX_ENTRANCE_HALF_WIDTH);
        int archHeight = randomBetween(random, MIN_ENTRANCE_HEIGHT, MAX_ENTRANCE_HEIGHT);
        int distance = baseRadius + (int) Math.ceil(LUMP_SIZE) + MAX_BUTTRESS_LENGTH + 1;

        for (int across = -halfWidth; across <= halfWidth; across++) {
            double t = (double) across / (halfWidth + 1);
            int doorHeight = (int) Math.round(archHeight * Math.sqrt(1.0 - t * t));
            for (int out = 0; out <= distance; out++) {
                for (int y = 0; y < doorHeight; y++) {
                    den.add(centre.relative(facing, out).relative(side, across).above(y));
                }
            }
        }
    }

    private static void carveHoles(RandomSource random, BlockPos centre, int baseRadius, double phaseA, double phaseB,
                                   Direction facing, int denTop, Set<BlockPos> den) {
        double entranceAngle = Math.atan2(facing.getStepZ(), facing.getStepX());
        int holes = randomBetween(random, MIN_HOLES, MAX_HOLES);

        for (int i = 0; i < holes; i++) {
            double angle = entranceAngle + Math.toRadians(randomBetween(random, HOLE_ENTRANCE_GAP_DEG, 360 - HOLE_ENTRANCE_GAP_DEG));
            int y = randomBetween(random, 1, denTop + 1);
            double distance = surfaceRadius(baseRadius, baseRadius, angle, y, phaseA, phaseB) - 0.5;
            double holeX = Math.cos(angle) * distance;
            double holeZ = Math.sin(angle) * distance;
            double radius = randomRadius(random, MIN_HOLE_RADIUS, MAX_HOLE_RADIUS);
            int reach = (int) Math.ceil(radius);

            for (int x = -reach; x <= reach; x++) {
                for (int dy = -reach; dy <= reach; dy++) {
                    for (int z = -reach; z <= reach; z++) {
                        if (x * x + dy * dy + z * z <= radius * radius && y + dy >= 0) {
                            den.add(centre.offset((int) Math.round(holeX) + x, y + dy, (int) Math.round(holeZ) + z));
                        }
                    }
                }
            }
        }
    }

    private static void placeTrunk(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, int height,
                                   int baseRadius, int narrowFrom, double phaseA, double phaseB, Set<BlockPos> den, Set<BlockPos> logs) {
        for (int y = 0; y < height; y++) {
            double taper = taperAt(y, height, baseRadius, narrowFrom);
            int reach = (int) Math.ceil(taper + LUMP_SIZE) + 1;
            for (int x = -reach; x <= reach; x++) {
                for (int z = -reach; z <= reach; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    double radius = surfaceRadius(taper, baseRadius, Math.atan2(z, x), y, phaseA, phaseB);
                    if (distance > radius || (distance > radius - 0.7 && random.nextFloat() >= EDGE_CHANCE)) {
                        continue;
                    }
                    BlockPos pos = centre.offset(x, y, z);
                    if (den.contains(pos)) {
                        continue;
                    }
                    placeLog(level, corner, pos, Direction.Axis.Y, logs);
                    if (y == 0) {
                        placeRoot(level, corner, pos, logs);
                    }
                }
            }
        }
    }

    private static void placeButtresses(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre,
                                        int baseRadius, double phaseA, double phaseB, Set<BlockPos> den, Set<BlockPos> logs) {
        int buttresses = randomBetween(random, MIN_BUTTRESSES, MAX_BUTTRESSES);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        double step = Math.PI * 2.0 / buttresses;

        for (int i = 0; i < buttresses; i++) {
            double angle = rotation + step * i + angleVariation(random, BUTTRESS_ANGLE_RANGE_DEG);
            int length = randomBetween(random, MIN_BUTTRESS_LENGTH, MAX_BUTTRESS_LENGTH);
            double start = surfaceRadius(baseRadius, baseRadius, angle, 0, phaseA, phaseB) - 1.0;

            for (double t = 0.0; t <= length; t += 0.5) {
                int ridgeHeight = Math.max(1, (int) Math.round(BUTTRESS_HEIGHT * (1.0 - t / (length + 1))));
                int x = (int) Math.round(Math.cos(angle) * (start + t));
                int z = (int) Math.round(Math.sin(angle) * (start + t));
                for (int y = 0; y < ridgeHeight; y++) {
                    BlockPos pos = centre.offset(x, y, z);
                    if (den.contains(pos)) {
                        continue;
                    }
                    placeLog(level, corner, pos, Direction.Axis.Y, logs);
                    if (y == 0) {
                        placeRoot(level, corner, pos, logs);
                    }
                }
            }
        }
    }

    private static double taperAt(int y, int height, int baseRadius, int narrowFrom) {
        if (y <= narrowFrom) {
            return baseRadius;
        }
        double t = (double) (y - narrowFrom) / Math.max(1, height - 1 - narrowFrom);
        return baseRadius - (baseRadius - TOP_RADIUS) * Math.min(1.0, t);
    }

    private static double surfaceRadius(double taper, int baseRadius, double angle, int y, double phaseA, double phaseB) {
        double wave = 0.6 * Math.sin(2.0 * angle + phaseA + y * TWIST) + 0.4 * Math.sin(3.0 * angle + phaseB - y * TWIST);
        return taper + LUMP_SIZE * (taper / baseRadius) * (wave + 1.0) / 2.0;
    }

    private static void placeRoot(WorldGenLevel level, BlockPos corner, BlockPos base, Set<BlockPos> logs) {
        BlockPos pos = base.below();
        for (int i = 0; i < ROOT_DEPTH && level.getBlockState(pos).isAir(); i++) {
            placeLog(level, corner, pos, Direction.Axis.Y, logs);
            pos = pos.below();
        }
    }

    private static void removeLooseLogs(WorldGenLevel level, BlockPos centre, Set<BlockPos> logs) {
        Set<BlockPos> attached = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos log : logs) {
            if (log.getY() <= centre.getY()) {
                attached.add(log);
                queue.add(log);
            }
        }
        while (!queue.isEmpty()) {
            BlockPos log = queue.poll();
            for (Direction side : Direction.values()) {
                BlockPos next = log.relative(side);
                if (logs.contains(next) && attached.add(next)) {
                    queue.add(next);
                }
            }
        }
        for (BlockPos log : logs) {
            if (!attached.contains(log)) {
                level.setBlock(log, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static BlockPos growBranch(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start, double angle, int length,
                                       Set<BlockPos> logs) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            x += dx;
            z += dz;
            if (random.nextFloat() < BRANCH_RISE_CHANCE) {
                y++;
            }
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next, logs);
            previous = next;
        }
        return previous;
    }

    private static void connectBlocks(WorldGenLevel level, BlockPos corner, BlockPos from, BlockPos to, Set<BlockPos> logs) {
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
            placeLog(level, corner, pos.below(), axis, logs);
        }
    }

    private static void placeLeaves(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, int radius) {
        BlockState leaves = WyrmroostBlockRegistry.DRAGONSEYE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int radiusY = radius - 1;

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

    private static void placeLog(WorldGenLevel level, BlockPos corner, BlockPos pos, Direction.Axis axis, Set<BlockPos> logs) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.DRAGONSEYE_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.DRAGONSEYE_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        level.setBlock(pos, log, 2);
        logs.add(pos);
    }

    private static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int dx = pos.getX() - corner.getX();
        int dz = pos.getZ() - corner.getZ();
        return dx >= 0 && dx < AREA_SIZE && dz >= 0 && dz < AREA_SIZE;
    }

    private static double randomRadius(RandomSource random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double angleVariation(RandomSource random, float maxDegrees) {
        return Math.toRadians((random.nextFloat() * 2.0F - 1.0F) * maxDegrees);
    }
}