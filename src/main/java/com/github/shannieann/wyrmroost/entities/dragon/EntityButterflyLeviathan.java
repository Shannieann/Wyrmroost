package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.AnimatedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.aquatics.WRRandomSwimmingGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.aquatics.WRReturnToWaterGoal;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.aquatics.WRWaterLeapGoal;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.network.packets.KeybindHandler;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.Mafs;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
//TODO: Pending BFL Fixes:

//TODO: CLASS:
//Tidy up
//This class: See which methods are needed, which are not
//Fix serializer

//TODO: GOALS:
//ATTACK: Reimplement whole logic...
//ATTACK: If lightning rods near it, can attack
//Test all regular goals before tamed goals
//TAMED GOALS

//TODO: ANIMATIONS
//Test new animation method, write animation logic
//Test water leap and anim transition time

//TODO: TAMING
//All goals when tamed
//Eggs, breeding, taming
//Ride logic + rewrite key-binds

//TODO: ASSETS:
//Texture: Lightning on/off
//Texture Variants: base0, base1, special
//No sexual dimorphism, remove logic
//Child textures
//ANIMATIONS: Sitting
//ANIMATIONS: STANDING
//ANIMATIONS: Sleeping
//ANIMATIONS: Awakening
//Animations: Idle (if on ground)

//TODO: TEST AND SHOWCASE
//Water movement

//TODO: FINAL
//Config spawn
//Config attributes
//Tidy up EntityTypeRegistry

public class EntityButterflyLeviathan extends WRDragonEntity implements IForgeEntity {
    //TODO: Correct ALL Serializers
    public static final EntitySerializer<EntityButterflyLeviathan> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant)
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender));

    public static final EntityDataAccessor<Boolean> HAS_CONDUIT = SynchedEntityData.defineId(EntityButterflyLeviathan.class, EntityDataSerializers.BOOLEAN);
    public static final int CONDUIT_SLOT = 0;
    public final float entityDeltaPitchLimit = 1.0F;
    public final float entityYawAdjustment = 0.30F;
    public final float entityExtremityPitchAdjustment = 0.01F;
    public final LerpedFloat beachedTimer = LerpedFloat.unit();
    public final LerpedFloat swimTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public boolean beached = true;

    //TODO: ADJUST TIMES - animation and actual strike
    //TODO: Adjust number of variants
    protected static int ATTACK_ANIMATION_VARIANTS = 1;

    public static final String LIGHTNING_ANIMATION = "lightning";
    public static final int LIGHTNING_ANIMATION_TIME = 100;
    public static final int LIGHTNING_ANIMATION_QUEUE = 20;

    public static final String ATTACK_ANIMATION = "attack_";
    public static final int LAND_ATTACK_ANIMATION_TIME_1 = 20;
    public static final int SWIM_ATTACK_ANIMATION_TIME_1 = 13;
    public static final int LAND_ATTACK_ANIMATION_TIME_2 = 20;
    public static final int SWIM_ATTACK_ANIMATION_TIME_2 = 13;

    public static final int LAND_ATTACK_QUEUE_TIME_1 = 9;
    public static final int SWIM_ATTACK_QUEUE_TIME_1 = 9;
    public static final int LAND_ATTACK_QUEUE_TIME_2 = 9;
    public static final int SWIM_ATTACK_QUEUE_TIME_2 = 9;


    public EntityButterflyLeviathan(EntityType<? extends WRDragonEntity> entityType, Level level) {
        super(entityType, level);
        noCulling = WRConfig.NO_CULLING.get();
        maxUpStep = 2;
        setPathfindingMalus(BlockPathTypes.WATER, 0);
        setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0);
        this.deltaPitchLimit = entityDeltaPitchLimit;
        this.adjustmentYaw = entityYawAdjustment;
        this.adjustmentExtremityPitch = entityExtremityPitchAdjustment;
        this.groundMaxYaw = 10;
        this.setNavigator(NavigationType.SWIMMING);
    }

    // ====================================
    //      A) Entity Data
    // ====================================

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HAS_CONDUIT, false);
    }

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 180)
                .add(MOVEMENT_SPEED, 0.08F)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, 14)
                .add(FOLLOW_RANGE, 50);
    }

    @Override
    public EntitySerializer<EntityButterflyLeviathan> getSerializer() {
        return SERIALIZER;
    }


    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pReason) {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater() {
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

    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

    @Override
    public boolean defendsHome() {
        return true;
    }

    // ====================================
    //      A.5) Entity Data: SLEEP
    // ====================================

    //TODO: BFLs now sleep
    @Override
    public boolean shouldSleep() {
        return false;
    }

    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================


    @Override
    public String determineVariant() {
        return getRandom().nextDouble() < 0.02 ? "special" : "base" + getRandom().nextInt(2);
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
        return size.height * (beached ? 1f : 0.6f);
    }

    @Override
    public float getScale() {
        return getAgeScale(0.225f);
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
        beachedTimer.add((beached) ? 0.1f : -0.05f);
        swimTimer.add(isUnderWater() ? -0.1f : 0.1f);
        sitTimer.add(isInSittingPose() ? 0.1f : -0.1f);


        //TODO: TAMED LIGHTNING COOLDOWN
        //if (lightningCooldown > 0) --lightningCooldown;
        // =====================
        //       Beached Logic
        // =====================
        boolean prevBeached = beached;

        if (!beached && onGround && !wasTouchingWater) {
            beached = true;
        } else if (beached && wasTouchingWater) {
            beached = false;
        }
        if (prevBeached != beached) {
            refreshDimensions();
        }

        // =====================
        //       Rotation Logic
        // =====================
        /*
        if (isJumpingOutOfWater()) {
            Vec3 motion = getDeltaMovement();
            xRot = (float) (Math.signum(-motion.y) * Math.acos(Math.sqrt((motion.x*motion.x+motion.z*motion.z)) / motion.length()) * (double) (180f / Mafs.PI)) * 0.725f;
        }

         */

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
                            Mafs.nextDouble(getRandom()) * 1.5f,
                            Mafs.nextDouble(getRandom()),
                            Mafs.nextDouble(getRandom()) * 1.5f);
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


    /*
    //TODO: Extract logic
    public void lightningAnimation(int time) {
        lightningCooldown += 6;
        if (time == 10) playSound(WRSounds.ENTITY_BFLY_ROAR.get(), 3f, 1f, true);
        if (!level.isClientSide && isInWaterRainOrBubble() && time >= 10) {
            LivingEntity target = getTarget();
            if (target != null) {
                if (hasConduit()) {
                    if (time % 10 == 0) {
                        Vec3 vec3d = target.position().add(Mafs.nextDouble(getRandom()) * 2.333, 0, Mafs.nextDouble(getRandom()) * 2.333);
                        createLightning(level, vec3d, false);
                    }
                } else if (time == 10) createLightning(level, target.position(), false);
            }
        }
    }
     */
    //TODO: REMOVE
    public void conduitAnimation(int time) {
        if (getLookControl() instanceof WRGroundLookControl) ((WRGroundLookControl) getLookControl()).stopLooking();
        if (time == 0) playSound(WRSounds.ENTITY_BFLY_ROAR.get(), 5f, 1, true);
        else if (time == 15) {
            playSound(SoundEvents.BEACON_ACTIVATE, 1, 1);
            if (!level.isClientSide) createLightning(level, getConduitPos().add(0, 1, 0), true);
            else {
                Vec3 conduitPos = getConduitPos();
                for (int i = 0; i < 26; ++i) {
                    double velX = Math.cos(i);
                    double velZ = Math.sin(i);
                    level.addParticle(ParticleTypes.CLOUD, conduitPos.x, conduitPos.y + 0.8, conduitPos.z, velX, 0, velZ);
                }
            }
        }
    }
    //TODO: REMOVE
    public void biteAnimation(int time) {
        if (time == 0) playSound(WRSounds.ENTITY_BFLY_HURT.get(), 1, 1, true);
        else if (time == 6)
            attackInBox(getBoundingBox().move(Vec3.directionFromRotation(isUnderWater() ? xRot : 0, yHeadRot).scale(5.5f)).inflate(0.85), 40);
    }

    //TODO: REMOVE
    private static void createLightning(Level level, Vec3 position, boolean effectOnly) {
        if (level.isClientSide) return;
        LightningBolt entity = EntityType.LIGHTNING_BOLT.create(level);
        entity.moveTo(position);
        entity.setVisualOnly(effectOnly);
        level.addFreshEntity(entity);
    }

    @Override
    public boolean isImmuneToArrows() {
        return true;
    }

    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return ModUtils.contains(source, DamageSource.LIGHTNING_BOLT, DamageSource.IN_FIRE, DamageSource.IN_WALL, DamageSource.ON_FIRE) || super.isInvulnerableTo(source);
    }


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

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public void travel(Vec3 vec3d) {
        if (isInWater()) {
            if (canBeControlledByRider()) {
                float speed = getTravelSpeed() * 0.225f;
                LivingEntity entity = (LivingEntity) getControllingPassenger();
                double moveY = vec3d.y;
                double moveX = vec3d.x;
                double moveZ = entity.zza;

                yHeadRot = entity.yHeadRot;
                if (!isJumpingOutOfWater()) xRot = entity.xRot * 0.5f;
                double lookY = entity.getLookAngle().y;
                if (entity.zza != 0 && (isUnderWater() || lookY < 0)) moveY = lookY;

                setSpeed(speed);
                vec3d = new Vec3(moveX, moveY, moveZ);
            }

            // add motion if were coming out of water fast; jump out of water like a dolphin
            if (getDeltaMovement().y > 0.25 && level.getBlockState(new BlockPos(getEyePosition(1)).above()).getFluidState().isEmpty())
                setDeltaMovement(getDeltaMovement().multiply(1.2, 1.5f, 1.2d));

            moveRelative(getSpeed(), vec3d);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9d));

            /*
            animationSpeedOld = animationSpeed;
            double xDiff = getX() - xo;
            double yDiff = getY() - yo;
            double zDiff = getZ() - zo;
            if (yDiff < 0.2) yDiff = 0;
            float amount = Mth.sqrt((float)(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff))* 4f;
            if (amount > 1f) amount = 1f;

            animationSpeed += (amount - animationSpeed) * 0.4f;
            animationPosition += animationSpeed;
            */
            /*
            if (vec3d.z == 0 && getTarget() == null && !isInSittingPose())
                setDeltaMovement(getDeltaMovement().add(0, -0.003d, 0));
             */
        } else super.travel(vec3d);
    }

    @Override
    public float getTravelSpeed() {
        //@formatter:off
        return isInWater() ? (float) getAttributeValue(ForgeMod.SWIM_SPEED.get())
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event) {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollision(-10, this), 1, 0);
        else
            event.getCamera().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
    }

    @Override
    public int getYawRotationSpeed() {
        return 6;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
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
        return new Vec3(0, getPassengersRidingOffset(), index == 1 ? -2 : 0);
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider) {
        return true;
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed) {
        //TODO: TYPO
        //TODO: Lightning strikes when tamed, set of different methods

        if (pressed /*&& noAnimations()*/) {
            if (key == KeybindHandler.MOUNT_KEY) /*setAnimation(BITE_ANIMATION)*/ ;
            else if (key == KeybindHandler.ALT_MOUNT_KEY && !level.isClientSide && tamedLightningCheck()) {
                EntityHitResult ertr = Mafs.clipEntities(getControllingPlayer(), 40, e -> e instanceof LivingEntity && e != this);
                if (ertr != null && wantsToAttack((LivingEntity) ertr.getEntity(), getOwner())) {
                    setTarget((LivingEntity) ertr.getEntity());
                    /*AnimationPacket.send(this, LIGHTNING_ANIMATION);*/
                }
            }
        }
    }

    public boolean isJumpingOutOfWater() {
        return !isInWater() && !beached;
    }



    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public void applyStaffInfo(BookContainer container) {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), CONDUIT_SLOT, 0, -65, -75, DragonControlScreen.CONDUIT_UV).only(Items.CONDUIT).limit(1))
                .addAction(BookActions.TARGET);
    }


    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack) {
       /* if (((beached && lightningCooldown > 60 && level.isRainingAt(blockPosition())) || player.isCreative() || isHatchling()) && isFood(stack)) {
            eat(stack);
            if (!level.isClientSide) tame(getRandom().nextDouble() < 0.2, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        */
        return super.playerInteraction(player, hand, stack);


    }


    @Override
    public ItemStack eat(Level level, ItemStack stack) {
        //lightningCooldown = 0;
        return super.eat(level, stack);
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
            //TODO: Set Animation
            //if (!onLoad && flag && !hadConduit) setAnimation(CONDUIT_ANIMATION);
        }
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.isEdible() && stack.getFoodProperties(this).isMeat();
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public void doSpecialEffects() {
        if (getVariant().equals("special") && tickCount % 25 == 0) {
            double x = getX() + (Mafs.nextDouble(getRandom()) * getBbWidth() + 1);
            double y = getY() + (getRandom().nextDouble() * getBbHeight() + 1);
            double z = getZ() + (Mafs.nextDouble(getRandom()) * getBbWidth() + 1);
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
//        goalSelector.addGoal(0, new WRSitGoal(this));
//        goalSelector.addGoal(1, new MoveToHomeGoal(this));
//        goalSelector.addGoal(2, new WRFollowOwnerGoal(this));
//        goalSelector.addGoal(3, new DragonBreedGoal(this));


        goalSelector.addGoal(4, new BFLAttackGoal(this));
        //goalSelector.addGoal(5, new WRReturnToWaterGoal(this, 1.0,16,8));
        //goalSelector.addGoal(6, new WRWaterLeapGoal(this, 1,12,30,64));
        //goalSelector.addGoal(7, new WRRandomSwimmingGoal(this, 1.0, 64,48));

        goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 14f, 1));
        //goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        //targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        //targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        //targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, aquaticRandomTargetPredicate));
    }

    private class BFLAttackGoal extends AnimatedGoal {
        private int navRecalculationTicks;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;

        LivingEntity target;

        boolean animationPlaying;

        int lightningAttackCooldown;
        int checkForLightningCounter;

        boolean lightningLineQueued;
        boolean lightningLineSetup;
        int lightningLineCounter;

        Vec3 toTarget;
        Vec3 toTarget1;
        Vec3 toTarget2;
        Vec3 strikePos;
        Vec3 strikePos1;
        Vec3 strikePos2;

        boolean regularLightningAttackQueued;

        boolean meleeAttackQueued;
        int attackVariant;
        float inflateValue;
        int disableShieldTime;
        int attackQueueTime;

        public BFLAttackGoal(EntityButterflyLeviathan entity) {
            super(entity);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            target = getTarget();
            return !canBeControlledByRider() && target != null;
        }

        @Override
        public void start() {
            getNavigation().moveTo(target, 1.5);
            getLookControl().setLookAt(target);
            setAggressive(true);
            this.navRecalculationTicks = 0;
        }

        @Override
        public boolean canContinueToUse() {
            target = getTarget();
            if (target == null) {
                return false;
            }
            return true;
        }

        @Override
        public void stop(){
            setTarget(null);
            setAggressive(false);
            getNavigation().stop();
            animationPlaying = false;
            lightningLineQueued = false;
            lightningLineSetup = false;
            lightningLineCounter = 0;
            regularLightningAttackQueued = false;
            meleeAttackQueued = false;
            navRecalculationTicks = 0;
            pathedTargetX = 0;
            pathedTargetY = 0;
            pathedTargetZ = 0;
        }

        @Override
        public void tick() {
            target = getTarget();
            if (animationPlaying) {
                if (super.canContinueToUse()) {
                    super.tick();
                } else {
                    super.stop();
                    animationPlaying = false;
                    lightningLineQueued = false;
                    lightningLineSetup = false;
                    lightningLineCounter = 0;
                    regularLightningAttackQueued = false;
                    meleeAttackQueued = false;
                    return;
                }
                if (meleeAttackQueued) {
                    if (elapsedTime == attackQueueTime) {
                        attackInBox(getOffsetBox(getBbWidth()).inflate(inflateValue), disableShieldTime);
                    }
                }
                if (lightningLineQueued) {
                    //Animation Logic: If lightingLine is queued and we have reached the appropriate time, call the GoalLogic...
                    //Keep calling so long as we exceed the approriate time as this must be performed over multiple ticks
                    if (elapsedTime > LIGHTNING_ANIMATION_QUEUE) {
                        //If we have not yet setup the appropriate directions for the lightningLine, do that..
                        if (!lightningLineSetup) {
                            toTarget = target.position().subtract(position()).normalize();
                            toTarget1 = toTarget.yRot(0.523599F);
                            toTarget2 = toTarget.yRot(-0.523599F);
                            strikePos = position();
                            strikePos1 = position();
                            strikePos2 = position();
                            lightningLineSetup = true;
                        }
                        //Else, just summon the lightning line...
                        if (lightningLineCounter < 25) {
                            //Goal Logic: Instantiate 3 lightning bolts
                            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            LightningBolt lightningBolt1 = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            LightningBolt lightningBolt2 = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                            //Goal Logic: Set all of their damages to double the normal lightning bolt damage
                            lightningBolt.setDamage(10F);
                            lightningBolt1.setDamage(10F);
                            lightningBolt2.setDamage(10F);
                            //Goal Logic: Calculate all of their strike positions
                            strikePos = strikePos.add(toTarget.scale(2));
                            strikePos1 = strikePos1.add(toTarget1.scale(2));
                            strikePos2 = strikePos2.add(toTarget2.scale(2));
                            //Goal Logic: Set all of the actual lightning bolt's positions
                            lightningBolt.setPos(strikePos);
                            lightningBolt1.setPos(strikePos1);
                            lightningBolt2.setPos(strikePos2);
                            //Goal Logic: Add the lightning bolts to the world
                            level.addFreshEntity(lightningBolt);
                            level.addFreshEntity(lightningBolt1);
                            level.addFreshEntity(lightningBolt2);
                            lightningLineCounter++;
                        } else {
                            //Once we reach the limit, reset the lightningLine variables
                            //TODO: Ensure animation duration does not end before lightningLine logic ends
                            //TODO I.E: Ensure animation logic does not clip AttackLogic for lightningLine
                            lightningLineQueued = false;
                            lightningLineSetup = false;
                            lightningLineCounter = 0;
                        }
                    }

                }
                if (regularLightningAttackQueued) {
                    //Animation Logic: If regularLightning is queued and we have reached the appropriate time, call the AttackLogic...
                    if (elapsedTime == LIGHTNING_ANIMATION_QUEUE) {
                        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
                        lightningBolt.setDamage(10F);
                        lightningBolt.setPos(target.position());
                        level.addFreshEntity(lightningBolt);
                        regularLightningAttackQueued = false;
                    }

                }

            }
            //Do not navigate or attempt other attacks if performing either lightning attack
            if (!lightningLineQueued && !regularLightningAttackQueued) {
                target = getTarget();
                if (target != null) {
                    //getLookControl().setLookAt(target);
                    double distanceToTargetSqr = distanceToSqr(target);
                    //Recalculate navigation if we can see target, if we have not recently recalculated and if target has moved..
                    if ((getSensing().hasLineOfSight(target))
                            && this.navRecalculationTicks <= 0
                            && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D
                            || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D
                            || getRandom().nextFloat() < 0.05F)) {
                        //Store target's current position + adjust recalculation timer
                        this.pathedTargetX = target.getX();
                        this.pathedTargetY = target.getY();
                        this.pathedTargetZ = target.getZ();
                        this.navRecalculationTicks = 4 + getRandom().nextInt(7);

                        //We will perform faster recalculations if we are close to the target..
                        if (distanceToTargetSqr > 1024.0D) {
                            this.navRecalculationTicks += 10;
                        } else if (distanceToTargetSqr > 256.0D) {
                            this.navRecalculationTicks += 5;
                        }

                        //Get our final path point, ensure it is still close to the target...
                        //We only recalculate the path if our final path point (and hence our path) will not get us to within a reasonable distance of target...
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
                //Reduce by 1 if not already at 0
                this.navRecalculationTicks = Math.max(this.navRecalculationTicks - 1, 0);

                //Reduce by 1 if not already at 0

                //checkForLightningCounter = Math.max(checkForLightningCounter- 1, 0);

                //Decide which attack to use: LightningAttack or MeleeAttack.
                // We are not checking for lightningAttacks each tick, only every 40 ticks
                lightningAttackCooldown = Math.max(lightningAttackCooldown-1,0);

                if (canPerformLightningAttack()) {
                    performLightningAttack();
                }
                else if (canPerformMeleeAttack()) {
                    //performMeleeAttack();
                    //ToDo: Implement logic
                }
            }

        }

        public boolean canPerformLightningAttack() {
            if (lightningAttackCooldown <= 0) {
                if (isInWaterRainOrBubble()) {
                    return true;
                }
                if (level.getBlockStates(getBoundingBox().inflate(5)).anyMatch(test -> test.is(Blocks.LIGHTNING_ROD))) {
                    return true;
                }

                if (level.getBlockState(new BlockPos(position()).below()).is(Blocks.GOLD_BLOCK)) {
                    return false;
                }
            }
            return false;
        }

        private void performLightningAttack(){
            LivingEntity target = getTarget();
            if (target !=null && !animationPlaying) {
                //If on ground, queue a lightning line...
                if (isUsingLandNavigator()) {
                    //Attack Logic: Queue up ability to play when animation reaches the appropriate point
                    lightningLineQueued = true;
                    //Attack Logic: Stop moving in preparation for Lightning Strike
                    getNavigation().stop();
                    //Animation Logic: Set the animationPlaying flag correctly, start playing the animation via the super class
                    animationPlaying =true;
                    super.start(LIGHTNING_ANIMATION, LIGHTNING_ANIMATION_TYPE, LIGHTNING_ANIMATION_TIME);
                } else {
                    //TODO: Water attack! - TEST
                    //Attack Logic: Queue up ability to play when animation reaches the appropriate point
                    regularLightningAttackQueued = true;
                    //Attack Logic: Stop moving in preparation for lightning strike
                    getNavigation().stop();
                    //Animation Logic: Set the animationPlaying flag correctly, start playing the animation via the super class
                    animationPlaying =true;
                    super.start(LIGHTNING_ANIMATION, LIGHTNING_ANIMATION_TYPE, LIGHTNING_ANIMATION_TIME);
                }
                //ToDo: Tweak values
                lightningAttackCooldown = 400;
            }

        }

        private boolean canPerformMeleeAttack() {
            if (target != null && distanceToSqr(target) <= 30 && !animationPlaying) {
                //GoalLogic: try to perform a melee attack
                return true;
            }
            return false;
        }

        //TODO: TWEAK VALUES ACCORDING TO ANIMATIONS
        //TODO: Account for water vs. land attacks - TEST
        public void performMeleeAttack() {
            //Randomly define an attack variant...
            attackVariant = 1+getRandom().nextInt(ATTACK_ANIMATION_VARIANTS);
            //Queue a melee attack, ensuring it happens once we reach the proper time...
            meleeAttackQueued = true;
            //An animation is now playing, we will not call any other melee attacks...
            animationPlaying =true;
            //Start the animation with the selected variant...
            boolean swimming = isUsingSwimmingNavigator();
            String navVariant = swimming? "water" : "land";

            switch (attackVariant) {
                case 1 ->
                        {
                    inflateValue = 0.2F;
                    disableShieldTime = 50;
                    int time = swimming? SWIM_ATTACK_ANIMATION_TIME_1 : LAND_ATTACK_ANIMATION_TIME_1;
                    attackQueueTime = swimming? SWIM_ATTACK_QUEUE_TIME_1 : LAND_ATTACK_QUEUE_TIME_1;
                    super.start(ATTACK_ANIMATION+navVariant+attackVariant,2,time);
                        }
                case 2 -> {
                    inflateValue = 0.2F;
                    disableShieldTime = 50;
                    int time = swimming? SWIM_ATTACK_ANIMATION_TIME_2 : LAND_ATTACK_ANIMATION_TIME_2;
                    attackQueueTime = swimming? SWIM_ATTACK_QUEUE_TIME_2 : LAND_ATTACK_QUEUE_TIME_2;
                    super.start(ATTACK_ANIMATION+navVariant+attackVariant,2,time);
                }
            }
            //Rotate the entity to face the target...
            float desiredAngleYaw = (float)(Mth.atan2(target.position().z-position().z, target.position().x - position().x) * (double)(180F / (float)Math.PI)) - 90.0F;
            setYRot(desiredAngleYaw);
        }
    }
}


