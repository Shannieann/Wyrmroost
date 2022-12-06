package com.github.wolfshotz.wyrmroost.entities.dragon.ai;

import com.github.wolfshotz.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimatedGoal extends Goal {
    WRDragonEntity entity;
    String animationName;
    int animationType;
    float animationTime;
    float elapsedTime;

    public AnimatedGoal(WRDragonEntity entity, String animationName, int animationType, float animationTime){
        this.entity = entity;
        this.animationName = animationName;
        this.animationType = animationType;
        this.animationTime = animationTime;
        this.elapsedTime = 0;
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
        this.entity.setAnimation(animationName);
        this.entity.setAnimationType(animationType);
    }

    @Override
    public void tick() {
        elapsedTime++;
    }

    @Override
    public void stop(){
        this.entity.setAnimation("base");
        this.entity.setAnimationType(1);
    }
}
