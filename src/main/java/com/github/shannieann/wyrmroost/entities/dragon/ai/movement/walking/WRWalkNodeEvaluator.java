package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.*;

public class WRWalkNodeEvaluator extends WalkNodeEvaluator {

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter pLevel, BlockPos.MutableBlockPos pPos) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        BlockPathTypes blockpathtypes = getBlockPathTypeRaw(pLevel, pPos);
        if (blockpathtypes == BlockPathTypes.OPEN && j >= pLevel.getMinBuildHeight() + 1) {
            BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(pLevel, pPos.set(i, j - 1, k));
            blockpathtypes = blockpathtypes1 != BlockPathTypes.WALKABLE && blockpathtypes1 != BlockPathTypes.OPEN && blockpathtypes1 != BlockPathTypes.WATER && blockpathtypes1 != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            if (blockpathtypes1 == BlockPathTypes.DAMAGE_FIRE) {
                blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
            }

            if (blockpathtypes1 == BlockPathTypes.DAMAGE_CACTUS) {
                blockpathtypes = BlockPathTypes.DAMAGE_CACTUS;
            }

            if (blockpathtypes1 == BlockPathTypes.DAMAGE_OTHER) {
                blockpathtypes = BlockPathTypes.DAMAGE_OTHER;
            }

            if (blockpathtypes1 == BlockPathTypes.STICKY_HONEY) {
                blockpathtypes = BlockPathTypes.STICKY_HONEY;
            }

            if (blockpathtypes1 == BlockPathTypes.POWDER_SNOW) {
                blockpathtypes = BlockPathTypes.DANGER_POWDER_SNOW;
            }
        }

        /*
        if (blockpathtypes == BlockPathTypes.WALKABLE) {
            blockpathtypes = checkNeighbourBlocks(pLevel, pPos.set(i, j, k), blockpathtypes);
        }
        */

        return blockpathtypes;
    }
}

