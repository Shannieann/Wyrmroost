package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.ground;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WRGroundMoveControl extends MoveControl{

    WRDragonEntity entity;
    float maximumYawChange;
    public WRGroundMoveControl(WRDragonEntity entity, float maximumYawChange) {
        super(entity);
        this.entity = entity;
        this.maximumYawChange = maximumYawChange;
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.STRAFE) {
            float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f1 = (float) this.speedModifier * f;
            float f2 = this.strafeForwards;
            float f3 = this.strafeRight;
            float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 *= f4;
            f3 *= f4;
            float f5 = Mth.sin(this.mob.getYRot() * ((float) Math.PI / 180F));
            float f6 = Mth.cos(this.mob.getYRot() * ((float) Math.PI / 180F));
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            if (!this.isWalkable(f7, f8)) {
                this.strafeForwards = 1.0F;
                this.strafeRight = 0.0F;
            }

            this.mob.setSpeed(f1);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = MoveControl.Operation.WAIT;

        } else if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            double targetX = this.wantedX - this.mob.getX();
            double targetZ = this.wantedZ - this.mob.getZ();
            double targetY = this.wantedY - this.mob.getY();
            double distanceSqr = targetX * targetX + targetY * targetY + targetZ * targetZ;
            if (distanceSqr < (double) 2.5000003E-7F) {
                this.mob.setZza(0.0F);
                return;
            }
            float horizontalAngle = (float) (Mth.atan2(targetZ, targetX) * (double) (180F / (float) Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), horizontalAngle, maximumYawChange));
            this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level.getBlockState(blockpos);
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
            if (targetY > (double) this.mob.maxUpStep && targetX * targetX + targetZ * targetZ < (double) Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double) blockpos.getY()) {
                this.mob.getJumpControl().jump();
                this.operation = MoveControl.Operation.JUMPING;
            }
        } else if (this.operation == MoveControl.Operation.JUMPING) {
            this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.operation = MoveControl.Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0F);
        }
    }

    private boolean isWalkable(float pRelativeX, float pRelativeZ) {
        PathNavigation pathnavigation = this.mob.getNavigation();
        if (pathnavigation != null) {
            NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
            if (nodeevaluator != null && nodeevaluator.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)pRelativeX), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)pRelativeZ)) != BlockPathTypes.WALKABLE) {
                return false;
            }
        }

        return true;
    }
}