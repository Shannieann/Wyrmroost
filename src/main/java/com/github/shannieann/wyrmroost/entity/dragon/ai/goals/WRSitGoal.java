package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground.WRGroundLookControl;
import com.github.shannieann.wyrmroost.entity.dragon.ai.movement.swim.WRSwimmingLookControl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
public class WRSitGoal extends AnimatedGoal {

    private final WRDragonEntity entity;
    private boolean underwaterSitting;
    private boolean sitDownDone;

    public WRSitGoal(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    public boolean canUse() {
        LivingEntity owner = entity.getOwner();
        return entity.getSitting()
        && (owner == null || entity.distanceToSqr(owner) < 144.0)
        // TODO: Should BFLs be able to sit on land?
        && (entity.isOnGround() || (entity.speciesCanSwim() && entity.isInWater()))
        && !entity.isUsingFlyingNavigator()
        && entity.getPassengers().size() == 0
        && entity.getTarget() == null;
    }

    @Override
    public void start() {
        if (entity.speciesCanSwim() && entity.isUnderWater()) {
            underwaterSitting = true;
        }
        entity.clearAI();
        entity.setXRot(0);

        // Don't use sit down animation if underwater or not defined
        if (underwaterSitting || this.entity.getSitDownTime() == -1) {
            super.start("sit", 1, 0);
            this.sitDownDone = true;
        }
        else {
            int sitDownTime = this.entity.getSitDownTime();
            super.start("sit_down", 3, sitDownTime);
            this.sitDownDone = false;
        }
    }

    @Override
    public void tick() {
        if (! this.sitDownDone && ! super.canContinueToUse()) {
            // Done sitting down, start sitting
            // Doesn't apply underwater
            super.stop();
            if (entity.speciesCanSwim() && entity.isUnderWater()) {
                super.start("swim_sit", 1, 0);
            }
            else {
                super.start("sit", 1, 0);
            }
            this.sitDownDone = true;
        }

        LookControl lookControl = entity.getLookControl();
        if (lookControl instanceof WRGroundLookControl) {
            ((WRGroundLookControl) lookControl).stopLooking();
        }
        if (lookControl instanceof WRSwimmingLookControl) {
            ((WRSwimmingLookControl) lookControl).stopLooking();
        }
        super.tick();
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getSitting()
            // TODO: Should BFLs be able to sit on land?
            && (entity.isOnGround() || (entity.speciesCanSwim() && entity.isInWater()))
            && entity.getPassengers().size() == 0
            && entity.getTarget() == null; // getting attacked halfway should interrupt goal
    }

    @Override
    public void stop() {
        this.sitDownDone = false;
        entity.setSitting(false);
        super.stop();
    }
}
