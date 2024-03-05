package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class WRRandomLookAroundGoal extends Goal {
    //ToDo: Use this Goal for all WRMobs
    private final Mob mob;
    private double relX;
    private double relZ;
    private final double lookAngleRange;
    private int lookTime;

    public WRRandomLookAroundGoal(Mob pMob, double lookAngleRange) {
        this.mob = pMob;
        //These values ensure the mob is not looking around all over the place with large turn angles and fast rotation
        this.lookAngleRange = lookAngleRange/180.0D;
    }

    public boolean canUse() {
        return this.mob.getRandom().nextFloat() < 0.005F;
    }


    @Override
    public void start() {
        //from a range of looking angles, from 0 to 360, this will select a random angle lower than the range...
        double d0 = (Math.PI * lookAngleRange) * this.mob.getRandom().nextDouble();
        this.relX = Math.cos(d0);
        this.relZ = Math.sin(d0);
        this.lookTime = 20 + this.mob.getRandom().nextInt(20);
    }

    public boolean canContinueToUse() {
        return this.lookTime >= 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        --this.lookTime;
        this.mob.getLookControl().setLookAt(this.mob.getX() + this.relX, this.mob.getEyeY(), this.mob.getZ() + this.relZ);
    }
}
