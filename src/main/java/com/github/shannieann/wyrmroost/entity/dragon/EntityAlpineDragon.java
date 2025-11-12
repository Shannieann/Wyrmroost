package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRRandomFlyWalkGoal;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.registry.WRSounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

import java.util.Map;

import javax.annotation.Nullable;

public class EntityAlpineDragon extends WRDragonEntity implements ITameable, IBreedable {

    public static final int MAX_BREEDING_COOLDOWN = 12000; // 600 seconds, override
    private static final float MOVEMENT_SPEED = 0.22f;
    private static final float FLYING_SPEED = 0.185f;

    // =========================
    // A. Entity Data + Attributes
    // =========================

    public EntityAlpineDragon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNavigator(NavigationType.GROUND);
    }

    // ====================================
    //      Animations
    // ====================================

    @Override
    public int numIdleAnimationVariants() {
        return 0; // Only has sit idle animations?
    }

    @Override
    public int getIdleAnimationTime(int index) {
        int[] animationTimesInOrder = {};
        return animationTimesInOrder[index];
    }

    @Override
    public int numAttackAnimationVariants() {
        return 3;
    }

    @Override
    public int getAttackAnimationTime(int index) {
        int[] animationTimesInOrder = {17, 17, 17}; // all rounded up from 16.666 ticks
        return animationTimesInOrder[index];
    }

    public int getLieDownTime() { // 2.1667 seconds
        return 44;
    }

    public int getSitDownTime() { // 1 second
        return 20;
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.maxHealth.get())
                .add(Attributes.MOVEMENT_SPEED, EntityAlpineDragon.MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, EntityAlpineDragon.FLYING_SPEED)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.attackDamage.get())
                .add(FOLLOW_RANGE, 50);
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    @Override
    public float ageProgressAmount() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.ageProgress.get() / 100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.1F;
    }

    // ====================================
    //      A.2) Entity Data: INVENTORY
    // ====================================

    @Override
    public boolean canEquipSaddle() {
        return true;
    }

    @Override
    public boolean canEquipArmor() {
        return true;
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
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get();
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    @Override
    public String getDefaultVariant() {
        return "black";
    }

    @SuppressWarnings("null")
    @Override
    public String determineVariant() {
        final String variant;
        if (getRandom().nextDouble() < 0.02) {
            this.getAttribute(ATTACK_DAMAGE).setBaseValue(40D);
            variant = "special";
        } else {
            switch (getRandom().nextInt(6)) {
                case 1 -> variant = "blue";
                case 2 -> variant = "green";
                case 3 -> variant = "red";
                case 4 -> variant = "white";
                case 5 -> variant = "yellow";
                default -> variant = getDefaultVariant();
            }
        }
        return variant;
    }

/*
    @Override
    public ResourceLocation getClosedEyesTexture() {
        return CLOSED_EYE_TEXTURE_MAP.entrySet().stream()
            .filter(entry -> getVariant().contains(entry.getKey()))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse(WRDragonEntity.BLANK_EYES);
    }

    @Override
    public ResourceLocation getEyesTexture() {
        return isAlbino() ? EYES_SPECIAL : EYES;
    }

    @Override
    public ResourceLocation getBehaviorEyesTexture() {
        if (getSleeping() || getRandom().nextFloat() < 0.015) {
            return getClosedEyesTexture();
        }
        return getEyesTexture();
    }
*/

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean doHurtTarget(Entity enemy) {
        boolean flag = super.doHurtTarget(enemy);
        if (!isTame() && flag && !enemy.isAlive() && enemy.getType() == EntityType.BEE) {
            Bee bee = (Bee) enemy;
            if (bee.hasNectar() && bee.isLeashed()) {
                Entity holder = bee.getLeashHolder();
                if (holder instanceof Player) {
                    attemptTame(1, (Player) holder);
                }
            }
        }
        return flag;
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public float getStepHeight() {
        return 2;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }
    @Override
    public float getFlyingSpeed() {
        return FLYING_SPEED;
    }

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    public boolean dragonCanFly() {
        // TODO: Can babies fly?
        return true;
    }


    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger) && isJuvenile();
    }

    @Override
    public int getMaxPassengers() {
        return 1;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, this.getType().getDimensions().height * 0.95D, index == 1 ? -2 : 0);
    }

    @Override
     public float getFlyAccelModifier() {
        return 1.5f;
    }

    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.HONEYCOMB) || stack.is(Items.HONEY_BOTTLE) || stack.is(Items.HONEY_BLOCK);
    }

    @Override
    public InteractionResult breedLogic(Player breeder, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (((this.isOnGround() && !this.isUnderWater()) && this.isAdult()) && isFood(stack)) {
            eat(this.level, stack);
            setBreedingCooldown(6000);
            setBreedingCount(getBreedingCount() + 1);
            setInLove(breeder);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int hatchTime() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.breedLimit.get();
    }

    @Override
    public int getBreedingLimit() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.hatchTime.get() * 20;
    }

    @Override
    public int getMaxBreedingCooldown() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.maxBreedingCooldown.get();
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public Vec2 getTomeDepictionOffset() {
        return null;
    }

    @Override
    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView) {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-10, this, player), 0, 0);
        } else {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.3, 0);
        }
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_ALPINE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_ALPINE_HURT.get();
    }

    @Nullable
    protected SoundEvent getRoarSound() {
        return WRSounds.ENTITY_ALPINE_ROAR.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_ALPINE_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 1.0f;
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(2, new WRDragonBreedGoal<>(this));
        goalSelector.addGoal(3, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(4, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(5, new WRSitGoal(this));
        goalSelector.addGoal(6, new WRGetDroppedFoodGoal(this, 10, true));
        goalSelector.addGoal(7, new WRSleepGoal(this));
        goalSelector.addGoal(8, new WRIdleGoal(this));
        goalSelector.addGoal(9, new WRRandomFlyWalkGoal(this, 30, 10));
        goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 14f, 1));
        goalSelector.addGoal(12, new WRRandomLookAroundGoal(this,45));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]) {
            @Override
            protected double getFollowDistance() {
                return (double) getRestrictRadius();
            }
        }.setAlertOthers(new Class[0]));
        targetSelector.addGoal(4, new WRDefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                entity -> {
                    if (entity instanceof Bee || entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Spider) {
                        return true;
                    }
                    return false;
                })
        );

    }
}