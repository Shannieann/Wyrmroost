package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
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

/**
 * Basically another target goal that targets things within the home
 */
public class DefendHomeGoal extends TargetGoal
{
    private static final Predicate<LivingEntity> FILTER = e -> e instanceof Enemy && !(e instanceof Creeper) && !e.getName().getString().equalsIgnoreCase("Ignore Me");
    private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().selector(FILTER);
    private final TameableDragonEntity defender;
    public DefendHomeGoal(TameableDragonEntity defender)
    {
        super(defender, false, false);
        this.defender = defender;
        setFlags(EnumSet.of(Goal.Flag.TARGET));
    }


    @Override
    public boolean canUse()
    {
        if (defender.getHealth() <= defender.getMaxHealth() * 0.25) return false;
        if (!defender.hasRestriction()) return false;
        return defender.getRandom().nextDouble() < 0.2 && (targetMob = findPotentialTarget()) != null;
    }

    @Override
    public void start()
    {
        super.start();

        // alert others!
        for (Mob mob : defender.level.getEntitiesOfClass(Mob.class, defender.getBoundingBox().inflate(WRConfig.HOME_RADIUS.get()), defender::isAlliedTo))
            mob.setTarget(targetMob);
    }

    @Override
    public boolean canContinueToUse()
    {
        return defender.isWithinRestriction(targetMob.blockPosition()) && super.canContinueToUse();
    }

    @Override
    protected double getFollowDistance()
    {
        return defender.getRestrictRadius();
    }

    public LivingEntity findPotentialTarget()
    {
        return defender.level.getNearestEntity(LivingEntity.class,
                CONDITIONS,
                defender,
                defender.getX(),
                defender.getEyeY(),
                defender.getZ(),
                new AABB(defender.getRestrictCenter()).inflate(WRConfig.HOME_RADIUS.get()));
    }
}
