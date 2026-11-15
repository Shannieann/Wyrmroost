package com.java.shannieann.wyrmroost.block;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockOseriVines extends GrowingPlantHeadBlock {
    private static final VoxelShape SHAPE = Block.box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final double GROW_CHANCE = 0.1D;

    private final Supplier<Block> plant;
    private final MapCodec<BlockOseriVines> codec;

    public BlockOseriVines(Properties properties, Supplier<Block> plant) {
        super(properties, Direction.DOWN, SHAPE, false, GROW_CHANCE);
        this.plant = plant;
        this.codec = simpleCodec(newProperties -> new BlockOseriVines(newProperties, plant));
    }

    @Override
    public MapCodec<BlockOseriVines> codec() {
        return codec;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }

    @Override
    protected Block getBodyBlock() {
        return plant.get();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.relative(growthDirection.getOpposite());
        BlockState above = level.getBlockState(abovePos);
        if (!canAttachTo(above)) {
            return false;
        }
        return above.is(this) || above.is(getBodyBlock()) || above.is(BlockTags.LEAVES) || above.is(BlockTags.LOGS)
                || above.isFaceSturdy(level, abovePos, growthDirection);
    }
}
