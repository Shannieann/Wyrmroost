package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundMoveControl;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;

public class EntityRooststalker extends WRDragonEntity implements IBreedable {

    public static final int ITEM_SLOT = 0;
    private static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityRooststalker.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> SCAVENGING = SynchedEntityData.defineId(EntityRooststalker.class, EntityDataSerializers.BOOLEAN);


    public EntityRooststalker(EntityType<? extends EntityRooststalker> rooststalker, Level level) {
        super(rooststalker, level);
        this.setNavigator(NavigationType.GROUND);
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
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonBreedingConfig.ageProgress.get()/100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.45F;
    }
    @Override
    public float baseRenderScale() {
        return 0.625f;
    }

    // ====================================
    //      A.7) Entity Data: VARIANT
    // ====================================


    @Override
    public String getDefaultVariant() {
        return "0";
    }
    @Override
    public String determineVariant() {
        // Rare chance for albino. Otherwise, a random choice of the other 4 colors.
        // Since it is the digit in the tens place, we multiply by 10.
        int color = (getRandom().nextDouble() < 0.005) ? 40 : getRandom().nextInt(0, 4) * 10;
        // Rare chance for reverse-striped
        // Otherwise a random pattern
        int pattern = (getRandom().nextDouble() < 0.005) ? 5 : getRandom().nextInt(0, 5);

        // TODO change this... im too lazy to rename the files lol
        return String.valueOf(color + pattern);
    }
    public boolean isAlbino(){
        Integer i = Integer.getInteger(getVariant());
        if (i == null) return false;
        return i >= 40;
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
        return 1;
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
            if (attemptTame(tameChance, tamer)) {
                getAttribute(MAX_HEALTH).setBaseValue(20);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public float getRestrictRadius() {
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonAttributesConfig.homeRadius.get();
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
        if (isAlbino() && tickCount % 25 == 0) {
            double x = getX() + (WRMathsUtility.nextDouble(getRandom()) * 0.7d);
            double y = getY() + (getRandom().nextDouble() * 0.5d);
            double z = getZ() + (WRMathsUtility.nextDouble(getRandom()) * 0.7d);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return isAlbino()? new Vec2(1, 0) : new Vec2(0, 0);

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
        //goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        //goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new MoveToHomeGoal(this));
        //goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        //goalSelector.addGoal(7, new WRDragonBreedGoal(this));
        /*goalSelector.addGoal(8, new AvoidEntityGoal<>(this, Player.class, 7f, 1.15f, 1f) {
            @Override
            public boolean canUse() {
                return !isTame() && !getHeldItem().isEmpty() && super.canUse();
            }
        });

         */

        goalSelector.addGoal(9, new RSScavengeGoal(this));
        //goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1));
        //goalSelector.addGoal(11, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        //goalSelector.addGoal(12, new RandomLookAroundGoal(this));
        //goalSelector.addGoal(1, new WRRunWhenLosingGoal(this, 0.2f, 1.0f, 16.0f));
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new WRDefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Chicken || target instanceof Rabbit || target instanceof Turtle));
    }

    @Override
    public InteractionResult breedLogic(Player breeder, ItemStack stack) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (((this.isOnGround() && !this.isUnderWater()) && this.isAdult()) && isBreedingItem(stack)) {
            eat(this.level, stack);
            setBreedingCooldown(6000);
            setBreedingCount(getBreedingCount()+1);
            setInLove(breeder);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    @Override
    public int hatchTime() {
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonBreedingConfig.hatchTime.get()*20;
    }

    @Override
    public int getBreedingLimit() {
        return WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.dragonBreedingConfig.breedLimit.get();
    }


    class RSScavengeGoal extends AnimatedGoal {
        //ToDo: Ensure this reaches the chest correctly for animation to play
        private Container container;
        private  BlockPos openPos;
        private BlockPos chestPos;
        private Direction facingDirection;
        private boolean animationPlaying;

        public RSScavengeGoal(EntityRooststalker rooststalker) {
            super(rooststalker);
        }


        @Override
        public boolean canUse() {
            if (!isTame() && !hasItem()) {
                BlockPos chestPos = findNearestChest(blockPosition(),12,5);
                if (chestPos != BlockPos.ZERO && chestPos != null && facingDirection !=null){
                    container = getInventoryAtPosition(chestPos);
                    if (container !=null && !container.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public BlockPos findNearestChest(BlockPos mobPos, int searchRadius, int heightRange) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int dy = -heightRange; dy <= heightRange; dy++) { // Height difference
                for (int dx = -searchRadius; dx <= searchRadius; dx++) { // X range
                    for (int dz = -searchRadius; dz <= searchRadius; dz++) { // Z range
                        mutablePos.setWithOffset(mobPos, dx, dy, dz);
                        // Check if this position is valid and if we're dealing with a chest
                        if (isWithinRestriction(mutablePos) && level.getBlockEntity(mutablePos) instanceof ChestBlockEntity) {
                            BlockState blockState = level.getBlockState(mutablePos);
                            Direction facing = blockState.getValue(ChestBlock.FACING);
                            ChestType type = blockState.getValue(ChestBlock.TYPE);
                            if (type != ChestType.SINGLE) {
                                facing = adjustFacingForDoubleChest(blockState, mutablePos, facing);
                            }
                            BlockPos blockPos = mutablePos.immutable();
                            //Offset the position, so we get the correct blockPos where the chest would open...
                            blockPos = blockPos.relative(facing,1);
                            //Check if the position is valid to walk to
                            if (level.getBlockState(blockPos).isAir()) {
                                facingDirection = facing;
                                openPos = blockPos;
                                //Return the actual chest position
                                return mutablePos.immutable();
                            }
                        }
                    }
                }
            }

            return BlockPos.ZERO;
        }

        private Direction adjustFacingForDoubleChest(BlockState blockState, BlockPos pos, Direction facing) {
            // Adjust the direction based on chest type
            ChestType type = blockState.getValue(ChestBlock.TYPE);
            if (type == ChestType.LEFT) {
                return facing.getCounterClockWise(); // Adjust for left half of double chest
            } else if (type == ChestType.RIGHT) {
                return facing.getClockWise(); // Adjust for right half of double chest
            }
            return facing; // No adjustment needed for SINGLE
        }


        public void start() {
            System.out.println("MOVING TO:"+openPos);
            if (getMoveControl() instanceof WRGroundMoveControl){
                System.out.println("WR MOVE CONTROL");
            }
            getNavigation().moveTo(openPos.getX()+0.5, openPos.getY()-1, openPos.getZ()+0.5, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            if (container !=null){
                if (animationPlaying) {
                    return super.canContinueToUse();
                }
            }
            return false;
        }

        @Override
        public void tick() {
            super.tick();
            //If animation playing, scavenging has started, let it play out, then stop
            if (animationPlaying){
                if (super.canContinueToUse()) {
                    super.tick();
                } else {
                    super.stop();
                    stop();
                }
                //Else, continue searching if chest is close
            }
            /*else if (openPos.closerToCenterThan(position(), 0.2)) {
                getNavigation().stop();
                setDeltaMovement(Vec3.ZERO);
                //Rotate entity to face chest
                setYRot(facingDirection.toYRot());
                super.start("scavenge", 2, 20);
                setScavenging(true);
                animationPlaying = true;
                if (container !=null && container instanceof ChestBlockEntity && ((ChestBlockEntity) container).openersCounter.getOpenerCount() == 0){
                    interactChest(container, true);

                }
            }

             */
        }


        @Override
        public void stop() {
            super.stop();
            interactChest(container, false);
            setScavenging(false);
            animationPlaying = false;
        }

        /**
         * Returns the IInventory (if applicable) of the TileEntity at the specified position
         */
        @Nullable
        public Container getInventoryAtPosition(BlockPos pos) {
            Container inv = null;

            BlockState blockstate = level.getBlockState(pos);
            Block block = blockstate.getBlock();
            if (blockstate.hasBlockEntity()) {
                BlockEntity tileentity = level.getBlockEntity(pos);
                if (tileentity instanceof Container) {
                    inv = (Container) tileentity;
                    if (inv instanceof ChestBlockEntity && block instanceof ChestBlock)
                        inv = ChestBlock.getContainer((ChestBlock) block, blockstate, level, pos, true);
                }
            }

            return inv;
        }


        /**
         * Used to handle the chest opening animation when being used by the scavenger
         */
        private void interactChest(Container inventory, boolean open) {
            if (!(inventory instanceof ChestBlockEntity chest)) {
                return;
            }
            chest.openersCounter.openCount = open ? 1 : 0;
            chest.getLevel().blockEvent(chest.getBlockPos(), chest.getBlockState().getBlock(), 1, chest.openersCounter.getOpenerCount());
            if (!chest.isEmpty() && open) {
                int index = getRandom().nextInt(chest.getContainerSize());
                ItemStack stack = chest.getItem(index);

                if (!stack.isEmpty()) {
                    stack = chest.removeItemNoUpdate(index);
                    getInventory().insertItem(ITEM_SLOT, stack, false);
                }
            }
        }
    }
}