package com.github.shannieann.wyrmroost.entities.dragon.ai;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public class WRSwimmingNavigator extends WaterBoundPathNavigation{

    public WRSwimmingNavigator(WRDragonEntity entity){
        super(entity,entity.level);
    }

    @Override
    protected PathFinder createPathFinder(int range){
        return new PathFinder(nodeEvaluator=new SwimNodeEvaluator(true),range);
    }

    @Override
    public boolean isStableDestination(BlockPos pos){
        return!level.getBlockState(pos.below()).isAir();
    }

    @Override
    protected boolean canUpdatePath(){
        return true;
    }
}