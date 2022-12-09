package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class WRRandomSwimmingGoal extends Goal {

    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected boolean mustUpdate;
    private boolean checkNoActionTime;
    private final int horizontalRange;
    private final int verticalRange;


    public WRRandomSwimmingGoal(WRDragonEntity entity, double speed, int chance, int horizontalRange, int verticalRange) {
        this.entity = entity;
        this.speed = speed;
        this.executionChance = chance;
        this.checkNoActionTime = true;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isVehicle()) {
            return false;
        }
        if (entity.getTarget() != null){
            return false;
        }
        if (!entity.isSwimming()) {
            return false;
        }
        if (!entity.isInWater() && !entity.level.getFluidState(new BlockPos(entity.position()).below()).is(Fluids.WATER)) {
            return false;
        } else {
            if (!mustUpdate) {
                if (checkNoActionTime && entity.getNoActionTime() >= 100) {
                    return false;
                }
                if (entity.getRandom().nextInt(executionChance) != 0) {
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
                mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        BlockPos targetPosition;
        Vec3 speed = this.entity.getDeltaMovement();
        //If we are moving, generateRandomDirectionWithinRadians with a max turn of 90 degrees
        if (speed.length() != 0) {
            //Normalize the speed vector to get an actual direction...
            Vec3 speedUnit = speed.normalize();
            //Try and get a non-null offset.
            for (int i = 0; i < 16; i++) {
                Vec3 offset = generateRandomPositionWithinRadians(this.entity.getRandom(), 16, 8, 0, speedUnit.x, speedUnit.z, Math.PI / 2);
                if (offset != null) {
                    targetPosition = new BlockPos(this.entity.getX() + offset.x, this.entity.getY() + offset.y, this.entity.getZ() + offset.z);
                    if (this.entity.level.getFluidState(targetPosition).is(FluidTags.WATER)) {
                        //If our random position is WATER, return it directly, no need to adjust...
                        return new Vec3(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                    }

                    //If our random position is not WATER, attempt to adjust, move downwards searching for WATER...
                    for (int j = 0; j < 9; j++) {
                        targetPosition = targetPosition.below();
                        //Each time we lower it, check for WATER and AIR.
                        //If we find WATER, return and done!
                        if (this.entity.level.getFluidState(targetPosition).is(FluidTags.WATER)) {
                            return new Vec3(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                        }
                    }
                }
            }
            return null;
        }
        //We do not have a speed, attempt to generate an entirely random position...
        for (int k = 0; k < 16; k ++) {
            Vec3 offset = generateEntirelyRandomPosition(entity,entity.getRandom(),16,8);
            if (offset != null) {
                targetPosition = new BlockPos(this.entity.getX() + offset.x, this.entity.getY() + offset.y, this.entity.getZ() + offset.z);
                if (this.entity.level.getFluidState(targetPosition).is(FluidTags.WATER)) {
                    //If our random position is WATER, return it directly, no need to adjust...
                    return new Vec3(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                }

                //If our random position is not WATER, attempt to adjust, move downwards searching for WATER...
                for (int j = 0; j < 9; j++) {
                    targetPosition = targetPosition.below();
                    //Each time we lower it, check for WATER and AIR.
                    //If we find WATER, return and done!
                    if (this.entity.level.getFluidState(targetPosition).is(FluidTags.WATER)) {
                        return new Vec3(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                    }
                }
            }
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
        return !this.entity.getNavigation().isDone() && !this.entity.isVehicle();
    }
    @Override
    public void start() {
//        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
        this.entity.level.setBlock(new BlockPos(this.x,this.y,this.z), Blocks.EMERALD_BLOCK.defaultBlockState(),2);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        super.stop();
    }

    @Nullable
    public static Vec3 generateEntirelyRandomPosition(WRDragonEntity entity, Random random, int horizontalRange, int yRange) {
        return entity.position().add(random.nextInt(horizontalRange) - horizontalRange/2, random.nextInt(yRange) - yRange/2, random.nextInt(horizontalRange) - horizontalRange/2);

    }

    @Nullable
    public static Vec3 generateRandomPositionWithinRadians(Random pRandom, int pMaxHorizontalDifference, int pYRange, int pY, double pX, double pZ, double pMaxAngleDelta) {
        double initialAngle = Mth.atan2(pZ, pX) - (double)((float)Math.PI / 2F);
        double newAngle = initialAngle + (double)(2.0F * pRandom.nextFloat() - 1.0F) * pMaxAngleDelta;

        double xDistance = Math.sqrt(pRandom.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)pMaxHorizontalDifference;
        double zDistance = Math.sqrt(pRandom.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)pMaxHorizontalDifference;

        double xOffset = -xDistance * Math.sin(newAngle);
        double zOffset = zDistance * Math.cos(newAngle);
        if (!(Math.abs(xOffset) > (double)pMaxHorizontalDifference) && !(Math.abs(zOffset) > (double)pMaxHorizontalDifference)) {
            int yOffset = pRandom.nextInt(2 * pYRange + 1) - pYRange + pY;
            return new Vec3(xOffset, (double) yOffset, yOffset);
        } else {
            return null;
        }
    }
}
