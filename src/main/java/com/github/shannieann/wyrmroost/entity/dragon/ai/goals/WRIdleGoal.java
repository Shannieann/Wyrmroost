package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class WRIdleGoal extends AnimatedGoal{

    public WRIdleGoal(WRDragonEntity entity) {
        super(entity);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.entity.numIdleAnimationVariants() > 0
                && this.entity.getRandom().nextFloat() < 0.012
                && this.entity.isIdling()
                && ! this.entity.isAggressive()
                && ! this.entity.getSitting()
                && ! this.entity.getSleeping()
                && ! this.entity.isPassenger()
                && ! this.entity.isVehicle();
    }

    @Override
    public void start(){
        int randInt = entity.getRandom().nextInt(this.entity.numIdleAnimationVariants());
        System.out.println("Starting Idle animation: " + this.entity.getRandomIdleAnimation(randInt) + " for " + this.entity.getIdleAnimationTime(randInt) + " ticks");
        super.start(this.entity.getRandomIdleAnimation(randInt), 3, this.entity.getIdleAnimationTime(randInt));
    }

    @Override
    public boolean canContinueToUse(){
        return ! this.entity.isAggressive()
                && this.entity.isIdling()
                && ! this.entity.isPassenger()
                && ! this.entity.isVehicle()
                && super.canContinueToUse();
    }
}