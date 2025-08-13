package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WRFollowOwnerGoal extends Goal
{
    private final WRDragonEntity dragon;
    private int newPathTicks = 0;
    private double startDistance;
    private LivingEntity owner;
    private final double minTeleportDist;

    public WRFollowOwnerGoal(WRDragonEntity tameableEntity, double startDistance)
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = tameableEntity;
        this.startDistance = startDistance;
        this.minTeleportDist = this.dragon.getRestrictRadius() * 2;
    }

    public WRFollowOwnerGoal(WRDragonEntity tameableEntity)
    {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = tameableEntity;
        this.startDistance = this.dragon.getRestrictRadius();
        this.minTeleportDist = this.dragon.getRestrictRadius() * 2;
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
    public void start() {
    }

    @Override
    public void stop()
    {
        this.dragon.getNavigation().stop();
        this.newPathTicks = 0;
        this.owner = null;
    }

    @Override
    public void tick()
    {
        if (this.owner == null) return;
        this.dragon.getLookControl().setLookAt(this.owner, 90, 90);

        if (++newPathTicks >= 10 || this.dragon.getNavigation().isDone()) {
            newPathTicks = 0;

            double d2 = this.dragon.distanceToSqr(this.owner);

            if (d2 > minTeleportDist && (this.owner.getRootVehicle().isOnGround() || this.dragon.dragonCanFly()) && this.dragon.tryTeleportToOwner()) {
                this.dragon.getNavigation().stop();
            } else {
                this.dragon.getNavigation().moveTo(this.owner, 1);
            }
        }
    }
}
