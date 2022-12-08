package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.DefendHomeGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.network.packets.AddPassengerPacket;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class RoostStalkerEntity extends WRDragonEntity
{
    public static final int ITEM_SLOT = 0;
    //TODO: What are we using this serializer for?
    public static final EntitySerializer<RoostStalkerEntity> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.BOOL, "Sleeping", WRDragonEntity::isSleeping, WRDragonEntity::setSleeping)
            .track(EntitySerializer.INT, "Variant", WRDragonEntity::getVariant, WRDragonEntity::setVariant));
    private static final EntityDataAccessor<ItemStack> ITEM = SynchedEntityData.defineId(RoostStalkerEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> SCAVENGING = SynchedEntityData.defineId(RoostStalkerEntity.class, EntityDataSerializers.BOOLEAN);

    public RoostStalkerEntity(EntityType<? extends RoostStalkerEntity> stalker, Level level)
    {
        super(stalker, level);
        maxUpStep = 0;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, 0);
        entityData.define(ITEM, ItemStack.EMPTY);
        entityData.define(SCAVENGING, false);
    }


    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return (Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.285D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D));
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new MoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new ScavengeGoal(1.1d));
        goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Player.class, 7f, 1.15f, 1f)
        {
            @Override
            public boolean canUse()
            {
                return !isTame() && !getItem().isEmpty() && super.canUse();
            }
        });
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Chicken || target instanceof Rabbit || target instanceof Turtle));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        //TODO: Could this logic be extracted to the super class?
        sleepTimer.add(isSleeping()? 0.08f : -0.15f);

        if (!level.isClientSide)
        {
            ItemStack item = getStackInSlot(ITEM_SLOT);
            if (isFood(item) && getHealth() < getMaxHealth() && getRandom().nextDouble() <= 0.0075)
                eat(item);
        }
    }

    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        final InteractionResult success = InteractionResult.sidedSuccess(level.isClientSide);
        //TODO: Isn't this the same as stack?
        ItemStack playerItemInHand = player.getItemInHand(hand);

        //Taming
        if (!isTame() && playerItemInHand.is(Tags.Items.EGGS))
        {
            eat(stack);
            //TODO: Why are we changing the max health upon taming?
            if (tame(getRandom().nextDouble() < 0.25, player)) getAttribute(MAX_HEALTH).setBaseValue(20d);
            return success;
        }

        //Breeding
        if (isTame() && isBreedingItem(stack))
        {
            if (!level.isClientSide && canFallInLove() && getAge() == 0)
            {
                setInLove(player);
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        //Player Commands
        if (isOwnedBy(player))
        {
            //Sitting
            if (player.isShiftKeyDown())
            {
                setOrderedToSit(!isInSittingPose());
                return success;
            }
            //Riding
            if (stack.isEmpty() && playerItemInHand.isEmpty() && !isLeashed() && player.getPassengers().size() < 3)
            {
                if (!level.isClientSide && startRiding(player, true))
                {
                    //TODO: SIT ON HEAD ANIMATION?
                    setOrderedToSit(false);
                    AddPassengerPacket.send(this, player);
                }
                return success;
            }
            //Give Item (or exchange)
            //TODO: How do we take items away from Rooststalker without giving them anything in exchange?
            if ((!stack.isEmpty() && !isFood(stack)) || !playerItemInHand.isEmpty())
            {
                //TODO: Check setStackInSlot method's comments. We are not performing the checks for sidedness.
                //TODO: Perhaps perform these checks before the mob interact is ever called?
                setStackInSlot(ITEM_SLOT, stack);
                player.setItemInHand(hand, playerItemInHand);
                return success;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant() == -1 && tickCount % 25 == 0)
        {
            double x = getX() + (Mafs.nextDouble(getRandom()) * 0.7d);
            double y = getY() + (getRandom().nextDouble() * 0.5d);
            double z = getZ() + (Mafs.nextDouble(getRandom()) * 0.7d);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        if (slot == ITEM_SLOT) setItem(stack);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND? getItem() : super.getItemBySlot(slot);
    }

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), ITEM_SLOT, 0, 0, -15, DragonControlScreen.SADDLE_UV))
                .addAction(BookActions.TARGET);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == DamageSource.DROWN || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.getItem() == Items.GOLD_NUGGET;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    public int determineVariant()
    {
        return getRandom().nextDouble() < 0.005? -1 : 0;
    }


    @Override
    // Override normal dragon body controller to allow rotations while sitting: its small enough for it, why not. :P
    protected BodyRotationControl createBodyControl()
    {
        return new BodyRotationControl(this);
    }

    public ItemStack getItem()
    {
        return entityData.get(ITEM);
    }

    private boolean hasItem()
    {
        return getItem() != ItemStack.EMPTY;
    }

    public void setItem(ItemStack item)
    {
        entityData.set(ITEM, item);
        if (!item.isEmpty()) playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 0.5f, 1);
    }

    public boolean isScavenging()
    {
        return entityData.get(SCAVENGING);
    }

    public void setScavenging(boolean b)
    {
        entityData.set(SCAVENGING, b);
    }

    @Override
    public boolean canFly()
    {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_STALKER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_STALKER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_STALKER_DEATH.get();
    }

    @Override
    public float getSoundVolume()
    {
        return 0.8f;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 1);
    }

    //TODO: Safe to delete this?
    /*
    public static void setSpawnBiomes(BiomeLoadingEvent event)
    {
        Biome.BiomeCategory category = event.getCategory();
        if (category == Biome.BiomeCategory.PLAINS || category == Biome.BiomeCategory.FOREST || category == Biome.BiomeCategory.EXTREME_HILLS)
            event.getSpawns().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(WREntityTypes.ROOSTSTALKER.get(), 7, 2, 9));
    }

     */

    @Override
    public EntitySerializer<RoostStalkerEntity> getSerializer()
    {
        return SERIALIZER;
    }

    class ScavengeGoal extends MoveToBlockGoal
    {
        private Container chest;
        private int searchDelay = 20 + getRandom().nextInt(40) + 5;

        public ScavengeGoal(double speed)
        {
            super(RoostStalkerEntity.this, speed, 16);
        }

        //TODO: START?
        @Override
        public boolean canUse()
        {
            boolean flag = !isTame() && !hasItem() && super.canUse();
            if (flag) return (chest = getInventoryAtPosition()) != null && !chest.isEmpty();
            else return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return !hasItem() && chest != null && super.canContinueToUse();
        }

        @Override
        public void tick()
        {
            super.tick();

            if (isReachedTarget())
            {
                if (hasItem()) return;
                //TODO: MISSING ANIMATION
                setAnimation("scavenging");
                setScavenging(true);

                if (chest == null) return;
                if (chest instanceof ChestBlockEntity && ((ChestBlockEntity) chest).openersCounter.getOpenerCount() == 0)
                    interactChest(chest, true);
                if (!chest.isEmpty() && --searchDelay <= 0)
                {
                    int index = getRandom().nextInt(chest.getContainerSize());
                    ItemStack stack = chest.getItem(index);

                    if (!stack.isEmpty())
                    {
                        stack = chest.removeItemNoUpdate(index);
                        getInventory().insertItem(ITEM_SLOT, stack, false);
                    }
                }
            }
        }


        @Override
        public void stop()
        {
            super.stop();
            interactChest(chest, false);
            searchDelay = 20 + getRandom().nextInt(40) + 5;
            setScavenging(false);
        }

        /**
         * Returns the IInventory (if applicable) of the TileEntity at the specified position
         */
        @Nullable
        public Container getInventoryAtPosition()
        {
            Container inv = null;
            BlockState blockstate = level.getBlockState(blockPos);
            Block block = blockstate.getBlock();
            if (blockstate.hasBlockEntity())
            {
                BlockEntity tileentity = level.getBlockEntity(blockPos);
                if (tileentity instanceof Container)
                {
                    inv = (Container) tileentity;
                    if (inv instanceof ChestBlockEntity && block instanceof ChestBlock)
                        inv = ChestBlock.getContainer((ChestBlock) block, blockstate, level, blockPos, true);
                }
            }

            return inv;
        }

        /**
         * Return true to set given position as destination
         */
        @Override
        protected boolean isValidTarget(LevelReader world, BlockPos pos)
        {
            return level.getBlockEntity(pos) instanceof Container;
        }

        /**
         * Used to handle the chest opening animation when being used by the scavenger
         */
        private void interactChest(Container intentory, boolean open)
        {
            if (!(intentory instanceof ChestBlockEntity)) return; // not a chest, ignore it
            ChestBlockEntity chest = (ChestBlockEntity) intentory;

            chest.openersCounter.openCount = open? 1 : 0;
            chest.getLevel().blockEvent(chest.getBlockPos(), chest.getBlockState().getBlock(), 1, chest.openersCounter.getOpenerCount());
        }
    }
}
