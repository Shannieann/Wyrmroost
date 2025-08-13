package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

// When an otherwise aggressive dragon is at low health and wants to run away
public class WRRunWhenLosingGoal extends Goal {

    private final float healthPercent;
    private final WRDragonEntity dragon;
    private final int maxDistance;
    private final float speed;

    private Path path;

    public WRRunWhenLosingGoal(WRDragonEntity pDragon, float healthPercent, int pMaxDistance, float speed) {
        this.healthPercent = healthPercent;
        this.dragon = pDragon;
        this.speed = speed;
        this.maxDistance = pMaxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.dragon.getHealth()/this.dragon.getMaxHealth() > healthPercent
            || this.dragon.isImmobile()
            || this.dragon.isRiding()
            || this.dragon.getPassengers().size() > 0)
        {
            return false;
        }
        LivingEntity avoidEntity = dragon.getLastHurtByMob();

        if (avoidEntity == null){
            return false;
        }

        // If it's tamed and the owner is the one hitting it, don't run
        if (this.dragon.isTame() && avoidEntity.getUUID() == this.dragon.getOwnerUUID()) {
            return false;
        }

        Vec3 posAway = DefaultRandomPos.getPosAway(this.dragon, maxDistance, 7, avoidEntity.position());

        if (posAway == null) {
                return false;
            } else if (avoidEntity.distanceToSqr(posAway.x, posAway.y, posAway.z) < avoidEntity.distanceToSqr(this.dragon)) {
                return false;
            } else {
                this.path = dragon.getNavigation().createPath(posAway.x, posAway.y, posAway.z, 0);
                return this.path != null;
        }
    }

    @Override
    public void start() {
        dragon.getNavigation().moveTo(this.path, this.speed);
    }
}