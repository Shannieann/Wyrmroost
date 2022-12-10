package com.github.shannieann.wyrmroost.entities.effect;

import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class EffectLightningSphere extends Entity implements IEntityAdditionalSpawnData, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public WRDragonEntity source;
    public float duration;
    public float existenceTicks;

    public EffectLightningSphere(EntityType<?> type, Level level) {
        super(type, level);
        this.duration = 100;
    }

    public EffectLightningSphere(EntityType<?> type, Level level, float duration) {
        super(type, level);
        this.duration = duration;
    }

    @Override
    public void tick() {
        //If the LightningBall has exceeded its duration, discard it
        this.existenceTicks = tickCount;
        if ((!level.isClientSide && (tickCount > duration))) {
            //Remove the projectile...
            this.discard();
            return;
        }

        super.tick();

        //Check for collisions with other entities:
            // Define a bounding box that will get inflated every tick....
        //this.setBoundingBox(getBoundingBox().inflate(0.05*tickCount));
        AABB boundingBox = this.getBoundingBox();
        //If any of the entities that meet the canImpactEntity condition are within the bounding box...
        List<Entity> entities = level.getEntities(this, boundingBox, this::canImpactEntity);
        if (!entities.isEmpty()) {
        //Proceed to register an impact...
                impact(entities);
        }
    }

    public boolean canImpactEntity(Entity entity) {
        if (entity instanceof ButterflyLeviathanEntity) {
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
        return source != null && !entity.isAlliedTo(source);
    }

    public void impact(List<Entity> entities) {
        if (!entities.isEmpty()) {
            for (int i = 0; i< entities.size(); i++){
                Entity testEntity = entities.get(i);
                testEntity.hurt(DamageSource.LIGHTNING_BOLT,20);
                ((LivingEntity) testEntity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,3,true,true));
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
        buffer.writeInt(source.getId());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.source = (WRDragonEntity) level.getEntity(buffer.readInt());
    }

    public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("rotate", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
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


