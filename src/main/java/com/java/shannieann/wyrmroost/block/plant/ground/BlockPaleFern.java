package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;


public class BlockPaleFern extends BlockWyrmPlant {
    public static final MapCodec<BlockPaleFern> CODEC = simpleCodec(BlockPaleFern::new);
    public static final BooleanProperty TOUCHED = BooleanProperty.create("touched");

    private static final int RECOVER_TICKS = 40;

    //TODO: Alert dragons
    public BlockPaleFern(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOUCHED, false));
    }

    @Override
    public MapCodec<BlockPaleFern> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOUCHED);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (level.isClientSide() || !(entity instanceof LivingEntity) || state.getValue(TOUCHED)) {
            return;
        }
        level.setBlock(pos, state.setValue(TOUCHED, true), 3);
        level.scheduleTick(pos, this, RECOVER_TICKS);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(TOUCHED)) {
            level.setBlock(pos, state.setValue(TOUCHED, false), 3);
        }
    }
}