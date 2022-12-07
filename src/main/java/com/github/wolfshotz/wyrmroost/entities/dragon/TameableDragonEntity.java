package com.github.wolfshotz.wyrmroost.entities.dragon;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.sound.FlyingSound;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRSitGoal;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.DragonEggItem;
import com.github.wolfshotz.wyrmroost.items.book.action.BookActions;
import com.github.wolfshotz.wyrmroost.registry.WRKeybind;
import com.github.wolfshotz.wyrmroost.util.LerpedFloat;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public abstract class TameableDragonEntity extends WRDragonEntity implements MenuProvider
{
    public static final EntitySerializer<TameableDragonEntity> SERIALIZER = EntitySerializer.builder(b -> b
            .track(EntitySerializer.POS.optional(), "HomePos", t -> Optional.ofNullable(t.getHomePos()), (d, v) -> d.setHomePos(v.orElse(null)))
            .track(EntitySerializer.INT, "BreedCount", TameableDragonEntity::getBreedCount, TameableDragonEntity::setBreedCount));

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;

    @Deprecated // https://github.com/MinecraftForge/MinecraftForge/issues/7622
    public final LazyOptional<DragonInventory> inventory;
    public final LerpedFloat sleepTimer = LerpedFloat.unit();

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


    // =====================
    //      Entity Data
    // =====================

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
    }
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
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(SLEEPING) || key.equals(FLYING) || key.equals(TamableAnimal.DATA_FLAGS_ID)) {
            refreshDimensions();
            if (level.isClientSide && key == FLYING && isFlying() && canBeControlledByRider()) FlyingSound.play(this);
        } else if (key == ARMOR) {
            if (!level.isClientSide) {
                AttributeInstance attribute = getAttribute(Attributes.ARMOR);
                if (attribute.getModifier(DragonArmorItem.ARMOR_UUID) != null)
                    attribute.removeModifier(DragonArmorItem.ARMOR_UUID);
                if (hasArmor()) {
                    attribute.addTransientModifier(new AttributeModifier(DragonArmorItem.ARMOR_UUID, "Armor Modifier", DragonArmorItem.getDmgReduction(getArmorStack()), AttributeModifier.Operation.ADDITION));
                    playSound(SoundEvents.ARMOR_EQUIP_DIAMOND, 1, 1, true);
                }
            } else super.onSyncedDataUpdated(key);
        }
    }

    public abstract EntitySerializer<? extends TameableDragonEntity> getSerializer();



    // =====================
    //      Breeding/Eating Methods
    // =====================

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


    // =====================
    //      Riding Methods
    // =====================

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
        } else {
            super.travel(vec3d);
        }
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
    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        if (isRiding() && source == DamageSource.IN_WALL) return true;
        if (isImmuneToArrows() && source == DamageSource.CACTUS) return true;
        return super.isInvulnerableTo(source);
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

    // =====================
    //      Tamed Methods
    // =====================
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
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(SpawnEggItem.byId(getType()));
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
            boolean isMale = isMale();
            container.addTooltip(new TranslatableComponent("entity.wyrmroost.dragons.gender." + (isMale? "male" : "female"))
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

    // =====================
    //      Name Methods
    // =====================

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


    // =====================
    //      Inventory Methods
    // =====================
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playersInv, Player player)
    {
        System.out.println(new BookContainer(id, playersInv, this));
        return new BookContainer(id, playersInv, this);
    }

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

    public DragonInventory createInv()
    {
        return null;
    }

    @Override
    protected void dropEquipment()
    {
        inventory.ifPresent(i -> i.getContents().forEach(this::spawnAtLocation));
    }

    public void dropStorage()
    {
    }
    // =====================
    //      Misc Methods
    // =====================
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

    //TODO: Does registering Goals here have a point?
    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WRSitGoal(this));
    }

    @Override
    public abstract boolean isFood(ItemStack stack);
    public List<LivingEntity> getEntitiesNearby(double radius, Predicate<LivingEntity> filter)
    {
        return level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), filter.and(e -> e != this));
    }

}