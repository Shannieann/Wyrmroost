package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

// When a dragon is at low health and wants to run away
public class WRRunWhenLosingGoal extends AvoidEntityGoal<LivingEntity> {

    private final TargetingConditions runAwayTargeting;
    private final float healthPercent;
    private final WRDragonEntity dragon;
    private final float chanceToRun;
    private final double walkSpeedModifier;

    public WRRunWhenLosingGoal(WRDragonEntity pDragon, float healthPercent, float chanceToRun, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pDragon, LivingEntity.class, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
        this.healthPercent = healthPercent;
        this.chanceToRun = chanceToRun;
        this.dragon = pDragon;
        this.walkSpeedModifier = pWalkSpeedModifier;
        //super(dragon, LivingEntity.class, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
        this.runAwayTargeting = TargetingConditions.forCombat().range(pMaxDistance).selector(this.predicateOnAvoidEntity.and(avoidPredicate));
    }

    @Override
    public void start() {
        dragon.getNavigation().moveTo(this.path, this.walkSpeedModifier);
    }

    @Override
    public boolean canUse() {
        if (this.dragon.getHealth()/this.dragon.getMaxHealth() > healthPercent) return false;
        //if (dragon.getRandom().nextFloat() > chanceToRun) return false;
        // TODO make chances work
        // Looking back, this logic doesn't really make sense.
        // Like, they could just stop running if the chance is low (especially for royal reds)

        LivingEntity defaultAvoidEntity = this.dragon.level.getNearestEntity(this.dragon.level.getEntitiesOfClass(this.avoidClass, this.dragon.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist), (p_148078_) -> {
            return true;
        }), this.runAwayTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());

        this.toAvoid = dragon.getLastHurtByMob() != null? dragon.getLastHurtByMob() : defaultAvoidEntity;
        if (this.toAvoid == null){return false;}
        if (this.dragon.isTame() && this.toAvoid.getUUID() == this.dragon.getOwnerUUID()) return false; // in short, if its tamed and the owner is the one hitting it, don't run
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.dragon, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.dragon)) {
                return false;
            } else {
                this.path = dragon.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }

}