package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WRMoveToLandToSleepGoal extends Goal
{
    private Vec3 pos;
    private WRDragonEntity entity;
    private boolean nocturnal;
    private boolean isSilverGlider;

    public WRMoveToLandToSleepGoal(WRDragonEntity entity)
    {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.nocturnal = false;
        this.isSilverGlider = entity instanceof EntitySilverGlider;
    }

    public WRMoveToLandToSleepGoal(WRDragonEntity entity, boolean nocturnal)
    {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.nocturnal = nocturnal;
        this.isSilverGlider = entity instanceof EntitySilverGlider;
    }

    @Override
    public boolean canUse()
    {
        // If any of the below, can't use
        if (entity == null || entity.getRandom().nextDouble() > 0.05
            || entity.isOnGround() || entity.isPassenger() || entity.isRidingPlayer() || entity.getTarget() != null
            || (nocturnal && entity.level.getDayTime() > 14000 && entity.level.getDayTime() < 23500) // nocturnal during night
            || (!nocturnal && (entity.level.getDayTime() < 14000 || entity.level.getDayTime() > 23500)) // diurnal during day
        ) {
            return false;
        }

        Vec3 randomPos = LandRandomPos.getPos(entity, 30, 30);

        if (isSilverGlider) {
            // Make 10 attempts to find a random land pos on the beach (sand). Failing that, just get a random land pos
            for (int i = 0; i < 10; i++) {
                if (randomPos != null && entity.level.getBlockState(new BlockPos(randomPos.x, randomPos.y, randomPos.z))
                        .getBlock() == Blocks.SAND) {
                    this.pos = randomPos;
                    return true;
                }
                randomPos = LandRandomPos.getPos(entity, 30, 30);
            }
        }
        this.pos = randomPos;
        return (randomPos != null);
    }

    @Override
    public boolean canContinueToUse()
    {
        return this.entity.distanceToSqr(this.pos.x, this.pos.y, this.pos.z) > 2 && entity.getTarget() == null && !entity.isPassenger() && !entity.isRidingPlayer();
    }

    @Override
    public void tick()
    {
        if (entity.getAltitude() > 20) {
            entity.setDeltaMovement(0, -0.3, 0);
        }
        if (entity.getNavigation().isDone()) {
            entity.getNavigation().moveTo(pos.x, pos.y, pos.z, 0.9);
        }
        entity.getLookControl().setLookAt(pos.x, pos.y, pos.z);
    }

    @Override
    public void stop() {
        if (this.entity.distanceToSqr(this.pos.x, this.pos.y, this.pos.z) > 2 && this.entity.isOnGround()) {
            this.entity.setNavigator(WRDragonEntity.NavigationType.GROUND);
            this.entity.setSleeping(true);
        }
        this.entity = null;
        this.pos = null;
    }
}
