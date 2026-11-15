package com.java.shannieann.wyrmroost.block;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockOseriVinesPlant extends GrowingPlantBodyBlock {
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    private final Supplier<Block> head;
    private final MapCodec<BlockOseriVinesPlant> codec;

    public BlockOseriVinesPlant(Properties properties, Supplier<Block> head) {
        super(properties, Direction.DOWN, SHAPE, false);
        this.head = head;
        this.codec = simpleCodec(newProperties -> new BlockOseriVinesPlant(newProperties, head));
    }

    @Override
    public MapCodec<BlockOseriVinesPlant> codec() {
        return codec;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) head.get();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.relative(growthDirection.getOpposite());
        BlockState above = level.getBlockState(abovePos);
        if (!canAttachTo(above)) {
            return false;
        }
        return above.is(this) || above.is(getHeadBlock()) || above.is(BlockTags.LEAVES) || above.is(BlockTags.LOGS)
                || above.isFaceSturdy(level, abovePos, growthDirection);
    }
}
