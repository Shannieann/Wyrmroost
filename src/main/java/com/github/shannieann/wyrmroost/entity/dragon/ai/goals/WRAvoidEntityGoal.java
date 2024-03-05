package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WRAvoidEntityGoal<T extends LivingEntity> extends Goal
{
    private static final TargetingConditions AVOID_CONDITIONS = TargetingConditions.forNonCombat().range(8.0D);
    private final WRDragonEntity entity;
    private final Class<T> classToAvoid;
    private final float avoidDistance;
    private final double farSpeed;
    private final double nearSpeed;
    //private final EntityPredicate builtTargetSelector;
    private T avoidTarget;

    public WRAvoidEntityGoal(WRDragonEntity entity, Class<T> classToAvoid, float avoidDistance, double speed)
    {
        this(entity, classToAvoid, avoidDistance, speed, speed * 1.43);
    }

    public WRAvoidEntityGoal(WRDragonEntity entityIn, Class<T> avoidClass, float distance, double nearSpeedIn, double farSpeedIn) {
        this.entity = entityIn;
        this.classToAvoid = avoidClass;
        this.avoidDistance = distance;
        this.farSpeed = nearSpeedIn;
        this.nearSpeed = farSpeedIn;
        //this.builtTargetSelector = new EntityPredicate().range(distance).selector(targetPredicate);

        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (entity.isTame()) return false;
        this.avoidTarget = entity.level.getNearestEntity(classToAvoid, AVOID_CONDITIONS, entity, entity.getX(), entity.getY(), entity.getZ(), entity.getBoundingBox().inflate(avoidDistance, 3.0D, avoidDistance));
        if (avoidTarget == null) return false;
        Vec3 pos = RandomPos.generateRandomPos(entity, () -> avoidTarget.blockPosition());
        if (pos == null) return false;
        if (avoidTarget.position().distanceToSqr(pos) < avoidTarget.distanceToSqr(entity)) return false;
        return entity.getNavigation().moveTo(pos.x(), pos.y(), pos.z(), farSpeed);
    }

    @Override
    public boolean canContinueToUse()
    {
        return !entity.getNavigation().isDone();
    }


    public void tick()
    {
        if (entity.distanceToSqr(avoidTarget) < 49) entity.getNavigation().setSpeedModifier(nearSpeed);
        else entity.getNavigation().setSpeedModifier(farSpeed);
    }

    @Override
    public void stop()
    {
        avoidTarget = null;
    }
}
