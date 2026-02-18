package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRSwimToSurfaceGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRFlockFlyAwayGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRReturnToGroundIfIdleGoal;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.Nullable;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;


public class EntityCanariWyvern extends WRDragonEntity implements IBreedable, ITameable {

    // Usually at -1. Set to INTEGER.MAX_VALUE when conditions met, then set to 240 when on ground and threat display can begin. When timer hits 0 it attacks player.
    public static final EntityDataAccessor<Integer> THREATENING_TIMER = SynchedEntityData.defineId(EntityCanariWyvern.class, EntityDataSerializers.INT);

    // Only used for the goal when a group of canaris fly away in sync. Usually (0,0,0).
    public static final EntityDataAccessor<Integer> FLOCKING_X = SynchedEntityData.defineId(EntityCanariWyvern.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FLOCKING_Y = SynchedEntityData.defineId(EntityCanariWyvern.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FLOCKING_Z = SynchedEntityData.defineId(EntityCanariWyvern.class, EntityDataSerializers.INT);

    private static final Predicate<LivingEntity> THREATEN_PREDICATE = e -> e instanceof Player; // && ! ((Player)e).getAbilities().instabuild; Creative players should be allowed to begin taming process
    private static final TargetingConditions THREATEN_CONDITIONS = TargetingConditions.forCombat().selector(THREATEN_PREDICATE);

    private static final float MOVEMENT_SPEED = 0.21f;
    private static final float FLYING_SPEED = 0.14f;

    // This timer is used to check two different complicated things only once per second, which helps prevent lag
    private static final int CHECK_JUKEBOX_NEARBY_PLAYERS_INTERVAL = 20;
    private int checkJukeboxNearbyPlayersTimer;

    private int featherTime; // This doesn't need to be synced, it's just for randomly spawning feathers when tame

    public EntityCanariWyvern(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
        this.featherTime = this.random.nextInt(6000) + 3000;
    }

    // ====================================
    //      Animations
    // ====================================

    @Override
    public <E extends IAnimatable> PlayState predicateAnimation(AnimationEvent<E> event) {

        if (this.isRidingPlayer()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        return super.predicateAnimation(event);
    }

    @Override
    public int numIdleAnimationVariants() {
        return 2;
    }

    @Override
    public int getIdleAnimationTime(int index) {
        int[] animationTimesInOrder = {22, 41};
        return animationTimesInOrder[index];
    }

    @Override
    public int numAttackAnimationVariants() {
        return 5; // 4 land, 1 fly
    }

    @Override
    public int getAttackAnimationTime(int index) {
        int[] animationTimesInOrder = {30, 10, 20, 20, 30}; // first 4 land, last 1 fly
        return animationTimesInOrder[index];
    }

    public int getLieDownTime() {
        return 5;
    }

    public int getSitDownTime() {
        return 5;
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return (Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonAttributesConfig.maxHealth.get())
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, FLYING_SPEED)
                .add(Attributes.ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonAttributesConfig.attackDamage.get()));
    }

    // Should have same height when sitting/sleeping/standing
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    public int getYawRotationSpeed()
    {
        // Let it rotate instantly when threatening
        return isUsingFlyingNavigator() ? 12 : (getThreateningTimer() > 0 ? 1000 : 75);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(THREATENING_TIMER, -1);
        entityData.define(FLOCKING_X, 0);
        entityData.define(FLOCKING_Y, 0);
        entityData.define(FLOCKING_Z, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("ThreateningTimer",getThreateningTimer());
        nbt.putInt("FlockingX",getFlockingX());
        nbt.putInt("FlockingY",getFlockingY());
        nbt.putInt("FlockingZ",getFlockingZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setThreateningTimer(nbt.getInt("ThreateningTimer"));
        setFlockingX(nbt.getInt("FlockingX"));
        setFlockingY(nbt.getInt("FlockingY"));
        setFlockingZ(nbt.getInt("FlockingZ"));
        this.featherTime = random.nextInt(6000) + 6000;
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    @Override
    public float ageProgressAmount() {
        return WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonBreedingConfig.ageProgress.get()/100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.3F;
    }

    @Override
    public float baseRenderScale() {
        return 0.625f;
    }

    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Override
    public float getRestrictRadius() {
        int radiusRoot = WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonAttributesConfig.homeRadius.get();
        return radiusRoot * radiusRoot;
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    @Override
    public String getDefaultVariant() {
        return "0";
    }

    @Override
    public String determineVariant() {
        return String.valueOf(getRandom().nextInt(5));
    }

    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

    public int getThreateningTimer() {
        return entityData.get(THREATENING_TIMER);
    }
    public void setThreateningTimer(int threateningTimer) {
        entityData.set(THREATENING_TIMER, threateningTimer);
    }

    public int getFlockingX() {
        return entityData.get(FLOCKING_X);
    }
    public void setFlockingX(int flockingX) {
        entityData.set(FLOCKING_X, flockingX);
    }
    public int getFlockingY() {
        return entityData.get(FLOCKING_Y);
    }
    public void setFlockingY(int flockingY) {
        entityData.set(FLOCKING_Y, flockingY);
    }
    public int getFlockingZ() {
        return entityData.get(FLOCKING_Z);
    }
    public void setFlockingZ(int flockingZ) {
        entityData.set(FLOCKING_Z, flockingZ);
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void tick() {
        this.checkJukeboxNearbyPlayersTimer++;
        super.tick();

        if (isRidingPlayer()) {

            // animations are handled by predicateLocomotion
            // This controls riding vs not riding and dragon position

            setDeltaMovement(Vec3.ZERO);
            Player owner = (Player) this.getOwner();

            if (owner == null || ! owner.isAlive()) {
                stopRidingPlayer();
                return;
            }

            if ((owner.isShiftKeyDown() && ! owner.isOnGround() && ! owner.getAbilities().flying) || owner.isFallFlying() || (! speciesCanSwim() && isInWater())) {
                stopRidingPlayer();
                return;
            }

            this.setXRot(0.0f);
            this.setPos(getPosByDragonAndPlayer(owner));
            float yrot = owner.getYRot();
            this.setYRot(yrot);
            this.setYHeadRot(yrot);
        }

        if (getThreateningTimer() < 0) {
            if (this.checkJukeboxNearbyPlayersTimer >= CHECK_JUKEBOX_NEARBY_PLAYERS_INTERVAL) {
                this.checkJukeboxNearbyPlayersTimer = 0;

                // 10% chance per check (per second a player is nearby). Only check within 5 blocks, not entire 10-block restrict radius.
                // They try to avoid players within 8 blocks, so if players are this close it's probably intentional.
                if (getRandom().nextDouble() < 0.1 && getThreatLookTargetPlayer(5) != null) {
                    setThreateningTimer(Integer.MAX_VALUE);
                }
            }
        }

        setThreateningTimer(Math.max(getThreateningTimer()-1,-1)); // if timer is at -1, does nothing

        if (getThreateningTimer() == 0) {
            attackAfterThreat();
        }
        // If flying and not dropping, drop to the ground for threat pose.
        else if (getThreateningTimer() > 0 && this.getNavigation().isDone() && getNavigationType() == NavigationType.FLYING && this.getDeltaMovement().y > -0.1d) {
            this.setDeltaMovement(0, -0.1, 0);
            this.setNavigator(WRDragonEntity.NavigationType.GROUND);
        } 
        // If in water, get out of water, get out of it for threat pose.
        // TODO: add caustic swamp nasty liquid check
        else if (getThreateningTimer() > 0 && isInWater()) {
            Vec3 landPos = LandRandomPos.getPos(this, 15, 7);
            if (landPos != null) {
                this.getNavigation().moveTo(landPos.x, landPos.y, landPos.z, this.getAttributeValue(Attributes.MOVEMENT_SPEED)*1.5d);
            }
            // Also shove it out of water
            setDeltaMovement(0, 0.2, 0);
        }
        else if (getThreateningTimer() > 0 && isOnGround() && this.checkJukeboxNearbyPlayersTimer >= CHECK_JUKEBOX_NEARBY_PLAYERS_INTERVAL) {
            // Check and turn to face player every so often
            this.checkJukeboxNearbyPlayersTimer = 0;
            Player threatLookTarget = getThreatLookTargetPlayer(null);
            if (threatLookTarget != null) {
                float angle = (float) Math.toDegrees(Math.atan2(threatLookTarget.getZ() - getZ(), threatLookTarget.getX() - getX()));
                setYRot(angle);
                getLookControl().setLookAt(threatLookTarget);
            }
        }
    }

    private Player getThreatLookTargetPlayer(Integer modifyRestrictRadius) {
        return this.level.getNearestEntity(
            Player.class,
            THREATEN_CONDITIONS,
            this,
            this.getX(),
            this.getEyeY(),
            this.getZ(),
            new AABB(this.blockPosition()).inflate(modifyRestrictRadius == null ? getRestrictRadius() : modifyRestrictRadius)
        );
    }

    private void attackAfterThreat() {
        // If any player is within restrict radius when timer runs out, jump them
        Player target = getThreatLookTargetPlayer(null);

        if (target != null) {
            this.setTarget(target);
        }

        setThreateningTimer(-1); // reset timer either way
    }

    @Override
    public Vec3 getPosByDragonAndPlayer(Player owner) {
        double offX = 0d;
        double offY = 0d;
        double offZ = 0d;

        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();

        switch (this.getPosOnPlayer()) {
            case 1:
                offX = 0.3;
                offY = 1.4;
                offZ = 0.05d;
                break;
            case 2:
                offY = 1.83;
                offZ = 0.15d;
            case 3:
                offX = -0.3;
                offY = 1.4;
                offZ = 0.05d;
                break;
            default:
                return null;
        }

        if (owner.isShiftKeyDown()) {
            offY -= 0.3;
        }

        // Rotate the offset based on the player's rotation
        Vec3 rotatedOffset = WRMathsUtility.rotateXZVectorByYawAngle(owner.getYRot(), offX, offZ);
        return new Vec3(playerX + rotatedOffset.x, playerY + offY, playerZ + rotatedOffset.z);
    }

    public void aiStep() {
        super.aiStep();
        // Randomly shed feathers when tame
        if (! this.level.isClientSide && this.isTame() && this.isAlive() && ! this.isBaby() && --this.featherTime <= 0) {
            this.playSound(SoundEvents.MOSS_BREAK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Items.FEATHER);
            this.featherTime = this.random.nextInt(6000) + 3000;
        }
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean doHurtTarget(Entity entity)
    {
        if (super.doHurtTarget(entity) && entity instanceof LivingEntity)
        {
            if (!(entity instanceof Player)) { // non-player mobs always get 15 seconds of poison
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20));
                return true;
            }

            // for players, depends on difficulty
            switch (level.getDifficulty())
            {
                case HARD:
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20));
                    break;
                case NORMAL:
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 8 * 20));
                    break;
                case EASY:
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20));
                    break;
                case PEACEFUL:
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    // Poison immunity - copied from Spider
    public boolean canBeAffected(@SuppressWarnings("null") MobEffectInstance pPotioneffect) {
        if (pPotioneffect.getEffect() == MobEffects.POISON) {
           PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, pPotioneffect);
           MinecraftForge.EVENT_BUS.post(event);
           return event.getResult() == Result.ALLOW;
        } else {
           return super.canBeAffected(pPotioneffect);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            setThreateningTimer(-1); // should just attack instead of threaten
            setFlockingX(0);
            setFlockingY(0);
            setFlockingZ(0);
            return true;
        }
        return false;
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }
    @Override
    public float getFlyingSpeed() {
        return FLYING_SPEED;
    }

    @Override
    public float getStepHeight() {
        return 1;
    }

    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return false;
    }

    @Override
    // Override normal dragon body controller to allow rotations while sitting: its small enough for it, why not. :P
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }

    // ====================================
    //      D) Taming
    // ====================================

    @SuppressWarnings("null")
    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {

        if (tamer.level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (! isTame() && stack.is(Items.SWEET_BERRIES) && isInOverrideAnimation() && getOverrideAnimation().equals("taming")) {
            eat(tamer.getLevel(), stack);
            float tameChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.3f;
            boolean tamed = attemptTame(tameChance, tamer);
            if (tamed) {
                this.playSound(SoundEvents.CAT_PURREOW, 2f, 1.5f);
                getAttribute(MAX_HEALTH).setBaseValue(WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonAttributesConfig.maxHealth.get());
                heal((float)getAttribute(MAX_HEALTH).getBaseValue());
                setThreateningTimer(-1);
                setFlockingX(0);
                setFlockingY(0);
                setFlockingZ(0);
            } else {
                // give player 5 extra seconds for each fed berry
                setThreateningTimer(getThreateningTimer()+100);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public InteractionResult breedLogic(Player breeder, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (this.isOnGround() && !this.isUnderWater()) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int hatchTime() {
        return WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonBreedingConfig.hatchTime.get();
    }

    @Override
    public int getBreedingLimit() {
        return WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonBreedingConfig.breedLimit.get();
    }

    @Override
    public int getMaxBreedingCooldown() {
        return WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.dragonBreedingConfig.maxBreedingCooldown.get();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.SWEET_BERRIES;
    }

    @Override
    @SuppressWarnings({ "ConstantConditions", "null" })
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.SWEET_BERRIES;
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public Vec2 getTomeDepictionOffset() {
        return new Vec2(0,8);
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_CANARI_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@SuppressWarnings("null") DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_CANARI_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_CANARI_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 0.8f;
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(1, new WRRidePlayerGoal(this));
        goalSelector.addGoal(2, new WRAnimatedFloatGoal(this));
        goalSelector.addGoal(3, new WRSwimToSurfaceGoal(this, 1));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new CanariThreatenGoal(this));
        goalSelector.addGoal(6, new WRDragonBreedGoal<>(this));
        goalSelector.addGoal(7, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(8, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(9, new CanariDanceGoal(this));
        goalSelector.addGoal(10, new WRSleepGoal(this));
        goalSelector.addGoal(11, new WRSitGoal(this));
        goalSelector.addGoal(12, new WRGetDroppedFoodGoal(this, 12, true));
        goalSelector.addGoal(13, new AvoidEntityGoal<>(this, Player.class, THREATEN_PREDICATE, (isHatchling() ? 15f : 8f), 1.15D, 1.2D, entity -> true) {
            @Override
            public boolean canUse() {
                return !isTame() && getThreateningTimer() == -1 && super.canUse();
            }
        });
        goalSelector.addGoal(14, new WRIdleGoal(this));
        goalSelector.addGoal(15, new WRFlockFlyAwayGoal(this, 25, 35, 7));
        goalSelector.addGoal(16, new WRReturnToFlockGoal(this, 12) { // Need some overrides so this goal doesn't trigger when flock flying or threatening player
            @Override
            public boolean canUse() {
                return !(getFlockingX() != 0 || getFlockingY() != 0 || getFlockingZ() != 0
                || getThreateningTimer() > 0) && super.canUse();
            }
            @Override
            public boolean canContinueToUse() {
                return !(getFlockingX() != 0 || getFlockingY() != 0 || getFlockingZ() != 0
                || getThreateningTimer() > 0) && super.canContinueToUse();
            }
        });
        goalSelector.addGoal(17, new WRReturnToGroundIfIdleGoal(this));
        goalSelector.addGoal(18, new WRMoveToLandToSleepGoal(this, false));
        goalSelector.addGoal(19, new WRRandomWalkingGoal(this, 1));
        goalSelector.addGoal(20, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(21, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]) {
            @Override
            protected double getFollowDistance() {
                return (double) getRestrictRadius();
            }
        }.setAlertOthers(new Class[0])); // Like wolves, alert friends if hurt
        targetSelector.addGoal(4, new WRDefendHomeGoal(this));
    }

    // =====================================================================
    //      F.1) Threaten players that get too close goal
    // =====================================================================
    class CanariThreatenGoal extends AnimatedGoal
    {
        private boolean firstHalfAnimationDone = false;
        private final EntityCanariWyvern dragon;

        public CanariThreatenGoal(EntityCanariWyvern dragon) {
            super(dragon);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            // TODO: When the caustic swamp nasty not-water is added, check it's not in that...
            // powder snow and lava will hurt the canari and reset the threat timer anyways
            return getThreateningTimer() > 0 && ! dragon.isTame() && ! dragon.isLeashed() && ! dragon.isPassenger() && ! dragon.isVehicle() && ! dragon.isInWater() && ! dragon.isUsingFlyingNavigator() && ! dragon.isInOverrideAnimation();
        }

        private boolean checkClosestPlayerBeingNice()
        {
            List<Player> list = this.dragon.level.getNearbyEntities(
                    Player.class,
                    THREATEN_CONDITIONS,
                    this.dragon,
                    this.dragon.getBoundingBox().inflate(this.dragon.getRestrictRadius()));

            for (Player player : list) {

                Item mainItem = player.getMainHandItem().getItem();
                Item offhandItem = player.getOffhandItem().getItem();

                if (! player.isShiftKeyDown()
                    || (mainItem != Items.SWEET_BERRIES && offhandItem != Items.SWEET_BERRIES)
                    || (mainItem instanceof SwordItem || mainItem instanceof AxeItem || mainItem instanceof BowItem || mainItem instanceof TridentItem)
                    || (offhandItem instanceof SwordItem || offhandItem instanceof AxeItem || offhandItem instanceof BowItem || offhandItem instanceof TridentItem))
                {
                    // Some nearby player is scaring canari or just didn't get away in time, jump them
                    dragon.setThreateningTimer(-1);
                    dragon.setTarget(player);
                    return false;
                }
            }
            return true;
        }

        @Override
        public void start()
        {
            super.start("taming", AnimatedGoal.HOLD_ON_LAST_FRAME, 120); // 12-second animation

            // we don't have a threaten sound... this is a substitute
            dragon.playSound(SoundEvents.BEE_LOOP_AGGRESSIVE, 1f, 1.5F);
            dragon.playSound(SoundEvents.WOLF_GROWL, 5f, 3F);

            dragon.setThreateningTimer(240); // 12 seconds, but entity ticks are twice as fast as goal ticks...
        }

        @Override
        public boolean canContinueToUse() {
            return dragon.getThreateningTimer() > 0 && dragon.getTarget() == null;
        }

        @Override
        public void tick()
        {
            if (! firstHalfAnimationDone && dragon.getThreateningTimer() <= 120) {
                firstHalfAnimationDone = true;
                // before dropping threat a little, check if players aren't being scary
                if (checkClosestPlayerBeingNice()) {
                    // Player now has 6 seconds to tame wyvern or run away (+5 per sweet berry fed)
                    dragon.setThreateningTimer(120);
                    dragon.playSound(WRSounds.ENTITY_CANARI_IDLE.get(), 2f, 1F);
                } else {
                    // checkClosestPlayerBeingNice will set target if it returns false
                    super.stop();
                    return;
                }
            }

            getNavigation().stop();
        }

        @Override
        public void stop() {
            super.stop();
            dragon.setThreateningTimer(-1);
            firstHalfAnimationDone = false;
        }
    }

    // =====================================================================
    //      F.2) Dance when jukebox plays
    // =====================================================================
    class CanariDanceGoal extends AnimatedGoal
    {
        private final EntityCanariWyvern dragon;
        private BlockPos jukeboxPos;

        public CanariDanceGoal(EntityCanariWyvern dragon) {
            super(dragon);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
            this.dragon = dragon;
        }

        @Override
        public boolean canUse() {
            if (dragon.checkJukeboxNearbyPlayersTimer > 1 || ! dragon.isIdling() || dragon.getThreateningTimer() >= 0) {
                return false;
            }
            return isJukeboxPlayingNearby(dragon.blockPosition(), (int)(2*dragon.getRestrictRadius()), 5);
        }

        // Utility for canUse()
        private boolean isJukeboxPlayingNearby(BlockPos mobPos, int searchRadius, int heightRange) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int dy = -heightRange; dy <= heightRange; dy++) {
                for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                    for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                        mutablePos.setWithOffset(mobPos, dx, dy, dz);
                        if (level.getBlockEntity(mutablePos) instanceof JukeboxBlockEntity && ! ((JukeboxBlockEntity) level.getBlockEntity(mutablePos)).getRecord().isEmpty()) {
                            this.jukeboxPos = mutablePos;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private int checkJukebox() {
            if (this.jukeboxPos == null
                || ! dragon.level.getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX)
                || ((JukeboxBlockEntity) level.getBlockEntity(this.jukeboxPos)).getRecord().isEmpty())
            {
                this.jukeboxPos = null;
                return 0;
            }
            if (dragon.distanceToSqr(this.jukeboxPos.getX(), this.jukeboxPos.getY(), this.jukeboxPos.getZ()) > 25) {
                // need to move to jukebox
                return 1;
            }
            return 2;
        }

        @Override
        public void start() {
            this.dragon.setSleeping(false);
            if (checkJukebox() == 1) {
                dragon.getNavigation().moveTo(this.jukeboxPos.getX(), this.jukeboxPos.getY(), this.jukeboxPos.getZ(), 1);
            } else {
                super.start("dance", AnimatedGoal.LOOP, 10); // 1-second animation loop
            }
        }

        @Override
        public void tick() {
            super.tick();
            int jukeCheck = checkJukebox();
            switch (jukeCheck) {
                case 0:
                    super.stop();
                    return;
                case 1:
                    dragon.getNavigation().moveTo(this.jukeboxPos.getX(), this.jukeboxPos.getY(), this.jukeboxPos.getZ(), 1.2);
                    break;
                case 2:
                    if (! dragon.isInOverrideAnimation()) {
                        super.start("dance", AnimatedGoal.LOOP, 10); // 1-second animation loop
                    }
                    break;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return checkJukebox() != 0 && dragon.isIdling() && dragon.getThreateningTimer() < 0;
        }
    }

}

// TODO - reuse this attack animation logic in WRDragonEntity if applicable. Delete this once that works.

/*
    public class AttackGoal extends Goal
    {
        private int repathTimer = 10;
        private int attackDelay = 0;

        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && isWithinRestriction(target.blockPosition()) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(target);
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();

            if ((++repathTimer >= 10 || getNavigation().isDone()) && getSensing().hasLineOfSight(target))
            {
                repathTimer = 0;
                if (!isUsingFlyingNavigator()) setNavigator(NavigationType.FLYING);
                getNavigation().moveTo(target.getX(), target.getBoundingBox().maxY - 2, target.getZ(), 1);
                getLookControl().setLookAt(target, 90, 90);
            }

            if (--attackDelay <= 0 && distanceToSqr(target.position().add(0, target.getBoundingBox().getYsize(), 0)) <= 2.25 + target.getBbWidth())
            {
                attackDelay = 20 + getRandom().nextInt(10);
                swing(InteractionHand.MAIN_HAND);
                //AnimationPacket.send(EntityCanariWyvern.this, ATTACK_ANIMATION);
                doHurtTarget(target);
            }
        }

        @Override
        public void stop()
        {
            repathTimer = 10;
            attackDelay = 0;
        }
    }
*/