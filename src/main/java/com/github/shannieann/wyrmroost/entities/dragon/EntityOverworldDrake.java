package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.widgets.CollapsibleWidget;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.containers.util.DynamicSlot;
import com.github.shannieann.wyrmroost.entities.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.*;
import com.github.shannieann.wyrmroost.entities.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entities.dragon.ai.movement.swim.WRSwimmingLookControl;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.DragonArmorItem;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.Mafs;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;

import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;
/* Just gonna use sniffity's method of organizing cuz its good - koala
// TODO:
    Tidy up class, remove unnecessary methods, make sure methods follow a logical order (probs best to do this after we organize BFL, follow the same format)



*/

public class EntityOverworldDrake extends WRDragonEntity
{
    /*
    private static final EntitySerializer<EntityOverworldDrake> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender));


     */
    // inventory slot constants
    public static final int SADDLE_SLOT = 0;
    public static final int ARMOR_SLOT = 1;
    public static final int CHEST_SLOT = 2;

    // Dragon Entity Data
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityOverworldDrake.class, EntityDataSerializers.BOOLEAN);

    // Dragon Entity Animations
    // NOT USED ANYMORE -- Keeping for reference but will prolly delete later
    //public static final Animation GRAZE_ANIMATION = LogicalAnimation.create(35, EntityOverworldDrake::grazeAnimation, () -> OverworldDrakeModel::grazeAnimation);
    //public static final Animation HORN_ATTACK_ANIMATION = LogicalAnimation.create(15, EntityOverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::hornAttackAnimation);
    //public static final Animation ROAR_ANIMATION = LogicalAnimation.create(86, EntityOverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::roarAnimation);
    //public static final Animation[] ANIMATIONS = new Animation[]{GRAZE_ANIMATION, HORN_ATTACK_ANIMATION, ROAR_ANIMATION};

    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public LivingEntity thrownPassenger;

    public EntityOverworldDrake(EntityType<? extends EntityOverworldDrake> drake, Level level)
    {
        super(drake, level);
    }

    // ====================================
    //      A) Entity Data
    // ====================================

    /*
    @Override
    public EntitySerializer<EntityOverworldDrake> getSerializer()
    {
        return SERIALIZER;
    }


     */
    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(SADDLED, false);
    }

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
    @Override
    public float getScale()
    {
        return getAgeScale(0.275f);
    }

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
            thrownPassenger.setDeltaMovement(Mafs.nextDouble(getRandom()), 0.1 + getRandom().nextDouble(), Mafs.nextDouble(getRandom()));
            ((ServerChunkCache) level.getChunkSource()).broadcastAndSend(thrownPassenger, new ClientboundSetEntityMotionPacket(thrownPassenger)); // notify client
            thrownPassenger = null;
        }

        //if (!level.isClientSide && getTarget() == null && !isInSittingPose() && !getSleeping() && level.getBlockState(blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK && getRandom().nextDouble() < (isBaby() || getHealth() < getMaxHealth()? 0.005 : 0.001));
        //AnimationPacket.send(this, GRAZE_ANIMATION); TODO READD
    }
    // ====================================
    //      B.1) Tick and AI: Attack and Hurt
    // ====================================
    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        LivingEntity prev = getTarget();

        super.setTarget(target);

        boolean flag = getTarget() != null;
        setSprinting(flag);

        /*if (flag && prev != target && target.getType() == EntityType.PLAYER && !isTame() && noAnimations())
            AnimationPacket.send(this, EntityOverworldDrake.ROAR_ANIMATION);*/ // TODO ADD
    }
    // ====================================
    //      C) Navigation and Control
    // ====================================

    @Override
    public float getTravelSpeed()
    {
        float speed = (float) getAttributeValue(MOVEMENT_SPEED);
        if (canBeControlledByRider()) speed += 0.45f;
        return speed;
    }
    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event)
    {
        if (backView)
            event.getCamera().move(ClientEvents.getViewCollision(-0.5, this), 0.75, 0);
        else
            event.getCamera().move(ClientEvents.getViewCollision(-3, this), 0.3, 0);
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
        return true;
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

        if (entity instanceof LivingEntity)
        {
            LivingEntity passenger = ((LivingEntity) entity);
            if (isTame()) setSprinting(passenger.isSprinting());
            else if (!level.isClientSide && passenger instanceof Player)
            {
                double rng = getRandom().nextDouble();

                /*
                if (rng < 0.01) tame(true, (Player) passenger);
                else if (rng <= 0.1)


                {
                    setTarget(passenger);
                    boardingCooldown = 60;
                    ejectPassengers();
                    thrownPassenger = passenger; // needs to be queued for next tick otherwise some voodoo shit breaks the throwing off logic >.>
                }

                 */
            }
        }
    }

    // ====================================
    //      D) Taming
    // ====================================
    /*
    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        if (stack.getItem() == Items.SADDLE && !isSaddled() && isJuvenile())
        {
            if (!level.isClientSide)
            {
                getInventory().insertItem(SADDLE_SLOT, stack.copy(), false);
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!isTame() && isHatchling() && isFood(stack))
        {
         //   tame(getRandom().nextInt(10) == 0, player);
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.playerInteraction(player, hand, stack);
    }

     */

    // ====================================
    //      D.1) Taming: Inventory
    // ====================================
    @Override
    public DragonInventory createInv()
    {
        return new DragonInventory(this, 24);
    }
    public void tameLogic (Player tamer, ItemStack stack) {

    };
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
    public void applyTomeInfo(NewTarragonTomeContainer container) {
        container.addSaddleSlot().addArmorSlot().addChestSlot();
    }
    public boolean hasChest()
    {
        return !getStackInSlot(CHEST_SLOT).isEmpty();
    }

    public boolean isSaddled()
    {
        return entityData.get(SADDLED);
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
    @Override
    public void ate()
    {
        if (isBaby()) ageUp(60);
        if (getHealth() < getMaxHealth()) heal(4f);
    }
    // ====================================
    //      E.1) Client: Sounds
    // ====================================
    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        playSound(SoundEvents.COW_STEP, 0.3f, 1f);
        super.playStepSound(pos, blockIn);
    }


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
    // ====================================
    //      F) Goals
    // ====================================
    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        //goalSelector.addGoal(0, new WRSleepGoal(this));
        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        //goalSelector.addGoal(5, new ControlledAttackGoal(this, 1.425, true /*AnimationPacket.send(this, HORN_ATTACK_ANIMATION)*/));
        //goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        //goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new OWDGrazeGoal(this));
        //goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        //targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        //targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        //targetSelector.addGoal(3, new DefendHomeGoal(this));
        //targetSelector.addGoal(4, new HurtByTargetGoal(this));
        //targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, EntitySelector.ENTITY_STILL_ALIVE::test));
    }

    // ====================================
    //      F.n) Goals: OWDGrazeGoal
    // ====================================
    class OWDGrazeGoal extends AnimatedGoal{

        private BlockPos grazeBlockPosition;
        public OWDGrazeGoal(EntityOverworldDrake entity) {
            super(entity);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }


        @Override
        public boolean canUse() {
            //This should get a blockPos that is positioned correctly relative to the OWD's head.
            grazeBlockPosition = new BlockPos(Mafs.getYawVec(yBodyRot, 0, getBbWidth() / 2 + 1).add(position())).below();
            if (!level.getBlockState(grazeBlockPosition).is(Blocks.GRASS_BLOCK)){
                return false;
            }

            // Ok this was wolf's original if statement for graze goal...
            return (!level.isClientSide && getTarget() == null
                    && !isInSittingPose()
                    && !getSleeping()
                    && getRandom().nextDouble() < (isBaby() || getHealth() < getMaxHealth()? 0.005 : 0.001));
        }

        @Override
        public void start() {
            entity.clearAI();
            entity.setXRot(0);
            entity.getNavigation().stop();
        }

        @Override
        public void tick() {
            LookControl lookControl = entity.getLookControl();
            if (lookControl instanceof WRGroundLookControl) {
                ((WRGroundLookControl) lookControl).stopLooking();
            }
            if (lookControl instanceof WRSwimmingLookControl) {
                ((WRSwimmingLookControl) lookControl).stopLooking();
            }
            //ToDo: Flying look Control

            // At tick 71
            if (elapsedTime == 40) {
                // Eat the block underneath
                /*if (level.getBlockState(pos).is(Blocks.GRASS) && WRConfig.canGrief(level)) {
                    level.destroyBlock(pos, false);
                    ate();*/
                level.levelEvent(2001, grazeBlockPosition, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                level.setBlock(grazeBlockPosition, Blocks.DIRT.defaultBlockState(), 2);
                ate();
            }
            System.out.println(elapsedTime);
            // Play the idle anim once
            super.start("idle1", 2, 50);
            super.tick();
        }
    }




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
