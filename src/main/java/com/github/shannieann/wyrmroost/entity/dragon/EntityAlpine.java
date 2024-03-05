package com.github.shannieann.wyrmroost.entity.dragon;

/*import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.model.entity.AlpineModel;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.DragonBreedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.FlyerWanderGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.MoveToHomeGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.WRFollowOwnerGoal;
import com.github.shannieann.wyrmroost.entities.projectile.WindGustEntity;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.network.packets.AnimationPacket;
import com.github.shannieann.wyrmroost.network.packets.KeybindHandler;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.github.shannieann.wyrmroost.util.animation.Animation;
import com.github.shannieann.wyrmroost.util.animation.LogicalAnimation;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeSupplier;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.InteractionHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Level;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import javax.annotation.Nullable;

import static net.minecraft.entity.ai.attributes.Attributes.*;

public class AlpineEntity extends WRDragonEntity
{
    public static final EntitySerializer<AlpineEntity> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.BOOL, "Sleeping", WRDragonEntity::getSleeping, WRDragonEntity::setSleeping)
            .track(EntitySerializer.INT, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant));

    public static final Animation ROAR_ANIMATION = LogicalAnimation.create(84, AlpineEntity::roarAnimation, () -> AlpineModel::roarAnimation);
    public static final Animation WIND_GUST_ANIMATION = LogicalAnimation.create(25, AlpineEntity::windGustAnimation, () -> AlpineModel::windGustAnimation);
    public static final Animation BITE_ANIMATION = LogicalAnimation.create(10, null, () -> AlpineModel::biteAnimation);
    public static final Animation[] ANIMATIONS = new Animation[]{ROAR_ANIMATION, WIND_GUST_ANIMATION, BITE_ANIMATION};

    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public final LerpedFloat flightTimer = LerpedFloat.unit();

    public AlpineEntity(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1, 0.01f));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 10));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        targetSelector.addGoal(1, new NonTameRandomTargetGoal<>(this, BeeEntity.class, false, e -> ((BeeEntity) e).hasNectar()));
    }

    @Override
    public EntitySerializer<? extends WRDragonEntity> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(FLYING, false);
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, 0);
    }

    @Override
    public boolean speciesCanFly(){
        return true;
    }
    @Override
    public void aiStep()
    {
        super.aiStep();

        sitTimer.add(isInSittingPose() || getSleeping()? 0.1f : -0.1f);
        sleepTimer.add(getSleeping()? 0.1f : -0.1f);
        flightTimer.add(isUsingFlyingNavigator()? 0.1f : -0.05f);

        if (!level.isClientSide && noAnimations() && !getSleeping() && isJuvenile() && getRandom().nextDouble() < 0.0005)
            AnimationPacket.send(this, ROAR_ANIMATION);
    }

    public void roarAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_ALPINE_ROAR.get(), 3f, 1f);
        else if (time == 25)
        {
            for (LivingEntity entity : getEntitiesNearby(20, e -> e.getType() == WREntityTypes.ALPINE.get()))
            {
                AlpineEntity alpine = ((AlpineEntity) entity);
                if (alpine.noAnimations() && alpine.isIdling() && !alpine.getSleeping())
                    alpine.setAnimation(ROAR_ANIMATION);
            }
        }
    }

    public void windGustAnimation(int time)
    {
        if (time == 0) setDeltaMovement(getDeltaMovement().add(0, -0.35, 0));
        if (time == 4)
        {
            if (!level.isClientSide) level.addFreshEntity(new WindGustEntity(this));
            setDeltaMovement(getDeltaMovement().add(getLookAngle().reverse().multiply(1.5, 0, 1.5).add(0, 1, 0)));
            playSound(WRSounds.WING_FLAP.get(), 3, 1f, true);
        }
    }

    @Override
    public boolean doHurtTarget(Entity enemy)
    {
        boolean flag = super.doHurtTarget(enemy);

        if (!isTame() && flag && !enemy.isAlive() && enemy.getType() == EntityType.BEE)
        {
            BeeEntity bee = (BeeEntity) enemy;
            if (bee.hasNectar() && bee.isLeashed())
            {
                Entity holder = bee.getLeashHolder();
                if (holder instanceof Player) tame(true, (Player) holder);
            }
        }
        return flag;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        Entity attacker = source.getDirectEntity();
        if (attacker != null && attacker.getType() == EntityType.BEE)
        {
            setTarget((BeeEntity) attacker);
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        return size.scale(1, isInSittingPose() || getSleeping()? 0.7f : 1);
    }

    @Override
    public float getScale()
    {
        return getAgeScale(0.2f);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindHandler.ALT_MOUNT_KEY && pressed && noAnimations() && isUsingFlyingNavigator())
            setAnimation(WIND_GUST_ANIMATION);
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getInfo().move(ClientEvents.getViewCollision(-5d, this), 0.75d, 0);
        else
            event.getInfo().move(ClientEvents.getViewCollision(-3, this), 0.3, 0);
    }

    @Override
    protected void jumpFromGround()
    {
        super.jumpFromGround();
        if (!level.isClientSide)
            level.addFreshEntity(new WindGustEntity(this, position().add(0, 7, 0), calculateViewVector(90, yRot)));
    }

    @Override
    protected float getJumpPower()
    {
        if (dragonCanFly()) return (getBbHeight() * getBlockJumpFactor());
        else return super.getJumpPower();
    }

    @Override
    public void swing(InteractionHand hand)
    {
        setAnimation(BITE_ANIMATION);
        playSound(SoundEvents.GENERIC_EAT, 1, 1, true);
        super.swing(hand);
    }

    @Override
    public int determineVariant()
    {
        return getRandom().nextInt(6);
    }

    @Override
    protected boolean canAddPassenger(Entity entity)
    {
        return isJuvenile() && entity instanceof LivingEntity && isOwnedBy((LivingEntity) entity);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return ModUtils.contains(stack.getItem(), Items.HONEYCOMB, Items.HONEY_BOTTLE);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn)
    {
        return sizeIn.height * (isUsingFlyingNavigator()? 0.8f : 1.25f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_ALPINE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_ALPINE_ROAR.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_ALPINE_DEATH.get();
    }

    @Override
    public Animation[] getAnimations()
    {
        return ANIMATIONS;
    }

    public static AttributeSupplier.MutableAttribute getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 40)
                .add(MOVEMENT_SPEED, 0.22)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, 3)
                .add(FLYING_SPEED, 0.185f)
                .add(WREntityTypes.Attributes.PROJECTILE_DAMAGE.get(), 1);
    }
}
*/