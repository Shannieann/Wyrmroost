package com.java.shannieann.wyrmroost.block.plant.water;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.Block;


public class BlockUnglaCoral extends BlockUnderwaterPlant {
    private final MapCodec<BlockUnglaCoral> codec;

    public BlockUnglaCoral(Properties properties, Supplier<Block> deadBlock) {
        super(properties, deadBlock);
        this.codec = simpleCodec(newProperties -> new BlockUnglaCoral(newProperties, deadBlock));
    }

    @Override
    public MapCodec<BlockUnglaCoral> codec() {
        return codec;
    }
}
