package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRAvoidPlayerGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRGetDroppedFoodGoal;
import com.github.shannieann.wyrmroost.item.LDWyrmItem;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EntityLesserDesertwyrm extends WRDragonEntity {

    private static final float MOVEMENT_SPEED = 0.4f;

    public static final int BURROW_COOLDOWN_MAX = 200; // 10 seconds

    private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.defineId(EntityLesserDesertwyrm.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BURROW_ENTER_EXIT_COOLDOWN = SynchedEntityData.defineId(EntityLesserDesertwyrm.class, EntityDataSerializers.INT);

    Predicate<Entity> LDW_ATTACK_ABOVE_FILTER = filter -> {
        if (filter instanceof EntityLesserDesertwyrm) {
            return false;
        }
        // AttackAboveGoal triggers for fishing hooks and small entities
        // Exclude other LDWs
        // Include players that aren't in creative mode
        return filter instanceof FishingHook
            || (filter instanceof Player && ! ((Player) filter).getAbilities().instabuild)
            || (filter instanceof LivingEntity && filter.getBbWidth() < 0.9f && filter.getBbHeight() < 0.9f);
    };

    public EntityLesserDesertwyrm(EntityType<? extends EntityLesserDesertwyrm> type, Level worldIn)
    {
        super(type, worldIn);
    }

    // ====================================
    //      Animations
    // ====================================

    @Override
    protected AABB makeBoundingBox() {
        // When burrowed, provide a larger hitbox above the sand so fishing hooks can detect the entity
        AABB baseBox = super.makeBoundingBox();
        if (this.getBurrowed()) {
            // Create a hitbox that extends above the sand surface
            // This allows fishing hooks to collide with the entity even when it's burrowed
            return baseBox.expandTowards(0, 0.5, 0); // Extend upward by 0.5 blocks
        }
        return baseBox;
    }

    // simplify because LDW can't sit, sleep, swim, or fly
    @Override
    public <E extends IAnimatable> PlayState predicateAnimation(AnimationEvent<E> event) {

        // Dragon riding player and breaching (???) use different animations depending on the dragon. Must be handled in subclasses!

        // Every "override" animation should completely replace regular animations
        // ex: rooststalker scavenge, canari threaten, canari dance
        // UNLESS it is attack or idle, which can be played over regular animations
        if (this.isInOverrideAnimation()) {
            String currentAnim = this.getOverrideAnimation();
            // Actually set the animation on the controller so it plays
            if (!currentAnim.contains("idle") && !currentAnim.contains("attack")) {
                // Everything except idle and attack animations uses regular bones and shouldn't be layered over anything
                switch (this.getAnimationType()) {
                    case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim, ILoopType.EDefaultLoopTypes.LOOP));
                    case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim, ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim,ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
                }
                return PlayState.CONTINUE;
            }
            else {
                // Must be idle or attack animation. Choose regular bone animation as normal
            }
        }

        if (this.getBurrowed()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("base_burrowed", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.getDeltaMovement().length() > 0.05) { // lower threshold, it's slow
            if (this.isAggressive()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_fast", ILoopType.EDefaultLoopTypes.LOOP));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
            }

            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("base_ground", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    // ====================================
    //      A) Entity Data + Attributes
    // ====================================

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BURROWED, Boolean.FALSE);
        this.entityData.define(BURROW_ENTER_EXIT_COOLDOWN, 0);
        super.defineSynchedData();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("burrowed", this.getBurrowed());
        compound.putInt("burrowEnterExitCooldown", this.getburrowEnterExitCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("burrowed")) {
            this.setBurrowed(compound.getBoolean("burrowed"));
        }
    }

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType spawnType, BlockPos pos, Random random)
    {
        if (spawnType == MobSpawnType.SPAWNER) {
            return true;
        }
        Block block = serverLevelAccessor.getBlockState(pos.below()).getBlock();
        return block == Blocks.SAND && serverLevelAccessor.getRawBrightness(pos, 0) > 8;
    }

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, EntityLesserDesertwyrm.MOVEMENT_SPEED)
                .add(ATTACK_DAMAGE, 4);
    }

    public boolean getBurrowed() {
        return entityData.get(BURROWED);
    }

    public void setBurrowed(boolean burrow) {
        if (burrow) {
            this.playSound(SoundEvents.SAND_BREAK, 1F, 1F);
        }
        entityData.set(BURROWED, burrow);
    }

    public int getburrowEnterExitCooldown() {
        return entityData.get(BURROW_ENTER_EXIT_COOLDOWN);
    }

    public void setburrowEnterExitCooldown(int cooldown) {
        entityData.set(BURROW_ENTER_EXIT_COOLDOWN, cooldown);
    }

    // ====================================
    //      A.1) Entity Data: AGE
    // ====================================

    // No aging, no babies

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

    @Override
    public String getDefaultVariant() {
        return "lesser_desertwyrm";
    }

    @Override
    public String determineVariant() {
        return "lesser_desertwyrm";
    }

    // ====================================
    //      A.8) Entity Data: Miscellaneous
    // ====================================

    @Override
    public int getTier() {
        return 0; // Low tier
    }

    @Override
    public boolean isPushable()
    {
        return !getBurrowed() && super.isPushable();
    }

    @Override
    public boolean isPickable()
    {
        return !getBurrowed() && super.isPickable();
    }

    @Override
    protected void doPush(Entity entityIn)
    {
        if (!getBurrowed() && super.isPushable()) {
            super.doPush(entityIn);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    //Remove LDWs during the day if far away
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return !level.isDay();
    }

    // ====================================
    //      B) Tick and AI
    // ====================================

    @Override
    public void tick() {
        super.tick();
        setburrowEnterExitCooldown(Math.max(getburrowEnterExitCooldown() - 1,0));
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() != null) {
            EntityType<?> attackSource = source.getDirectEntity().getType();
            // This dragon doesn't have much health. Don't make fight easier by doing a lot of fishing rod damage
            if (attackSource == EntityType.FISHING_BOBBER) {
                amount = 0.1F;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        // When burrowed, immune to all damage sources except fishing hook
        return (getBurrowed() && source.getDirectEntity() != null && ! (source.getDirectEntity() instanceof FishingHook))
            || source == DamageSource.IN_WALL
            || source == DamageSource.FALLING_BLOCK
            || super.isInvulnerableTo(source);
    }

    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }
    @Override
    public float getFlyingSpeed() { // Can't fly
        return -1;
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean speciesCanFly() {
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
    public float getWalkTargetValue(BlockPos pos)
    {
        // Move to sand faster
        if (level.getBlockState(pos).getMaterial() == Material.SAND) {
            return 1.2f;
        }
        return super.getWalkTargetValue(pos, level);
    }

    // LDW is never really "in" sand, it's just animated to look like it is
    // and it can't take damage anyways so hitbox isn't an issue
    private boolean isAboveSand() {
        return level.getBlockState(this.blockPosition().below(1)).is(BlockTags.SAND);
    }

    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        // Don't allow simple pickup if burrowed or attacking anything
        if (player.getItemInHand(hand).isEmpty() && !getBurrowed() && this.getTarget() == null) {
            if (!level.isClientSide) {
                ItemStack stack = new ItemStack(WRItems.LDWYRM.get());
                CompoundTag tag = new CompoundTag();
                CompoundTag subTag = serializeNBT();
                tag.put(LDWyrmItem.DATA_CONTENTS, subTag);
                if (hasCustomName()) {
                    stack.setHoverName(getCustomName());
                }
                stack.setTag(tag);
                Containers.dropItemStack(level, getX(), getY(), getZ(), stack);
                this.discard();
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    @SuppressWarnings({ "ConstantConditions", "null" })
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getFoodProperties(this) != null && stack.getFoodProperties(this).isMeat();
    }

    // ====================================
    //      E) Client
    // ====================================

    @Override
    public Vec2 getTomeDepictionOffset() {
        return null;
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_LDWYRM_IDLE.get();
    }

    @Override
    public float getSoundVolume()
    {
        return 0.15f;
    }

    // ====================================
    //      F) Goals
    // ====================================

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new LWDAttackAboveGoal(this));
        goalSelector.addGoal(3, new LWDFindNewSpotGoal(this)); // 0.1% chance of triggering per tick
        goalSelector.addGoal(4, new LWDStayBurrowedGoal(this)); // similar to WRSitGoal, but can be interrupted by attack above or find new spot
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.1d, true) {
            @Override
            public boolean canUse() {
                return ! getBurrowed() && super.canUse();
            }
        });
        goalSelector.addGoal(6, new LWDGetDroppedFoodGoal(this, 5, true));
        goalSelector.addGoal(7, new LWDBurrowGoal(this));
        goalSelector.addGoal(8, new WRAvoidPlayerGoal(this, 6f, 0.4d, 0.4d, p -> true) {
            @Override
            public boolean canUse() {
                return !getBurrowed() && super.canUse();
            }
        });
        goalSelector.addGoal(9, new LWDFindSandGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, LivingEntity.class, false,
                target -> (target instanceof LivingEntity
                        && target.distanceTo(this) < 1.5f
                        && target.getBbWidth() < 0.9f
                        && target.getBbHeight() < 0.9f
                        && !(target instanceof EntityLesserDesertwyrm) // don't attack other LDWs
                )) {
                    @Override
                    public boolean canUse() {
                        return ! getBurrowed() && super.canUse();
                    }
                });
    }

    // TODO: When we make the greater desertwyrm, move any goals that both can use to their own files

    // =====================================================================
    //      F.1) Attack small entities while burrowed/get fished goal
    // =====================================================================
    class LWDAttackAboveGoal extends AnimatedGoal {

        private final EntityLesserDesertwyrm dragon;
        private FishingHook hook;
        private Entity target;

        public LWDAttackAboveGoal(EntityLesserDesertwyrm entity) {
            super(entity);
            dragon = (EntityLesserDesertwyrm) entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!dragon.isAboveSand() || !dragon.getBurrowed()) {
                return false;
            }

            //create an axis-aligned bounding box and expand it upwards

            // With this box, has attack radius of ~1 horizontalblock from dragon
            //AABB boundingBox = getBoundingBox().expandTowards(0, 2, 0).inflate(0.5, 0, 0.5);

            // Only attack directly above
            AABB boundingBox = getBoundingBox().expandTowards(0, 1, 0);

            //Check for the entities above with our previous filter...
            List<Entity> entities = level.getEntities(this.dragon, boundingBox, LDW_ATTACK_ABOVE_FILTER);

            if (entities.isEmpty()) {
                return false;
            }
            Optional<Entity> closest = entities.stream().min(Comparator.comparingDouble(entity -> entity.distanceTo(this.dragon)));
            Entity entity = closest.get();

            if (entity instanceof FishingHook) {
                this.hook = (FishingHook) entity;
            }
            else {
                this.target = entity;
            }
            return true;
        }

        @SuppressWarnings("null")
        public void start() {
            //If the entity is a fishing hook, unburrow, hiss, and attack player
            if (this.hook != null) {

                this.hook.discard();

                this.dragon.setBurrowed(false);
                this.dragon.setDeltaMovement(0, 0.5, 0);
                this.dragon.getLookControl().setLookAt(this.hook.getPlayerOwner(), 90, 90);

                // We don't have a hiss sound, so I layered and pitched vanilla sounds to make it sound unique
                // volume is loud because default volume for this dragon is pretty quiet
                dragon.playSound(SoundEvents.CAT_HISS, 3, 0.1F);
                dragon.playSound(SoundEvents.CAT_HISS, 2, 0.5F);
                dragon.playSound(SoundEvents.CAT_HISS, 2, 1F);
                dragon.playSound(SoundEvents.CREEPER_PRIMED, 3, 0.1F);

                super.start("hiss", 2, 20); // Hiss animation is loop in json file, but that doesn't seem right
                dragon.setburrowEnterExitCooldown(BURROW_COOLDOWN_MAX);
                dragon.setTarget(hook.getPlayerOwner()); // Have it attack while hissing
            }
            else {
                this.dragon.doHurtTarget(this.target);
                // immediately stop afterwards, no tick() or animation (for now) for attacking
            }
        }

        @Override
        public boolean canContinueToUse() {
            // Only continue if we have a fishing hook (which requires unburrowing)
            // For regular attacks, stop immediately to let LWDStayBurrowedGoal resume
            return this.hook != null && super.canContinueToUse();
        }

        @Override
        public void stop() {
            super.stop(); // Nothing happens if we call this without calling super.start() first
            this.hook = null;
            this.target = null;
        }
    }

    // =====================================================================
    //      F.2) Goal to stay burrowed (like WRSitGoal)
    // =====================================================================
    public class LWDStayBurrowedGoal extends Goal {

        private final EntityLesserDesertwyrm entity;

        public LWDStayBurrowedGoal(EntityLesserDesertwyrm entity) {
            this.entity = entity;
            // Use only MOVE flag so it can be interrupted by LWDAttackAboveGoal (which has MOVE, JUMP, LOOK)
            // This allows the attack goal to take control while keeping the burrowed state
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (! entity.getBurrowed()) {
                return false;
            }
            if (! entity.isAboveSand()) { // Also use this goal to unburrow out of sand.
                // Only happens here in edge cases, like when pushed out of sand halfway through burrowing
                // allow immediate reburrowing
                entity.setBurrowed(false);
                entity.setburrowEnterExitCooldown(0);
                return false;
            }
            return entity.getBurrowed();
        }

        @Override
        public void start() {
            entity.getNavigation().stop();
            entity.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getBurrowed() && entity.isAboveSand();
        }

        @Override
        public void stop() {
            // Only unburrow + stop animationif we're no longer in sand or burrow was set to false by something else
            // Don't unburrow just because we were interrupted by LWDAttackAboveGoal
            if (!entity.isAboveSand() || !entity.getBurrowed()) {
                entity.setBurrowed(false);
                entity.setburrowEnterExitCooldown(BURROW_COOLDOWN_MAX);
            }
        }
    }

    // =====================================================================
    //      F.3) Goal to burrow self
    // =====================================================================
    class LWDBurrowGoal extends AnimatedGoal {

        private final EntityLesserDesertwyrm dragon;

        public LWDBurrowGoal(EntityLesserDesertwyrm entity) {
            super(entity);
            dragon = (EntityLesserDesertwyrm) entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return ! dragon.getBurrowed() && dragon.isAboveSand() && dragon.getburrowEnterExitCooldown() == 0;
        }

        @Override
        public void start() {
            setDeltaMovement(Vec3.ZERO);
            dragon.getNavigation().stop();
            super.start("burrow", 3, 20);
        }

        @Override
        public void tick() {
            super.tick();
            dragon.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && dragon.getTarget() == null;
        }

        @Override
        public void stop() {
            super.stop(); // stop animation override no matter why we stopped
            if (dragon.getTarget() == null) { // only set burrowed if it didn't get hit out of animation
                dragon.setBurrowed(true);
                dragon.setburrowEnterExitCooldown(BURROW_COOLDOWN_MAX);
                // stop movement again just in case
                dragon.setDeltaMovement(Vec3.ZERO);
                dragon.getNavigation().stop();
                // base_burrowed handled by predicate locomotion
            }
        }
    }

    // =====================================================================
    //      F.4) Find New Burrow Spot Goal
    // =====================================================================
    class LWDFindNewSpotGoal extends Goal {

        private final EntityLesserDesertwyrm dragon;
        private BlockPos burrowSpot;
        private Vec3 burrowSpotVec;
        private int navTime = 0;
        private static final int giveUpTime = 200; // give up if can't reach sand in 10 seconds
        private Path currentPath;
        private static final int PATH_RECALCULATION_INTERVAL = 20; // Recalculate path once per second

        public LWDFindNewSpotGoal(EntityLesserDesertwyrm entity) {
            this.dragon = (EntityLesserDesertwyrm) entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (getRandom().nextDouble() > 0.001 // 0.1% chance of triggering per tick before checking other conditions
                || dragon.getburrowEnterExitCooldown() > 0
                || !dragon.isAboveSand()
                || !dragon.getBurrowed()) {
                return false;
            }

            BlockPos dragonPos = dragon.blockPosition();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int dx = -10; dx <= 10; dx++) {
                for (int dz = -10; dz <= 10; dz++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        mutablePos.setWithOffset(dragonPos, dx, dy, dz);
                        if (dragon.level.getBlockState(mutablePos).is(BlockTags.SAND) && dragon.level.getBlockState(mutablePos.above()).isAir()) {
                            this.burrowSpot = mutablePos.immutable();
                            this.burrowSpotVec = new Vec3(burrowSpot.getX(), burrowSpot.getY(), burrowSpot.getZ());
                            // have to set this in canUse() because LWDStayBurrowedGoal stop() is where we stop the burrowed animation override
                            // but that stop() is called before LWDFindNewSpotGoal start() is called
                            // and it needs getBurrowed() to be false to detect that we need to stop the animation
                            // we can set burrow cooldown later because that's not used until later
                            this.dragon.setBurrowed(false);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            // Don't burrow until done navigating or cooldown runs out
            dragon.setburrowEnterExitCooldown(BURROW_COOLDOWN_MAX);
            currentPath = dragon.getNavigation().createPath(burrowSpot, 1);
        }

        @Override
        public void tick() { // Keep trying movement until we're done
            navTime++;

            if (navTime % PATH_RECALCULATION_INTERVAL == 0 || currentPath == null) {
                currentPath = dragon.getNavigation().createPath(burrowSpot, 1);
            }
            if (currentPath == null || ! dragon.getNavigation().isInProgress()) {
                dragon.getNavigation().moveTo(burrowSpot.getX(), burrowSpot.getY(), burrowSpot.getZ(), 0.4D);
                currentPath = null;
            } else {
                getNavigation().moveTo(this.currentPath, 0.4F);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return navTime < giveUpTime && dragon.position().distanceTo(burrowSpotVec) > 1;
        }

        @Override
        public void stop() {
            currentPath = null;
            navTime = 0;
            burrowSpot = null;
            burrowSpotVec = null;
            dragon.setburrowEnterExitCooldown(0); // Allow reburrowing right away
        }
    }

    // =====================================================================
    //      F.5) Get Dropped Food Goal - extended to not use when burrowed
    // =====================================================================
    class LWDGetDroppedFoodGoal extends WRGetDroppedFoodGoal {
        public LWDGetDroppedFoodGoal(EntityLesserDesertwyrm dragon, int radius, boolean eatOnPickup) {
            super(dragon, radius, eatOnPickup);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !((EntityLesserDesertwyrm) dragon).getBurrowed();
        }
    }

    // ====================================
    //      F.6) Move To Sand Goal
    // ====================================
    class LWDFindSandGoal extends Goal {

        private final EntityLesserDesertwyrm dragon;
        private BlockPos sandSpot;
        private Vec3 sandSpotVec;
        private int navTime = 0;
        private static final int giveUpTime = 200; // give up if can't reach sand in 10 seconds
        private Path currentPath;
        private static final int PATH_RECALCULATION_INTERVAL = 20; // Recalculate path once per second

        public LWDFindSandGoal(EntityLesserDesertwyrm entity) {
            this.dragon = (EntityLesserDesertwyrm) entity;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (dragon.getBurrowed() || dragon.getTarget() != null) {
                return false;
            }

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int searchRadius = 0; searchRadius <= 10; searchRadius++) {
                if (findSandSpot(searchRadius, mutablePos, dragon)) {
                    this.sandSpot = mutablePos.immutable();
                    this.sandSpotVec = new Vec3(sandSpot.getX(), sandSpot.getY(), sandSpot.getZ());
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            currentPath = dragon.getNavigation().createPath(sandSpot, 1);
        }

        @Override
        public void tick() { // Keep trying movement until we're done
            navTime++;

            if (navTime % PATH_RECALCULATION_INTERVAL == 0 || currentPath == null) {
                currentPath = dragon.getNavigation().createPath(sandSpot, 1);
            }
            if (currentPath == null || ! dragon.getNavigation().isInProgress()) {
                dragon.getNavigation().moveTo(sandSpot.getX(), sandSpot.getY(), sandSpot.getZ(), 0.4D);
                currentPath = null;
            } else {
                getNavigation().moveTo(this.currentPath, 0.4F);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return navTime < giveUpTime && dragon.position().distanceTo(sandSpotVec) > 1;
        }

        @Override
        public void stop() {
            currentPath = null;
            navTime = 0;
            sandSpot = null;
            sandSpotVec = null;
        }
    }

    private boolean findSandSpot(int searchRadius, BlockPos.MutableBlockPos mutablePos, EntityLesserDesertwyrm dragon) {
        BlockPos dragonPos = dragon.blockPosition();
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            // Search only at the given radius, not closer. This lets us slowly expand search.
            if (Math.abs(dx) < searchRadius) {
                continue;
            }
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                if (Math.abs(dz) < searchRadius) {
                    continue;
                }
                for (int dy = -3; dy <= 3; dy++) {
                    BlockPos tempPos = dragonPos.offset(dx, dy, dz);
                    if (dragon.level.getBlockState(tempPos).is(BlockTags.SAND) && dragon.level.getBlockState(tempPos.above()).isAir()) {
                        mutablePos.setWithOffset(dragonPos, dx, dy, dz);
                        return true;
                    }
                }
            }
        }
        return false;
    }





/*
    // ============================================================
    //      F.x) Custom Idle Goal for burrowed vs not burrowed.
    //           Add this if idle animations are added for LDW
    // ============================================================
    class LWDIdleGoal extends WRIdleGoal {

        public LWDIdleGoal(EntityLesserDesertwyrm entity) {
            super(entity);
        }

        @Override
        public void start() {
            if (((EntityLesserDesertwyrm) entity).getBurrowed()) {
                super.start("idle_burrowed_thishasnotbeenmadeyet", 1, 40);
            } else {
                super.start("idle1_thishasalsonotbeenmade", 1, 40);
            }
        }
    }
*/
}
