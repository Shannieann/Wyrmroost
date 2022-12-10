package com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

// When a dragon is at low health and wants to run away
public class WRRunWhenLosingGoal extends AvoidEntityGoal<LivingEntity> {

    private final TargetingConditions runAwayTargeting;
    private final float healthPercent;
    private final WRDragonEntity dragon;
    private final float chanceToRun;
    private final double walkSpeedModifier;

    public WRRunWhenLosingGoal(WRDragonEntity pMob, float healthPercent, float chanceToRun, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pMob, LivingEntity.class, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
        this.healthPercent = healthPercent;
        this.chanceToRun = chanceToRun;
        this.dragon = (WRDragonEntity) mob;
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
        if (dragon.getRandom().nextFloat() > chanceToRun) return false;



        this.toAvoid = dragon.getLastHurtByMob();
        System.out.println(toAvoid);

        if (this.toAvoid == null) return false;
        if (this.dragon.isTame() && this.toAvoid.getUUID() == this.dragon.getOwnerUUID()) return false; // in short, if its tamed and the owner is the one hitting it, don't run

            Vec3 vec3 = DefaultRandomPos.getPosAway(this.dragon, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.dragon)) {
                return false;
            } else {
                this.path = dragon.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                System.out.println(this.path != null);
                return this.path != null;
            }
        }

}