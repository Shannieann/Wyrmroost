package com.java.shannieann.wyrmroost.block.plant.attached;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockCrevasseCotton extends BlockAttachedPlant {
    public static final MapCodec<BlockCrevasseCotton> CODEC = simpleCodec(BlockCrevasseCotton::new);
    private static final VoxelShape[] SHAPES = makeShapes(2.0D, 10.0D);

    // =============
    //  PARAMETERS
    // =============

    //PUFF
    private static final int PUFF_PARTICLES = 6;
    private static final double PUFF_SPREAD = 0.2D;
    private static final double PUFF_SPEED = 0.02D;
    private static final double MIN_PROJECTILE_SPEED = 0.01D;

    public BlockCrevasseCotton(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockCrevasseCotton> codec() {
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

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!(entity instanceof Projectile) || entity.getDeltaMovement().lengthSqr() < MIN_PROJECTILE_SPEED * MIN_PROJECTILE_SPEED) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    PUFF_PARTICLES, PUFF_SPREAD, PUFF_SPREAD, PUFF_SPREAD, PUFF_SPEED);
        }
    }
}