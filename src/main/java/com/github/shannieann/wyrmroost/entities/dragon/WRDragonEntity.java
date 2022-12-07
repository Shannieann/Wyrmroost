package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entities.dragon.ai.AnimatedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.BetterPathNavigator;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.DragonBodyController;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.FlyerPathNavigator;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.shannieann.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.shannieann.wyrmroost.items.DragonArmorItem;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public abstract class WRDragonEntity extends TamableAnimal implements IAnimatable {

    private int sleepCooldown;
    public int breedCount;
    private float ageProgress = 1;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT); // todo in 1.17: make this use strings for nbt based textures
    public static final EntityDataAccessor<ItemStack> ARMOR = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);
    //TODO: What is this?
    private static final UUID SCALE_MOD_UUID = UUID.fromString("81a0addd-edad-47f1-9aa7-4d76774e055a");
    private static final int AGE_UPDATE_INTERVAL = 200;
    protected static int IDLE_ANIMATION_VARIANTS;
    protected static int ATTACK_ANIMATION_VARIANTS;
    protected static float SITTING_ANIMATION_TIME;
    protected static float SLEEPING_ANIMATION_TIME;


    private static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.STRING);
    /**
     * ANIMATION_TYPE:
     * Case 1: LOOP
     * Case 2: PLAY_ONCE
     * Case 3: HOLD_ON_LAST_FRAME
     */
    private static final EntityDataAccessor<Integer> ANIMATION_TYPE = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);
    /**
     * MOVING_STATE:
     * Case 0: Ground
     * Case 1: Flying
     * Case 2: Swimming
     */
    private static final EntityDataAccessor<Float> ANIMATION_TIME = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MOVING_STATE = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);

    protected WRDragonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // =====================
    //      Animation Logic
    // =====================

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "generalController", 0, this::generalPredicate));
        data.addAnimationController(new AnimationController(this, "moveController", 0, this::movingPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public <E extends IAnimatable> PlayState generalPredicate(AnimationEvent<E> event)
    {
        String animation = this.getAnimation();
        //If we do have an Ability animation play that
        if (!animation.equals("base")) {
            int animationType = this.getAnimationType();
            ILoopType loopType;
            switch (animationType) {
                case 1: loopType = ILoopType.EDefaultLoopTypes.LOOP;
                    break;
                case 2: loopType = ILoopType.EDefaultLoopTypes.PLAY_ONCE;
                    break;
                case 3: loopType = ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME;
                    break;
                default:
                    return PlayState.STOP;
            }
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, loopType));
            return PlayState.CONTINUE;
        }
        //Else, do basic locomotion
        //TODO: Custom Death Animations
        //Death
        if ((this.dead || this.getHealth() < 0.01 || this.isDeadOrDying())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        //This moving only plays if it's *just* moving and not doing anything else, as its only reached under those conditions...
        int movingState = this.getMovingState();
        if (event.isMoving()) {
            switch (movingState) {
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
                case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        //Idle:
        int idleVariant = this.random.nextInt(IDLE_ANIMATION_VARIANTS+1);
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle_"+idleVariant, ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public <E extends IAnimatable> PlayState movingPredicate(AnimationEvent<E> event)
    {
        int movingState = this.getMovingState();
        if (event.isMoving()) {
            switch (movingState) {
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_walk", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_fly", ILoopType.EDefaultLoopTypes.LOOP));
                case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0,new AnimatedGoal(this,this.getAnimation(),this.getAnimationType(),this.getAnimationTime()));
    }


    // =====================
    //      Entity Data
    // =====================
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData data, @javax.annotation.Nullable CompoundTag dataTag)
    {
        if (hasEntityDataAccessor(GENDER)) setGender(getRandom().nextBoolean());
        if (hasEntityDataAccessor(VARIANT)) setVariant(determineVariant());

        return super.finalizeSpawn(level, difficulty, reason, data, dataTag);
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(ANIMATION, "base");
        this.entityData.define(ANIMATION_TYPE, 1);
        this.entityData.define(MOVING_STATE, 0);
        this.entityData.define(ANIMATION_TIME, 0F);
        entityData.define(HOME_POS, BlockPos.ZERO);
        entityData.define(AGE, 0);
        super.defineSynchedData();
    }

    public String getAnimation()
    {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(String animation)
    {entityData.set(ANIMATION, animation);}

    public int getAnimationType()
    {
        return entityData.get(ANIMATION_TYPE);
    }

    public void setAnimationType(int animation)
    {
        entityData.set(ANIMATION_TYPE, animation);
    }

    public int getMovingState()
    {
        return entityData.get(ANIMATION_TYPE);
    }

    public void setMovingState(int movingState)
    {
        entityData.set(MOVING_STATE, movingState);
    }

    public float getAnimationTime()
    {
        return entityData.get(ANIMATION_TIME);
    }

    public void setAnimationTime(float animationTime)
    {
        entityData.set(ANIMATION_TIME, animationTime);
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }

    public boolean hasEntityDataAccessor(EntityDataAccessor<?> param)
    {
        return entityData.itemsById.containsKey(param.getId());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (key.equals(SLEEPING) || key.equals(FLYING) || key.equals(TamableAnimal.DATA_FLAGS_ID))
        {
            refreshDimensions();
        }
        else if (key == AGE)
        {
            setAge(entityData.get(AGE));
            updateAgeProgress();
            refreshDimensions();

            float scale = getScale();
            if (scale >= 1)
            {
                AttributeModifier mod = new AttributeModifier(SCALE_MOD_UUID, "Scale modifier", scale, AttributeModifier.Operation.MULTIPLY_BASE);
                for (Attribute att : getScaledAttributes())
                {
                    AttributeInstance instance = getAttribute(att);
                    instance.removeModifier(mod);
                    instance.addTransientModifier(mod);
                }
            }
        }
        else super.onSyncedDataUpdated(key);
    }

    // =====================
    //      tick Methods
    // =====================
    @Override
    public void tick(){
        super.tick();

        if (!level.isClientSide) {
            // uhh so were falling, we should probably start flying
            boolean flying = shouldFly();
            if (flying != isFlying()) setFlying(flying);

            // todo figure out a better target system?
            LivingEntity target = getTarget();
            if (target != null && (!target.isAlive() || !canAttack(target) || !wantsToAttack(target, getOwner())))
                setTarget(null);
        }


        updateAgeProgress();
        if (age < 0 && tickCount % AGE_UPDATE_INTERVAL == 0) entityData.set(AGE, age);

        if (this.level.isClientSide) {
            doSpecialEffects();
            int age = getAge();
            if (age < 0) setAge(++age);
            else if (age > 0) setAge(--age);
        }

        if (sleepCooldown > 0) --sleepCooldown;
        if (isSleeping())
        {
            ((LessShitLookController) getLookControl()).stopLooking();
            if (getHealth() < getMaxHealth() && getRandom().nextDouble() < 0.005) heal(1);

            if (shouldWakeUp())
            {
                setSleeping(false);
            }
        }
        else if (shouldSleep())
        {
            setSleeping(true);
        }

        //Animations:
        //Sleeping
        if (this.isSleeping()) {
            this.setAnimation("sleep");
            this.setAnimationType(1);
            this.setAnimationTime(20);
        }
        //Sitting
        if (this.isInSittingPose()){
            this.setAnimation("sit");
            this.setAnimationTime(20);
        }
    }

    @Override
    public void travel(Vec3 vec3d){
        float speed = getTravelSpeed();
        boolean isFlying = isFlying();
        if (isFlying)
        {
            // Move relative to yaw - handled in the move controller or by passenger
            moveRelative(speed, vec3d);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9f));
            calculateEntityAnimation(this, true);
        }
        else super.travel(vec3d);
    }


    // =====================
    //      Navigation and Control
    // =====================
    @Override
    protected PathNavigation createNavigation(Level levelIn)
    {
        return new BetterPathNavigator(this);
    }

    @Override
    protected BodyRotationControl createBodyControl()
    {
        return new DragonBodyController(this);
    }


    // =====================
    //       Entity AI
    // =====================
    public void clearAI()
    {
        jumping = false;
        navigation.stop();
        setTarget(null);
        setSpeed(0);
        setYya(0);
    }

    // =====================
    //      Sleep Methods
    // =====================
    public boolean isSleeping()
    {
        return hasEntityDataAccessor(SLEEPING) && entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleep)
    {

        if (isSleeping() == sleep) return;

        entityData.set(SLEEPING, sleep);
        if (!level.isClientSide)
        {
            if (sleep)
            {
                setAnimation("sleeping");
                setAnimationTime(SLEEPING_ANIMATION_TIME);
                setAnimationType(3);
                clearAI();
                setXRot(0);
            }
            else sleepCooldown = 350;
        }
    }

    public boolean shouldSleep()
    {
        if (sleepCooldown > 0) return false;
        if (level.isDay()) return false;
        if (!isIdling()) return false;
        if (isTame())
        {
            if (isAtHome())
            {
                if (defendsHome()) return getHealth() < getMaxHealth() * 0.25;
            }
            else if (!isInSittingPose()) return false;
        }

        return getRandom().nextDouble() < 0.0065;
    }

    public boolean isIdling()
    {
        return getNavigation().isDone() && getTarget() == null && !isVehicle() && !isInWaterOrBubble() && !isFlying();
    }

    public boolean shouldWakeUp()
    {
        return level.isDay() && getRandom().nextDouble() < 0.0065;
    }

    // =====================
    //      Breed Methods
    // =====================

    public int getBreedCount()
    {
        return breedCount;
    }

    public void setBreedCount(int i)
    {
        this.breedCount = i;
    }

    public boolean isBreedingItem(ItemStack stack)
    {
        return isFood(stack);
    }

    // =====================
    //      Home Methods
    // =====================

    public boolean isAtHome()
    {
        return hasRestriction() && isWithinRestriction();
    }

    public boolean trySafeTeleport(BlockPos pos)
    {
        if (level.noCollision(this, getBoundingBox().move(pos.subtract(blockPosition()))))
        {
            moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, getYRot(), getXRot());
            return true;
        }
        return false;
    }

    @Override
    public BlockPos getRestrictCenter()
    {
        BlockPos pos = getHomePos();
        return pos == null? BlockPos.ZERO : pos;
    }

    @javax.annotation.Nullable
    public BlockPos getHomePos()
    {
        BlockPos pos = entityData.get(HOME_POS);
        return pos == BlockPos.ZERO? null : pos;
    }

    public void setHomePos(@javax.annotation.Nullable BlockPos pos)
    {
        entityData.set(HOME_POS, pos == null? BlockPos.ZERO : pos);
    }

    public void clearHome()
    {
        setHomePos(null);
    }

    @Override
    public boolean hasRestriction()
    {
        return getHomePos() != null;
    }

    @Override
    public float getRestrictRadius()
    {
        return WRConfig.HOME_RADIUS.get() * WRConfig.HOME_RADIUS.get();
    }

    @Override
    public void restrictTo(BlockPos pos, int distance)
    {
        setHomePos(pos);
    }

    @Override
    public boolean isWithinRestriction()
    {
        return isWithinRestriction(blockPosition());
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos)
    {
        BlockPos home = getHomePos();
        return home == null || home.distSqr(pos) <= getRestrictRadius();
    }

    public boolean tryTeleportToOwner()
    {
        if (getOwner() == null) return false;
        final int CONSTRAINT = (int) (getBbWidth() * 0.5) + 1;
        BlockPos pos = getOwner().blockPosition();
        BlockPos.MutableBlockPos potentialPos = new BlockPos.MutableBlockPos();

        for (int x = -CONSTRAINT; x < CONSTRAINT; x++)
            for (int y = 0; y < 4; y++)
                for (int z = -CONSTRAINT; z < CONSTRAINT; z++)
                {
                    potentialPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (trySafeTeleport(potentialPos)) return true;
                }
        return false;
    }

    public boolean defendsHome()
    {
        return false;
    }


    // =====================
    //      Flying Methods
    // =====================

    public boolean isFlying()
    {
        return hasEntityDataAccessor(FLYING) && entityData.get(FLYING);
    }

    public void setFlying(boolean fly)
    {
        if (isFlying() == fly) return;
        entityData.set(FLYING, fly);
        if (fly)
        {
            if (liftOff()) {
                navigation = new FlyerPathNavigator(this);
                //Updates moving state to fly
                this.setMovingState(1);
            }
        }
        else {
            navigation = new BetterPathNavigator(this);
            //Updates moving state to walk
            this.setMovingState(0);
        }
    }

    public boolean liftOff()
    {
        if (!canFly()) return false;
        if (!onGround) return true; // We can't lift off the ground in the air...

        int heightDiff = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) getX(), (int) getZ()) - (int) getY();
        if (heightDiff > 0 && heightDiff <= getFlightThreshold())
            return false; // position has too low of a ceiling, can't fly here.

        setOrderedToSit(false);
        setSleeping(false);
        jumpFromGround();
        return true;
    }

    public boolean shouldFly()
    {
        return canFly() && getAltitude() > 1;
    }

    public boolean canFly()
    {
        return isJuvenile() && !isUnderWater() && !isLeashed();
    }

    public double getAltitude()
    {
        BlockPos.MutableBlockPos pos = blockPosition().mutable();

        // cap to the level void (y = 0)
        while (pos.getY() > 0 && !level.getBlockState(pos.move(Direction.DOWN)).getMaterial().isSolid());
        return getY() - pos.getY();
    }

    public int getFlightThreshold()
    {
        return (int) getBbHeight();
    }

    public int getYawRotationSpeed()
    {
        return isFlying()? 6 : 75;
    }

    public void flapWings()
    {
        playSound(WRSounds.WING_FLAP.get(), 3, 1, false);
        setDeltaMovement(getDeltaMovement().add(0, 1.285, 0));
    }

    @Override
    protected float getJumpPower()
    {
        return canFly()? (getBbHeight() * getBlockJumpFactor()) * 0.6f : super.getJumpPower();
    }

    @Override // Disable fall calculations if we can fly (fall damage etc.)
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        return !canFly() && super.causeFallDamage(distance - (int) (getBbHeight() * 0.8), damageMultiplier, source);
    }
    // =====================
    //      Age Methods
    // =====================

    public void updateAgeProgress()
    {
        // no reason to recalculate this value several times per tick/frame...
        float growth = DragonEggProperties.get(getType()).getGrowthTime();
        float min = Math.min(getAge(), 0);
        ageProgress = 1 - (min / growth);
    }

    public float ageProgress()
    {
        return ageProgress;
    }

    public boolean isJuvenile()
    {
        return ageProgress() > 0.5f;
    }

    public boolean isAdult()
    {
        return ageProgress() >= 1f;
    }

    public boolean isHatchling()
    {
        return ageProgress() < 0.5f;
    }

    @Override
    public boolean isBaby()
    {
        return !isAdult();
    }

    @Override
    public void setBaby(boolean baby)
    {
        setAge(baby? DragonEggProperties.get(getType()).getGrowthTime() : 0);
        entityData.set(AGE, this.age);
    }

    @Override
    public int getAge()
    {
        return age;
    }

    @Override
    public void ageUp(int age, boolean forced)
    {
        super.ageUp(age, forced);
        entityData.set(AGE, this.age);
    }

    @Override
    public float getScale()
    {
        return 0.5f + (0.5f * ageProgress());
    }

    public float getAgeScale(float baby)
    {
        return baby + ((1 - baby) * ageProgress());
    }


    // =====================
    //      Armor Methods
    // =====================

    public boolean hasArmor()
    {
        return hasEntityDataAccessor(ARMOR) && entityData.get(ARMOR).getItem() instanceof DragonArmorItem;
    }

    public ItemStack getArmorStack()
    {
        return hasEntityDataAccessor(ARMOR)? entityData.get(ARMOR) : ItemStack.EMPTY;
    }

    public void setArmor(@javax.annotation.Nullable ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof DragonArmorItem)) stack = ItemStack.EMPTY;
        entityData.set(ARMOR, stack);
    }

    // =====================
    //      Variant Methods
    // =====================
    public int determineVariant()
    {
        return 0;
    }
    //Special Variants = -1
    public int getVariant()
    {
        return hasEntityDataAccessor(VARIANT)? entityData.get(VARIANT) : 0;
    }

    public void setVariant(int variant)
    {
        entityData.set(VARIANT, variant);
    }

    // =====================
    //      Gender Methods
    // =====================
    public boolean isMale()
    {
        return !hasEntityDataAccessor(GENDER) || entityData.get(GENDER);
    }

    public void setGender(boolean sex)
    {
        entityData.set(GENDER, sex);
    }

    // =====================
    //      Sitting Methods
    // =====================
    @Override
    public void setInSittingPose(boolean flag)
    {
        super.setInSittingPose(flag);
        if (flag) {
            clearAI();
            setAnimation("sitting");
            setAnimationType(3);
            setAnimationTime(20);
        }
    }

    // =====================
    //      Attack Methods
    // =====================

    public void attackInBox(AABB box)
    {
        attackInBox(box, 0);
    }

    public void attackInBox(AABB box, int disabledShieldTime)
    {
        List<LivingEntity> attackables = level.getEntitiesOfClass(LivingEntity.class, box, entity -> entity != this && !hasPassenger(entity) && wantsToAttack(entity, getOwner()));
        //if (WRConfig.DEBUG_MODE.get() && level.isClientSide) DebugRendering.box(box, 0x99ff0000, Integer.MAX_VALUE);
        for (LivingEntity attacking : attackables)
        {
            doHurtTarget(attacking);
            if (disabledShieldTime > 0 && attacking instanceof Player)
            {
                Player player = ((Player) attacking);
                if (player.isUsingItem() && player.getUseItem().is(Items.SHIELD))
                {
                    player.getCooldowns().addCooldown(Items.SHIELD, disabledShieldTime);
                    player.stopUsingItem();
                    level.broadcastEntityEvent(player, (byte) 9);
                }
            }
        }
    }


    @Override
    public boolean doHurtTarget(Entity entity)
    {
        if (!this.getAnimation().equals("base")) {
            int attackVariant = this.random.nextInt(ATTACK_ANIMATION_VARIANTS+1);
            this.setAnimation("attack_"+attackVariant);
            this.setAnimationType(1);
            this.setAnimationTime(80);
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (isImmuneToArrows() && source.getDirectEntity() != null)
        {
            EntityType<?> attackSource = source.getDirectEntity().getType();
            if (attackSource == EntityType.ARROW) return false;
            else if (attackSource == WREntityTypes.GEODE_TIPPED_ARROW.get()) amount *= 0.5f;
        }

        setSleeping(false);
        setOrderedToSit(false);
        return super.hurt(source, amount);
    }

    // =====================
    //      Sound Methods
    // =====================
    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch)
    {
        playSound(soundIn, volume, pitch, false);
    }

    public void playSound(SoundEvent sound, float volume, float pitch, boolean local)
    {
        if (isSilent()) return;

        volume *= getSoundVolume();
        pitch *= getVoicePitch();

        if (local) level.playLocalSound(getX(), getY(), getZ(), sound, getSoundSource(), volume, pitch, false);
        else level.playSound(null, getX(), getY(), getZ(), sound, getSoundSource(), volume, pitch);
    }

    @Override
    public float getSoundVolume()
    {
        return getScale();
    }

    @Override
    public float getVoicePitch()
    {
        return ((random.nextFloat() - random.nextFloat()) * 0.2f + 1) * (2 - ageProgress());
    }

    @Override
    public void playAmbientSound()
    {
        if (!isSleeping()) super.playAmbientSound();
    }

    // =====================
    //      Client Methods
    // =====================
    public void doSpecialEffects()
    {
    }
    // =====================
    //      Misc Methods
    // =====================
    public Attribute[] getScaledAttributes()
    {
        return new Attribute[]{MAX_HEALTH, ATTACK_DAMAGE};
    }
    public float getTravelSpeed()
    {
        //@formatter:off
        return isFlying()? (float) getAttributeValue(FLYING_SPEED)
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    @Deprecated
    public boolean isImmuneToArrows()
    {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || isSleeping()) size = size.scale(1, 0.5f);
        return size;
    }

    @Override
    protected int getExperienceReward(Player player)
    {
        return Math.max((int) ((getBbWidth() * getBbHeight()) * 0.25) + getRandom().nextInt(3), super.getExperienceReward(player));
    }

    /**
     * A universal getter for the position of the mouth on the dragon.
     * This is prone to be inaccurate, but can serve good enough for most things
     * If a more accurate position is needed, best to override and adjust accordingly.
     *
     * @return An approximate position of the mouth of the dragon
     */
    public Vec3 getApproximateMouthPos()
    {
        Vec3 position = getEyePosition(1).subtract(0, 0.75d, 0);
        double dist = (getBbWidth() / 2) + 0.75d;
        return position.add(calculateViewVector(getXRot(), yHeadRot).scale(dist));
    }


    public AABB getOffsetBox(float offset)
    {
        return getBoundingBox().move(Vec3.directionFromRotation(0, yBodyRot).scale(offset));
    }

    public void setRotation(float yaw, float pitch)
    {
        this.setYRot(yaw % 360.0F);
        this.setXRot(pitch % 360.0F);
    }

    public boolean isRiding()
    {
        return getVehicle() != null;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder()
    {
        return false;
    }

    public static boolean canFlyerSpawn(EntityType<? extends WRDragonEntity> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType spawnType, BlockPos pos, Random random)
    {
        return serverLevelAccessor.getBlockState(pos.below()).getFluidState().isEmpty();
    }

}


