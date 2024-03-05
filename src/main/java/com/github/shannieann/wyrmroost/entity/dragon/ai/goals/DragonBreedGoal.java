package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;

public class DragonBreedGoal extends Goal
{
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected final WRDragonEntity dragon;
    //protected final EntityPredicate predicate;
    protected WRDragonEntity targetMate;
    protected int spawnBabyDelay;

    public DragonBreedGoal(WRDragonEntity dragon)
    {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.dragon = dragon;
        /*this.predicate = new EntityPredicate()
                .range(dragon.getBbWidth() * 8)
                .allowInvulnerable()
                .allowSameTeam()
                .allowUnseeable()
                .selector(e -> ((Animal) e).canMate(dragon));*/
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse()
    {
        if (!dragon.isInLove()) return false;
        int breedLimit = WRConfig.getBreedLimitFor(dragon.getType());
        if (breedLimit > 0 && dragon.breedCount >= breedLimit)
        {
            dragon.resetLove();
            return false;
        }
        return (targetMate = getNearbyMate()) != null;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse()
    {
        return targetMate.isAlive() && targetMate.isInLove() && dragon.isInLove() && spawnBabyDelay < 60;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop()
    {
        targetMate = null;
        spawnBabyDelay = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick()
    {
        dragon.getLookControl().setLookAt(targetMate, 10f, dragon.getYawRotationSpeed());
        dragon.getNavigation().moveTo(targetMate, 1);
        if (++spawnBabyDelay >= 60 && dragon.distanceTo(targetMate) < dragon.getBbWidth() * 2)
            dragon.spawnChildFromBreeding((ServerLevel) dragon.level, targetMate);
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    @Nullable
    protected WRDragonEntity getNearbyMate()
    {
        return dragon.level.getNearbyEntities(dragon.getClass(), PARTNER_TARGETING, dragon, dragon.getBoundingBox().inflate(dragon.getBbWidth() * 8))
                .stream()
                .min(Comparator.comparingDouble(dragon::distanceToSqr)).orElse(null);
    }
}
