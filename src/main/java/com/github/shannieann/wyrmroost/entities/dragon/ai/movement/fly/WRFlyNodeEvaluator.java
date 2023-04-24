package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.fly;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;

public class WRFlyNodeEvaluator extends FlyNodeEvaluator {

    //TODO: Further optimization, do not check for trivialities such as fences, doors, walls, etc.
    @Override
    public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockPathTypes blockpathtypes = getBlockPathTypeRaw(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ));
        if (blockpathtypes == BlockPathTypes.OPEN && pY >= pLevel.getMinBuildHeight() + 1) {
            BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(pLevel, blockpos$mutableblockpos.set(pX, pY - 1, pZ));
            if (blockpathtypes1 != BlockPathTypes.DAMAGE_FIRE && blockpathtypes1 != BlockPathTypes.LAVA) {
                if (blockpathtypes1 == BlockPathTypes.DAMAGE_CACTUS) {
                    blockpathtypes = BlockPathTypes.DAMAGE_CACTUS;
                } else if (blockpathtypes1 == BlockPathTypes.DAMAGE_OTHER) {
                    blockpathtypes = BlockPathTypes.DAMAGE_OTHER;
                } else if (blockpathtypes1 == BlockPathTypes.COCOA) {
                    blockpathtypes = BlockPathTypes.COCOA;
                } else if (blockpathtypes1 == BlockPathTypes.FENCE) {
                    blockpathtypes = BlockPathTypes.FENCE;
                } else {
                    blockpathtypes = blockpathtypes1 != BlockPathTypes.WALKABLE && blockpathtypes1 != BlockPathTypes.OPEN && blockpathtypes1 != BlockPathTypes.WATER ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
                }
            } else {
                blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
            }
        }
        return blockpathtypes;
    }
}