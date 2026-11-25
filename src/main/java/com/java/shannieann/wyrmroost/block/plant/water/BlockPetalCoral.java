package com.java.shannieann.wyrmroost.block.plant.water;

import java.util.function.Supplier;

import com.java.shannieann.wyrmroost.block.plant.attached.BlockAttachedPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockPetalCoral extends BlockAttachedPlant {
    private static final VoxelShape[] LARGE_SHAPES = makeShapes(0.0D, 16.0D);
    private static final VoxelShape[] DWARF_SHAPES = makeShapes(0.0D, 6.0D);
    private static final VoxelShape PLATFORM = Block.box(0.0D, 11.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    private final Supplier<Block> deadBlock;
    private final boolean platform;
    private final MapCodec<BlockPetalCoral> codec;

    public BlockPetalCoral(Properties properties, Supplier<Block> deadBlock, boolean platform) {
        super(properties);
        this.deadBlock = deadBlock;
        this.platform = platform;
        this.codec = simpleCodec(newProperties -> new BlockPetalCoral(newProperties, deadBlock, platform));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, true));
    }

    @Override
    public MapCodec<BlockPetalCoral> codec() {
        return codec;
    }

    @Override
    protected boolean canFace(Direction facing) {
        return facing.getAxis().isHorizontal();
    }

    @Override
    protected VoxelShape getShapeFor(Direction facing) {
        return platform ? LARGE_SHAPES[facing.get3DDataValue()] : DWARF_SHAPES[facing.get3DDataValue()];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return platform ? PLATFORM : super.getCollisionShape(state, level, pos, context);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (deadBlock != null && !CoralDeath.isWet(state, level, pos)) {
            level.scheduleTick(pos, this, CoralDeath.deathDelay(level.getRandom()));
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (deadBlock != null) {
            CoralDeath.dieIfDry(state, level, pos, deadBlock.get());
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        BlockState updated = super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
        if (updated.is(this) && deadBlock != null && !CoralDeath.isWet(updated, level, pos)) {
            CoralDeath.scheduleCheck(tickAccess, pos, this, random);
        }
        return updated;
    }
}
