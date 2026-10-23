package com.java.shannieann.wyrmroost.world;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FeatureSalBush implements Feature {
    public static final FeatureSalBush INSTANCE = new FeatureSalBush();
    public static final MapCodec<FeatureSalBush> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        level.setBlock(origin, Blocks.IRON_BLOCK.defaultBlockState(), 2);
        return true;
    }
}