package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WRRandomLiftOffGoal extends AnimatedGoal
{
    private final WRDragonEntity entity;
    private final int animationTimeTicks;
    private final double ascensionSpeed; // 0.15 is good for smallish dragons like silver glider but is comedially high for coin dragon

    // Should only be used for dragons that can walk but spend most of their time in the air
    public WRRandomLiftOffGoal(WRDragonEntity entity, int animationTimeTicks, double ascensionSpeed)
    {
        super(entity);
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        this.entity = entity;
        this.animationTimeTicks = animationTimeTicks;
        this.ascensionSpeed = ascensionSpeed;
    }

    @Override
    public boolean canUse()
    { // Make sure it's not doing anything else
        return entity.getRandom().nextDouble() < 0.01
            && entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND && entity.getNavigation().isDone() && entity.getTarget() == null
            && entity.isOnGround()
            && (! entity.isInOverrideAnimation() || entity.getOverrideAnimation().equals("float")) // allow random liftoff when floating on water
            && ! entity.isSleeping() && ! entity.getSitting() && ! entity.isLeashed()
            && ! entity.isPassenger() && ! entity.isVehicle()
            && entity.canLiftOff();
    }

    @Override
    public void start()
    {
        super.stop(); // clear float animation if that exists
        super.start("takeoff", AnimatedGoal.PLAY_ONCE, animationTimeTicks);
    }

    @Override
    public boolean canContinueToUse()
    {
        return (entity.getAltitude() <= entity.getFlightThreshold()*2 || (entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND && entity.canLiftOff()))
            && entity.getTarget() == null && ! entity.isPassenger() && ! entity.isVehicle();
    }

    public void tick()
    {
        if (! super.canContinueToUse()) {
            super.stop();
        } else {
            super.tick();
        }

        // Looks natural if we start moving up halfway through takeoff animation
        if (super.elapsedTime >= animationTimeTicks/2) {
            entity.setDeltaMovement(0, ascensionSpeed, 0);
            entity.setNavigator(WRDragonEntity.NavigationType.FLYING);
        }
        else {
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setNavigator(WRDragonEntity.NavigationType.GROUND);
        }
    }

    @Override
    public void stop()
    {
        super.stop();
        entity.getNavigation().stop();
    }
}
