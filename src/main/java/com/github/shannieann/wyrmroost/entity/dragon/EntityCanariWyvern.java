package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
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
    public int numIdleAnimationVariants(){
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
        return 10;
    }

    public int getSitDownTime() {
        return 10;
    }

    // Canari has a swim animation
    public boolean notAquaticShouldUseSwimAnimation() {
        return isInWater();
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
        return isUsingFlyingNavigator()? 12 : 75;
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

    @Override
    public void setSitting(boolean sit) {
        super.setSitting(sit);
        if (sit) {
            setFlockingX(0);
            setFlockingY(0);
            setFlockingZ(0);
        }
    }

    @Override
    public void setSleeping(boolean sleep) {
        super.setSleeping(sleep);
        if (sleep) {
            setFlockingX(0);
            setFlockingY(0);
            setFlockingZ(0);
        }
    }

    @Override
    public void setHomePos(@Nullable BlockPos pos) {
        super.setHomePos(pos);
        if (pos != null) {
            setFlockingX(0);
            setFlockingY(0);
            setFlockingZ(0);
        }
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void tick() {
        this.checkJukeboxNearbyPlayersTimer++;
        super.tick();

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
                offZ = 0.1d;
                break;
            case 2:
                offY = 1.83;
                offZ = 0.2d;
            case 3:
                offX = -0.3;
                offY = 1.4;
                offZ = 0.1d;
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
            if (!(entity instanceof Player)) { // other mobs get full poison
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20));
                return true;
            }

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

        if (!isTame() && stack.is(Items.SWEET_BERRIES) && getAnimation().equals("taming")) {
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
        goalSelector.addGoal(2, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(4, new CanariThreatenGoal(this));
        goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Player.class, THREATEN_PREDICATE, (isHatchling() ? 15f : 8f), 1.15D, 1.2D, entity -> true) {
            @Override
            public boolean canUse() {
                return !isTame() && getThreateningTimer() == -1 && super.canUse();
            }
        });
        goalSelector.addGoal(6, new WRDragonBreedGoal<>(this));
        goalSelector.addGoal(7, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(8, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(9, new WRSitGoal(this));
        goalSelector.addGoal(10, new CanariDanceGoal(this));
        goalSelector.addGoal(11, new WRGetDroppedFoodGoal(this, 15, true));
        goalSelector.addGoal(12, new WRSleepGoal(this));
        goalSelector.addGoal(13, new WRIdleGoal(this));
        goalSelector.addGoal(14, new CanariFlockFlyAwayGoal(this, 25, 35));
        goalSelector.addGoal(15, new CanariReturnToFlockGoal(this, 12));
        goalSelector.addGoal(16, new WRReturnToGroundIfIdleGoal(this));
        goalSelector.addGoal(17, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(18, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(19, new RandomLookAroundGoal(this));

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
            return getThreateningTimer() > 0 && ! dragon.isTame() && ! dragon.isLeashed() && ! dragon.isPassenger() && ! dragon.isVehicle() && ! dragon.isInWater() && ! dragon.isUsingFlyingNavigator() && ! dragon.getAnimationInOverride();
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
            dragon.playSound(SoundEvents.BEE_LOOP_AGGRESSIVE, 1, 1.5F);
            dragon.playSound(SoundEvents.WOLF_GROWL, 3, 2F);
            dragon.playSound(SoundEvents.PARROT_HURT, 2, 0.8F);

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
    //      F.2) Fly away as a group goal
    // If more dragons that do this are added, move this to its own file
    // and find a way to convert it to work for any one species.
    // May need to add the flocking X, Y, Z synced ints to the base class for that.
    // =====================================================================
    public class CanariFlockFlyAwayGoal extends Goal {

        private final EntityCanariWyvern dragon;
        private int minDistance;
        private int maxDistance;
        private boolean abortGoal;
        private int giveUpTimer;

        public CanariFlockFlyAwayGoal(EntityCanariWyvern dragon, int minDistance, int maxDistance) {
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.dragon = dragon;
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.abortGoal = false;
            this.giveUpTimer = 0;
        }

        @Override
        public boolean canUse() {
            // Very low chance of triggering a flock fly away as the leader. This seems to be about every ~30 seconds.
            boolean canFlockFlyLeader = getRandom().nextInt(10000) < 8
                                        && dragon.getFlockingX() == 0 && dragon.getFlockingY() == 0 && dragon.getFlockingZ() == 0
                                        && dragon.isOnGround() && ! dragon.getAnimationInOverride() && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed()   ;  
            // In start(), the leader will set the flock target. Followers just need to have such a position set. They can be woken up from sleep or interrupted mid-idle or mid-attack.
            boolean canFlockFlyFollower = dragon.getFlockingX() != 0 && dragon.getFlockingY() != 0 && dragon.getFlockingZ() != 0
                                        && dragon.isOnGround() && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
            return canFlockFlyLeader || canFlockFlyFollower;
        }

        @Override
        public void start() {

            if (dragon.getFlockingX() == 0 && dragon.getFlockingY() == 0 && dragon.getFlockingZ() == 0) {
                // This one is the leader: find a fly away position and set it for everyone in flock
                setLeaderFlyAwayPos();
                if (abortGoal) {
                    return;
                }
                setFollowerFlyAwayPos();
            }

            this.dragon.clearAI();
            this.dragon.setAnimationInOverride(false); // force quit idles/special animations
            this.dragon.setSleeping(false);

            this.dragon.playSound(getAmbientSound(), 1.5F, 1.0F);

            this.dragon.getJumpControl().jump();
            this.dragon.setNavigator(NavigationType.FLYING);
            this.dragon.getNavigation().createPath(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
            if (this.dragon.getNavigation().getPath() == null) {
                this.dragon.getNavigation().moveTo(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
            }
        }

        private void setLeaderFlyAwayPos() {

            Vec3 pos = null;

            for (int i = 0; i < 10; i++) { // Make ten attempts to find position between min/max range before giving up
                pos = LandRandomPos.getPos(this.dragon, this.maxDistance, 20);
                if (pos != null && this.dragon.distanceToSqr(pos) > this.minDistance * this.minDistance) {
                    break;
                } else {
                    pos = null;
                }
            }

            if (pos == null) {
                this.abortGoal = true;
                return;
            }

            this.dragon.setFlockingX(((int)pos.x));
            this.dragon.setFlockingY((int)pos.y);
            this.dragon.setFlockingZ((int)pos.z);
        }

        private void setFollowerFlyAwayPos() {

            int flyAwayX = this.dragon.getFlockingX();
            int flyAwayY = this.dragon.getFlockingY();
            int flyAwayZ = this.dragon.getFlockingZ();

            for (EntityCanariWyvern mob : this.dragon.level.getEntitiesOfClass(EntityCanariWyvern.class, this.dragon.getBoundingBox().inflate(this.dragon.getRestrictRadius()), entity -> entity != this.dragon)) {
                // Followers get a random position within 3 horizontal blocks of leader, unless they already have one
                if (mob.getFlockingX() == 0 && mob.getFlockingY() == 0 && mob.getFlockingZ() == 0) {
                    mob.setFlockingX(flyAwayX + getRandom().nextInt(7) - 4);
                    mob.setFlockingY(flyAwayY);
                    mob.setFlockingZ(flyAwayZ + getRandom().nextInt(7) - 4);
                }
            }
        }

        @Override
        public void tick() {

            this.giveUpTimer++;

            if (this.dragon.getNavigation().isDone() && ! isNearFlyAwayPos(false)) {
                this.dragon.getNavigation().createPath(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
                if (this.dragon.getNavigation().getPath() == null) {
                    this.dragon.getNavigation().moveTo(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
                }
            }

            // They keep landing for some reason... make them fly unless they're pretty close to target
            if (! isNearFlyAwayPos(true) && this.dragon.getAltitude() < 1.5*this.dragon.getFlightThreshold()) {
                Vec3 direction = new Vec3(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ()).subtract(position()).normalize();
                direction = new Vec3(direction.x, 1, direction.z);
                setDeltaMovement(direction.scale(0.3));
            }
        }

        @Override
        public boolean canContinueToUse() {
            return ! this.abortGoal && this.giveUpTimer < 600 && ! isNearFlyAwayPos(false) && dragon.getFlockingX() != 0 && dragon.getFlockingY() != 0 && dragon.getFlockingZ() != 0
                && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
        }

        private boolean isNearFlyAwayPos(boolean loosenRequirements) {
            if (loosenRequirements) {
                return dragon.distanceToSqr(dragon.getFlockingX(), dragon.getFlockingY(), dragon.getFlockingZ()) < 1.5*dragon.getRestrictRadius();
            }
            return dragon.distanceToSqr(dragon.getFlockingX(), dragon.getFlockingY(), dragon.getFlockingZ()) < dragon.getRestrictRadius();
        }

        @Override
        public void stop() {
            this.dragon.getNavigation().stop();
            this.dragon.setFlockingX(0);
            this.dragon.setFlockingY(0);
            this.dragon.setFlockingZ(0);

            this.abortGoal = false;
            this.giveUpTimer = 0;
        }
    }

    // =====================================================================
    // F.3) Goal to stay in a flock together when not tamed.
    // Also give this its own file if we get more flocking dragons
    // =====================================================================
    public class CanariReturnToFlockGoal extends Goal {

        private EntityCanariWyvern farthestFlockMember;
        private final EntityCanariWyvern dragon;
        private int timer = 0;
        private double distanceToFarthestFlockMember;
        private int chance; // Has to trigger at a really specific frequency to look natural. 5/1000 to 15/1000 chance per tick seems good.

        public CanariReturnToFlockGoal(EntityCanariWyvern dragon, int chance) {
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            this.dragon = dragon;
            this.farthestFlockMember = null;
            this.distanceToFarthestFlockMember = 0;
            this.chance = chance;
        }

        @Override
        public boolean canUse() {
            if (dragon.getRandom().nextInt(1000) > this.chance 
                || dragon.getFlockingX() != 0 || dragon.getFlockingY() != 0 || dragon.getFlockingZ() != 0
                || dragon.getThreateningTimer() > 0 || dragon.getTarget() != null || dragon.isTame() || dragon.isLeashed() || dragon.getSleeping())
            {
                return false;
            }

            for (EntityCanariWyvern mob : this.dragon.level.getEntitiesOfClass(EntityCanariWyvern.class, this.dragon.getBoundingBox().inflate(2*this.dragon.getRestrictRadius()), entity -> entity != this.dragon)) {
                if (this.farthestFlockMember == null || this.dragon.distanceToSqr(mob) > this.distanceToFarthestFlockMember) {
                    this.distanceToFarthestFlockMember = this.dragon.distanceToSqr(mob);
                    this.farthestFlockMember = mob;
                }
            }
            if (this.farthestFlockMember != null && this.distanceToFarthestFlockMember > dragon.getRestrictRadius()) {
            }
            return this.farthestFlockMember != null;
        }

        @Override
        public void start() {
            // Can get stuck in water, etc. Give it a shove into the air
            if (this.dragon.dragonCanFly() && this.dragon.canLiftOff()) {
                Vec3 direction = new Vec3(this.farthestFlockMember.getX(), this.farthestFlockMember.getY(), this.farthestFlockMember.getZ()).subtract(position()).normalize();
                direction = new Vec3(direction.x, 1, direction.z);
                setDeltaMovement(direction.scale(0.3));
            }
        }

        @Override
        public void tick() {
            this.timer++;
            dragon.getNavigation().moveTo(farthestFlockMember, 1f);
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer < 400 && farthestFlockMember != null
                && dragon.getFlockingX() == 0 && dragon.getFlockingY() == 0 && dragon.getFlockingZ() == 0
                && dragon.getThreateningTimer() < 0 && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
        }

        @Override
        public void stop() {
            this.timer = 0;
            this.farthestFlockMember = null;
            this.dragon.getNavigation().stop();
        }

    }

    // =====================================================================
    //      F.4) Dance when jukebox plays
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
                    if (! dragon.getAnimationInOverride()) {
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