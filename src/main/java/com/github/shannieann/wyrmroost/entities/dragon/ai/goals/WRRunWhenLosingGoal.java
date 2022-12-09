package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

// When a dragon is at low health and wants to run away
public class WRRunWhenLosingGoal extends AvoidEntityGoal<LivingEntity> {

    private final TargetingConditions runAwayTargeting;
    private final float healthPercent;

    public WRRunWhenLosingGoal(WRDragonEntity pMob, float healthPercent, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pMob, null, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
        this.healthPercent = healthPercent;
        this.runAwayTargeting = TargetingConditions.forCombat().range(pMaxDistance).selector(this.predicateOnAvoidEntity.and(avoidPredicate));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getHealth()/this.mob.getMaxHealth() > healthPercent) return false;
        if (this.mob.getRandom().nextInt(999) > 0) return false;



        this.toAvoid = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass((Class)this.mob.getLastHurtByMob().getType().getClass(), this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist), (p_148078_) -> {
            return true;
        }), this.runAwayTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
    }
}
