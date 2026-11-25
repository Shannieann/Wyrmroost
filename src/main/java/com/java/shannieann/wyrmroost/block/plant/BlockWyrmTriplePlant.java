package com.java.shannieann.wyrmroost.block.plant;

import com.java.shannieann.wyrmroost.block.plant.ground.TriplePart;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockWyrmTriplePlant extends VegetationBlock {
    public static final MapCodec<BlockWyrmTriplePlant> CODEC = simpleCodec(BlockWyrmTriplePlant::new);
    public static final EnumProperty<TriplePart>       PART  = EnumProperty.create("part", TriplePart.class);
    private static final VoxelShape                    SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public BlockWyrmTriplePlant(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, TriplePart.LOWER));
    }

    @Override
    public MapCodec<? extends BlockWyrmTriplePlant> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.SUPPORTS_VEGETATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() + 2 > level.getMaxY()) {
            return null;
        }
        if (!level.getBlockState(pos.above()).canBeReplaced(context) || !level.getBlockState(pos.above(2)).canBeReplaced(context)) {
            return null;
        }
        return this.defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.defaultBlockState().setValue(PART, TriplePart.MIDDLE), 3);
        level.setBlock(pos.above(2), this.defaultBlockState().setValue(PART, TriplePart.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        TriplePart part = state.getValue(PART);
        if (part == TriplePart.LOWER) {
            return super.canSurvive(state, level, pos);
        }
        BlockState below = level.getBlockState(pos.below());
        if (!below.is(this)) {
            return false;
        }
        TriplePart belowPart = below.getValue(PART);
        return part == TriplePart.MIDDLE ? belowPart == TriplePart.LOWER : belowPart == TriplePart.MIDDLE;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        TriplePart part = state.getValue(PART);
        if (facing == Direction.UP && part != TriplePart.UPPER) {
            TriplePart expected = part == TriplePart.LOWER ? TriplePart.MIDDLE : TriplePart.UPPER;
            if (!facingState.is(this) || facingState.getValue(PART) != expected) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        if (facing == Direction.DOWN && !this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    public static void placeAt(LevelAccessor level, BlockState state, BlockPos pos, int flags) {
        level.setBlock(pos, state.setValue(PART, TriplePart.LOWER), flags);
        level.setBlock(pos.above(), state.setValue(PART, TriplePart.MIDDLE), flags);
        level.setBlock(pos.above(2), state.setValue(PART, TriplePart.UPPER), flags);
    }
}
