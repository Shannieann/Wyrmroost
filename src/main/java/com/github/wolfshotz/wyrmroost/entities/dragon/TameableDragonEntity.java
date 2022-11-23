package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;

//import com.github.wolfshotz.wyrmroost.client.sound.FlyingSound;
import com.github.wolfshotz.wyrmroost.client.sound.FlyingSound;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRSitGoal;
//import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.DragonEggItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRKeybind;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.LerpedFloat;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.shadowed.eliotlash.mclib.utils.MathHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


/**
 * Created by com.github.WolfShotz 7/10/19 - 21:36
 * This is where the magic happens. Here be our Dragons!
 */public abstract class TameableDragonEntity extends TamableAnimal implements IAnimatable, MenuProvider
{
    public static final EntitySerializer<TameableDragonEntity> SERIALIZER = EntitySerializer.builder(b -> b
            .track(EntitySerializer.POS.optional(), "HomePos", t -> Optional.ofNullable(t.getHomePos()), (d, v) -> d.setHomePos(v.orElse(null)))
            .track(EntitySerializer.INT, "BreedCount", TameableDragonEntity::getBreedCount, TameableDragonEntity::setBreedCount));

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;
    private static final int AGE_UPDATE_INTERVAL = 200;
    private static final UUID SCALE_MOD_UUID = UUID.fromString("81a0addd-edad-47f1-9aa7-4d76774e055a");

    // Common Data Parameters
    public static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.INT); // todo in 1.17: make this use strings for nbt based textures
    public static final EntityDataAccessor<ItemStack> ARMOR = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(TameableDragonEntity.class, EntityDataSerializers.INT);

    @Deprecated // https://github.com/MinecraftForge/MinecraftForge/issues/7622
    public final LazyOptional<DragonInventory> inventory;
    public final LerpedFloat sleepTimer = LerpedFloat.unit();
    private int sleepCooldown;
    public boolean wingsDown;
    public int breedCount;
    private int animationTick;
    private float ageProgress = 1;

    public TameableDragonEntity(EntityType<? extends TameableDragonEntity> dragon, Level level)
    {
        super(dragon, level);

        maxUpStep = 1;

        this.noCulling = true;
        DragonInventory inv = createInv();
        inventory = LazyOptional.of(inv == null? null : () -> inv);
        lookControl = new LessShitLookController(this);
        if (hasEntityDataAccessor(FLYING)) moveControl = new FlyerMoveController(this);
    }

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

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WRSitGoal(this));
    }

    public abstract EntitySerializer<? extends TameableDragonEntity> getSerializer();

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        if (inventory.isPresent()) nbt.put("Inv", inventory.orElse(null).serializeNBT());
        ((EntitySerializer<TameableDragonEntity>) getSerializer()).serialize(this, nbt);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        if (inventory.isPresent()) inventory.orElse(null).deserializeNBT(nbt.getCompound("Inv"));
        ((EntitySerializer<TameableDragonEntity>) getSerializer()).deserialize(this, nbt);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(HOME_POS, BlockPos.ZERO);
        entityData.define(AGE, 0);
    }

    public boolean hasEntityDataAccessor(EntityDataAccessor<?> param)
    {
        return entityData.itemsById.containsKey(param.getId());
    }

    public int getVariant()
    {
        return hasEntityDataAccessor(VARIANT)? entityData.get(VARIANT) : 0;
    }

    public void setVariant(int variant)
    {
        entityData.set(VARIANT, variant);
    }

    /**
     * @return true for male, false for female. anything else is a political abomination and needs to be cancelled.
     */
    public boolean isMale()
    {
        return !hasEntityDataAccessor(GENDER) || entityData.get(GENDER);
    }

    public void setGender(boolean sex)
    {
        entityData.set(GENDER, sex);
    }

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

    public boolean isFlying()
    {
        return hasEntityDataAccessor(FLYING) && entityData.get(FLYING);
    }

    public void setFlying(boolean fly)
    {
        if (isFlying() == fly) return;
        entityData.set(FLYING, fly);
        Path prev = navigation.getPath();
        if (fly)
        {
            // make sure NOT to switch the navigator if liftoff fails
            setOnGround(false);
            if (liftOff()) navigation = new FlyerPathNavigator(this);
            else return;
        }
        else {
            setOnGround(true);
            navigation = new BetterPathNavigator(this);
        }

        navigation.moveTo(prev, 1);
    }

    public boolean hasArmor()
    {
        return hasEntityDataAccessor(ARMOR) && entityData.get(ARMOR).getItem() instanceof DragonArmorItem;
    }

    public ItemStack getArmorStack()
    {
        return hasEntityDataAccessor(ARMOR)? entityData.get(ARMOR) : ItemStack.EMPTY;
    }

    public void setArmor(@Nullable ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof DragonArmorItem)) stack = ItemStack.EMPTY;
        entityData.set(ARMOR, stack);
    }

    @Override
    public void setInSittingPose(boolean flag)
    {
        super.setInSittingPose(flag);
        if (flag) clearAI();
    }

    public DragonInventory getInventory()
    {
        return inventory.orElseThrow(() -> new NoSuchElementException("This boi doesn't have an inventory wtf are u doing"));
    }

    public DragonInventory createInv()
    {
        return null;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (level.isClientSide)
        {
            doSpecialEffects();

            // because age isn't incremented on client, do it ourselves...
            int age = getAge();
            if (age < 0) setAge(++age);
            else if (age > 0) setAge(--age);
        }
        else
        {
            // uhh so were falling, we should probably start flying
            boolean flying = shouldFly();
            if (flying != isFlying()) setFlying(flying);

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

            // todo figure out a better target system?
            LivingEntity target = getTarget();
            if (target != null && (!target.isAlive() || !canAttack(target) || !wantsToAttack(target, getOwner())))
                setTarget(null);
        }

        updateAgeProgress();
        if (age < 0 && tickCount % AGE_UPDATE_INTERVAL == 0) entityData.set(AGE, age);
    }

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
                if (!canFly())
                {
                    stopRiding();
                    return;
                }

                vec3d = vec3d.scale(1.5);
                setFlying(true);
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

        if (isOwnedBy(player) && player.isShiftKeyDown() && !isFlying())
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
    public void travel(Vec3 vec3d)
    {
        float speed = getTravelSpeed();
        boolean isFlying = isFlying();

        if (canBeControlledByRider()) // Were being controlled; override ai movement
        {
            LivingEntity entity = (LivingEntity) getControllingPassenger();
            double moveX = entity.xxa * 0.5;
            double moveY = vec3d.y;
            double moveZ = entity.zza;

            // rotate head to match driver. yaw is handled relative to this.
            yHeadRot = entity.yHeadRot;
            setXRot(entity.getXRot() * 0.65f);;
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
                    if (entity.jumping && canFly()) setFlying(true);
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
        }

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

    @Override
    public void calculateEntityAnimation(LivingEntity what, boolean includeY)
    {
        if (isFlying())
        {
            animationSpeedOld = animationSpeed;
            double x = getX() - xo;
            double y = includeY ? getY() - yo : 0.0D;
            double z = getZ() - zo;
            float speed = Mth.sqrt((float) (x * x + y * y + z * z)) * 4f;
            if (speed > 1f) speed = 1f;

            animationSpeed += (speed - animationSpeed) * 0.4F;
            animationPosition += animationSpeed;
        }
        else super.calculateEntityAnimation(what, includeY);
    }

    public float getTravelSpeed()
    {
        //@formatter:off
        return isFlying()? (float) getAttributeValue(FLYING_SPEED)
                : (float) getAttributeValue(MOVEMENT_SPEED);
        //@formatter:on
    }

    public boolean shouldFly()
    {
        return canFly() && getAltitude() > getFlightThreshold();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (key.equals(SLEEPING) || key.equals(FLYING) || key.equals(TamableAnimal.DATA_FLAGS_ID))
        {
            refreshDimensions();
            if (level.isClientSide && key == FLYING && isFlying() && canBeControlledByRider()) FlyingSound.play(this);
        }
        else if (key == ARMOR)
        {
            if (!level.isClientSide)
            {
                AttributeInstance attribute = getAttribute(Attributes.ARMOR);
                if (attribute.getModifier(DragonArmorItem.ARMOR_UUID) != null)
                    attribute.removeModifier(DragonArmorItem.ARMOR_UUID);
                if (hasArmor())
                {
                    attribute.addTransientModifier(new AttributeModifier(DragonArmorItem.ARMOR_UUID, "Armor Modifier", DragonArmorItem.getDmgReduction(getArmorStack()), AttributeModifier.Operation.ADDITION));
                    playSound(SoundEvents.ARMOR_EQUIP_DIAMOND, 1, 1, true);
                }
            }
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

    public Attribute[] getScaledAttributes()
    {
        return new Attribute[]{MAX_HEALTH, ATTACK_DAMAGE};
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

    public AABB getOffsetBox(float offset)
    {
        return getBoundingBox().move(Vec3.directionFromRotation(0, yBodyRot).scale(offset));
    }

    @Override // Dont damage owners other pets!
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
    public boolean canAttack(LivingEntity target)
    {
        return !isHatchling() && !canBeControlledByRider() && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (isImmuneToArrows() && source.getDirectEntity() != null)
        {
            EntityType<?> attackSource = source.getDirectEntity().getType();
            if (attackSource == EntityType.ARROW) return false;
            else if (attackSource == WREntities.GEODE_TIPPED_ARROW.get()) amount *= 0.5f;
        }

        setSleeping(false);
        setOrderedToSit(false);
        return super.hurt(source, amount);
    }

    public void doSpecialEffects()
    {
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

    @Nullable
    public BlockPos getHomePos()
    {
        BlockPos pos = entityData.get(HOME_POS);
        return pos == BlockPos.ZERO? null : pos;
    }

    public void setHomePos(@Nullable BlockPos pos)
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

    public boolean isAtHome()
    {
        return hasRestriction() && isWithinRestriction();
    }

    @Override
    protected void dropEquipment()
    {
        inventory.ifPresent(i -> i.getContents().forEach(this::spawnAtLocation));
    }

    public void dropStorage()
    {
    }

    public void setRotation(float yaw, float pitch)
    {
        this.setYRot(yaw % 360.0F);
        this.setXRot(pitch % 360.0F);
    }

    public double getAltitude()
    {
        BlockPos.MutableBlockPos pos = blockPosition().mutable();

        // cap to the level void (y = 0)
        while (pos.getY() > 0 && !level.getBlockState(pos.move(Direction.DOWN)).getMaterial().isSolid());
        return getY() - pos.getY();
    }

    // overload because... WHY IS `Level` A PARAMETER WTF THE FIELD IS LITERALLY PUBLIC
    public void eat(ItemStack stack)
    {
        eat(level, stack);
    }

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
    public void heal(float healAmount)
    {
        super.heal(healAmount);
        level.broadcastEntityEvent(this, HEAL_PARTICLES_EVENT_ID);
    }

    public int getYawRotationSpeed()
    {
        return isFlying()? 6 : 75;
    }

    public boolean isRiding()
    {
        return getVehicle() != null;
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

    private void updateAgeProgress()
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
    public boolean canMate(Animal mate)
    {
        if (!(mate instanceof TameableDragonEntity)) return false;
        TameableDragonEntity dragon = (TameableDragonEntity) mate;
        if (isInSittingPose() || dragon.isInSittingPose()) return false;
        if (hasEntityDataAccessor(GENDER) && isMale() == dragon.isMale()) return false;
        return super.canMate(mate);
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
        ((TameableDragonEntity) mate).breedCount++;

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

    public int getBreedCount()
    {
        return breedCount;
    }

    public void setBreedCount(int i)
    {
        this.breedCount = i;
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

    public void clearAI()
    {
        jumping = false;
        navigation.stop();
        setTarget(null);
        setSpeed(0);
        setYya(0);
    }

    public boolean isIdling()
    {
        return getNavigation().isDone() && getTarget() == null && !isVehicle() && !isInWaterOrBubble() && !isFlying();
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

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(SpawnEggItem.byId(getType()));
    }

    public List<LivingEntity> getEntitiesNearby(double radius, Predicate<LivingEntity> filter)
    {
        return level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), filter.and(e -> e != this));
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

    public void flapWings()
    {
        playSound(WRSounds.WING_FLAP.get(), 3, 1, false);
        setDeltaMovement(getDeltaMovement().add(0, 1.285, 0));
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        if (isRiding() && source == DamageSource.IN_WALL) return true;
        if (isImmuneToArrows() && source == DamageSource.CACTUS) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag dataTag)
    {
        if (hasEntityDataAccessor(GENDER)) setGender(getRandom().nextBoolean());
        if (hasEntityDataAccessor(VARIANT)) setVariant(determineVariant());

        return super.finalizeSpawn(level, difficulty, reason, data, dataTag);
    }

    public int determineVariant()
    {
        return 0;
    }

    @Override
    public boolean isPickable()
    {
        return super.isPickable() && !isRiding();
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

    @Override
    public boolean isSuppressingSlidingDownLadder()
    {
        return false;
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

    public boolean defendsHome()
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

    public boolean canFly()
    {
        return isJuvenile() && !isUnderWater() && !isLeashed();
    }

    /**
     * Get the motion this entity performs when jumping
     */
    @Override
    protected float getJumpPower()
    {
        return canFly()? (getBbHeight() * getBlockJumpFactor()) * 0.6f : super.getJumpPower();
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

    @Override // Disable fall calculations if we can fly (fall damage etc.)
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source)
    {
        return !canFly() && super.causeFallDamage(distance - (int) (getBbHeight() * 0.8), damageMultiplier, source);
    }

    public int getFlightThreshold()
    {
        return (int) getBbHeight();
    }

    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
    }

    @Override
    public void dropLeash(boolean sendPacket, boolean dropLead)
    {
        super.dropLeash(sendPacket, dropLead);
        clearHome();
    }

    @Deprecated
    public boolean isImmuneToArrows()
    {
        return false;
    }

    /*public void applyStaffInfo(BookContainer container)
    {
        container.addAction(BookActions.HOME, BookActions.SIT)
                .addTooltip(getName())
                .addTooltip(new StringTextComponent(Character.toString('\u2764'))
                        .withStyle(TextFormatting.RED)
                        .append(new StringTextComponent(String.format(" %s / %s", (int) (getHealth() / 2), (int) getMaxHealth() / 2))
                                .withStyle(TextFormatting.WHITE)));

        if (hasEntityDataAccessor(GENDER))
        {
            boolean isMale = isMale();
            container.addTooltip(new TranslationTextComponent("entity.wyrmroost.dragons.gender." + (isMale? "male" : "female"))
                    .withStyle(isMale? TextFormatting.DARK_AQUA : TextFormatting.RED));
        }
    }*/

    @Override
    public Component getDisplayName()
    {
        return super.getDisplayName();
    }

    /*@Override
    public Container createMenu(int id, Inventory playersInv, Player player)
    {
        return new BookContainer(id, playersInv, this);
    }*/

    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory.isPresent() && !getInventory().isEmpty())
            return inventory.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public abstract boolean isFood(ItemStack stack);

    public boolean isBreedingItem(ItemStack stack)
    {
        return isFood(stack);
    }

    // ================================
    //        Entity Animation
    // ================================
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public abstract <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event);
    public static boolean canFlyerSpawn(EntityType<? extends TameableDragonEntity> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, Random random)
    {
        return level.getBlockState(pos.below()).getFluidState().isEmpty();
    }
}