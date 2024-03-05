package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingLookControl;
import net.minecraft.world.entity.ai.control.LookControl;
public class WRSitGoal extends AnimatedGoal {
    //ToDo:
    // Test on land creatures
    // SittING?
    private final WRDragonEntity entity;

    public WRSitGoal(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    public boolean canUse() {
        if (!entity.getSitting()){
            return false;
        }
        if (!entity.isOnGround() && !entity.isUsingFlyingNavigator()) {
            return false;
        }
        return true;
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
        //Allows us to check for other methods, elsewhere, that might have set the DataParameter to false
        //For instance, the hurt method...
        if (!entity.getSitting()){
            return false;
        }

        return true;
    }

    @Override
    public void stop(){
        entity.setSitting(false);
        super.stop();
    }
}
