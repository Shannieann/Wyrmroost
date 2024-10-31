package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.ai.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.FlyerWanderGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.projectile.breath.FireBreathEntity;
import com.github.shannieann.wyrmroost.network.packets.KeybindHandler;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EntityRoyalRed extends WRDragonEntity implements IBreedable {
    //TODO: Breath + Nether Portals
    //TODO: Further breath optimizations + evaluator
    //TODO: Sleeping logic
    //TODO: Dynamic animations
    static {
        //TODO: Which are needed?
        IDLE_ANIMATION_VARIANTS = 1;
        //TODO: Correct number
        ATTACK_ANIMATION_VARIANTS = 2;
        SITTING_ANIMATION_TIME = 60;
        SLEEPING_ANIMATION_TIME = 60;
    }

    @Override
    public int idleAnimationVariants(){
        return 0;
    }

    public static final EntityDataAccessor<Boolean> BREATHING_FIRE = SynchedEntityData.defineId(EntityRoyalRed.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> KNOCKED_OUT = SynchedEntityData.defineId(EntityRoyalRed.class, EntityDataSerializers.BOOLEAN);

    /*
    private static final EntitySerializer<EntityRoyalRed> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender)
            .track(EntitySerializer.INT, "KnockOutTime", EntityRoyalRed::getKnockOutTime, EntityRoyalRed::setKnockoutTime));


     */
    public static final int ARMOR_SLOT = 0;
    private static final int MAX_KNOCKOUT_TIME = 3600; // 3 minutes

    public static final String ROAR_ANIMATION = "roar";
    public static final int ROAR_ANIMATION_TYPE = 2;
    public static final int ROAR_ANIMATION_TIME = 80;
    public static final boolean ROAR_ANIMATION_MOVES = true;

    public static final String FIRE_ANIMATION = "fire";
    public static final int FIRE_ANIMATION_TYPE = 1;
    public static final int FIRE_ANIMATION_TIME = 80;
    public static final boolean FIRE_ANIMATION_MOVES = false;

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

    public EntityRoyalRed(EntityType<? extends WRDragonEntity> type, Level worldIn) {
        super(type, worldIn);
        noCulling = WRConfig.NO_CULLING.get();
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
    }

    // ====================================
    //      A) Entity Data
    // ====================================


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(KNOCKED_OUT, false);
        entityData.define(BREATHING_FIRE, false);

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (level.isClientSide && key.equals(BREATHING_FIRE) && getBreathingFire()) ;
            //BreathSound.play(this);
        else super.onSyncedDataUpdated(key);
    }

    public static AttributeSupplier.Builder getAttributeSupplier() {
        // base male attributes
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 130)
                .add(MOVEMENT_SPEED, 0.22)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(FOLLOW_RANGE, 60)
                .add(ATTACK_KNOCKBACK, 4)
                .add(ATTACK_DAMAGE, 12)
                .add(FLYING_SPEED, 0.1)
                .add(WREntityTypes.Attributes.PROJECTILE_DAMAGE.get(), 4);
    }

    /*
    @Override
    public EntitySerializer<EntityRoyalRed> getSerializer() {
        return SERIALIZER;
    }

     */

    public void setBreathingFire(boolean breathingFire) {
        if (!level.isClientSide) entityData.set(BREATHING_FIRE, breathingFire);
    }


    public boolean getBreathingFire() {
        return entityData.get(BREATHING_FIRE);
    }

    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================
    @Override
    public boolean defendsHome() {
        return true;
    }


    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================

    @Override
    public int determineVariant() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getMonth().equals(Month.APRIL) && currentDate.getDayOfMonth() == 1) // April fools RR
            return -2;
        if (!this.isNoAi()) { // For normal generation: Chance for melanistic variants
            return getRandom().nextDouble() < 0.03 ? -1 : 0;
        } else {  // TODO why tho?
            return 0;
        }
    }


    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || getSleeping()) size = size.scale(1, 0.5f);
        return size;
    }

    @Override
    public Vec3 getApproximateMouthPos() {
        Vec3 rotVector = calculateViewVector(getXRot() * 0.65f, yHeadRot);
        Vec3 position = getEyePosition(1).subtract(0, 0.9, 0);
        position = position.add(rotVector.scale(getBbWidth() + 1.3));
        return position;
    }

    @Override
    public Attribute[] getScaledAttributes() {
        return ArrayUtils.addAll(super.getScaledAttributes(), ATTACK_KNOCKBACK);
    }

    @Override
    public float ageProgressAmount() {
        return 0;
    }

    @Override
    public float initialBabyScale() {
        return 0;
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || isKnockedOut();
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return getBbHeight() + 0.5f;
    }

    //ToDo: Fix
    /*
    @Override
    public float getScale() {
        float i = getAgeScale(0.3f);
        if (getGender() == 1) i *= 0.8f;
        return i;
    }

     */


    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void aiStep() {
        //System.out.println(getAnimation());
        super.aiStep();
        // =====================
        //       Update Timers
        // =====================
        flightTimer.add(isUsingFlyingNavigator() ? 0.1f : -0.085f);
        sitTimer.add(isInSittingPose() ? 0.075f : -0.1f);
        sleepTimer.add(getSleeping() ? 0.035f : -0.05f);
        breathTimer.add(getBreathingFire() ? 0.15f : -0.2f);
        knockOutTimer.add(isKnockedOut() ? 0.05f : -0.1f);

        if (!level.isClientSide) {
            // =====================
            //       Fire Logic
            // =====================

            //TODO: DEBUG METHODS
            if (getBreathingFire() && getControllingPlayer() == null && getTarget() == null)
                setBreathingFire(false);


            if (breathTimer.get() == 1) {
                //TODO: Reposition fire breath start point
                level.addFreshEntity(new FireBreathEntity(this));
            }

            // =====================
            //       Roar Logic
            // =====================

            //TODO: Improve or extract to a Roar Goal.
            /*
            if (this.getAnimation().equals("base")
                    || this.getAnimation().equals("walk")
                    || this.getAnimation().equals("walk_fast")
                    || this.getAnimation().equals("swim")
                    || this.getAnimation().equals("swim_fast")
                    && !this.isKnockedOut() && !this.getSleeping() && !this.getBreathingFire() && getRandom().nextDouble() < 0.0004) {
                setAnimation(ROAR_ANIMATION);
                setAnimationType(ROAR_ANIMATION_TYPE);
                setAnimationTime(ROAR_ANIMATION_TIME);
                setIsMovingAnimation(ROAR_ANIMATION_MOVES);
                setManualAnimationCall(true);
            }
            */


            // =====================
            //       Knockout Logic
            // =====================
            if (isKnockedOut() && --knockOutTime <= 0) setKnockedOut(false);


        }
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    private boolean shouldBreatheFire() {
        //TODO: Distance Values, tweak?
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distFromTarget = distanceToSqr(target);
            double degrees = Math.atan2(target.getZ() - getZ(), target.getX() - getX()) * (180 / Math.PI) - 90;
            double headAngle = Math.abs(Mth.wrapDegrees(degrees - yHeadRot));
            return (!isAtHome() && (distFromTarget > 100 || target.getY() - getY() > 3 || isUsingFlyingNavigator()) && headAngle < 30 && canBreatheFire());
        }
        return false;
    }

    public boolean canBreatheFire() {
        return getAgeProgress() > 0.75f;
    }

    public boolean isKnockedOut() {
        return entityData.get(KNOCKED_OUT);
    }

    public void setKnockedOut(boolean b) {
        entityData.set(KNOCKED_OUT, b);
        if (!level.isClientSide) {
            knockOutTime = b ? MAX_KNOCKOUT_TIME : 0;
            if (b) {
                setXRot(0);
                clearAI();
                setNavigator(NavigationType.GROUND);
            }
        }
    }

    public int getKnockOutTime() {
        return knockOutTime;
    }

    public void setKnockoutTime(int i) {
        knockOutTime = Math.max(0, i);
        if (i > 0 && !isKnockedOut()) entityData.set(KNOCKED_OUT, true);
    }

    //TODO Not working
    @Override
    public void die(DamageSource cause) {
        if (isTame() || isKnockedOut() || cause.getEntity() == null)
            super.die(cause);
        else // knockout RR's instead of killing them
        {
            setHealth(getMaxHealth() * 0.25f); // reset to 25% health
            setKnockedOut(true);
        }
    }


    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isImmuneToArrows() {
        //TODO: DEBUGGING
        return false;
    }


    // ====================================
    //      C) Navigation and Control
    // ====================================

    public boolean speciesCanWalk() {
        return true;
    }


    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================


    @Override
    public boolean speciesCanFly() {
        return true;
    }


    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (isKnockedOut()) return false;
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public boolean dragonCanFly() {
        return super.dragonCanFly() && !isKnockedOut();
    }

    @Override
    public int getYawRotationSpeed() {
        return isUsingFlyingNavigator() ? 5 : 7;
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================


    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    @Override
    public void setThirdPersonMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollisionDistance(-8.5, this, player), 0, 0);
        //else
            //event.getCamera().move(ClientEvents.getViewCollisionDistance(-5, this), -0.75, 0);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return isJuvenile() && !isKnockedOut() && super.canAddPassenger(passenger);
    }

    @Override
    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, getBbHeight() * 1.15, index == 0 ? 1.75f : 1.0);
    }

    //TODO: Whole keybind logic
    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed) {
        //if (!noAnimations()) return;

        if (key == KeybindHandler.MOUNT_KEY && pressed && !getBreathingFire()) {
            //if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            //else meleeAttack();
        }

        if (key == KeybindHandler.ALT_MOUNT_KEY && canBreatheFire()) setBreathingFire(pressed);
    }
    // ====================================
    //      C.1) Navigation and Control: Swimming
    // ====================================

    public boolean speciesCanSwim() {
        return false;
    }

    // ====================================
    //      D) Taming
    // ====================================

    /*@Override
    public void applyStaffInfo(BookContainer container) {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), ARMOR_SLOT, 0, -15, -15, DragonControlScreen.ARMOR_UV).only(DragonArmorItem.class))
                .addAction(BookActions.TARGET);
    }*/

    /*
    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack) {
        if (!isTame() && isFood(stack)) {
            if (isHatchling() || player.isCreative()) {
                eat(stack);
                tame(getRandom().nextDouble() < 0.1, player);
                setKnockedOut(false);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (isKnockedOut() && knockOutTime <= MAX_KNOCKOUT_TIME / 2) {
                if (!level.isClientSide) {
                    // base taming chances on consciousness; the closer it is to waking up the better the chances
                    if (tame(getRandom().nextInt(knockOutTime) < MAX_KNOCKOUT_TIME * 0.2d, player)) {
                        setKnockedOut(false);
                        //AnimationPacket.send(this, ROAR_ANIMATION);
                    } else knockOutTime += 600; // add 30 seconds to knockout time
                    eat(stack);
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                } else return InteractionResult.CONSUME;
            }
        }

        return super.playerInteraction(player, hand, stack);
    }

     */

    public InteractionResult tameLogic (Player tamer, ItemStack stack) {
        if (!isFood(stack)) return InteractionResult.PASS;

        if (isHatchling() || tamer.isCreative()) {
            eat(tamer.level, stack);
            attemptTame(0.1f, tamer, stack);
            setKnockedOut(false);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (isKnockedOut() && knockOutTime <= MAX_KNOCKOUT_TIME / 2) {
            if (!level.isClientSide) {
                if (attemptTame((float) knockOutTime/MAX_KNOCKOUT_TIME * 0.2f, tamer, stack)) {
                    setKnockedOut(false);
                    //AnimationPacket.send(this, ROAR_ANIMATION);
                } else knockOutTime += 600; // add 30 seconds to knockout time
                eat(tamer.level, stack);
                return InteractionResult.SUCCESS;
            } else return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {
        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case -1 -> new Vec2(1, 5);
            default -> new Vec2(0, 5);
        };
    }

    @Override
    public boolean canEquipArmor() {
        return true;
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties(stack, this).isMeat();
    }


    // ====================================
    //      E) Client
    // ====================================

    // ====================================
    //      E.1) Client: Sounds
    // ====================================


    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_ROYALRED_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_ROYALRED_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_ROYALRED_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 1.5f * getScale();
    }

    //TODO: SOUNDS
    /*
    public void roarAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_ROYALRED_ROAR.get(), 3, 1, true);
        ((WRGroundLookControl) getLookControl()).stopLooking();
        for (LivingEntity entity : getEntitiesNearby(10, this::isAlliedTo))
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60));
    }
    */

    /*public void meleeAttack()
    {
        if (!level.isClientSide)
            AnimationPacket.send(this, isUsingFlyingNavigator() || getRandom().nextBoolean()? BITE_ATTACK_ANIMATION : SLAP_ATTACK_ANIMATION);
    }*/

    // ====================================
    //      E.2) Client: Camera
    // ====================================

    @Override
    public float getMountCameraYOffset() {
        return -5.7f;
    }


    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        //goalSelector.addGoal(4, new WRRunWhenLosingGoal(this, 0.1f, 40, 0.95f, 0.99f ));
        goalSelector.addGoal(5, new RRAttackGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new WRDragonBreedGoal(this));
        goalSelector.addGoal(9, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        // TODO Replace this goal with a different WRFlyAwayWhenLosingGoal. Pretty sure RRs would just walk away and not use their wings which... isn't that smart.
        goalSelector.addGoal(3, new WRRunWhenLosingGoal(this, 0.2f, 0.1f, 20f, 1.15f, 1f));
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        //TODO: Attack multiple LivingEntities, perhaps not select targets but rather exlude?
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                e -> e.getType() == EntityType.PLAYER || e instanceof Animal || e instanceof AbstractVillager));
    }

    @Override
    public InteractionResult breedLogic(Player tamer, ItemStack stack) {
        return null;
    }

    @Override
    public int hatchTime() {
        return 500;
    }


    // ====================================
    //      F.n) Goals: RRAttackGoal
    // ====================================


    class RRAttackGoal extends AnimatedGoal {
        private EntityRoyalRed entity;

        boolean animationPlaying;
        int ticksUntilNextAttack;
        private boolean attackIsQueued;
        private int queuedAttackTimer;
        private int attackQueueTimer = 0;
        double inflateValue;
        int disableShieldTime;
        private int elapsedTicks;

        public RRAttackGoal(EntityRoyalRed entity) {
            super(entity);
            setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public void start() {
            float test;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) return false;
                return TargetingConditions.forCombat().test(null, target);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = getTarget();
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) {
                    return false;
                }
                return TargetingConditions.forCombat().test(null, target);
            }
            return false;
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            double distFromTarget = distanceToSqr(target);
            boolean isBreathingFire = getBreathingFire();
            boolean canSeeTarget = getSensing().hasLineOfSight(target);
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


                getLookControl().setLookAt(target, 90, 90);

                //Random chance to start flying / fly when target is far away...
                //TODO: If we are already flying... should we check for this?
                if (getRandom().nextDouble() < 0.001 || distFromTarget > 900) {
                    setNavigator(NavigationType.FLYING);
                }

                //GoalLogic: Do either breathe fire or melee attack
                //Goal Logic: Option 1 - Breathe Fire
                if (entity.shouldBreatheFire() != isBreathingFire) {
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
                //Only if flying!
                else if (distFromTarget <= 24 && !isBreathingFire && canSeeTarget && !isUsingFlyingNavigator()) {
                    //GoalLogic: try to perform a melee attack
                    this.checkAndPerformAttack();
                }
            }

            if (getNavigation().isDone() || age % 10 == 0) {
                boolean isFlyingTarget = target instanceof WRDragonEntity && ((WRDragonEntity) target).isUsingFlyingNavigator();
                double y = target.getY() + (!isFlyingTarget && getRandom().nextDouble() > 0.1 ? 8 : 0);
                getNavigation().moveTo(target.getX(), y, target.getZ(), !isUsingFlyingNavigator() && isBreathingFire ? 0.8d : 1.3d);
            }

        }

        @Override
        public void stop() {
            LivingEntity livingentity = this.entity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.entity.setTarget(null);
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
                //yBodyRot = (float) Mafs.getAngle(EntityRoyalRed.this, target) + 90;
                //setYRot(yBodyRot);

                //AnimationLogic: decide which attack variant we are using
                //TODO: REMOVED TAIL ATTACK TEMPORARILY, VARIANT 3, RE_ADD
                int attackVariant = 1+entity.random.nextInt(ATTACK_ANIMATION_VARIANTS) /*+ 1*/;
                String attackAnimation = ATTACK_ANIMATION + attackVariant;
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

        //TODO: CARE, PARENT GOAL HAS BEEN TWEAKED SLIGHTLY. ADAPT.

        /*
    class AttackGoalImproved extends AnimatedGoal {
        private EntityRoyalRed entity;

        boolean animationPlaying;
        int ticksUntilNextAttack;
        private boolean attackIsQueued;
        private int queuedAttackTimer;
        private int attackQueueTimer = 0;
        double inflateValue;
        int disableShieldTime;
        private int elapsedTicks;
        private boolean goalKillFlag;
        private long lastCanUseCheck;


        public AttackGoalImproved(EntityRoyalRed entity) {
            super(entity);
            setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
            this.entity = entity;
        }

        @Override
        public void start() {
            float test = 0;
        }

        //TODO: If works, copy canUse method entirely over
        //TODO: If works, test with / without extra setPaths
        @Override
        public boolean canUse() {
            long i = level.getGameTime();
            if (i - this.lastCanUseCheck < 40L) {
                return false;
            } else {
                this.lastCanUseCheck = i;
                LivingEntity target = getTarget();
                if (target != null && target.isAlive()) {
                    if (!isWithinRestriction(target.blockPosition())) {
                        return false;
                    }
                    if (getSensing().hasLineOfSight(target)) {
                        if (TargetingConditions.forCombat().test(null, target)) {
                            return getNavigation().moveTo(target.getX(), target.getX(), target.getY(), !isUsingFlyingNavigator() && getBreathingFire() ? 0.8d : 1.3d);
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = getTarget();
            if (goalKillFlag) {
                return false;
            }
            if (target != null && target.isAlive()) {
                if (!isWithinRestriction(target.blockPosition())) {
                    return false;
                }
                return TargetingConditions.forCombat().test(null, target);
            }
            return false;
        }

        @Override
        public void tick() {
            //If we have an attack queued, do not proceed with the attack selection logic

            //If animation is over (checked via Goal class) in AnimationLogic update the local variable for the GoalLogic
            if (animationPlaying) {
                if (super.canContinueToUse()) {
                    super.tick();
                } else {
                    super.stop();
                    animationPlaying = false;
                }
            }
            LivingEntity target = getTarget();
            if (target != null) {
                double distFromTarget = distanceToSqr(target);
                boolean isBreathingFire = getBreathingFire();
                boolean canSeeTarget = getSensing().hasLineOfSight(target);
                getLookControl().setLookAt(target, 90, 90);
                if (attackIsQueued) {
                    if (this.attackQueueTimer == queuedAttackTimer) {
                        attackQueueTimer = 0;
                        attackIsQueued = false;
                        attackInBox(getOffsetBox(getBbWidth()).inflate(inflateValue), disableShieldTime);
                    } else {
                        attackQueueTimer++;
                    }
                } else {
                    //Attack Logic:
                    // 1.- Determines whether we should fly or walk while attacking.
                    // 2.- Determines whether we should use a melee attack or breathe fire.

                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);

                    //Random chance to start flying / fly when target is far away...
                    if (!isUsingFlyingNavigator() && (getRandom().nextDouble() < 0.001 || distFromTarget > 900)) {
                        setNavigator(NavigationType.FLYING);
                    }


                    //GoalLogic: Do either breathe fire or melee attack
                    //Goal Logic: Option 1 - Breathe Fire
                    if (entity.shouldBreatheFire() != isBreathingFire) {
                        //AnimationLogic: Only breathe fire if we can animate correspondingly...
                        if (!animationPlaying) {
                            //GoalLogic: Start breathing fire
                            setBreathingFire(entity.shouldBreatheFire());
                            //AnimationLogic: Start fire breath animation...
                            animationPlaying = true;
                            super.start(FIRE_ANIMATION, FIRE_ANIMATION_TYPE, FIRE_ANIMATION_TIME, FIRE_ANIMATION_MOVES);
                        }
                    }

                    //Goal Logic: Option 2 - Melee Attack
                    else if (distFromTarget <= 24 && !isBreathingFire && canSeeTarget) {
                        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                        //GoalLogic: try to perform a melee attack
                        this.checkAndPerformAttack();
                    }
                }

                //Attack Move Logic:
                //If our target is flying, just attempt to go to it, do not attempt to fly above it...
                //Else, if our target is on the ground and we are flying, random chance to try and move above it instead of moving to it...
                boolean isFlyingTarget = target instanceof WRDragonEntity && ((WRDragonEntity) target).isUsingFlyingNavigator();
                boolean goAboveRandom = getRandom().nextDouble() > 0.1;
                boolean goAbove = goAboveRandom && !isFlyingTarget && isUsingFlyingNavigator();
                double y = target.getY() + (goAbove ? 8 : 0);

                if (getNavigation().isDone() && !getNavigation().isStuck()) {
                    //Navigation has stopped, and we are not stuck...
                    //Try and set a new path...
                    if (!getNavigation().moveTo(target.getX(), y, target.getZ(), !isUsingFlyingNavigator() && isBreathingFire ? 0.8d : 1.3d)) {
                        // We failed at setting a new path to the target, path returned null
                        //At this point: If we are not flying, start flying. This might resolve the problem next tick by giving us new navigation options...
                        if (!isUsingFlyingNavigator() && canLiftOff()) {
                            setNavigator(NavigationType.FLYING);
                        } else {
                            goalKillFlag = true;
                            /*
                            //We are already using the flying navigator. Options are limited. Let us do one final check.
                            if (goAbove) {
                                //If we tried to go above the target, perhaps this is what caused the issue.
                                // Try one final time to path to the other alternative position
                                if (!getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), !isUsingFlyingNavigator() && isBreathingFire ? 0.8d : 1.3d)) {
                                    //We failed at finding a path, even with new adjustment, time to kill the goal.
                                    goalKillFlag = true;
                                }
                                //We did not try to go above the target, perhaps by going above we can fix the issue.
                                // Try one final time to path to the other alternative position
                            } else {
                                if (!getNavigation().moveTo(target.getX(), target.getY() + 8, target.getZ(), !isUsingFlyingNavigator() && isBreathingFire ? 0.8d : 1.3d)) {
                                    //We failed at finding a path, even with new adjustment, time to kill the goal.
                                    goalKillFlag = true;
                                }
                            }

                             *//*
                        }
                    }
                } else if (getNavigation().isStuck()) {
                    //TODO: Try and provide alternatives if stuck?
                    goalKillFlag = true;
                }
            }
        }

        @Override
        public void stop () {
            LivingEntity livingentity = this.entity.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                this.entity.setTarget((LivingEntity) null);
            }
            this.entity.setAggressive(false);
            this.entity.getNavigation().stop();
            this.entity.setBreathingFire(false);
            goalKillFlag = false;
            super.stop();
        }

        protected void checkAndPerformAttack () {
            LivingEntity target = getTarget();
            //GoalLogic: check we can perform an attack, timer set by AnimationLogic
            if (this.ticksUntilNextAttack <= 0) {
                //yBodyRot = (float) Mafs.getAngle(EntityRoyalRed.this, target) + 90;
                //setYRot(yBodyRot);

                //AnimationLogic: decide which attack variant we are using
                int attackVariant = entity.random.nextInt(ATTACK_ANIMATION_VARIANTS) + 1;
                String attackAnimation = ATTACK_ANIMATION + attackVariant;
                int attackAnimationTime = 0;

                //GoalLogic: check reset attack cooldown based on AnimationLogic, animation time
                switch (attackVariant) {
                    case 1 -> {
                        attackAnimationTime = ATTACK_ANIMATION_TIME_1;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_1;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_1;
                    }
                    case 2 -> {
                        attackAnimationTime = ATTACK_ANIMATION_TIME_2;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_2;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_2;
                    }
                    case 3 -> {
                        attackAnimationTime = ATTACK_ANIMATION_TIME_3;
                        this.ticksUntilNextAttack = (int) ATTACK_ANIMATION_TIME_3;
                        inflateValue = 0.2;
                        disableShieldTime = 50;
                        this.queuedAttackTimer = ATTACK_QUEUE_TIME_3;
                    }
                }

                //AnimationLogic: Only do melee attack if we can animate correspondingly...
                if (!animationPlaying) {
                    animationPlaying = true;
                    //AnimationLogic: start corresponding animation
                    super.start(attackAnimation, ATTACK_ANIMATION_TYPE, attackAnimationTime, ATTACK_ANIMATION_MOVES);
                    //GoalLogic: Do melee attack, with parameters coming from animation logic
                    this.attackIsQueued = true;
                }
            }
        }
    }
    */
