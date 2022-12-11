package com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class WRRandomSwimmingGoal extends Goal {

    protected final WRDragonEntity entity;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    protected int executionChance;
    protected int horizontalDistance;
    protected int verticalDistance;

    public WRRandomSwimmingGoal(WRDragonEntity entity, double speedIn, int chance, int horizontalDistance, int verticalDistance) {
        this.entity = entity;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.horizontalDistance = horizontalDistance;
        this.verticalDistance = verticalDistance;
    }

    @Override
    public boolean canUse() {
        if (this.entity.isVehicle()) {
            return false;
        }

        if (this.entity.getTarget() != null){
            return false;
        }

        if (!this.entity.isUsingSwimmingNavigator()) {
            return false;
        }

        else {
            Vec3 targetPosition = this.getPosition();
            if (targetPosition == null) {
                return false;
            } else {
                this.x = targetPosition.x;
                this.y = targetPosition.y;
                this.z = targetPosition.z;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        /*Vec3 targetVec =  BehaviorUtils.getRandomSwimmablePos(this.entity, horizontalDistance, verticalDistance);
        if (targetVec != null) {
            Vec3 entityPos = this.entity.position();
            double distance = entityPos.subtract(targetVec).length();

            if (distance < 10) {
                return null;
            }
            //TODO: REMOVE DEBUGGING POSITION
            //return (targetVec);
        }
        return null;

         */
        return new Vec3 (32,86,0);

    }

    @Override
    public boolean canContinueToUse() {
        //TODO: TEST distance + avoid loops
        if (this.entity.distanceToSqr(this.x,this.y,this.z) < 75) {
            return false;
        }
        if (!this.entity.isUsingSwimmingNavigator()) {
               return false;
        }
        if (this.entity.getTarget() != null){
            return false;
        }
        return !this.entity.getNavigation().isDone() && !this.entity.isVehicle();
    }
    @Override
    public void start() {
        this.entity.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick(){
        if (entity.getNavigation().getPath() != null){
            List<Node> nodeList = entity.getNavigation().getPath().nodes;
            if (!nodeList.isEmpty()) {
                for (int i = 0; i<nodeList.size(); i++) {
                    entity.level.addAlwaysVisibleParticle(ParticleTypes.END_ROD, nodeList.get(i).x,nodeList.get(i).y,nodeList.get(i).z,0,0,0);
                }
            }
        }
        super.tick();
    }
}