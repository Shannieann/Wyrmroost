package com.github.shannieann.wyrmroost.entities.dragon.ai;


import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;

public class WRSwimControl extends MoveControl {
    private int maxTurnX = 85;
//TODO: Rename class
    private final WRDragonEntity entity;

    public WRSwimControl(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < (double)2.5000003E-7F) {
                this.mob.setZza(0.0F);
            } else {
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                //Set max turn value: Sharp turns if controlled by rider of if aggressive
                int maxTurnY;
                if (this.entity.getBreaching()){
                    maxTurnY = 90;
                }
                else if ((this.entity.getTarget() != null) || this.entity.canBeControlledByRider()) {
                     //TODO: Tweak Values
                    maxTurnY = 15;
                } else {
                     //TODO: Tweak Values
                     maxTurnY = 4;
                }
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, (float) maxTurnY));
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
                float speed = ((float) this.mob.getAttributeValue(ForgeMod.SWIM_SPEED.get()));
                if (this.mob.isInWater() || this.mob.level.getBlockState(new BlockPos(mob.position()).below()).is(Blocks.WATER)) {
                    this.mob.setSpeed(speed);
                    double d4 = Math.sqrt(d0 * d0 + d2 * d2);
                    if (Math.abs(d1) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F) {
                        float f2 = -((float)(Mth.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
                        f2 = Mth.clamp(Mth.wrapDegrees(f2), (float)(-this.maxTurnX), (float)this.maxTurnX);
                        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, 5.0F));
                    }

                    float f4 = Mth.cos(this.mob.getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(this.mob.getXRot() * ((float)Math.PI / 180F));
                    this.mob.zza = f4 * speed;
                    this.mob.yya = -f3 * speed;
                } else {
                    this.mob.setSpeed(speed);
                }

            }
        } else {
            this.mob.setSpeed(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }
}