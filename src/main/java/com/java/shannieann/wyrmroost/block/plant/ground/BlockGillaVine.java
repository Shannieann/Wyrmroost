package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.lang.Math;


public class BlockGillaVine extends BlockWyrmPlant {
    public static final MapCodec<BlockGillaVine> CODEC = simpleCodec(BlockGillaVine::new);

    //THORNS
    private static final float THORN_DAMAGE = 1.0F;
    private static final double SLOWDOWN = 0.8D;
    private static final double MOVING_THRESHOLD = 0.003D;

    //TODO: Gilla berries
    public BlockGillaVine(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlockGillaVine> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        Vec3 movement = entity.getDeltaMovement();
        entity.setDeltaMovement(movement.multiply(SLOWDOWN, 1.0D, SLOWDOWN));
        if (!level.isClientSide() && (Math.abs(movement.x) > MOVING_THRESHOLD || Math.abs(movement.z) > MOVING_THRESHOLD)) {
            entity.hurt(level.damageSources().cactus(), THORN_DAMAGE);
        }
    }
}
