package com.github.shannieann.wyrmroost.entity.dragon.ai;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.BodyRotationControl;


public class WRBodyControl extends BodyRotationControl {

    private int headStableTime;

    public WRDragonEntity entity;
    private float lastStableYHeadRot;

    public WRBodyControl(WRDragonEntity dragon)
    {
        super(dragon);
        this.entity = dragon;
    }

    public void clientTick() {
        if (this.isMoving()) {
            this.entity.yBodyRot = this.entity.getYRot();
            this.rotateHeadToMatchBodyIfNecessary();
            this.lastStableYHeadRot = this.entity.yHeadRot;
            this.headStableTime = 0;
        } else {
            //Changed from Vanilla Behavior - will attempt to update head faster if not moving
            //This results in smoother, non-jittery body movement when stationary and looking
            //It will still allow a difference between head and body equal to MaxHeadYRot
            if (Math.abs(this.entity.yHeadRot - this.lastStableYHeadRot) > 0) {
                this.headStableTime = 0;
                this.lastStableYHeadRot = this.entity.yHeadRot;
                this.rotateBodyToMatchHeadIfNecessary();
            } else {
                ++this.headStableTime;
                if (this.headStableTime > 10) {
                    this.rotateBodyToFront();
                }
            }
        }
    }

    private boolean isMoving() {
        double d0 = this.entity.getX() - this.entity.xo;
        double d1 = this.entity.getZ() - this.entity.zo;
        return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
    }

    private void rotateHeadToMatchBodyIfNecessary() {
        this.entity.yHeadRot = Mth.rotateIfNecessary(this.entity.yHeadRot, this.entity.yBodyRot, (float)this.entity.getMaxHeadYRot());
    }

    private void rotateBodyToMatchHeadIfNecessary() {
        this.entity.yBodyRot = Mth.rotateIfNecessary(this.entity.yBodyRot, this.entity.yHeadRot, (float)this.entity.getMaxHeadYRot());
    }

    private void rotateBodyToFront() {
        int i = this.headStableTime - 10;
        float f = Mth.clamp((float)i / 10.0F, 0.0F, 1.0F);
        float f1 = (float)this.entity.getMaxHeadYRot() * (1.0F - f);
        this.entity.yBodyRot = Mth.rotateIfNecessary(this.entity.yBodyRot, this.entity.yHeadRot, f1);
    }

    protected float rotlerp(float pSourceAngle, float pTargetAngle, float pMaximumChange) {
        float f = Mth.wrapDegrees(pTargetAngle - pSourceAngle);
        if (f > pMaximumChange) {
            f = pMaximumChange;
        }

        if (f < -pMaximumChange) {
            f = -pMaximumChange;
        }

        float f1 = pSourceAngle + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }
}
