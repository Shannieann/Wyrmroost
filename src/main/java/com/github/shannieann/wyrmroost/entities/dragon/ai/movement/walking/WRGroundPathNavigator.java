package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class WRGroundPathNavigator extends GroundPathNavigation {
    WRDragonEntity entity;

    public WRGroundPathNavigator(WRDragonEntity entity)
    {
        super(entity, entity.level);
        this.entity = entity;
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new WRWalkNodeEvaluator(((WRDragonEntity)(mob)).speciesCanSwim());
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    //TODO: Analyze and improve
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

    //TODO: CORRECT AND IMPROVE
    @Override
    public boolean isStableDestination(BlockPos pPos) {
        if (!entity.speciesCanSwim()) {
            return this.level.getBlockState(pPos).isSolidRender(this.level, pPos);
        }
        BlockPos blockpos = pPos.below();
        return this.level.getFluidState(blockpos).is(FluidTags.WATER) || this.level.getBlockState(pPos).isSolidRender(this.level, pPos);
    }


}
