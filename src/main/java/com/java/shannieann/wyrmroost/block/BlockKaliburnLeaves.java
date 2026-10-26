package com.java.shannieann.wyrmroost.block;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockKaliburnLeaves extends Block {
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
    private static final TagKey<Block> KALIBURN_LOGS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "kaliburn_logs"));
    private static final VoxelShape UP_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
    private static final VoxelShape DOWN_SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape NORTH_SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 14.0D);
    private static final VoxelShape WEST_SHAPE = Block.box(2.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(0.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public BlockKaliburnLeaves(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> UP_SHAPE;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).is(KALIBURN_LOGS);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
}
