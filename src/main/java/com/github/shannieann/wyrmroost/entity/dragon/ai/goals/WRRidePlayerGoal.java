package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;
// Dummy goal so we don't actually do anything while riding player
// position and rotations are handled in tick() since goal ticks are only every 0.1 seconds
// while AI ticks are every 0.05. If we handled it here, it would be laggy
// Animations handled in predicateLocomotion for each child class that can ride players
public class WRRidePlayerGoal extends Goal {

    private final WRDragonEntity entity;

    public WRRidePlayerGoal(WRDragonEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        return entity.isRidingPlayer();
    }

    @Override
    public boolean canContinueToUse() {
        return entity.isRidingPlayer();
    }

}
