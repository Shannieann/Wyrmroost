package com.java.shannieann.wyrmroost.block.plant.water;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockLarnWeed extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<LarnLeaves> LEAVES = EnumProperty.create("leaves", LarnLeaves.class);
    public static final EnumProperty<LarnSegment> SEGMENT = EnumProperty.create("segment", LarnSegment.class);
    public static final BooleanProperty TREATED = BooleanProperty.create("treated");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);

    // =============
    //  PARAMETERS
    // =============

    //GROWTH
    private static final int MAX_HEIGHT = 12;
    private static final int GROW_CHANCE = 3;

    //STICKINESS
    private static final double STICKY_HORIZONTAL = 0.3D;
    private static final double STICKY_VERTICAL = 0.6D;

    private final Supplier<Item> shoot;
    private final MapCodec<BlockLarnWeed> codec;

    //TODO: Suds
    //TODO: Caustic water
    public BlockLarnWeed(Properties properties, Supplier<Item> shoot) {
        super(properties);
        this.shoot = shoot;
        this.codec = simpleCodec(newProperties -> new BlockLarnWeed(newProperties, shoot));
        this.registerDefaultState(this.stateDefinition.any().setValue(LEAVES, LarnLeaves.NONE).setValue(SEGMENT, LarnSegment.STALK)
                .setValue(TREATED, false).setValue(WATERLOGGED, false));
    }

    @Override
    public MapCodec<BlockLarnWeed> codec() {
        return codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEAVES, SEGMENT, TREATED, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState below = level.getBlockState(belowPos);
        return below.is(this) || below.isFaceSturdy(level, belowPos, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!state.canSurvive(level, pos)) {
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return state.setValue(SEGMENT, segmentFor(state, level, pos));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LEAVES) == LarnLeaves.LARGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROW_CHANCE) != 0) {
            return;
        }
        BlockPos above = pos.above();
        if (!canGrowInto(level.getBlockState(above)) || heightBelow(level, pos) >= MAX_HEIGHT) {
            return;
        }
        grow(level, pos, above);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.FLINT_AND_STEEL) || state.getValue(TREATED)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (!level.isClientSide()) {
            BlockState treated = state.setValue(TREATED, true);
            level.setBlock(pos, treated.setValue(SEGMENT, segmentFor(treated, level, pos)), 3);
            stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (isSticky(state)) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(STICKY_HORIZONTAL, STICKY_VERTICAL, STICKY_HORIZONTAL));
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(shoot.get());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static boolean isSticky(BlockState state) {
        return !state.getValue(TREATED) && !state.getValue(WATERLOGGED);
    }

    public static LarnSegment segmentFor(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(TREATED) || state.getValue(WATERLOGGED)) {
            return LarnSegment.BLEACHED;
        }
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof BlockLarnWeed && below.getValue(WATERLOGGED)) {
            return LarnSegment.TRANSITION;
        }
        return LarnSegment.STALK;
    }

    public static boolean canGrowInto(BlockState state) {
        return state.isAir() || (state.is(Blocks.WATER) && state.getFluidState().isSource());
    }

    private int heightBelow(LevelReader level, BlockPos pos) {
        int height = 1;
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (height < MAX_HEIGHT) {
            cursor.move(Direction.DOWN);
            if (!level.getBlockState(cursor).is(this)) {
                break;
            }
            height++;
        }
        return height;
    }

    private void grow(LevelAccessor level, BlockPos top, BlockPos above) {
        boolean water = level.getFluidState(above).is(FluidTags.WATER);
        BlockState oldTop = level.getBlockState(top);
        BlockPos belowTop = top.below();
        BlockState belowState = level.getBlockState(belowTop);
        if (belowState.is(this) && belowState.getValue(LEAVES) != LarnLeaves.NONE) {
            level.setBlock(belowTop, belowState.setValue(LEAVES, LarnLeaves.NONE), 3);
        }
        level.setBlock(top, oldTop.setValue(LEAVES, LarnLeaves.SMALL), 3);
        BlockState grown = this.defaultBlockState().setValue(LEAVES, LarnLeaves.LARGE).setValue(WATERLOGGED, water);
        level.setBlock(above, grown.setValue(SEGMENT, segmentFor(grown, level, above)), 3);
    }
}
