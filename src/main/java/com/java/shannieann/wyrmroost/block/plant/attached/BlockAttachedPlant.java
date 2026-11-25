package com.java.shannieann.wyrmroost.block.plant.attached;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public abstract class BlockAttachedPlant extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected BlockAttachedPlant(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
    }

    protected abstract boolean canFace(Direction facing);

    protected abstract VoxelShape getShapeFor(Direction facing);

    protected boolean needsWater() {
        return false;
    }

    protected boolean canAttachTo(BlockState support, LevelReader level, BlockPos supportPos, Direction facing) {
        return support.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShapeFor(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean water = level.getFluidState(pos).getType() == Fluids.WATER;
        BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATERLOGGED, water);
        if (state.canSurvive(level, pos)) {
            return state;
        }
        for (Direction direction : context.getNearestLookingDirections()) {
            state = state.setValue(FACING, direction.getOpposite());
            if (state.canSurvive(level, pos)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        if (!canFace(facing)) {
            return false;
        }
        if (needsWater() && !state.getValue(WATERLOGGED)) {
            return false;
        }
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return canAttachTo(level.getBlockState(supportPos), level, supportPos, facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!state.canSurvive(level, pos)) {
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected static VoxelShape[] makeShapes(double inset, double depth) {
        VoxelShape[] shapes = new VoxelShape[6];
        double high = 16.0D - inset;
        shapes[Direction.UP.get3DDataValue()] = Block.box(inset, 0.0D, inset, high, depth, high);
        shapes[Direction.DOWN.get3DDataValue()] = Block.box(inset, 16.0D - depth, inset, high, 16.0D, high);
        shapes[Direction.NORTH.get3DDataValue()] = Block.box(inset, inset, 16.0D - depth, high, high, 16.0D);
        shapes[Direction.SOUTH.get3DDataValue()] = Block.box(inset, inset, 0.0D, high, high, depth);
        shapes[Direction.WEST.get3DDataValue()] = Block.box(16.0D - depth, inset, inset, 16.0D, high, high);
        shapes[Direction.EAST.get3DDataValue()] = Block.box(0.0D, inset, inset, depth, high, high);
        return shapes;
    }
}
