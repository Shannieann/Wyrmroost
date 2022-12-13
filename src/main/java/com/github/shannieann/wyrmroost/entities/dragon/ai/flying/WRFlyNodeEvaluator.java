package com.github.shannieann.wyrmroost.entities.dragon.ai.flying;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;

public class WRFlyNodeEvaluator extends FlyNodeEvaluator {


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
    /*
    private final Long2ObjectMap<BlockPathTypes> pathTypeByPosCache = new Long2ObjectOpenHashMap<>();

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter pBlockaccess, int pX, int pY, int pZ, Mob pEntityliving, int pXSize, int pYSize, int pZSize, boolean pCanBreakDoors, boolean pCanEnterDoors) {
        EnumSet<BlockPathTypes> enumset = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes blockpathtypes = BlockPathTypes.BLOCKED;
        BlockPos blockpos = pEntityliving.blockPosition();
        blockpathtypes = getWRBlockPathTypes(pBlockaccess, pX, pY, pZ, pXSize, pYSize, pZSize, pCanBreakDoors, pCanEnterDoors, enumset, blockpathtypes, blockpos);
        if (enumset.contains(BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        } else {
            BlockPathTypes blockpathtypes1 = BlockPathTypes.BLOCKED;

            for(BlockPathTypes blockpathtypes2 : enumset) {
                if (pEntityliving.getPathfindingMalus(blockpathtypes2) < 0.0F) {
                    return blockpathtypes2;
                }

                if (pEntityliving.getPathfindingMalus(blockpathtypes2) >= pEntityliving.getPathfindingMalus(blockpathtypes1)) {
                    blockpathtypes1 = blockpathtypes2;
                }
            }

            return blockpathtypes == BlockPathTypes.OPEN && pEntityliving.getPathfindingMalus(blockpathtypes1) == 0.0F ? BlockPathTypes.OPEN : blockpathtypes1;
        }
    }

    public BlockPathTypes getWRBlockPathTypes(BlockGetter pLevel, int pX, int pY, int pZ, int pXSize, int pYSize, int pZSize, boolean pCanOpenDoors, boolean pCanEnterDoors, EnumSet<BlockPathTypes> pNodeTypeEnum, BlockPathTypes pNodeType, BlockPos pPos) {
        for(int i = 0; i < pXSize; ++i) {
            for(int j = 0; j < pYSize; ++j) {
                for(int k = 0; k < pZSize; ++k) {
                    int l = i + pX;
                    int i1 = j + pY;
                    int j1 = k + pZ;
                    BlockPathTypes blockpathtypes = this.getWRBlockPathType(pLevel, l, i1, j1);
                    blockpathtypes = this.evaluateBlockPathType(pLevel, pCanOpenDoors, pCanEnterDoors, pPos, blockpathtypes);
                    if (i == 0 && j == 0 && k == 0) {
                        pNodeType = blockpathtypes;
                    }

                    pNodeTypeEnum.add(blockpathtypes);
                }
            }
        }

        return pNodeType;
    }

    //CLASS IDENTIY: ABC
    public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockPathTypes blockpathtypes = this.getRawBlockPathTypes(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ));
        if (blockpathtypes == BlockPathTypes.OPEN && pY >= pLevel.getMinBuildHeight() + 1) {
            BlockPathTypes blockpathtypes1 = this.getRawBlockPathTypes(pLevel, blockpos$mutableblockpos.set(pX, pY - 1, pZ));
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

        if (blockpathtypes == BlockPathTypes.WALKABLE || blockpathtypes == BlockPathTypes.OPEN) {
            blockpathtypes = checkNeighbourBlocks(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ), blockpathtypes);
        }

        return blockpathtypes;
    }

    public BlockPathTypes getRawWRBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        return getStaticBlockPathTypes(pLevel, new BlockPos.MutableBlockPos(pX, pY, pZ));
    }

    //CLASS IDENTIY ABC
    public static BlockPathTypes getStaticBlockPathTypes(BlockGetter pLevel, BlockPos.MutableBlockPos pPos) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        BlockPathTypes blockpathtypes = getRawBlockPathTypes(pLevel, pPos);
        //If our target block is OPEN and we are above the void, we have a position we could potentially walk to..
        if (blockpathtypes == BlockPathTypes.OPEN && j >= pLevel.getMinBuildHeight() + 1) {
            //Proceed to check the block below to ensure we can walk on it...
            BlockPathTypes blockpathtypes1 = getRawBlockPathTypes(pLevel, pPos.set(i, j - 1, k));
            //We set the block where we want to go, located at blockpathtypes's position, above blockPathTypes1's position, ensuring we can walk on it...
            //This depends on both blockPathType being OPEN (checked previously) and blockPathType1 (below) being BLOCKED (solid)
            //If both these conditions are met, we set it to WALKABLE.... else, we assume that there's an empty space beneath the target position, and we thus set it as OPEN...
            blockpathtypes = blockpathtypes1 != BlockPathTypes.BLOCKED ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            //If, however, we have fire below us, change the return to FIRE!
            if (blockpathtypes1 == BlockPathTypes.DAMAGE_FIRE) {
                blockpathtypes = BlockPathTypes.DAMAGE_FIRE;
            }
        }
        if (blockpathtypes == BlockPathTypes.WALKABLE || blockpathtypes == BlockPathTypes.OPEN) {
            //TODO: UNCHECKED DANGERS
            //NOTE: We are -NOT- checking neighboring blocks for dangers, such as cacti, etc, this is potentially dangerous!
            blockpathtypes = checkNeighbourBlocks(pLevel, pPos.set(i, j, k), blockpathtypes);
        }

        return blockpathtypes;
    }


    protected static BlockPathTypes getRawBlockPathTypes(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        if (blockstate.isAir()) {
            return BlockPathTypes.OPEN;
        }
        FluidState fluidstate = pLevel.getFluidState(pPos);

        if (fluidstate.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }
        if (fluidstate.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }
        if (isBurningBlock(blockstate)) {
            return BlockPathTypes.DAMAGE_FIRE;
        }
        //Anything else that blocks motion, slabs, stairs, fences, etc, we will just treat as a regular solid block...
        if (blockstate.getMaterial().blocksMotion()){
            return BlockPathTypes.BLOCKED;
        } else {
            //Anything else that does not block motions, berry bushes, plants, etc, we will just treat as air, we can walk right through it..
            return BlockPathTypes.OPEN;
        }

    }
}

     */