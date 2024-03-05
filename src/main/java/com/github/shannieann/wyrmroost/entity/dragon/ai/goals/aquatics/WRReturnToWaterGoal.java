package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;


public class WRReturnToWaterGoal extends Goal {
    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int radius;
    protected int verticalDistance;

    public WRReturnToWaterGoal(WRDragonEntity entity, double speedIn, int radius, int verticalDistance) {
        this.entity = entity;
        this.speed = speedIn;
        this.radius = radius;
        this.verticalDistance = verticalDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        if (this.entity.canBeControlledByRider()) {
            return false;
        }

        if (this.entity.isUsingLandNavigator()){
            Vec3 targetPosition = this.getPosition();
            if (targetPosition == null) {
                return false;
            } else {
                this.x = targetPosition.x;
                this.y = targetPosition.y;
                this.z = targetPosition.z;
                return true;
            }
        }
        return false;
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 targetVec =  BehaviorUtils.getRandomSwimmablePos(this.entity, radius, verticalDistance);
        if (targetVec != null) {
            BlockPos.MutableBlockPos targetPosMutable = new BlockPos.MutableBlockPos(targetVec.x,targetVec.y,targetVec.z);
            //Check to see found position is at least 3 blocks deep in water
            for (int i = 0; i <4; i++) {
                targetPosMutable = targetPosMutable.below(i).mutable();
                if (!this.entity.level.getFluidState(targetPosMutable).is(FluidTags.WATER)) {
                    return null;
                }
            }
            return targetVec;
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getNavigation().isDone()){
            return false;
        }
        if (this.entity.canBeControlledByRider()) {
            return false;
        }
        if (this.entity.level.getFluidState(this.entity.blockPosition().below()).is(FluidTags.WATER) && this.entity.level.getFluidState(this.entity.blockPosition()).is(FluidTags.WATER)) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.x,this.y,this.z,this.speed);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }
}

