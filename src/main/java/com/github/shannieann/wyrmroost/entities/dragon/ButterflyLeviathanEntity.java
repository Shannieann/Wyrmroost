package com.github.shannieann.wyrmroost.entities.dragon;

/*import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.model.entity.ButterflyLeviathanModel;
import com.github.wolfshotz.wyrmroost.client.screen.DragonControlScreen;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.items.book.action.BookActions;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindHandler;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.LerpedFloat;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.LogicalAnimation;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeSupplier;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.EntityDataAccessor;
import net.minecraft.network.datasync.EntityDataSerializers;
import net.minecraft.network.datasync.SynchedEntityData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.MobEffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityHitResult;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.vector.Vec3;
import net.minecraft.world.ServerLevelAccessor;
import net.minecraft.world.IWorld;
import net.minecraft.world.LevelReader;
import net.minecraft.world.Level;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.entity.ai.attributes.Attributes.*;

public class ButterflyLeviathanEntity extends WRDragonEntity
{
    public static final EntitySerializer<ButterflyLeviathanEntity> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.INT, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant));

    public static final Animation LIGHTNING_ANIMATION = LogicalAnimation.create(64, ButterflyLeviathanEntity::lightningAnimation, () -> ButterflyLeviathanModel::roarAnimation);
    public static final Animation CONDUIT_ANIMATION = LogicalAnimation.create(59, ButterflyLeviathanEntity::conduitAnimation, () -> ButterflyLeviathanModel::conduitAnimation);
    public static final Animation BITE_ANIMATION = LogicalAnimation.create(17, ButterflyLeviathanEntity::biteAnimation, () -> ButterflyLeviathanModel::biteAnimation);
    public static final Animation[] ANIMATIONS = new Animation[]{LIGHTNING_ANIMATION, CONDUIT_ANIMATION, BITE_ANIMATION};

    public static final EntityDataAccessor<Boolean> HAS_CONDUIT = SynchedEntityData.defineId(ButterflyLeviathanEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int CONDUIT_SLOT = 0;

    public final LerpedFloat beachedTimer = LerpedFloat.unit();
    public final LerpedFloat swimTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public int lightningCooldown = 0;
    public boolean beached = true;

    public ButterflyLeviathanEntity(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
        noCulling = WRConfig.NO_CULLING.get();
        moveControl = new MoveController();
        maxUpStep = 2;

        setPathfindingMalus(PathNodeType.WATER, 0);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new WRSitGoal(this));
        goalSelector.addGoal(1, new MoveToHomeGoal(this));
        goalSelector.addGoal(2, new AttackGoal());
        goalSelector.addGoal(3, new WRFollowOwnerGoal(this));

        goalSelector.addGoal(4, new DragonBreedGoal(this));
        goalSelector.addGoal(5, new JumpOutOfWaterGoal());
        goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1, 40));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 14f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        targetSelector.addGoal(4, new DefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false, e ->
        {
            EntityType<?> type = e.getType();
            return e.isInWater() == isInWater() && (type == EntityType.PLAYER || type == EntityType.GUARDIAN || type == EntityType.SQUID);
        }));
    }

    @Override
    public EntitySerializer<ButterflyLeviathanEntity> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(HAS_CONDUIT, false);
        entityData.define(VARIANT, 0);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        Vec3 conduitPos = getConduitPos();

        // cooldown for lightning attack
        if (lightningCooldown > 0) --lightningCooldown;

        // handle "beached" logic (if this fat bastard is on land)
        boolean prevBeached = beached;
        if (!beached && onGround && !wasTouchingWater) beached = true;
        else if (beached && wasTouchingWater) beached = false;
        if (prevBeached != beached) refreshDimensions();
        beachedTimer.add((beached)? 0.1f : -0.05f);
        swimTimer.add(isUnderWater()? -0.1f : 0.1f);
        sitTimer.add(isInSittingPose()? 0.1f : -0.1f);

        if (isJumpingOutOfWater())
        {
            Vec3 motion = getDeltaMovement();
            xRot = (float) (Math.signum(-motion.y) * Math.acos(Math.sqrt(Entity.getHorizontalDistanceSqr(motion)) / motion.length()) * (double) (180f / Mafs.PI)) * 0.725f;
        }

        // conduit effects
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
                        entity.addEffect(new MobEffectInstance(Effects.CONDUIT_POWER, 220, 0, true, true));

                    if (!attacked && entity instanceof IMob)
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

            animationSpeedOld = animationSpeed;
            double xDiff = getX() - xo;
            double yDiff = getY() - yo;
            double zDiff = getZ() - zo;
            if (yDiff < 0.2) yDiff = 0;
            float amount = Mth.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff) * 4f;
            if (amount > 1f) amount = 1f;

            animationSpeed += (amount - animationSpeed) * 0.4f;
            animationPosition += animationSpeed;

            if (vec3d.z == 0 && getTarget() == null && !isInSittingPose())
                setDeltaMovement(getDeltaMovement().add(0, -0.003d, 0));
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
    public ItemStack eat(Level level, ItemStack stack)
    {
        lightningCooldown = 0;
        return super.eat(level, stack);
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant() == -1 && tickCount % 25 == 0)
        {
            double x = getX() + (Mafs.nextDouble(getRandom()) * getBbWidth() + 1);
            double y = getY() + (getRandom().nextDouble() * getBbHeight() + 1);
            double z = getZ() + (Mafs.nextDouble(getRandom()) * getBbWidth() + 1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == CONDUIT_SLOT)
        {
            boolean flag = stack.getItem() == Items.CONDUIT;
            boolean hadConduit = hasConduit();
            entityData.set(HAS_CONDUIT, flag);
            if (!onLoad && flag && !hadConduit) setAnimation(CONDUIT_ANIMATION);
        }
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (pressed && noAnimations())
        {
            if (key == KeybindHandler.MOUNT_KEY) setAnimation(BITE_ANIMATION);
            else if (key == KeybindHandler.ALT_MOUNT_KEY && !level.isClientSide && canZap())
            {
                EntityHitResult ertr = Mafs.clipEntities(getControllingPlayer(), 40, e -> e instanceof LivingEntity && e != this);
                if (ertr != null && wantsToAttack((LivingEntity) ertr.getEntity(), getOwner()))
                {
                    setTarget((LivingEntity) ertr.getEntity());
                    AnimationPacket.send(this, LIGHTNING_ANIMATION);
                }
            }
        }
    }

    @Override
    public boolean shouldSleep()
    {
        return false;
    }

    public Vec3 getConduitPos()
    {
        return getEyePosition(1)
                .add(0, 0.4, 0.35)
                .add(calculateViewVector(xRot, yHeadRot).scale(4.15));
    }

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), CONDUIT_SLOT, 0, -65, -75, DragonControlScreen.CONDUIT_UV).only(Items.CONDUIT).limit(1))
                .addAction(BookActions.TARGET);
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getInfo().move(ClientEvents.getViewCollision(-10, this), 1, 0);
        else
            event.getInfo().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level)
    {
        return level.noCollision(this);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public boolean defendsHome()
    {
        return true;
    }

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

    @Override
    protected PathNavigator createNavigation(Level level)
    {
        return new Navigator();
    }

    public boolean hasConduit()
    {
        return entityData.get(HAS_CONDUIT);
    }

    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 1);
    }

    public boolean isJumpingOutOfWater()
    {
        return !isInWater() && !beached;
    }

    public boolean canZap()
    {
        return isInWaterRainOrBubble() && lightningCooldown <= 0;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
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

    @Override
    public float getScale()
    {
        return getAgeScale(0.225f);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions size)
    {
        return size.height * (beached? 1f : 0.6f);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return getType().getDimensions().scale(getScale());
    }

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
    public int getYawRotationSpeed()
    {
        return 6;
    }

    @Override
    public int determineVariant()
    {
        return getRandom().nextDouble() < 0.02? -1 : getRandom().nextInt(2);
    }

    @Override
    public boolean canFly()
    {
        return false;
    }

    @Override
    public Animation[] getAnimations()
    {
        return ANIMATIONS;
    }

    @Override
    public CreatureAttribute getMobType()
    {
        return CreatureAttribute.WATER;
    }

    @Override
    public boolean checkSpawnRules(IWorld levelIn, MobSpawnType spawnReasonIn)
    {
        return true;
    }

    private static void createLightning(Level level, Vec3 position, boolean effectOnly)
    {
        if (level.isClientSide) return;
        LightningBoltEntity entity = EntityType.LIGHTNING_BOLT.create(level);
        entity.moveTo(position);
        entity.setVisualOnly(effectOnly);
        level.addFreshEntity(entity);
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

    public static AttributeSupplier.MutableAttribute getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 180)
                .add(MOVEMENT_SPEED, 0.08)
                .add(ForgeMod.SWIM_SPEED.get(), 0.3)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, 14)
                .add(FOLLOW_RANGE, 50);
    }

    public class Navigator extends SwimmerPathNavigator
    {
        public Navigator()
        {
            super(ButterflyLeviathanEntity.this, ButterflyLeviathanEntity.this.level);
        }

        @Override
        protected PathFinder createPathFinder(int range)
        {
            return new PathFinder(nodeEvaluator = new WalkAndSwimNodeProcessor(), range);
        }

        @Override
        public boolean isStableDestination(BlockPos pos)
        {
            return !level.getBlockState(pos.below()).isAir(level, pos.below());
        }

        @Override
        protected boolean canUpdatePath()
        {
            return true;
        }
    }

    private class MoveController extends MovementController
    {
        public MoveController()
        {
            super(ButterflyLeviathanEntity.this);
        }

        public void tick()
        {
            if (operation == Action.MOVE_TO && !canBeControlledByRider())
            {
                operation = Action.WAIT;
                double x = wantedX - getX();
                double y = wantedY - getY();
                double z = wantedZ - getZ();
                double distSq = x * x + y * y + z * z;
                if (distSq < 2.5000003E-7) setSpeed(0f); // why move...
                else
                {
                    float newYaw = (float) Math.toDegrees(Mth.atan2(z, x)) - 90f;
                    float pitch = -((float) (Mth.atan2(y, Mth.sqrt(x * x + z * z)) * 180 / Math.PI));
                    pitch = Mth.clamp(Mth.wrapDegrees(pitch), -85f, 85f);

                    yHeadRot = newYaw;
                    yBodyRot = yRot = rotlerp(yRot, yHeadRot, getYawRotationSpeed());
                    pitch = rotlerp(pitch, pitch, 75);
                    ((LessShitLookController) getLookControl()).stopLooking();
                    float speed = isInWater()? (float) getAttributeValue(ForgeMod.SWIM_SPEED.get()) : (float) getAttributeValue(MOVEMENT_SPEED);
                    setSpeed(speed);
                    if (isInWater())
                    {
                        zza = Mth.cos(pitch * (Mafs.PI / 180f)) * speed;
                        yya = -Mth.sin(pitch * (Mafs.PI / 180f)) * speed;
                    }
                }
            }
            else
            {
                setSpeed(0);
                setZza(0);
                setYya(0);
            }
        }
    }

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

            if (noAnimations())
            {
                if (distFromTarget > 225 && (isTame() || target.getType() == EntityType.PLAYER) && canZap())
                    AnimationPacket.send(ButterflyLeviathanEntity.this, LIGHTNING_ANIMATION);
                else if (isClose && Mth.degreesDifferenceAbs((float) Mafs.getAngle(ButterflyLeviathanEntity.this, target) + 90, yRot) < 30)
                    AnimationPacket.send(ButterflyLeviathanEntity.this, BITE_ANIMATION);
            }
        }
    }

    private class JumpOutOfWaterGoal extends Goal
    {
        private BlockPos pos;

        public JumpOutOfWaterGoal()
        {
            setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (isInSittingPose()) return false;
            if (canBeControlledByRider()) return false;
            if (!isUnderWater()) return false;
            if (level.getFluidState(this.pos = level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, blockPosition()).below()).isEmpty())
                return false;
            if (pos.getY() <= 0) return false;
            return getRandom().nextDouble() < 0.001;
        }

        @Override
        public boolean canContinueToUse()
        {
            return !canBeControlledByRider() && isUnderWater();
        }

        @Override
        public void start()
        {
            getNavigation().stop();
            this.pos = pos.relative(getDirection(), (int) ((pos.getY() - getY()) * 0.5d));
        }

        @Override
        public void tick()
        {
            getMoveControl().setWantedPosition(pos.getX(), pos.getY(), pos.getZ(), 1.2d);
        }

        @Override
        public void stop()
        {
            pos = null;
            clearAI();
        }
    }
}
*/