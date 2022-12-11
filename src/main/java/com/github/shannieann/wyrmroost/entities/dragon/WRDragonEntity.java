package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.sound.FlyingSound;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.WRSwimControl;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.WRSwimmingNavigator;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.AnimatedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.*;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.WRSitGoal;
import com.github.shannieann.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.DragonArmorItem;
import com.github.shannieann.wyrmroost.items.DragonEggItem;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRKeybind;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.Mafs;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
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

    private int sleepCooldown;
    public int breedCount;
    private float ageProgress = 1;

    //Only for swimmers:
    public float prevYRot;
    public float deltaYRot;
    public float adjustYaw;
    public float adjustmentYaw;
    public float prevSetYaw;
    public float setYaw;

    public float deltaPitch;
    public float adjustedPitch;
    public float adjustmentPitch;
    public float deltaPitchLimit;
    public float targetPitchRadians;
    public float currentPitchRadians;


    public enum NavigationType {
        GROUND,
        FLYING,
        SWIMMING
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final EntitySerializer<WRDragonEntity> SERIALIZER = EntitySerializer.builder(b -> b
            .track(EntitySerializer.POS.optional(), "HomePos", t -> Optional.ofNullable(t.getHomePos()), (d, v) -> d.setHomePos(v.orElse(null)))
            .track(EntitySerializer.STRING, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant)
            .track(EntitySerializer.BOOL, "Sleeping", WRDragonEntity::isSleeping, WRDragonEntity::setSleeping)
            .track(EntitySerializer.INT, "BreedCount", WRDragonEntity::getBreedCount, WRDragonEntity::setBreedCount));

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;

    @Deprecated // https://github.com/MinecraftForge/MinecraftForge/issues/7622
    public final LazyOptional<DragonInventory> inventory;
    public final LerpedFloat sleepTimer = LerpedFloat.unit();

    public static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<ItemStack> ARMOR = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.ITEM_STACK);
    //TODO: These two are probably better to do inside an interface, IBreachable
    public static final EntityDataAccessor<Float> BREACH_ATTACK_COOLDOWN = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> BREACHING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);


    public static final EntityDataAccessor<String> GENDER = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.STRING);
    //TODO: What is this?
    private static final UUID SCALE_MOD_UUID = UUID.fromString("81a0addd-edad-47f1-9aa7-4d76774e055a");
    private static final int AGE_UPDATE_INTERVAL = 200;
    protected static int IDLE_ANIMATION_VARIANTS;
    protected static int ATTACK_ANIMATION_VARIANTS;
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
    private static final EntityDataAccessor<Integer> MOVING_STATE = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> MANUAL_ANIMATION_CALL = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_MOVING_ANIMATION = SynchedEntityData.defineId(WRDragonEntity.class, EntityDataSerializers.BOOLEAN);

    protected WRDragonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        maxUpStep = 1;
        this.noCulling = true;
        DragonInventory inv = createInv();
        inventory = LazyOptional.of(inv == null? null : () -> inv);
        lookControl = new LessShitLookController(this);

        //TODO: DEFAULT NAVIGATORS


    }

    // =====================
    //      Animation Logic
    // =====================

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "generalController", 0, this::generalPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public <E extends IAnimatable> PlayState generalPredicate(AnimationEvent<E> event)
    {
        //TODO: Test pushing entities and moving?
        String animation = this.getAnimation();
//      Boolean playingAnimation = this.getPlayingAnimation();
        //If we do have an Ability animation play that
        if (!animation.equals("base")) {
            //If it's a moving animation, run that logic, add the ability animation to a moving animation..
            int animationType = this.getAnimationType();
            //Ability + move Animations:
            if (this.getIsMovingAnimation()) {
                //Ability + move (slow) Animations:
                if (this.getDeltaMovement().length() !=0 && !this.isAggressive()) {
                    int movingState = this.getMovingState();
                    switch (movingState) {
                        case 0 -> animation = "walk_" + animation;
                        case 1 -> animation = "fly_" + animation;
                        case 2 -> animation = "swim_" + animation;
                    }
                    event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                }
                //Ability + move (fast) Animations:
                if (this.getDeltaMovement().length() !=0 && !this.isAggressive()) {
                    int movingState = this.getMovingState();
                    animation = switch (movingState) {
                        case 0 -> "walk_fast_" + animation;
                        case 1 -> "fly_fast_" + animation;
                        case 2 -> "swim_fast_" + animation;
                        default -> animation;
                    };
                    event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                }
            }
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
        /*
        //Death
        if ((this.dead || this.getHealth() < 0.01 || this.isDeadOrDying())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }

        */
        //This moving only plays if it's *just* moving and not doing anything else, as its only reached under those conditions...
        int movingState = this.getMovingState();
        if (this.getDeltaMovement().length() !=0 && this.isAggressive()) {
            switch (movingState) {
                case 0 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_fast", ILoopType.EDefaultLoopTypes.LOOP));
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly_fast", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim_fast", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        if (this.getDeltaMovement().length() !=0 && !this.isAggressive()) {
            switch (movingState) {
                case 0 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        //Idle:
        if (this.getRandom().nextDouble() < 0.001) {
            int idleVariant = this.random.nextInt(IDLE_ANIMATION_VARIANTS)+1;
            event.getController().setAnimation(new AnimationBuilder().  addAnimation("idle"+idleVariant, ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        event.getController().setAnimation(new AnimationBuilder().  addAnimation("base", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    // ====================================
    //      A) Entity Data
    // ====================================


    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(ANIMATION, "base");
        this.entityData.define(ANIMATION_TYPE, 1);
        this.entityData.define(MOVING_STATE, 0);
        this.entityData.define(ANIMATION_TIME, 0);
        this.entityData.define(MANUAL_ANIMATION_CALL, false);
        this.entityData.define(IS_MOVING_ANIMATION, false);
        entityData.define(HOME_POS, BlockPos.ZERO);
        entityData.define(AGE, 0);
        entityData.define(BREACHING, false);
        entityData.define(BREACH_ATTACK_COOLDOWN, 600F);
        entityData.define(GENDER, "male");
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, "base0");
        entityData.define(ARMOR, ItemStack.EMPTY);
        super.defineSynchedData();
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        if (inventory.isPresent()) nbt.put("Inv", inventory.orElse(null).serializeNBT());
        ((EntitySerializer<WRDragonEntity>) getSerializer()).serialize(this, nbt);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        if (inventory.isPresent()) inventory.orElse(null).deserializeNBT(nbt.getCompound("Inv"));
        ((EntitySerializer<WRDragonEntity>) getSerializer()).deserialize(this, nbt);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData data, @javax.annotation.Nullable CompoundTag dataTag)
    {
        String gender;
        if (getRandom().nextBoolean()){
            gender = "male";
        } else {
            gender = "female";
        }
        if (hasEntityDataAccessor(GENDER)) {
            setGender(gender);
        }
        if (hasEntityDataAccessor(VARIANT)) {
            setVariant(determineVariant());
        }
        return super.finalizeSpawn(level, difficulty, reason, data, dataTag);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(SLEEPING) || isUsingFlyingNavigator() || key.equals(TamableAnimal.DATA_FLAGS_ID)) {
            refreshDimensions();
            if (level.isClientSide  && isUsingFlyingNavigator() && canBeControlledByRider())
                FlyingSound.play(this);
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
        } else if (key == AGE) {
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
        } else super.onSyncedDataUpdated(key);
    }


    public EntitySerializer<? extends WRDragonEntity> getSerializer(){
        return SERIALIZER;
    }


    public String getAnimation()
    {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {entityData.set(ANIMATION, animation);}

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

    public boolean getManualAnimationCall() {
        return entityData.get(MANUAL_ANIMATION_CALL);
    }

    public void setManualAnimationCall(boolean manualAnimationCall)
    {
        entityData.set(MANUAL_ANIMATION_CALL, manualAnimationCall);
    }

    public boolean getIsMovingAnimation() {
        return entityData.get(IS_MOVING_ANIMATION);
    }

    public void setIsMovingAnimation(boolean movingAnimation)
    {
        entityData.set(IS_MOVING_ANIMATION, movingAnimation);
    }


    public int getMovingState()
    {
        return entityData.get(MOVING_STATE);
    }

    public void setMovingState(int movingState)
    {
        entityData.set(MOVING_STATE, movingState);
    }


    public void setBreaching(boolean breaching) {
        entityData.set(BREACHING, breaching);


    }

    public boolean getBreaching() {
        return entityData.get(BREACHING);

    }


    public void setBreachAttackCooldown(float breachCooldown) {
        entityData.set(BREACH_ATTACK_COOLDOWN, breachCooldown);

    }

    public float getBreachAttackCooldown() {
        return entityData.get(BREACH_ATTACK_COOLDOWN);

    }

    public boolean hasEntityDataAccessor(EntityDataAccessor<?> param)
    {
        return entityData.itemsById.containsKey(param.getId());
    }

    public Attribute[] getScaledAttributes()
    {
        return new Attribute[]{MAX_HEALTH, ATTACK_DAMAGE};
    }

    public float getTravelSpeed()
    {
        //@formatter:off
        return isUsingFlyingNavigator()? (float) getAttributeValue(FLYING_SPEED)
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    public static boolean canFlyerSpawn(EntityType<? extends WRDragonEntity> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType spawnType, BlockPos pos, Random random)
    {
        return serverLevelAccessor.getBlockState(pos.below()).getFluidState().isEmpty();
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================
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
    public String getGender()
    {
        return (!hasEntityDataAccessor(GENDER) || entityData.get(GENDER).equals("male")) ? "male" : "female";
    }

    public void setGender(String gender)
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


    // ====================================
    //      A.5) Entity Data: SLEEP
    // ====================================

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
                setManualAnimationCall(true);
                setIsMovingAnimation(false);
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


    public boolean shouldWakeUp()
    {
        return level.isDay() && getRandom().nextDouble() < 0.0065;
    }

    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================

    public String determineVariant()
    {
        return "base0";
    }

    public String getVariant()
    {
        String returnValue = hasEntityDataAccessor(VARIANT) ? entityData.get(VARIANT) : "base0";
        return returnValue.isEmpty() ? "base0" : returnValue;

    }

    public void setVariant(String variant)
    {
        entityData.set(VARIANT, variant);
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
        return new ItemStack(SpawnEggItem.byId(getType()));
    }


    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {



            setNavigator(getProperNavigator());

            switch (getNavigationType()) {
                case SWIMMING -> System.out.println("IS USING -SWIMMING- NAVIGATOR");
                case FLYING -> System.out.println("IS USING -FLYING- NAVIGATOR");
                case GROUND -> System.out.println("IS USING -GROUND- NAVIGATOR");
            }

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
        if (isSleeping()) {
            if (getLookControl() instanceof LessShitLookController) ((LessShitLookController) getLookControl()).stopLooking();
            if (getHealth() < getMaxHealth() && getRandom().nextDouble() < 0.005) heal(1);

            if (shouldWakeUp()) {
                setSleeping(false);
            }
        } else if (shouldSleep()) {
            setSleeping(true);
        }

        //Animations:
        //Sleeping
        if (this.isSleeping()) {
            this.setAnimation("sleep");
            this.setAnimationType(1);
            this.setAnimationTime(20);
            this.setManualAnimationCall(true);
            setIsMovingAnimation(false);
        }
        //Sitting
        if (this.isInSittingPose()) {
            this.setAnimation("sit");
            this.setAnimationType(2);
            this.setAnimationTime(20);
            this.setManualAnimationCall(true);
            setIsMovingAnimation(false);
        }

        if (isUsingSwimmingNavigator() && level.isClientSide){
            //YAW OPERATIONS:
            //The following lines of code handle the dynamic yaw animations for entities...
            //Grab the change in the entity's Yaw, deltaYRot...
            //deltaYaw will tell us in which direction the entity is rotating...
            deltaYRot = this.yRot - prevYRot;
            //Store the previous yaw value, so we can use itn ext tick to calculate deltaYaw...
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
                    }
                    if (deltaPitch < 0) {
                        adjustedPitch = adjustedPitch - deltaPitchLimit;
                    }
                }
                //If we are changing at an acceptable rate, reach the target directly...
                else {
                    adjustedPitch = xRot;
                }
                //Convert the value to Rads, this will be used by the model class...
                targetPitchRadians = (adjustedPitch * (Mth.PI / 180.0F));
            } else {
                //If we are breaching, ignore previous logic, do fast rotations...
                targetPitchRadians = (float) -((Mth.atan2((this.getDeltaMovement().y), Mth.sqrt((float) ((this.getDeltaMovement().x) * (this.getDeltaMovement().x) + (this.getDeltaMovement().z) * (this.getDeltaMovement().z))))));
                currentPitchRadians = targetPitchRadians;
            }
        }
    }



    public void clearAI()
    {
        jumping = false;
        navigation.stop();
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

    @Deprecated
    public boolean isImmuneToArrows()
    {
        return false;
    }


    public List<LivingEntity> getEntitiesNearby(double radius, Predicate<LivingEntity> filter)
    {
        return level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), filter.and(e -> e != this));
    }

    @Override // Dont damage owners other pets!
    public boolean doHurtTarget(Entity entity)
    {
        /*
        //TODO: set animation before we call this method
        if (this.getAnimation().equals("base")) {
            int attackVariant = this.random.nextInt(ATTACK_ANIMATION_VARIANTS)+1;
            this.setAnimation("attack_"+attackVariant);
            this.setAnimationType(2);
            this.setAnimationTime(80);
        }

         */
        return !isAlliedTo(entity) && super.doHurtTarget(entity);
    }


    @Override // We shouldnt be targetting pets...
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner)
    {
        return !isAlliedTo(target);
    }

    @Override
    public boolean canAttack(LivingEntity target)
    {
        return !isHatchling() && !canBeControlledByRider() && super.canAttack(target);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        if (isRiding() && source == DamageSource.IN_WALL) return true;
        if (isImmuneToArrows() && source == DamageSource.CACTUS) return true;
        return super.isInvulnerableTo(source);
    }

    // ====================================
    //      B.2) Tick and AI: Sit
    // ====================================

    @Override
    public void setInSittingPose(boolean flag)
    {
        super.setInSittingPose(flag);
        if (flag) {
            clearAI();
            setAnimation("sitting");
            setAnimationType(3);
            setAnimationTime(20);
            setManualAnimationCall(true);
            setIsMovingAnimation(false);
        }
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    protected PathNavigation createNavigation(Level levelIn)
    {
        return new BetterPathNavigator(this);
    }

    public void setNavigator(NavigationType navigator) {
        switch (navigator) {
            case GROUND: {
                this.moveControl = new MoveControl(this);
                this.lookControl = new LessShitLookController(this);
                this.navigation = new BetterPathNavigator(this);
                this.setMovingState(0);
                break;
            }
            case FLYING: {
                this.moveControl = new FlyerMoveController(this);
                this.lookControl = new LessShitLookController(this);
                this.navigation = new FlyerPathNavigator(this);
                this.setMovingState(1);
                setOrderedToSit(false);
                setSleeping(false);
                jumpFromGround();
                break;
            }
            case SWIMMING: {
                this.moveControl = new WRSwimControl(this);
                this.lookControl = new SmoothSwimmingLookControl(this, 10);
                this.navigation = new WRSwimmingNavigator(this);
                this.setMovingState(2);
                break;
            }
        }
    }


    @Override
    protected BodyRotationControl createBodyControl()
    {
        return new DragonBodyController(this);
    }

    @Override
    public void travel(Vec3 vec3d){
        float speed = getTravelSpeed();
        boolean isFlying = isUsingFlyingNavigator();
        if (canBeControlledByRider()) // Were being controlled; override ai movement
        {
            LivingEntity entity = (LivingEntity) getControllingPassenger();
            double moveX = entity.xxa * 0.5;
            double moveY = vec3d.y;
            double moveZ = entity.zza;

            // rotate head to match driver. yaw is handled relative to this.
            yHeadRot = entity.yHeadRot;
            setXRot(entity.getXRot() * 0.65f);
            setYRot(Mth.rotateIfNecessary(yHeadRot, getYRot(), getYawRotationSpeed()));

            if (isControlledByLocalInstance())
            {
                if (isFlying)
                {
                    moveX = vec3d.x;
                    moveZ = moveZ > 0? moveZ : 0;
                    if (ClientEvents.keybindFlight)
                        moveY = ClientEvents.getClient().options.keyJump.isDown()? 1f : WRKeybind.FLIGHT_DESCENT.isDown()? -1f : 0;
                    else if (moveZ > 0) moveY = -entity.getXRot() * (Math.PI / 180);
                }
                else
                {
                    speed *= 0.225f;
                    if (entity.jumping && dragonCanFly()) setNavigator(NavigationType.FLYING);
                }

                vec3d = new Vec3(moveX, moveY, moveZ);
                setSpeed(speed);
            }
            else if (entity instanceof Player)
            {
                calculateEntityAnimation(this, true);
                setDeltaMovement(Vec3.ZERO);
                if (!level.isClientSide && isFlying)
                    ((ServerPlayer) entity).connection.aboveGroundVehicleTickCount = 0;
                return;
            }
        } else {
            if (isFlying) {
                // Move relative to yaw - handled in the move controller or by passenger
                moveRelative(speed, vec3d);
                move(MoverType.SELF, getDeltaMovement());
                setDeltaMovement(getDeltaMovement().scale(0.9f));
                calculateEntityAnimation(this, true);
            } else super.travel(vec3d);
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
                return NavigationType.SWIMMING;
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
        if (navigation instanceof FlyerPathNavigator){
            return NavigationType.FLYING;
        }
        if (navigation instanceof BetterPathNavigator){
            return NavigationType.GROUND;
        }
        return NavigationType.GROUND;
    }

    public boolean isIdling()
    {
        return getNavigation().isDone() && getTarget() == null && !isVehicle() && !isInWaterOrBubble() && !isUsingFlyingNavigator();
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

    public boolean isLandNavigator(){
        return this.getNavigation() instanceof GroundPathNavigation;
    }



    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        return 0.0F;

    }

    public abstract boolean speciesCanWalk();

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    public abstract boolean speciesCanFly();


    /*
    public boolean isUsingFlyingNavigator()
    {
        return hasEntityDataAccessor(FLYING) && entityData.get(FLYING);
    }

     */

    /*
    public void setFlying(boolean fly)
    {
        if (isUsingFlyingNavigator() == fly) return;
        entityData.set(FLYING, fly);
        if (fly) {
            if (liftOff()) {
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                this.setMovingState(1);
                navigation = new FlyerPathNavigator(this);
            }
        }
        else {
            System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
            this.setMovingState(0);
            navigation = new BetterPathNavigator(this);
        }
    }

     */


    /*
    public boolean shouldFly()
    {
        return dragonCanFly() && getAltitude() > 1 && !isUsingSwimmingNavigator();
    }
     */

    public boolean shouldUseFlyingNavigator()
    {
        if (getAltitude() >1) {
            if (!speciesCanSwim() && isUnderWater()) {
                return false;
            }
            return canLiftOff();
        }
        return false;
    }

    public boolean canLiftOff()
    {
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
        if (speciesCanFly()) {
            return isJuvenile() && !isLeashed() && speciesCanFly();
        }
        return false;
    }

    public boolean isUsingFlyingNavigator()
    {
        return getNavigation() instanceof FlyerPathNavigator;
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
        return isUsingFlyingNavigator()? 6 : 75;
    }

    public void flapWings()
    {
        playSound(WRSounds.WING_FLAP.get(), 3, 1, false);
        setDeltaMovement(getDeltaMovement().add(0, 1.285, 0));
    }

    @Override
    protected float getJumpPower()
    {
        return dragonCanFly()? (getBbHeight() * getBlockJumpFactor()) * 0.6f : super.getJumpPower();
    }

    @Override // Disable fall calculations if we can fly (fall damage etc.)
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        return !dragonCanFly() && super.causeFallDamage(distance - (int) (getBbHeight() * 0.8), damageMultiplier, source);
    }

    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================

    public abstract boolean speciesCanSwim();

    public boolean isUsingSwimmingNavigator()
    {
        return getNavigation() instanceof WRSwimmingNavigator;
    }

    public boolean shouldUseSwimmingNavigator() {
        //If it's in 1-block-deep water, switch to land navigator
        if (this.isInWater() && this.level.getBlockState(blockPosition().below()).canOcclude() && this.level.getBlockState(blockPosition().above()).is(Blocks.AIR)) {
            return false;
        }
        //...else, if it's not in water and it's on the ground, switch to land navigator
        if (!this.isInWater() && this.isOnGround()) {
            return false;
        }
        //If it's on water surface, switch to water navigator
        if (!this.isInWater() && this.level.getFluidState(blockPosition().below()).is(FluidTags.WATER)) {
            return true;
        }
        //If it's falling and cannot fly, still use water navigator
        if (!speciesCanFly() && !this.isInWater() && !this.isOnGround() && this.level.getBlockState(new BlockPos(position())).is(Blocks.AIR)) {
            return true;
        }
        //If it's in swimmable water (meaning, at least two blocks deep), switch to water navigator
        if (this.isInWater() && !this.level.getBlockState(blockPosition().below()).canOcclude()) {
            return true;
        }
        return false;
    }

    public boolean getSwimmingNavigation() {
       return getNavigation() instanceof WRSwimmingNavigator;
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================

    @Override
    public void rideTick()
    {
        super.rideTick();

        Entity entity = getVehicle();

        if (entity == null || !entity.isAlive())
        {
            stopRiding();
            return;
        }

        setDeltaMovement(Vec3.ZERO);
        clearAI();

        if (entity instanceof Player)
        {
            Player player = (Player) entity;

            int index = player.getPassengers().indexOf(this);
            if ((player.isShiftKeyDown() && !player.getAbilities().flying) || isInWater() || index > 2)
            {
                stopRiding();
                setOrderedToSit(false);
                return;
            }

            setXRot(player.getXRot() / 2);
            yHeadRot = yBodyRot = yRotO  = player.getYRot();
            setYRot(yHeadRot);
            setRotation(player.yHeadRot, player.getXRot());

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
            Vec3 pos = Mafs.getYawVec(player.yBodyRot, vec3d.x, vec3d.z).add(player.getX(), player.getY() + vec3d.y, player.getZ());
            setPos(pos.x, pos.y, pos.z);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Vec3 getRidingPosOffset(int passengerIndex)
    {
        double x = getBbWidth() * 0.5d + getVehicle().getBbWidth() * 0.5d;
        switch (passengerIndex)
        {
            default:
            case 0:
                return new Vec3(0, 1.81, 0);
            case 1:
                return new Vec3(x, 1.38d, 0);
            case 2:
                return new Vec3(-x, 1.38d, 0);
        }
    }

    @Override
    public void positionRider(Entity passenger)
    {
        Vec3 offset = getPassengerPosOffset(passenger, getPassengers().indexOf(passenger));
        Vec3 pos = Mafs.getYawVec(yBodyRot, offset.x, offset.z).add(getX(), getY() + offset.y + passenger.getMyRidingOffset(), getZ());
        passenger.setPos(pos.x, pos.y, pos.z);
    }

    public Vec3 getPassengerPosOffset(Entity entity, int index)
    {
        return new Vec3(0, getPassengersRidingOffset(), 0);
    }
    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if (getControllingPassenger() == passenger && isOwnedBy((LivingEntity) passenger))
        {
            clearAI();
            setOrderedToSit(false);
            clearHome();
            if (isLeashed()) dropLeash(true, true);
        }
    }
    /**
     * Get the player potentially controlling this dragon
     * {@code null} if its not a player or no controller at all.
     */
    @Nullable
    public Player getControllingPlayer()
    {
        Entity passenger = getControllingPassenger();
        return passenger instanceof Player? (Player) passenger : null;
    }

    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
    }

    /**
     * Recieve the keybind message from the current controlling passenger.
     *
     * @param key     shut up
     * @param mods    the modifiers that is pressed when this key was pressed (e.g. shift was held, ctrl etc {@link org.lwjgl.glfw.GLFW})
     * @param pressed true if pressed, false if released. pretty straight forward idk why ur fucking asking.
     */
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider)
    {
        return false;
    }

    /**
     * which tl;dr does not update any AI including Goal Selectors, Pathfinding, Moving, etc.
     * Do not perform any AI actions while: Not Sleeping; not being controlled, etc.
     */
    @Override
    public boolean isImmobile()
    {
        return super.isImmobile() || isSleeping() || isRiding();
    }


    @Override
    public boolean canBeControlledByRider() // Only OWNERS can control their pets
    {
        Entity entity = getControllingPassenger();
        return entity instanceof Player && isOwnedBy(((Player) entity));
    }

    @Nullable
    @Override
    public Entity getControllingPassenger()
    {
        List<Entity> passengers = getPassengers();
        return passengers.isEmpty()? null : passengers.get(0);
    }

    @Override
    protected boolean canAddPassenger(Entity entityIn)
    {
        return false;
    }
    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return false;
    }


    public boolean tame(boolean tame, @Nullable Player tamer)
    {
        if (getOwner() == tamer) return true;
        if (level.isClientSide) return false;
        if (tame && tamer != null && !ForgeEventFactory.onAnimalTame(this, tamer))
        {
            tame(tamer);
            setHealth(getMaxHealth());
            clearAI();
            level.broadcastEntityEvent(this, (byte) 7); // heart particles
            return true;
        }
        else level.broadcastEntityEvent(this, (byte) 6); // black particles

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

    public void applyStaffInfo(BookContainer container)
    {
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
    }

    @Override
    public boolean isPickable()
    {
        return super.isPickable() && !isRiding();
    }

    // Ok so some basic notes here:
    // if the action result is a SUCCESS, the player swings its arm.
    // however, itll send that arm swing twice if we aren't careful.
    // essentially, returning SUCCESS on server will send a swing arm packet to notify the client to animate the arm swing
    // client tho, it will just animate it.
    // so if we aren't careful, both will happen. So its important to do the following for common execution:
    // InteractionResult.sidedSuccess(level.isClientSide);
    // essentially, if the provided boolean is true, it will return SUCCESS, else CONSUME.
    // so since the level is client, it will be SUCCESS on client and CONSUME on server.
    // That way, the server never sends the arm swing packet.
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        final InteractionResult SUCCESS = InteractionResult.sidedSuccess(level.isClientSide);

        if (isOwnedBy(player) && player.isShiftKeyDown() && !isUsingFlyingNavigator())
        {
            setOrderedToSit(!isOrderedToSit());
            return SUCCESS;
        }

        if (isTame())
        {
            if (isFood(stack))
            {
                boolean flag = getHealth() < getMaxHealth();
                if (isBaby())
                {
                    ageUp((int) ((-getAge() / 20) * 0.015F), true);
                    flag = true;
                }

                if (flag)
                {
                    eat(stack);
                    return SUCCESS;
                }
            }

            if (isBreedingItem(stack) && getAge() == 0)
            {
                if (!level.isClientSide && !isInLove())
                {
                    eat(stack);
                    setInLove(player);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }

        if (canAddPassenger(player) && !player.isShiftKeyDown())
        {
            if (!level.isClientSide) player.startRiding(this);
            return SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = stack.interactLivingEntity(player, this, hand);
        if (!result.consumesAction()) result = playerInteraction(player, hand, stack);
        if (result.consumesAction()) setSleeping(false);
        return result;
    }

    @Override
    public void dropLeash(boolean sendPacket, boolean dropLead)
    {
        super.dropLeash(sendPacket, dropLead);
        clearHome();
    }

    @Override
    public Component getDisplayName()
    {
        return super.getDisplayName();
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory.isPresent() && !getInventory().isEmpty())
            return inventory.cast();
        return super.getCapability(capability, facing);
    }

    public ItemStack getStackInSlot(int slot)
    {
        return inventory.map(i -> i.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    /**
     * It is VERY important to be careful when using this.
     * It is VERY sidedness sensitive. If not done correctly, it can result in the loss of items! <P>
     * {@code if (!level.isClient) setStackInSlot(...)}
     */
    public void setStackInSlot(int slot, ItemStack stack)
    {
        inventory.ifPresent(i -> i.setStackInSlot(slot, stack));
    }
    public DragonInventory getInventory()
    {
        return inventory.orElseThrow(() -> new NoSuchElementException("This boi doesn't have an inventory wtf are u doing"));
    }

    @Override
    protected void dropEquipment()
    {
        inventory.ifPresent(i -> i.getContents().forEach(this::spawnAtLocation));
    }

    public void dropStorage()
    {
    }

    public DragonInventory createInv()
    {
        return null;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playersInv, Player player)
    {
        //System.out.println(new BookContainer(id, playersInv, this));
        return new BookContainer(id, playersInv, this);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

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

    public void eat(ItemStack stack)
    {
        eat(level, stack);
    }

    @Override
    public abstract boolean isFood(ItemStack stack);

    @Override
    @SuppressWarnings("ConstantConditions")
    public ItemStack eat(Level level, ItemStack stack)
    {
        Vec3 mouth = getApproximateMouthPos();

        if (level.isClientSide)
        {
            double width = getBbWidth();
            for (int i = 0; i < Math.max(width * width * 2, 12); ++i)
            {
                Vec3 vec3d1 = new Vec3(((double) getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) getRandom().nextFloat() - 0.5D) * 0.1D);
                vec3d1 = vec3d1.zRot(-getXRot() * (Mafs.PI / 180f));
                vec3d1 = vec3d1.yRot(-getYRot() * (Mafs.PI / 180f));
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), mouth.x + Mafs.nextDouble(getRandom()) * (width * 0.2), mouth.y, mouth.z + Mafs.nextDouble(getRandom()) * (width * 0.2), vec3d1.x, vec3d1.y, vec3d1.z);
            }
            ModUtils.playLocalSound(level, new BlockPos(mouth), getEatingSound(stack), 1f, 1f);
        }
        else
        {
            final float max = getMaxHealth();
            if (getHealth() < max) heal(Math.max((int) max / 5, 4)); // Base healing on max health, minimum 2 hearts.

            Item item = stack.getItem();
            if (item.isEdible())
            {
                for (Pair<MobEffectInstance, Float> pair : item.getFoodProperties().getEffects())
                    if (!level.isClientSide && pair.getFirst() != null && getRandom().nextFloat() < pair.getSecond())
                        addEffect(new MobEffectInstance(pair.getFirst()));
            }
            if (item.hasContainerItem(stack))
                spawnAtLocation(item.getContainerItem(stack), (float) (mouth.y - getY()));
            stack.shrink(1);
        }

        return stack;
    }

    @Override
    public void heal(float healAmount)
    {
        super.heal(healAmount);
        level.broadcastEntityEvent(this, HEAL_PARTICLES_EVENT_ID);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate)
    {
        return (AgeableMob) getType().create(level);
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal mate)
    {
        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, mate, null);
        if (MinecraftForge.EVENT_BUS.post(event)) return; // cancelled

        final AgeableMob child = event.getChild();
        if (child == null)
        {
            ItemStack eggStack = DragonEggItem.getStack(getType());
            ItemEntity eggItem = new ItemEntity(level, getX(), getY(), getZ(), eggStack);
            eggItem.setDeltaMovement(0, getBbHeight() / 3, 0);
            level.addFreshEntity(eggItem);
        }
        else
        {
            child.setBaby(true);
            child.moveTo(getX(), getY(), getZ(), 0, 0);
            level.addFreshEntityWithPassengers(child);
        }

        breedCount++;
        ((WRDragonEntity) mate).breedCount++;

        ServerPlayer serverPlayer = getLoveCause();

        if (serverPlayer == null && mate.getLoveCause() != null)
            serverPlayer = mate.getLoveCause();

        if (serverPlayer != null)
        {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this, mate, child);
        }

        setAge(6000);
        mate.setAge(6000);
        resetLove();
        mate.resetLove();
        level.broadcastEntityEvent(this, (byte) 18);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
            level.addFreshEntity(new ExperienceOrb(level, getX(), getY(), getZ(), getRandom().nextInt(7) + 1));
    }


    @Override
    public boolean canMate(Animal mate)
    {
        if (!(mate instanceof WRDragonEntity)) return false;
        WRDragonEntity dragon = (WRDragonEntity) mate;
        if (isInSittingPose() || dragon.isInSittingPose()) return false;
        if (hasEntityDataAccessor(GENDER) && (getGender()).equals(dragon.getGender())) return false;
        return super.canMate(mate);
    }

    // ====================================
    //      E) Client
    // ====================================

    public void doSpecialEffects()
    {
    }
    
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == HEAL_PARTICLES_EVENT_ID)
        {
            for (int i = 0; i < getBbWidth() * getBbHeight(); ++i)
            {
                double x = getX() + Mafs.nextDouble(getRandom()) * getBbWidth() + 0.4d;
                double y = getY() + getRandom().nextDouble() * getBbHeight();
                double z = getZ() + Mafs.nextDouble(getRandom()) * getBbWidth() + 0.4d;
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0, 0);
            }
        }
        else super.handleEntityEvent(id);
    }

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


    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals()
    {
        //Goal will only get called when we manually set an animation and want the time counter to apply to it
        goalSelector.addGoal(0,new AnimatedGoal(this,this.getAnimation(),this.getAnimationType(),this.getAnimationTime()));
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WRSitGoal(this));
    }

    // ====================================
    //      F.n) Goals: n
    // ====================================

}


