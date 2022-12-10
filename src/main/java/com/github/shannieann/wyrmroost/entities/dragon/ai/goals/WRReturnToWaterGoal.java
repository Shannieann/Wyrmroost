package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;


public class WRReturnToWaterGoal extends Goal {
    private final WRDragonEntity entity;
    private final float speed;
    private BlockPos targetPos;
    private int tickCounter;

    public WRReturnToWaterGoal(WRDragonEntity entity, float speedIn) {
        this.entity = entity;
        this.speed = speedIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }


    @Override
    public boolean canUse() {
        //canUse if it's on ground, outside of the water...
        if (this.entity.isOnGround() && !this.entity.isInWater()){
            return false;
        }
        if (this.entity.canBeControlledByRider()) {
            return false;
        }
        //canUse if it's in 1-block-deep water
        if (this.entity.isInWater() && this.entity.level.getBlockState(this.entity.blockPosition().below()).canOcclude() && this.entity.level.getBlockState(this.entity.blockPosition().above()).is(Blocks.AIR)) {
            targetPos = getPosition();
            return targetPos != null;
        }
        return false;
    }

    @Nullable
    protected BlockPos getPosition() {
        Vec3 targetVec =  BehaviorUtils.getRandomSwimmablePos(this.entity, 32,8);
        if (targetVec != null) {
            BlockPos blockPos = new BlockPos(targetVec);
            BlockPos heightMapPos = this.entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos);
            if (this.entity.level.getFluidState(heightMapPos.below()).is(FluidTags.WATER) && this.entity.level.getFluidState(heightMapPos).is(FluidTags.WATER)) {
                return heightMapPos.below();
            }
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.entity.getNavigation().isDone()){
            return false;
        }
        if (this.entity.canBeControlledByRider()) {
            return false;
        }
        if (this.entity.level.getFluidState(this.entity.blockPosition().below()).is(FluidTags.WATER) && this.entity.level.getFluidState(this.entity.blockPosition()).is(FluidTags.WATER)) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.entity.level.setBlock(targetPos,Blocks.REDSTONE_BLOCK.defaultBlockState(),2);
        //this.entity.getNavigation().moveTo(targetPos.getX(),targetPos.getY(),targetPos.getZ(), this.speed);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }
}

