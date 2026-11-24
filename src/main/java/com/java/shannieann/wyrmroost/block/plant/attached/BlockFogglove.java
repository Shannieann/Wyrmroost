package com.java.shannieann.wyrmroost.block.plant.attached;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockFogglove extends BlockAttachedPlant {
    public static final MapCodec<BlockFogglove> CODEC = simpleCodec(BlockFogglove::new);
    private static final VoxelShape[] SHAPES = makeShapes(2.0D, 13.0D);

    //TODO: Fog variant, interaction with ice
    public BlockFogglove(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockFogglove> codec() {
        return CODEC;
    }

    @Override
    protected boolean canFace(Direction facing) {
        return true;
    }

    @Override
    protected VoxelShape getShapeFor(Direction facing) {
        return SHAPES[facing.get3DDataValue()];
    }
}
