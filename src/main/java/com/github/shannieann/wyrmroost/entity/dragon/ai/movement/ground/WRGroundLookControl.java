package com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.ai.control.LookControl;

public class WRGroundLookControl extends LookControl
{
    private final WRDragonEntity dragon;
    private boolean stopLooking;

    public WRGroundLookControl(WRDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void tick()
    {
        super.tick();
        stopLooking = false;
    }

    @Override
    public void setLookAt(double x, double y, double z, float speed, float maxAngle)
    {
        if (!stopLooking) {
            super.setLookAt(x, y, z, speed, maxAngle);
        }
    }

    public void stopLooking()
    {
        //Method called to ensure we no longer look at targets, if something must lock rotation
        this.stopLooking = true;
    }
}
