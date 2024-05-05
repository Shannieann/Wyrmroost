package com.github.shannieann.wyrmroost.entity.dragon;

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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EntityLesserDesertwyrm extends WRDragonEntity
{
    private int burrowTicks = 30;
    private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.defineId(EntityLesserDesertwyrm.class, EntityDataSerializers.BOOLEAN);

    @Override
    public int idleAnimationVariants(){
        return 0;
    }
    public EntityLesserDesertwyrm(EntityType<? extends EntityLesserDesertwyrm> type, Level worldIn)
    {
        super(type, worldIn);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return null;
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(BURROWED, Boolean.FALSE);
        super.defineSynchedData();
    }

    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("burrowed", this.getBurrowed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if (compound.contains("burrowed")) {
            this.setBurrowed(compound.getBoolean("burrowed"));
        }
    }

    @Override
    public float ageProgressAmount() {
        return 0;
    }

    @Override
    public float initialBabyScale() {
        return 0;
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
                .add(MOVEMENT_SPEED, 0.4)
                .add(ATTACK_DAMAGE, 4);
    }

    @Override
    protected void registerGoals()
    //TODO: Animate all goals
    {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new WRBurrowGoal());
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, LivingEntity.class, 6f, 0.8d, 1.2d,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        //IF the LDW is burrowed, and the block below it is not of SAND material...
        if (getBurrowed()) {
            //Unburrow and attack above
            if (level.getBlockState(blockPosition().below(1)).getMaterial() != Material.SAND) setBurrowed(false);
            attackAbove();
        }
    }

    private void attackAbove()
    {
        //Filter the entities we can attack, excluding LDWs
        Predicate<Entity> predicateFilter = filter -> {
            if (filter instanceof EntityLesserDesertwyrm) {
                return false;

            }
            //LDW can attack fishing books and small entities
            return filter instanceof FishingHook || (filter instanceof LivingEntity && filter.getBbWidth() < 0.9f && filter.getBbHeight() < 0.9f);
        };

        //create an axis-algigned bounding box and expand it upwards
        AABB boundingBox = getBoundingBox().expandTowards(0, 2, 0).inflate(0.5, 0, 0.5);
        //Check for the entities above with our previous filter...
        List<Entity> entities = level.getEntities(this, boundingBox, predicateFilter);
        //If no attackble entities are found, do nothing
        if (entities.isEmpty()) {
            return;
        }
        //If some entities are found, get the closest entity
        Optional<Entity> closest = entities.stream().min(Comparator.comparingDouble(entity -> entity.distanceTo(this)));
        Entity entity = closest.get();
        //If the entity is a fishing hook, unburrow....
        if (entity instanceof FishingHook) {
            entity.discard();
            this.setDeltaMovement(0, 0.8, 0);
            this.setBurrowed(false);
            //Hiss if a fishing rod removes it from sand
            //TODO: Link to Goal
//            setAnimationState(3);

        }
        //If the entity is a living entity, attack it...
        else {
            //TODO: Link to Goal
//            setAnimationState(2);
            doHurtTarget(entity);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (player.getItemInHand(hand).isEmpty()) {
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
        return super.mobInteract(player, hand);
    }
    public InteractionResult tameLogic (Player tamer, ItemStack stack) {
        return InteractionResult.PASS;
    };
    //Attracted to sand
    /*


    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world)
    {
        if (level.getBlockState(pos).getMaterial() == Material.SAND) return 10f;
        return super.getWalkTargetValue(pos, level);
    }
 */




    //Remove LDWs during the  if far away
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return !level.isDay();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return super.isInvulnerableTo(source) || source == DamageSource.IN_WALL;
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

    @Override
    public boolean isPushable()
    {
        return !getBurrowed();
    }

    @Override
    public boolean isPickable()
    {
        return !getBurrowed();
    }

    @Override
    protected void doPush(Entity entityIn)
    {
        if (!getBurrowed()) {
            super.doPush(entityIn);
        }
    }

    @Override
    public boolean isImmobile()
    {
        return super.isImmobile() || getBurrowed();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(ForgeSpawnEggItem.fromEntityType(getType()));
    }

    public boolean getBurrowed()
    {
        return entityData.get(BURROWED);
    }

    public void setBurrowed(boolean burrow)
    {
        entityData.set(BURROWED, burrow);
    }


    /*
       @Override
    public int getAnimationTick()
    {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick)
    {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation()
    {
        return animation;
    }

    @Override
    public Animation[] getAnimations()
    {
        return new Animation[] {BITE_ANIMATION};
    }

    @Override
    public void setAnimation(Animation animation)
    {
        this.animation = animation;
        setAnimationTick(0);
    }
     */
    //Burrow Goal:
    //TODO: Extend Animated Goal
    class WRBurrowGoal extends Goal
    {
        public WRBurrowGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override //Burrow if it is not burrowed and has sand below
        public boolean canUse()
        {
            return !getBurrowed() && this.belowIsSand();
        }

        @Override
        //continue trying to burrow so long as below is sand, we are burrowed and we still have burrow time remaining
        public boolean canContinueToUse()
        {
            return belowIsSand() && (getBurrowed()) || burrowTicks > 0;

        }

        @Override
        public void tick()
        {
            if (burrowTicks > 0 && --burrowTicks == 0) {
                setBurrowed(true);
//                setAnimationState(1);
            }
        }

        @Override
        public void stop()
        {
            burrowTicks = 30;
            setBurrowed(false);
        }

        private boolean belowIsSand()
        {
            return level.getBlockState(blockPosition().below(1)).is(BlockTags.SAND);
        }
    }
}
