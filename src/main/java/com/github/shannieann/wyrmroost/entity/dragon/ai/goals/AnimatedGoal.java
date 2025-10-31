package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimatedGoal extends Goal {
    public WRDragonEntity entity;
    public String animationName;
    public int animationType; // 1 = loop, 2 = play once, 3 = hold on last frame
    public int animationTime; // Keep in mind that AI goal ticks are oftn checked every 0.1 seconds, as opposed to the usual 0.05 seconds for general entity updates
    public int elapsedTime;

    public static final int LOOP = 1;
    public static final int PLAY_ONCE = 2;
    public static final int HOLD_ON_LAST_FRAME = 3;

    public AnimatedGoal(WRDragonEntity entity){
        this.entity = entity;
        this.elapsedTime = 0;
    }

    public AnimatedGoal(WRDragonEntity entity, String animationName, int animationType, int animationTime){
        this.entity = entity;
        this.animationName = animationName;
        this.animationType = animationType;
        this.animationTime = animationTime;
        this.elapsedTime = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return (elapsedTime < animationTime);
    }

    @Override
    public void start(){
        //This method will get called only if an animation was set manually (tick, etc.)
        //If we have called this, we already have a set animation in the constructor, so there's no need to set it again...
        //Just proceed to count ticks directly...
    }

    public void start(String animationName, int animationType, int animationTime) {
        this.elapsedTime = 0;
        this.entity.setAnimation(this.animationName = animationName);
        this.entity.setAnimationType(this.animationType = animationType);
        this.entity.setAnimationTime(this.animationTime = animationTime);
        this.entity.setAnimationInOverride(true);
    }

    @Override
    public void tick() {
        //Timer to know when to stop animation
        //This is the whole reason why we add this Goal to all entities, so any manually set animations can use the timer
        System.out.println("Ticking animation: " + this.entity.getAnimation() + " for " + this.elapsedTime + " ticks");
        elapsedTime++;
    }

    @Override
    public void stop(){
        //Once the animation has ran its course, we reset it, clearing up the entity to play new animations
        //We ensure we reset manualAnimationCall
        this.elapsedTime = 0;
        System.out.println("Stopping animation: " + this.entity.getAnimation());
        this.entity.setAnimationInOverride(false);
    }
}