package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class WRMoveToHomeGoal extends Goal
{
    private int time;
    private final WRDragonEntity dragon;
    private final Vec3 home;
    private final int TIME_UNTIL_TELEPORT = 600; // 30 seconds
    private final int TELEPORT_DISTANCE;

    public WRMoveToHomeGoal(WRDragonEntity creatureIn)
    {
        this.dragon = creatureIn;
        this.home = Vec3.atLowerCornerOf(dragon.getRestrictCenter());
        setFlags(EnumSet.of(Flag.MOVE));
        this.TELEPORT_DISTANCE = (int) (dragon.getRestrictRadius() * 2);
    }

    @Override
    public boolean canUse()
    {
        return dragon.isTame() && dragon.hasRestriction() && !dragon.isWithinRestriction() && !dragon.getSitting() && !dragon.isLeashed();
    }

    @Override
    public void start()
    {
        // Don't completely clear AI, might have left boundary while defending home
        dragon.getNavigation().stop();
    }

    @Override
    public void stop()
    {
        this.time = 0;
    }

    @Override
    public void tick() {
        time++;

        if (dragon.distanceToSqr(home) > TELEPORT_DISTANCE || time >= TIME_UNTIL_TELEPORT) {
            dragon.trySafeTeleport(dragon.getRestrictCenter().above());
        }
        else if (dragon.getNavigation().isDone()) {
            BlockPos movePos = RandomPos.generateRandomPosTowardDirection(dragon, (int) (dragon.getRestrictRadius() / 10), new Random(), new BlockPos(home));
            if (movePos != null) {
                dragon.getNavigation().moveTo(movePos.getX(), movePos.getY(), movePos.getY(), 1.1);
            }

        }
    }
}
