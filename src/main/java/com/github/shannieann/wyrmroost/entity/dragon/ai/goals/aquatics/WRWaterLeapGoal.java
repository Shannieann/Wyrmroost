package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;
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
    private final float distanceCheck;
    private Vec3 waterTargetPosition;
    private Vec3 initialPosition;
    private boolean step1Done;
    private boolean step2Done;
    private boolean step3Done;
    private boolean stopFlag;
    private boolean speedFlag;
    private int finalTicks;
    private boolean boostflag;
    private boolean animationFlag;
    private int step2Ticks;

    public WRWaterLeapGoal(WRDragonEntity entity, double speedIn, int horizontalOffset, int minWaterDistance, float distanceCheck) {
        super(entity);
        this.entity = entity;
        this.speedTowardsTarget = speedIn;
        this.distanceCheck = distanceCheck;
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
        if (!entity.isUsingSwimmingNavigator()) {
            return false;
        }
        if (entity.getTarget()!=null){
            return false;
        }
        if (entity.getRandom().nextDouble() < 0.001) {
        //Get a random position...
            Vec3 randomPosition = (BehaviorUtils.getRandomSwimmablePos(this.entity, 32, 0));
            if (randomPosition != null) {
                //Shift the random position to the water surface, and check if we have indeed reached water...
                BlockPos waterTargetBlockPos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(randomPosition)).below();
                if (!(entity.level.getFluidState(waterTargetBlockPos).is(FluidTags.WATER))) {
                    return false;
                }
                //Check to see we have a 3*3 column of water blocks from the water surface to the initial position, 20 blocks below...
                for (int xOffset = -1; xOffset <=1; xOffset++) {
                    for (int yOffset = 0; yOffset >= -20; yOffset--) {
                        for (int zOffset = -1; zOffset <= 1; zOffset++) {
                            BlockPos.MutableBlockPos targetBlockPosMutable = waterTargetBlockPos.mutable();
                            targetBlockPosMutable.move(xOffset,yOffset,zOffset);
                            if (!entity.level.isLoaded(targetBlockPosMutable)) {
                                return false;
                            }
                            if (!entity.level.getFluidState(targetBlockPosMutable).is(FluidTags.WATER)) {
                                return false;
                            }
                        }
                    }
                }
                //Set the initial jump position 20 blocks below the random water position...
                BlockPos initialBlockPos = waterTargetBlockPos.below(20);
                if (!(entity.level.getFluidState(initialBlockPos).is(FluidTags.WATER))) {
                    return false;
                }
                initialPosition = new Vec3(initialBlockPos.getX(),initialBlockPos.getY(),initialBlockPos.getZ());
                waterTargetPosition = new Vec3(waterTargetBlockPos.getX(),waterTargetBlockPos.getY(),waterTargetBlockPos.getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        //If first navigation is stuck before it reaches the initial position, stop the goal.
        if (!step1Done && entity.getNavigation().isStuck()) {
            return false;
        }
        //If it somehow leaves the water before reaching the initial position, stop the goal.
        if (!step1Done && !entity.isInWater()) {
            return false;
        }
        //If second navigation is stuck before it reaches the water target position, stop the goal.
        if (!step2Done && entity.getNavigation().isStuck()) {
            return false;
        }
        if (stopFlag) {
            return false;
        }
        //Entity being controlled should overwrite all other goals
        if (entity.canBeControlledByRider()) {
            return false;
        }
        //Tick Counter for step 2 - ensures if we somehow miss the breach the goals gets stopped.
        if (step2Ticks > 60) {
            return false;
        }
        //If it has finished all the steps, and it has had time to perform the return animation, stop the Goal.
        if (finalTicks > 25) {
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

        //Step 1: Reach startPosition, below the target position...
        if (!step1Done) {
            //Check to see if we have reached the first position...
           if (entity.distanceToSqr(initialPosition.x, initialPosition.y, initialPosition.z) < distanceCheck) {
                step1Done = true;
               //Move to target at regular speed for 5 ticks first, this ensures we align with target before accelerating...
               entity.getNavigation().moveTo(waterTargetPosition.x, waterTargetPosition.y, waterTargetPosition.z, speedTowardsTarget);
               //If the navigation is stopped, but not stuck, calculate a new path...
            } else if (!entity.getNavigation().isStuck() && entity.getNavigation().isDone()) {
               entity.getNavigation().moveTo(initialPosition.x, initialPosition.y, initialPosition.z, speedTowardsTarget);
           }
        }

        //Step 2: Reach water target position and overshoot,flying out...
        if (step1Done && !step2Done) {
            //As soon as it leaves the water...
            if (!entity.isInWater()) {
                entity.getNavigation().stop();
                //Start fly animation, proceed to next step
                super.start(breachFlyAnimation, 1, 5);
                entity.getNavigation().stop();
                step2Done = true;
            }

            //We have already been moving slowly to target, now we accelerate and start the actual breaching..
            if (step2Ticks >5 && !speedFlag) {
                speedFlag = true;
                //We unlock Yaw
                entity.setYawUnlocked(true);
                //We freeze basic locomotion
                entity.setBreaching(true);
                super.start(breachStartAnimation, 1, 10);
                entity.getNavigation().moveTo(waterTargetPosition.x, waterTargetPosition.y, waterTargetPosition.z, speedTowardsTarget*2.2);
            }

            //Once we approach the target position, launch us out of the water
            if (!boostflag && waterTargetPosition.y-entity.position().y < 6) {
                double yMovement = entity.getDeltaMovement().y;
                if (yMovement < 1.2) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x,1.2,entity.getDeltaMovement().z);
                }
                boostflag = true;
            }
            super.start(breachStartAnimation, 1, 10);
            step2Ticks++;
        }

        //Step 3: Return to the water...
        if (step2Done && !step3Done) {
            if (this.entity.isUnderWater()) {
                //As soon as it returns to the water...
                step3Done = true;
                //Lock Yaw once again
                entity.setYawUnlocked(false);
                //Give it a new position to move towards, avoids it getting stuck
                entity.getNavigation().moveTo(initialPosition.x, initialPosition.y, initialPosition.z, speedTowardsTarget);
            } else {
                //If it has not yet reached the water, keep flying...
                super.start(breachFlyAnimation, 1, 5);
            }
        }
        //Step 4: Goal ending, hold for a couple of ticks to finish last animation...
        if (step3Done) {
            //Once it has reached the water again, hold the goal for a few extra ticks to ensure animations plays fully
            super.start(breachEndAnimation, 3, 15);
            finalTicks++;
            if (finalTicks > 15 && !animationFlag) {
                animationFlag = true;
                entity.setBreaching(false);
            }
        }
    }

    @Override
    public void stop() {
        step1Done = false;
        step2Done = false;
        step3Done = false;
        stopFlag = false;
        speedFlag = false;
        boostflag = false;
        step2Ticks = 0;
        waterTargetPosition = null;
        initialPosition = null;
        finalTicks = 0;
        animationFlag = false;
        entity.setBreaching(false);
        entity.setYawUnlocked(false);
        entity.getNavigation().stop();
        super.stop();
    }
}