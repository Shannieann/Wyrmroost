package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WRReturnToGroundIfIdleGoal extends Goal
{
    private final WRDragonEntity entity;
    private boolean abortGoal = false;

    public WRReturnToGroundIfIdleGoal(WRDragonEntity entity) // Should only be used for dragons that can fly but spend most of their time on the ground
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        this.entity = entity;
    }

    @Override
    public boolean canUse()
    { // Make sure it's not doing anything else
      // Needs small chance to trigger or it results in dragon getting stuck when stepping up on any block
        return entity.getRandom().nextDouble() < 0.01
            && entity.getNavigationType() == WRDragonEntity.NavigationType.FLYING && entity.getNavigation().isDone() && entity.getTarget() == null
            && ! entity.isInOverrideAnimation() && ! entity.isSleeping() && ! entity.getSitting() && ! entity.isLeashed()
            && ! entity.isPassenger() && ! entity.isVehicle() && ! entity.isInWaterOrBubble();
    }

    @Override
    public void start()
    {
        if (entity.getAltitude() <= entity.getFlightThreshold() + 2) {
            entity.setDeltaMovement(0, -0.05, 0);
            entity.setNavigator(WRDragonEntity.NavigationType.GROUND);
            abortGoal = true;
            return;
        }
        else {
            entity.setDeltaMovement(0, -0.2, 0);
            entity.setNavigator(WRDragonEntity.NavigationType.GROUND);

        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return ! abortGoal &&! entity.isOnGround() && entity.getTarget() == null && ! entity.isPassenger() && ! entity.isVehicle();
    }


    public void tick()
    {
        if (abortGoal) {
            stop();
            return;
        }
        entity.setDeltaMovement(0, -0.05, 0);
        entity.setNavigator(WRDragonEntity.NavigationType.GROUND);
    }

    @Override
    public void stop()
    {
        entity.getNavigation().stop();
        abortGoal = false;
    }
}
