package com.github.shannieann.wyrmroost.entity.dragon.ai.movement.ground;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class WRGroundNavigation extends GroundPathNavigation {
    public WRGroundNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }
    
    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator =((WRDragonEntity)mob).speciesCanSwim()? new AmphibiousNodeEvaluator(false) : new WalkNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

}
