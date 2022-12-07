package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.ai.AnimatedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.shannieann.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.DragonArmorItem;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.network.packets.KeybindHandler;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.Mafs;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


public class RoyalRedEntity extends TameableDragonEntity
{

    public static final EntityDataAccessor<Boolean> BREATHING_FIRE = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> KNOCKED_OUT = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntitySerializer<RoyalRedEntity> SERIALIZER = TameableDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.BOOL, "Gender", TameableDragonEntity::isMale, TameableDragonEntity::setGender)
            .track(EntitySerializer.INT, "Variant", TameableDragonEntity::getVariant, TameableDragonEntity::setVariant)
            .track(EntitySerializer.BOOL, "Sleeping", TameableDragonEntity::isSleeping, TameableDragonEntity::setSleeping)
            .track(EntitySerializer.INT, "KnockOutTime", RoyalRedEntity::getKnockOutTime, RoyalRedEntity::setKnockoutTime));

    public static final int ARMOR_SLOT = 0;
    private static final int MAX_KNOCKOUT_TIME = 3600; // 3 minutes

    public final LerpedFloat flightTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public final LerpedFloat breathTimer = LerpedFloat.unit();
    public final LerpedFloat knockOutTimer = LerpedFloat.unit();
    private int knockOutTime = 0;

    public RoyalRedEntity(EntityType<? extends TameableDragonEntity> type, Level worldIn)
    {
        super(type, worldIn);
        noCulling = WRConfig.NO_CULLING.get();

        //TODO: Double call?
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();

        entityData.define(GENDER, false);
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, 0);
        entityData.define(BREATHING_FIRE, false);
        entityData.define(KNOCKED_OUT, false);
        entityData.define(FLYING, false);
        entityData.define(ARMOR, ItemStack.EMPTY);
    }

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        // base male attributes
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 130)
                .add(MOVEMENT_SPEED, 0.22)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(FOLLOW_RANGE, 60)
                .add(ATTACK_KNOCKBACK, 4)
                .add(ATTACK_DAMAGE, 12)
                .add(FLYING_SPEED, 0.121)
                .add(WREntityTypes.Attributes.PROJECTILE_DAMAGE.get(), 4);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new RRAttackGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e -> e.getType() == EntityType.PLAYER || e instanceof Animal));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        flightTimer.add(isFlying()? 0.1f : -0.085f);
        sitTimer.add(isInSittingPose()? 0.075f : -0.1f);
        sleepTimer.add(isSleeping()? 0.035f : -0.05f);
        breathTimer.add(isBreathingFire()? 0.15f : -0.2f);
        knockOutTimer.add(isKnockedOut()? 0.05f : -0.1f);

        if (!level.isClientSide)
        {
            if (isBreathingFire() && getControllingPlayer() == null && getTarget() == null)
                setBreathingFire(false);

            if (breathTimer.get() == 1) level.addFreshEntity(new FireBreathEntity(this));

            //if (noAnimations() && !isKnockedOut() && !isSleeping() && !isBreathingFire() && isJuvenile() && getRandom().nextDouble() < 0.0004)
            //AnimationPacket.send(this, ROAR_ANIMATION);

            if (isKnockedOut() && --knockOutTime <= 0) setKnockedOut(false);
        }
    }

    //TODO: LOCATE
    @Override
    public EntitySerializer<RoyalRedEntity> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        if (!isTame() && isFood(stack))
        {
            if (isHatchling() || player.isCreative())
            {
                eat(stack);
                tame(getRandom().nextDouble() < 0.1, player);
                setKnockedOut(false);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (isKnockedOut() && knockOutTime <= MAX_KNOCKOUT_TIME / 2)
            {
                if (!level.isClientSide)
                {
                    // base taming chances on consciousness; the closer it is to waking up the better the chances
                    if (tame(getRandom().nextInt(knockOutTime) < MAX_KNOCKOUT_TIME * 0.2d, player))
                    {
                        setKnockedOut(false);
                        //AnimationPacket.send(this, ROAR_ANIMATION);
                    }
                    else knockOutTime += 600; // add 30 seconds to knockout time
                    eat(stack);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
                else return InteractionResult.CONSUME;
            }
        }

        return super.playerInteraction(player, hand, stack);
    }

    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 1);
    }

    public void roarAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_ROYALRED_ROAR.get(), 3, 1, true);
        ((LessShitLookController) getLookControl()).stopLooking();
        for (LivingEntity entity : getEntitiesNearby(10, this::isAlliedTo))
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
    }

    //TODO: Passenger Keybind Logic
    public void slapAttackAnimation(int time)
    {
        if (time == 7) playSound(WRSounds.ENTITY_ROYALRED_HURT.get(), 1, 1, true);
        else if (time != 12) return;

        attackInBox(getOffsetBox(getBbWidth()).inflate(0.2), 50);
        setYRot(yHeadRot);
    }

    private void biteAttackAnimation(int time)
    {
        if (time == 4)
        {
            attackInBox(getOffsetBox(getBbWidth()).inflate(-0.3), 100);
            playSound(WRSounds.ENTITY_ROYALRED_HURT.get(), 2, 1, true);
        }
    }


    @Override
    public void die(DamageSource cause)
    {
        if (isTame() || isKnockedOut() || cause.getEntity() == null)
            super.die(cause);
        else // knockout RR's instead of killing them
        {
            setHealth(getMaxHealth() * 0.25f); // reset to 25% health
            setKnockedOut(true);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (level.isClientSide && key.equals(BREATHING_FIRE) && isBreathingFire());
            //BreathSound.play(this);
        else super.onSyncedDataUpdated(key);
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        //if (!noAnimations()) return;

        if (key == KeybindHandler.MOUNT_KEY && pressed && !isBreathingFire())
        {
            //if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            //else meleeAttack();
        }

        if (key == KeybindHandler.ALT_MOUNT_KEY && canBreatheFire()) setBreathingFire(pressed);
    }

    public boolean canBreatheFire()
    {
        return ageProgress() > 0.75f;
    }

    /*public void meleeAttack()
    {
        if (!level.isClientSide)
            AnimationPacket.send(this, isFlying() || getRandom().nextBoolean()? BITE_ATTACK_ANIMATION : SLAP_ATTACK_ANIMATION);
    }*/

    @Override
    public Vec3 getApproximateMouthPos()
    {
        Vec3 rotVector = calculateViewVector(getXRot() * 0.65f, yHeadRot);
        Vec3 position = getEyePosition(1).subtract(0, 0.9, 0);
        position = position.add(rotVector.scale(getBbWidth() + 1.3));
        return position;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        float heightFactor = isSleeping()? 0.5f : isInSittingPose()? 0.9f : 1;
        return size.scale(1, heightFactor);
    }

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), ARMOR_SLOT, 0, -15, -15, DragonControlScreen.ARMOR_UV).only(DragonArmorItem.class))
                .addAction(BookActions.TARGET);
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollision(-8.5, this), 0, 0);
        else
            event.getCamera().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    @Override
    public int determineVariant()
    {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getMonth().equals(Month.APRIL) && currentDate.getDayOfMonth() == 1)
            return 99;
        return getRandom().nextDouble() < 0.03? -1 : 0;
    }

    @Override
    public boolean isImmobile()
    {
        return super.isImmobile() || isKnockedOut();
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn)
    {
        return getBbHeight() + 0.5f;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return isTame() && isJuvenile() && !isKnockedOut() && getPassengers().size() < 3;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index)
    {
        return new Vec3(0, getBbHeight() * 0.85f, index == 0? 0.5f : -1);
    }

    @Override
    public float getScale()
    {
        float i = getAgeScale(0.3f);
        if (isMale()) i *= 0.8f;
        return i;
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isFlying()? 5 : 7;
    }

    public boolean isBreathingFire()
    {
        return entityData.get(BREATHING_FIRE);
    }

    public void setBreathingFire(boolean b)
    {
        if (!level.isClientSide) entityData.set(BREATHING_FIRE, b);
    }

    public boolean isKnockedOut()
    {
        return entityData.get(KNOCKED_OUT);
    }

    public void setKnockedOut(boolean b)
    {
        entityData.set(KNOCKED_OUT, b);
        if (!level.isClientSide)
        {
            knockOutTime = b? MAX_KNOCKOUT_TIME : 0;
            if (b)
            {
                setXRot(0);
                clearAI();
                setFlying(false);
            }
        }
    }

    public int getKnockOutTime()
    {
        return knockOutTime;
    }

    public void setKnockoutTime(int i)
    {
        knockOutTime = Math.max(0, i);
        if (i > 0 && !isKnockedOut()) entityData.set(KNOCKED_OUT, true);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        if (isKnockedOut()) return false;
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public boolean canFly()
    {
        return super.canFly() && !isKnockedOut();
    }

    @Override
    public boolean isImmuneToArrows()
    {
        return true;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public boolean shouldSleep()
    {
        return !isKnockedOut() && super.shouldSleep();
    }

    @Override
    public boolean defendsHome()
    {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_ROYALRED_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_ROYALRED_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_ROYALRED_DEATH.get();
    }

    @Override
    public float getSoundVolume()
    {
        return 1.5f * getScale();
    }


    @Override
    public Attribute[] getScaledAttributes()
    {
        return ArrayUtils.addAll(super.getScaledAttributes(), ATTACK_KNOCKBACK);
    }



    /*@org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }*/

    @Override
    public void registerControllers(AnimationData data) {

    }

    class RRAttackGoal extends AnimatedGoal
    {
        private RoyalRedEntity entity;

        public RRAttackGoal(RoyalRedEntity entity)
        {
            super(entity,"base",1,10);
            setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public void start(){ }

        @Override
        public boolean canUse()
        {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive())
            {
                if (!isWithinRestriction(target.blockPosition())) return false;
                return TargetingConditions.forCombat().test(null, target);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse(){
            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) return false;
                return TargetingConditions.forCombat().test(null, target);
            }
            return false;
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();
            double distFromTarget = distanceToSqr(target);
            double degrees = Math.atan2(target.getZ() - getZ(), target.getX() - getX()) * (180 / Math.PI) - 90;
            boolean isBreathingFire = isBreathingFire();
            boolean canSeeTarget = getSensing().hasLineOfSight(target);
            getLookControl().setLookAt(target, 90, 90);

            double headAngle = Math.abs(Mth.wrapDegrees(degrees - yHeadRot));

            //Only breathe fire if we are not at home, and we are far from target in some regard,
            // and head angle permits it, and we are old enough to do so
            //TODO: Distance Values, tweak?
            boolean shouldBreatheFire =
                            !isAtHome()
                            && (distFromTarget > 100 || target.getY() - getY() > 3 || isFlying())
                            && headAngle < 30
                            && canBreatheFire();


            //Fire logic:
            if (isBreathingFire != shouldBreatheFire) {
                setBreathingFire(isBreathingFire = shouldBreatheFire);
            }
            //We have now decided whether to breathe fire or not, if we are call the animation...
            if (isBreathingFire) {
                //TODO: Correct animations
                super.start(entity, "randomized fire breath animation", 2, 3);

                super.start();
            }


            //Random chance to start flying / fly when target is far away...
            //TODO: If we are already flying... should we check for this?
            if (getRandom().nextDouble() < 0.001 || distFromTarget > 900) {
                setFlying(true);
            }
            //If we have not started flying, and we are close to target, melee attack
            //TODO: Remove else?
            else if (distFromTarget <= 24 && !isBreathingFire && canSeeTarget) {
                yBodyRot = (float) Mafs.getAngle(RoyalRedEntity.this, target) + 90;
                setYRot(yBodyRot);
                //TODO: RANDOMIZE MELEE ANIMATIONS
                super.start(entity, "randomized animation", 2, 3);
                //TODO: Do I need to super other things? DEBUG
            }
            //TODO: ANALYZE
            if (getNavigation().isDone() || age % 10 == 0)
            {
                boolean isFlyingTarget = target instanceof TameableDragonEntity && ((TameableDragonEntity) target).isFlying();
                double y = target.getY() + (!isFlyingTarget && getRandom().nextDouble() > 0.1? 8 : 0);
                getNavigation().moveTo(target.getX(), y, target.getZ(), !isFlying() && isBreathingFire? 0.8d : 1.3d);
            }
        }

        //TODO: STOP? To ensure we no longer breathe fire, etc.
    }

}
