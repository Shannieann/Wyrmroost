
package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDefendHomeGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDragonBreedGoal;
import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRFollowOwnerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRGetDroppedFoodGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRIdleGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRMoveToHomeGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRRandomLiftOffGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRRidePlayerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSitGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSleepGoal;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


public class EntitySilverGlider extends WRDragonEntity implements IBreedable, ITameable
{

    // Only used to make a group of silver gliders fly in circles in a flock. When not flocking, (0,0,0).
    public static final EntityDataAccessor<Integer> FLOCKING_X = SynchedEntityData.defineId(EntitySilverGlider.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FLOCKING_Y = SynchedEntityData.defineId(EntitySilverGlider.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FLOCKING_Z = SynchedEntityData.defineId(EntitySilverGlider.class, EntityDataSerializers.INT);

    private static final float MOVEMENT_SPEED = 0.23f;
    private static final float FLYING_SPEED = 0.12f;

    private static final ResourceLocation EYES = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/silver_glider/eyes.png");
    private static final ResourceLocation EYES_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/silver_glider/spe_eyes.png");



    private TemptGoal temptGoal;
    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(new ItemLike[]{Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH});

    public EntitySilverGlider(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
    }

    // ====================================
    //      Animations
    // ====================================

    @Override
    public int numIdleAnimationVariants() {
        return 4;
    }

    @Override
    public int getIdleAnimationTime(int index) {
        int[] animationTimesInOrder = {34, 26, 40, 20};
        return animationTimesInOrder[index];
    }

    public int attackAnimationVariants() {
        return 1;
    }

    @Override
    public int getAttackAnimationTime(int index) {
        int[] animationTimesInOrder = {6};
        return animationTimesInOrder[index];
    }

    public int getLieDownTime() {
        return 6; // seconds or ticks??
    }

    public int getSitDownTime() {
        return 14;
    }

    // ====================================
    // A) Entity Data + Attributes
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return (Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonAttributesConfig.maxHealth.get())
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, FLYING_SPEED)
                .add(Attributes.ATTACK_DAMAGE,
                        WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonAttributesConfig.attackDamage.get()));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (getSitting() || getSleeping()) size = size.scale(1, 0.87f);
        return size;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FLOCKING_X, 0);
        entityData.define(FLOCKING_Y, 0);
        entityData.define(FLOCKING_Z, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("FlockingX", getFlockingX());
        nbt.putInt("FlockingY", getFlockingY());
        nbt.putInt("FlockingZ", getFlockingZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setFlockingX(nbt.getInt("FlockingX"));
        setFlockingY(nbt.getInt("FlockingY"));
        setFlockingZ(nbt.getInt("FlockingZ"));
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    @Override
    public float ageProgressAmount() {
        return WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonBreedingConfig.ageProgress.get()/100F;
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
    public float getRestrictRadius() {
        int radiusRoot = WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonAttributesConfig.homeRadius.get();
        return radiusRoot * radiusRoot;
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    @Override
    public String getDefaultVariant() {
        return "female";
    }

    @Override
    public String determineVariant() {

        String gender = getGender() == 1 ? "male" : "female";

        if (getRandom().nextDouble() < 0.05) {
            return "spe_"+gender;
        }

        if (gender == "male") {
            return "male_" + String.valueOf(getRandom().nextInt(3));
        }
        return "female";
    }

    @Override
    public ResourceLocation getEyesTexture() {
        return isGolden() ? EYES_SPECIAL : EYES;
    }

    @Override
    public ResourceLocation getBehaviorEyesTexture() {
        if (getSleeping()) {
            return BLANK_EYES; // We don't have closed eye texture for silver glider
        }
        return getEyesTexture();
    }

    public boolean isGolden(){
        return getVariant().contains("spe_");
    }

    public ResourceLocation getGlowTexture() {
        if (this.getGender() == 1 && this.isAdult()) { // Only adult male silver gliders glow
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/silver_glider/"+getVariant()+"_glow.png");
        }
        return null;
    }

    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

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
    public int getHeadRotSpeed()
    {
        return 30;
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isUsingFlyingNavigator() ? 5 : 75;
    }

    public static boolean getSpawnPlacement(EntityType<EntitySilverGlider> fEntityType, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos blockPos, Random random)
    {
        if (spawnReason == MobSpawnType.SPAWNER) return true;
        Block block = level.getBlockState(blockPos.below()).getBlock();
        return block == Blocks.AIR || block == Blocks.SAND && level.getRawBrightness(blockPos, 0) > 8;
    }

    @Override
    public Attribute[] getScaledAttributes()
    {
        return new Attribute[] {MAX_HEALTH};
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    // Override for elytra logic
    public void tick() {

        super.tick();
        setEatingCooldown(Math.max(getEatingCooldown()-1,0));
        setBreedingCooldown(Math.max(getBreedingCooldown()-1,0));
        setSleepingCooldown(Math.max(getSleepingCooldown()-1,0));

        // TODO: Do silver gliders attack anything ever?
        // Check if attack animation has completed
        if (attackAnimationStartTick >= 0 && getAnimationInOverride()) {
            int elapsedTicks = tickCount - attackAnimationStartTick;
            int requiredTicks = getAnimationTime(); // TODO: Attack animation end may not necessarily be at the end of the animation time?
            System.out.println("[DEBUG] Attack animation check for " + this.getName().getString() + ": elapsed=" + elapsedTicks + ", required=" + requiredTicks);
            if (elapsedTicks >= requiredTicks) {
                System.out.println("[DEBUG] Attack animation completed for " + this.getName().getString() + ", turning off override");
                setAnimationInOverride(false);
                attackAnimationStartTick = -1;
            }
        }

        if (isRidingPlayer()) {

            // animations are handled by WRRidePlayerGoal.java
            // This controls riding vs not riding, dragon position on player, and dragon rotation

            setDeltaMovement(Vec3.ZERO);
            Player owner = (Player) this.getOwner();

            if (owner == null || ! owner.isAlive()) {
                stopRidingPlayer();
                return;
            }
    
            if (owner.isShiftKeyDown() && ! owner.isOnGround() && ! owner.getAbilities().flying) {
                stopRidingPlayer();
                return;
            }

            // Try to start player elytra flight
            if (! owner.isOnGround() && ! owner.isFallFlying() && isAdult() && ! owner.isInWater() && ! owner.hasEffect(MobEffects.LEVITATION)) {
                owner.startFallFlying();
             }

             /*
    public boolean shouldGlide(Player player)
    {
        if (isBaby()) return false;
        if (!player.jumping) return false;
        if (player.getAbilities().flying) return false;
        if (player.isFallFlying()) return false;
        if (player.isInWater()) return false;
        if (player.getDeltaMovement().y > 0) return false;
        if (isDiving() && !player.isOnGround()) return true;
        return getAltitude() - 1.8 > 4;
    }
*/

            this.setPos(getPosByDragonAndPlayer(owner));
            float yrot = owner.getYRot();
            this.setYRot(yrot);
            this.setYHeadRot(yrot);

            if (owner.isFallFlying()) {
                safeSetCappedXRot((owner.getXRot() + 90), 360.0f);

                /* TODO: Do we need this, or does it just work?
                 *
                 *  Vec3 vec3d = player.getLookAngle().scale(0.3);
                 * player.setDeltaMovement(player.getDeltaMovement().scale(0.6).add(vec3d.x, Math.min(vec3d.y * 2, 0), vec3d.z));
                 * player.fallDistance = 0;
                 * 
                 */
            } else {
                this.setXRot(90.0f); // needs to be vertical clinging to player
            }
        }

        NavigationType properNavigator = getProperNavigationType();
        if (properNavigator != this.getNavigationType()) {
            setNavigator(properNavigator);
        }

        // copy paste from WRDragonEntity

        else if (getDeltaMovement().length() <= (this.getFlyingSpeed()/2) && getXRot() != 0) {
        // Every tick, slowly orient the dragon's pitch back to 0 if its barely moving, so it isn't just awkwardly pointing down or up. Also if the player is upside down.
            //System.out.println("Rotating back to 0");
            safeSetCappedXRot(0, 180.0f);  // TODO: temporary, 180 allows for instant change with no cap. Reduce cap to 5 or something when xrot works.
        }
        else { // If it's moving, rotate based on angle
            Vec3 deltaVector = this.getDeltaMovement();
            // Horizontal direction doesn't matter, only distance. Use Pythagorean theorem.
            double horizontalDistance = Math.sqrt(deltaVector.x * deltaVector.x + deltaVector.z * deltaVector.z);
            double pitchInRadians = Math.atan2(deltaVector.y, horizontalDistance);
            float pitchInDegrees = wrapPitchDegrees((float) Math.toDegrees(pitchInRadians));
            //System.out.println("Rotating based on angle: " + pitchInDegrees);
            //System.out.println("Current xRot: " + this.getXRot());
            safeSetCappedXRot(pitchInDegrees, 180.0f); // TODO: temporary, 180 allows for instant change with no cap. Reduce cap to 10 or something when xrot works.
        }

        // UPDATE AGE:
        // The entity's is updated once every minute (AGE_UPDATE_INTERVAL == 1200)
        // Abstract method ageProgressAmount() sets the float amount by which to update age every minute
        if (!isAdult() && tickCount % AGE_UPDATE_INTERVAL == 0) {
            setAgeProgress(getAgeProgress()+ageProgressAmount());
        }

    }

    @Override
    public Vec3 getPosByDragonAndPlayer(Player owner) {

        double offX = 0d;
        // should be on player's back if adult, head if baby
        double offY = isAdult() ? 1.5d : 1.83d;
        double offZ = -0.5d;

        if (owner.isShiftKeyDown() && ! owner.isFallFlying()) {
            offY -= 0.2;
        }

        // Rotate the offset based on the player's rotation
        Vec3 rotatedOffset = WRMathsUtility.rotateXZVectorByYawAngle(owner.getYRot(), offX, offZ);
        return new Vec3(owner.getX() + rotatedOffset.x, owner.getY() + offY, owner.getZ() + rotatedOffset.z);
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            setFlockingX(0);
            setFlockingY(0);
            setFlockingZ(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || super.isInvulnerableTo(source);
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
        return true;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return false;
    }

    // ====================================
    //      D) Taming
    // ====================================

    // TODO: Figure out how to integrate this with tempt goal

    @SuppressWarnings("null")
    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {

        if (tamer.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        //         if (! isTame() && this.temptGoal != null && this.temptGoal.isRunning()) {

        if (! isTame()) {
            eat(tamer.getLevel(), stack);
            float tameChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.3f;
            boolean tamed = attemptTame(tameChance, tamer);
            if (tamed) {
                System.out.println("Tamed");
                this.playSound(SoundEvents.CAT_PURREOW, 2f, 1.5f);
                getAttribute(MAX_HEALTH).setBaseValue(WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonAttributesConfig.maxHealth.get());
                heal((float)getAttribute(MAX_HEALTH).getBaseValue());
                setFlockingX(0);
                setFlockingY(0);
                setFlockingZ(0);
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
        return WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonBreedingConfig.hatchTime.get();
    }

    @Override
    public int getBreedingLimit() {
        return WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonBreedingConfig.breedLimit.get();
    }

    @Override
    public int getMaxBreedingCooldown() {
        return WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonBreedingConfig.maxBreedingCooldown.get();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.COD
            || stack.getItem() == Items.SALMON 
            || stack.getItem() == Items.TROPICAL_FISH 
            || stack.getItem() == Items.PUFFERFISH;
    }

    @Override
    @SuppressWarnings({ "ConstantConditions", "null" })
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.COD
            || stack.getItem() == Items.SALMON
            || stack.getItem() == Items.TROPICAL_FISH
            || stack.getItem() == Items.PUFFERFISH
            || stack.getItem() == Items.BAKED_POTATO;
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public void doSpecialEffects()
    {
        if (getVariant().equals("special") && tickCount % 5 == 0)
        {
            double x = getX() + getRandom().nextGaussian();
            double y = getY() + getRandom().nextDouble();
            double z = getZ() + getRandom().nextGaussian();
            level.addParticle(new DustParticleOptions(new Vector3f(1f, 0.8f, 0), 1f), x, y, z, 0, 0.2f, 0);
        }
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case "special" -> new Vec2(1, 1);
            default -> new Vec2(0, 1);
        };
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@SuppressWarnings("null") DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_SILVERGLIDER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_DEATH.get();
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

        // TODO: Land at night to sleep goal
        // TODO: Flock in circles goal

        this.temptGoal = new TemptGoal(this, 0.8d, TEMPT_INGREDIENT, true);

        goalSelector.addGoal(1, new WRRidePlayerGoal(this, "perch", "player_fly", "boost"));
        goalSelector.addGoal(2, this.temptGoal);
        goalSelector.addGoal(3, new AvoidEntityGoal<Player>(this, Player.class, 10f, 1.0, 1.1));
        goalSelector.addGoal(4, new WRDragonBreedGoal(this));
        goalSelector.addGoal(5, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new WRSitGoal(this));
        goalSelector.addGoal(8, new WRGetDroppedFoodGoal(this, 20, true, (itemEntity -> itemEntity.getItem().getItem() == Items.BAKED_POTATO)));
        goalSelector.addGoal(9, new WRSleepGoal(this));
        goalSelector.addGoal(10, new SwoopGoal());
        goalSelector.addGoal(11, new WRIdleGoal(this));
        goalSelector.addGoal(12, new WRRandomLiftOffGoal(this));
        goalSelector.addGoal(13, new AnimatedFloatGoal(this, "float", AnimatedGoal.LOOP, 0));
        goalSelector.addGoal(14, new LookAtPlayerGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(15, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(16, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this)); // Does it defend owner?
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this)); // Does it attack?
        targetSelector.addGoal(4, new WRDefendHomeGoal(this)); // Does it defend home?
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Salmon || target instanceof Cod || target instanceof TropicalFish));
    }

    public class SwoopGoal extends Goal
    {
        private BlockPos pos;

        public SwoopGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (! isUsingFlyingNavigator() || isPassenger() || isRidingPlayer()
                || getRandom().nextDouble() > 0.001
                || level.getFluidState(this.pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPosition()).below()).isEmpty()) {
                return false;
            }
            return getY() - pos.getY() > 8;
        }

        @Override
        public boolean canContinueToUse()
        {
            return blockPosition().distSqr(pos) > 8;
        }

        @Override
        public void tick()
        {
            if (getNavigation().isDone()) {
                getNavigation().moveTo(pos.getX(), pos.getY() + 2, pos.getZ(), 1);
            }
            getLookControl().setLookAt(pos.getX(), pos.getY() + 2, pos.getZ());
        }
    }

    public class AnimatedFloatGoal extends AnimatedGoal
    {
        public AnimatedFloatGoal(WRDragonEntity dragon, String animationName, int animationType, int animationTime) {
            super(dragon, animationName, animationType, animationTime);
            this.setFlags(EnumSet.of(Flag.JUMP));
            entity.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
           return entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() || entity.isInLava();
        }

        public boolean canContinueToUse() {
            return entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() || entity.isInLava();
         }

        public boolean requiresUpdateEveryTick() {
           return true;
        }

        public void tick() {
           if (entity.getRandom().nextDouble() < 0.8D) {
              entity.getJumpControl().jump();
           }

        }
    }

}
