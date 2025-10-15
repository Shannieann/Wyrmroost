package com.github.shannieann.wyrmroost.entity.dragon.ai.movement.fly;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;

public class WRFlyLookControl extends LookControl {

    private final int maxYRotFromCenter;
    private boolean stopLooking;

    public WRFlyLookControl(Mob pMob, int pMaxYRotFromCenter) {
        super(pMob);
        this.maxYRotFromCenter = pMaxYRotFromCenter;
    }

    public void tick() {
        stopLooking = false;
        if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            this.getYRotD().ifPresent((rot) -> {
                this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, rot + 20.0F, this.yMaxRotSpeed);
            });
            this.getXRotD().ifPresent((rot) -> {
                // Commenting out as this makes it impossible for anything else to set xrot while flying. Unlike for y, there is no separate head xrot.
                // this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), rot + 10.0F, this.xMaxRotAngle));
            });
        } else {
            if (this.mob.getNavigation().isDone()) {
                // Commenting out as this makes it impossible for anything else to set xrot while flying. Unlike for y, there is no separate head xrot.
                // this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 5.0F));
            }

            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
        }

        float f = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
        if (f < (float)(-this.maxYRotFromCenter)) {
            this.mob.yBodyRot = Math.max(this.mob.yHeadRot,this.mob.yBodyRot - 4.0F);
            this.mob.yBodyRot -= 4.0F;
        } else if (f > (float)this.maxYRotFromCenter) {
            this.mob.yBodyRot = Math.min(this.mob.yHeadRot,this.mob.yBodyRot - 4.0F);
            this.mob.yBodyRot += 4.0F;
        }
    }

    @Override
    public void setLookAt(double x, double y, double z, float speed, float maxAngle) {
        if (!stopLooking) {
            super.setLookAt(x, y, z, speed, maxAngle);
        }
    }

    public void stopLooking() {
        //Method called to ensure we no longer look at targets, if something must lock rotation
        this.stopLooking = true;
    }
}
