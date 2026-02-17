
package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRAnimatedFloatGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDefendHomeGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDragonBreedGoal;
import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRFollowOwnerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRGetDroppedFoodGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRIdleGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRMoveToHomeGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRRidePlayerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSitGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRSleepGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRRandomSwimmingGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics.WRSwimToSurfaceGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRFlockFlyInCirclesGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers.WRRandomLiftOffGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRMoveToLandToSleepGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRTemptGoal;
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
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


public class EntitySilverGlider extends WRDragonEntity implements IBreedable, ITameable
{
    // 0 = not flocking, 1 = flock follower, 2 = flock leader
    public static final EntityDataAccessor<Integer> IS_FLOCKING = SynchedEntityData.defineId(EntitySilverGlider.class, EntityDataSerializers.INT);

    private static final float MOVEMENT_SPEED = 0.23f;
    private static final float FLYING_SPEED = 0.12f;

    private static final ResourceLocation EYES = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/silver_glider/eyes.png");
    private static final ResourceLocation EYES_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/silver_glider/spe_eyes.png");

    private WRTemptGoal temptGoal;
    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(new ItemLike[]{Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH});

    public EntitySilverGlider(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
    }

    // ====================================
    //      Animations
    // ====================================

    @Override
    public <E extends IAnimatable> PlayState predicateAnimation(AnimationEvent<E> event) {

        if (this.isRidingPlayer()) {
            if (this.isAdult() && this.getOwner().isFallFlying())  {
                if (this.getOwner() != null && (this.getOwner().getDeltaMovement().length() > 1.1)) {
                    // wing flapping animation when flying fast while riding player
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("boost", ILoopType.EDefaultLoopTypes.LOOP));
                } else {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("player_fly", ILoopType.EDefaultLoopTypes.LOOP));
                }
                System.out.println("Movement speed: " + this.getOwner().getDeltaMovement().length());
            }
            else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("perch", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE; // Absolutely nothing else should play while riding on player
        }
        return super.predicateAnimation(event);
    }

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
        entityData.define(IS_FLOCKING, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("IsFlocking", getIsFlocking());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setIsFlocking(nbt.getInt("IsFlocking"));
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

    public int getIsFlocking() {
        return entityData.get(IS_FLOCKING);
    }
    public void setIsFlocking(int isFlocking) {
        entityData.set(IS_FLOCKING, isFlocking);
    }

    public boolean silverGliderFlightTick(LivingEntity entity) {
        if (!entity.level.isClientSide) {
            entity.gameEvent(GameEvent.ELYTRA_FREE_FALL);
        }
        return true;
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

    // Can't breathe underwater but can hold breath for 1 minute (as opposed to 15 seconds)
    // Copied from WaterAnimal.class
    protected void handleAirSupply(int pAirSupply) {
        if (this.isAlive() && this.isUnderWater()) {
           this.setAirSupply(pAirSupply - 1);
           if (this.getAirSupply() == -20) {
              this.setAirSupply(0);
              this.hurt(DamageSource.DROWN, 2.0F);
            }
        } else {
           this.setAirSupply(1200);
        }
    }

    @Override
    public double getFluidJumpThreshold() { // So animated float goal works better
        return 0.1d;
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    // custom breath hold
    @Override
    public void baseTick() {
        int airSupply = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(airSupply);
     }

    @Override
    // Override for elytra logic
    public void tick() {

        if (this.temptGoal != null && this.temptGoal.isRunning() && !this.isTame() && this.tickCount % 100 == 0) {
            this.playSound(WRSounds.ENTITY_SILVERGLIDER_IDLE.get(), 1.5F, 3.0F);
        }

        super.tick();

        if (isRidingPlayer()) {

            // animations are handled by WRRidePlayerGoal.java
            // This controls riding vs not riding, dragon position on player, and dragon rotation

            setDeltaMovement(Vec3.ZERO);
            Player owner = (Player) this.getOwner();

            if (owner == null || ! owner.isAlive()) {
                stopRidingPlayer();
                return;
            }

            if (owner.isShiftKeyDown() && ! owner.isFallFlying() && ! owner.isOnGround() && ! owner.getAbilities().flying) {
                stopRidingPlayer();
                return;
            }

            // Try to start player elytra flight (mixin keeps it active while in air and clears when on ground).
            // Disable if player already has creative flight (getAbilities().flying is true only when creative player is actively flying).
            // Don't start if owner is barely moving (probably stuck in ground)
            if (isAdult() &&! owner.isFallFlying() && ! owner.getAbilities().flying && ! owner.isOnGround()
                && owner.getDeltaMovement().length() > 0.1 && ! owner.isInWater() && ! owner.hasEffect(MobEffects.LEVITATION)) {
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
            // Geckolib 3 has known issues with entity pitch rendering (xRot). Yaw works, pitch may not show visually.
            if (owner.isFallFlying()) {
                this.safeSetCappedXRot((owner.getXRot() + 90), 360.0f);
            } else {
                this.safeSetCappedXRot(90.0f, 360.0f);
            }
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
        else if (owner.isFallFlying() || owner.isSwimming()) {
            offY -= 1.2d;
            offZ = 0d;
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
            setIsFlocking(0);
            return true;
        }
        return false;
    }

    // Some changes so that whenever it has a target, it attacks once and then flies away
    // coward glider
    @Override
    public boolean doHurtTarget(Entity entity)
    {
        boolean didHurt = super.doHurtTarget(entity);
        boolean hitFish = entity instanceof Cod || entity instanceof Salmon || entity instanceof TropicalFish;
        if (didHurt && ! hitFish) {
            setTarget(null);
            setAggressive(false);

            // launch into air and fly away
            if (canLiftOff()) {
                setDeltaMovement(0, 0.2, 0);
                setNavigator(NavigationType.FLYING);
            }

            // Get random position ~30 blocks away and ~20 blocks up
            double randX = entity.getX() + (getRandom().nextDouble() * 5) + ((getRandom().nextBoolean() ? -1 : 1) * 25);
            double randZ = entity.getZ() + (getRandom().nextDouble() * 5) + ((getRandom().nextBoolean() ? -1 : 1) * 25);
            double randY = entity.getY() + 30;

            getNavigation().createPath(randX, randY, randZ, 1);
            if (getNavigation().getPath() == null) {
                getNavigation().moveTo(randX, randY, randZ, 1.5D);
            }
        }
        return didHurt;
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

    @SuppressWarnings("null")
    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {

        if (tamer.level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (! isTame() && this.temptGoal != null && this.temptGoal.isRunning()
            && (stack.getItem() == Items.COD || stack.getItem() == Items.SALMON || stack.getItem() == Items.TROPICAL_FISH || stack.getItem() == Items.PUFFERFISH)
        ) {
            eat(tamer.getLevel(), stack);
            float tameChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.3f;
            boolean tamed = attemptTame(tameChance, tamer);
            if (tamed) {
                this.playSound(SoundEvents.CAT_PURREOW, 2f, 1.5f);
                getAttribute(MAX_HEALTH).setBaseValue(WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.dragonAttributesConfig.maxHealth.get());
                heal((float)getAttribute(MAX_HEALTH).getBaseValue());
                setIsFlocking(0);
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
        return 0.6f;
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.temptGoal = new WRTemptGoal(this, 0.8d, TEMPT_INGREDIENT, true);

        goalSelector.addGoal(1, new WRRidePlayerGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(3, this.temptGoal);
        goalSelector.addGoal(4, new WRDragonBreedGoal(this));
        goalSelector.addGoal(5, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new WRSleepGoal(this));
        goalSelector.addGoal(8, new WRSitGoal(this));
        goalSelector.addGoal(9, new WRGetDroppedFoodGoal(this, 25, true, Items.BAKED_POTATO));

        goalSelector.addGoal(10, new AvoidEntityGoal<Player>(this, Player.class, 10f, 1.0, 1.1, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test) {
            @Override
            // Try to make escape pos in air so dragon flies away from players
            public boolean canUse() {
                boolean canUse = super.canUse();
                if (canUse && canLiftOff()) { // Make it fly away from player
                    setNavigator(NavigationType.FLYING);
                    setDeltaMovement(0, 0.2, 0);

                    Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
                    this.path = this.pathNav.createPath(pos.x, pos.y+20, pos.z, 0);
                    return this.path != null;
                }
                return canUse;
            }
        });
        goalSelector.addGoal(11, new WRMoveToLandToSleepGoal(this, false));
        goalSelector.addGoal(12, new SilverGliderSwoopGoal());
        goalSelector.addGoal(13, new WRFlockFlyInCirclesGoal(this, 20, 20));
        goalSelector.addGoal(14, new WRIdleGoal(this));
        goalSelector.addGoal(15, new WRRandomLiftOffGoal(this, 20, 0.15));
        goalSelector.addGoal(16, new WRRandomSwimmingGoal(this, 1, 15, 15, 5));
        goalSelector.addGoal(17, new WRSwimToSurfaceGoal(this));
        goalSelector.addGoal(18, new WRAnimatedFloatGoal(this));
        goalSelector.addGoal(19, new LookAtPlayerGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(20, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]) {
            @Override
            protected double getFollowDistance() {
                return (double) getRestrictRadius();
            }
        });
        targetSelector.addGoal(4, new WRDefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Salmon || target instanceof Cod || target instanceof TropicalFish));
    }

    public class SilverGliderSwoopGoal extends Goal
    {
        private BlockPos pos;

        public SilverGliderSwoopGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            if (! isUsingFlyingNavigator() || isPassenger() || isRidingPlayer()
                || getRandom().nextDouble() > 0.001
                || getIsFlocking() == 2 // leader
                || getTarget() != null // attacking
                || level.getFluidState(this.pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPosition()).below()).isEmpty()) {
                return false;
            }
            return getY() - pos.getY() > 8;
        }

        @Override
        public void start() {
            setIsFlocking(0);
        }

        @Override
        public boolean canContinueToUse()
        {
            return blockPosition().distSqr(pos) > 4;
        }

        @Override
        public void tick()
        {
            if (getNavigation().isDone()) {
                getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1);
            }
            getLookControl().setLookAt(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public void stop() {
            super.stop();
            pos = null;
        }
    }

}
