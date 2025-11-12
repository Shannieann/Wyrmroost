package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.fly.WRFlyLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

// Should always take priority over sitting so sitting dragons can sleep!
public class WRSleepGoal extends AnimatedGoal {

    private final WRDragonEntity entity;
    private boolean underwaterSleeping;
    private final boolean nocturnal;
    private boolean lieDownDone;

    public WRSleepGoal(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
        this.nocturnal = false;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.TARGET));
    }

    public WRSleepGoal(WRDragonEntity entity, boolean nocturnal) {
        super(entity);
        this.entity = entity;
        this.nocturnal = nocturnal;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.TARGET));
    }
    @Override
    public boolean canUse() {
        // Currently Silver Glider can swim, but can't sleep underwater. So sleep logic is separate for BFL. If/when more aquatic dragons added, need to revisit

        //Sleep only if not doing any other activities...
        if (! entity.isIdling())  {
            return false;
        }

        //If it cannot swim, only sleep on ground
        if (! entity.canBreatheUnderwater() && ! entity.isOnGround()) {
            return false;
        }

        //Sleep only if dragon did not recently sleep / woke up
        if (entity.getSleepingCooldown() > 0) {
            return false;
        }
        //Sleep only at night
        if (! this.nocturnal && (entity.level.getDayTime() < 14000 || entity.level.getDayTime() > 23500)) {
            return false;
        }
        else if (this.nocturnal && (entity.level.getDayTime() >= 14000 && entity.level.getDayTime() <= 23500)) {
            return false;
        }

        //If BFL, only sleep in deep water
        if (entity instanceof EntityButterflyLeviathan) {

            if (! entity.isUnderWater()) {
                return false;
            }

            double yWaterPos = (entity.position().y);
            // Iterate downwards from the entity's Y position to find the top water level
            while (yWaterPos <= entity.level.getMaxBuildHeight()) {
                BlockPos checkPos = new BlockPos(entity.getX(), yWaterPos, entity.getZ());
                BlockState state = entity.level.getBlockState(checkPos);

                if (state.getBlock() == Blocks.WATER) {
                    yWaterPos = yWaterPos + 1.0; // Top of the water block
                }
                yWaterPos++;
            }
            if (yWaterPos != entity.level.getMaxBuildHeight()){
                if (yWaterPos-entity.getY() < 10){
                    return false;
                }
                else {
                    return false;
                }
            }
        }

        //If tamed, sleep only if at home and at reasonable health...
        //Or if set to sit down and all previous conditions are met...
        if (entity.isTame() && !(entity instanceof EntityButterflyLeviathan)) {
            if ((entity.isAtHome() && entity.defendsHome())) {
                return entity.getHealth() < entity.getMaxHealth() * 0.5;
            }
            return entity.getSitting();
        }

        return true;
    }

    @Override
    public void start() {
        entity.setSleeping(true);
        if (entity.canBreatheUnderwater() && entity.isUnderWater()) {
            underwaterSleeping = true;
        }
        entity.clearAI();
        entity.setXRot(0);

        if (underwaterSleeping) { // No lie down animation for water sleeping
            this.lieDownDone = true;
        }
        // don't use lie down animation if already sitting or doesn't exist
        else if (entity.getSitting() || this.entity.getLieDownTime() == -1) {
            this.lieDownDone = true;
        }
        else {
            int lieDownTime = this.entity.getLieDownTime();
            super.start("lay_down", 3, lieDownTime);
            this.lieDownDone = false;
        }
    }

    @Override
    public void tick() {
        if (! this.lieDownDone && ! super.canContinueToUse()) {
            // Done lying down, start sleeping
            // Doesn't apply underwater
            super.stop();
            this.lieDownDone = true; // Mark as done so we don't keep checking
            // sleep animation handled by predicateBasicLocomotion
        }
        else if (! this.lieDownDone) {
            super.tick();
        }

        LookControl lookControl = entity.getLookControl();
        if (lookControl instanceof WRGroundLookControl) {
            ((WRGroundLookControl) lookControl).stopLooking();
        }
        if (lookControl instanceof WRSwimmingLookControl) {
            ((WRSwimmingLookControl) lookControl).stopLooking();
        }
        if (lookControl instanceof WRFlyLookControl) {
            ((WRFlyLookControl) lookControl).stopLooking();
        }
        // Heal while sleeping
        if (entity.getHealth() < entity.getMaxHealth() && entity.getRandom().nextDouble() < 0.005) {
            entity.heal(1);
        }
    }

    @Override
    public boolean canContinueToUse(){
        //If wrong time of day, wake up
        if (! this.nocturnal && (entity.level.getDayTime() < 14000 || entity.level.getDayTime() > 23500)) {
            return false;
        } else if (this.nocturnal && (entity.level.getDayTime() >= 14000 && entity.level.getDayTime() <= 23500)) {
            return false;
        }
        //If it's a water sleeping entity, and it somehow gets out of water, wake up
        if (underwaterSleeping && !entity.isUnderWater()) {
            return false;
        }
        // If it can't breathe underwater and somehow sleeps in the water, wake up
        else if (! entity.canBreatheUnderwater() && entity.isUnderWater()) {
            return false;
        }
        //Allows us to check for other methods, elsewhere, that might have set the DataParameter to false
        //For instance, the hurt method...
        if (!entity.getSleeping()){
            return false;
        }
        if (!entity.isIdling()) {
            return false;
        }
        return true;
    }

    @Override
    public void stop(){
        entity.setSleeping(false);
        this.lieDownDone = false;
        this.underwaterSleeping = false;
        entity.setSleepingCooldown(WRDragonEntity.MAX_SLEEPING_COOLDOWN);
        super.stop();
    }
}