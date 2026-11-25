package com.java.shannieann.wyrmroost.block.plant.water;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public final class PlantShock {
    public static final ResourceKey<DamageType> SHOCK = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "shock"));

    //ToDo: Interact with BFL's lightning
    //KNOCKBACK
    private static final double PUSH_STRENGTH = 0.15D;
    private static final double PUSH_LIFT = 0.05D;

    //PARTICLES
    private static final int SPARKS = 8;
    private static final double SPARK_SPREAD = 0.3D;
    private static final double SPARK_SPEED = 0.05D;

    public static void shock(Level level, BlockPos pos, Entity entity, float damage, boolean push) {
        if (level.isClientSide() || !(entity instanceof LivingEntity)) {
            return;
        }
        entity.hurt(level.damageSources().source(SHOCK), damage);
        if (push) {
            Vec3 away = entity.position().subtract(pos.getX() + 0.5D, entity.getY(), pos.getZ() + 0.5D);
            if (away.lengthSqr() > 1.0E-4D) {
                away = away.normalize();
                entity.push(away.x * PUSH_STRENGTH, PUSH_LIFT, away.z * PUSH_STRENGTH);
            }
        }
        sparks(level, pos);
    }

    public static void sparks(Level level, BlockPos pos) {
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    SPARKS, SPARK_SPREAD, SPARK_SPREAD, SPARK_SPREAD, SPARK_SPEED);
        }
    }
}
