package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;

public class WRIdleGoal extends AnimatedGoal{
    private int idleAnimationVariants;
    private int idleAnimation1Time;
    private int idleAnimation2Time;
    private int idleAnimation3Time;

    public WRIdleGoal(WRDragonEntity entity, int idleAnimation1Time) {
        super(entity);
        this.idleAnimationVariants = entity.idleAnimationVariants();
        this.idleAnimation1Time = idleAnimation1Time;
    }

    public WRIdleGoal(WRDragonEntity entity, int idleAnimation1Time, int idleAnimation2Time) {
        super(entity);
        this.idleAnimationVariants = entity.idleAnimationVariants();
        this.idleAnimation1Time = idleAnimation1Time;
        this.idleAnimation2Time = idleAnimation2Time;

    }

    public WRIdleGoal(WRDragonEntity entity, int idleAnimation1Time, int idleAnimation2Time, int idleAnimation3Time) {
        super(entity);
        this.idleAnimationVariants = entity.idleAnimationVariants();
        this.idleAnimation1Time = idleAnimation1Time;
        this.idleAnimation2Time = idleAnimation2Time;
        this.idleAnimation3Time = idleAnimation3Time;
    }

    @Override
    public boolean canUse() {
        if (!this.entity.isOnGround())
            return false;
        if (this.entity.isAggressive())
            return false;
        if (this.entity.getRandom().nextDouble() < /*0.001*/ 1)
            return true;
        return false;
    }

    @Override
    public void start(){
        int idleVariant = entity.getRandom().nextInt(idleAnimationVariants)+1;
        int idleAnimationTime;
        switch (idleVariant) {
            case 1: idleAnimationTime = idleAnimation1Time;
            break;
            case 2: idleAnimationTime = idleAnimation2Time;
            break;
            case 3: idleAnimationTime = idleAnimation3Time;
            break;
            default: idleAnimationTime = idleAnimation1Time;
        }
        super.start("idle"+idleVariant, 2,idleAnimationTime);
    }

    @Override
    public boolean canContinueToUse(){
        if (this.entity.isAggressive()) {
            return false;
        }
        return super.canContinueToUse();
    }
}