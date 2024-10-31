package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entity.dragon.ai.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;

import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
/*
// TODO:
    Tidy up class, remove unnecessary methods, make sure methods follow a logical order (probs best to do this after we organize BFL, follow the same format)
    WRReturnToLandGoal (should not be specific to OWDs) which makes land creatures find their way back to land...
        For this one, we should try and use the GroundNavigator if possible...

*/

public class EntityOverworldDrake extends WRDragonEntity implements IBreedable
{
    /*
    private static final EntitySerializer<EntityOverworldDrake> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender));
     */
    // inventory slot constants
    public static final int SADDLE_SLOT = 0;
    public static final int ARMOR_SLOT = 1;
    public static final int CHEST_SLOT = 2;

    @Override
    public int idleAnimationVariants(){
        return 3;
    }
    // Dragon Entity Data
    // Dragon Entity Animations
    // NOT USED ANYMORE -- Keeping for reference but will prolly delete later
    //public static final Animation GRAZE_ANIMATION = LogicalAnimation.create(35, EntityOverworldDrake::grazeAnimation, () -> OverworldDrakeModel::grazeAnimation);
    //public static final Animation HORN_ATTACK_ANIMATION = LogicalAnimation.create(15, EntityOverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::hornAttackAnimation);
    //public static final Animation ROAR_ANIMATION = LogicalAnimation.create(86, EntityOverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::roarAnimation);
    //public static final Animation[] ANIMATIONS = new Animation[]{GRAZE_ANIMATION, HORN_ATTACK_ANIMATION, ROAR_ANIMATION};

    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public LivingEntity thrownPassenger;
    public boolean shouldRoar = false, shouldBuck = false;

    // TODO this is clientside I believe, as all movement is. So we could pretty easily do a momentum bar at the bottom of the screen.
    public float momentum = 0.0f;

    public EntityOverworldDrake(EntityType<? extends EntityOverworldDrake> drake, Level level)
    {
        super(drake, level);
    }

    // =====================
    //      Animation Logic
    // =====================

    @Override
    public <E extends IAnimatable> PlayState predicateRiding(AnimationEvent<E> event) {
        if (getDeltaMovement().length() >= 0.1f){
            if (getDeltaMovement().y <= -0.1f) return PlayState.CONTINUE; // If we're falling we want to just continue what animation we were playing before (removes stuttering when going down hills)
            if (riderIsSprinting || getDeltaMovement().length() >= 0.25f) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk_fast", ILoopType.EDefaultLoopTypes.LOOP));
            }
            else
                event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        return super.predicateBasicLocomotion(event);
    }

    // Chest handling
    // Create a new predicate b/c this should be able to run even when other animations are running as well
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
    //      A) Entity Data
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 70)
                .add(MOVEMENT_SPEED, 0.2125)
                .add(KNOCKBACK_RESISTANCE, 0.75)
                .add(FOLLOW_RANGE, 20)
                .add(ATTACK_KNOCKBACK, 2.85)
                .add(ATTACK_DAMAGE, 8);
    }
    // ====================================
    //      A.4) Entity Data: HOME
    // ====================================

    @Override
    public boolean defendsHome() {
        return true;
    }

    // ====================================
    //      A.6) Entity Data: VARIANT
    // ====================================

    @Override
    public int determineVariant()
    {
        if (getRandom().nextDouble() < 0.008) return -1;

        if (Biome.getBiomeCategory(level.getBiome(blockPosition())) == Biome.BiomeCategory.SAVANNA) return 1;
        return 0;
    }

    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================
    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || getSleeping()) size = size.scale(1, 0.75f);
        return size;
    }
    /*
    @Override
    public float getScale()
    {
        return getAgeScale(0.275f);
    }

     */

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

        sitTimer.add((isInSittingPose() || getSleeping())? 0.1f : -0.1f);
        sleepTimer.add(getSleeping()? 0.04f : -0.06f);


        // =====================
        //       Throwing Passenger Off Logic
        // =====================
        if (thrownPassenger != null)
        {
            thrownPassenger.setDeltaMovement(WRMathsUtility.nextDouble(getRandom()), 0.1 + getRandom().nextDouble(), WRMathsUtility.nextDouble(getRandom()));
            ((ServerChunkCache) level.getChunkSource()).broadcastAndSend(thrownPassenger, new ClientboundSetEntityMotionPacket(thrownPassenger)); // notify client
            thrownPassenger = null;
        }
    }

    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================
    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        LivingEntity prev = getTarget();

        super.setTarget(target);

        boolean targetExists = getTarget() != null;
        setSprinting(targetExists);

        // TODO maybe an isAnimating() method in superclass?
        if (targetExists && prev != target && target.getType() == EntityType.PLAYER && !isTame() && getAnimation().equals("base"))
            shouldRoar = true;
    }
    // ====================================
    //      C) Navigation and Control
    // ====================================

    Float oldSpeed;
    boolean riderIsSprinting = false;
    @Override
    public void travel(Vec3 vec3d) {
        if (isControlledByLocalInstance()){

            if (!this.isAlive()) {
                return;
            }

            LivingEntity rider = (LivingEntity) this.getControllingPassenger();
            //Store previous yaw value
            this.yRotO = this.getYRot();
            //Client (rendering): Align body to entity direction
            this.yBodyRot = this.getYRot();
            //Client (rendering): Align head to body
            this.yHeadRot = this.yBodyRot;
            //This should allow for strafing
            float sideMotion = 0;
            //This allows for moving forward
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


            riderIsSprinting = ClientEvents.getClient().options.keySprint.isDown(); // Set if we're sprinting

            float speed = lerpSpeed(getTravelSpeed()) * 0.8f; // Trying out a lower riding speed for OWDs (to try to give horses some spotlight too)
            // Handle momentum

            //System.out.println(momentum);
            if (riderIsSprinting){
                if (momentum <= 0.12f) momentum += 0.001f;
            }
            else if (momentum > 0.0f) momentum -= 0.005f;



            if (forwardMotion < 0.0F) { // Huh? Ig I'll keep it here because it works
                forwardMotion *= 0.25F; // Ohhh its like if you're going backward you're slower I guess.
            }
            if (this.isControlledByLocalInstance()){

                handleGroundRiding(speed, sideMotion, forwardMotion, vec3d, rider);
            }
        }

    }

    // Smooth transition between speeds. Maybe extract to superclass if needed?

    private float lerpSpeed(float newSpeed){
        float toReturn;

        if (oldSpeed == null) toReturn = newSpeed;
        else toReturn = Mth.lerp(0.02f, oldSpeed, newSpeed);

        if (toReturn != 0.0f) oldSpeed = toReturn;

        return toReturn;
    }
    @Override
    public float getTravelSpeed()
    {
        float speed = (float) getAttributeValue(MOVEMENT_SPEED);
        //if (canBeControlledByRider()) speed += 0.15f;
        return speed + momentum;
    }

    @Override
    public float ageProgressAmount() {
        return 0;
    }

    @Override
    public float initialBabyScale() {
        return 0;
    }

    @Override
    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView)
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.75, 0);
        else
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.3, 0);
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }




    // ====================================
    //      C.1) Navigation and Control: Flying
    // ====================================

    @Override
    public boolean speciesCanFly() {
        return false;
    }

    // ====================================
    //      C.2) Navigation and Control: Swimming
    // ====================================


    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    // ====================================
    //      C.3) Navigation and Control: Riding
    // ====================================
    @Override
    protected boolean canAddPassenger(Entity entity)
    {
        return isSaddled() && isJuvenile() && (isOwnedBy((LivingEntity) entity) || (!isTame() && boardingCooldown <= 0));
    }
    @Override
    public void positionRider(Entity entity)
    {
        super.positionRider(entity);

        if (entity instanceof LivingEntity passenger)
        {
            if (isTame()) setSprinting(passenger.isSprinting());
            else if (!level.isClientSide && passenger instanceof Player player)
            {
                double rng = getRandom().nextDouble();


                if (rng < 0.005) {
                    tame(player);
                    level.broadcastEntityEvent(this, (byte) 7); // heart particles
                }

                else if (rng <= 0.1)
                {
                    shouldBuck = true;
                }
            }
        }
    }

    // ====================================
    //      D) Taming
    // ====================================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {

        if (!isJuvenile())
            super.mobInteract(player, hand);

        ItemStack stack = player.getItemInHand(hand);

        if (canAddPassenger(player)){
            player.startRiding(this);
        }
        else if (stack.getItem() == Items.SADDLE) {
            {
            if (!level.isClientSide) {
                getInventory().insertItem(SADDLE_SLOT, stack.copy(), false);
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }


        //if (!isTame() && isHatchling() && isFood(stack))
        //{
            //   tame(getRandom().nextInt(10) == 0, player);
        //    stack.shrink(1);
        //    return InteractionResult.sidedSuccess(level.isClientSide);
        //}

        return super.mobInteract(player, hand);
    }

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================
    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 24);
    }
    // Does not apply here because we don't feed anything to tame it.
    public InteractionResult tameLogic(Player tamer, ItemStack stack) {
        return InteractionResult.PASS;
    }
    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad)
    {
        boolean playSound = !stack.isEmpty() && !onLoad;
        switch (slot)
        {
            case SADDLE_SLOT:
                entityData.set(SADDLED, !stack.isEmpty());
                if (playSound) playSound(SoundEvents.HORSE_SADDLE, 1f, 1f);
                break;
            case ARMOR_SLOT:
                setArmor(stack);
                if (playSound) playSound(SoundEvents.ARMOR_EQUIP_DIAMOND, 1f, 1f);
                break;
            case CHEST_SLOT:
                entityData.set(CHESTED, !stack.isEmpty());
                if (playSound) playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1f, 1f);
                break;
        }
    }
    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case -1 -> new Vec2(1,3);
            default -> new Vec2(0,3);
        };
    }

    @Override
    public boolean canEquipArmor() {
        return true;
    }

    @Override
    public boolean canEquipSaddle() {
        return true;
    }

    @Override
    public boolean canEquipChest() {
        return true;
    }

    @Override
    public void dropStorage()
    {
        DragonInventory inv = getInventory();
        for (int i = CHEST_SLOT + 1; i < inv.getSlots(); i++)
            spawnAtLocation(inv.extractItem(i, 65, false), getBbHeight() / 2f);
    }
    // ====================================
    //      D.2) Taming: Breeding and Food
    // ====================================

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.is(Tags.Items.CROPS_WHEAT);
    }

    // ====================================
    //      E.1) Client: Sounds
    // ====================================



    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_OWDRAKE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_OWDRAKE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_OWDRAKE_DEATH.get();
    }

    @Override
    protected <E extends WRDragonEntity> void locomotionSoundEvent(SoundKeyframeEvent<E> event, LocalPlayer player, String anim) {
        super.locomotionSoundEvent(event, player, anim);
        if ("buck".equals(anim)){
            player.clientLevel.playLocalSound(event.getEntity().getOnPos(), WRSounds.ENTITY_OWDRAKE_HURT.get(), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
        } else if ("walk".equals(anim) || "walk_fast".equals(anim)){
            player.clientLevel.playLocalSound(event.getEntity().getOnPos(), SoundEvents.COW_STEP, SoundSource.NEUTRAL, 0.3f, 1.0f, false);
        }
    }

    // ====================================
    //      E.2) Client: Camera
    // ====================================


    @Override
    public float getMountCameraYOffset() {
        return -3.5f;
    }

    // ====================================
    //      F) Goals
    // ====================================
    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(0, new WRSleepGoal(this));
        goalSelector.addGoal(1, new OWDBuckGoal(this));
        goalSelector.addGoal(2, new OWDRoarGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        goalSelector.addGoal(5, new ControlledAttackGoal(this, 1.425, true /*AnimationPacket.send(this, HORN_ATTACK_ANIMATION)*/));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        //goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new OWDGrazeGoal(this));
        goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, EntitySelector.ENTITY_STILL_ALIVE::test));
    }

    @Override
    public InteractionResult breedLogic(Player tamer, ItemStack stack) {
        return null;
    }

    @Override
    public int hatchTime() {
        return 500;
    }


    // ====================================
    //      F.n) Goals: OWDGrazeGoal
    // ====================================

    class OWDGrazeGoal extends AnimatedGoal{

        private BlockPos grazeBlockPosition;
        public OWDGrazeGoal(EntityOverworldDrake entity)
        {
            super(entity);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            //This should get a blockPos that is positioned correctly relative to the OWD's head.
            grazeBlockPosition = new BlockPos(WRMathsUtility.rotateXZVectorByYawAngle(yBodyRot, 0, getBbWidth() / 2 + 1).add(position())).below();
            if (!level.getBlockState(grazeBlockPosition).is(Blocks.GRASS_BLOCK)){
                return false;
            }
            //ToDo: Once we implement config, we should check for WRCanGrief...

            //Only graze if no target, not sitting and not sleeping
            //Graze chance is higher when baby and when health is lower than max health
            return (!level.isClientSide && getTarget() == null
                    && !getSitting()
                    && !getSleeping()
                    && getRandom().nextDouble() < ((isBaby() || getHealth() < getMaxHealth())? 0.005 : 0.001));
        }

        @Override
        public void start()
        {
            entity.clearAI();
            entity.setXRot(0);
            entity.getNavigation().stop();
        }

        @Override
        public void tick()
        {
            if (lookControl instanceof WRGroundLookControl) {
                ((WRGroundLookControl) lookControl).stopLooking();
            }
            if (elapsedTime == 40) {
                // Eat the block underneath
                level.levelEvent(2001, grazeBlockPosition, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                level.setBlock(grazeBlockPosition, Blocks.DIRT.defaultBlockState(), 2);
                eatGrass();
            }
            // Play the idle anim once
            super.start("graze", 2, 50);
            super.tick();
        }

        @Override
        public boolean canContinueToUse()
        {
            if (getSitting() || getSleeping() || isVehicle() || getTarget() != null)
                return false;
            return super.canContinueToUse();
        }

        private void eatGrass()
        {
            if (isBaby()) ageUp(60);
            if (getHealth() < getMaxHealth()) heal(4f);
        }
    }

    // ====================================
    //      F.n) Goals: OWDRoarGoal
    // ====================================

    class OWDRoarGoal extends AnimatedGoal{

        public OWDRoarGoal(EntityOverworldDrake entity)
        {
            super(entity);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            // This is set to true whenever the OWD gets a new target.
            return shouldRoar;
        }

        @Override
        public void start()
        {
            entity.clearAI();
            entity.getNavigation().stop();
            shouldRoar = false;
        }

        @Override
        public void tick()
        {
            if (lookControl instanceof WRGroundLookControl) {
                ((WRGroundLookControl) lookControl).stopLooking();
            }

            if (elapsedTime == 1){
                playSound(WRSounds.ENTITY_OWDRAKE_ROAR.get(), 3f, 1f);
            }
            else if (elapsedTime == 15) {
                applyWeakness();
            }
            // Play the idle anim once
            super.start("roar", 2, 50);
            super.tick();
        }

        @Override
        public boolean canContinueToUse()
        {
            if (getSleeping())
                return false;
            return super.canContinueToUse();
        }


        // This method is taken from the old code
        private void applyWeakness()
        {
            for (LivingEntity entity : getEntitiesNearby(15, e -> !isAlliedTo(e))) // Dont get too close now ;)
            {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
                if (distanceToSqr(entity) <= 10)
                {
                    double angle = WRMathsUtility.getAngle(getX(), getZ(), entity.getX(), entity.getZ()) * Math.PI / 180;
                    entity.push(1.2 * -Math.cos(angle), 0.4d, 1.2 * -Math.sin(angle));
                }
            }
        }
    }

    // ====================================
    //      F.n) Goals: OWDBuckGoal
    // ====================================

    class OWDBuckGoal extends AnimatedGoal{

        Player passenger;
        public OWDBuckGoal(EntityOverworldDrake entity)
        {
            super(entity);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            if (hasExactlyOnePlayerPassenger()){
                passenger = (Player) getPassengers().get(0);
                return shouldBuck;
            }
            return false;
        }

        @Override
        public void start()
        {
            entity.clearAI();
            entity.getNavigation().stop();
        }

        @Override
        public void stop() {
            super.stop();
            shouldBuck = false;
        }

        @Override
        public void tick()
        {
            if (lookControl instanceof WRGroundLookControl) {
                ((WRGroundLookControl) lookControl).stopLooking();
            }

            if (elapsedTime == 26) {
                setTarget(passenger);
                boardingCooldown = 60;
                ejectPassengers();
                thrownPassenger = passenger;
            }

            super.start("buck", 2, 50);
            super.tick();
        }

        @Override
        public boolean canContinueToUse() {
            return hasExactlyOnePlayerPassenger() && super.canContinueToUse();
        }
    }

    // ====================================
    //      F.n) Goals: OWDAttackGoal
    // ====================================

    /*@Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed)
    {
        if (key == KeybindHandler.MOUNT_KEY && pressed && noAnimations())
        {
            if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            else setAnimation(HORN_ATTACK_ANIMATION);
        }
    }*/ // TODO READD




    /*@Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);

        DragonInventory i = getInventory();
        CollapsibleWidget chestWidget = BookContainer.collapsibleWidget( 0, 174, 121, 75, CollapsibleWidget.TOP)
                .condition(this::hasChest);
        ModUtils.createContainerSlots(i, 3, 17, 12, 5, 3, DynamicSlot::new, chestWidget::addSlot);

        container.slot(BookContainer.accessorySlot(i, ARMOR_SLOT, 15, -11, 22, DragonControlScreen.ARMOR_UV).only(DragonArmorItem.class))
                .slot(BookContainer.accessorySlot(i, CHEST_SLOT, -15, -11, 22, DragonControlScreen.CHEST_UV).only(ChestBlock.class).limit(1).canTake(p -> i.isEmptyAfter(CHEST_SLOT)))
                .slot(BookContainer.accessorySlot(i, SADDLE_SLOT, 0, -15, -7, DragonControlScreen.SADDLE_UV).only(Items.SADDLE))
                .addAction(BookActions.TARGET)
                .addCollapsible(chestWidget);
    }*/


    /*@Override
    public boolean isImmobile()
    {
        return  || super.isImmobile(); TODO READD
    }*/









    // ===============================
    // OLD UNUSED ANIM STUFF -- JUST KEEPING IN CASE WE NEED FOR REFERENCE LATER
    // ===============================
        /*
    public void roarAnimation(int time)
    {
        if (time == 0) playSound(WRSounds.ENTITY_OWDRAKE_ROAR.get(), 3f, 1f, true);
        else if (time == 15)
        {
            for (LivingEntity entity : getEntitiesNearby(15, e -> !isAlliedTo(e))) // Dont get too close now ;)
            {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
                if (distanceToSqr(entity) <= 10)
                {
                    double angle = Mafs.getAngle(getX(), getZ(), entity.getX(), entity.getZ()) * Math.PI / 180;
                    entity.push(1.2 * -Math.cos(angle), 0.4d, 1.2 * -Math.sin(angle));
                }
            }
        }
    }

    public void hornAttackAnimation(int time)
    {
        if (time == 8)
        {
            LivingEntity target = getTarget();
            if (target != null) setYRot(yBodyRot = (float) Mafs.getAngle(this, target) + 90f);
            playSound(SoundEvents.IRON_GOLEM_ATTACK, 1, 0.5f, true);
            AABB box = getOffsetBox(getBbWidth()).inflate(-0.075);
            attackInBox(box);
            for (BlockPos pos : ModUtils.eachPositionIn(box))
            {
                if (level.getBlockState(pos).is(BlockTags.LEAVES))
                    level.destroyBlock(pos, false, this);
            }
        }
    }

    public void grazeAnimation(int time)
    {
        if (level.isClientSide && time == 13)
        {
            BlockPos pos = new BlockPos(Mafs.getYawVec(yBodyRot, 0, getBbWidth() / 2 + 1).add(position()));
            if (level.getBlockState(pos).is(Blocks.GRASS) && WRConfig.canGrief(level))
            {
                level.destroyBlock(pos, false);
                ate();
            }
            else if (level.getBlockState(pos = pos.below()).getBlock() == Blocks.GRASS_BLOCK)
            {
                level.levelEvent(2001, pos, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
                ate();
            }
        }
    }
*/

}
