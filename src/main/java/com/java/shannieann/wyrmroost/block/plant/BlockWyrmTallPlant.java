package com.java.shannieann.wyrmroost.block.plant;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;


public class BlockWyrmTallPlant extends DoublePlantBlock {
    public static final MapCodec<BlockWyrmTallPlant> CODEC = simpleCodec(BlockWyrmTallPlant::new);

    public BlockWyrmTallPlant(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends BlockWyrmTallPlant> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.SUPPORTS_VEGETATION);
    }
}
