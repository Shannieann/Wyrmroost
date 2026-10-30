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
    private static final float TREE_CHANCE = 0.25F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_TRUNK_HEIGHT = 10;
    private static final int MAX_TRUNK_HEIGHT = 15;
    private static final float MIN_TRUNK_RADIUS = 2.5F;
    private static final float MAX_TRUNK_RADIUS = 3.5F;
    private static final float TAPER = 0.35F;
    private static final int MIN_FLUTES = 5;
    private static final int MAX_FLUTES = 8;
    private static final float FLUTE_DEPTH = 0.35F;
    private static final float FLUTE_TWIST_DEG = 4.0F;
    private static final float FLARE = 0.6F;
    private static final int FLARE_HEIGHT = 4;

    //LEADERS
    private static final int MIN_LEADERS = 2;
    private static final int MAX_LEADERS = 4;
    private static final float LEADER_ANGLE_RANGE_DEG = 30.0F;
    private static final int MIN_LEADER_LENGTH = 8;
    private static final int MAX_LEADER_LENGTH = 13;
    private static final float MIN_LEADER_LEAN_DEG = 10.0F;
    private static final float MAX_LEADER_LEAN_DEG = 30.0F;
    private static final float LEADER_TWIST_DEG = 20.0F;
    private static final float THICK_LEADER_PART = 0.6F;

    //LIMBS
    private static final int MIN_LIMBS = 2;
    private static final int MAX_LIMBS = 4;
    private static final float LIMB_START = 0.45F;
    private static final float LIMB_END = 0.7F;
    private static final int MIN_LIMB_LENGTH = 5;
    private static final int MAX_LIMB_LENGTH = 9;
    private static final float LIMB_TURN_DEG = 20.0F;
    private static final float LIMB_WANDER_CHANCE = 0.35F;
    private static final float LIMB_DROOP_CHANCE = 0.5F;
    private static final int MAX_LIMB_DROP = 2;
    private static final float THICK_LIMB_PART = 0.5F;
    private static final float DEAD_LIMB_CHANCE = 0.45F;

    //LEAVES
    private static final float MIN_CLUMP_RADIUS = 2.5F;
    private static final float MAX_CLUMP_RADIUS = 4.0F;
    private static final float CLUMP_FLATTEN = 0.6F;
    private static final int MIN_SUB_CLUMPS = 1;
    private static final int MAX_SUB_CLUMPS = 3;
    private static final int LEADER_CLUMP_SPACING = 3;
    private static final float LEADER_CLUMP_CHANCE = 0.6F;
    private static final float LEAF_EDGE_CHANCE = 0.5F;

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
        if (!TreeRegions.isRegionFor(chunkX, chunkZ, TreeRegions.SHIMARIN)) {
            return false;
        }
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

        int height = randomBetween(random, MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
        float trunkRadius = MIN_TRUNK_RADIUS + random.nextFloat() * (MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS);
        int flutes = randomBetween(random, MIN_FLUTES, MAX_FLUTES);
        double flutePhase = random.nextFloat() * Math.PI * 2.0;
        double fluteTwist = Math.toRadians(FLUTE_TWIST_DEG) * (random.nextBoolean() ? 1.0 : -1.0);
        for (int y = 0; y < height; y++) {
            placeTrunkLayer(level, corner, base.above(y), radiusAt(y, height, trunkRadius), flutes, flutePhase + fluteTwist * y);
        }
        BlockPos top = base.above(height - 1);
        double topRadius = radiusAt(height - 1, height, trunkRadius);

        List<BlockPos> clumps = new ArrayList<>();
        int leaders = randomBetween(random, MIN_LEADERS, MAX_LEADERS);
        double rotation = random.nextFloat() * Math.PI * 2.0;
        for (int i = 0; i < leaders; i++) {
            double angle = rotation + Math.PI * 2.0 / leaders * i + angleVariation(random, LEADER_ANGLE_RANGE_DEG);
            BlockPos start = top.offset((int) Math.round(Math.cos(angle) * topRadius * 0.5), 0, (int) Math.round(Math.sin(angle) * topRadius * 0.5));
            growLeader(level, random, corner, start, angle, clumps);
        }

        int limbs = randomBetween(random, MIN_LIMBS, MAX_LIMBS);
        double limbRotation = random.nextFloat() * Math.PI * 2.0;
        for (int i = 0; i < limbs; i++) {
            int y = randomBetween(random, Math.round(height * LIMB_START), Math.round(height * LIMB_END));
            double angle = limbRotation + Math.PI * 2.0 / limbs * i + angleVariation(random, LEADER_ANGLE_RANGE_DEG);
            growLimb(level, random, corner, base.above(y), angle, (int) Math.ceil(radiusAt(y, height, trunkRadius)), clumps);
        }

        BlockState leaves = WyrmroostBlockRegistry.SHIMARIN_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        for (BlockPos clump : clumps) {
            float radius = MIN_CLUMP_RADIUS + random.nextFloat() * (MAX_CLUMP_RADIUS - MIN_CLUMP_RADIUS);
            placeClump(level, random, corner, clump, radius, leaves);
            int subClumps = randomBetween(random, MIN_SUB_CLUMPS, MAX_SUB_CLUMPS);
            for (int i = 0; i < subClumps; i++) {
                int reach = (int) Math.ceil(radius / 2.0);
                BlockPos offset = clump.offset(random.nextInt(reach * 2 + 1) - reach, random.nextInt(3) - 1, random.nextInt(reach * 2 + 1) - reach);
                placeClump(level, random, corner, offset, radius * 0.7F, leaves);
            }
        }
        return true;
    }


    private static double radiusAt(int y, int height, float trunkRadius) {
        double radius = trunkRadius * (1.0 - TAPER * y / (height - 1));
        if (y < FLARE_HEIGHT) {
            radius *= 1.0 + FLARE * (FLARE_HEIGHT - y) / FLARE_HEIGHT;
        }
        return radius;
    }

    private static void placeTrunkLayer(WorldGenLevel level, BlockPos corner, BlockPos centre, double radius, int flutes, double phase) {
        BlockState wood = WyrmroostBlockRegistry.SHIMARIN_WOOD.get().defaultBlockState();
        int reach = (int) Math.ceil(radius) + 1;

        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                double edge = radius * (1.0 - FLUTE_DEPTH * (0.5 + 0.5 * Math.cos(flutes * Math.atan2(z, x) + phase)));
                BlockPos pos = centre.offset(x, 0, z);
                if ((x == 0 && z == 0) || Math.sqrt(x * x + z * z) <= edge) {
                    if (isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, wood, 2);
                    }
                }
            }
        }
    }

    private static void growLeader(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start, double angle, List<BlockPos> clumps) {
        int length = randomBetween(random, MIN_LEADER_LENGTH, MAX_LEADER_LENGTH);
        double lean = Math.tan(Math.toRadians(MIN_LEADER_LEAN_DEG + random.nextFloat() * (MAX_LEADER_LEAN_DEG - MIN_LEADER_LEAN_DEG)));
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            angle += angleVariation(random, LEADER_TWIST_DEG);
            x += Math.cos(angle) * lean;
            z += Math.sin(angle) * lean;
            BlockPos next = new BlockPos((int) Math.floor(x), start.getY() + i, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next, i <= length * THICK_LEADER_PART);
            if (i > length / 3 && i % LEADER_CLUMP_SPACING == 0 && random.nextFloat() < LEADER_CLUMP_CHANCE) {
                clumps.add(next);
            }
            previous = next;
        }
        clumps.add(previous.above());
    }

    private static void growLimb(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start, double angle, int trunkReach,
                                 List<BlockPos> clumps) {
        int length = trunkReach + randomBetween(random, MIN_LIMB_LENGTH, MAX_LIMB_LENGTH);
        boolean dead = random.nextFloat() < DEAD_LIMB_CHANCE;
        double x = start.getX() + 0.5;
        double z = start.getZ() + 0.5;
        int y = start.getY();
        BlockPos previous = start;

        for (int i = 1; i <= length; i++) {
            if (i > trunkReach) {
                angle += angleVariation(random, LIMB_TURN_DEG);
                if (random.nextFloat() < LIMB_WANDER_CHANCE) {
                    y += random.nextFloat() < LIMB_DROOP_CHANCE ? -1 : 1;
                    y = Math.max(y, start.getY() - MAX_LIMB_DROP);
                }
            }
            x += Math.cos(angle);
            z += Math.sin(angle);
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next, i <= trunkReach + (length - trunkReach) * THICK_LIMB_PART);
            if (!dead && i == trunkReach + (length - trunkReach) / 2) {
                clumps.add(next.above());
            }
            previous = next;
        }

        if (dead) {
            connectBlocks(level, corner, previous, previous.above(randomBetween(random, 1, 2)), false);
        } else {
            clumps.add(previous.above());
        }
    }

    private static void placeClump(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos centre, float radius, BlockState leaves) {
        float radiusY = Math.max(1.0F, radius * CLUMP_FLATTEN);
        int reach = (int) Math.ceil(radius);
        int reachY = (int) Math.ceil(radiusY);

        for (int x = -reach; x <= reach; x++) {
            for (int y = -reachY; y <= reachY; y++) {
                for (int z = -reach; z <= reach; z++) {
                    double distance = (x * x + z * z) / (radius * radius) + (y * y) / (radiusY * radiusY);
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

    private static void connectBlocks(WorldGenLevel level, BlockPos corner, BlockPos from, BlockPos to, boolean thick) {
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
            if (thick) {
                placeWood(level, corner, pos);
                for (Direction side : Direction.values()) {
                    if (side.getAxis() != axis) {
                        placeWood(level, corner, pos.relative(side));
                    }
                }
            } else {
                placeLog(level, corner, pos, axis);
            }
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

    private static void placeWood(WorldGenLevel level, BlockPos corner, BlockPos pos) {
        if (!isInsideArea(corner, pos)) {
            return;
        }
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.SHIMARIN_LEAVES.get())) {
            return;
        }
        level.setBlock(pos, WyrmroostBlockRegistry.SHIMARIN_WOOD.get().defaultBlockState(), 2);
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
