package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class WRRandomFlyWalkGoal extends Goal {
    private final WRDragonEntity entity;
    private final int radius;
    private final int minimumDistance;
    private int switchCooldown;

    protected double x;
    protected double y;
    protected double z;

    private boolean isLanding;

    public WRRandomFlyWalkGoal(WRDragonEntity entity, int radius, int minimumFlyDistance) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.radius = radius;
        this.minimumDistance = minimumFlyDistance;
        this.switchCooldown = 200;
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

        if (entity.getRandom().nextDouble() < 0.001) {
            if (entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND) {
                Vec3 targetPosition = getLandPosition();

                if (targetPosition == null)
                    return false;
                else {
                    this.x = targetPosition.x;
                    this.y = targetPosition.y;
                    this.z = targetPosition.z;
                    return true;
                }
            }

            if (entity.getNavigationType() == WRDragonEntity.NavigationType.FLYING) {
                Vec3 targetPosition = getFlyingPos(radius, 128, 5, entity.getLookAngle().x, entity.getLookAngle().z);

                if (targetPosition == null)
                    return false;
                else {
                    this.x = targetPosition.x;
                    this.y = targetPosition.y;
                    this.z = targetPosition.z;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start(){
    }

    @Override
    public void tick() {
        if (switchCooldown > 0) {
            switchCooldown--;
        }

        if (isLanding && entity.getNavigation().isDone()) {
            entity.setNavigator(WRDragonEntity.NavigationType.GROUND);
            isLanding = false;
        }

        if (entity.getNavigation().isDone()) {
            if (entity.getNavigationType() == WRDragonEntity.NavigationType.FLYING) {
                if (switchCooldown == 0) {
                    if (entity.getRandom().nextDouble() < 0.001) {
                        switchCooldown = 200;
                        Vec3 landingPos = getLandPosition();
                        if (landingPos != null) {
                            entity.getNavigation().moveTo(landingPos.x, landingPos.y, landingPos.z, entity.getAttributeValue(Attributes.FLYING_SPEED));
                            isLanding = true;
                        }

                    }
                } else {
                    Vec3 flyingPos = getFlyingPos(radius, 128, 5, entity.getLookAngle().x, entity.getLookAngle().z);
                    entity.getNavigation().moveTo(flyingPos.x, flyingPos.y, flyingPos.z, entity.getAttributeValue(Attributes.FLYING_SPEED));

                }

                if (entity.getNavigationType() == WRDragonEntity.NavigationType.GROUND) {
                    if (switchCooldown == 0) {
                        if (entity.getRandom().nextDouble() < 0.001) {
                            switchCooldown = 200;
                            Vec3 liftOffPos = getFlyingPos(radius, 128, 5, entity.getLookAngle().x, entity.getLookAngle().z);
                            if (liftOffPos != null) {
                                entity.setNavigator(WRDragonEntity.NavigationType.FLYING);
                                entity.getNavigation().moveTo(liftOffPos.x, liftOffPos.y, liftOffPos.z, entity.getAttributeValue(Attributes.FLYING_SPEED));
                                isLanding = true;
                            }
                        }
                    }
                    else {
                        Vec3 landPos = getLandPosition();
                        entity.getNavigation().moveTo(landPos.x, landPos.y, landPos.z, entity.getAttributeValue(Attributes.MOVEMENT_SPEED));

                    }
                }
            }

        }
    }

    @Nullable
    protected Vec3 getLandPosition() {
        return LandRandomPos.getPos(this.entity, this.radius, 40);
    }

    @Nullable
    public Vec3 getFlyingPos(int pRadius, int yMax, int yMin, double pX, double pZ) {
        BlockPos blockpos = null;
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            blockpos = generateAngleConstrainedPosInRange(this.entity.getRandom(), pRadius, yMax, yMin,(int)entity.getY(), pX, pZ, 20D);
            if (blockpos != null) {
                if (blockpos.getY() < yMin) {
                    blockpos = new BlockPos(blockpos.getX(), yMin, blockpos.getZ());
                }
                if (Vec3.atCenterOf(blockpos).distanceTo(this.entity.position()) >= minimumDistance &&
                        !GoalUtils.isWater(this.entity, blockpos) &&
                        !GoalUtils.hasMalus(this.entity, blockpos)) {
                    break;
                }
            }
            attempts++;
        }

        return (blockpos != null && attempts < maxAttempts) ? Vec3.atCenterOf(blockpos) : null;
    }

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

    @Override
    public boolean canContinueToUse(){
        if (this.entity.canBeControlledByRider())
            return false;

        if (this.entity.getTarget() != null)
            return false;

        if (this.entity.getSleeping())
            return false;

        if (this.entity.getSitting())
            return false;

        return true;

    }
}