package com.github.shannieann.wyrmroost.entity.effect;

import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class EffectLightningNova extends Entity implements IEntityAdditionalSpawnData, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public float duration;
    public float existenceTicks;
    List<Entity> struckEntities = new ArrayList<Entity>();


    public EffectLightningNova(EntityType<?> type, Level level) {
        this(type, level, 100);
    }

    public EffectLightningNova(EntityType<?> type, Level level, float duration) {
        super(type, level);
        this.duration = duration;
        this.noCulling = true;
    }

    @Override
    public void tick() {
        //If the LightningBall has exceeded its duration, discard it
        this.existenceTicks = tickCount;
        //ToDo: Disabled discard for debugging
        /*
        if ((!level.isClientSide && (tickCount > duration))) {
            //Remove the projectile...
            this.discard();
            return;
        }

         */

        super.tick();

        //ToDo: Disabled inflate for debugging

        /*
        //Check for collisions with other entities:
            // Define a bounding box that will get inflated every tick....
        //this.setBoundingBox(this.getBoundingBox().inflate(0.05*tickCount));
        AABB boundingBox = this.getBoundingBox().inflate(3*tickCount);
        //If any of the entities that meet the canImpactEntity condition are within the bounding box...
        List<Entity> entities = level.getEntities(this, boundingBox, this::canImpactEntity);
        if (!entities.isEmpty()) {
        //Proceed to register an impact...
            impact(entities);
        }

         */
    }

    public boolean canImpactEntity(Entity entity) {
        if (entity instanceof EntityButterflyLeviathan) {
            return false;
        }
        if (!entity.isAlive()) {
            return false;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        if (entity.isSpectator() || !entity.isPickable() || entity.noPhysics) {
            return false;
        }
        if (struckEntities.contains(entity)) {
            return false;
        }
        return true;
    }

    public void impact(List<Entity> entities) {
        if (!entities.isEmpty()) {
            for (int i = 0; i< entities.size(); i++){
                Entity testEntity = entities.get(i);
                Vec3 posToEntity = (testEntity.position().subtract(this.position())).normalize();
                testEntity.hurt(DamageSource.LIGHTNING_BOLT,20);
                //KnockBack!
                testEntity.setDeltaMovement(testEntity.getDeltaMovement().add(posToEntity.scale(10)));
                testEntity.setDeltaMovement(getDeltaMovement().add(0,1.5,0));
                ((LivingEntity) testEntity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,3,true,true));
                struckEntities.add(testEntity);
            }
         }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = getBoundingBox().getSize() * 4;
        if (Double.isNaN(d0)) d0 = 4;
        d0 *= 64;
        return distance < d0 * d0;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return getBbWidth();
    }

    @Override
    protected void defineSynchedData() {
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
    }

    public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("rotate", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}


