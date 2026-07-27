package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.WRRideableDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * Dummy goal while the dragon is tame, saddled, and ridden by its owner.
 * Takes MOVE, LOOK, JUMP so other goals do not run. Stops navigation.
 */
public class WRPlayerVehicleGoal extends Goal {

    private final WRRideableDragonEntity entity;

    public WRPlayerVehicleGoal(WRRideableDragonEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    private boolean isRiddenByOwner() {
        LivingEntity rider = entity.getControllingPassenger() instanceof LivingEntity l ? l : null;
        return rider != null && entity.isOwnedBy(rider);
    }

    @Override
    public boolean canUse() {
        return entity.isTame() && entity.isSaddled() && entity.isVehicle() && isRiddenByOwner();
    }

    @Override
    public boolean canContinueToUse() {
        return entity.isTame() && entity.isSaddled() && entity.isVehicle() && isRiddenByOwner();
    }

    @Override
    public void start() {
        entity.getNavigation().stop();
        entity.setSprinting(true);
    }

    @Override
    public void stop() {
        entity.setSprinting(false);
    }
}
