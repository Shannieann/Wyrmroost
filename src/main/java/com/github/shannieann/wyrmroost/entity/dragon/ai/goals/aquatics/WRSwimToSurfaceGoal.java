package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.aquatics;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import java.util.EnumSet;

public class WRSwimToSurfaceGoal extends Goal {

    private final WRDragonEntity entity;
    private BlockPos surfacePos;
    private double probability;

    // TODO: Check for caustic swamp nasty water, light water, etc?
    public WRSwimToSurfaceGoal(WRDragonEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.JUMP));
        this.probability = 0.03d;
    }

    public WRSwimToSurfaceGoal(WRDragonEntity entity, double probability) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.JUMP));
        this.probability = probability;
    }

    @Override
    public boolean canUse() { // Make it alwas trigger if almost out of breath, otherwise use provided probability
        return entity.getTarget() == null && ((entity.getAirSupply() < 60 && !entity.canBreatheUnderwater())
                || (entity.isInWater() && entity.getRandom().nextDouble() < probability));
    }

    @Override
    public void start() {
        BlockPos.MutableBlockPos surfacePos = entity.blockPosition().mutable();
        while (entity.level.getBlockState(surfacePos).getBlock() != Blocks.AIR) {
            surfacePos.move(Direction.UP);
            if (surfacePos.getY() > entity.level.getMaxBuildHeight()) {
                surfacePos = null;
                return;
            }
        }
        this.surfacePos = surfacePos.offset(entity.getRandom().nextInt(8) - 4, 1, entity.getRandom().nextInt(8) - 4).immutable();
    }

    @Override
    public boolean canContinueToUse() {
        return surfacePos != null && ((entity.getAirSupply() < 60 && !entity.canBreatheUnderwater()) || entity.isInWater()) && entity.getTarget() == null;
    }

    @Override
    public void tick() {
        System.out.println("WRSwimToSurfaceGoal tick");
        if (surfacePos != null && entity.getNavigation().isDone()) {
            entity.getNavigation().moveTo(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ(), 1.0F);
        }
        if (entity.getDepth() < 2.0F && entity.getAltitude() < 0.5f && entity.getDeltaMovement().y() < 0.2f) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.1f, 0));
        }
        if (surfacePos != null && entity.distanceToSqr(surfacePos.getX(), surfacePos.getY(), surfacePos.getZ()) < 1.5F) {
            System.out.println("WRSwimToSurfaceGoal reached surface pos");
            surfacePos = null;
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        this.surfacePos = null;
    }
}