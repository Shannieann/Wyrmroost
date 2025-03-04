package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRRandomSwimmingGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRReturnToWaterGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRWaterLeapGoal;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.entity.dragon_egg.WRDragonEggEntity;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EntityAlpineDragon extends WRDragonEntity implements ITameable, IBreedable {

    // =========================
    // A. Entity Data + Attributes
    // =========================
    public final LerpedFloat sitTimer = LerpedFloat.unit();

    public EntityAlpineDragon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNavigator(NavigationType.GROUND);
    }

    @Override
    public int idleAnimationVariants() {
        return 0;
    }

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.maxHealth.get())
                .add(MOVEMENT_SPEED, 0.22)
                .add(FLYING_SPEED, 0.185f)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.attackDamage.get())
                .add(FOLLOW_RANGE, 50);
    }

    @Override
    public float ageProgressAmount() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.ageProgress.get() / 100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.1F;
    }

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Override
    public float getRestrictRadius() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get();
    }

    @Override
    public String getDefaultVariant() {
        return "black";
    }

    @Override
    public String determineVariant() {
        if (getRandom().nextDouble() < 0.02) {
            this.getAttribute(ATTACK_DAMAGE).setBaseValue(40D);
            return "special";
        }
        return switch (getRandom().nextInt(6)) {
            case 1 -> "blue";
            case 2 -> "green";
            case 3 -> "red";
            case 4 -> "white";
            case 5 -> "yellow";
            default -> getDefaultVariant();
        };
    }

    // =========================
    // B. Tick and AI
    // =========================
    @Override
    public void aiStep() {
        super.aiStep();
        sitTimer.add(isInSittingPose() ? 0.1f : -0.1f);
    }

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

    // =========================
    // C. Navigation and Control
    // =========================
    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    @Override
    public float getStepHeight() {
        return 2;
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

    // =========================
    // D. Taming
    // =========================
    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.HONEYCOMB);
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return null;
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

    // =========================
    // E. Client
    // =========================
    @Override
    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView) {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-10, this, player), 0, 0);
        } else {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.3, 0);
        }
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WRSleepGoal(this));
        goalSelector.addGoal(0, new WRSitGoal(this));
//        goalSelector.addGoal(1, new MoveToHomeGoal(this));
//        goalSelector.addGoal(2, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(3, new WRDragonBreedGoal(this));

        goalSelector.addGoal(5, new WRReturnToWaterGoal(this, 1.0,16,12,3));
        goalSelector.addGoal(5, new WRWaterLeapGoal(this, 1,12,30,64));
        goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1));
        //goalSelector.addGoal(7,new WRIdleGoal(this, idleAnimation1Time));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 14f, 1));
        goalSelector.addGoal(9, new WRRandomLookAroundGoal(this,45));

        //targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        //targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        //targetSelector.addGoal(3, new HurtByTargetGoal(this));
        //targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                entity -> {
                    if (entity instanceof Bee) {
                        return true;
                    }

                    if (entity.getClass() == this.getClass() || entity instanceof WRDragonEggEntity) {
                        return false;
                    }

                    if (entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Spider) {
                        return true;
                    }
                    return false;}));
    }
}