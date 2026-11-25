package com.java.shannieann.wyrmroost.world.feature.plant.patch;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;


public abstract class FeaturePlantPatch extends Feature<NoneFeatureConfiguration> {

    // =============
    //  PARAMETERS
    // =============

    //AREA
    protected static final int AREA_SIZE = 48;

    private final int tries;
    private final int spreadXZ;
    private final int spreadY;

    protected FeaturePlantPatch(Codec<NoneFeatureConfiguration> codec, int tries, int spreadXZ, int spreadY) {
        super(codec);
        this.tries = tries;
        this.spreadXZ = spreadXZ;
        this.spreadY = spreadY;
    }

    // =============
    //  PROCEDURAL GENERATION
    // =============
    protected abstract boolean placeAt(WorldGenLevel level, BlockPos pos, RandomSource random, BlockPos corner);

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        BlockPos corner = new BlockPos(((origin.getX() >> 4) << 4) - 16, 0, ((origin.getZ() >> 4) << 4) - 16);
        int placed = 0;
        for (int i = 0; i < tries; i++) {
            BlockPos pos = origin.offset(randomBetween(random, -spreadXZ, spreadXZ), randomBetween(random, -spreadY, spreadY), randomBetween(random, -spreadXZ, spreadXZ));
            if (pos.getY() <= level.getMinY() || pos.getY() >= level.getMaxY() - 3) {
                continue;
            }
            if (isInsideArea(corner, pos) && placeAt(level, pos, random, corner)) {
                placed++;
            }
        }
        return placed > 0;
    }

    protected static boolean isInsideArea(BlockPos corner, BlockPos pos) {
        int x = pos.getX() - corner.getX();
        int z = pos.getZ() - corner.getZ();
        return x >= 0 && x < AREA_SIZE && z >= 0 && z < AREA_SIZE;
    }

    protected static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
