package com.github.shannieann.wyrmroost.entities.dragon.ai;

import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.shannieann.wyrmroost.util.Mafs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class WRSwimmingNavigator {}/*extends WaterBoundPathNavigation {

    public Navigator() {
        super(ButterflyLeviathanEntity.this, ButterflyLeviathanEntity.this.level);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        return new PathFinder(nodeEvaluator = new SwimNodeEvaluator(true), range);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !level.getBlockState(pos.below()).isAir();
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    private class MoveController extends MoveControl {
        public MoveController() {
            super(ButterflyLeviathanEntity.this);
        }

        public void tick() {
            if (operation == Operation.MOVE_TO && !canBeControlledByRider()) {
                operation = Operation.WAIT;
                double x = wantedX - getX();
                double y = wantedY - getY();
                double z = wantedZ - getZ();
                double distSq = x * x + y * y + z * z;
                if (distSq < 2.5000003E-7) setSpeed(0f); // why move...
                else {
                    float newYaw = (float) Math.toDegrees(Mth.atan2(z, x)) - 90f;
                    float pitch = -((float) (Mth.atan2(y, Mth.sqrt((float) (x * x + z * z))) * 180 / Math.PI));
                    pitch = Mth.clamp(Mth.wrapDegrees(pitch), -85f, 85f);

                    yHeadRot = newYaw;
                    yBodyRot = yRot = rotlerp(yRot, yHeadRot, getYawRotationSpeed());
                    pitch = rotlerp(pitch, pitch, 75);
                    ((LessShitLookController) getLookControl()).stopLooking();
                    float speed = isInWater() ? (float) getAttributeValue(ForgeMod.SWIM_SPEED.get()) : (float) getAttributeValue(MOVEMENT_SPEED);
                    setSpeed(speed);
                    if (isInWater()) {
                        zza = Mth.cos(pitch * (Mafs.PI / 180f)) * speed;
                        yya = -Mth.sin(pitch * (Mafs.PI / 180f)) * speed;
                    }
                }
            } else {
                setSpeed(0);
                setZza(0);
                setYya(0);
            }
        }
    }

     */

