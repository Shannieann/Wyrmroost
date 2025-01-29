package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class WRRandomLiftOffGoal extends Goal {
    private final WRDragonEntity entity;
    private final int radius;
    private final int yMax;
    private final int yMin;
    private final int minimumDistance;

    protected final float spread;

    protected double x;
    protected double y;
    protected double z;

    public WRRandomLiftOffGoal(WRDragonEntity entity, int radius, int maximumLiftOffHeight, int minimumLiftOffHeight, int minimumLiftOffDistance,float spread) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.radius = radius;
        this.yMax = maximumLiftOffHeight;
        this.yMin = minimumLiftOffHeight;
        this.minimumDistance = minimumLiftOffDistance;
        this.spread = spread;
    }

    @Override
    public boolean canUse() {
        if (this.entity.canBeControlledByRider())
            return false;

        if (this.entity.getTarget() != null)
            return false;

        if (this.entity.getSleeping())
            return false;

        if (this.entity.getSitting())
            return false;

        //ToDo: Note, random chance
        if (entity.getRandom().nextDouble() < 0.001) {
            Vec3 targetPosition = getPos(entity, radius, yMax, yMin, entity.getLookAngle().x, entity.getLookAngle().z, spread);

            if (targetPosition == null)
                return false;
            else {
                this.x = targetPosition.x;
                this.y = targetPosition.y;
                this.z = targetPosition.z;
                return true;

            }

        }
        return false;
    }



    public Vec3 getPos(PathfinderMob pMob, int pRadius, int yMax, int YMin, double pX, double pZ, float pAmplifier) {
        BlockPos blockpos = null;
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            blockpos = generateAngleConstrainedPosInRange(pMob.getRandom(), pRadius, yMax, yMin,entity.getY(), pX, pZ, (double) pAmplifier);
            if (blockpos != null) {
                if (blockpos.getY() < YMin) {
                    blockpos = new BlockPos(blockpos.getX(), YMin, blockpos.getZ());
                }
                if (Vec3.atCenterOf(blockpos).distanceTo(pMob.position()) >= minimumDistance &&
                        !GoalUtils.isWater(pMob, blockpos) &&
                        !GoalUtils.hasMalus(pMob, blockpos)) {
                    break;
                }
            }
            attempts++;
        }

        return (blockpos != null && attempts < maxAttempts) ? Vec3.atCenterOf(blockpos) : null;
    }


    //Given a startingY value, a minY and a maxY, this will generate a random position between startingY + yMin and startingY + yMax, within the angles given
    public BlockPos generateAngleConstrainedPosInRange(Random pRandom, int pMaxHorizontalDifference, int yMax, int yMin, int pY, double pX, double pZ, double pMaxAngleDelta) {
        double d0 = Mth.atan2(pZ, pX) - (double)((float)Math.PI / 2F);
        double d1 = d0 + (double)(2.0F * pRandom.nextFloat() - 1.0F) * pMaxAngleDelta;
        double d2 = Math.sqrt(pRandom.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)pMaxHorizontalDifference;
        double d3 = -d2 * Math.sin(d1);
        double d4 = d2 * Math.cos(d1);

        if (!(Math.abs(d3) > (double)pMaxHorizontalDifference) && !(Math.abs(d4) > (double)pMaxHorizontalDifference)) {
            int i = pRandom.nextInt(yMax - yMin + 1) + pY + yMin;
            return new BlockPos(d3, (double)i, d4);
        } else {
            return null;
        }
    }
}