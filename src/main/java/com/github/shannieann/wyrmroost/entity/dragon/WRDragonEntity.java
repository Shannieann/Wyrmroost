package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.fly.WRFlyLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.WRBodyControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundMoveControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.fly.FlyerMoveController;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.fly.FlyerPathNavigator;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundNavigation;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingMoveControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingNavigator;
import com.github.shannieann.wyrmroost.item.DragonArmorItem;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.registry.WRKeybind;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public abstract class WRDragonEntity extends TamableAnimal implements IAnimatable, MenuProvider {


    double travelX0;
    double travelY0;
    double travelZ0;
    public int sleepCooldown;
    //Only for swimmers:
    public float prevYRot;
    public float deltaYRot;
    public float adjustYaw;
    public float adjustmentYaw;
    public float prevSetYaw;
    public float setYaw;
    public float deltaPitch;
    public float adjustedPitch;
    public float deltaPitchLimit;
    public float targetPitchRadians;
    public float currentPitchRadians;
    public float deltaPitchExtremities;
    public float pitchExtremities;
    public float pitchExtremitiesRadians;
    public float prevSetExtremityPitch;
    public float setExtremityPitch;
    public float adjustExtremityPitch;
    public float adjustmentExtremityPitch;
    public float groundMaxYaw;
    public float targetXRotation;
    private int xRotLerpSteps;
    // X rot for the entire body. These are client-side only
    public Vec3 debugTarget;
    public int WAKE_UP_ANIMATION_TIME;
    public int WAKE_UP_WATER_ANIMATION_TIME;

    // Used in setting 1st person camera positions when flying but set in DragonRiderLayer & WRDragonRender
    public Vector3f cameraRotVector = new Vector3f();
    public Map<UUID, Vector3d> cameraBonePos = new HashMap<>();



    //TODO: CANCEL DAMAGE FROM CACTUS AND BERRY BUSHES AND OTHERS

    public enum NavigationType {
        GROUND,
        FLYING,
        SWIMMING
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;

    @Deprecated // https://github.com/MinecraftForge/MinecraftForge/issues/7622
    public final LazyOptional<DragonInventory> inventory;
    public final LerpedFloat sleepTimer = LerpedFloat.unit();
    public static final EntityDataAccessor<Float> AGE_PROGRESS = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<ItemStack> ARMOR = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BREACHING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> YAW_UNLOCK = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * GENDER:
     * 0 --> FEMALE
     * 1 --> MALE
     * Originally determined via coin flip
     */
    public static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);

    public static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> EATING_COOLDOWN = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BREEDING_COOLDOWN = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> BREEDING_COUNT = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Float> DRAGON_X_ROTATION = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.FLOAT);
    private static final int AGE_UPDATE_INTERVAL = 1200;

    protected static int IDLE_ANIMATION_VARIANTS;

    protected static int ATTACK_ANIMATION_VARIANTS;

    //ToDo: SittING / SleepING animations are not implemented for BFL. Once we implement for land creatures, they should not interfere with sleep goal.
    protected static int SITTING_ANIMATION_TIME;
    protected static int SLEEPING_ANIMATION_TIME;
    protected static final boolean ATTACK_ANIMATION_MOVES = true;
    protected float maxPitchAdjustment;

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
    private static final EntityDataAccessor<Integer> ANIMATION_TIME = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);

    protected WRDragonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
        DragonInventory inv = createInv();
        inventory = LazyOptional.of(inv == null? null : () -> inv);
    }

    // =====================
    //      Animation Logic
    // =====================

    @Override
    public void registerControllers(AnimationData data)
    {
        data.addAnimationController(new AnimationController<>(this, "controllerAbility", 0, this::predicateAbility));

        AnimationController<? extends WRDragonEntity> locomotion = new AnimationController<>(this, "controllerBasicLocomotion", 0, this::predicateBasicLocomotion);
        locomotion.registerSoundListener(this::locomotionListener);
        data.addAnimationController(locomotion);

    }

    @Override
    public AnimationFactory getFactory()
    {
        return this.factory;
    }

    public <E extends IAnimatable> PlayState predicateAbility(AnimationEvent<E> event)
    {
        //BoneType: INVISIBLE Bones

        //All Ability Animations to be played are stored as DataParameters Strings
        //We begin by getting the animation that should be played.
        //This string may have been set as part of an AnimatedGoal that requires a one-shot ability animation to play..
        String animation = this.getAnimation();
        //If we do have a one-shot ability animation, we will play that.
        //"base" is the null value for getAnimation
        if (!animation.equals("base")) {
            //If we do have an ability animation, we get the type (Loop, Play once, Hold on last frame)
            int animationType = this.getAnimationType();
            ILoopType loopType;
            switch (animationType) {
                case 1 -> loopType = ILoopType.EDefaultLoopTypes.LOOP;
                case 2 -> loopType = ILoopType.EDefaultLoopTypes.PLAY_ONCE;
                case 3 -> loopType = ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME;
                default -> {return PlayState.STOP;}
            }
            //We proceed to play the corresponding animation...
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, loopType));
            return PlayState.CONTINUE;
        }
        //Else, just return base:
        //This will not cause a transition to a stiff pose, as walking animations will be running concurrently...
        //We are only resetting the position of the iBones/Bones here
        event.getController().setAnimation(new AnimationBuilder().addAnimation("base", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public <E extends IAnimatable> PlayState predicateBasicLocomotion(AnimationEvent<E> event) {
        //BoneType: VISIBLE Bones


        /*
        //Basic Locomotion: Death
        if ((this.dead || this.getHealth() < 0.01 || this.isDeadOrDying())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
       */

        //Basic Locomotion: Movement
        //These moving animations play whenever the entity is moving
        //By using regular bones for these abilities as opposed to invisible ones, we both animations to overlay...
        //We select between slow or fast movement based on whether the entity is aggressive or not
        //We confirm the entity is moving with intention (not being pushed) by verifying it has a targetPosition on its navigator
        NavigationType navigationType = this.getNavigationType();
        if (this.getSleeping() || this.getSitting() || this.getBreaching()){
            return PlayState.STOP;
        }

        if (this.getDeltaMovement().length() != 0 && this.isAggressive()) {
            switch (navigationType) {
                case GROUND -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_fast", ILoopType.EDefaultLoopTypes.LOOP));
                case FLYING -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly_fast", ILoopType.EDefaultLoopTypes.LOOP));
                case SWIMMING-> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim_fast", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement().length() !=0 && !this.isAggressive() && !this.getNavigation().isDone()) {
            switch (navigationType) {
                case GROUND -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                case FLYING ->
                        {
                            if (isDiving()){
                                //ToDo: Account for more flying cases
                                event.getController().setAnimation(new AnimationBuilder().addAnimation("dive", ILoopType.EDefaultLoopTypes.LOOP));
                            } else if (isGliding()){
                                event.getController().setAnimation(new AnimationBuilder().addAnimation("glide", ILoopType.EDefaultLoopTypes.LOOP));
                            }
                            else event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
                        }
                case SWIMMING -> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }

        //Basic Locomotion: Riding Cases
        if (this.canBeControlledByRider() && getDeltaMovement().length() >= 0.1f) {
            return predicateRiding(event);
        }

        //Basic Locomotion: Default cases
        //If the entity is swimming, and it is not doing anything else that warrants an animation, it will just swim in place.
        if (!this.getSleeping()) {
            if (this.isUsingSwimmingNavigator()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("base_swim", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
            //If the entity is flying, and it is not doing anything else that warrants an animation, it will just fly in place.
            if (this.isUsingFlyingNavigator()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
            //If the entity is on ground, and it is not doing anything else that warrants an animation, it will just stand ("naturally") in place
            if (this.isUsingLandNavigator()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("base_ground", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }
        }
        //Default case
        //If nothing else was triggered, reset the entity to its base animation
        event.getController().setAnimation(new AnimationBuilder().addAnimation("base", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }


    //Riding Predicate>
    //Can be overriden in subclasses to generate custom locomotion animations
    public <E extends IAnimatable> PlayState predicateRiding(AnimationEvent<E> event) {
        NavigationType navigationType = this.getNavigationType();
        switch (navigationType) {
            case GROUND -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_fast", ILoopType.EDefaultLoopTypes.LOOP));
            case FLYING -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly_fast", ILoopType.EDefaultLoopTypes.LOOP));
            case SWIMMING-> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim_fast", ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }



        public abstract int idleAnimationVariants();

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================


    // TODO reduce the amount of shared entity data... this amount could be burdensome on the server
    @Override
    protected void defineSynchedData() {
        entityData.define(ANIMATION, "base");
        entityData.define(ANIMATION_TYPE, 1);
        entityData.define(ANIMATION_TIME, 0);

        entityData.define(AGE_PROGRESS, 1f);
        entityData.define(VARIANT, getDefaultVariant());
        entityData.define(GENDER, 0);

        entityData.define(BREACHING, false);
        entityData.define(YAW_UNLOCK, false);

        entityData.define(SLEEPING, false);
        entityData.define(SITTING, false);

        entityData.define(DRAGON_X_ROTATION, 0f);

        entityData.define(ARMOR, ItemStack.EMPTY);
        entityData.define(SADDLED, false);
        entityData.define(CHESTED, false);

        entityData.define(EATING_COOLDOWN, 0);

        entityData.define(BREEDING_COOLDOWN, 0);
        entityData.define(BREEDING_COUNT, 0);

        entityData.define(HOME_POS, BlockPos.ZERO);

        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Gender",getGender());
        nbt.putString("Variant",getVariant());

        nbt.putBoolean("Sleeping",getSleeping());
        nbt.putBoolean("Sitting",getSitting());

        nbt.putInt("EatingCooldown",getEatingCooldown());

        nbt.putInt("BreedingCooldown",getBreedingCooldown());
        nbt.putInt("BreedingCount",getBreedingCount());

        nbt.putFloat("AgeProgress",getAgeProgress());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {

        super.readAdditionalSaveData(nbt);
        setGender(nbt.getInt("Gender"));

        String variant = nbt.contains("Variant")? nbt.getString("Variant") : getDefaultVariant();
        // Default variant if variant is not set in the NBT
        setVariant(variant);

        setSleeping(nbt.getBoolean("Sleeping"));
        setSitting(nbt.getBoolean("Sitting"));

        setEatingCooldown(nbt.getInt("EatingCooldown"));

        setBreedingCooldown(nbt.getInt("BreedingCooldown"));
        setBreedingCount(nbt.getInt("BreedingCount"));

        float age = nbt.contains("AgeProgress")? nbt.getFloat("AgeProgress") : 1.0f;
        // Default age is 1 if age is not set in the NBT
        setAgeProgress(age);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData data, @javax.annotation.Nullable CompoundTag dataTag) {
        /**
         * GENDER:
         * 0 --> FEMALE
         * 1 --> MALE
         * Originally determined via coin flip
         */

        if (hasEntityDataAccessor(GENDER)) {
            int gender;
            if (getRandom().nextBoolean()) {
                gender = 1;
            } else {
                gender = 0;
            }
            setGender(gender);
        }

        //determineVariant is a method in each subclass, specific to each creature
        if (hasEntityDataAccessor(VARIANT)) {
            setVariant(determineVariant());
        }

        if ((reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWN_EGG)){
            setAgeProgress(1f);
        }
        return super.finalizeSpawn(level, difficulty, reason, data, dataTag);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(SLEEPING) || isUsingFlyingNavigator() || key.equals(TamableAnimal.DATA_FLAGS_ID)) {
            refreshDimensions();
            //if (level.isClientSide  && isUsingFlyingNavigator() && canBeControlledByRider())
                //FlyingSound.play(this); TODO add this back, but it was really annoying while testing lol
        } else if (key == ARMOR) {
            if (!level.isClientSide) {
                AttributeInstance attribute = getAttribute(Attributes.ARMOR);
                if (attribute.getModifier(DragonArmorItem.ARMOR_UUID) != null)
                    attribute.removeModifier(DragonArmorItem.ARMOR_UUID);
                if (hasArmor()) {
                    attribute.addTransientModifier(new AttributeModifier(DragonArmorItem.ARMOR_UUID, "Armor Modifier", DragonArmorItem.getDmgReduction(getArmorStack()), AttributeModifier.Operation.ADDITION));
                    playSound(SoundEvents.ARMOR_EQUIP_DIAMOND, 1, 1, true);
                }
            }
        }
        /*
        if (key.equals(AGE_PROGRESS)) {
            System.out.println("SYNCED AGE PROGRESS: "+entityData.get(AGE_PROGRESS));
            setAgeProgress(entityData.get(AGE_PROGRESS));

        }


        /*else if (key == AGE) {
            setAge(entityData.get(AGE));
            updateAgeProgress();
            refreshDimensions();
            float scale = getScale();
            if (scale >= 1) {
                AttributeModifier mod = new AttributeModifier(SCALE_MOD_UUID, "Scale modifier", scale, AttributeModifier.Operation.MULTIPLY_BASE);
                for (Attribute att : getScaledAttributes()) {
                    AttributeInstance instance = getAttribute(att);
                    instance.removeModifier(mod);
                    instance.addTransientModifier(mod);
                }
            }
        }
               */
        else super.onSyncedDataUpdated(key);
    }

    public String getAnimation()
    {
        return entityData.get(ANIMATION);
    }
    public void setAnimation(String animation) {
        entityData.set(ANIMATION, animation);
    }

    public int getAnimationType()
    {
        return entityData.get(ANIMATION_TYPE);
    }
    public void setAnimationType(int animation)
    {
        entityData.set(ANIMATION_TYPE, animation);
    }
    public int getAnimationTime()
    {
        return entityData.get(ANIMATION_TIME);
    }
    public void setAnimationTime(int animationTime)
    {
        entityData.set(ANIMATION_TIME, animationTime);
    }

    public void setBreaching(boolean breaching) {
        entityData.set(BREACHING, breaching);
    }
    public boolean getBreaching() {
        return entityData.get(BREACHING);

    }

    public void setYawUnlocked(boolean unlock) {
        entityData.set(YAW_UNLOCK, unlock);
    }
    public boolean getYawUnlocked() {
        return entityData.get(YAW_UNLOCK);

    }

    public boolean hasEntityDataAccessor(EntityDataAccessor<?> param)
    {
        return entityData.itemsById.containsKey(param.getId());
    }

    public Attribute[] getScaledAttributes() {
        return new Attribute[]{MAX_HEALTH, ATTACK_DAMAGE};
    }

    public float getTravelSpeed() {
        if (isUsingFlyingNavigator() && getAttributes().hasAttribute(FLYING_SPEED)) {
            return (float) getAttributeValue(FLYING_SPEED);
        }
        if (isUsingSwimmingNavigator()) {
            return 10F;
        }
        return (float) getAttributeValue(MOVEMENT_SPEED);
    }

    public static boolean canFlyerSpawn(EntityType<? extends WRDragonEntity> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType spawnType, BlockPos pos, Random random) {
        return serverLevelAccessor.getBlockState(pos.below()).getFluidState().isEmpty();
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    /**
     * AGE PROGRESS CATEGORIES:
     * 0 to 0.5 --> HATCHLING
     * 0.5 TO 1 --> JUVENILE
     * 1 --> ADULT
     */

    public boolean isHatchling() {
        return getAgeProgress() < 0.5f;
    }

    public boolean isJuvenile() {
        return getAgeProgress() >= 0.5f;
    }

    public boolean isAdult() {
        return getAgeProgress() >= 1f;
    }

    public float getAgeProgress() {
        return entityData.get(AGE_PROGRESS);
    }

    //Method automatically refreshes the entity's dimensions, adjusting the bounding box size
    public void setAgeProgress(float ageProgress)
    {
        entityData.set(AGE_PROGRESS,Math.min(ageProgress,1));
        this.refreshDimensions();
    }

    //Amount by which to increase age progress each minute...
    //Age progress starts at 0 and caps out at 1.
    //Hence, if ageProgressAmount = 0.1, it will take 10 minutes for an entity to fully grow
    public abstract float ageProgressAmount();

    //Initial scale for the baby entity...
    //This corresponds to the ratio babyEntitySize / adultEntitySize
    //For example, if initialBabyScale is 0.2, then the entity as a baby starts out at 20% of its adult size
    public abstract float initialBabyScale();


    public float baseRenderScale(){
        return 1.0f;
    }

    /** This is here because usually the child models are smaller than the adult models in block bench
     * However, we want them to be around the same so we can apply the same scale to them.
     * @return the scale that makes the child model the same size as the adult model.
     */
    public float childToAdultScale(){
        return 1.0f;
    }


    //Overrides method in living entity
    //This allows for WRDragonEntity's dimensions to be correctly refreshed when refreshDimensions is called
    @Override
    public float getScale() {
        return initialBabyScale() + ((1 - initialBabyScale()) * getAgeProgress());
    }

    // ====================================
    //      A.2) Entity Data: ARMOR
    // ====================================

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



    // ====================================
    //      A.3) Entity Data: GENDER
    // ====================================

    /**
     * GENDER:
     * 0 --> FEMALE
     * 1 --> MALE
     * Originally determined via coin flip
     */

    public int getGender()
    {
        return entityData.get(GENDER);
    }
    // To make files look nicer
    public String getGenderString(){
        return (getGender() == 0)? "female" : "male";
    }

    public void setGender(int gender)
    {
        entityData.set(GENDER, gender);
    }

    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

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
    public BlockPos getRestrictCenter() {
        BlockPos pos = getHomePos();
        return pos == null? BlockPos.ZERO : pos;
    }


    @Nullable
    public BlockPos getHomePos() {
        BlockPos pos = entityData.get(HOME_POS);
        return pos == BlockPos.ZERO? null : pos;
    }

    public void setHomePos(@Nullable BlockPos pos) {
        entityData.set(HOME_POS, pos == null? BlockPos.ZERO : pos);
    }

    public void clearHome() {
        setHomePos(null);
    }

    @Override
    public boolean hasRestriction() {
        return this.getHomePos() != null;

    }

    @Override
    public void restrictTo(BlockPos pos, int distance) {
        setHomePos(pos);
    }

    @Override
    public boolean isWithinRestriction() {
        return isWithinRestriction(blockPosition());
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos) {
        BlockPos home = getHomePos();
        return home == null || home.distSqr(pos) <= getRestrictRadius();
    }

    public boolean tryTeleportToOwner() {
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

    public boolean defendsHome() {
        return false;
    }

    @Override
    public float getRestrictRadius() {
        return -1;
    }


    // ====================================
    //      A.5) Entity Data: SLEEP
    // ====================================

    public boolean getSleeping() {
        return hasEntityDataAccessor(SLEEPING) && entityData.get(SLEEPING);
    }

    public void setSleeping(boolean sleep) {
        entityData.set(SLEEPING, sleep);
    }

    // ====================================
    //      A.6) Entity Data: SIT
    // ====================================

    public boolean getSitting() {
        return hasEntityDataAccessor(SITTING) && entityData.get(SITTING);
    }

    public void setSitting(boolean sit) {
        entityData.set(SITTING, sit);
        this.setOrderedToSit(sit);
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    public String getDefaultVariant(){
        return "base";
    }

    public String determineVariant()
    {
        return getDefaultVariant();
    }

    public String getVariant()
    {
        return hasEntityDataAccessor(VARIANT) ? entityData.get(VARIANT) : "";

    }

    public void setVariant(String variant)
    {
        entityData.set(VARIANT, variant);
    }


    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || getSleeping()) size = size.scale(1, 0.5f);
        return size;
    }

    @Override
    protected int getExperienceReward(Player player)
    {
        return Math.max((int) ((getBbWidth() * getBbHeight()) * 0.25) + getRandom().nextInt(3), super.getExperienceReward(player));
    }


    public Vec3 getApproximateMouthPos()
    {
        Vec3 position = getEyePosition(1).subtract(0, 0.75d, 0);
        double dist = (getBbWidth() / 2) + 0.75d;
        return position.add(calculateViewVector(getXRot(), yHeadRot).scale(dist));
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(ForgeSpawnEggItem.fromEntityType(getType()));
    }

    public int getEatingCooldown() {
        return entityData.get(EATING_COOLDOWN);

    }

    public void setEatingCooldown(int cooldown) {
        entityData.set(EATING_COOLDOWN, cooldown);
    }

    public int getBreedingCount() {
        return entityData.get(BREEDING_COUNT);

    }

    public void setBreedingCount(int count) {
        entityData.set(BREEDING_COUNT, count);
    }

    public int getBreedingCooldown() {
        return entityData.get(BREEDING_COOLDOWN);

    }

    public void setBreedingCooldown(int cooldown) {
        entityData.set(BREEDING_COOLDOWN, cooldown);
    }


    // ====================================
    //      B) Tick and AI
    // ====================================
    @Override
    public void tick() {


        super.tick();
        setEatingCooldown(Math.max(getEatingCooldown()-1,0));
        setBreedingCooldown(Math.max(getBreedingCooldown()-1,0));

        // X ROTATION
        if (speciesCanFly()) {
            if (this.getNavigationType() != NavigationType.FLYING)
                setDragonXRotation(0, false); // Shouldn't be rotated on ground or water
            if (getDragonXRotation() != 0 && (getDeltaMovement().length() <= 0.25)) { // Every tick, slowly orient the dragon back to normal if its barely moving, so it isn't just awkwardly pointing down or up. Also if the player is upside down.
                setXRotationAndLerp(Mth.approachDegrees(getDragonXRotation(), 0.0f, 1.0f));
            }
            if (xRotLerpSteps > 0){ // Lerp X values so they're smooth! (LivingEntity does this too)
                float oldX = getDragonXRotation();
                float xRot = Mth.wrapDegrees(targetXRotation - oldX);
                setDragonXRotation(oldX + xRot / this.xRotLerpSteps, true);
                xRotLerpSteps--;
            }
        }

        NavigationType properNavigator = getProperNavigator();
        if (properNavigator != this.getNavigationType()) {
            setNavigator(properNavigator);
        }


        // UPDATE AGE:
        // The entity's is updated once every minute (AGE_UPDATE_INTERVAL == 1200)
        // Abstract method ageProgressAmount() sets the float amount by which to update age every minute
        if (!isAdult() && tickCount % AGE_UPDATE_INTERVAL == 0) {
            setAgeProgress(getAgeProgress()+ageProgressAmount());
        }

        //Update sleep timers
        if (sleepCooldown > 0) {
            sleepCooldown = Math.max(sleepCooldown-1,0);
        }

            //YAW OPERATIONS:
            //The following lines of code handle the dynamic yaw animations for entities...
        if (isUsingSwimmingNavigator() && level.isClientSide) {
            //Grab the change in the entity's Yaw, deltaYRot...
            //deltaYaw will tell us in which direction the entity is rotating...
            deltaYRot = this.yRot - prevYRot;
            //Store the previous yaw value, so we can use it next tick to calculate deltaYaw...
            prevYRot = this.yRot;

            //adjustYaw is a local variable that changes to try and match the change in Yaw....
            //So, adjustYaw starts at 0.
            // If it's rotating in the negative direction (deltaYRot negative), adjustYaw will start decreasing to catch up...
            // Likewise, if it's rotating in the positive direction (deltaYRot positive) adjustYaw will start increasing to catch up...
            //The increase or decrease always depends on the adjustment variable. This determines how "fast" adjustYaw will catch up.
            //The max and min functions ensure that adjustYaw doesn't overshoot deltaYRot...
            //Thus, adjustment will determine --how fast-- the pieces of the entity's model change their rotation.
            //The multiplying factor in the corresponding entity's model will determine --how far-- they rotate.
            //We store the prevAdjustYaw value and use this and the current adjustYaw value for partial tick methods.
            prevSetYaw = setYaw;

            if (adjustYaw > deltaYRot) {
                adjustYaw = adjustYaw - adjustmentYaw;
                adjustYaw = Math.max(adjustYaw, deltaYRot);
            } else if (adjustYaw < deltaYRot) {
                adjustYaw = adjustYaw + adjustmentYaw;
                adjustYaw = Math.min(adjustYaw, deltaYRot);
            }
            setYaw = (adjustYaw * (Mth.PI / 180.0F));

            //Troubleshooting:
            // If the rotation "lags behind" (does not change directions fast enough) increase adjustment.
            // If the rotation looks choppy (adjusts too fast), decrease adjustment
            // If the entity seems to "dislocate", reduce the multipliers for bone rotation in the Model class.
            // Reducing rotation multiplier in model class can also reduce choppiness, at the cost of how wide the bone rotation is.

            //PITCH OPERATIONS:
            if (!this.getBreaching()) {
                //Calculate deltaPitch, between our target (xRot) and the previous value we applied to the model...
                deltaPitch = this.xRot - adjustedPitch;
                //Store the current
                currentPitchRadians = adjustedPitch * (Mth.PI / 180.0F);
                //Model "wants" to set its pitch to xRot, however if xRot is changing too fast, we slow down this change...
                if (Mth.abs(deltaPitch) > deltaPitchLimit) {
                    //Increase or Decrease pitch to attempt to reach target value...
                    if (deltaPitch > 0) {
                        adjustedPitch = adjustedPitch + deltaPitchLimit;
                        deltaPitchExtremities = 30;
                    }
                    if (deltaPitch < 0) {
                        adjustedPitch = adjustedPitch - deltaPitchLimit;
                        deltaPitchExtremities = -30;
                    }
                }
                //If we are changing at an acceptable rate, reach the target directly...
                else {
                    adjustedPitch = xRot;
                    deltaPitchExtremities = 0;
                }
                //Head will grab the change in pitch we're applying and speed up ahead of body..
                //Will only do so if we are not overshooting the target position...
                pitchExtremities = adjustedPitch + deltaPitchExtremities;

                //Convert the value to Rads, this will be used by the model class...
                targetPitchRadians = (adjustedPitch * (Mth.PI / 180.0F));
                pitchExtremitiesRadians = (pitchExtremities * (Mth.PI / 180.0F));

                //EXTREMITY PITCH OPERATIONS:
                prevSetExtremityPitch = setExtremityPitch;
                if (adjustExtremityPitch > deltaPitch) {
                    adjustExtremityPitch = adjustExtremityPitch - adjustmentExtremityPitch;
                    adjustExtremityPitch = Math.max(adjustExtremityPitch, deltaPitch);
                } else if (adjustExtremityPitch < deltaPitch) {
                    adjustExtremityPitch = adjustExtremityPitch + adjustmentExtremityPitch;
                    adjustExtremityPitch = Math.min(adjustExtremityPitch, deltaPitch);
                }
                setExtremityPitch = (adjustExtremityPitch * (Mth.PI / 180.0F));

            } else {
                //If we are breaching, ignore previous logic, do fast rotations...
                targetPitchRadians = (float) -((Mth.atan2((this.getDeltaMovement().y), Mth.sqrt((float) ((this.getDeltaMovement().x) * (this.getDeltaMovement().x) + (this.getDeltaMovement().z) * (this.getDeltaMovement().z))))));
                currentPitchRadians = targetPitchRadians;
            }
        }

    }


    public void clearAI() {
        jumping = false;
        navigation.stop();
        if (lookControl instanceof WRGroundLookControl) {
            ((WRGroundLookControl) lookControl).stopLooking();
        }
        if (lookControl instanceof WRSwimmingLookControl) {
            ((WRSwimmingLookControl) lookControl).stopLooking();
        }
        setTarget(null);
        setSpeed(0);
        setYya(0);
    }



    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================
    public void attackInBox(AABB box)
    {
        attackInBox(box, 0);
    }

    public void attackInBox(AABB box, int disabledShieldTime)
    {
        List<LivingEntity> attackables = level.getEntitiesOfClass(LivingEntity.class, box, entity -> entity != this && !hasPassenger(entity) && wantsToAttack(entity, getOwner()));
        for (LivingEntity attacking : attackables) {
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
    public boolean hurt(DamageSource source, float amount)
    {
        if (isImmuneToArrows() && source.getDirectEntity() != null) {
            EntityType<?> attackSource = source.getDirectEntity().getType();
            if (attackSource == EntityType.ARROW) return false;
            else if (attackSource == WREntityTypes.GEODE_TIPPED_ARROW.get()) amount *= 0.5f;
        }



        setSleeping(false);
        setSitting(false);
        return super.hurt(source, amount);
    }

    @Deprecated
    public boolean isImmuneToArrows() {
        return false;
    }


    public List<LivingEntity> getEntitiesNearby(double radius, Predicate<LivingEntity> filter)
    {
        return level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), filter.and(e -> e != this));
    }

    @Override
    public boolean doHurtTarget(Entity entity)
    {
        return !isAlliedTo(entity) && super.doHurtTarget(entity);
    }


    @Override // We shouldnt be targetting pets...
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner)
    {
        return !isAlliedTo(target);
    }



    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        if (isRiding() && source == DamageSource.IN_WALL) return true;
        if (isImmuneToArrows() && source == DamageSource.CACTUS) return true;
        return super.isInvulnerableTo(source);
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================


    public void setNavigator(NavigationType navigator) {
        if (navigator == getNavigationType()) {
            return;
        }
        switch (navigator) {
            case GROUND -> {
                this.moveControl = new WRGroundMoveControl(this, groundMaxYaw);
                this.lookControl = new WRGroundLookControl(this);
                this.navigation = new WRGroundNavigation(this, this.level);
            }
            case FLYING -> {
                this.moveControl = new FlyerMoveController(this);
                this.lookControl = new WRFlyLookControl(this,10);
                this.navigation = new FlyerPathNavigator(this);
                setOrderedToSit(false);
                setSleeping(false);
            }
            case SWIMMING -> {
                this.moveControl = new WRSwimmingMoveControl(this);
                this.lookControl = new WRSwimmingLookControl(this, 10);
                this.navigation = new WRSwimmingNavigator(this);
            }
        }
    }



    public NavigationType getProperNavigator(){
        //Priority order:
        //1.- Swimming
        //2.- Flying
        //3.- Ground

        //All the checks for speciesCanX is performed here...
        //We only check the shouldCondition if it makes sense according to the speciesCanX...
        boolean shouldUseFlyingNavigator = false;
        boolean shouldUseSwimmingNavigator = false;
        NavigationType navigationType = getNavigationType();
        boolean isUsingSwimmingNavigator = (navigationType == NavigationType.SWIMMING);
        boolean isUsingFlyingNavigator = (navigationType == NavigationType.FLYING);

        if (speciesCanSwim()) {
            //Local variable to avoid multiple method calls..
            shouldUseSwimmingNavigator = shouldUseSwimmingNavigator();
            if (shouldUseSwimmingNavigator != isUsingSwimmingNavigator) {
                return shouldUseSwimmingNavigator ? NavigationType.SWIMMING : NavigationType.GROUND;
            }
        }
        if (speciesCanFly()) {
            shouldUseFlyingNavigator = shouldUseFlyingNavigator();
            if (shouldUseFlyingNavigator != isUsingFlyingNavigator) {
                return NavigationType.FLYING;
            }
        }
        if (speciesCanWalk()) {
            if (!shouldUseFlyingNavigator && !shouldUseSwimmingNavigator) {
                return NavigationType.GROUND;
            }
        }
        //None of the conditions are met, we keep using the previous navigator.
        return navigationType;
    }

    public NavigationType getNavigationType(){
        PathNavigation navigation = this.getNavigation();
        if (navigation instanceof WRSwimmingNavigator){
            return NavigationType.SWIMMING;
        }
        else if (navigation instanceof FlyerPathNavigator){
            return NavigationType.FLYING;
        }
        else if (navigation instanceof GroundPathNavigation){
            return NavigationType.GROUND;
        }
        return NavigationType.GROUND;
    }

    @Override
    public float getStepHeight() {
        return 1;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new WRBodyControl(this);
    }


    //Handles travel methods for the DragonEntity. If needed, can be overriden in specific subclasses.
    @Override
    public void travel(Vec3 vec3d) {
        // Check if the entity is a vehicle and can be controlled by the rider
        if (this.isVehicle() && this.canBeControlledByRider()) {
            // Return early if the entity is not alive
            if (!this.isAlive()) return;

            LivingEntity rider = (LivingEntity) this.getControllingPassenger();

            // Store previous yaw value
            this.yRotO = this.getYRot();

            // While being ridden, entity's pitch = 0.5 of rider's pitch
            this.setXRot(rider.getXRot() * 0.5F);

            // While being ridden, entity's yaw = rider's yaw
            this.setYRot(rider.getYRot());

            // Client (rendering): Align body to entity direction
            this.yBodyRot = this.getYRot();

            // Client (rendering): Align head to body
            this.yHeadRot = this.yBodyRot;

            // This should allow for strafing
            float sideMotion = rider.xxa * 0.5F;

            // This allows for moving forward
            float forwardMotion = rider.zza;

            if (forwardMotion < 0.0F) { // Huh? Ig I'll keep it here because it works
                forwardMotion *= 0.25F; // Ohhh it's like if you're going backward you're slower I guess.
            }

            // ToDo: What is this flying speed case?
            this.flyingSpeed = this.getSpeed() * 0.1F;

            // Handle movement based on navigator type
            if (this.isControlledByLocalInstance()) {
                float speed = getTravelSpeed();
                if (isUsingFlyingNavigator()) {
                    handleFreeFlyingRiding(speed, rider); // Free Flying (Diving, 180s, etc.)
                    // else handleCombatFlyingMovement(speed, livingentity); // Combat flying (More controlled flight)
                } else if (isUsingSwimmingNavigator()) {
                    handleWaterRiding(5, sideMotion, 5, vec3d, rider);
                } else {
                    handleGroundRiding(speed, sideMotion, forwardMotion, vec3d, rider);
                }
            }

            this.calculateEntityAnimation(this, isUsingFlyingNavigator());
            this.tryCheckInsideBlocks();
        } else {
            // For non-vehicle entities, use default travel behavior
            this.flyingSpeed = getTravelSpeed();
            super.travel(vec3d);
        }
    }


    private static final float SPEED_COEFFICIENT = 75f/7f;
    private float currentSpeed = 0f;
    // Separated these methods to make it look cleaner. Also allows for subclasses to possibly override them if need be.
    protected void handleFreeFlyingRiding(float speed, LivingEntity livingentity) {
        // Convert to ground nav if applicable
        if (getAltitude() <= getFlightThreshold()) {
            setNavigator(NavigationType.GROUND);
        }


            //Set rotation based on input
        if (getDeltaMovement().length() >= 0.25) { // Don't allow them to rotate unless they're moving a lot!
            float rotationChange = ClientEvents.getClient().options.keyJump.isDown() ? 2.0f : WRKeybind.FLIGHT_DESCENT.isDown() ? -3.0f : 0;
            setXRotationAndLerp(targetXRotation + rotationChange);
        }

        // Acceleration
        float acceleration = ClientEvents.getClient().options.keyUp.isDown()? 0.02f : -0.01f; // TODO accelerate faster depending on dragon?
        currentSpeed = Mth.clamp(currentSpeed + acceleration, 0f, speed*SPEED_COEFFICIENT);

            // Set direction to travel
        Vec3 lookVec = Vec3.directionFromRotation(-getDragonXRotation(), livingentity.getYRot());
        Vec3 moveVec = new Vec3(xxa, lookVec.y, zza);
        //if (moveVec.y!=0) moveVec.multiply(1, 0.5f, 1); // Half upward movement

        this.setSpeed(currentSpeed);
        super.travel(moveVec);
    }


    public void setDragonXRotation(float rotation, boolean wrapDegrees){
        float rot = (wrapDegrees)?
                (float) (rotation + Math.ceil(-rotation / 360) * 360)
                : rotation;
        getEntityData().set(DRAGON_X_ROTATION, rot);
    }
    public void setXRotationAndLerp(float targetXRotation){
        this.targetXRotation = targetXRotation;
        xRotLerpSteps = 6;
    }



    public float getDragonXRotation(){
        return getEntityData().get(DRAGON_X_ROTATION);

    }

    protected void handleGroundRiding(float speed, float groundX, float groundZ, Vec3 vec3d, LivingEntity livingentity) {
        // normal movement
        if (ClientEvents.getClient().options.keyJump.isDown() && getBlockStateOn().getMaterial().isSolid() && speciesCanFly()) {
            jumpFromGround(); // Jump when on the ground, for taking off.
        }
        if (dragonCanFly() && getAltitude() > getFlightThreshold() + 1) {
            setNavigator(NavigationType.FLYING);
        }
        else {
            setSpeed(speed);
            super.travel(new Vec3(groundX, vec3d.y, groundZ));
        }
    }


    // Will be used for BFL, etc.
    protected void handleWaterRiding(float speed, float waterX, float waterZ, Vec3 vec3d,  LivingEntity livingentity){
        setSpeed(speed);
        super.travel(new Vec3(5, vec3d.y, 5));
    }

    // ====================================
    @Override
    public boolean isNoGravity() {
        return isUsingFlyingNavigator();
    }

    public boolean isIdling()
    {
        return getNavigation().isDone() && getTarget() == null && !isVehicle() && (speciesCanSwim() || !isInWaterOrBubble()) && !isUsingFlyingNavigator();
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


    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;

    }

    public abstract boolean speciesCanWalk();


    public boolean isUsingLandNavigator() {
        return (getNavigation() instanceof GroundPathNavigation);
    }

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    public abstract boolean speciesCanFly();

    public boolean shouldUseFlyingNavigator() {
        if (getAltitude() > 1) {
            if (!speciesCanSwim() && isUnderWater()) {
                return false;
            }
            return canLiftOff();
        }
        return false;
    }

    public boolean canLiftOff() {
        if (!dragonCanFly()) {
            return false;
        }

        if (!onGround) {
            return true; // We can't lift off the ground in the air...
        }

        int heightDiff = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) getX(), (int) getZ()) - (int) getY();
        if (heightDiff > 0 && heightDiff <= getFlightThreshold())
            return false; // position has too low of a ceiling, can't fly here.
        return true;
    }

    public boolean dragonCanFly()
    {
        return isJuvenile() && !isLeashed() && speciesCanFly();
    }

    public boolean isDiving(){
        return getDragonXRotation() > 270.0f && getDragonXRotation() < 320.0f;
    }
    public boolean isFlyingUpward() {return getDragonXRotation() > 25.0f && getDragonXRotation() < 90.0f;}
    public boolean isGliding() { return getDragonXRotation() >= 320.0f && getDragonXRotation() < 340.0f;}

    public boolean isUsingFlyingNavigator()
    {
        return getNavigation() instanceof FlyerPathNavigator;
    }

    public double getAltitude() {
        BlockPos.MutableBlockPos pos = blockPosition().mutable();

        // cap to the level void (y = -64)
        while (pos.getY() > -64 && !level.getBlockState(pos.move(Direction.DOWN)).getMaterial().isSolid());
        return getY() - pos.getY();
    }

    public int getFlightThreshold() {
        return (int) getBbHeight();
    }

    public int getYawRotationSpeed() {
        return isUsingFlyingNavigator()? 6 : 75;
    }

    //TODO: ???
    public void flapWings() {
        playSound(WRSounds.WING_FLAP.get(), 3, 1, false);
        setDeltaMovement(getDeltaMovement().add(0, 1.285, 0));
    }

    @Override
    protected float getJumpPower() {
        return dragonCanFly()? (getBbHeight() * getBlockJumpFactor()) * 0.6f : super.getJumpPower();
    }

    @Override // Disable fall calculations if we can fly (fall damage etc.)
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return !dragonCanFly() && super.causeFallDamage(distance - (int) (getBbHeight() * 0.8), damageMultiplier, source);
    }

    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================

    //Only return true for "true swimmers" (can swim and dive)
    public abstract boolean speciesCanSwim();

    public boolean isUsingSwimmingNavigator() {
        return getNavigation() instanceof WRSwimmingNavigator;
    }

    public boolean shouldUseSwimmingNavigator() {
        //If it cannot fly, and it's not in water, and it's not on the ground either, it's falling..
        //Use swimming navigator here for animation purposes
        if (!speciesCanFly() && !this.isInWater() && !this.isOnGround()) {
            return true;
        }
        return speciesCanFly()?this.isUnderWater():this.isInWater();
    }


    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================

    public abstract boolean speciesCanBeRidden();

    public int getMaxPassengers(){
        return 1;
    }

    @Override
    public void rideTick() {
        //Method only gets called when the dragon is riding something; not when something is riding the dragon
        super.rideTick();
        Entity entity = getVehicle();

        if (entity == null || !entity.isAlive()) {
            stopRiding();
            return;
        }

        setDeltaMovement(Vec3.ZERO);
        clearAI();

        if (entity instanceof Player player) {
            int index = player.getPassengers().indexOf(this);
            if ((player.isShiftKeyDown() && !player.getAbilities().flying) || isInWater() || index > 2) {
                stopRiding();
                setOrderedToSit(false);
                return;
            }

            setXRot(player.getXRot() / 2);
            yHeadRot = yBodyRot = yRotO  = player.getYRot();
            setYRot(yHeadRot);
            setRotation(

                    player.yHeadRot, player.getXRot());

            Vec3 vec3d = getRidingPosOffset(index);
            if (player.isFallFlying())
            {
                if (!dragonCanFly())
                {
                    stopRiding();
                    return;
                }

                vec3d = vec3d.scale(1.5);
                setNavigator(NavigationType.FLYING);
            }
            Vec3 pos = WRMathsUtility.rotateXZVectorByYawAngle(player.yBodyRot, vec3d.x, vec3d.z).add(player.getX(), player.getY() + vec3d.y, player.getZ());
            setPos(pos.x, pos.y, pos.z);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Vec3 getRidingPosOffset(int passengerIndex) {
        double x = getBbWidth() * 0.5d + getVehicle().getBbWidth() * 0.5d;
        switch (passengerIndex)
        {
            default:
            case 0:
                return new Vec3(0, 1.81, 0);
            case 1:
                return new Vec3(x, 1.38d, 0);
        }
    }

    @Override
    public void positionRider(Entity passenger) {
        Vec3 offset = getPassengerPosOffset(passenger, getPassengers().indexOf(passenger));
        //We have an offset, for the passenger, from the entity it is riding...
        //i.e: A vector that points from the entity to the passenger...
        //However, we do not position the passenger relative to the entity, we position them relative to an absolute coordinate system...
        //Hence, we must turn this offset vector, relative to the entity, into one relative to Minecraft's coordinates...
        //To do this, we apply a transformation to the offset vector...
        Vec3 pos = WRMathsUtility.rotateXZVectorByYawAngle(yBodyRot, offset.x, offset.z).add(getX(), getY() + offset.y + passenger.getMyRidingOffset(), getZ());
        passenger.setPos(pos.x, pos.y, pos.z);
    }

    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        //Note: Method will, probably, be Overriden in child classes
        return new Vec3(0, getPassengersRidingOffset(), 0);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (getControllingPassenger() == passenger && isOwnedBy((LivingEntity) passenger)) {
            clearAI();
            setOrderedToSit(false);
            clearHome();
            if (isLeashed()) dropLeash(true, true);
        }
    }

    @Nullable
    public Player getControllingPlayer() {
        Entity passenger = getControllingPassenger();
        return passenger instanceof Player? (Player) passenger : null;
    }

    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player)
    {
    }

    /**
     * Recieve the keybind message from the current controlling passenger.
     *
     * @param key     shut up
     * @param mods    the modifiers that is pressed when this key was pressed (e.g. shift was held, ctrl etc {@link org.lwjgl.glfw.GLFW})
     * @param pressed true if pressed, false if released. pretty straight forward idk why ur fucking asking.
     */
    public void receivePassengerKeybind(int key, int mods, boolean pressed) {
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return false;
    }

    //Do not perform AI actions while entity is being ridden
    // Do *NOT* check for Sleeping, as this is now a Goal and entity's AI must still work while asleep
    @Override
    public boolean isImmobile() {
        return super.isImmobile() || isRiding();
    }


    @Override
    // Only OWNERS can control their pets
    public boolean canBeControlledByRider() {
        Entity entity = getControllingPassenger();
        return entity instanceof Player && isOwnedBy(((Player) entity));
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty()? null : passengers.get(0);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return isTame() && speciesCanBeRidden() && getPassengers().size() < getMaxPassengers();
    }

    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }

    //attemptTame runs when the taming conditions are met, only for tameables
    public boolean attemptTame(float tameSucceedChance, @Nullable Player tamer) {
        if (level.isClientSide) {
            return false;
        }
        //Checks if we hit the probability threshold and if the event was NOT canceled
        if (getRandom().nextDouble() < tameSucceedChance && !ForgeEventFactory.onAnimalTame(this, tamer)) {
            tame(tamer);
            setHealth(getMaxHealth());
            clearAI();
            level.broadcastEntityEvent(this, (byte) 7); // heart particles
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isAlliedTo(Entity entity)
    {
        if (entity == this) return true;
        if (entity instanceof LivingEntity && isOwnedBy(((LivingEntity) entity))) return true;
        if (entity instanceof TamableAnimal && getOwner() != null && getOwner().equals(((TamableAnimal) entity).getOwner()))
            return true;
        return entity.isAlliedTo(getTeam());
    }

    /**
     * This is no longer to be overriden in each dragon class.
     * Instead, override #canEquipArmor, #canEquipSaddle, etc.
     * @param container to add things to
     */
    public void applyTomeInfo(NewTarragonTomeContainer container){
        if (canEquipSaddle()){
            container.addSaddleSlot();
        }
        if (canEquipArmor()){
            container.addArmorSlot();
        }
        if (canEquipChest()){
            container.addChestSlot();
        }
        if (canEquipSpecialItem() != null){
            container.addExtraSlot(canEquipSpecialItem());
        }
    }

    /*{
        container.addAction(BookActions.HOME, BookActions.SIT)
                .addTooltip(getName())
                .addTooltip(new TextComponent(Character.toString('\u2764'))
                        .withStyle(ChatFormatting.RED)
                        .append(new TextComponent(String.format(" %s / %s", (int) (getHealth() / 2), (int) getMaxHealth() / 2))
                                .withStyle(ChatFormatting.WHITE)));

        if (hasEntityDataAccessor(GENDER))
        {
            String gender = getGender();
            Boolean isMale = (gender.equals("male"));
            container.addTooltip(new TranslatableComponent("entity.wyrmroost.dragons.gender." + gender)
                    .withStyle(isMale? ChatFormatting.DARK_AQUA : ChatFormatting.RED));
        }
    }*/

    @Override
    public boolean isPickable()
    {
        return super.isPickable() && !isRiding();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this instanceof  ITameable && !isTame()){
            if (((ITameable)this).tameLogic(player,stack) == InteractionResult.SUCCESS) {
                this.setHomePos(new BlockPos(this.position()));
                return InteractionResult.SUCCESS;
            } return ((ITameable)this).tameLogic(player,stack);
        }



        if (this instanceof IBreedable && getBreedingCooldown() <= 0 && getBreedingCount() < ((IBreedable)this).getBreedingLimit()) {
            IBreedable thisIBreedable = (IBreedable) this;
            if(thisIBreedable.breedLogic(player,stack) == InteractionResult.SUCCESS){
                return thisIBreedable.breedLogic(player,stack);
            }
        }

        if (isOwnedBy(player) && isFood(stack)) {
            if (getEatingCooldown() <= 0) {
                eat(this.level, stack);
                setEatingCooldown(500);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

        }

        if (isOwnedBy(player) && getPassengers().isEmpty() && canAddPassenger(player)) {
          player.startRiding(this);
          travelX0 = this.position().x;
          travelY0 = this.position().y;
          travelZ0 = this.position().z;
        }


        if (!isOwnedBy(player) && canBeControlledByRider() && canAddPassenger(player)) {
            player.startRiding(this);
        }

        if (isOwnedBy(player) && player.isShiftKeyDown()) {
            setSitting(!getSitting());
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void dropLeash(boolean sendPacket, boolean dropLead) {
        super.dropLeash(sendPacket, dropLead);
        clearHome();
    }

    @Override
    public Component getDisplayName()    {
        return super.getDisplayName();
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {

    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory.isPresent() && !getInventory().isEmpty())
            return inventory.cast();
        return super.getCapability(capability, facing);
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.map(i -> i.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    /**
     * It is VERY important to be careful when using this.
     * It is VERY sided-ness sensitive. If not done correctly, it can result in the loss of items! <P>
     * {@code if (!level.isClient) setStackInSlot(...)}
     */
    public void setStackInSlot(int slot, ItemStack stack) {
        inventory.ifPresent(i -> i.setStackInSlot(slot, stack));
    }
    public DragonInventory getInventory() {
        return inventory.orElseThrow(() -> new NoSuchElementException("This boi doesn't have an inventory wtf are u doing"));
    }

    public boolean canEquipSaddle() {
        return false;
    }
    public boolean canEquipArmor() {
        return false;
    }
    public boolean canEquipChest() {
        return false;
    }
    @Nullable
    public Predicate<ItemStack> canEquipSpecialItem() {
        return null;
    }

    public boolean isSaddled() {
        return canEquipSaddle() && entityData.get(SADDLED);
    }
    public boolean isChested() {
        return canEquipChest() && entityData.get(CHESTED);
    }

    @Override
    protected void dropEquipment() {
        inventory.ifPresent(i -> i.getContents().forEach(this::spawnAtLocation));
    }

    public void dropStorage() {
    }

    public DragonInventory createInv() {
        return null;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playersInv, Player player)
    {
        //System.out.println(new BookContainer(id, playersInv, this));
        return new NewTarragonTomeContainer(id, playersInv, this);
    }


    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    public boolean isBreedingItem(ItemStack stack) {
        return isFood(stack);
    }

    @Override
    public abstract boolean isFood(ItemStack stack);

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack eat(Level level, ItemStack stack) {
        Vec3 mouth = getApproximateMouthPos();
        //ClientSide: Spawn Particles + sound
        if (level.isClientSide) {
            WRModUtils.playLocalSound(level, new BlockPos(mouth), getEatingSound(stack), 1f, 1f);
        }
        else {
            //ServerSide: Heal
            final float maxHealth = getMaxHealth();
            if (getHealth() < maxHealth) {
                heal(Math.max((int) maxHealth / 5, 4)); // Base healing on max health, minimum 2 hearts.
            }

            Item item = stack.getItem();
            //Apply possible item effects
            if (item.isEdible()) {
                for (Pair<MobEffectInstance, Float> pair : item.getFoodProperties().getEffects())
                    if (!level.isClientSide && pair.getFirst() != null && getRandom().nextFloat() < pair.getSecond()) {
                        addEffect(new MobEffectInstance(pair.getFirst()));
                    }
            }

            if (item.hasContainerItem(stack)) {
                spawnAtLocation(item.getContainerItem(stack), (float) (mouth.y - getY()));

            }
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public void heal(float healAmount) {
        super.heal(healAmount);
        level.broadcastEntityEvent(this, HEAL_PARTICLES_EVENT_ID);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return (AgeableMob) getType().create(level);
    }

    public void spawnDragonEgg(WRDragonEntity dragonEntity, int hatchTime) {
        ItemStack dragonEggItemStack = WRItems.DRAGON_EGG.get().getItemStack(dragonEntity, hatchTime);
        ItemEntity eggItem = new ItemEntity(level, getX(), getY(), getZ(), dragonEggItemStack);
        eggItem.setDeltaMovement(0, getBbHeight() / 3, 0);
        level.addFreshEntity(eggItem);
    }

    @Override
    public boolean canMate(Animal mate) {
        if (!(mate instanceof WRDragonEntity)) return false;
        WRDragonEntity dragon = (WRDragonEntity) mate;
        if (isInSittingPose() || dragon.isInSittingPose()) return false;
        if (hasEntityDataAccessor(GENDER) && (getGender()) == (dragon.getGender())) return false;
        return super.canMate(mate);
    }

    // ====================================
    //      E) Client
    // ====================================

    /**
     * This gets the location of the dragon's image for the tarragon tome.
     * X is the amount down, Y is the amount right. (Starts at 0)
     * For example, for a melanistic Royal Red, it would be Vec2(5,1)
     * @returns the location of the depiction in dragon_depictions.png
     */
    public abstract Vec2 getTomeDepictionOffset();
    @Override
    public void handleEntityEvent(byte id) {
        if (id == HEAL_PARTICLES_EVENT_ID)
        {
            for (int i = 0; i < getBbWidth() * getBbHeight(); ++i)
            {
                double x = getX() + WRMathsUtility.nextDouble(getRandom()) * getBbWidth() + 0.4d;
                double y = getY() + getRandom().nextDouble() * getBbHeight();
                double z = getZ() + WRMathsUtility.nextDouble(getRandom()) * getBbWidth() + 0.4d;
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0);
            }
        }
        else super.handleEntityEvent(id);
    }

    public void doSpecialEffects() {}

    // ====================================
    //      E.1) Client: Sounds
    // ====================================
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
    /*
    @Override
    public float getVoicePitch()
    {
        return ((random.nextFloat() - random.nextFloat()) * 0.2f + 1) * (2 - ageProgress());
    }

     */

    @Override
    public void playAmbientSound()
    {
        if (!getSleeping()) super.playAmbientSound();
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        // Does nothing by default. usually steps will be handled in locomotionListener
    }

    /**
     * @return The sound you want to play based on the animation.
     */
    private <E extends WRDragonEntity> void locomotionListener(SoundKeyframeEvent<E> event){
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null){
            locomotionSoundEvent(event, player, event.getController().getCurrentAnimation().animationName);
        }
    }
    protected <E extends WRDragonEntity> void locomotionSoundEvent(SoundKeyframeEvent<E> event, LocalPlayer player, String anim){

    }
    // ====================================
    //      E.2) Client: Camera
    // ====================================

    // TODO maybe change to a Vec3 when needed? (getMountCameraOffset).
    public float getMountCameraYOffset(){
        return 0.0f;
    }

    // ====================================
    //      F) Goals
    // ====================================
}


