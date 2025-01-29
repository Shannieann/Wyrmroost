package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class WRRandomSwimmingGoal extends Goal {
    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int radius;
    protected int verticalDistance;

    public WRRandomSwimmingGoal(WRDragonEntity entity, double speedIn, int radius, int verticalDistance) {
        this.entity = entity;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.radius = radius;
        this.verticalDistance = verticalDistance;
    }

    @Override
    public boolean canUse()
    {
        if (this.entity.canBeControlledByRider())
            return false;

        if (this.entity.getTarget() != null)
            return false;

        if (!this.entity.isUsingSwimmingNavigator())
            return false;

        if (this.entity.getSleeping())
            return false;

        else {
            Vec3 targetPosition = this.getPosition();
            if (targetPosition == null)
                return false;
             else {
                this.x = targetPosition.x;
                this.y = targetPosition.y;
                this.z = targetPosition.z;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition()
    {
        Vec3 targetVec =  BehaviorUtils.getRandomSwimmablePos(this.entity, radius, verticalDistance);
        if (targetVec != null) {
            Vec3 entityPos = this.entity.position();
            double deltaX = targetVec.x - entityPos.x;
            double deltaZ = targetVec.z - entityPos.z;

            if ((Math.sqrt(deltaX*deltaX+deltaZ*deltaZ)) < 12) {
                return null;
            }
            return (targetVec);
        }
        return null;
    }

    @Override
    public boolean canContinueToUse()
    {
        //If it's close enough to assume it has reached the Goal
        if (this.entity.distanceToSqr(this.x,this.y,this.z) < 100) {
            return false;
        }
        if (!this.entity.isUsingSwimmingNavigator()) {
               return false;
        }
        if (this.entity.getTarget() != null){
            return false;
        }
        if (this.entity.canBeControlledByRider()){
            return false;
        }
        return !this.entity.getNavigation().isDone();
    }
    @Override
    public void start()
    {
        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }

    @Override
    public void stop()
    {
        this.entity.getNavigation().stop();
    }
}