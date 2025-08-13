package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingLookControl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
public class WRSitGoal extends AnimatedGoal {

    private final WRDragonEntity entity;

    public WRSitGoal(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        return owner != null
        && entity.distanceToSqr(owner) < 144.0
        // Right now only tame entities can sit.
        // Will need to rework goal + change priorities in all classes if that changes.
        && entity.getSitting()
        && entity.isTame()
        // TODO: Should BFLs be able to sit on land?
        && ((!entity.speciesCanSwim() && entity.isOnGround()) || (entity.speciesCanSwim() && entity.isInWater()))
        && !entity.isUsingFlyingNavigator()
        && !entity.isRiding()
        && entity.getPassengers().size() == 0
        && entity.getTarget() == null
        && !entity.isImmobile();
    }

    @Override
    public void start(){
        entity.clearAI();
        entity.setXRot(0);
    }

    @Override
    public void tick() {
        LookControl lookControl = entity.getLookControl();
        if (lookControl instanceof WRGroundLookControl) {
            ((WRGroundLookControl) lookControl).stopLooking();
        }
        if (lookControl instanceof WRSwimmingLookControl) {
            ((WRSwimmingLookControl) lookControl).stopLooking();
        }
        //ToDo: Flying look Control
        super.start("sit", 1, 20);
        super.tick();
    }

    @Override
    public boolean canContinueToUse(){
        return entity.getSitting()
                    && entity.isOnGround()
                    // TODO: Should BFLs be able to sit on land?
                    && ((!entity.speciesCanSwim() && entity.isOnGround()) || (entity.speciesCanSwim() && entity.isInWater()))
                    && !entity.isRiding()
                    && entity.getPassengers().size() == 0
                    && entity.getTarget() == null // getting attacked halfway should interrupt goal
                    && !entity.isImmobile();
    }

    @Override
    public void stop(){
        entity.setSitting(false);
        super.stop();
    }
}
