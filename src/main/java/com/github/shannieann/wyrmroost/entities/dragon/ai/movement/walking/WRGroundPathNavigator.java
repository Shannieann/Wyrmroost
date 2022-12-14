package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
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

    @Override
    protected void followThePath() {
        Vec3 vec3 = this.getTempMobPos();
        float distancemodifier = 0.75F;
        this.maxDistanceToWaypoint = this.mob.getBbWidth() * distancemodifier;
        Vec3i vec3i = this.path.getNextNodePos();
        double d0 = Math.abs(this.mob.getX() - ((double)vec3i.getX() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
        double d1 = Math.abs(this.mob.getY() - (double)vec3i.getY());
        double d2 = Math.abs(this.mob.getZ() - ((double)vec3i.getZ() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
        boolean flag = d0 <= (double)this.maxDistanceToWaypoint && d2 <= (double)this.maxDistanceToWaypoint && d1 < 1.0D; //Forge: Fix MC-94054
        if (flag || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }

        this.doStuckDetection(vec3);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 pVec) {
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        } else {
            Vec3 vec3 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!pVec.closerThan(vec3, 2.0D)) {
                return false;
            } else if (this.canMoveDirectly(pVec, this.path.getNextEntityPos(this.mob))) {
                return true;
            } else {
                Vec3 vec31 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
                Vec3 vec32 = vec31.subtract(vec3);
                Vec3 vec33 = pVec.subtract(vec3);
                return vec32.dot(vec33) > 0.0D;
            }
        }
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

    @Override
    public boolean isStableDestination(BlockPos pPos) {
        if (!entity.speciesCanSwim()) {
            return this.level.getBlockState(pPos).isSolidRender(this.level, pPos);
        }
        BlockPos blockpos = pPos.below();
        return this.level.getFluidState(blockpos).is(FluidTags.WATER) || this.level.getBlockState(pPos).isSolidRender(this.level, pPos);
    }
}
