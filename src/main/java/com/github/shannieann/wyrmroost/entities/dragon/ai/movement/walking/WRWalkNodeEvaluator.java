package com.github.shannieann.wyrmroost.entities.dragon.ai.movement.walking;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entities.dragon.ai.goals.aquatics.WRReturnToWaterGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.*;


public class WRWalkNodeEvaluator extends WalkNodeEvaluator {
    //TODO: Check all methods in super class and perhaps override and optimize more

    private boolean amphibious;

    public WRWalkNodeEvaluator(boolean amphibious) {
        this.amphibious = amphibious;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        BlockPathTypes blockpathtypes = getBlockPathTypeRaw(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ));
        //It can swim but it's on land..
        //Perform extra checks to see if it should do amphibious navigation: Only if it's trying to get to water
        //Only do the extra amphibious block checks if the current node is WATER
        if (((WRDragonEntity)mob).speciesCanSwim() && mob.goalSelector.getRunningGoals().anyMatch(g -> (g.getGoal() instanceof WRReturnToWaterGoal)) && (blockpathtypes == BlockPathTypes.WATER)) {
            for (Direction direction : Direction.values()) {
                BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(pLevel, blockpos$mutableblockpos.set(pX, pY, pZ).move(direction));
                if (blockpathtypes1 == BlockPathTypes.BLOCKED) {
                    return BlockPathTypes.WATER_BORDER;
                }
            }
            return BlockPathTypes.WATER;
        }  else return getBlockPathTypeStatic(pLevel, blockpos$mutableblockpos);
    }

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter pLevel, BlockPos.MutableBlockPos pPos) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        //We are checking two blocks:
        //blockpathtypes (ABOVE)
        //blockpathtypes1 (BELOW)

        //We check the block where we want to go, located at blockPathType's position, above blockPathTypes1's position, ensuring we can walk on it...
        BlockPathTypes blockpathtypes = getBlockPathTypeRaw(pLevel, pPos);
        //If our target block is OPEN and we are above the void, we have a position we could potentially walk to...
        //There are other options here for our target position: It could be WATER, it could be LAVA...
        //If it's any one of these we will pass it back directly as-is, to check against the Malus
        //Meaning, we might be able to walk -in- LAVA, or WATER, etc, depending on the Malus
        //If it is indeed OPEN however, we must check the position beneath, to determine if it's indeed OPEN or WALKABLE
        if (blockpathtypes == BlockPathTypes.OPEN && j >= pLevel.getMinBuildHeight() + 1) {
            //Proceed to check the block below to ensure we can walk on top of it...
            BlockPathTypes blockpathtypes1 = getBlockPathTypeRaw(pLevel, pPos.set(i, j - 1, k));
            //blockPathTypes1, the block below, must not be "open" in any way for it to be a solid "base" for walking on...
            //If this is the case, then the block above, our target position, is also OPEN...

            blockpathtypes =
                    blockpathtypes1 != BlockPathTypes.WALKABLE &&
                    blockpathtypes1 != BlockPathTypes.OPEN &&
                    blockpathtypes1 != BlockPathTypes.WATER &&
                    blockpathtypes1 != BlockPathTypes.LAVA ?
                    BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
        }
        return blockpathtypes;
    }

    protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        FluidState fluidstate = pLevel.getFluidState(pPos);
        //We are only interested in checking for a few possible BlockTypes that are of interest to us...
        //All else, fences, walls, doors, etc, we can treat as SOLID
        if (fluidstate.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }
        if (fluidstate.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }
        //Anything else that blocks motion, slabs, stairs, fences, etc, we will just treat as a regular solid block...
        if (blockstate.getMaterial().blocksMotion()){
            return BlockPathTypes.BLOCKED;
        } else {
            //Anything else that does not block motions, berry bushes, plants, etc, we will just treat as air, we can walk right through it..
            return BlockPathTypes.OPEN;
        }
    }


    @Override
    public int getNeighbors(Node[] p_164676_, Node p_164677_) {
        int i = super.getNeighbors(p_164676_, p_164677_);
        if (this.isAmphibious()) {
            BlockPathTypes blockpathtypes = this.getCachedBlockType(this.mob, p_164677_.x, p_164677_.y + 1, p_164677_.z);
            BlockPathTypes blockpathtypes1 = this.getCachedBlockType(this.mob, p_164677_.x, p_164677_.y, p_164677_.z);
            int j;
            if (this.mob.getPathfindingMalus(blockpathtypes) >= 0.0F) {
                j = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
            } else {
                j = 0;
            }

            double d0 = this.getFloorLevel(new BlockPos(p_164677_.x, p_164677_.y, p_164677_.z));
            Node node = this.findAcceptedNode(p_164677_.x, p_164677_.y + 1, p_164677_.z, Math.max(0, j - 1), d0, Direction.UP, blockpathtypes1);
            Node node1 = this.findAcceptedNode(p_164677_.x, p_164677_.y - 1, p_164677_.z, j, d0, Direction.DOWN, blockpathtypes1);
            if (this.isNeighborValid(node, p_164677_)) {
                p_164676_[i++] = node;
            }

            if (this.isNeighborValid(node1, p_164677_)) {
                p_164676_[i++] = node1;
            }
        }
        return i;
    }

    @Override
    public boolean isAmphibious(){
        return this.amphibious;
    }
}

