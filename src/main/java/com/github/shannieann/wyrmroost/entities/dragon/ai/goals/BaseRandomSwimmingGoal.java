package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class BaseRandomSwimmingGoal extends RandomStrollGoal {
    public BaseRandomSwimmingGoal(PathfinderMob p_25753_, double p_25754_, int p_25755_) {
        super(p_25753_, p_25754_, p_25755_);
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 position =  BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7);
        if (position != null) {
            this.mob.level.setBlock(new BlockPos(position), Blocks.REDSTONE_BLOCK.defaultBlockState(),2);
        }
        return position;
    }

}
