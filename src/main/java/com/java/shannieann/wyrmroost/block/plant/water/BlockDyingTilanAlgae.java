package com.java.shannieann.wyrmroost.block.plant.water;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;


public class BlockDyingTilanAlgae extends BlockTilanAlgae {
    public static final MapCodec<BlockDyingTilanAlgae> CODEC = simpleCodec(BlockDyingTilanAlgae::new);
    public static final BooleanProperty CRACKING = BooleanProperty.create("cracking");

    //BREAKING
    private static final int BREAK_TICKS = 60;

    public BlockDyingTilanAlgae(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CRACKING, false));
    }

    @Override
    public MapCodec<BlockDyingTilanAlgae> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRACKING);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && !state.getValue(CRACKING)) {
            level.setBlock(pos, state.setValue(CRACKING, true), 3);
            level.scheduleTick(pos, this, BREAK_TICKS);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(CRACKING)) {
            level.destroyBlock(pos, false);
        }
    }
}
