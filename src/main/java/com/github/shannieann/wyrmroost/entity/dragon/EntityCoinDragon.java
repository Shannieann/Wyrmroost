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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSleepGoal;
import net.minecraft.world.entity.item.ItemEntity;
import software.bernie.geckolib3.core.manager.AnimationData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import static net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED;
import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;


// Simple Entity really, just bob and down in the same spot, and land to sleep at night. Easy.
public class EntityCoinDragon extends WRDragonEntity
{

    private static final float FLYING_SPEED = 0.02f;

    public EntityCoinDragon(EntityType<? extends EntityCoinDragon> coindragon, Level level) {
        super(coindragon, level);
    }


    // ====================================
    //      Animations
    // ====================================

    @Override
    public int numIdleAnimationVariants() {
        return 0;
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
                .add(MAX_HEALTH, 4)
                .add(Attributes.FLYING_SPEED, EntityCoinDragon.FLYING_SPEED);
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
        return "body_0";
    }

    @Override
    public String determineVariant() {
        return "body_" + String.valueOf(this.getRandom().nextInt(5));
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

    @Override
    public void aiStep() {
        super.aiStep();
        // Coin dragons don't eat, so no food logic needed
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || super.isInvulnerableTo(source);
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

    // move up if too low, move down if too high, else, just bob up and down
    @Override
    public void travel(Vec3 positionIn)
    {
        if (isNoAi()) return;
        double moveSpeed = 0.02;
        double yMot;
        double altitiude = getAltitude();
        if (altitiude < 1.5) yMot = moveSpeed;
        else if (altitiude > 3) yMot = -moveSpeed;
        else yMot = Math.sin(tickCount * 0.1) * 0.0035;

        setDeltaMovement(getDeltaMovement().add(0, yMot, 0));
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.91));
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

    public double getAltitude()
    {
        BlockPos.MutableBlockPos pos = blockPosition().mutable().move(0, -1, 0);
        while (pos.getY() > 0 && !level.getBlockState(pos).canOcclude()) pos.setY(pos.getY() - 1);
        return getY() - pos.getY();
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
        return WRSounds.ENTITY_COINDRAGON_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_COINDRAGON_IDLE.get();
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
        goalSelector.addGoal(0, new WRSleepGoal(this));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8));
    }

}
