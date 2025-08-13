package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.function.Predicate;

public class WRDefendHomeGoal extends TargetGoal {

    private static final Predicate<LivingEntity> FILTER = e -> e instanceof Enemy && !(e instanceof Creeper) && !e.getName().getString().equalsIgnoreCase("Ignore Me");
    private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().selector(FILTER);
    private final WRDragonEntity defender;
    private LivingEntity targetMob;

    public WRDefendHomeGoal(WRDragonEntity defender)
    {
        super(defender, false, false);
        this.defender = defender;
        setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetMob = null;
    }

    @Override
    public boolean canUse() {
        if (!defender.isTame() || !defender.hasRestriction() || defender.getHealth() <= defender.getMaxHealth() * 0.25) {
            return false;
        }
        this.targetMob = findPotentialTarget();
        return this.targetMob != null;
    }

    @Override
    public void start() {
        super.start();
        for (Mob mob : defender.level.getEntitiesOfClass(Mob.class, defender.getBoundingBox().inflate(defender.getRestrictRadius()), defender::isAlliedTo)) {
            mob.setTarget(this.targetMob);
        }
    }

    @Override
    // Used in TargetGoal parent class
    protected double getFollowDistance() {
        return defender.getRestrictRadius();
    }

    public LivingEntity findPotentialTarget() {
        return defender.level.getNearestEntity(
            LivingEntity.class,
            CONDITIONS,
            defender,
            defender.getX(),
            defender.getEyeY(),
            defender.getZ(),
            new AABB(defender.getRestrictCenter()).inflate(defender.getRestrictRadius())
        );
    }

}
