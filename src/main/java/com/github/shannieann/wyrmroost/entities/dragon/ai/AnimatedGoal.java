package com.github.shannieann.wyrmroost.entities.dragon.ai;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AnimatedGoal extends Goal {
    public WRDragonEntity entity;
    public String animationName;
    public int animationType;
    public float animationTime;
    public float elapsedTime;

    public AnimatedGoal(WRDragonEntity entity){
        this.entity = entity;
        this.elapsedTime = 0;
    }

    public AnimatedGoal(WRDragonEntity entity, String animationName, int animationType, float animationTime){
        this.entity = entity;
        this.animationName = animationName;
        this.animationType = animationType;
        this.animationTime = animationTime;
        this.elapsedTime = 0;
    }


    @Override
    public boolean canUse(){
        //This method will get called whenever the entity tries to execute an animation...
        //It should only use this Goal -directly- if the animation was set manually...
        //Meaning, if the animation was -NOT- set via a Goal that extends this class and called the start() method...
        //And was instead set directly in methods such as tick(), etc.
        if (entity.getManualAnimationCall()) {
            return !animationName.equals("base");
        }

        return false;
    }


    @Override
    public boolean canContinueToUse() {
        if (elapsedTime > animationTime) {
            return false;
        }
        return true;
    }


    @Override
    public void start(){
        //This method will get called only if an animation was set manually (tick, etc.)
        //If we have called this, we already have a set animation in the constructor, so there's no need to set it again...
        //Just proceed to count ticks directly...
    }

    public void start(String animationName, int animationType, float animationTime){
        this.entity.setAnimation(this.animationName = animationName);
        this.entity.setAnimationType(this.animationType = animationType);
        this.entity.setAnimationTime(this.animationTime = animationTime);
    }

    @Override
    public void tick() {
        //Timer to know when to stop animation
        //This is the whole reason why we add this Goal to all entities, so any manually set animations can use the timer
        elapsedTime++;
    }

    @Override
    public void stop(){
        //Once the animation has ran its course, we reset it, clearing up the entity to play new animations
        //We ensure we reset manualAnimationCall
        this.elapsedTime = 0;
        this.entity.setAnimation("base");
        this.entity.setAnimationType(1);
        this.entity.setAnimationTime(0);
        this.entity.setPlayingAnimation(false);
        this.entity.setManualAnimationCall(false);
    }
}
