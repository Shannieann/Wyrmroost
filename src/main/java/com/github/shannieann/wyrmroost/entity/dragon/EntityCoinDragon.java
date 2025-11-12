package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.item.CoinDragonItem;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;

import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRMoveToHomeGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRRandomWalkingGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSleepGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRRandomLiftOffGoal;

import net.minecraft.world.entity.item.ItemEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import javax.annotation.Nullable;

// Simple Entity really, just bob and down in the same spot, and land to sleep at night. Easy.
public class EntityCoinDragon extends WRDragonEntity
{

    private static final float FLYING_SPEED = 0.02f;
    private static final float MOVEMENT_SPEED = 0.1f;
    private static final float MAX_HEALTH = 4.0f;

    private static final String[] VARIANTS = {"blue", "gold", "gray", "green", "pink", "red"};

    public EntityCoinDragon(EntityType<? extends EntityCoinDragon> coindragon, Level level) {
        super(coindragon, level);
        this.setHomePos(blockPosition()); // Set home to spawn position
    }

    // ====================================
    //      Animations
    // ====================================

    // Simplify, plus make sure walking animation is actually only used when walking
    @Override
    public <E extends IAnimatable> PlayState predicateAnimation(AnimationEvent<E> event) {

        // Every "override" animation should completely replace regular animations
        // ex: rooststalker scavenge, canari threaten, canari dance
        // UNLESS it is attack or idle, which can be played over regular animations
        if (this.isInOverrideAnimation()) {
            String currentAnim = this.getOverrideAnimation();
            // Actually set the animation on the controller so it plays
            if (currentAnim.contains("sit_down") || currentAnim.contains("lay_down")) {
                System.out.println("adding base ground for special animation: " + currentAnim);
                // This needs to be layered over base ground specifically
                event.getController().setAnimation(new AnimationBuilder().addAnimation("base_ground", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
            else if (!currentAnim.contains("idle")) {
                // Everything except idle and attack animations uses regular bones and shouldn't be layered over anything
                System.out.println("Playing regular bone special animation: " + currentAnim);
                switch (this.getAnimationType()) {
                    case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim, ILoopType.EDefaultLoopTypes.LOOP));
                    case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim, ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim,ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
                System.out.println("Animation override: " + currentAnim + " is not idle, no layer");
                return PlayState.CONTINUE;
            }
            else {
                // Must be idle or attack animation. Choose regular bone animation as normal
            }
        }

        if (this.getSleeping()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("sleep", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        NavigationType navigationType = this.getNavigationType();

        if (navigationType == NavigationType.GROUND) {
            if (this.getDeltaMovement().length() > (this.getMovementSpeed()/3)) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("base_ground", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        else if (navigationType == NavigationType.FLYING) {
            Vec3 deltaMovement = this.getDeltaMovement();
            if (Math.abs(deltaMovement.x()) > 0.05 || Math.abs(deltaMovement.z()) > 0.05) { // If it's flying even slightly horizontal
                event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
            } else { // If it's flying almost entirely vertical
                event.getController().setAnimation(new AnimationBuilder().addAnimation("base_fly", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }

        // Should never get here
        return PlayState.STOP;
    }

    private int numGroundIdleAnimationVariants() {
        return 3;
    }

    private int getGroundIdleAnimationTime(int index) {
        int[] animationTimesInOrder = {20, 50, 40}; // Time in seconds is 2, 5, 4
        return animationTimesInOrder[index];
    }

    private int numFlyingIdleAnimationVariants() {
        return 2;
    }

    private int getFlyingIdleAnimationTime(int index) {
        int[] animationTimesInOrder = {30, 40}; // Time in seconds is 3, 4
        return animationTimesInOrder[index];
    }

    @Override
    public int numAttackAnimationVariants() {
        return 0;
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(Attributes.FLYING_SPEED, EntityCoinDragon.FLYING_SPEED)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.MAX_HEALTH, MAX_HEALTH);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        InteractionResult stackResult = player.getItemInHand(hand).interactLivingEntity(player, this, hand);
        if (stackResult.consumesAction()) {
            return stackResult;
        }

        if (player.isShiftKeyDown()) {
            ItemEntity itemEntity = new ItemEntity(level, getX(), getY(), getZ(), getItemStack());
            double x = player.getX() - getX();
            double y = player.getY() - getY();
            double z = player.getZ() - getZ();
            itemEntity.setDeltaMovement(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
            level.addFreshEntity(itemEntity);
            remove(RemovalReason.DISCARDED);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size)
    {
        return size.height * 0.8645f;
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(WRItems.COIN_DRAGON.get());
    }

    @Override
    public boolean requiresCustomPersistence()
    {
        return true;
    }

    public ItemStack getItemStack()
    {
        ItemStack stack = new ItemStack(WRItems.COIN_DRAGON.get());
        CompoundTag entityData = serializeNBT();
        stack.getOrCreateTag().put(CoinDragonItem.DATA_ENTITY, entityData);
        if (hasCustomName()) stack.setHoverName(getCustomName());
        return stack;
    }

    // Should have same height when sitting/sleeping/standing
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    // Always adult
    public boolean isHatchling() {
        return false;
    }

    public boolean isJuvenile() {
        return false;
    }

    public boolean isAdult() {
        return true;
    }

    public float ageProgressAmount() {
        return 0.0F;
    }

    @Override
    public float initialBabyScale() {
        return 0.5F;
    }

    @Override
    public float baseRenderScale() {
        return 0.5f;
    }

    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

    @Override
    public float getRestrictRadius() {
        return WRServerConfig.SERVER.ENTITIES.COIN_DRAGON.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.COIN_DRAGON.dragonAttributesConfig.homeRadius.get();
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    @Override
    public String getDefaultVariant() {
        return "gray";
    }

    @Override
    public String determineVariant() {
        return VARIANTS[this.getRandom().nextInt(VARIANTS.length)];
    }

    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

    @Override
    public boolean hasGender() {
        return false; // Coin dragons don't have gender
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || source == DamageSource.FALL || super.isInvulnerableTo(source);
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public float getMovementSpeed() {
        return -1; // Can't walk
    }
    @Override
    public float getFlyingSpeed() { 
        return FLYING_SPEED;
    }

    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    public boolean speciesCanWalk() {
        return false;
    }

    @Override
    public boolean dragonCanFly() {
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
    public float getStepHeight() {
        return 0.5f;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder()
    {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // no fall damage
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public void doSpecialEffects() {
        // Not applicable
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return new Vec2(0, 0);
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.get(); // TODO: hurt sound
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.get(); // TODO: death sound
    }

    @Override
    public float getSoundVolume() {
        return 0.8f;
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack) {
        return false; // Coin dragons don't eat
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(1, new WRSleepGoal(this));
        goalSelector.addGoal(2, new CoinDragonIdleGoal(this));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(4, new WRRandomWalkingGoal(this, 1));
        goalSelector.addGoal(5, new WRRandomLiftOffGoal(this, 3, 0.05)); // fly_transition is takeoff??
        goalSelector.addGoal(6, new CoinDragonReturnToGroundGoal(this));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        goalSelector.addGoal(8, new CoinDragonBobUpAndDownGoal(this));
    }

    // =====================================================================
    //      F.1) Coin Dragon special idle goal for separate fly/ground idles
    // =====================================================================
    class CoinDragonIdleGoal extends AnimatedGoal
    {
        public CoinDragonIdleGoal(EntityCoinDragon dragon) {
            super(dragon);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.entity.getRandom().nextFloat() < 0.015
                    && this.entity.getNavigation().isDone()
                    && ! this.entity.getSleeping()
                    && ! this.entity.isPassenger();
        }
    
        @Override
        public void start(){
            if (this.entity.isOnGround()) {
                int randInt = entity.getRandom().nextInt(((EntityCoinDragon) entity).numGroundIdleAnimationVariants());
                super.start(("idle"+(randInt+1)), 3, ((EntityCoinDragon) entity).getGroundIdleAnimationTime(randInt));
            } else {
                int randInt = entity.getRandom().nextInt(((EntityCoinDragon) entity).numFlyingIdleAnimationVariants());
                super.start(("idle_fly"+(randInt+1)), 3, ((EntityCoinDragon) entity).getFlyingIdleAnimationTime(randInt));
            }
        }

        @Override
        public boolean canContinueToUse(){
            return this.entity.getNavigation().isDone()
                    && ! this.entity.isPassenger()
                    && super.canContinueToUse();
        }

    }

    // =====================================================================
    //      F.2) Coin Dragon return to ground goal
    // (Not sure if flight threshold from WRReturnToGroundIfIdleGoal would work for this?
    // Made a simpler version for coin dragon. Also massively increased probability)
    // =====================================================================
    class CoinDragonReturnToGroundGoal extends Goal
    {
        private final EntityCoinDragon entity;
        public CoinDragonReturnToGroundGoal(EntityCoinDragon dragon) {
            this.entity = dragon;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.entity.getRandom().nextDouble() < 0.01
                    && this.entity.getNavigationType() == NavigationType.FLYING
                    && this.entity.getNavigation().isDone()
                    && ! this.entity.isPassenger();
        }

        @Override
        public void tick() {
            this.entity.setDeltaMovement(0, -0.05, 0);
            this.entity.setNavigator(NavigationType.GROUND);
        }
        
        @Override
        public boolean canContinueToUse() {
            return ! this.entity.isPassenger() && ! this.entity.isOnGround();
        }
    }

    // =====================================================================
    //      F.3) Coin Dragon bob up and down goal when flying/idle goal
    // =====================================================================
    class CoinDragonBobUpAndDownGoal extends Goal
    {
        private final EntityCoinDragon dragon;

        public CoinDragonBobUpAndDownGoal(EntityCoinDragon dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return dragon.getRandom().nextDouble() < 0.01
                    && dragon.getNavigationType() == NavigationType.FLYING
                    && dragon.getNavigation().isDone()
                    && !dragon.isPassenger();
        }

        @Override
        public boolean canContinueToUse() {
            return dragon.getNavigation().isDone()
                    && !dragon.isPassenger()
                    && dragon.getNavigationType() == NavigationType.FLYING
                    && super.canContinueToUse()
                    && dragon.getRandom().nextDouble() > 0.0005; // very small chance to randomly stop, just so this
                                                                 // doesn't go on forever
        }

        @Override
        public void tick() {
            double yMot;
            double altitiude = dragon.getAltitude();
            if (altitiude < 1) {
                yMot = dragon.getFlyingSpeed();
            } else if (altitiude > 2.5) {
                yMot = -dragon.getFlyingSpeed();
            } else {
                yMot = Math.sin(dragon.tickCount * 0.1) * 0.0035;
            }

            dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, yMot, 0));
            dragon.move(MoverType.SELF, dragon.getDeltaMovement());
            dragon.setDeltaMovement(dragon.getDeltaMovement().scale(0.91));
        }

        @Override
        public void stop() {
            dragon.getNavigation().stop();
        }
    }
}
