package com.github.wolfshotz.wyrmroost.entities.dragon;

/*import com.github.wolfshotz.wyrmroost.client.model.entity.LesserDesertwyrmModel;
import com.github.wolfshotz.wyrmroost.items.LDWyrmItem;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import com.github.wolfshotz.wyrmroost.util.animation.LogicalAnimation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

/**
 * Desertwyrm Dragon Entity
 * Seperated from AbstractDragonEntity:
 * This does not need/require much from that class and would instead create redundancies. do this instead.
public class LesserDesertwyrmEntity extends Animal implements IAnimatable
{
    public static final String DATA_BURROWED = "Burrowed";
    public static final Animation BITE_ANIMATION = LogicalAnimation.create(10, null, () -> LesserDesertwyrmModel::biteAnimation);
    private static final EntityDataAccessor<Boolean> BURROWED = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<LivingEntity> AVOIDING = t -> EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(t) && !(t instanceof LesserDesertwyrmEntity);

    public Animation animation = NO_ANIMATION;
    public int animationTick;

    public LesserDesertwyrmEntity(EntityType<? extends LesserDesertwyrmEntity> minutus, Level level)
    {
        super(minutus, level);
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new BurrowGoal());
        goalSelector.addGoal(3, new AvoidEntityGoal<>(this, LivingEntity.class, 6f, 0.8d, 1.2d, AVOIDING));
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    @Override
    public ItemStack getPickedResult(HitResult target)
    {
        return new ItemStack(SpawnEggItem.byId(getType()));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(BURROWED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(DATA_BURROWED, isBurrowed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        setBurrowed(compound.getBoolean(DATA_BURROWED));
    }

    /**
     * Whether or not the Minutus is burrowed
    public boolean isBurrowed()
    {
        return entityData.get(BURROWED);
    }

    public void setBurrowed(boolean burrow)
    {
        entityData.set(BURROWED, burrow);
    }

    // ================================

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (isBurrowed())
        {
            if (level.getBlockState(blockPosition().below(1)).getMaterial() != Material.SAND) setBurrowed(false);
            attackAbove();
        }
    }

    /**
     * Called to update the entity's position/logic.
    @Override
    public void tick()
    {
        super.tick();
        updateAnimations();
    }

    private void attackAbove()
    {
        Predicate<Entity> predicateFilter = filter ->
        {
            if (filter instanceof LesserDesertwyrmEntity) return false;
            return filter instanceof FishingHook || (filter instanceof LivingEntity && filter.getBbWidth() < 0.9f && filter.getBbHeight() < 0.9f);
        };
        AABB aabb = getBoundingBox().expandTowards(0, 2, 0).inflate(0.5, 0, 0.5);
        List<Entity> entities = level.getEntities(this, aabb, predicateFilter);
        if (entities.isEmpty()) return;

        Optional<Entity> closest = entities.stream().min(Comparator.comparingDouble(entity -> entity.distanceTo(this)));
        Entity entity = closest.get();
        if (entity instanceof FishingHook)
        {
            entity.remove(RemovalReason.KILLED);
            setDeltaMovement(0, 0.8, 0);
            setBurrowed(false);
        }
        else
        {
            if (getAnimation() != BITE_ANIMATION) setAnimation(BITE_ANIMATION);
            doHurtTarget(entity);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (player.getItemInHand(hand).isEmpty())
        {
            if (!level.isClientSide)
            {
                ItemStack stack = new ItemStack(WRItems.LDWYRM.get());
                CompoundTag tag = new CompoundTag();
                CompoundTag subTag = serializeNBT();
                tag.put(LDWyrmItem.DATA_CONTENTS, subTag);
                if (hasCustomName()) stack.setHoverName(getCustomName());
                stack.setTag(tag);
                Containers.dropItemStack(level, getX(), getY(), getZ(), stack);
                remove(RemovalReason.KILLED);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) // Attracted to sand
    {
        if (level.getBlockState(pos).getMaterial() == Material.SAND) return 10f;
        return super.getWalkTargetValue(pos, level);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return !level.isDay();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob p_241840_2_)
    {
        return null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return super.isInvulnerableTo(source) || source == DamageSource.IN_WALL;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_LDWYRM_IDLE.get();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.15f;
    }

    @Override
    public boolean isPushable()
    {
        return !isBurrowed();
    }

    @Override
    public boolean isPickable()
    {
        return !isBurrowed();
    }

    @Override
    protected void doPush(Entity entityIn)
    {
        if (!isBurrowed()) super.doPush(entityIn);
    }

    @Override
    protected boolean isImmobile()
    {
        return super.isImmobile() || isBurrowed();
    }

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

    public static <F extends Mob> boolean getSpawnPlacement(EntityType<F> fEntityType, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, Random random)
    {
        if (reason == MobSpawnType.SPAWNER) return true;
        Block block = level.getBlockState(pos.below()).getBlock();
        return block == Blocks.SAND && level.getRawBrightness(pos, 0) > 8;
    }

    public static void setSpawnBiomes(BiomeLoadingEvent event)
    {
        if (event.getCategory() == Biome.BiomeCategory.DESERT)
            event.getSpawns().addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(WREntities.LESSER_DESERTWYRM.get(), 11, 1, 3));
    }

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 4)
                .add(MOVEMENT_SPEED, 0.4)
                .add(ATTACK_DAMAGE, 4);
    }

    class BurrowGoal extends Goal
    {
        private int burrowTicks = 30;

        public BurrowGoal()
        {
            setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        public boolean canUse()
        {
            return !isBurrowed() && belowIsSand();
        }

        public boolean canContinueToUse()
        {
            return belowIsSand() && (isBurrowed() || burrowTicks > 0);
        }

        @Override
        public void stop()
        {
            burrowTicks = 30;
            setBurrowed(false);
        }

        @Override
        public void tick()
        {
            if (burrowTicks > 0 && --burrowTicks == 0) setBurrowed(true);
        }

        private boolean belowIsSand()
        {
            return level.getBlockState(blockPosition().below(1)).is(BlockTags.SAND);
        }
    }
}
*/