package com.java.shannieann.wyrmroost.block.plant.water;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockTilanAlgae extends BlockWyrmPlant {
    public static final MapCodec<BlockTilanAlgae> CODEC = simpleCodec(BlockTilanAlgae::new);
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    public BlockTilanAlgae(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends BlockTilanAlgae> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    //TODO: Caustic water instead of water
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        FluidState below = level.getFluidState(pos);
        FluidState here = level.getFluidState(pos.above());
        return below.is(FluidTags.WATER) && below.isSource() && here.getType() == Fluids.EMPTY;
    }
}
