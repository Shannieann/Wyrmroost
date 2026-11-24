package com.java.shannieann.wyrmroost.world.feature.tree;

import java.util.HashMap;
import java.util.Map;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LeafLitterBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import java.lang.Math;


public class FeatureOseri extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //PLACEMENT
    private static final int SPACING_CHUNKS = 6;
    private static final int ANCHOR_CHUNK = 3;
    private static final int CENTRE_MARGIN = 4;
    private static final int MAX_REACH = 48;
    private static final float TREE_CHANCE = 1.0F;

    //ROOTS
    private static final int MIN_ROOTS = 5;
    private static final int MAX_ROOTS = 8;
    private static final float ROOT_ANGLE_RANGE_DEG = 20.0F;
    private static final int MIN_ROOT_LENGTH = 8;
    private static final int MAX_ROOT_LENGTH = 16;
    private static final float ROOT_RADIUS = 4.0F;
    private static final float ROOT_START_HEIGHT = 6.0F;

    //TRUNK
    private static final int MIN_TRUNK_HEIGHT = 32;
    private static final int MAX_TRUNK_HEIGHT = 44;
    private static final float MIN_BASE_RADIUS = 5.0F;
    private static final float MAX_BASE_RADIUS = 7.0F;
    private static final float TOP_RADIUS = 3.0F;
    private static final float MIN_LEAN = 4.0F;
    private static final float MAX_LEAN = 8.0F;
    private static final float MIN_BEND = 3.0F;
    private static final float MAX_BEND = 6.0F;
    private static final float BEND_ANGLE_RANGE_DEG = 30.0F;
    private static final float MIN_WAVES = 0.75F;
    private static final float MAX_WAVES = 1.25F;
    private static final int STRANDS = 3;
    private static final float CORE_SIZE = 0.55F;
    private static final float STRAND_OFFSET = 0.5F;
    private static final float STRAND_SIZE = 0.5F;
    private static final float TWIST_DEG = 12.5F;

    //LIMBS
    private static final int MIN_LIMBS = 5;
    private static final int MAX_LIMBS = 7;
    private static final float LIMB_START = 0.7F;
    private static final float GOLDEN_ANGLE_DEG = 137.5F;
    private static final float LIMB_ANGLE_RANGE_DEG = 15.0F;
    private static final int MIN_LIMB_LENGTH = 14;
    private static final int MAX_LIMB_LENGTH = 22;
    private static final float LIMB_RISE = 0.8F;
    private static final float LIMB_DROOP = 0.03F;
    private static final float LIMB_WANDER_DEG = 7.0F;
    private static final float LIMB_RADIUS = 3.0F;

    //CANOPY
    private static final float MIN_PAD_RADIUS = 6.0F;
    private static final float MAX_PAD_RADIUS = 9.0F;
    private static final float PAD_FLATTEN = 0.4F;
    private static final int PAD_SPACING = 6;
    private static final float PAD_START = 0.35F;
    private static final float LEAF_EDGE_CHANCE = 0.5F;

    //CASCADES
    private static final float CASCADE_CHANCE = 0.2F;
    private static final int MIN_CASCADE = 6;
    private static final int MAX_CASCADE = 24;
    private static final int CASCADE_JITTER = 4;
    private static final int CASCADE_GROUND_GAP = 3;

    //FLOORING
    private static final float FLOORING_CHANCE = 0.5F;

    // =============
    //  PROCEDURAL GENERATION
    // =============
    public FeatureOseri(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ChunkGenerator generator = context.chunkGenerator();
        RandomState randomState = level.getLevel().getChunkSource().randomState();
        int chunkX = context.origin().getX() >> 4;
        int chunkZ = context.origin().getZ() >> 4;
        int reachChunks = MAX_REACH / 16 + 1;

        boolean placed = false;
        int firstCell = Math.floorDiv(chunkX - reachChunks - ANCHOR_CHUNK, SPACING_CHUNKS);
        int lastCell = Math.floorDiv(chunkX + reachChunks - ANCHOR_CHUNK, SPACING_CHUNKS);
        int firstRow = Math.floorDiv(chunkZ - reachChunks - ANCHOR_CHUNK, SPACING_CHUNKS);
        int lastRow = Math.floorDiv(chunkZ + reachChunks - ANCHOR_CHUNK, SPACING_CHUNKS);
        for (int cell = firstCell; cell <= lastCell; cell++) {
            for (int row = firstRow; row <= lastRow; row++) {
                int anchorChunkX = cell * SPACING_CHUNKS + ANCHOR_CHUNK;
                int anchorChunkZ = row * SPACING_CHUNKS + ANCHOR_CHUNK;
                //TODO: Actual Placement
                if (!TreeRegions.isRegionFor(anchorChunkX, anchorChunkZ, TreeRegions.OSERI)) {
                    continue;
                }
                RandomSource random = RandomSource.create(level.getSeed() ^ (anchorChunkX * 341873128712L + anchorChunkZ * 132897987541L));
                if (random.nextFloat() >= TREE_CHANCE) {
                    continue;
                }
                int x = (anchorChunkX << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
                int z = (anchorChunkZ << 4) + randomBetween(random, CENTRE_MARGIN, 15 - CENTRE_MARGIN);
                if (Math.abs(x - ((chunkX << 4) + 8)) > MAX_REACH + 8 || Math.abs(z - ((chunkZ << 4) + 8)) > MAX_REACH + 8) {
                    continue;
                }

                OseriTree tree = new OseriTree(level, generator, randomState);
                if (!buildTree(tree, random, x, z)) {
                    continue;
                }
                for (Map.Entry<BlockPos, BlockState> entry : tree.blocks.entrySet()) {
                    BlockPos pos = entry.getKey();
                    if (pos.getX() >> 4 != chunkX || pos.getZ() >> 4 != chunkZ) {
                        continue;
                    }
                    BlockState current = level.getBlockState(pos);
                    BlockState state = entry.getValue();
                    if (current.isAir() || (current.is(BlockTags.LEAVES) && !state.is(BlockTags.LEAVES))) {
                        level.setBlock(pos, state, 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }


    private static boolean buildTree(OseriTree tree, RandomSource random, int x, int z) {
        int groundY = tree.groundY(x, z);
        if (!tree.groundBelow(x, groundY, z).getFluidState().isEmpty()) {
            return false;
        }
        BlockPos base = new BlockPos(x, groundY, z);

        Block[] leafColours = {WyrmroostBlockRegistry.OSERI_LEAVES.get(), WyrmroostBlockRegistry.BLUE_OSERI_LEAVES.get(),
                WyrmroostBlockRegistry.GOLDEN_OSERI_LEAVES.get(), WyrmroostBlockRegistry.PINK_OSERI_LEAVES.get(), WyrmroostBlockRegistry.PURPLE_OSERI_LEAVES.get()};
        Block[] vineColours = {WyrmroostBlockRegistry.OSERI_VINES.get(), WyrmroostBlockRegistry.BLUE_OSERI_VINES.get(),
                WyrmroostBlockRegistry.GOLDEN_OSERI_VINES.get(), WyrmroostBlockRegistry.PINK_OSERI_VINES.get(), WyrmroostBlockRegistry.PURPLE_OSERI_VINES.get()};
        Block[] vinePlantColours = {WyrmroostBlockRegistry.OSERI_VINES_PLANT.get(), WyrmroostBlockRegistry.BLUE_OSERI_VINES_PLANT.get(),
                WyrmroostBlockRegistry.GOLDEN_OSERI_VINES_PLANT.get(), WyrmroostBlockRegistry.PINK_OSERI_VINES_PLANT.get(), WyrmroostBlockRegistry.PURPLE_OSERI_VINES_PLANT.get()};
        Block[] flooringColours = {WyrmroostBlockRegistry.OSERI_FLOORING.get(), WyrmroostBlockRegistry.BLUE_OSERI_FLOORING.get(),
                WyrmroostBlockRegistry.GOLDEN_OSERI_FLOORING.get(), WyrmroostBlockRegistry.PINK_OSERI_FLOORING.get(), WyrmroostBlockRegistry.PURPLE_OSERI_FLOORING.get()};
        int colour = random.nextInt(leafColours.length);

        BlockState wood = WyrmroostBlockRegistry.OSERI_WOOD.get().defaultBlockState();
        BlockState leaves = leafColours[colour].defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        GrowingPlantHeadBlock vine = (GrowingPlantHeadBlock) vineColours[colour];
        BlockState vineTip = vine.getMaxAgeState(vine.defaultBlockState());
        BlockState vineBody = vinePlantColours[colour].defaultBlockState();
        Block flooring = flooringColours[colour];

        int height = randomBetween(random, MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
        float baseRadius = MIN_BASE_RADIUS + random.nextFloat() * (MAX_BASE_RADIUS - MIN_BASE_RADIUS);
        double leanAngle = random.nextFloat() * Math.PI * 2.0;
        double bendAngle = leanAngle + Math.PI / 2.0 + angleVariation(random, BEND_ANGLE_RANGE_DEG);
        double lean = MIN_LEAN + random.nextFloat() * (MAX_LEAN - MIN_LEAN);
        double bend = MIN_BEND + random.nextFloat() * (MAX_BEND - MIN_BEND);
        double waves = MIN_WAVES + random.nextFloat() * (MAX_WAVES - MIN_WAVES);
        double wavePhase = random.nextFloat() * Math.PI * 2.0;
        double twist = Math.toRadians(TWIST_DEG) * (random.nextBoolean() ? 1.0 : -1.0);
        double strandPhase = random.nextFloat() * Math.PI * 2.0;

        double[] trunkX = new double[height + 1];
        double[] trunkZ = new double[height + 1];
        double[] trunkRadius = new double[height + 1];
        for (int y = 0; y <= height; y++) {
            double t = (double) y / height;
            double wave = Math.sin(Math.PI * 2.0 * waves * t + wavePhase) - Math.sin(wavePhase);
            trunkX[y] = base.getX() + 0.5 + Math.cos(leanAngle) * lean * t + Math.cos(bendAngle) * bend * wave;
            trunkZ[y] = base.getZ() + 0.5 + Math.sin(leanAngle) * lean * t + Math.sin(bendAngle) * bend * wave;
            trunkRadius[y] = baseRadius + (TOP_RADIUS - baseRadius) * t;
        }

        for (int y = 0; y <= height; y++) {
            double radius = trunkRadius[y];
            int worldY = base.getY() + y;
            placeBall(tree, trunkX[y], worldY, trunkZ[y], radius * CORE_SIZE, wood);
            for (int i = 0; i < STRANDS; i++) {
                double angle = strandPhase + twist * y + Math.PI * 2.0 / STRANDS * i;
                double sx = trunkX[y] + Math.cos(angle) * radius * STRAND_OFFSET;
                double sz = trunkZ[y] + Math.sin(angle) * radius * STRAND_OFFSET;
                placeBall(tree, sx, worldY, sz, radius * STRAND_SIZE, wood);
                connectBlocks(tree, toPos(trunkX[y], worldY, trunkZ[y]), toPos(sx, worldY, sz), wood);
            }
            if (y > 0) {
                connectBlocks(tree, toPos(trunkX[y - 1], worldY - 1, trunkZ[y - 1]), toPos(trunkX[y], worldY, trunkZ[y]), wood);
            }
        }

        int roots = randomBetween(random, MIN_ROOTS, MAX_ROOTS);
        double rootRotation = random.nextFloat() * Math.PI * 2.0;
        for (int i = 0; i < roots; i++) {
            double angle = rootRotation + Math.PI * 2.0 / roots * i + angleVariation(random, ROOT_ANGLE_RANGE_DEG);
            growRoot(tree, random, trunkX[0], base.getY(), trunkZ[0], angle, baseRadius, wood);
        }

        Map<Long, Integer> canopyFloor = new HashMap<>();
        double topX = trunkX[height];
        double topZ = trunkZ[height];
        int topY = base.getY() + height;
        placePad(tree, random, toPos(topX, topY + 1, topZ), MAX_PAD_RADIUS, leaves, canopyFloor);

        int limbs = randomBetween(random, MIN_LIMBS, MAX_LIMBS);
        double limbAngle = random.nextFloat() * Math.PI * 2.0;
        int firstLimb = Math.round(height * LIMB_START);
        for (int i = 0; i < limbs; i++) {
            limbAngle += Math.toRadians(GOLDEN_ANGLE_DEG) + angleVariation(random, LIMB_ANGLE_RANGE_DEG);
            int y = randomBetween(random, firstLimb, height);
            growLimb(tree, random, trunkX[y], base.getY() + y, trunkZ[y], limbAngle, wood, leaves, canopyFloor);
        }

        double furthest = 1.0;
        for (long key : canopyFloor.keySet()) {
            BlockPos column = BlockPos.of(key);
            furthest = Math.max(furthest, Math.hypot(column.getX() - topX, column.getZ() - topZ));
        }
        for (Map.Entry<Long, Integer> entry : canopyFloor.entrySet()) {
            if (random.nextFloat() >= CASCADE_CHANCE) {
                continue;
            }
            BlockPos column = BlockPos.of(entry.getKey());
            double reach = Math.hypot(column.getX() - topX, column.getZ() - topZ) / furthest;
            int length = MIN_CASCADE + (int) Math.round((MAX_CASCADE - MIN_CASCADE) * reach * reach) + random.nextInt(CASCADE_JITTER * 2 + 1) - CASCADE_JITTER;
            BlockPos start = new BlockPos(column.getX(), entry.getValue(), column.getZ());
            int hanging = 0;
            while (hanging < length) {
                BlockPos pos = start.below(hanging + 1);
                if (!tree.isAir(pos) || !tree.isAir(pos.below(CASCADE_GROUND_GAP))) {
                    break;
                }
                hanging++;
            }
            for (int i = 1; i <= hanging; i++) {
                tree.place(start.below(i), i == hanging ? vineTip : vineBody);
            }
        }

        for (Map.Entry<Long, Integer> entry : canopyFloor.entrySet()) {
            if (random.nextFloat() >= FLOORING_CHANCE) {
                continue;
            }
            BlockPos column = BlockPos.of(entry.getKey());
            int floorY = tree.groundY(column.getX(), column.getZ());
            BlockPos pos = new BlockPos(column.getX(), floorY, column.getZ());
            BlockState ground = tree.groundBelow(column.getX(), floorY, column.getZ());
            if (floorY < entry.getValue() && tree.isAir(pos) && !tree.blocks.containsKey(pos.below())
                    && !ground.isAir() && ground.getFluidState().isEmpty()) {
                tree.place(pos, flooring.defaultBlockState()
                        .setValue(LeafLitterBlock.AMOUNT, randomBetween(random, 1, 4))
                        .setValue(LeafLitterBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)));
            }
        }
        return true;
    }

    private static void growRoot(OseriTree tree, RandomSource random, double startX, int groundY, double startZ,
                                 double angle, float trunkRadius, BlockState wood) {
        int length = randomBetween(random, MIN_ROOT_LENGTH, MAX_ROOT_LENGTH) + (int) trunkRadius;
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        BlockPos previous = toPos(startX, groundY, startZ);

        for (int s = 1; s <= length; s++) {
            double t = (double) s / length;
            double x = startX + dx * s;
            double z = startZ + dz * s;
            int y = groundY + (int) Math.round(ROOT_START_HEIGHT * (1.0 - t) * (1.0 - t));
            BlockPos next = toPos(x, y, z);
            connectBlocks(tree, previous, next, wood);
            placeBall(tree, x, y, z, ROOT_RADIUS * (1.0 - t), wood);
            previous = next;
        }
    }

    private static void growLimb(OseriTree tree, RandomSource random, double startX, int startY, double startZ,
                                 double angle, BlockState wood, BlockState leaves, Map<Long, Integer> canopyFloor) {
        int length = randomBetween(random, MIN_LIMB_LENGTH, MAX_LIMB_LENGTH);
        double x = startX;
        double z = startZ;
        BlockPos previous = toPos(x, startY, z);

        for (int s = 1; s <= length; s++) {
            angle += angleVariation(random, LIMB_WANDER_DEG);
            x += Math.cos(angle);
            z += Math.sin(angle);
            int y = startY + (int) Math.round(LIMB_RISE * s - LIMB_DROOP * s * s);
            BlockPos next = toPos(x, y, z);
            connectBlocks(tree, previous, next, wood);
            placeBall(tree, x, y, z, LIMB_RADIUS * (1.0 - (double) s / length), wood);
            if ((s >= length * PAD_START && s % PAD_SPACING == 0) || s == length) {
                float radius = MIN_PAD_RADIUS + random.nextFloat() * (MAX_PAD_RADIUS - MIN_PAD_RADIUS);
                placePad(tree, random, next.above(), radius, leaves, canopyFloor);
            }
            previous = next;
        }
    }

    private static void placePad(OseriTree tree, RandomSource random, BlockPos centre, float radius,
                                 BlockState leaves, Map<Long, Integer> canopyFloor) {
        float radiusY = Math.max(1.0F, radius * PAD_FLATTEN);
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
                    if (tree.place(pos, leaves)) {
                        long key = new BlockPos(pos.getX(), 0, pos.getZ()).asLong();
                        Integer lowest = canopyFloor.get(key);
                        if (lowest == null || pos.getY() < lowest) {
                            canopyFloor.put(key, pos.getY());
                        }
                    }
                }
            }
        }
    }

    private static void placeBall(OseriTree tree, double centreX, int centreY, double centreZ, double radius, BlockState state) {
        BlockPos centre = toPos(centreX, centreY, centreZ);
        tree.place(centre, state);
        int reach = (int) Math.ceil(radius);
        for (int x = -reach; x <= reach; x++) {
            for (int y = -reach; y <= reach; y++) {
                for (int z = -reach; z <= reach; z++) {
                    if (x * x + y * y + z * z <= radius * radius) {
                        tree.place(centre.offset(x, y, z), state);
                    }
                }
            }
        }
    }

    private static void connectBlocks(OseriTree tree, BlockPos from, BlockPos to, BlockState state) {
        int currentX = from.getX();
        int currentY = from.getY();
        int currentZ = from.getZ();
        tree.place(from, state);

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

            tree.place(new BlockPos(currentX, currentY, currentZ), state);
        }
    }

    private static BlockPos toPos(double x, int y, double z) {
        return new BlockPos((int) Math.floor(x), y, (int) Math.floor(z));
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double angleVariation(RandomSource random, float maxDegrees) {
        return Math.toRadians((random.nextFloat() * 2.0F - 1.0F) * maxDegrees);
    }


    private static class OseriTree {
        private final Map<BlockPos, BlockState> blocks = new HashMap<>();
        private final Map<Long, Integer> ground = new HashMap<>();
        private final WorldGenLevel level;
        private final ChunkGenerator generator;
        private final RandomState randomState;

        private OseriTree(WorldGenLevel level, ChunkGenerator generator, RandomState randomState) {
            this.level = level;
            this.generator = generator;
            this.randomState = randomState;
        }

        private int groundY(int x, int z) {
            long key = new BlockPos(x, 0, z).asLong();
            Integer y = ground.get(key);
            if (y == null) {
                y = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, level, randomState);
                ground.put(key, y);
            }
            return y;
        }

        private BlockState groundBelow(int x, int groundY, int z) {
            return generator.getBaseColumn(x, z, level, randomState).getBlock(groundY - 1);
        }

        private boolean isAir(BlockPos pos) {
            return !blocks.containsKey(pos) && pos.getY() >= groundY(pos.getX(), pos.getZ());
        }

        private boolean place(BlockPos pos, BlockState state) {
            BlockState current = blocks.get(pos);
            if (current == null && pos.getY() < groundY(pos.getX(), pos.getZ())) {
                return false;
            }
            if (current != null && !(current.is(BlockTags.LEAVES) && !state.is(BlockTags.LEAVES))) {
                return false;
            }
            blocks.put(pos, state);
            return true;
        }
    }
}