package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WRRandomLiftOffGoal extends Goal
{
    private final WRDragonEntity entity;

    public WRRandomLiftOffGoal(WRDragonEntity entity) // Should only be used for dragons that can walk but spend most of their time in the air
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        this.entity = entity;
    }

    @Override
    public boolean canUse()
    { // Make sure it's not doing anything else
        return entity.getRandom().nextDouble() < 0.01
            && entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND && entity.getNavigation().isDone() && entity.getTarget() == null
            && entity.isOnGround()
            && ! entity.getAnimationInOverride() && ! entity.isSleeping() && ! entity.getSitting() && ! entity.isLeashed()
            && ! entity.isPassenger() && ! entity.isVehicle()
            && entity.canLiftOff();
    }

    @Override
    public void start()
    {
        entity.setDeltaMovement(0, 0.2, 0);
        entity.setNavigator(WRDragonEntity.NavigationType.FLYING);
    }

    @Override
    public boolean canContinueToUse()
    {
        return (entity.getAltitude() <= entity.getFlightThreshold() || entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND)
            && entity.getTarget() == null && ! entity.isPassenger() && ! entity.isVehicle()
            && entity.canLiftOff();
    }

    public void tick()
    {
        entity.setDeltaMovement(0, 0.2, 0);
        entity.setNavigator(WRDragonEntity.NavigationType.FLYING);
    }

    @Override
    public void stop()
    {
        entity.getNavigation().stop();
    }
}
