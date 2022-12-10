package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.WRRandomSwimmingGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.WRReturnToWaterGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.WRWaterLeapGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;


import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
//TODO: Pending BFL Fixes:
//TODO: Tweak pitch adjustments
//TODO: BFL textures, etc
//TODO: Jump out of water goal, speed

//This class: See which methods are needed, which are not

//Ground Nav <--> Swimmer Nav, test both
//Return to water goal

//LookRandomly, lookAtPlayer
//Attack: BFL Attack
//Attack: Dive and Leap?
//Attack: Nova?

//Fix serializer

//Ride logic + rewrite keybinds
//All tamed goals
//Eggs, breeding, taming

//Config Spawn + TidyUp EntityTypeRegistry

//Finalize assets!

public class ButterflyLeviathanEntity extends WRDragonEntity
{
    //TODO: Correct ALL Serializers
    public static final EntitySerializer<ButterflyLeviathanEntity> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant)
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender));

    public static final EntityDataAccessor<Boolean> HAS_CONDUIT = SynchedEntityData.defineId(ButterflyLeviathanEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int CONDUIT_SLOT = 0;
    public static final float YAW_ADJUSTMENT = 0.10F;
    public static final float DELTA_PITCH_LIMIT = 1.0F;

    public final LerpedFloat beachedTimer = LerpedFloat.unit();
    public final LerpedFloat swimTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public int lightningCooldown = 0;
    public boolean beached = true;

    public ButterflyLeviathanEntity(EntityType<? extends WRDragonEntity> entityType, Level level)
    {
        super(entityType, level);
        noCulling = WRConfig.NO_CULLING.get();
        //moveControl = new MoveController();
        maxUpStep = 2;
        setPathfindingMalus(BlockPathTypes.WATER, 0);
        this.adjustmentYaw = YAW_ADJUSTMENT;
        this.deltaPitchLimit = DELTA_PITCH_LIMIT;
    }

    // ====================================
    //      A) Entity Data
    // ====================================

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(HAS_CONDUIT, false);
    }

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 180)
                .add(MOVEMENT_SPEED, 0.08)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, 14)
                .add(FOLLOW_RANGE, 50);
    }

    @Override
    public EntitySerializer<ButterflyLeviathanEntity> getSerializer()
    {
        return SERIALIZER;
    }

    public boolean hasConduit()
    {
        return entityData.get(HAS_CONDUIT);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pReason) {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, Random random)
    {
        if (reason == MobSpawnType.SPAWNER) return true;
        if (level.getFluidState(pos).is(FluidTags.WATER))
        {
            final double chance = random.nextDouble();
            if (reason == MobSpawnType.CHUNK_GENERATION) return chance < 0.325;
            else if (reason == MobSpawnType.NATURAL) return chance < 0.001;
        }
        return false;
    }

    @Override
    public MobType getMobType()
    {
        return MobType.WATER;
    }

    public Vec3 getConduitPos()
    {
        return getEyePosition(1)
                .add(0, 0.4, 0.35)
                .add(calculateViewVector(xRot, yHeadRot).scale(4.15));
    }
    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

    @Override
    public boolean defendsHome()
    {
        return true;
    }

    // ====================================
    //      A.5) Entity Data: SLEEP
    // ====================================
    @Override
    public boolean shouldSleep()
    {
        return false;
    }

    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================


    @Override
    public String determineVariant()
    {
        return getRandom().nextDouble() < 0.02? "special" : "base"+getRandom().nextInt(2);
    }

    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions size)
    {
        return size.height * (beached? 1f : 0.6f);
    }

    @Override
    public float getScale()
    {
        return getAgeScale(0.225f);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level)
    {
        return level.noCollision(this);
    }


    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void aiStep()
    {
        super.aiStep();

        // =====================
        //       Update Timers
        // =====================
        beachedTimer.add((beached)? 0.1f : -0.05f);
        swimTimer.add(isUnderWater()? -0.1f : 0.1f);
        sitTimer.add(isInSittingPose()? 0.1f : -0.1f);


        if (lightningCooldown > 0) --lightningCooldown;
        // =====================
        //       Beached Logic
        // =====================
        boolean prevBeached = beached;

        if (!beached && onGround && !wasTouchingWater) {
            beached = true;
        }
        else if (beached && wasTouchingWater) {
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
        if (hasConduit())
        {
            if (level.isClientSide && isInWaterRainOrBubble() && getRandom().nextDouble() <= 0.1)
            {
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
            if (tickCount % 80 == 0)
            {
                boolean attacked = false;
                for (LivingEntity entity : getEntitiesNearby(25, Entity::isInWaterRainOrBubble))
                {
                    if (entity != getTarget() && (entity instanceof Player || isAlliedTo(entity)))
                        entity.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 220, 0, true, true));

                    if (!attacked && entity instanceof Enemy)
                    {
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
    public boolean canZap()
    {
        return isInWaterRainOrBubble() && lightningCooldown <= 0;
    }


    //TODO: Extract logic
    public void lightningAnimation(int time)
    {
        lightningCooldown += 6;
        if (time == 10) playSound(WRSounds.ENTITY_BFLY_ROAR.get(), 3f, 1f, true);
        if (!level.isClientSide && isInWaterRainOrBubble() && time >= 10)
        {
            LivingEntity target = getTarget();
            if (target != null)
            {
                if (hasConduit())
                {
                    if (time % 10 == 0)
                    {
                        Vec3 vec3d = target.position().add(Mafs.nextDouble(getRandom()) * 2.333, 0, Mafs.nextDouble(getRandom()) * 2.333);
                        createLightning(level, vec3d, false);
                    }
                }
                else if (time == 10) createLightning(level, target.position(), false);
            }
        }
    }

    public void conduitAnimation(int time)
    {
        ((LessShitLookController) getLookControl()).stopLooking();
        if (time == 0) playSound(WRSounds.ENTITY_BFLY_ROAR.get(), 5f, 1, true);
        else if (time == 15)
        {
            playSound(SoundEvents.BEACON_ACTIVATE, 1, 1);
            if (!level.isClientSide) createLightning(level, getConduitPos().add(0, 1, 0), true);
            else
            {
                Vec3 conduitPos = getConduitPos();
                for (int i = 0; i < 26; ++i)
                {
                    double velX = Math.cos(i);
                    double velZ = Math.sin(i);
                    level.addParticle(ParticleTypes.CLOUD, conduitPos.x, conduitPos.y + 0.8, conduitPos.z, velX, 0, velZ);
                }
            }
        }
    }

    public void biteAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_BFLY_HURT.get(), 1, 1, true);
        else if (time == 6)
            attackInBox(getBoundingBox().move(Vec3.directionFromRotation(isUnderWater()? xRot : 0, yHeadRot).scale(5.5f)).inflate(0.85), 40);
    }

    private static void createLightning(Level level, Vec3 position, boolean effectOnly)
    {
        if (level.isClientSide) return;
        LightningBolt entity = EntityType.LIGHTNING_BOLT.create(level);
        entity.moveTo(position);
        entity.setVisualOnly(effectOnly);
        level.addFreshEntity(entity);
    }

    @Override
    public boolean isImmuneToArrows()
    {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return ModUtils.contains(source, DamageSource.LIGHTNING_BOLT, DamageSource.IN_FIRE, DamageSource.IN_WALL) || super.isInvulnerableTo(source);
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public void travel(Vec3 vec3d)
    {
        if (isInWater())
        {
            if (canBeControlledByRider())
            {
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
        }
        else super.travel(vec3d);
    }

    @Override
    public float getTravelSpeed()
    {
        //@formatter:off
        return isInWater()? (float) getAttributeValue(ForgeMod.SWIM_SPEED.get())
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollision(-10, this), 1, 0);
        else
            event.getCamera().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
    }

    @Override
    public int getYawRotationSpeed()
    {
        return 6;
    }

    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    @Override
    public boolean canFly()
    {
        return false;
    }


    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================

    @Override
    public boolean canSwim()
    {
        return true;
    }
    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================
    @Override // 2 passengers
    protected boolean canAddPassenger(Entity passenger)
    {
        return isTame() && isJuvenile() && getPassengers().size() < 2;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index)
    {
        return new Vec3(0, getPassengersRidingOffset(), index == 1? -2 : 0);
    }

    @Override
    public boolean canBeRiddenInWater(Entity rider)
    {
        return true;
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (pressed /*&& noAnimations()*/)
        {
            if (key == KeybindHandler.MOUNT_KEY) /*setAnimation(BITE_ANIMATION)*/;
            else if (key == KeybindHandler.ALT_MOUNT_KEY && !level.isClientSide && canZap())
            {
                EntityHitResult ertr = Mafs.clipEntities(getControllingPlayer(), 40, e -> e instanceof LivingEntity && e != this);
                if (ertr != null && wantsToAttack((LivingEntity) ertr.getEntity(), getOwner()))
                {
                    setTarget((LivingEntity) ertr.getEntity());
                    /*AnimationPacket.send(this, LIGHTNING_ANIMATION);*/
                }
            }
        }
    }

    public boolean isJumpingOutOfWater()
    {
        return !isInWater() && !beached;
    }


    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), CONDUIT_SLOT, 0, -65, -75, DragonControlScreen.CONDUIT_UV).only(Items.CONDUIT).limit(1))
                .addAction(BookActions.TARGET);
    }


    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        if (((beached && lightningCooldown > 60 && level.isRainingAt(blockPosition())) || player.isCreative() || isHatchling()) && isFood(stack))
        {
            eat(stack);
            if (!level.isClientSide) tame(getRandom().nextDouble() < 0.2, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.playerInteraction(player, hand, stack);
    }


    @Override
    public ItemStack eat(Level level, ItemStack stack)
    {
        lightningCooldown = 0;
        return super.eat(level, stack);
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == CONDUIT_SLOT)
        {
            boolean flag = stack.getItem() == Items.CONDUIT;
            boolean hadConduit = hasConduit();
            entityData.set(HAS_CONDUIT, flag);
            //TODO: Set Animation
            //if (!onLoad && flag && !hadConduit) setAnimation(CONDUIT_ANIMATION);
        }
    }

    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 1);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public void doSpecialEffects()
    {
        if (getVariant().equals("special") && tickCount % 25 == 0)
        {
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
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_BFLY_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_BFLY_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_BFLY_DEATH.get();
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals()
    {
//        goalSelector.addGoal(0, new WRSitGoal(this));
//        goalSelector.addGoal(1, new MoveToHomeGoal(this));
//        goalSelector.addGoal(2, new WRFollowOwnerGoal(this));

//        goalSelector.addGoal(3, new DragonBreedGoal(this));

        //goalSelector.addGoal(4, new AttackGoal());
        
        goalSelector.addGoal(5, new WRReturnToWaterGoal(this, 1));
        goalSelector.addGoal(6, new WRWaterLeapGoal(this, 1));
        goalSelector.addGoal(7, new WRRandomSwimmingGoal(this, 0.5, 10,32,24));

//        goalSelector.addGoal(8, new LookAtPlayerGoal(this, LivingEntity.class, 14f));
//        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        /*
        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e ->
        {
            EntityType<?> type = e.getType();
            return e.isInWater() == isInWater() && (type == EntityType.PLAYER || type == EntityType.GUARDIAN || type == EntityType.SQUID);
        }));

         */
    }

    //TODO: Extract

    private class AttackGoal extends Goal
    {
        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            return !canBeControlledByRider() && getTarget() != null;
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();
            if (target == null) return;
            double distFromTarget = distanceToSqr(target);

            getLookControl().setLookAt(target, getMaxHeadYRot(), getMaxHeadXRot());

            boolean isClose = distFromTarget < 40;

            if (getNavigation().isDone())
                getNavigation().moveTo(target, 1.2);

            if (isClose) yRot = (float) Mafs.getAngle(ButterflyLeviathanEntity.this, target) + 90f;

            /*
            if (noAnimations())
            {
                if (distFromTarget > 225 && (isTame() || target.getType() == EntityType.PLAYER) && canZap())
                    AnimationPacket.send(ButterflyLeviathanEntity.this, LIGHTNING_ANIMATION);
                else if (isClose && Mth.degreesDifferenceAbs((float) Mafs.getAngle(ButterflyLeviathanEntity.this, target) + 90, yRot) < 30)
                    AnimationPacket.send(ButterflyLeviathanEntity.this, BITE_ANIMATION);
            }

             */
        }
    }
}
