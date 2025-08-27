package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WRFollowOwnerGoal extends Goal
{
    private final WRDragonEntity dragon;
    private double startDistance;
    private LivingEntity owner;
    private final int TIME_UNTIL_TELEPORT = 200; // 10 seconds
    private final int TELEPORT_DISTANCE;
    private int time;

    public WRFollowOwnerGoal(WRDragonEntity tameableEntity, double startDistance)
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = tameableEntity;
        this.startDistance = startDistance;
        this.TELEPORT_DISTANCE = (int) (dragon.getRestrictRadius() * 2);
    }

    public WRFollowOwnerGoal(WRDragonEntity tameableEntity)
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = tameableEntity;
        this.startDistance = this.dragon.getRestrictRadius();
        this.TELEPORT_DISTANCE = (int) (dragon.getRestrictRadius() * 2);
    }

    @Override
    public boolean canUse()
    {
        this.owner = this.dragon.getOwner();
        return !(this.owner == null || this.owner.isSpectator() || this.dragon.getSitting() || this.dragon.distanceToSqr(this.owner) < (this.startDistance)
                || dragon.isLeashed() || dragon.hasRestriction());
    }

    @Override
    public boolean canContinueToUse()
    {
        return !(this.owner == null || this.owner.isSpectator() || this.dragon.getSitting() || this.dragon.distanceToSqr(this.owner) < (this.startDistance)
                        || dragon.isLeashed() || dragon.hasRestriction());
    }

    @Override
    public void stop()
    {
        this.dragon.getNavigation().stop();
        this.time = 0;
        this.owner = null;
    }

    @Override
    public void tick()
    {
        if (this.owner == null) {
            return;
        }

        this.dragon.getLookControl().setLookAt(this.owner, 90, 90);

        if (this.dragon.distanceToSqr(this.owner) > TELEPORT_DISTANCE || time > TIME_UNTIL_TELEPORT
        && (this.owner.getRootVehicle().isOnGround() || this.dragon.dragonCanFly()))
        {
            this.dragon.getNavigation().stop();
            this.dragon.tryTeleportToOwner();
        }
        else if (dragon.getNavigation().isDone()) {
            this.dragon.getNavigation().moveTo(this.owner, 1);
        }

        time++;
    }
}
