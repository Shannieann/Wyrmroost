package com.java.shannieann.wyrmroost.block.plant.hanging;

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


public class BlockHangingPlant extends GrowingPlantHeadBlock {
    //Like Kelp's tip, but outside water, and upside down...
    private static final VoxelShape SHAPE = Block.box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final double GROW_CHANCE = 0.1D;

    private final Supplier<Block> body;
    private final MapCodec<BlockHangingPlant> codec;

    public BlockHangingPlant(Properties properties, Supplier<Block> body) {
        super(properties, Direction.DOWN, SHAPE, false, GROW_CHANCE);
        this.body = body;
        this.codec = simpleCodec(newProperties -> new BlockHangingPlant(newProperties, body));
    }

    @Override
    public MapCodec<BlockHangingPlant> codec() {
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
        return body.get();
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
