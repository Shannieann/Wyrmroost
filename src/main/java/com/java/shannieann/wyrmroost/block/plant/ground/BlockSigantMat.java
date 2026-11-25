package com.java.shannieann.wyrmroost.block.plant.ground;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockSigantMat extends Block {
    public static final EnumProperty<MatShape> SHAPE = EnumProperty.create("shape", MatShape.class);
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
    private static final VoxelShape MAT_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    //BOUNCE
    private static final double LIVING_BOUNCE = 0.6D;
    private static final double OTHER_BOUNCE = 0.4D;

    private final float fallDamageFactor;
    private final MapCodec<BlockSigantMat> codec;

    public BlockSigantMat(Properties properties, float fallDamageFactor) {
        super(properties);
        this.fallDamageFactor = fallDamageFactor;
        this.codec = simpleCodec(newProperties -> new BlockSigantMat(newProperties, fallDamageFactor));
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, MatShape.CENTRE).setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<BlockSigantMat> codec() {
        return codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MAT_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = connect(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        if (facing == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (facing.getAxis().isHorizontal()) {
            return connect(state, level, pos);
        }
        return state;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, fallDamageFactor, level.damageSources().fall());
        }
    }

    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(level, entity);
            return;
        }
        Vec3 movement = entity.getDeltaMovement();
        if (movement.y < 0.0D) {
            double bounce = entity instanceof LivingEntity ? LIVING_BOUNCE : OTHER_BOUNCE;
            entity.setDeltaMovement(movement.x, -movement.y * bounce, movement.z);
        }
    }

    public static BlockState connect(BlockState state, BlockGetter level, BlockPos pos) {
        boolean north = !(level.getBlockState(pos.north()).getBlock() instanceof BlockSigantMat);
        boolean east = !(level.getBlockState(pos.east()).getBlock() instanceof BlockSigantMat);
        boolean south = !(level.getBlockState(pos.south()).getBlock() instanceof BlockSigantMat);
        boolean west = !(level.getBlockState(pos.west()).getBlock() instanceof BlockSigantMat);
        int open = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0);
        if (open == 1) {
            Direction side = north ? Direction.NORTH : east ? Direction.EAST : south ? Direction.SOUTH : Direction.WEST;
            return state.setValue(SHAPE, MatShape.EDGE).setValue(FACING, side);
        }
        if (open == 2) {
            if (north && east) {
                return state.setValue(SHAPE, MatShape.CORNER).setValue(FACING, Direction.NORTH);
            }
            if (east && south) {
                return state.setValue(SHAPE, MatShape.CORNER).setValue(FACING, Direction.EAST);
            }
            if (south && west) {
                return state.setValue(SHAPE, MatShape.CORNER).setValue(FACING, Direction.SOUTH);
            }
            if (west && north) {
                return state.setValue(SHAPE, MatShape.CORNER).setValue(FACING, Direction.WEST);
            }
        }
        return state.setValue(SHAPE, MatShape.CENTRE).setValue(FACING, Direction.NORTH);
    }
}
