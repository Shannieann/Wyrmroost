package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;

public class WRDragonBreedGoal<T extends WRDragonEntity & IBreedable> extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected final WRDragonEntity dragon;
    protected final IBreedable breedable;

    protected WRDragonEntity targetMate;
    protected int spawnBabyDelay;

    public WRDragonBreedGoal(T dragon) {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.dragon = dragon;
        this.breedable = dragon;
    }

    public boolean canUse() {
        if (!dragon.isInLove()) {
            return false;
        }
        targetMate = getNearbyMate();
        if (targetMate == null){
            return false;
        }

        return true;
    }

    public boolean canContinueToUse()
    {
        return targetMate.isAlive() && targetMate.isInLove() && dragon.isInLove() && spawnBabyDelay < 60;
    }

    public void stop() {
        targetMate = null;
        spawnBabyDelay = 0;
    }

    public void tick() {
        dragon.getLookControl().setLookAt(targetMate, 10f, dragon.getYawRotationSpeed());
        dragon.getNavigation().moveTo(targetMate, 1);
        if (++spawnBabyDelay >= 60 && dragon.distanceTo(targetMate) < dragon.getBbWidth() * 2)
            dragon.spawnDragonEgg(dragon, breedable.hatchTime());
    }

    @Nullable
    protected WRDragonEntity getNearbyMate() {
        return dragon.level.getNearbyEntities(dragon.getClass(), PARTNER_TARGETING, dragon, dragon.getBoundingBox().inflate(dragon.getBbWidth() * 8))
                .stream()
                .filter(potentialDragonMate -> !(potentialDragonMate.getGender() == dragon.getGender()))
                .min(Comparator.comparingDouble(dragon::distanceToSqr)).orElse(null);
    }
}