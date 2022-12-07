package com.github.shannieann.wyrmroost.entities.dragon.helpers.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.phys.Vec3;

/**
 * Im not actually sure if this is a good solution or not... but it seems to be working a little bit....
 */
public class BetterPathNavigator extends GroundPathNavigation
{
    public BetterPathNavigator(Mob entity)
    {
        super(entity, entity.level);
    }


    @Override
    protected void followThePath()
    {
        Vec3 pos = getTempMobPos();
        Vec3 pathPos = Vec3.atBottomCenterOf(path.getNextNodePos());

        double xDiff = Math.abs(pathPos.x() - mob.getX());
        double yDiff = Math.abs(pathPos.y() - mob.getY());
        double zDiff = Math.abs(pathPos.z() - mob.getZ());

        maxDistanceToWaypoint = ((int) (mob.getBbHeight() + 1f)) * 0.5f;
        boolean isWithinPathPoint = xDiff < maxDistanceToWaypoint && zDiff < maxDistanceToWaypoint && yDiff < 1;

        if (isWithinPathPoint || (mob.canCutCorner(path.getNextNode().type) && isPathLongEnough(pos)))
            path.advance();

        doStuckDetection(pos);
    }

    private boolean isPathLongEnough(Vec3 entityPosition)
    {
        if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) return false;

        Vec3 pathPos = Vec3.atBottomCenterOf(path.getNextNodePos());
        if (!entityPosition.closerThan(pathPos, maxDistanceToWaypoint)) return false;

        Vec3 nextPathPos = Vec3.atBottomCenterOf(path.getNodePos(path.getNextNodeIndex() + 1));
        Vec3 midOfNextAndCurrent = nextPathPos.subtract(pathPos);
        Vec3 midOfEntityAndCurrent = entityPosition.subtract(pathPos);
        return midOfNextAndCurrent.dot(midOfEntityAndCurrent) > 0;
    }
}
