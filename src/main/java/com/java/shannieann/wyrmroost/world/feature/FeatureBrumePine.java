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


public class FeatureBrumePine extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float TREE_CHANCE = 0.3F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_HEIGHT = 44;
    private static final int MAX_HEIGHT = 48;
    private static final float MIN_TRUNK_RADIUS = 4.0F;
    private static final float MAX_TRUNK_RADIUS = 5.5F;
    private static final float TAPER = 0.4F;
    private static final float CORE_SIZE = 0.6F;
    private static final float STRAND_OFFSET = 0.68F;
    private static final float STRAND_THICKNESS = 0.32F;
    private static final int MIN_STRANDS = 2;
    private static final int MAX_STRANDS = 3;
    private static final float MIN_TWIST_SPEED = 0.8F;
    private static final float MAX_TWIST_SPEED = 1.1F;
    private static final float ROOT_FLARE = 0.6F;
    private static final int ROOT_FLARE_HEIGHT = 5;

    //STUBS
    private static final int MIN_STUBS = 6;
    private static final int MAX_STUBS = 10;
    private static final float STUB_START = 0.4F;
    private static final float STUB_END = 0.8F;
    private static final int MIN_STUB_LENGTH = 2;
    private static final int MAX_STUB_LENGTH = 4;

    //HAND
    private static final int MIN_FINGERS = 4;
    private static final int MAX_FINGERS = 5;
    private static final float FINGER_ANGLE_RANGE_DEG = 20.0F;
    private static final int MIN_FINGER_SPREAD = 2;
    private static final int MAX_FINGER_SPREAD = 3;
    private static final int MIN_FINGER_LENGTH = 7;
    private static final int MAX_FINGER_LENGTH = 10;
    private static final float FINGER_LEAN_CHANCE = 0.25F;
    private static final float CLAW_CHANCE = 0.4F;

    //LEAVES
    private static final float FINGER_LEAF_CHANCE = 0.5F;
    private static final int MAX_FINGER_TIP = 2;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureBrumePine(Codec<NoneFeatureConfiguration> codec) {
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
        float trunkRadius = MIN_TRUNK_RADIUS + random.nextFloat() * (MAX_TRUNK_RADIUS - MIN_TRUNK_RADIUS);
        int strands = randomBetween(random, MIN_STRANDS, MAX_STRANDS);
        double twist = MIN_TWIST_SPEED + random.nextFloat() * (MAX_TWIST_SPEED - MIN_TWIST_SPEED);
        if (random.nextBoolean()) {
            twist = -twist;
        }
        double rotation = random.nextFloat() * Math.PI * 2.0;
        for (int y = 0; y < height; y++) {
            double radius = radiusAt(y, height, trunkRadius);
            placeTrunkLayer(level, corner, base.above(y), radius, strands, rotation);
            rotation += twist / radius;
        }
        BlockPos top = base.above(height - 1);

        int stubs = randomBetween(random, MIN_STUBS, MAX_STUBS);
        for (int i = 0; i < stubs; i++) {
            int y = randomBetween(random, Math.round(height * STUB_START), Math.round(height * STUB_END));
            double angle = random.nextFloat() * Math.PI * 2.0;
            double length = radiusAt(y, height, trunkRadius) + randomBetween(random, MIN_STUB_LENGTH, MAX_STUB_LENGTH);
            BlockPos start = base.above(y);
            BlockPos end = start.offset((int) Math.round(Math.cos(angle) * length), -randomBetween(random, 1, 2), (int) Math.round(Math.sin(angle) * length));
            connectBlocks(level, corner, start, end);
        }

        BlockState leaves = WyrmroostBlockRegistry.BRUME_PINE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);

        int fingers = randomBetween(random, MIN_FINGERS, MAX_FINGERS);
        double handRotation = random.nextFloat() * Math.PI * 2.0;
        for (int i = 0; i < fingers; i++) {
            double angle = handRotation + Math.PI * 2.0 / fingers * i + angleVariation(random, FINGER_ANGLE_RANGE_DEG);
            growFinger(level, random, corner, top, angle, leaves);
        }
        return true;
    }


    private static double radiusAt(int y, int height, float trunkRadius) {
        double radius = trunkRadius * (1.0 - TAPER * y / (height - 1));
        if (y < ROOT_FLARE_HEIGHT) {
            radius *= 1.0 + ROOT_FLARE * (ROOT_FLARE_HEIGHT - y) / ROOT_FLARE_HEIGHT;
        }
        return radius;
    }

    private static void placeTrunkLayer(WorldGenLevel level, BlockPos corner, BlockPos centre, double radius, int strands, double angle) {
        BlockState wood = WyrmroostBlockRegistry.BRUME_PINE_WOOD.get().defaultBlockState();
        double core = Math.max(0.5, radius * CORE_SIZE);
        double offset = radius * STRAND_OFFSET;
        double thickness = radius * STRAND_THICKNESS;
        int reach = (int) Math.ceil(radius) + 1;

        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                boolean inside = x * x + z * z <= core * core;
                for (int i = 0; i < strands && !inside; i++) {
                    double strandAngle = angle + Math.PI * 2.0 / strands * i;
                    double sx = x - Math.cos(strandAngle) * offset;
                    double sz = z - Math.sin(strandAngle) * offset;
                    inside = sx * sx + sz * sz <= thickness * thickness;
                }
                BlockPos pos = centre.offset(x, 0, z);
                if (inside && isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
                    level.setBlock(pos, wood, 2);
                }
            }
        }
    }

    private static void growFinger(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos top, double angle, BlockState leaves) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double x = top.getX() + 0.5;
        double z = top.getZ() + 0.5;
        int y = top.getY();
        BlockPos previous = top;
        List<BlockPos> finger = new ArrayList<>();

        int spread = randomBetween(random, MIN_FINGER_SPREAD, MAX_FINGER_SPREAD);
        for (int i = 0; i < spread; i++) {
            x += dx;
            z += dz;
            y++;
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            finger.add(next);
            previous = next;
        }

        int length = randomBetween(random, MIN_FINGER_LENGTH, MAX_FINGER_LENGTH);
        for (int i = 0; i < length; i++) {
            if (random.nextFloat() < FINGER_LEAN_CHANCE) {
                x += dx;
                z += dz;
            }
            y++;
            BlockPos next = new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
            connectBlocks(level, corner, previous, next);
            finger.add(next);
            previous = next;
        }

        BlockPos tip = previous;
        if (random.nextFloat() < CLAW_CHANCE) {
            tip = previous.offset(-(int) Math.round(dx), 1, -(int) Math.round(dz));
            connectBlocks(level, corner, previous, tip);
        }

        for (BlockPos pos : finger) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() < FINGER_LEAF_CHANCE) {
                    placeLeaves(level, corner, pos.relative(direction), leaves);
                }
            }
        }

        int needle = randomBetween(random, 1, MAX_FINGER_TIP);
        for (int i = 1; i <= needle; i++) {
            placeLeaves(level, corner, tip.above(i), leaves);
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
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.BRUME_PINE_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.BRUME_PINE_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
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