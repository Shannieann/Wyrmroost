package com.github.shannieann.wyrmroost.entities.dragon.ai;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;

/**
 * Created by com.github.WolfShotz - 8/26/19 - 16:12
 * <p>
 * Disallows rotations while sitting, sleeping, and helps control yaw while controlling
 */

public class WRBodyControl extends BodyRotationControl
{
    public WRDragonEntity entity;
    private float lastStableYHeadRot;

    public WRBodyControl(WRDragonEntity dragon)
    {
        super(dragon);
        this.entity = dragon;
    }

    public void clientTick() {
        if (this.isMoving()) {
            //If it's moving match the BodyRot to its movement yaw...
            this.entity.yBodyRot = this.entity.getYRot();
            //Head will only rotate if it's offset angle to movement is too much
            this.rotateHeadIfNecessary();
            this.lastStableYHeadRot = this.entity.yHeadRot;
        } else {
            //If it's not moving, rotate body to match head if differnt is too much
            this.rotateBodyToMatchHeadIfNecessary();
        }
        //TODO: PREVENT ROTATIONS IF ASLEEP
    }

    private boolean isMoving() {
        double d0 = this.entity.getX() - this.entity.xo;
        double d1 = this.entity.getZ() - this.entity.zo;
        return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
    }

    private void rotateHeadIfNecessary() {
        this.entity.yHeadRot = Mth.rotateIfNecessary(this.entity.yHeadRot, this.entity.yBodyRot, (float)this.entity.getMaxHeadYRot());
    }

    private void rotateBodyToMatchHeadIfNecessary() {
        this.entity.yBodyRot = Mth.rotateIfNecessary(this.entity.yBodyRot, this.entity.yHeadRot, (float)this.entity.getMaxHeadYRot());
    }
}
