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

    @Override
    public boolean canUse(){
        if (this.entity.getAnimation().equals("base")){
            return false;
        }
        return true;
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
        this.entity.setAnimationTime(animationTime);
    }

    public void start(String animationName, int animationType, float animationTime){
        this.animationName = animationName;
        this.animationType = animationType;
        this.animationTime= animationTime;
        this.start();
    }

    @Override
    public void tick() {
        elapsedTime++;
    }

    @Override
    public void stop(){
        this.entity.setAnimation("base");
        this.entity.setAnimationType(1);
        this.entity.setAnimationTime(0);

    }
}
