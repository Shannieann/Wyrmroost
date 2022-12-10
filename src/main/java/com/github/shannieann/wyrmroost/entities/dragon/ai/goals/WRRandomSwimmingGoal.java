package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;


public class WRRandomSwimmingGoal extends Goal {

    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected boolean mustUpdate;
    protected int horizontalDistance;
    protected int verticalDistance;

    public WRRandomSwimmingGoal(WRDragonEntity entity, double speedIn, int chance, int horizontalDistance, int verticalDistance) {
        this.entity = entity;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.horizontalDistance = horizontalDistance;
        this.verticalDistance = verticalDistance;
    }

    @Override
    public boolean canUse() {
        if (this.entity.isVehicle()) {
            return false;
        }

        if (this.entity.getTarget() != null){
            return false;
        }

        if (!this.entity.isSwimming()) {
            return false;
        }

        if (!this.entity.isInWater() && !this.entity.level.getBlockState(new BlockPos(entity.position()).below()).is(Blocks.WATER)) {
            return false;
        } else {
            if (!this.mustUpdate) {
                if (this.entity.getRandom().nextInt(this.executionChance) != 0) {
                    return false;
                }
            }
            Vec3 vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.x = vector3d.x;
                this.y = vector3d.y;
                this.z = vector3d.z;
                this.mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 targetVec =  BehaviorUtils.getRandomSwimmablePos(this.entity, horizontalDistance, verticalDistance);
        if (targetVec != null) {
            Vec3 entityPos = this.entity.position();
            double distance = entityPos.subtract(targetVec).length();

            if (distance < 10) {
                return null;
            }
            return targetVec;
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.distanceToSqr(this.x,this.y,this.z) < 50) {
            return false;
        }
        if (!this.entity.isSwimming()) {
            return false;
        }
        if (this.entity.getTarget() != null){
            return false;
        }
        return !this.entity.getNavigation().isDone() && !this.entity.isVehicle();
    }
    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }
}