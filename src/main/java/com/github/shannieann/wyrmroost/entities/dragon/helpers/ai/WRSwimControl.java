package com.github.shannieann.wyrmroost.entities.dragon.helpers.ai;


import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeMod;

public class WRSwimControl extends MoveControl {
    private final float maxTurnPitch = 85;
    private final WRDragonEntity entity;

    public WRSwimControl(WRDragonEntity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO && !this.entity.getNavigation().isDone()) {
            double entityToTargetX = this.wantedX - this.entity.getX();
            double entityToTargetY = this.wantedY - this.entity.getY();
            double entityToTargetZ = this.wantedZ - this.entity.getZ();
            double distanceToTargetSqr = entityToTargetX * entityToTargetX + entityToTargetY * entityToTargetY + entityToTargetZ * entityToTargetZ;
            //If we have reached the target, set speed to 0..
            if (distanceToTargetSqr < (double)2.5000003E-7F) {
                this.entity.setZza(0.0F);
            } else {
                //Yaw: Adjust currentAngle to match desiredAngle, but do gradually, only allowing incrmeents of maxTurnYaw
                //Else, control movement to reach the target
                float desiredAngleYaw = (float)(Mth.atan2(entityToTargetZ, entityToTargetX) * (double)(180F / (float)Math.PI)) - 90.0F;
                float currentAngleYaw = this.entity.getYRot();
                //Set max turn value: Sharp turns if controlled by rider of if aggressive
                float maxTurnYaw;
                if (this.entity.getBreaching()){
                    maxTurnYaw = 90;
                }
                else if ((this.entity.getTarget() != null) || this.entity.canBeControlledByRider()) {
                    maxTurnYaw = 15;
                } else {
                    maxTurnYaw = 90;
                }
                this.entity.setYRot(this.rotlerp(currentAngleYaw, desiredAngleYaw, maxTurnYaw));
                this.entity.yBodyRot = this.entity.getYRot();
                this.entity.yHeadRot = this.entity.getYRot();

                float speed = ((float) this.entity.getAttributeValue(ForgeMod.SWIM_SPEED.get()));
                if (this.entity.isInWater() || this.entity.level.getBlockState(new BlockPos(entity.position()).below()).is(Blocks.WATER)) {
                    this.entity.setSpeed(speed);
                    double horizontalDistanceToTarget = Math.sqrt(entityToTargetX * entityToTargetX + entityToTargetZ * entityToTargetZ);

                    //If we are not directly above or below the target...
                    //Or if we are not at the target level...
                    if (Math.abs(entityToTargetY) > (double)1.0E-5F || Math.abs(horizontalDistanceToTarget) > (double)1.0E-5F) {
                        float desiredAnglePitch = -((float)(Mth.atan2(entityToTargetY, horizontalDistanceToTarget) * (double)(180F / (float)Math.PI)));
                        desiredAnglePitch = Mth.clamp(Mth.wrapDegrees(desiredAnglePitch), (float)(-this.maxTurnPitch), (float)this.maxTurnPitch);
                        this.entity.setXRot(this.rotlerp(this.entity.getXRot(), desiredAnglePitch, 20.0F));
                    }
                    //Adjust acceleration components, this ensures the entity does not freeze entirely when reaching its target...
                    float f4 = Mth.cos(this.entity.getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(this.entity.getXRot() * ((float)Math.PI / 180F));
                    this.entity.zza = f4 * speed;
                    this.entity.yya = -f3 * speed;
                } else {
                    this.entity.setSpeed(speed);
                }

            }
        } else {
            this.entity.setSpeed(0.0F);
            this.entity.setXxa(0.0F);
            this.entity.setYya(0.0F);
            this.entity.setZza(0.0F);
        }
    }
}