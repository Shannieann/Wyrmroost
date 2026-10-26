package com.java.shannieann.wyrmroost.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockDragonwoodLog extends RotatedPillarBlock {
    private static final VoxelShape Y_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
    private static final VoxelShape X_SHAPE = Block.box(0.0D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);
    private static final VoxelShape Z_SHAPE = Block.box(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 16.0D);

    public BlockDragonwoodLog(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BambooStalkBlock.LEAVES);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> X_SHAPE;
            case Z -> Z_SHAPE;
            default -> Y_SHAPE;
        };
    }
}
