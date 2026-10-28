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


public class FeatureHylanSedge extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final float TREE_CHANCE = 0.25F;
    private static final int CENTRE_MARGIN = 4;
    private static final int AREA_SIZE = 48;

    //TRUNK
    private static final int MIN_HEIGHT = 4;
    private static final int MAX_HEIGHT = 7;
    private static final int MIN_LEAN = 3;
    private static final int MAX_LEAN = 5;
    private static final int STUMP_HEIGHT = 2;
    private static final float EARLY_LEAN = 0.5F;

    //BRANCHES
    private static final int MIN_BRANCHES = 1;
    private static final int MAX_BRANCHES = 3;
    private static final int MIN_BRANCH_LENGTH = 2;
    private static final int MAX_BRANCH_LENGTH = 4;
    private static final float BRANCH_ANGLE_RANGE_DEG = 35.0F;

    //CANOPY
    private static final float MIN_CANOPY_LENGTH = 4.5F;
    private static final float MAX_CANOPY_LENGTH = 6.0F;
    private static final float CANOPY_WIDTH = 3.0F;
    private static final float CANOPY_DEPTH = 2.0F;
    private static final float LEAF_EDGE_CHANCE = 0.7F;
    private static final int TUFT_RADIUS = 1;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureHylanSedge(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        //TODO: Actual Placement, only on the highest level of the Shattered Plains
        if (!TreeRegions.isRegionFor(chunkX, chunkZ, TreeRegions.HYLAN_SEDGE)) {
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

        double wind = random.nextFloat() * Math.PI * 2.0;
        double windX = Math.cos(wind);
        double windZ = Math.sin(wind);
        int height = randomBetween(random, MIN_HEIGHT, MAX_HEIGHT);
        int lean = randomBetween(random, MIN_LEAN, MAX_LEAN);

        BlockPos previous = base;
        placeLog(level, corner, base, Direction.Axis.Y);
        for (int y = 1; y <= height; y++) {
            double bend = bendAt(y, height, lean);
            BlockPos next = base.offset((int) Math.round(windX * bend), y, (int) Math.round(windZ * bend));
            connectBlocks(level, corner, previous, next);
            previous = next;
        }
        BlockPos top = previous;

        BlockPos upwind = base.offset(-(int) Math.round(windX), 0, -(int) Math.round(windZ));
        for (int y = 0; y < STUMP_HEIGHT; y++) {
            placeLog(level, corner, upwind.above(y), Direction.Axis.Y);
        }

        int branches = randomBetween(random, MIN_BRANCHES, MAX_BRANCHES);
        for (int i = 0; i < branches; i++) {
            int startY = randomBetween(random, height / 2, height - 1);
            double bend = bendAt(startY, height, lean);
            BlockPos start = base.offset((int) Math.round(windX * bend), startY, (int) Math.round(windZ * bend));
            double angle = wind + angleVariation(random, BRANCH_ANGLE_RANGE_DEG);
            int length = randomBetween(random, MIN_BRANCH_LENGTH, MAX_BRANCH_LENGTH);
            BlockPos end = start.offset((int) Math.round(Math.cos(angle) * length), random.nextInt(2), (int) Math.round(Math.sin(angle) * length));
            connectBlocks(level, corner, start, end);
            placeCanopy(level, random, corner, end, windX, windZ, TUFT_RADIUS + 1.0F, TUFT_RADIUS + 0.5F, TUFT_RADIUS);
        }

        float length = MIN_CANOPY_LENGTH + random.nextFloat() * (MAX_CANOPY_LENGTH - MIN_CANOPY_LENGTH);
        placeCanopy(level, random, corner, top, windX, windZ, length, CANOPY_WIDTH, CANOPY_DEPTH);
        return true;
    }


    private static double bendAt(int y, int height, int lean) {
        double t = (double) y / height;
        return lean * (EARLY_LEAN * t + (1.0 - EARLY_LEAN) * t * t);
    }

    private static void placeCanopy(WorldGenLevel level, RandomSource random, BlockPos corner, BlockPos start,
                                    double windX, double windZ, float length, float width, float depth) {
        BlockState leaves = WyrmroostBlockRegistry.HYLAN_SEDGE_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        int reach = (int) Math.ceil(length) + 1;
        int reachY = (int) Math.ceil(depth) + 1;

        for (int x = -reach; x <= reach; x++) {
            for (int y = -reachY; y <= reachY; y++) {
                for (int z = -reach; z <= reach; z++) {
                    double along = x * windX + z * windZ;
                    double across = -x * windZ + z * windX;
                    if (along < -1.0) {
                        continue;
                    }
                    double fa = (along - length / 2.0) / (length / 2.0 + 1.0);
                    double fw = across / width;
                    double fy = (y - along * 0.2) / depth;
                    double distance = fa * fa + fw * fw + fy * fy;
                    if (distance > 1.2 || (distance > 0.8 && random.nextFloat() >= LEAF_EDGE_CHANCE)) {
                        continue;
                    }
                    BlockPos pos = start.offset(x, y, z);
                    if (isInsideArea(corner, pos) && level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, leaves, 2);
                    }
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
        if (!current.isAir() && !current.is(WyrmroostBlockRegistry.HYLAN_SEDGE_LEAVES.get())) {
            return;
        }
        BlockState log = WyrmroostBlockRegistry.HYLAN_SEDGE_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
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