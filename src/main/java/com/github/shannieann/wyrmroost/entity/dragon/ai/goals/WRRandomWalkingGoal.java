package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

// Pretty much the same as RandomStrollGoal, but accounts for restrict radius and follow player
public class WRRandomWalkingGoal extends RandomStrollGoal {

    public static final float PROBABILITY = 0.001F;
    protected final float probability;
    private final WRDragonEntity mob;
 
    public WRRandomWalkingGoal(WRDragonEntity pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        this.probability = PROBABILITY;
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public WRRandomWalkingGoal(WRDragonEntity pMob, double pSpeedModifier, float probability) {
        super(pMob, pSpeedModifier);
        this.probability = probability;
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    @Override
    public boolean canUse()
    {
        if (this.mob.getRandom().nextFloat() >= this.probability)
            return false;

        if (this.mob.isVehicle())
            return false;

        if (this.mob.getTarget() != null)
            return false;

        if (!this.mob.isUsingLandNavigator()) {
            return false;
        }

        if (this.mob.getSleeping() || this.mob.getSitting() || this.mob.isRidingPlayer())
            return false;

        Vec3 pos = this.getPosition();
        if (pos == null) {
            return false;
        }
        this.wantedX = pos.x;
        this.wantedY = pos.y;
        this.wantedZ = pos.z;
        this.forceTrigger = false;
        return true;
    }

    /** LandRandomPos expects a linear block radius (e.g. 10–15).
     * OWD (and others) store getRestrictRadius() as squared; use sqrt and cap so pathfinding can succeed.
     */
    private int getRandomWalkHorizontalRange() {
        float r = this.mob.getRestrictRadius();
        if (r <= 0) return 12;
        int linear = (int) Math.sqrt(r);
        return Math.min(24, Math.max(8, linear));
    }

    @Nullable
    protected Vec3 getPosition() {
        int range = getRandomWalkHorizontalRange();
        Vec3 randomPos = LandRandomPos.getPos(this.mob, range, 7);
        int attemptCounter = 15;
        // Ensure randomPos is not null before checking restriction
        while (attemptCounter > 0 && randomPos != null 
            && !this.mob.isWithinRestriction(new BlockPos(randomPos)) 
            && (!this.mob.isTame() || Math.sqrt(this.mob.getOwner().distanceToSqr(randomPos)) < this.mob.getRestrictRadius())) {
            randomPos = LandRandomPos.getPos(this.mob, (int) this.mob.getRestrictRadius(), 7);
            attemptCounter--;
        }
        return randomPos;
    }

    @Override
    public void start()
    {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public boolean canContinueToUse()
    {
        //If it's close enough to assume it has reached the Goal
        if (this.mob.distanceToSqr(this.wantedX,this.wantedY,this.wantedZ) < 9)
            return false;

        if (this.mob.isVehicle())
            return false;

        if (this.mob.getTarget() != null)
            return false;

        if (!this.mob.isUsingLandNavigator())
            return false;

        if (this.mob.getSleeping() || this.mob.getSitting() || this.mob.isRidingPlayer())
            return false;

        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop()
    {
        this.mob.getNavigation().stop();
        super.stop();
    }
}