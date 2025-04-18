package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

public class MoveToHomeGoal extends Goal
{
    private int time;
    private final WRDragonEntity dragon;

    public MoveToHomeGoal(WRDragonEntity creatureIn)
    {
        this.dragon = creatureIn;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        return !dragon.isWithinRestriction();
    }

    @Override
    public void start()
    {
        dragon.clearAI();
    }

    @Override
    public void stop()
    {
        this.time = 0;
    }

    @Override
    public void tick() {
        int sq = 20 * 20;
        Vec3 home = Vec3.atLowerCornerOf(dragon.getRestrictCenter());
        final int TIME_UNTIL_TELEPORT = 600; // 30 seconds

        time++;
        if (dragon.distanceToSqr(home) > sq + 35 || time >= TIME_UNTIL_TELEPORT)
            dragon.trySafeTeleport(dragon.getRestrictCenter().above());
        else {
            BlockPos movePos;
            if (dragon.getNavigation().isDone() && (movePos = RandomPos.generateRandomPosTowardDirection(dragon, 20, new Random(), new BlockPos(home))) != null)
                dragon.getNavigation().moveTo(movePos.getX(), movePos.getY(), movePos.getY(), 1.1);
        }
    }
}
