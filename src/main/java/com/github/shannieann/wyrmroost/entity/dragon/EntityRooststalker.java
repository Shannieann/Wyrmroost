package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.ai.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDragonBreedGoal;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
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
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class EntityRooststalker extends WRDragonEntity implements IBreedable {

    public static final int ITEM_SLOT = 0;
    private static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityRooststalker.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> SCAVENGING = SynchedEntityData.defineId(EntityRooststalker.class, EntityDataSerializers.BOOLEAN);


    public EntityRooststalker(EntityType<? extends EntityRooststalker> stalker, Level level) {
        super(stalker, level);
    }


    // ====================================
    //      Animations
    // ====================================
    @Override
    public int idleAnimationVariants() {
        return 0;
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================
    public static AttributeSupplier.Builder getAttributeSupplier() {
        return (Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonAttributesConfig.maxHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.285D)
                .add(Attributes.ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonAttributesConfig.maxHealth.get()));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(HELD_ITEM, ItemStack.EMPTY);
        entityData.define(SCAVENGING, false);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }


    public boolean isScavenging() {
        return entityData.get(SCAVENGING);
    }

    public void setScavenging(boolean b) {
        entityData.set(SCAVENGING, b);
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    @Override
    public float ageProgressAmount() {
        return 0;
    }

    @Override
    public float initialBabyScale() {
        return 0;
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================

    /**
     * A few things to note for Rooststalker here:
     * - We can't separate the pattern and the color because the pattern changes depending on the color.
     * - The digit in the tens place decides color
     * - 0 is the default red-brown color
     * - 1 is green
     * - 2 is black
     * - 3 is blue
     * - 4 is albino (rare version)
     * - The digit in the ones place decides the pattern
     * - 0 is patternless
     * - 1 is spider/ribcage
     * - 2 is socks
     * - 3 is diamond
     * - 4 is striped
     * - 5 is reverse-striped (rare)
     * - Koala
     */
    @Override
    public int determineVariant() {
        // Rare chance for albino. Otherwise, a random choice of the other 4 colors.
        // Since it is the digit in the tens place, we multiply by 10.
        int color = (getRandom().nextDouble() < 0.005) ? 40 : getRandom().nextInt(0, 4) * 10;
        // Rare chance for reverse-striped
        // Otherwise a random pattern
        int pattern = (getRandom().nextDouble() < 0.005) ? 5 : getRandom().nextInt(0, 5);

        return color + pattern;
    }

    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

    // ====================================
    //      B) Tick and AI
    // ====================================
    @Override
    public void aiStep() {
        super.aiStep();

        //TODO: Could this logic be extracted to the super class?
        sleepTimer.add(getSleeping() ? 0.08f : -0.15f);

        if (!level.isClientSide) {
            ItemStack item = getStackInSlot(ITEM_SLOT);
            if (isFood(item) && getHealth() < getMaxHealth() && getRandom().nextDouble() <= 0.0075)
                eat(this.level, item);
        }
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
    public float getStepHeight() {
        return 0;
    }

    @Override
    public boolean speciesCanFly() {
        return false;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean dragonCanFly() {
        return false;
    }

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return false;
    }

    @Override
    // Override normal dragon body controller to allow rotations while sitting: its small enough for it, why not. :P
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this);
    }


    // ====================================
    //      D) Taming
    // ====================================
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {
        if (tamer.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        //Taming
        // The reason we don't use isFood here is that roost stalkers eat meat as food. Eggs are just the taming food.
        // Other mod eggs will work here, too.
        // (Not sure if this is a good idea but why not?)
        if (!isTame() && stack.is(Tags.Items.EGGS)) {
            eat(tamer.getLevel(), stack);
            float tameChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.25f;
            if (attemptTame(tameChance, tamer, stack)) {
                getAttribute(MAX_HEALTH).setBaseValue(20);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================
    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {
        if (slot == ITEM_SLOT) setHeldItem(stack);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? getHeldItem() : super.getItemBySlot(slot);
    }

    public ItemStack getHeldItem() {
        return entityData.get(HELD_ITEM);
    }

    private boolean hasItem() {
        return getHeldItem() != ItemStack.EMPTY;
    }

    public void setHeldItem(ItemStack item) {
        entityData.set(HELD_ITEM, item);
        if (!item.isEmpty()) playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 0.5f, 1);
    }

    @Nullable
    @Override
    public Predicate<ItemStack> canEquipSpecialItem() {
        return (stack) -> true;
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.GOLD_NUGGET;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getFoodProperties(this).isMeat();
    }

    // ====================================
    //      E) Client
    // ====================================
    @Override
    public void doSpecialEffects() {
        if (getVariant() == -1 && tickCount % 25 == 0) {
            double x = getX() + (WRMathsUtility.nextDouble(getRandom()) * 0.7d);
            double y = getY() + (getRandom().nextDouble() * 0.5d);
            double z = getZ() + (WRMathsUtility.nextDouble(getRandom()) * 0.7d);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case -1 -> new Vec2(1, 0);
            default -> new Vec2(0, 0);
        };
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_STALKER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_STALKER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_STALKER_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 0.8f;
    }


    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new MoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new WRDragonBreedGoal(this));
        goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Player.class, 7f, 1.15f, 1f) {
            @Override
            public boolean canUse() {
                return !isTame() && !getHeldItem().isEmpty() && super.canUse();
            }
        });

        goalSelector.addGoal(9, new RSScavengeGoal(1.1d));
        goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        goalSelector.addGoal(1, new WRRunWhenLosingGoal(this, 0.2f, 1.0f, 16.0f, 1.5f, 1.5f));
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Chicken || target instanceof Rabbit || target instanceof Turtle));
    }

    @Override
    public InteractionResult breedLogic(Player tamer, ItemStack stack) {
        return null;
    }

    @Override
    public int hatchTime() {
        return 300;
    }


    class RSScavengeGoal extends MoveToBlockGoal {
        private Container chest;
        private int searchDelay = 20 + getRandom().nextInt(40) + 5;

        public RSScavengeGoal(double speed) {
            super(EntityRooststalker.this, speed, 16);
        }


        @Override
        public boolean canUse() {
            if (!isTame() && !hasItem() && super.canUse()) {
                chest = getInventoryAtPosition();
                if (chest != null && !chest.isEmpty()) {
                    return true;

                }
            }
            return false;
        }

        public void start() {

        }

        @Override
        public boolean canContinueToUse() {
            return !hasItem() && chest != null && super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();

            if (isReachedTarget()) {
                if (hasItem()) return;
                //TODO: MISSING ANIMATION
                //setAnimation("scavenging");
                setScavenging(true);

                if (chest == null) return;
                if (chest instanceof ChestBlockEntity && ((ChestBlockEntity) chest).openersCounter.getOpenerCount() == 0)
                    interactChest(chest, true);
                if (!chest.isEmpty() && --searchDelay <= 0) {
                    int index = getRandom().nextInt(chest.getContainerSize());
                    ItemStack stack = chest.getItem(index);

                    if (!stack.isEmpty()) {
                        stack = chest.removeItemNoUpdate(index);
                        getInventory().insertItem(ITEM_SLOT, stack, false);
                    }
                }
            }
        }


        @Override
        public void stop() {
            super.stop();
            interactChest(chest, false);
            searchDelay = 20 + getRandom().nextInt(40) + 5;
            setScavenging(false);
        }

        /**
         * Returns the IInventory (if applicable) of the TileEntity at the specified position
         */
        @Nullable
        public Container getInventoryAtPosition() {
            Container inv = null;
            BlockState blockstate = level.getBlockState(blockPos);
            Block block = blockstate.getBlock();
            if (blockstate.hasBlockEntity()) {
                BlockEntity tileentity = level.getBlockEntity(blockPos);
                if (tileentity instanceof Container) {
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
        protected boolean isValidTarget(LevelReader world, BlockPos pos) {
            return level.getBlockEntity(pos) instanceof Container;
        }

        /**
         * Used to handle the chest opening animation when being used by the scavenger
         */
        private void interactChest(Container inventory, boolean open) {
            if (!(inventory instanceof ChestBlockEntity chest)) return; // not a chest, ignore it

            chest.openersCounter.openCount = open ? 1 : 0;
            chest.getLevel().blockEvent(chest.getBlockPos(), chest.getBlockState().getBlock(), 1, chest.openersCounter.getOpenerCount());
        }
    }

}