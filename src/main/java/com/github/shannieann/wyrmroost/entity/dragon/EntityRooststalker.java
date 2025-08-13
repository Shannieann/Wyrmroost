package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.ITameable;
import com.github.shannieann.wyrmroost.item.DragonEggItem;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundMoveControl;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH;
public class EntityRooststalker extends WRDragonEntity implements ITameable, IBreedable {

    public static final int ITEM_SLOT = 0;
    public static final int MAX_BREEDING_COOLDOWN = 6000; // 300 seconds, override
    public static final int MAX_SCAVENGING_COOLDOWN = 1200; // 60 seconds

    public static final EntityDataAccessor<Integer> SCAVENGING_COOLDOWN = SynchedEntityData.defineId(EntityRooststalker.class, EntityDataSerializers.INT);

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
        entityData.define(SCAVENGING_COOLDOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("ScavengingCooldown",getScavengingCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setScavengingCooldown(nbt.getInt("ScavengingCooldown"));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
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

    public int getScavengingCooldown() {
        return entityData.get(SCAVENGING_COOLDOWN);

    }
    public void setScavengingCooldown(int cooldown) {
        entityData.set(SCAVENGING_COOLDOWN, cooldown);
    }

    public boolean defendsHome() {
        return true;
    }

    @Override
    public float getRestrictRadius() {
        int radiusRoot = WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonAttributesConfig.homeRadius.get();
        return radiusRoot * radiusRoot;
    }

    // ====================================
    //      B) Tick and AI
    // ====================================
    @Override
    public void aiStep() {
        super.aiStep();

        if (!level.isClientSide) {
            ItemStack item = getHeldItem();
            double temp = getRandom().nextDouble();
            if ((isFood(item) || isDrink(item))&& getHealth() < getMaxHealth() && temp<= 0.03d) { // 0.03d = eats food in 1-5 seconds
                eat(this.level, item);
                setHeldItem(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void tick() {
        setScavengingCooldown(Math.max(getScavengingCooldown()-1,0));
        super.tick();
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
    @Override
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {

        if (tamer.level.isClientSide) {
            return InteractionResult.CONSUME;
        }

        //Taming
        // The reason we don't use isFood here is that roost stalkers eat meat as food. Eggs are just the taming food.
        // Other mod eggs will work here, too.
        // (Not sure if this is a good idea but why not?)
        if (!isTame() && stack.is(Tags.Items.EGGS)) {
            // TODO: make sure you can't tame them with rooststalker eggs
            if (stack.getItem() instanceof DragonEggItem && ((DragonEggItem)stack.getItem()).getName(stack).getString() == "Rooststalker") {
                // this isn't working
                System.out.println("[DEBUG] No rooststalker eggs allowed!!");
                return InteractionResult.PASS;
            }
            eat(tamer.getLevel(), stack);
            float tameChance = (tamer.isCreative() || this.isHatchling()) ? 1.0f : 0.25f;
            boolean tamed = attemptTame(tameChance, tamer);
            if (tamed) {
                getAttribute(MAX_HEALTH).setBaseValue(20);
                heal((float)getAttribute(MAX_HEALTH).getBaseValue());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================

    public boolean canEquipHeldItem() {
        return true;
    }
/* Unused?
    @Nullable
    @Override
    public Predicate<ItemStack> canEquipSpecialItem() {
        return (stack) -> true;
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }
*/
    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean hasGender() {
        return false;
    }

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
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonBreedingConfig.hatchTime.get()*20;
    }

    @Override
    public int getBreedingLimit() {
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonBreedingConfig.breedLimit.get();
    }

    @Override
    public int getMaxBreedingCooldown() {
        return WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.dragonBreedingConfig.maxBreedingCooldown.get();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.GOLD_NUGGET;
    }

    @Override
    @SuppressWarnings({ "ConstantConditions", "null" })
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getFoodProperties(this) != null && stack.getFoodProperties(this).isMeat();
    }

    private boolean isDrink(ItemStack stack) {
        if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof MilkBucketItem) {
            return true;
        }
        return false;
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
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new WRRunWhenLosingGoal(this, 0.2f, 8, 1.5f));
        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Player.class, 7f, 1.15f, 1f) {
            @Override
            public boolean canUse() {
                return !isTame() && !getHeldItem().isEmpty() && super.canUse();
            }
        });
        goalSelector.addGoal(6, new WRDragonBreedGoal<>(this));
        goalSelector.addGoal(7, new WRMoveToHomeGoal(this));
        goalSelector.addGoal(8, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(9, new RSScavengeGoal(this));
        goalSelector.addGoal(10, new RSGetDroppedFoodGoal(this));
        goalSelector.addGoal(11, new WRSleepGoal(this, true));
        goalSelector.addGoal(12, new WRSitGoal(this));
        goalSelector.addGoal(13, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(14, new LookAtPlayerGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(15, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]) {
            @Override
            protected double getFollowDistance() {
                return (double) getRestrictRadius();
            }
        }.setAlertOthers(new Class[0]));
        targetSelector.addGoal(4, new WRDefendHomeGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof Chicken || target instanceof Rabbit || target instanceof Turtle));
    }

    /*
     * TL;DR expected behavior: Rooststalker will navigate to chest, position self at center in front of chest,
     * and play the scavenge animation. It tries to steal the least common item in the chest. If an item was successfully stolen,
     * it runs away. If it spends more than 30 seconds trying to get to the chest, is tamed halfway, is attacked halfway,
     * or is a passenger, it will give up. After it gives up or succeeds, it will not attempt to scavenge again for 1 minute.
     */
    class RSScavengeGoal extends AnimatedGoal {
        // store chest info
        private Container container;
        private ChestBlockEntity chestEntity;
        private Vec3 openPos;
        private BlockPos chestPos;
        private Direction facingDirection;
        // logic control vars
        private boolean animationStarted = false;
        private boolean abortGoal = false;
        private int stealFromSlot = -1;
        // cooldown vars
        private int navTime = 0;
        private static final int giveUpTime = 600; // give up if can't reach chest in 30 seconds
        private int pathRecalcCooldown = 0;
        // path vars
        private Path currentPath;
        private static final int PATH_RECALCULATION_INTERVAL = 20; // Recalculate path once per second

        public RSScavengeGoal(EntityRooststalker rooststalker) {
            super(rooststalker);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (getScavengingCooldown() <= 0 && !isTame() && !hasHeldItem() && !isLeashed() && !isRiding() && !isVehicle() && !isInWater()) {
                BlockPos foundChestPos = findNearestNonEmptyChest(blockPosition(), 12, 5);

                if (this.facingDirection != null && foundChestPos != null && foundChestPos != BlockPos.ZERO && this.openPos != null) {
                    this.chestPos = foundChestPos;
                    return true;
                }
            }
            setScavengingCooldown(MAX_SCAVENGING_COOLDOWN); // only attempt scavenge every minute
            return false;
        }

        // Utility for canUse()
        public BlockPos findNearestNonEmptyChest(BlockPos mobPos, int searchRadius, int heightRange) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int dy = -heightRange; dy <= heightRange; dy++) {
                for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                    for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                        mutablePos.setWithOffset(mobPos, dx, dy, dz);
                        if (isWithinRestriction(mutablePos) && level.getBlockEntity(mutablePos) instanceof ChestBlockEntity) {

                            // skip empty chests
                            this.container = getInventoryAtPosition(mutablePos);
                            if (this.container == null || this.container.isEmpty()) {
                                continue;
                            }

                            BlockState blockState = level.getBlockState(mutablePos);
                            Direction facing = blockState.getValue(ChestBlock.FACING);
                            ChestType type = blockState.getValue(ChestBlock.TYPE);

                            BlockPos offsetPos = mutablePos.relative(facing, 1); // only used to check if block has air ,not for navigation
                            if (level.getBlockState(offsetPos).isAir()) {
                                this.facingDirection = facing;

                                double offsetX = 0.0;
                                double offsetZ = 0.0;

                                if (type == ChestType.LEFT) {
                                    // move to right side of left half of double chest
                                    if (facing == Direction.NORTH) offsetX += 0.5;
                                    else if (facing == Direction.SOUTH) offsetX += -0.5;
                                    else if (facing == Direction.EAST) offsetZ += -0.5;
                                    else if (facing == Direction.WEST) offsetZ += 0.5;
                                } else if (type == ChestType.RIGHT) {
                                    // move to left side of right half of double chest
                                    if (facing == Direction.NORTH) offsetX += -0.5;
                                    else if (facing == Direction.SOUTH) offsetX += 0.5;
                                    else if (facing == Direction.EAST) offsetZ += 0.5;
                                    else if (facing == Direction.WEST) offsetZ += -0.5;
                                }

                                this.openPos = Vec3.atCenterOf(offsetPos).add(offsetX, -0.5, offsetZ); // y should be ground level
                                return mutablePos.immutable();
                            }
                        }
                    }
                }
            }
            return BlockPos.ZERO;
        }

        public void start() {

            if (getMoveControl() instanceof WRGroundMoveControl) {
                System.out.println("[DEBUG] WR MOVE CONTROL");
            }

            this.currentPath = getNavigation().createPath(this.openPos.x, this.openPos.y, this.openPos.z, 1);
            if (this.currentPath != null) {
                this.pathRecalcCooldown = PATH_RECALCULATION_INTERVAL;
                getNavigation().moveTo(this.currentPath, 1.0F);
            } else {
                getNavigation().moveTo(this.openPos.x, this.openPos.y, this.openPos.z, 1.0F);
            }
        }

        @Override
        public void tick() {

            // FIRST PRIORITY: If animation is playing, check if it should continue
            if (this.animationStarted) {
                if (canContinueToUse()) {
                    super.tick();
                }
            }

            // SECOND PRIORITY: If close enough to chest, start animation
            else if (this.openPos != null && this.openPos.distanceToSqr(position()) < 0.09) {
                // Start animation if close enough to chest
                // non-squared distance <0.3
                // Stop goal if chest is now bad
                this.container = getInventoryAtPosition(this.chestPos);
                if (!(level.getBlockEntity(this.chestPos) instanceof ChestBlockEntity)
                    || !(level.getBlockState(this.chestPos.relative(this.facingDirection, 1)).isAir())
                    || this.container == null
                    || this.container.isEmpty())
                {
                    this.abortGoal = true;
                    stop();
                    return;
                }

                // Start animation otherwise
                this.animationStarted = true;
                getNavigation().stop();
                setDeltaMovement(Vec3.ZERO);
                setYRot(this.facingDirection.getOpposite().toYRot());
                super.start("scavenge", 2, 20);

                if (this.container instanceof ChestBlockEntity chest && chest.openersCounter.getOpenerCount() == 0) {
                    this.chestEntity = chest;
                    interactChest( true);
                } else if (this.container instanceof CompoundContainer) {
                    this.chestEntity = (ChestBlockEntity) level.getBlockEntity(this.chestPos);
                    if (this.chestEntity.openersCounter.getOpenerCount() == 0) {
                        interactChest(true);
                    }
                }
            }

            // THIRD PRIORITY: If not navigating (typically because too close to chest), shove it closer. pathfinding sucks
            else if (this.openPos != null && (! getNavigation().isInProgress())) {
                getNavigation().moveTo(this.openPos.x, this.openPos.y, this.openPos.z, 1.0F);
                setYRot(this.facingDirection.getOpposite().toYRot());
                Vec3 direction = this.openPos.subtract(position()).normalize();
                direction = new Vec3(direction.x, 0, direction.z); // no vertical push so it doesn't get stuck climbing
                setDeltaMovement(direction.scale(0.1));
            }

            // FOURTH PRIORITY: If not close enough to chest, move to chest
            else {
                // Caching path is more efficient, but path doesn't really work at <5 blocks distance
                // Also path creation fails a lot for some reason
                // This is how vanilla MoveToBlockGoal.class does it. Not sure why it casts so much.
                // this.mob.getNavigation().moveTo((double)((float)this.blockPos.getX()) + 0.5, (double)(this.blockPos.getY() + 1), (double)((float)this.blockPos.getZ()) + 0.5, this.speedModifier);

                // PRIORITY 4.1: If far away with no valid path, try to recalculate path
                if (this.openPos.distanceToSqr(position()) >= 25
                    && (this.pathRecalcCooldown <= 0 || this.currentPath == null || this.currentPath.isDone()))  // Try to recalc path
                {
                    this.currentPath = getNavigation().createPath(this.openPos.x, this.openPos.y, this.openPos.z, 1);
                    if (this.currentPath != null) {
                        this.pathRecalcCooldown = PATH_RECALCULATION_INTERVAL;
                        getNavigation().moveTo(this.currentPath, 1.0F);
                    }
                }
                // PRIORITY 4.2: If far away with valid path, use cached path
                else if (this.openPos.distanceToSqr(position()) >= 25 && this.pathRecalcCooldown > 0 && this.currentPath != null && !this.currentPath.isDone()) {
                    this.pathRecalcCooldown--;
                }

                // PRIORITY 4.3: If 4.1 or 4.2 failed, or if close to chest, use moveTo
                if (this.openPos.distanceToSqr(position()) < 25 || this.currentPath == null || this.currentPath.isDone()) { // Path failed or too close, use moveTo
                    this.currentPath = null;
                    this.pathRecalcCooldown--;
                    getNavigation().moveTo(this.openPos.x, this.openPos.y, this.openPos.z, 1.0F);
                }
                this.navTime++;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.abortGoal
                    && ((this.animationStarted && super.canContinueToUse()) || (! this.animationStarted && this.navTime < giveUpTime))
                    && !isTame()
                    && !isRiding()
                    && getTarget() == null // getting attacked halfway should interrupt goal
                    && !isImmobile();
        }

        @Nullable
        // utility for tick()
        public Container getInventoryAtPosition(BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (state.hasBlockEntity()) {
                BlockEntity tile = level.getBlockEntity(pos);
                if (tile instanceof ChestBlockEntity && block instanceof ChestBlock chestBlock) {
                    return ChestBlock.getContainer(chestBlock, state, level, pos, true);
                }
            }
            return null;
        }

        // utility for tick()
        private void interactChest(boolean open) {

            this.chestEntity.openersCounter.openCount = open ? 1 : 0;
            SoundEvent sound = open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE;

            if (this.container instanceof CompoundContainer) {
                // For double chests, derive which half the entity is opening from facing and type
                BlockState state = level.getBlockState(chestPos);
                ChestType type = state.getValue(ChestBlock.TYPE);
                Direction facing = state.getValue(ChestBlock.FACING);
                handleDoubleChestOpenClose(open);
                // play sound. method in ChestBlockEntity is private, so we have to do it this way
                level.playSound((Player)null, this.chestPos.getX(), this.chestPos.getY(), this.chestPos.getZ(), sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }
            else if (this.container instanceof ChestBlockEntity) {
                // For single chests, use the original simple approach with blockEvent
                this.chestEntity.getLevel().blockEvent(this.chestPos, this.chestEntity.getBlockState().getBlock(), 1, this.chestEntity.openersCounter.getOpenerCount());
                // play sound. method in ChestBlockEntity is private, so we have to do it this way
                level.playSound((Player)null, this.chestPos.getX(), this.chestPos.getY(), this.chestPos.getZ(), sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            }

            // Handle item removal only when opening
            if (open && !this.container.isEmpty()) {

                java.util.List<Integer> nonEmptySlots = new java.util.ArrayList<>();
                for (int i = 0; i < this.container.getContainerSize(); i++) {
                    if (!this.container.getItem(i).isEmpty()) {
                        nonEmptySlots.add(i);
                    }
                }

                if (!nonEmptySlots.isEmpty()) {

                    // Sort by rarest item (smallest stack size) first
                    Collections.sort(nonEmptySlots, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer stackSize1, Integer stackSize2) {
                            return Integer.compare(stackSize1, stackSize2);
                        }
                    });
                    Collections.sort(nonEmptySlots, (slot1, slot2) ->
                        ((Integer)this.container.getItem(slot1).getCount()).compareTo((Integer)this.container.getItem(slot2).getCount()));

                    this.stealFromSlot = nonEmptySlots.get(0);
                    // Only set held item item after animation is done so it doesn't display weird
                }
            }
        }

        // Helper method to handle chest opening/closing for double chests
        private void handleDoubleChestOpenClose(boolean open) {

            Level lvl = this.chestEntity.getLevel();
            BlockState baseState = lvl.getBlockState(this.chestPos);

            ChestType type = baseState.getValue(ChestBlock.TYPE);
            Direction facing = baseState.getValue(ChestBlock.FACING);

            // For double chests, use a more reliable approach with multiple block events
            // This ensures both halves are properly synchronized
            // Send block events to both halves with a slight delay to ensure proper
            // animation
            lvl.blockEvent(this.chestPos, baseState.getBlock(), 1, open ? 1 : 0);

            // Figure out the offset for the other half of the chest
            Direction otherDir;
            otherDir = (type == ChestType.LEFT) ? facing.getClockWise() : facing.getCounterClockWise();

            BlockPos otherHalfPos = this.chestPos.relative(otherDir);
            BlockEntity otherHalfEntity = lvl.getBlockEntity(otherHalfPos);
            BlockState otherHalfState = lvl.getBlockState(otherHalfPos);

            if (otherHalfEntity instanceof ChestBlockEntity && otherHalfState.getBlock() instanceof ChestBlock) {
                // Send block event to the other half
                lvl.blockEvent(otherHalfPos, otherHalfState.getBlock(), 1, open ? 1 : 0);

                // For double chests, also try to trigger a block update to ensure the animation plays
                if (open) {
                    // Mark both blocks for update to ensure the chest animation renders properly
                    lvl.sendBlockUpdated(this.chestPos, baseState, baseState, 3);
                    lvl.sendBlockUpdated(otherHalfPos, otherHalfState, otherHalfState, 3);
                }
            }
        }

        @Override
        public void stop() {
            if (this.container != null && this.chestEntity != null && this.chestEntity.openersCounter.getOpenerCount() > 0) {
                interactChest(false);
            }

            if (this.stealFromSlot != -1 && !this.abortGoal) {
                ItemStack singleItem = this.container.removeItem(this.stealFromSlot, 1);
                if (!singleItem.isEmpty()) {
                    setHeldItem(singleItem); // current held item should always be empty
                }
                runAwayFromChest();
            }

            // cleanup
            this.container = null;
            this.openPos = null;
            this.chestPos = null;
            this.chestEntity = null;
            this.facingDirection = null;
            this.animationStarted = false;
            this.abortGoal = false;
            this.stealFromSlot = -1;
            this.navTime = 0;
            this.pathRecalcCooldown = 0;
            this.currentPath = null;
            setScavengingCooldown(MAX_SCAVENGING_COOLDOWN);
            super.stop();
        }

        // utility for stop()
        private void runAwayFromChest() {
            int distance = 12 + (int) (getRandom().nextDouble() * 5); // run 12-16 blocks away (cast to int rounds down)
            Vec3 posAway = DefaultRandomPos.getPosAway(this.entity, distance, 2, this.openPos);
            if (posAway == null) { // Widen search
                posAway = DefaultRandomPos.getPosAway(this.entity, distance, 7, this.openPos);
            }
            if (posAway != null) {
                getNavigation().moveTo(this.entity.getNavigation().createPath(posAway.x, posAway.y, posAway.z, 0), 1.5d); // run away like it doesn't want to get caught
            }
        }

    }

    // Small extension so rooststalkers pick up food instead of immediately eating it
    // they'll eat it very soon if not at full health
    class RSGetDroppedFoodGoal extends WRGetDroppedFoodGoal {
        public RSGetDroppedFoodGoal(EntityRooststalker dragon) {
            super(dragon, 12, false);
        }

        @Override
        public boolean canUse() {
            return getHeldItem().isEmpty() && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            if (super.readyForOtherAction) {
                if (super.targetItemStack != null && super.targetItemStack.isAlive()) {
                    ItemStack itemStackOneItem = super.splitTargetItemStack();
                    setHeldItem(itemStackOneItem); // Current held item should always be empty
                    super.oneItem.discard();
                    stop();
                    return;
                }
            }
        }
    }

}