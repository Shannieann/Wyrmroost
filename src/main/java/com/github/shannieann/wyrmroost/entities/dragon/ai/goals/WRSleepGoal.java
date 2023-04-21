package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entities.dragon.ai.movement.swimming.WRSwimmingLookControl;
import com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking.WRGroundLookControl;
import net.minecraft.world.entity.ai.control.LookControl;

public class WRSleepGoal extends AnimatedGoal{

    private final WRDragonEntity entity;
    private boolean underwaterSleeping;
    private boolean wakeUp;

    public WRSleepGoal(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        if (entity.speciesCanSwim() && (!entity.isUnderWater() && !entity.isOnGround())) {
            return false;
        }
        if (!entity.speciesCanSwim() && !entity.isOnGround()) {
            return false;
        }
        //Sleep only if dragon did not recently sleep / woke up
        if (entity.sleepCooldown > 0) {
            return false;
        }
        //Sleep only at night
        if (entity.level.getDayTime() < 14000 || entity.level.getDayTime() > 23500) {
            return false;
        }
        //Sleep only if not doing any other activities...
        if (!isIdling())  {
            return false;
        }
        //If tamed, sleep only if at home and at reasonable health...
        //Or if set to sit down and all previous conditions are met...
        if (entity.isTame())
        {
            if (entity.isAtHome()) {
                if (entity.defendsHome()) {
                    return entity.getHealth() < entity.getMaxHealth() * 0.25;
                }
            }
            else if (!entity.isInSittingPose()) {
                return false;
            }
        }
        return true;
    }


    public boolean isIdling()
    {
        return entity.getNavigation().isDone() && entity.getTarget() == null && !entity.isVehicle() && (entity.speciesCanSwim() || !entity.isInWaterOrBubble()) && !entity.isUsingFlyingNavigator();
    }

    @Override
    public void start(){
        entity.setSleeping(true);
        if (entity.speciesCanSwim() && entity.isUnderWater()) {
            underwaterSleeping = true;
        }
        entity.clearAI();
        entity.setXRot(0);
    }

    @Override
    public void tick(){
        if (!wakeUp) {
            LookControl lookControl = entity.getLookControl();
            if (lookControl instanceof WRGroundLookControl) {
                ((WRGroundLookControl)lookControl).stopLooking();
            }
            if (lookControl instanceof WRSwimmingLookControl) {
                ((WRSwimmingLookControl)lookControl).stopLooking();
            }
            //ToDo: Flying look Control

            if (entity.getHealth() < entity.getMaxHealth() && entity.getRandom().nextDouble() < 0.005) {
                entity.heal(1);
            }

            if (shouldWakeUp()) {
                wakeUp = true;
            } else {
                super.start(underwaterSleeping? "sleep_water" : "sleep",1,20);
            }
        } else {
            super.tick();
            if (!super.canContinueToUse()) {
                super.stop();
                stop();
            }
        }
    }


    public boolean shouldWakeUp(){
        if (entity.level.getDayTime() < 14000 || entity.level.getDayTime() > 23500) {
            return entity.getRandom().nextDouble() < 0.0065;
        }
        if (underwaterSleeping && !entity.isUnderWater()) {
            return true;
        }
        //Allows us to check for other methods, elsewhere, that might have set the DataParameter to false
        //For instance, the hurt method...
        if (!entity.isSleeping()){
            return true;
        }
        return false;
    }

    @Override
    public void stop(){
        entity.setSleeping(false);
        wakeUp = false;
        entity.sleepCooldown = 350;
    }
}
