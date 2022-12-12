package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WRWaterLeapGoal extends AnimatedGoal {
    private final String breachStartAnimation = "breach_start";
    private final String breachFlyAnimation = "breach_fly";
    private final String breachEndAnimation = "breach_end";
    private final WRDragonEntity entity;
    private final double speedTowardsTarget;
    private final int horizontalDistance;
    private final int verticalDistance;
    private Vec3 waterTargetPosition;
    private Vec3 initialPosition;
    private boolean step1Done;
    private boolean step2Done;
    private boolean step3Done;
    private boolean stopFlag;
    private int finalTicks;

    public WRWaterLeapGoal(WRDragonEntity entity, double speedIn, int horizontalDistance, int verticalDistance) {
        super(entity);
        this.entity = entity;
        this.speedTowardsTarget = speedIn;
        this.horizontalDistance = horizontalDistance;
        this.verticalDistance = verticalDistance;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.isInSittingPose()) {
            return false;
        }
        if (entity.canBeControlledByRider()) {
            return false;
        }
        if (!entity.isUnderWater()) {
            return false;
        }
          //if (entity.getRandom().nextDouble() < 0.05) {
        //Get a random position...
        Vec3 randomPosition = (BehaviorUtils.getRandomSwimmablePos(this.entity, 32, 0));
        if (randomPosition != null) {
            //Shift the random position to the water surface, and check if we have indeed reached water...
            BlockPos waterTargetBlockPos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(randomPosition)).below();
            waterTargetPosition = new Vec3(waterTargetBlockPos.getX(),waterTargetBlockPos.getY(),waterTargetBlockPos.getZ());
            if (!(entity.level.getFluidState(waterTargetBlockPos).is(FluidTags.WATER))) {
                return false;
            }
            //Get the initial position, 20 blocks below the target position, plus a random variation on X / Z, +/- 12
            BlockPos initialBlockPos = new BlockPos(waterTargetPosition.x+entity.getRandom().nextInt(horizontalDistance*2+1)-horizontalDistance,waterTargetPosition.y-verticalDistance,waterTargetPosition.z+entity.getRandom().nextInt(horizontalDistance*2+1)-horizontalDistance);
            if (!(entity.level.getFluidState(initialBlockPos).is(FluidTags.WATER))) {
                return false;
            }
            initialPosition = new Vec3(initialBlockPos.getX(),initialBlockPos.getY(),initialBlockPos.getZ());
            return true;
        }
   // }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!step1Done && entity.getNavigation().isDone()) {
            return false;
        }
        if (!step1Done && !entity.isInWater()) {
            return false;
        }
        if (!step2Done && entity.getNavigation().isDone()) {
            return false;
        }
        if (stopFlag) {
            return false;
        }
        if (entity.canBeControlledByRider()) {
            return false;
        }
        //If it has finished all the steps and its had time to perform the return animation, stop the Goal.
        if (finalTicks > 30) {
            return false;
        }
        //If it's somehow using the landNavigator, do stop goal. Goal only makes sense for waterNavigator.
        if (!this.entity.isUsingSwimmingNavigator()) {
            return false;
        }
        //Else continue the Goal.
        return true;
    }

    @Override
    public void start() {
        entity.getLookControl().setLookAt(initialPosition.x,initialPosition.y,initialPosition.z);
        entity.getNavigation().moveTo(initialPosition.x, initialPosition.y, initialPosition.z, speedTowardsTarget);
    }

    @Override
    public void tick() {
        //Step 1: Reach startPosition, below the target position...
        //Step 2: Reach water target position and overshoot,flying out...
        //Step 3: Return to the water...
        //Step 4: Goal ending, hold for a couple of ticks to finish last animation..

        //Step 1:
        if (!step1Done) {
            //Check to see if we have reached the first position...
           if (entity.distanceToSqr(initialPosition.x, initialPosition.y, initialPosition.z) < 16.0F) {
                step1Done = true;
                entity.setBreaching(true);
               //Move to next position, at increased speed
                super.start(breachStartAnimation, 1, 10,false);
                entity.getNavigation().moveTo(waterTargetPosition.x, waterTargetPosition.y, waterTargetPosition.z, 2*speedTowardsTarget);
            } else if (entity.getNavigation().isStuck()) {
                stopFlag = true;
            }
        }

        //Step 2:
        if (step1Done) {
            //As soon as it leaves the water...
            if (!entity.isInWater()) {
                //Start fly animation, proceed to next step
                super.start(breachFlyAnimation, 1, 10, false);
                entity.getNavigation().stop();
                step2Done = true;
            } else if (entity.getNavigation().isStuck()) {
                //If navigation somehow stops and its still in the water, stop the goal..
                stopFlag = true;
            } else {
                super.start(breachStartAnimation, 1, 10, false);
            }
        }

        //Step 3:
        if (step2Done) {
            if (this.entity.isInWater()) {
                //As soon as it returns to the water...
                //Play breach end...
                super.start(breachEndAnimation, 1, 15, false);
                step3Done = true;
            } else {
                //If it has not yet reached the water, keep flying...
                super.start(breachFlyAnimation, 1, 10, false);
            }
        }

        //Step 4:
        if (step3Done) {
            //Once it has reached the water again, hold the goal for a few extra ticks to ensure animations plays fully
            finalTicks++;
        }
    }

    @Override
    public void stop() {
        step1Done = false;
        step2Done = false;
        step3Done = false;
        stopFlag = false;
        waterTargetPosition = null;
        initialPosition = null;
        finalTicks = 0;
        entity.setBreaching(false);
        entity.getNavigation().stop();
        System.out.println("GOAL STOPPED");
        super.stop();
    }
}