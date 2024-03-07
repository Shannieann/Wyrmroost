package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.world.entity.ai.behavior.BehaviorUtils;


public class WRReturnToWaterGoal extends Goal {
    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected int lowerY;
    protected int higherY;

    protected final double speed;
    protected int radius;

    public WRReturnToWaterGoal(WRDragonEntity entity, double speedIn, int radius, int lowerY, int higherY) {
        this.entity = entity;
        this.speed = speedIn;
        this.radius = radius;
        //lowerY represents how many blocks below it the entity will search for water
        this.lowerY = lowerY;
        //higherY represents how many blocks above it the entity will search for water
        this.higherY = higherY;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        if (this.entity.getNavigation().isInProgress()){
            return false;
        }
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

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getNavigation().isInProgress()){
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

    @Nullable
    protected Vec3 getPosition() {
        Vec3 targetVec =  getWaterPosition(this.entity, radius, lowerY, higherY);
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

    @Nullable
    public Vec3 getWaterPosition(WRDragonEntity entity, int radius, int lowerY, int higherY) {
        BlockPos randomPos = randomPosition(entity.getRandom(), radius, lowerY, higherY);
        for(int i = 0; i++ < 10; ) {
            if (randomPos != null) {
                boolean flag = entity.level.getBlockState(randomPos).isPathfindable(entity.level, new BlockPos(randomPos), PathComputationType.WATER);
                if (flag) {
                    return new Vec3(randomPos.getX(),randomPos.getY(),randomPos.getZ());
                }

            }
        }
        return null;
    }

    public BlockPos randomPosition(Random pRandom, int pHorizontalDistance, int lowerY, int higherY) {
        int i = pRandom.nextInt(2 * pHorizontalDistance + 1) - pHorizontalDistance;
        int j = ((int)entity.getY()-lowerY)+(pRandom.nextInt( lowerY+higherY));
        int k = pRandom.nextInt(2 * pHorizontalDistance + 1) - pHorizontalDistance;
        return new BlockPos(i, j, k);
    }

}

