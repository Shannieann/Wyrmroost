package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WRWaterLeapGoal extends AnimatedGoal {

    private BlockPos pos;
    private final WRDragonEntity entity;
    private final String breachStartAnimation = "breach_start";
    private final String breachFlyAnimation = "breach_fly";
    private final String breachEndAnimation = "breach_end";
    private final double speedTowardsTarget;
    private boolean step1Done;
    private int step1Ticks;
    private boolean step2Done;
    private int step2Ticks;
    private int finalTicks;

    private Vec3 startPos;
    public WRWaterLeapGoal(WRDragonEntity entity, double speedIn)
    {
        super(entity);
        this.entity = entity;
        this.speedTowardsTarget = speedIn;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (entity.isInSittingPose()) {
            return false;
        }
        if (entity.canBeControlledByRider()) {
            return false;
        }
        if (!entity.isUnderWater()) {
            return false;
        }
        if (entity.getRandom().nextDouble() < 0.05) {
            //Get the water surface position at which we are aiming...
            if (entity.level.getFluidState(this.pos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, this.entity.blockPosition()).below()).isEmpty()) {
                return false;
            }
            //Get the target position, ensure it's not too far away...
            this.pos = pos.relative(entity.getDirection(), (int) ((pos.getY() - entity.getY()) * 0.5d));
            if (pos.distSqr(new Vec3i(entity.position().x, entity.position().y, entity.position().z)) > 256) {
                return false;
            }
            if (pos.getY() - entity.getY() > 8) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean canContinueToUse() {
        if (entity.canBeControlledByRider()) {
            return false;
        }

        //If it hasn't attacked the target yet, and the entity somehow leaves the water, stop the Goal.
        if (!step1Done &&  (!entity.isInWater())) {
            return false;
        }

        // If it takes too long reaching the jump position, stop the Goal.
        if (step1Ticks> 100) {
            return false;
        }

        // If it has attacked the target but has not yet navigated to its end position, and it cannot reach the end position, stop the Goal.
        if (step1Done && !step2Done && !this.entity.level.getBlockState(new BlockPos(startPos)).isPathfindable(this.entity.level, new BlockPos(startPos), PathComputationType.WATER)) {
            return false;
        }
        // If it takes too long reaching the end position after the jump, stop the Goal.
        if (step2Ticks > 60) {
            return false;
        }
        //If it has finished all the steps, stop the Goal.
        if (step2Done && finalTicks > 12) {
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
    public void start()
    {
        entity.getLookControl().setLookAt(pos.getX(),pos.getY(),pos.getZ());
        this.entity.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedTowardsTarget);
        startPos = this.entity.position();
        entity.setBreaching(true);
        super.start(breachStartAnimation, 1, 10,false);
    }


    @Override
    public void tick() {
        //Step 1: Reach Jump starting position, at water surface level, where the target's at.
        //Step 3: Perform grab and attack.
        //Step 4: Return to the water.
        if (!step1Done) {
            //Attempt step 1....
            if (moveStep1()) {
                //If the JumpStart position is reached, record this position (will be used to determine when to deal damage).
                step1Done = true;
                //Apply an upwards boost to this entity, ensuring it jumps.
                if (entity.getDeltaMovement().y < 1.50D) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x,1.50D,entity.getDeltaMovement().z);
                }
            }
        }

        //Attempt Step2....
        if (step1Done && !step2Done) {
            if (moveStep2()){
                step2Done = true;
                super.start(breachEndAnimation, 2, 12,false);
            } else {
                super.start(breachFlyAnimation, 1, 10,false);
            }
        }

        if (step2Done) {
            finalTicks++;
        }
    }

    public boolean moveStep1(){
        //Creature will attempt to move to the target, from the StartPosition.
        // If this position is unreachable, the Goal will be stopped in canContinueToUse().
        //A tick counter is implemented to stop the Goal if this takes too long. This avoids the creature getting stuck forever if something does not work out.
        step1Ticks = ++step1Ticks;
        entity.getNavigation().moveTo(pos.getX(),pos.getY(),pos.getZ(),speedTowardsTarget);
        return entity.distanceToSqr(pos.getX(),pos.getY(),pos.getZ()) < 16.0F;
    }

    public boolean moveStep2(){
        //Once done with the Breach, return to the water and try to move back to the original StrikePosition.
        //This is done to avoid the entity trying to get to an odd place after the attack is performed.
        // If this position is unreachable, the Goal will be stopped in canContinueToUse().
        //A tick counter is implemented to stop the Goal if this takes too long. This avoids the creature getting stuck forever if something does not work out.
        step2Ticks = ++step2Ticks;
        entity.getNavigation().moveTo(startPos.x,startPos.y,startPos.z,speedTowardsTarget);
        super.start(breachFlyAnimation, 1, 10,false);
        return (entity.distanceToSqr(startPos.x,startPos.y,startPos.z) <= 10);
    }

    @Override
    public void stop()
    {
        step1Done = false;
        step1Ticks = 0;
        step2Done = false;
        step2Ticks = 0;
        startPos = null;
        entity.setBreaching(false);
        entity.clearAI();
        entity.getNavigation().stop();
        super.stop();
    }
}