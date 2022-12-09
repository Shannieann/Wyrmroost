package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumSet;

public class WRWaterLeapGoal extends AnimatedGoal {

    private BlockPos pos;
    private final WRDragonEntity entity;
    private final String breachStartAnimation = "breach_start";
    private final String breachFlyAnimation = "breach_fly";
    private boolean hasBreached;
    private boolean hasFinishedBreaching;
    private int tickCounter;

    public WRWaterLeapGoal(WRDragonEntity entity)
    {
        super(entity);
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
        //Get the water surface position at which we are aiming...
        if (entity.level.getFluidState(this.pos = entity.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, this.entity.blockPosition()).below()).isEmpty())
            return false;
        //get the target position
        this.pos = pos.relative(entity.getDirection(), (int) ((pos.getY() - entity.getY()) * 0.5d));
        if (pos.distSqr(new Vec3i(entity.position().x,entity.position().y,entity.position().z)) > 100) {
            return false;
        }
        //TODO: Do not use always...
        //return entity.getRandom().nextDouble() < 0.001;
        return this.entity.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.5d);
    }

    @Override
    public boolean canContinueToUse(){
        if (entity.canBeControlledByRider()) {
            return false;
        }
        if (tickCounter >= 20) {
            stop();
        }
        return true;
    }

    @Override
    public void start()
    {
        super.start(breachStartAnimation, 1, 10);
    }

    @Override
    public void tick()
    {
        //If it has not yet jumped out of the water, and somehow its navigation stops, cancel the goal...
        if (!hasBreached && !hasFinishedBreaching) {
            if (!this.entity.level.getBlockState(this.pos).is(Blocks.AIR)) {
                if (this.entity.getNavigation().isStuck() || this.entity.getNavigation().isDone()) {
                    stop();
                } else {
                    //If it's still navigating to the start position, keep playing breach start
                    super.start(breachStartAnimation, 1, 10);
                }
            } else {
                super.start(breachFlyAnimation, 1, 10);
                    hasBreached = true;
            }
        }
        if (hasBreached && !hasFinishedBreaching && !this.entity.level.getFluidState(this.pos).is(FluidTags.WATER)) {
            super.start(breachFlyAnimation, 1, 10);
        } else {
            hasFinishedBreaching = true;
        }
        if (hasFinishedBreaching) {
            tickCounter++;
        }

    }

    @Override
    public void stop()
    {
        entity.clearAI();
        entity.getNavigation().stop();
        super.stop();
    }
}