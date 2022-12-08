package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.ai.AnimatedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.DragonInventory;
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

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

//TODO: Tidy up class

public class RoyalRedEntity extends WRDragonEntity
{
    static {
        IDLE_ANIMATION_VARIANTS = 1;
        ATTACK_ANIMATION_VARIANTS = 3;
        SITTING_ANIMATION_TIME = 60;
        SLEEPING_ANIMATION_TIME = 60;
    }



    public static final EntityDataAccessor<Boolean> BREATHING_FIRE = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> KNOCKED_OUT = SynchedEntityData.defineId(RoyalRedEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntitySerializer<RoyalRedEntity> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getVariant, WRDragonEntity::setGender)
            .track(EntitySerializer.STRING, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant)
            .track(EntitySerializer.BOOL, "Sleeping", WRDragonEntity::isSleeping, WRDragonEntity::setSleeping)
            .track(EntitySerializer.INT, "KnockOutTime", RoyalRedEntity::getKnockOutTime, RoyalRedEntity::setKnockoutTime));

    public static final int ARMOR_SLOT = 0;
    private static final int MAX_KNOCKOUT_TIME = 3600; // 3 minutes

    public static final String ROAR_ANIMATION = "roar";
    public static final int ROAR_ANIMATION_TYPE = 2;
    public static final int ROAR_ANIMATION_TIME = 80;

    public static final String FIRE_ANIMATION = "fire";
    public static final int FIRE_ANIMATION_TYPE = 1;
    public static final int FIRE_ANIMATION_TIME = 80;

    public static final String ATTACK_ANIMATION = "attack";
    public static final int ATTACK_ANIMATION_TYPE = 2;
    public static final int ATTACK_ANIMATION_TIME_1 = 20;
    public static final int ATTACK_ANIMATION_TIME_2 = 13;
    public static final int ATTACK_ANIMATION_TIME_3 = 35;
    public static final int ATTACK_QUEUE_TIME_1 = 9;
    public static final int ATTACK_QUEUE_TIME_2 = 9;
    public static final int ATTACK_QUEUE_TIME_3 = 25;

    public final LerpedFloat flightTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public final LerpedFloat breathTimer = LerpedFloat.unit();
    public final LerpedFloat knockOutTimer = LerpedFloat.unit();
    private int knockOutTime = 0;

    public RoyalRedEntity(EntityType<? extends WRDragonEntity> type, Level worldIn)
    {
        super(type, worldIn);
        noCulling = WRConfig.NO_CULLING.get();
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
    }

    // ====================================
    //      A) Entity Data
    // ====================================

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(GENDER, "male");
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, "base0");
        entityData.define(BREATHING_FIRE, false);
        entityData.define(KNOCKED_OUT, false);
        entityData.define(FLYING, false);
        entityData.define(ARMOR, ItemStack.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (level.isClientSide && key.equals(BREATHING_FIRE) && getBreathingFire());
            //BreathSound.play(this);
        else super.onSyncedDataUpdated(key);
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
    public EntitySerializer<RoyalRedEntity> getSerializer()
    {
        return SERIALIZER;
    }

    public void setBreathingFire(boolean breathingFire)
    {
        if (!level.isClientSide) entityData.set(BREATHING_FIRE, breathingFire);
    }


    public boolean getBreathingFire()
    {
        return entityData.get(BREATHING_FIRE);
    }
    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================
    @Override
    public boolean defendsHome()
    {
        return true;
    }


    // ====================================
    //      A.5) Entity Data: SLEEP
    // ====================================

    @Override
    public boolean shouldSleep()
    {
        return !isKnockedOut() && super.shouldSleep();
    }


    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================

    @Override
    public String determineVariant()
    {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getMonth().equals(Month.APRIL) && currentDate.getDayOfMonth() == 1)
            return "april";
        return getRandom().nextDouble() < 0.03? "special" : "base";
    }


    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.5f);
        return size;
    }

    @Override
    public Vec3 getApproximateMouthPos()
    {
        Vec3 rotVector = calculateViewVector(getXRot() * 0.65f, yHeadRot);
        Vec3 position = getEyePosition(1).subtract(0, 0.9, 0);
        position = position.add(rotVector.scale(getBbWidth() + 1.3));
        return position;
    }

    @Override
    public Attribute[] getScaledAttributes()
    {
        return ArrayUtils.addAll(super.getScaledAttributes(), ATTACK_KNOCKBACK);
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
    public float getScale()
    {
        float i = getAgeScale(0.3f);
        if (getGender().equals("male")) i *= 0.8f;
        return i;
    }


    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void aiStep()
    {
        super.aiStep();
        // =====================
        //       Update Timers
        // =====================
        flightTimer.add(isFlying()? 0.1f : -0.085f);
        sitTimer.add(isInSittingPose()? 0.075f : -0.1f);
        sleepTimer.add(isSleeping()? 0.035f : -0.05f);
        breathTimer.add(getBreathingFire()? 0.15f : -0.2f);
        knockOutTimer.add(isKnockedOut()? 0.05f : -0.1f);

        if (!level.isClientSide) {
            // =====================
            //       Fire Logic
            // =====================

            if (getBreathingFire() && getControllingPlayer() == null && getTarget() == null)
                setBreathingFire(false);

            if (breathTimer.get() == 1) {
                //TODO: Reposition fire breath start point
                level.addFreshEntity(new FireBreathEntity(this));
            }

            // =====================
            //       Roar Logic
            // =====================

            if (this.getAnimation().equals("base")
                    || this.getAnimation().equals("walk")
                    || this.getAnimation().equals("walk_fast")
                    || this.getAnimation().equals("swim")
                    || this.getAnimation().equals("swim_fast")
                    && !this.isKnockedOut() && !this.isSleeping() && !this.getBreathingFire() && getRandom().nextDouble() < 0.0004) {
                setAnimation(ROAR_ANIMATION);
                setAnimationType(ROAR_ANIMATION_TYPE);
                setAnimationTime(ROAR_ANIMATION_TIME);
                setManualAnimationCall(true);
            }




            // =====================
            //       Knockout Logic
            // =====================
            if (isKnockedOut() && --knockOutTime <= 0) setKnockedOut(false);


        }
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    private boolean shouldBreatheFire(){
        //TODO: Distance Values, tweak?
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distFromTarget = distanceToSqr(target);
            double degrees = Math.atan2(target.getZ() - getZ(), target.getX() - getX()) * (180 / Math.PI) - 90;
            double headAngle = Math.abs(Mth.wrapDegrees(degrees - yHeadRot));
            return (!isAtHome() && (distFromTarget > 100 || target.getY() - getY() > 3 || isFlying()) && headAngle < 30 && canBreatheFire());
        }
        return false;
    }

    public boolean canBreatheFire()
    {
        return ageProgress() > 0.75f;
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
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isImmuneToArrows()
    {
        return true;
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

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
    public int getYawRotationSpeed()
    {
        return isFlying()? 5 : 7;
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollision(-8.5, this), 0, 0);
        else
            event.getCamera().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
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

    //TODO: Keybind animations
    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        //if (!noAnimations()) return;

        if (key == KeybindHandler.MOUNT_KEY && pressed && !getBreathingFire())
        {
            //if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            //else meleeAttack();
        }

        if (key == KeybindHandler.ALT_MOUNT_KEY && canBreatheFire()) setBreathingFire(pressed);
    }

    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), ARMOR_SLOT, 0, -15, -15, DragonControlScreen.ARMOR_UV).only(DragonArmorItem.class))
                .addAction(BookActions.TARGET);
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
    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 1);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    // ====================================
    //      E) Client
    // ====================================

    // ====================================
    //      E.1) Client: Sounds
    // ====================================


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

    //TODO: SOUNDS
    /*
    public void roarAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_ROYALRED_ROAR.get(), 3, 1, true);
        ((LessShitLookController) getLookControl()).stopLooking();
        for (LivingEntity entity : getEntitiesNearby(10, this::isAlliedTo))
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
    }
    */

    /*public void meleeAttack()
    {
        if (!level.isClientSide)
            AnimationPacket.send(this, isFlying() || getRandom().nextBoolean()? BITE_ATTACK_ANIMATION : SLAP_ATTACK_ANIMATION);
    }*/

    // ====================================
    //      F) Goals
    // ====================================

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
        //TODO: Target Villagers, etc.
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e -> e.getType() == EntityType.PLAYER || e instanceof Animal));
    }

    // ====================================
    //      F.n) Goals: RRAttackGoal
    // ====================================

    class RRAttackGoal extends AnimatedGoal
    {
        private RoyalRedEntity entity;

        boolean animationPlaying;
        int ticksUntilNextAttack;
        private boolean attackIsQueued;
        private int queuedAttackTimer;
        private int attackQueueTimer = 0;
        double inflateValue;
        int disableShieldTime;

        public RRAttackGoal(RoyalRedEntity entity)
        {
            super(entity);
            setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public void start(){}

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
            if (attackIsQueued) {
                if (this.attackQueueTimer == queuedAttackTimer) {
                    attackQueueTimer = 0;
                    attackIsQueued = false;
                    attackInBox(getOffsetBox(getBbWidth()).inflate(inflateValue), disableShieldTime);
                } else {
                    attackQueueTimer++;
                }
            } else {

                //If animation is over (checked via Goal class) in AnimationLogic update the local variable for the GoalLogic
                if (animationPlaying) {
                    if (super.canContinueToUse()) {
                        super.tick();
                    } else {
                        super.stop();
                        animationPlaying = false;
                    }
                }

                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);

                LivingEntity target = getTarget();
                double distFromTarget = distanceToSqr(target);

                boolean isBreathingFire = getBreathingFire();
                boolean canSeeTarget = getSensing().hasLineOfSight(target);
                getLookControl().setLookAt(target, 90, 90);

                //Random chance to start flying / fly when target is far away...
                //TODO: If we are already flying... should we check for this?
                if (getRandom().nextDouble() < 0.001 || distFromTarget > 900) {
                    setFlying(true);
                }


                //GoalLogic: Do either breathe fire or melee attack
                //Goal Logic: Option 1 - Breathe Fire
                if (entity.shouldBreatheFire() != isBreathingFire){
                    //AnimationLogic: Only breathe fire if we can animate correspondingly...
                    if (!animationPlaying) {
                        //GoalLogic: Start breathing fire
                        setBreathingFire(entity.shouldBreatheFire());
                        //AnimationLogic: Start fire breath animation...
                        animationPlaying = true;
                        super.start(FIRE_ANIMATION, FIRE_ANIMATION_TYPE, FIRE_ANIMATION_TIME);
                    }
                }


                //Goal Logic: Option 2 - Melee Attack
                else if (distFromTarget <= 24 && !isBreathingFire && canSeeTarget) {
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                    //GoalLogic: try to perform a melee attack
                    this.checkAndPerformAttack();
                }
                if (getNavigation().isDone() || age % 10 == 0)
                {
                    boolean isFlyingTarget = target instanceof WRDragonEntity && ((WRDragonEntity) target).isFlying();
                    double y = target.getY() + (!isFlyingTarget && getRandom().nextDouble() > 0.1? 8 : 0);
                    getNavigation().moveTo(target.getX(), y, target.getZ(), !isFlying() && isBreathingFire? 0.8d : 1.3d);
                }
            }
        }

        @Override
        public void stop () {
            LivingEntity livingentity = this.entity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.entity.setTarget((LivingEntity)null);
            }
            this.entity.setAggressive(false);
            this.entity.getNavigation().stop();
            this.entity.setBreathingFire(false);
            super.stop();
        }

        protected void checkAndPerformAttack() {
            LivingEntity target = getTarget();
            //GoalLogic: check we can perform an attack, timer set by AnimationLogic
            if (this.ticksUntilNextAttack <= 0) {
                yBodyRot = (float) Mafs.getAngle(RoyalRedEntity.this, target) + 90;
                setYRot(yBodyRot);

                //AnimationLogic: decide which attack variant we are using
                int attackVariant = entity.random.nextInt(ATTACK_ANIMATION_VARIANTS)+1;
                String attackAnimation = ATTACK_ANIMATION+attackVariant;
                int attackAnimationTime = 0;

                //GoalLogic: check reset attack cooldown based on AnimationLogic, animation time
                switch (attackVariant) {
                    case 1:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_1;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_1;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_1;
                        break;
                    case 2:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_2;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_2;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_2;

                        break;

                    case 3:
                        attackAnimationTime = ATTACK_ANIMATION_TIME_3;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_3;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_3;

                        break;
                }

                //AnimationLogic: Only do melee attack if we can animate correspondingly...
                if (!animationPlaying) {
                    animationPlaying = true;
                    //AnimationLogic: start corresponding animation
                    super.start(attackAnimation, ATTACK_ANIMATION_TYPE, attackAnimationTime);
                    //GoalLogic: Do melee attack, with parameters coming from animation logic
                    this.attackIsQueued = true;
                }
            }
        }
    }
}