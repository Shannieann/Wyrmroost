package com.java.shannieann.wyrmroost.block.plant.water;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockVarishCoral extends DoublePlantBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    //SHOCK
    private static final float TOUCH_DAMAGE = 2.0F;
    private static final float BREAK_DAMAGE = 5.0F;

    private final Supplier<Block> deadBlock;
    private final MapCodec<BlockVarishCoral> codec;

    public BlockVarishCoral(Properties properties, Supplier<Block> deadBlock) {
        super(properties);
        this.deadBlock = deadBlock;
        this.codec = simpleCodec(newProperties -> new BlockVarishCoral(newProperties, deadBlock));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, deadBlock != null));
    }

    @Override
    public MapCodec<BlockVarishCoral> codec() {
        return codec;
    }

    public boolean isAlive() {
        return deadBlock != null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() + 1 > level.getMaxY()) {
            return null;
        }
        if (isAlive() && (!isFullWater(level, pos) || !isFullWater(level, pos.above()))) {
            return null;
        }
        if (!isAlive() && !level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        return this.defaultBlockState().setValue(WATERLOGGED, isFullWater(level, pos));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, isFullWater(level, pos.above())), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState below = level.getBlockState(belowPos);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return below.getBlock() instanceof BlockVarishCoral && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return below.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        BlockState empty = state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        DoubleBlockHalf half = state.getValue(HALF);
        boolean partnerSide = (half == DoubleBlockHalf.LOWER && facing == Direction.UP) || (half == DoubleBlockHalf.UPPER && facing == Direction.DOWN);
        if (partnerSide && (!(facingState.getBlock() instanceof BlockVarishCoral) || facingState.getValue(HALF) == half)) {
            return empty;
        }
        if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, pos)) {
            return empty;
        }
        if (isAlive() && !CoralDeath.isWet(state, level, pos)) {
            CoralDeath.scheduleCheck(tickAccess, pos, this, random);
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (isAlive() && !CoralDeath.isWet(state, level, pos)) {
            level.scheduleTick(pos, this, CoralDeath.deathDelay(level.getRandom()));
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!isAlive() || CoralDeath.isWet(state, level, pos)) {
            return;
        }
        BlockPos partnerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        BlockState partner = level.getBlockState(partnerPos);
        BlockState dead = deadBlock.get().defaultBlockState();
        level.setBlock(pos, dead.setValue(HALF, state.getValue(HALF)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)), 2);
        if (partner.is(this)) {
            level.setBlock(partnerPos, dead.setValue(HALF, partner.getValue(HALF)).setValue(WATERLOGGED, partner.getValue(WATERLOGGED)), 2);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (isAlive()) {
            PlantShock.shock(level, pos, entity, TOUCH_DAMAGE, true);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (isAlive() && !player.isCreative()) {
            PlantShock.shock(level, pos, player, BREAK_DAMAGE, true);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public static void placeAt(LevelAccessor level, BlockState state, BlockPos pos, int flags) {
        level.setBlock(pos, state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, true), flags);
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, true), flags);
    }

    private static boolean isFullWater(LevelReader level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        return fluid.is(FluidTags.WATER) && fluid.getAmount() == 8;
    }
}
