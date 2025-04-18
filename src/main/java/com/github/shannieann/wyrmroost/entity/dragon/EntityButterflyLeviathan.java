package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRRandomSwimmingGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRReturnToWaterGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRWaterLeapGoal;
import com.github.shannieann.wyrmroost.entity.dragon_egg.WRDragonEggEntity;
import com.github.shannieann.wyrmroost.network.KeybindHandler;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

//2024.08.26

//ToDo:
// Riding - BFL and aquatics
// Riding - BFL abilities
// BFL attack - fix look distance + head rot
// Conduit
// Armor
// Tidy up EntityTypeRegistry
// Datagen


public class EntityButterflyLeviathan extends WRDragonEntity implements IForgeEntity, IBreedable, ITameable {
    public static final EntityDataAccessor<Boolean> HAS_CONDUIT = SynchedEntityData.defineId(EntityButterflyLeviathan.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> LIGHTNING_COOLDOWN = SynchedEntityData.defineId(EntityButterflyLeviathan.class, EntityDataSerializers.INT);
    public static final int CONDUIT_SLOT = 0;
    public final float entityDeltaPitchLimit = 1.0F;
    public final float entityYawAdjustment = 0.30F;
    public final float entityExtremityPitchAdjustment = 0.01F;
    public final LerpedFloat swimTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public static final String LIGHTNING_STRIKE_ANIMATION = "lightning_strike";
    public static final int LIGHTNING_STRIKE_ANIMATION_TIME = 40;
    public static final int LIGHTNING_STRIKE_ANIMATION_QUEUE = 15;
    public static final String LIGHTNING_FORK_ANIMATION = "lightning_fork";
    public static final int LIGHTNING_FORK_ANIMATION_TIME = 40;
    public static final int LIGHTNING_FORK_ANIMATION_QUEUE = 13;
    public static final String ATTACK_ANIMATION = "attack_";
    public static final int LAND_ATTACK_ANIMATION_TIME_1 = 10;
    public static final int WATER_ATTACK_ANIMATION_TIME_1 = 10;
    public static final int LAND_ATTACK_ANIMATION_TIME_2 = 10;
    public static final int WATER_ATTACK_ANIMATION_TIME_2 = 10;

    public static final int LAND_ATTACK_QUEUE_TIME_1 = 7;
    public static final int WATER_ATTACK_QUEUE_TIME_1 = 7;
    public static final int LAND_ATTACK_QUEUE_TIME_2 = 8;
    public static final int WATER_ATTACK_QUEUE_TIME_2 = 6;
    public final int idleAnimation1Time = 80;

    public EntityButterflyLeviathan(EntityType<? extends WRDragonEntity> entityType, Level level) {
        super(entityType, level);
        setPathfindingMalus(BlockPathTypes.WATER, 0);
        setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0);
        setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
        setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        this.deltaPitchLimit = entityDeltaPitchLimit;
        this.adjustmentYaw = entityYawAdjustment;
        this.adjustmentExtremityPitch = entityExtremityPitchAdjustment;
        this.groundMaxYaw = 5;
        this.setNavigator(NavigationType.SWIMMING);
    }
    @Override
    public int idleAnimationVariants(){
        return 1;
    }


    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_CONDUIT, false);
        entityData.define(LIGHTNING_COOLDOWN, 0);
    }

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonAttributesConfig.maxHealth.get())
                .add(MOVEMENT_SPEED, 0.10F)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonAttributesConfig.attackDamage.get())
                .add(FOLLOW_RANGE, 50);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    @Override
    public float ageProgressAmount(){
        return WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonBreedingConfig.ageProgress.get()/100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.1F;
    }

    @Override
    public float baseRenderScale() {
        return 2.4f;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pReason) {
        return true;
    }


    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, Random random) {
        if (reason == MobSpawnType.SPAWNER) return true;
        if (level.getFluidState(pos).is(FluidTags.WATER)) {
            final double chance = random.nextDouble();
            if (reason == MobSpawnType.CHUNK_GENERATION) return chance < 0.325;
            else if (reason == MobSpawnType.NATURAL) return chance < 0.001;
        }
        return false;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    public boolean hasConduit() {
        return entityData.get(HAS_CONDUIT);
    }

    public Vec3 getConduitPos() {
        return getEyePosition(1)
                .add(0, 0.4, 0.35)
                .add(calculateViewVector(xRot, yHeadRot).scale(4.15));
    }

    public int getLightningAttackCooldown() {
        return entityData.get(LIGHTNING_COOLDOWN);
    }

    public void setLightningAttackCooldown(int cooldown) {
        entityData.set(LIGHTNING_COOLDOWN,cooldown);
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
        return WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonAttributesConfig.homeRadius.get();
    }
    
    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================


    @Override
    public String getDefaultVariant() {
        return "blue";
    }

    @Override
    public String determineVariant() {
        if (getRandom().nextDouble() < 0.02) return "special";
        return switch (getRandom().nextInt(2)) {
            case 1 -> "purple";
            default -> getDefaultVariant();
        };

    }

    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions size) {
        return size.height * 0.6f;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.noCollision(this);
    }


    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void aiStep() {
        super.aiStep();

        // =====================
        //       Update Timers
        // =====================
        swimTimer.add(isUnderWater() ? -0.1f : 0.1f);
        sitTimer.add(isInSittingPose() ? 0.1f : -0.1f);
        setLightningAttackCooldown(Math.max(getLightningAttackCooldown()-1,0));
        //TODO: TAMED LIGHTNING COOLDOWN


        // =====================
        //       Conduit Logic
        // =====================

        Vec3 conduitPos = getConduitPos();
        if (hasConduit()) {
            if (level.isClientSide && isInWaterRainOrBubble() && getRandom().nextDouble() <= 0.1) {
                for (int i = 0; i < 16; ++i)
                    level.addParticle(ParticleTypes.NAUTILUS,
                            conduitPos.x,
                            conduitPos.y + 2.25,
                            conduitPos.z,
                            WRMathsUtility.nextDouble(getRandom()) * 1.5f,
                            WRMathsUtility.nextDouble(getRandom()),
                            WRMathsUtility.nextDouble(getRandom()) * 1.5f);
            }

            // nearby entities: if evil, kill, if not, give reallly cool potion effect
            if (tickCount % 80 == 0) {
                boolean attacked = false;
                for (LivingEntity entity : getEntitiesNearby(25, Entity::isInWaterRainOrBubble)) {
                    if (entity != getTarget() && (entity instanceof Player || isAlliedTo(entity)))
                        entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 220, 0, true, true));

                    if (!attacked && entity instanceof Enemy) {
                        attacked = true;
                        entity.hurt(DamageSource.MAGIC, 4);
                        playSound(SoundEvents.CONDUIT_ATTACK_TARGET, 1, 1);
                    }
                }
            }

            // play some sounds because immersion is important for some reason
            if (level.isClientSide && tickCount % 100 == 0)
                if (getRandom().nextBoolean()) playSound(SoundEvents.CONDUIT_AMBIENT, 1f, 1f, true);
                else playSound(SoundEvents.CONDUIT_AMBIENT_SHORT, 1f, 1f, true);
        }
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean isImmuneToArrows() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return WRModUtils.contains(source, DamageSource.LIGHTNING_BOLT, DamageSource.IN_FIRE, DamageSource.IN_WALL, DamageSource.ON_FIRE) || super.isInvulnerableTo(source);
    }


    public List<AABB> generateAttackBoxes(){
        List<AABB> attackBoxList = new ArrayList<>();
        attackBoxList.add(getBoundingBox().move(Vec3.directionFromRotation(isUsingSwimmingNavigator()? getXRot() : 0,yHeadRot).scale(1.0F)).inflate(1.2));
        attackBoxList.add(getBoundingBox().move(Vec3.directionFromRotation(isUsingSwimmingNavigator()? getXRot() : 0,yHeadRot).scale(5.0F)).inflate(0.67,2.5,0.67));
        attackBoxList.add(getBoundingBox().move(Vec3.directionFromRotation(isUsingSwimmingNavigator()? getXRot() : 0,yHeadRot).scale(7.0F)).inflate(0.67));
        return attackBoxList;
    }

    public boolean canPerformLightningAttack() {
        // Assuming both cooldown availability and baby plus growth stage, and not tamed...
        // If tamed, lightning must be commanded...
        // Lightning attacks can be performed if either in water, rain or bubble...
        //Or if there's a lightning rod relatively close to the BFL
        if (getLightningAttackCooldown() <= 0 && !isHatchling()) {
            if (isInWaterRainOrBubble()) {
                return true;
            }
            if (level.getBlockStates(getBoundingBox().inflate(5)).anyMatch(test -> test.is(Blocks.LIGHTNING_ROD))) {
                return true;
            }
        }
        return false;
    }
    // ====================================
    //      C) Navigation and Control
    // ====================================


    public boolean tamedLightningCheck() {
        //if (lightningCooldown <= 0) {
        if (isInWaterRainOrBubble()) {
            return true;
        }
        if (this.level.getBlockState(new BlockPos(position()).below()).is(Blocks.GOLD_BLOCK)) {
            return true;
        }
        //}
        return false;
    }

    @Override
    public float getTravelSpeed() {
        //@formatter:off
        return isInWater() ? (float) getAttributeValue(ForgeMod.SWIM_SPEED.get())
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    @Override
    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView) {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-10, this, player), 0, 0);
        }
        else
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.3, 0);
    }

    @Override
    public int getYawRotationSpeed() {
        return 6;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public float getStepHeight() {
        return 3;
    }

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    @Override
    public boolean speciesCanFly() {
        return false;
    }


    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================


    @Override
    public boolean speciesCanSwim() {
        return true;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================
    @Override // 2 passengers
    protected boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger) && isJuvenile();
    }

    @Override
    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, this.getType().getDimensions().height*0.95D, index == 1 ? -2 : 0);
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    @Override
    public void receivePassengerKeybind(int key, int mods, boolean pressed) {
        //TODO: Lightning strikes when tamed, set of different methods, when compared to regular, wild lightning strike

        if (pressed /*&& noAnimations()*/) {
            if (key == KeybindHandler.MOUNT_KEY) /*setAnimation(BITE_ANIMATION)*/ ;
            else if (key == KeybindHandler.ALT_MOUNT_KEY && !level.isClientSide && tamedLightningCheck()) {
                EntityHitResult ertr = WRMathsUtility.clipEntities(getControllingPlayer(), 40, e -> e instanceof LivingEntity && e != this);
                if (ertr != null && wantsToAttack((LivingEntity) ertr.getEntity(), getOwner())) {
                    setTarget((LivingEntity) ertr.getEntity());
                    /*AnimationPacket.send(this, LIGHTNING_ANIMATION);*/
                }
            }
        }
    }

    // ====================================
    //      D) Taming
    // ====================================

    public InteractionResult tameLogic(Player tamer, ItemStack stack) {
        // Return early if the code is running on the client side
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        // Check if the conditions for taming are met
        boolean isTamingConditionsMet = (this.isOnGround() && !this.isUnderWater()
                && getLightningAttackCooldown() > 50) || tamer.isCreative() || this.isHatchling();
        boolean isFoodItem = isFood(stack);
        boolean canEat = getEatingCooldown() <= 0;

        // If all conditions for taming and eating are satisfied
        if (isTamingConditionsMet && isFoodItem && canEat) {
            // Consume the food item
            eat(this.level, stack);
            setEatingCooldown(40); // Set the cooldown after eating

            // Attempt to tame the creature based on the player's mode and the creature's state
            float tameSuccessChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.2f;
            super.attemptTame(tameSuccessChance, tamer);

            // Return successful taming interaction result
            return InteractionResult.SUCCESS;
        }

        // Return pass if taming conditions are not met
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {
        if (slot == CONDUIT_SLOT) {
            boolean flag = stack.getItem() == Items.CONDUIT;
            boolean hadConduit = hasConduit();
            entityData.set(HAS_CONDUIT, flag);
            //if (!onLoad && flag && !hadConduit) setAnimation(CONDUIT_ANIMATION);
        }
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case "special" -> new Vec2(1,4);
            default -> new Vec2(0,4);
        };
    }

    @Nullable
    @Override
    public Predicate<ItemStack> canEquipSpecialItem() {
        return (item) -> item.is(Items.CONDUIT);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.isEdible() && stack.getFoodProperties(this).isMeat();
    }

    @Override
    public InteractionResult breedLogic(Player breeder, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (((this.isOnGround() && !this.isUnderWater()) && this.isAdult()) && isFood(stack)) {
            eat(this.level, stack);
            setBreedingCooldown(6000);
            setBreedingCount(getBreedingCount()+1);
            setInLove(breeder);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    public int getBreedingLimit(){
        return WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonBreedingConfig.breedLimit.get();
    }

    @Override
    public int hatchTime() {
        //Multiply by 20 to convert seconds to ticks
        return WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonBreedingConfig.hatchTime.get()*20;
    }


    // ====================================
    //      E) Client
    // ====================================

    @Override
    public void doSpecialEffects() {
        if (getVariant().equals("special") && tickCount % 25 == 0) {
            double x = getX() + (WRMathsUtility.nextDouble(getRandom()) * getBbWidth() + 1);
            double y = getY() + (getRandom().nextDouble() * getBbHeight() + 1);
            double z = getZ() + (WRMathsUtility.nextDouble(getRandom()) * getBbWidth() + 1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_BFLY_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_BFLY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_BFLY_DEATH.get();
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

        goalSelector.addGoal(4, new BFLAttackGoal(this));
        goalSelector.addGoal(5, new WRReturnToWaterGoal(this, 1.0,16,12,3));
        goalSelector.addGoal(5, new WRWaterLeapGoal(this, 1,12,30,64));
        goalSelector.addGoal(6, new WRRandomSwimmingGoal(this, 1.0, 64,48));
        goalSelector.addGoal(7,new WRIdleGoal(this, idleAnimation1Time));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 14f, 1));
        goalSelector.addGoal(9, new WRRandomLookAroundGoal(this,45));

        //targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        //targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        //targetSelector.addGoal(3, new HurtByTargetGoal(this));
        //targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                entity -> {
                    System.out.println("Evaluating entity: " + entity.getName().getString() + " | Class: " + entity.getClass().getName());
                    //Avoid targeting fish, to avoid creating lag
                    //Avoid targeting self
                    if (entity instanceof AbstractFish || entity.getClass() == this.getClass() || entity instanceof WRDragonEggEntity) {
                        return false;
                    }
                    //If we are not in water, we can target entities in water and out of water...
                    //If we are in water, only target entities in water...
                    return (!this.isInWater() || entity.isInWater());}));

    }

    public class BFLAttackGoal extends AnimatedGoal {
        private int navRecalculationTicks;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        LivingEntity target;
        boolean animationPlaying;
        boolean lightningForkQueued;
        boolean lightningForkSetup;
        int lightningLineCounter;
        Vec3 toTarget;
        Vec3 toTarget1;
        Vec3 toTarget2;
        Vec3 strikePos;
        Vec3 strikePos1;
        Vec3 strikePos2;
        boolean lightningStrikeQueued;
        boolean meleeAttackQueued;
        int attackVariant;
        float inflateValue;
        int disableShieldTime;
        int attackQueueTime;
        public BFLAttackGoal(EntityButterflyLeviathan entity) {
            super(entity);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        //Goal is usable if entity is not being ridden, and it has a target different to its owner
        @Override
        public boolean canUse() {
            if (canBeControlledByRider()) {
                return false;
            }
            if (isHatchling()) {
                return false;
            }
            target = getTarget();
            if (target != null && target != entity.getOwner()) {
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            getLookControl().setLookAt(target,0.0F,30F);
            setAggressive(true);
        }

        @Override
        public boolean canContinueToUse() {
            target = getTarget();
            //Stop using if target is lost
            //Unless we have set up a lightning fork, in which case we must finish the fork first...
            if (!lightningForkQueued && target == null) {
                return false;
            }
            return true;
        }

        @Override
        public void stop(){
            super.stop();
            setTarget(null);
            setAggressive(false);
            getNavigation().stop();
            animationPlaying = false;
            lightningForkQueued = false;
            lightningForkSetup = false;
            lightningLineCounter = 0;
            lightningStrikeQueued = false;
            meleeAttackQueued = false;
            navRecalculationTicks = 0;
            pathedTargetX = 0;
            pathedTargetY = 0;
            pathedTargetZ = 0;
        }

        @Override
        public void tick() {
            //As part of this goal, we can queue two behaviors:
            // A melee attack
            // A lightning attack
            // We queue behaviors so the actions happen in line with the animations
            // Hence, we queue a melee attack, and start performing the melee attack animation...
            // We only actually perform the melee attack once it makes sense to do so in terms of the animation...
            target = getTarget();
            if (target == null){
                return;
            }
            //If an animation is already playing, play until completion...
            if (animationPlaying) {
                if (super.canContinueToUse()) {
                    super.tick();
                } else {
                    //If animation is completed, stop animation logic...
                    //And clear all queues...
                    super.stop();
                    animationPlaying = false;
                    lightningForkQueued = false;
                    lightningForkSetup = false;
                    lightningLineCounter = 0;
                    lightningStrikeQueued = false;
                    meleeAttackQueued = false;
                    return;
                }

                //If a melee attack is queued...
                if (meleeAttackQueued) {
                    getLookControl().setLookAt(target,0.0F,30F);
                    // Note: the time for the attack to be performed depends on the attack variant being used
                    // The attack variant defines the animation variant which defines the time when it makes sense to perform the attack...
                    if (elapsedTime == attackQueueTime) {
                        List<AABB> attackBoxes = generateAttackBoxes();
                        if (!attackBoxes.isEmpty()) {
                            for (int i = 0; i <attackBoxes.size(); i++) {
                                attackInBox(attackBoxes.get(i));
                            }
                        }
                        //Perform the corresponding melee attack...
                        meleeAttackQueued = false;
                    }
                }
                if (lightningForkQueued) {
                    float desiredAngleYaw = (float)(Mth.atan2(target.position().z-position().z, target.position().x - position().x) * (double)(180F / (float)Math.PI));
                    setYRot(desiredAngleYaw);
                    setYHeadRot(desiredAngleYaw);
                    setYBodyRot(desiredAngleYaw);

                    //Animation Logic: If lightingFork is queued, and we have reached the appropriate time, call the GoalLogic...
                    //Keep calling so long as we exceed the appropriate time as this must be performed over multiple ticks
                    if (elapsedTime > LIGHTNING_FORK_ANIMATION_QUEUE) {
                        //If we have not yet set up the appropriate directions for the lightningLine, do that...
                        getLookControl().setLookAt(target,0.0F,30F);
                        if (lightningLineCounter < 25) {
                            //Instantiate 3 lightning bolts
                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            LightningBolt lightningBolt1 = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            LightningBolt lightningBolt2 = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            //Set all of their damages to double the normal lightning bolt damage
                            lightningBolt.setDamage(10F);
                            lightningBolt1.setDamage(10F);
                            lightningBolt2.setDamage(10F);
                            //Calculate all of their strike positions
                            strikePos = strikePos.add(toTarget.scale(2));
                            strikePos1 = strikePos1.add(toTarget1.scale(2));
                            strikePos2 = strikePos2.add(toTarget2.scale(2));
                            //Set up all the actual lightning bolt's positions
                            lightningBolt.setPos(strikePos);
                            lightningBolt1.setPos(strikePos1);
                            lightningBolt2.setPos(strikePos2);
                            //Add the lightning bolts to the world
                            level.addFreshEntity(lightningBolt);
                            level.addFreshEntity(lightningBolt1);
                            level.addFreshEntity(lightningBolt2);
                            lightningLineCounter++;
                        } else {
                            //Once we reach the limit, reset the lightningLine variables
                            lightningForkQueued = false;
                            lightningForkSetup = false;
                            lightningLineCounter = 0;
                        }
                    }

                }

                if (lightningStrikeQueued) {
                    //Animation Logic: If regularLightning is queued, and we have reached the appropriate time, call the AttackLogic...
                    if (elapsedTime == LIGHTNING_STRIKE_ANIMATION_QUEUE) {
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                        lightningBolt.setDamage(10F);
                        lightningBolt.setPos(target.position());
                        level.addFreshEntity(lightningBolt);
                        lightningStrikeQueued = false;
                    }
                }
            }

            // Attempt to navigate or attack if not already performing a lightning attack...
            if (!lightningForkQueued && !lightningStrikeQueued) {
                target = getTarget();
                if (target != null) {
                    double distanceToTargetSqr = distanceToSqr(target);
                    //Recalculate navigation only if we can see target, if we have not recently recalculated and if target has moved...
                    if ((getSensing().hasLineOfSight(target))
                            && this.navRecalculationTicks <= 0
                            && distanceToTargetSqr >= 64
                            && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D
                            || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D
                            || getRandom().nextFloat() < 0.05F)) {
                        // We are recalculating navigation...
                        // Hence, we store target's current position
                        this.pathedTargetX = target.getX();
                        this.pathedTargetY = target.getY();
                        this.pathedTargetZ = target.getZ();

                        //And we adjust the recalculation timer...
                        this.navRecalculationTicks = 4 + getRandom().nextInt(7);
                        //We will perform faster recalculations if we are close to the target...
                        if (distanceToTargetSqr > 1024.0D) {
                            this.navRecalculationTicks += 10;
                        } else if (distanceToTargetSqr > 256.0D) {
                            this.navRecalculationTicks += 5;
                        }

                        //We only recalculate the path if our current final path point (and hence our current path) will not get us to within a reasonable distance of target...
                        Node finalPathPoint;
                        if (getNavigation().getPath() == null || ((finalPathPoint = getNavigation().getPath().getEndNode()) == null) || target.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) > 25) {
                            if (!getNavigation().moveTo(target, 1.5)) {
                                this.navRecalculationTicks += 15;
                            }
                        }
                    }

                    //Adjust tick counter...
                    this.navRecalculationTicks = this.adjustedTickDelay(this.navRecalculationTicks);
                }

                //Update timer: Reduce by 1 if not already at 0
                this.navRecalculationTicks = Math.max(this.navRecalculationTicks - 1, 0);

                //Reduce by 1 if not already at 0

                //checkForLightningCounter = Math.max(checkForLightningCounter- 1, 0);

                //Decide which attack to use: LightningAttack or MeleeAttack.

                //Do not randomly use lightning attacks if tamed
                if (!isTame() && canPerformLightningAttack()) {
                    queueLightningAttack();
                }
                else if (canPerformMeleeAttack()) {
                     queueMeleeAttack();
                }
            }

        }

        private void queueLightningAttack(){
            LivingEntity target = getTarget();
            //There are two kinds of LightningAttacks:...
            // A: A Lightning Fork attack, used if on ground
            // B: A Lightning strike attack, used if on water...

            if (target !=null && !animationPlaying) {
                float desiredAngleYaw = (float)(Mth.atan2(
                        target.position().z-position().z,
                        target.position().x - position().x)
                        * (double)(180F / (float)Math.PI));
                setYRot(desiredAngleYaw);
                yHeadRot = desiredAngleYaw;
                yBodyRot = desiredAngleYaw;
                //If on ground, queue a lightning fork...
                //If on water, queue a lightning strike
                if (isUsingLandNavigator()) {
                    //Queue up ability to play when animation reaches the appropriate point
                    lightningForkQueued = true;
                    //Stop moving in preparation for Lightning Fork
                    getNavigation().stop();
                    //Set the animationPlaying flag correctly, start playing the animation via the super class
                    animationPlaying =true;
                    super.start(LIGHTNING_FORK_ANIMATION, 2, LIGHTNING_FORK_ANIMATION_TIME);
                    //Set up the Lightning Fork parameters
                    toTarget = target.position().subtract(position()).normalize();
                    toTarget1 = toTarget.yRot(0.523599F);
                    toTarget2 = toTarget.yRot(-0.523599F);
                    strikePos = position();
                    strikePos1 = position();
                    strikePos2 = position();
                    lightningForkSetup = true;
                } else if (isUsingSwimmingNavigator()){
                    //Queue up ability to play when animation reaches the appropriate point
                    lightningStrikeQueued = true;
                    //Stop moving in preparation for Lightning Strike
                    getNavigation().stop();
                    //Set the animationPlaying flag correctly, start playing the animation via the super class
                    animationPlaying =true;
                    super.start(LIGHTNING_STRIKE_ANIMATION, 2, LIGHTNING_STRIKE_ANIMATION_TIME);
                }
                // Lightning Attack is now queued, set the cooldown...
                setLightningAttackCooldown(400);
            }

        }

        private boolean canPerformMeleeAttack() {
            //Perform a melee attack if we are sufficiently close to the target, and no animation is currently playing...
            //If an animation is playing, the entity is "locked" in terms of actions, it does not make sense to perform another attack...
            if (target != null && distanceToSqr(target) <= 64 && !animationPlaying) {
                return true;
            }
            return false;
        }

        public void queueMeleeAttack() {
            //Randomly define an attack variant...
            attackVariant = 1+getRandom().nextInt(ATTACK_ANIMATION_VARIANTS);
            //Queue a melee attack, ensuring it happens once we reach the proper time...
            meleeAttackQueued = true;
            //An animation is now playing, we will not call any other melee attacks...
            animationPlaying =true;
            //Start the animation with the selected variant...
            boolean swimming = isUsingSwimmingNavigator();
            String navVariant = swimming? "water" : "land";
            //Get the entity to face its target properly
            float desiredAngleYaw = (float)(Mth.atan2(target.position().z-position().z, target.position().x - position().x) * (double)(180F / (float)Math.PI));
            setYRot(desiredAngleYaw);
            yHeadRot = desiredAngleYaw;
            yBodyRot = desiredAngleYaw;
            //set the attackQueueTime depending on which animation variant is being used
            switch (attackVariant) {
                case 1 ->
                        {
                    int time = swimming? WATER_ATTACK_ANIMATION_TIME_1 : LAND_ATTACK_ANIMATION_TIME_1;
                    attackQueueTime = swimming? WATER_ATTACK_QUEUE_TIME_1 : LAND_ATTACK_QUEUE_TIME_1;
                    super.start(ATTACK_ANIMATION+navVariant+attackVariant,2,time);
                        }
                case 2 -> {
                    int time = swimming? WATER_ATTACK_ANIMATION_TIME_2 : LAND_ATTACK_ANIMATION_TIME_2;
                    attackQueueTime = swimming? WATER_ATTACK_QUEUE_TIME_2 : LAND_ATTACK_QUEUE_TIME_2;
                    super.start(ATTACK_ANIMATION+navVariant+attackVariant,2,time);
                }
            }
        }
    }
}