package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import static net.minecraft.world.entity.ai.attributes.Attributes.*;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.containers.RideableDragonInventoryContainer;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

// A lot of fields and methods in WRDragonEntity are specific to rideable dragons/dragons that can carry stuff in chests.
// Moving riding-specific code here to keep WRDragonEntity and each dragon file cleaner/simpler. Kind of like AbstractHorse
public abstract class WRRideableDragonEntity extends WRDragonEntity implements ContainerListener {

    /** Synced from server to client so both use the same value (avoids "moved wrongly"). Not saved to NBT. */
    private static final EntityDataAccessor<Float> MOMENTUM = SynchedEntityData.defineId(WRRideableDragonEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(WRRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> CHESTED = SynchedEntityData.defineId(WRRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);

    /** Donkey-like inventory: slot 0 = saddle, 1 = armor, 2–16 = chest (when chested). */
    private SimpleContainer rideableDragonInventory;
    public static final int INV_CHEST_SLOTS = 15;
    public static final int INV_BASE_SIZE = 2;
    public static final int SADDLE_SLOT = 0;
    public static final int ARMOR_SLOT = 1;
    public static final int CHEST_SLOT = 2;

    public WRRideableDragonEntity(EntityType<? extends WRRideableDragonEntity> rideableDragon, Level level) {
        super(rideableDragon, level);
        createRideableDragonInventory();
    }

    // ====================================
    //      Animations
    // ====================================

    // Chest handling
    // Create a new predicate b/c this should be able to run even when other animations are running as well, independent of override
    private boolean chestOpened = false;
    public <E extends IAnimatable> PlayState predicateChest(AnimationEvent<E> event){
        // If the main rider is a player
        if (isControlledByLocalInstance()){
            // If they enter their inventory
            if (ClientEvents.getClient().screen instanceof EffectRenderingInventoryScreen){
                if (chestOpened) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("chest_opened", ILoopType.EDefaultLoopTypes.LOOP));
                }
                 else {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("chest_open", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    if (event.getController().getAnimationState().equals(AnimationState.Stopped)) // When this animation finishes stay on the chest_opened animation
                        chestOpened = true;
                }

            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("chest_close", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                chestOpened = false;
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController(this, "controllerChest", 0, this::predicateChest));
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    @Override
    protected void defineSynchedData() {
        entityData.define(MOMENTUM, 0f);
        entityData.define(SADDLED, false);
        entityData.define(CHESTED, false);
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Saddled", isSaddled());
        nbt.putBoolean("Chested", isChested());
        if (rideableDragonInventory != null) {
            if (!rideableDragonInventory.getItem(SADDLE_SLOT).isEmpty()) {
                nbt.put("SaddleItem", rideableDragonInventory.getItem(SADDLE_SLOT).save(new CompoundTag()));
            }
            if (!rideableDragonInventory.getItem(ARMOR_SLOT).isEmpty()) {
                nbt.put("ArmorItem", rideableDragonInventory.getItem(ARMOR_SLOT).save(new CompoundTag()));
            }
            if (isChested()) {
                ListTag items = new ListTag();
                for (int i = INV_BASE_SIZE; i < rideableDragonInventory.getContainerSize(); i++) {
                    ItemStack stack = rideableDragonInventory.getItem(i);
                    if (!stack.isEmpty()) {
                        CompoundTag tag = new CompoundTag();
                        tag.putByte("Slot", (byte) i);
                        stack.save(tag);
                        items.add(tag);
                    }
                }
                nbt.put("ChestItems", items);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setSaddled(nbt.getBoolean("Saddled"));
        setChested(nbt.getBoolean("Chested"));
        createRideableDragonInventory();
        if (rideableDragonInventory != null) {
            if (nbt.contains("SaddleItem", 10)) {
                ItemStack saddle = ItemStack.of(nbt.getCompound("SaddleItem"));
                if (saddle.is(Items.SADDLE)) {
                    rideableDragonInventory.setItem(SADDLE_SLOT, saddle);
                }
            }
            if (nbt.contains("ArmorItem", 10)) {
                rideableDragonInventory.setItem(ARMOR_SLOT, ItemStack.of(nbt.getCompound("ArmorItem")));
            }
            if (isChested() && nbt.contains("ChestItems", 9)) {
                ListTag items = nbt.getList("ChestItems", 10);
                for (int i = 0; i < items.size(); i++) {
                    CompoundTag tag = items.getCompound(i);
                    int slot = tag.getByte("Slot") & 255;
                    if (slot >= INV_BASE_SIZE && slot < rideableDragonInventory.getContainerSize()) {
                        rideableDragonInventory.setItem(slot, ItemStack.of(tag));
                    }
                }
            }
            updateRideableDragonContainerEquipment();
        }
    }

    // ====================================
    //      A.2) Entity Data: INVENTORY
    // ====================================

    // May need to be overridden by child classes
    public boolean canEquipChest() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    public boolean isSaddled() {
        return entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        if (hasEntityDataAccessor(SADDLED)) {
            entityData.set(SADDLED, saddled);
        }
    }

    public boolean isChested() {
        return canEquipChest() && entityData.get(CHESTED);
    }

    /** Call when chest is added/removed to resize inventory. */
    public void setChested(boolean chested) {
        if (hasEntityDataAccessor(CHESTED)) {
            entityData.set(CHESTED, chested);
        }
        createRideableDragonInventory();
    }

    @SuppressWarnings("null")
    @Override
    protected void dropEquipment() {

        if (level.isClientSide) {
            return;
        }

        if (isChested()) {
            spawnAtLocation(new ItemStack(Items.CHEST));
        }
        if (isSaddled()) {
            spawnAtLocation(new ItemStack(Items.SADDLE));
        }
        setSaddled(false);
        setChested(false);
        super.dropEquipment();
    }

    @Override
    public void dropStorage() {
        if (rideableDragonInventory != null && isChested()) {
            for (int i = INV_BASE_SIZE; i < rideableDragonInventory.getContainerSize(); i++) {
                ItemStack stack = rideableDragonInventory.getItem(i);
                if (!stack.isEmpty()) {
                    spawnAtLocation(stack, getBbHeight() / 2f);
                    rideableDragonInventory.setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override public void travel(Vec3 vec3d) {
        if (isControlledByLocalInstance()) {
            if (! this.isAlive()) {
                return;
            }

            LivingEntity rider = (LivingEntity) this.getControllingPassenger();

            // Store previous yaw value
            this.yRotO = this.getYRot();

            // While being ridden, entity's pitch = 0.5 of rider's pitch
            this.setXRot(rider.getXRot() * 0.5F);

            // Client (rendering): Align body to entity direction
            this.yBodyRot = this.getYRot();

            // Client (rendering): Align head to body
            this.yHeadRot = this.yBodyRot;

            // This should allow for strafing
            float sideMotion = rider.xxa * 0.5F;

            // This allows for moving forward
            float forwardMotion = rider.zza;

            //If rider wants to turn sideways (yaw)...
            //Linear Interpolation system for changing the vehicle's yaw...
            //The Vehicle's Yaw will approach the Rider's Yaw...
            //The speed at which it approaches depends on the speed of the vehicle...
            Vec3 deltaMovement = getDeltaMovement();
            double deltaMovementXZlength = Math.sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z);
            double alphaValue = deltaMovementXZlength > 1.0F ? 1.0F : deltaMovementXZlength;
            if (rider.yRot > this.yRot) {
                setYRot((float) (this.yRot + (rider.yRot - this.yRot) * alphaValue));
            } else if (rider.yRot < this.yRot) {
                setYRot((float) (this.yRot + (rider.yRot - this.yRot) * alphaValue));
            }

            // Gain momentum while moving, lose it all when stopped.
            if (!level.isClientSide) {
                float mom = getMomentum();
                if (deltaMovement.length() > 0.0f) {
                    if (mom <= 0.12f) mom += 0.001f;
                } else {
                    mom = 0.0f;
                }
                setMomentum(mom);
            }

            if (forwardMotion < 0.0F) { // Huh? Ig I'll keep it here because it works
                forwardMotion *= 0.25F; // Ohhh it's like if you're going backward you're slower I guess.
            }

            // ToDo: What is this flying speed case?
            this.flyingSpeed = this.getSpeed() * 0.1F;

            // Handle movement based on navigator type
            float speed = getTravelSpeed();
            if (isUsingFlyingNavigator()) {
                handleFreeFlyingRiding(speed, rider); // Free Flying (Diving, 180s, etc.)
                // else handleCombatFlyingMovement(speed, livingentity); // Combat flying (More controlled flight)
            } else if (isUsingSwimmingNavigator()) {
                handleWaterRiding(5, sideMotion, 5, vec3d, rider);
            } else {
                handleGroundRiding(speed, sideMotion, forwardMotion, vec3d, rider);
            }

            this.calculateEntityAnimation(this, isUsingFlyingNavigator());
            this.tryCheckInsideBlocks();
        }
        else {
            super.travel(vec3d);
        }
    }

    /** Current momentum (synced; server writes, client reads). */
    protected final float getMomentum() {
        return entityData.get(MOMENTUM);
    }

    /** Set momentum (server only; synced to client). */
    protected final void setMomentum(float value) {
        entityData.set(MOMENTUM, value);
    }

    @Override
    public float getTravelSpeed()
    {
        return (float) getAttributeValue(MOVEMENT_SPEED) + getMomentum();
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================
    @Override
    protected boolean canAddPassenger(Entity entity)
    {
        return isJuvenile() && isOwnedBy((LivingEntity) entity);
    }

    @Override
    public boolean canBeControlledByRider() {
        return super.canBeControlledByRider() && isSaddled();
    }

    // Many child classes will need to override this method for special
    // functionality.
    protected InteractionResult rideableDragonMobInteractHelper(Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        // shiftclick is for sit => patrol => follow only
        if (isOwnedBy(player) && player.isShiftKeyDown() && !isVehicle()) {

            if (getSitting()) { // Set to patrol mode
                setSitting(false);
                BlockPos homePos = new BlockPos(position());
                setHomePos(homePos);
                player.displayClientMessage(new TranslatableComponent("command.wyrmroost.dragon.patrol",
                        getName(),
                        "(" + homePos.getX() + ", " + homePos.getY() + ", " + homePos.getZ() + ")")
                        .withStyle(ChatFormatting.ITALIC), true);
                return InteractionResult.SUCCESS;
            } else if (getHomePos() != null) { // Set to follow mode (default if no homePos and not sitting)
                clearHome();
                setSitting(false);
                player.displayClientMessage(new TranslatableComponent("command.wyrmroost.dragon.follow",
                        getName())
                        .withStyle(ChatFormatting.ITALIC), true);
                return InteractionResult.SUCCESS;
                // TODO: Should BFLs be able to sit on land?
            } else if (!isUsingFlyingNavigator() && (isOnGround() || (speciesCanSwim() && isInWater()))) { // Set to
                                                                                                           // stay mode
                setSitting(true);
                player.displayClientMessage(new TranslatableComponent("command.wyrmroost.dragon.sit",
                        getName())
                        .withStyle(ChatFormatting.ITALIC), true);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // OWD ride for taming is handled here
        if (this instanceof ITameable && !isTame()) {
            return ((ITameable) this).tameLogic(player, stack); // overrides need to call attemptTame
        }

        if (this instanceof IBreedable && isAdult() && isTame()
                && getBreedingCooldown() <= 0 && getBreedingCount() < ((IBreedable) this).getBreedingLimit()
                && isBreedingItem(stack)) {
            IBreedable thisIBreedable = (IBreedable) this;
            InteractionResult result = thisIBreedable.breedLogic(player, stack); // overrides need to set cooldown
            if (result == InteractionResult.SUCCESS) {
                setInLove(player);
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                if (isFood(stack) || isDrink(stack)) { // skip eating cooldown check if breeding
                    eat(this.level, stack);
                } else {
                    this.usePlayerItem(player, hand, stack);
                }
            }
            return result;
        }

        if (isOwnedBy(player) && (isFood(stack) || isDrink(stack))) {
            if (getEatingCooldown() <= 0) {
                eat(this.level, stack);
                this.usePlayerItem(player, hand, stack);
                this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                return InteractionResult.SUCCESS;
            }
        }

        // Saddle by right-clicking while holding saddle
        if (isOwnedBy(player) && stack.getItem() == Items.SADDLE) {
            if (!isSaddled()) {
                setSaddled(true);
                if (!player.getAbilities().instabuild)
                    stack.shrink(1);
                playSound(SoundEvents.HORSE_SADDLE, 1f, 1f);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // Add chest by right-clicking while holding chest
        if (isOwnedBy(player) && stack.getItem() == Items.CHEST && canEquipChest()) {
            if (!isChested()) {
                setChested(true);
                if (!player.getAbilities().instabuild)
                    stack.shrink(1);
                playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1f, 1f);
                return InteractionResult.SUCCESS;
            }
        }

        // if dragon not rideable, and dragon can equip held item, right click sets held item
        // at some point we're going to have a dragon that can hold things and be ridden
        // and I'll have to change this

        if (isOwnedBy(player) && this.canAddPassenger(player)) {
            player.startRiding(this);
            travelX0 = this.position().x;
            travelY0 = this.position().y;
            travelZ0 = this.position().z;
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult result = rideableDragonMobInteractHelper(player, hand);
        if (result == InteractionResult.SUCCESS && isSaddled() && getRideableDragonInventory().getItem(SADDLE_SLOT).isEmpty()) {
            getRideableDragonInventory().setItem(SADDLE_SLOT, new ItemStack(Items.SADDLE));
        }
        return result;
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    /**
     * Size: 2 (saddle + armor) or 2 + 15 when chested, like AbstractChestedHorse.
     */
    protected int getRideableDragonInventorySize() {
        return isChested() ? INV_BASE_SIZE + INV_CHEST_SLOTS : INV_BASE_SIZE;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new RideableDragonInventoryContainer(id, playerInv, this);
    }

    private void createRideableDragonInventory() {
        int newSize = getRideableDragonInventorySize();
        SimpleContainer old = this.rideableDragonInventory;
        this.rideableDragonInventory = new SimpleContainer(newSize);
        if (old != null) {
            old.removeListener(this);
            int copy = Math.min(old.getContainerSize(), this.rideableDragonInventory.getContainerSize());
            for (int i = 0; i < copy; i++) {
                ItemStack stack = old.getItem(i);
                if (!stack.isEmpty()) {
                    this.rideableDragonInventory.setItem(i, stack.copy());
                }
            }
        }
        this.rideableDragonInventory.addListener(this);
        updateRideableDragonContainerEquipment();
    }

    private void updateRideableDragonContainerEquipment() {
        if (!level.isClientSide) {
            boolean hasSaddle = !rideableDragonInventory.getItem(SADDLE_SLOT).isEmpty();
            ItemStack armor = rideableDragonInventory.getItem(ARMOR_SLOT);
            setSaddled(hasSaddle);
            setArmor(armor);
        }
    }

    @Override
    public void containerChanged(Container container) {
        if (container == rideableDragonInventory) {
            boolean wasSaddled = isSaddled();
            updateRideableDragonContainerEquipment();
            if (tickCount > 20 && !wasSaddled && isSaddled()) {
                playSound(SoundEvents.HORSE_SADDLE, 0.5F, 1.0F);
            }
        }
    }

    public SimpleContainer getRideableDragonInventory() {
        return rideableDragonInventory;
    }

    /**
     * Ensures the drake inventory has the correct size for the given chested state.
     * Called from the container when opening on the client: entity sync can set
     * CHESTED
     * without calling setChested(), so the client may have CHESTED=true but still
     * a 2-slot inventory. We force entity data and createRideableDragonInventory() so the
     * size matches.
     */
    public void ensureInventorySizeForMenu(boolean chested) {
        int current = getRideableDragonInventory().getContainerSize();
        int expected = chested ? INV_BASE_SIZE + INV_CHEST_SLOTS : INV_BASE_SIZE;
        if (current != expected) {
            setChested(chested);
            createRideableDragonInventory();
        }
    }

    /** Donkey-like: 5 columns for chest slots. */
    public int getInventoryColumns() {
        return 5;
    }

    /**
     * @param container to add things to
     */
    @Override
    public void applyTomeInfo(NewTarragonTomeContainer container){
        container.addSaddleSlot();
        if (canEquipChest()){
            container.addChestSlot();
        }
        super.applyTomeInfo(container);
    }

}