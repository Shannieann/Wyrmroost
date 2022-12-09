package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumSet;

public class WRWaterLeapGoal extends Goal {

    private BlockPos pos;
    private final WRDragonEntity entity;

    public WRWaterLeapGoal(WRDragonEntity entity)
    {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        if (entity.isInSittingPose()) {
            return false;
        }
        if (entity.canBeControlledByRider()) {
            return false;
        }
        if (!entity.isUnderWater()) {
            return false;
        }
        //TODO: Improve logic
        //Get the water surface position at which we are aiming...
        if (entity.level.getFluidState(this.pos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, this.entity.blockPosition()).below()).isEmpty())
            return false;
        //TODO: Improve logic
        if (pos.getY() <= 0) {
            return false;
        }
        return true;

        //return entity.getRandom().nextDouble() < 0.001;
    }

    @Override
    public boolean canContinueToUse()
    {
        return true;
    }

    @Override
    public void start()
    {
        entity.getNavigation().stop();
        this.pos = pos.relative(entity.getDirection(), (int) ((pos.getY() - entity.getY()) * 0.5d));
        this.entity.level.setBlock(pos, Blocks.PINK_CONCRETE.defaultBlockState(),2);
        this.entity.setNoAi(true);
    }

    @Override
    public void tick()
    {
        //getMoveControl().setWantedPosition(pos.getX(), pos.getY(), pos.getZ(), 1.2d);
    }

    @Override
    public void stop()
    {
        pos = null;
        //clearAI();
    }
}