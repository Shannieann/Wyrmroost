package com.github.shannieann.wyrmroost.entities.dragon;

import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.DragonBodyController;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.shannieann.wyrmroost.entities.util.EntitySerializer;
import com.github.shannieann.wyrmroost.items.book.action.BookActions;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


public class CanariWyvernEntity extends TameableDragonEntity
{
    private static final EntitySerializer<CanariWyvernEntity> SERIALIZER = TameableDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.BOOL, "Gender", TameableDragonEntity::isMale, TameableDragonEntity::setGender)
            .track(EntitySerializer.INT, "Variant", TameableDragonEntity::getVariant, TameableDragonEntity::setVariant)
            .track(EntitySerializer.BOOL, "Sleeping", TameableDragonEntity::isSleeping, TameableDragonEntity::setSleeping));


    public Player pissedOffTarget;

    public CanariWyvernEntity(EntityType<? extends TameableDragonEntity> dragon, Level level)
    {
        super(dragon, level);

    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, new MoveToHomeGoal(this));
        goalSelector.addGoal(4, new AttackGoal());
        goalSelector.addGoal(5, new ThreatenGoal());
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 8f));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(2, new DefendHomeGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected BodyRotationControl createBodyControl()
    {
        return new DragonBodyController(this);
    }

    @Override
    public EntitySerializer<CanariWyvernEntity> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(FLYING, false);
        entityData.define(GENDER, false);
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, 0);
    }



    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        InteractionResult result = super.playerInteraction(player, hand, stack);
        if (result.consumesAction()) return result;

        if (!isTame() && isFood(stack) && (isPissed() || player.isCreative() || isHatchling()))
        {
            eat(stack);
            if (!level.isClientSide) tame(getRandom().nextDouble() < 0.2, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (isOwnedBy(player) && player.getPassengers().size() < 3 && !player.isShiftKeyDown() && !isLeashed())
        {
            setOrderedToSit(true);
            setFlying(false);
            clearAI();
            startRiding(player, true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }


    @Override
    public boolean doHurtTarget(Entity entity)
    {
        if (super.doHurtTarget(entity) && entity instanceof LivingEntity)
        {
            int i = 5;
            switch (level.getDifficulty())
            {
                case HARD:
                    i = 15;
                    break;
                case NORMAL:
                    i = 8;
                    break;
                default:
                    break;
            }
            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20));
            return true;
        }
        return false;
    }


    @Override
    public boolean isInvulnerableTo(DamageSource source)
    {
        return source == DamageSource.MAGIC || super.isInvulnerableTo(source);
    }

    @Override
    public void applyStaffInfo(BookContainer container)
    {
        super.applyStaffInfo(container);
        container.addAction(BookActions.TARGET);
    }


    @Override
    public boolean shouldSleep()
    {
        return !isPissed() && super.shouldSleep();
    }

    @Override
    public boolean defendsHome()
    {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_CANARI_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_CANARI_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_CANARI_DEATH.get();
    }


    @Override
    public int determineVariant()
    {
        return getRandom().nextInt(5);
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isFlying()? 12 : 75;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem() == Items.SWEET_BERRIES;
    }


    public boolean isPissed()
    {
        return pissedOffTarget != null;
    }


    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 12)
                .add(MOVEMENT_SPEED, 0.2)
                .add(FLYING_SPEED, 0.1)
                .add(ATTACK_DAMAGE, 3);
    }

    /*@org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }*/


    public class ThreatenGoal extends Goal
    {
        public Player target;

        public ThreatenGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
        }

        @Override
        public boolean canUse()
        {
            if (isTame()) return false;
            if (isFlying()) return false;
            if (getTarget() != null) return false;
            if ((target = level.getNearestPlayer(getX(), getY(), getZ(), 12d, true)) == null)
                return false;
            return canAttack(target);
        }

        @Override
        public void tick()
        {
            double distFromTarget = distanceToSqr(target);
            if (distFromTarget > 30 && !isPissed())
            {
                if (getNavigation().isDone())
                {
                    Vec3 vec3d = DefaultRandomPos.getPosAway(CanariWyvernEntity.this, 16, 7, target.position());
                    if (vec3d != null) getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.5);
                }
            }
            else
            {
                getLookControl().setLookAt(target, 90, 90);
                if (!isPissed())
                {
                    pissedOffTarget = target;
                    clearAI();
                }

                if (distFromTarget < 6) setTarget(target);
            }
        }

        @Override
        public void stop()
        {
            target = null;
            pissedOffTarget = null;
        }
    }

    public class AttackGoal extends Goal
    {
        private int repathTimer = 10;
        private int attackDelay = 0;

        public AttackGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse()
        {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && isWithinRestriction(target.blockPosition()) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(target);
        }

        @Override
        public void tick()
        {
            LivingEntity target = getTarget();

            if ((++repathTimer >= 10 || getNavigation().isDone()) && getSensing().hasLineOfSight(target))
            {
                repathTimer = 0;
                if (!isFlying()) setFlying(true);
                getNavigation().moveTo(target.getX(), target.getBoundingBox().maxY - 2, target.getZ(), 1);
                getLookControl().setLookAt(target, 90, 90);
            }

            if (--attackDelay <= 0 && distanceToSqr(target.position().add(0, target.getBoundingBox().getYsize(), 0)) <= 2.25 + target.getBbWidth())
            {
                attackDelay = 20 + getRandom().nextInt(10);
                swing(InteractionHand.MAIN_HAND);
                //AnimationPacket.send(CanariWyvernEntity.this, ATTACK_ANIMATION);
                doHurtTarget(target);
            }
        }

        @Override
        public void stop()
        {
            repathTimer = 10;
            attackDelay = 0;
        }
    }
}