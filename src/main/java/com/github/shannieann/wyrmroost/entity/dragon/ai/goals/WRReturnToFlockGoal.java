package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

// Goal to stay in a flock together when not tamed.
public class WRReturnToFlockGoal extends Goal
{
    private WRDragonEntity farthestFlockMember;
    private final WRDragonEntity dragon;
    private int timer = 0;
    private double distanceToFarthestFlockMember;
    private int chance; // Has to trigger at a really specific frequency to look natural. 5/1000 to 15/1000 chance per tick seems good.

    private boolean flyersOnly; // Only try to join flying flock members

    public WRReturnToFlockGoal(WRDragonEntity dragon, int chance, boolean flyersOnly) {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = dragon;
        this.farthestFlockMember = null;
        this.distanceToFarthestFlockMember = 0;
        this.chance = chance;
        this.flyersOnly = flyersOnly;
    }

    public WRReturnToFlockGoal(WRDragonEntity dragon, int chance) {
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.dragon = dragon;
        this.farthestFlockMember = null;
        this.distanceToFarthestFlockMember = 0;
        this.chance = chance;
        this.flyersOnly = false;
    }

    @Override
    public boolean canUse() {
        if (dragon.getRandom().nextInt(1000) > this.chance 
            || dragon.getTarget() != null || dragon.isTame() || dragon.isLeashed() || dragon.getSleeping())
        {
            return false;
        }

        for (WRDragonEntity mob : this.dragon.level.getEntitiesOfClass(this.dragon.getClass(), this.dragon.getBoundingBox().inflate(2*this.dragon.getRestrictRadius()), entity -> entity != this.dragon)) {
            if (this.farthestFlockMember == null || this.dragon.distanceToSqr(mob) > this.distanceToFarthestFlockMember
            && (! this.flyersOnly || mob.isUsingFlyingNavigator())) {
                this.distanceToFarthestFlockMember = this.dragon.distanceToSqr(mob);
                this.farthestFlockMember = mob;
            }
        }
        if (this.farthestFlockMember != null && this.distanceToFarthestFlockMember > dragon.getRestrictRadius()) {
        }
        return this.farthestFlockMember != null;
    }

    @Override
    public void start() {
        // Can get stuck in water, etc. Give it a shove into the air
        if (this.dragon.dragonCanFly() && this.dragon.canLiftOff()) {
            Vec3 direction = new Vec3(this.farthestFlockMember.getX(), this.farthestFlockMember.getY(), this.farthestFlockMember.getZ()).subtract(this.dragon.position()).normalize();
            direction = new Vec3(direction.x, 1, direction.z);
            this.dragon.setDeltaMovement(direction.scale(0.3));
        }
    }

    @Override
    public void tick() {
        this.timer++;
        dragon.getNavigation().moveTo(farthestFlockMember, 1f);
    }

    @Override
    public boolean canContinueToUse() {
        return this.timer < 400 && farthestFlockMember != null && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
    }

    @Override
    public void stop() {
        this.timer = 0;
        this.farthestFlockMember = null;
        this.dragon.getNavigation().stop();
    }

}