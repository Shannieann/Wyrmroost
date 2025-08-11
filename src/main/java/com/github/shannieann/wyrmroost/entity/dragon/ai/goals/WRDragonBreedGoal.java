package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class WRDragonBreedGoal<T extends WRDragonEntity & IBreedable> extends Goal {

    protected final WRDragonEntity dragon;
    protected final IBreedable breedable;
    protected int spawnBabyDelay;
    protected int maxBreedingCooldown;

    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    private static final int MATE_SEARCH_INTERVAL = 20; // Search every second
    private int mateSearchCooldown = MATE_SEARCH_INTERVAL;
    protected WRDragonEntity targetMate;
    private int loveTime = 0;

    protected boolean success = false;

    public WRDragonBreedGoal(T dragon) {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.dragon = dragon;
        this.breedable = dragon;
        this.maxBreedingCooldown = dragon.getMaxBreedingCooldown();
    }

    @Override
    public boolean canUse() {
        if (!dragon.isInLove() || dragon.getBreedingCooldown() > 0 || dragon.getTarget() != null) {
            return false;
        }
        if (mateSearchCooldown <= 0) {
            targetMate = getNearbyMate();
            mateSearchCooldown = MATE_SEARCH_INTERVAL;
        } else {
            mateSearchCooldown--;
        }
        return targetMate != null;
    }

    @Override
    public void start() {
        this.mateSearchCooldown = MATE_SEARCH_INTERVAL;
        this.dragon.setSitting(false);
    }

    @Override
    public boolean canContinueToUse() {
        return dragon.isInLove() && targetMate != null && targetMate.isAlive() && targetMate.isInLove() && spawnBabyDelay < 60 && loveTime < 60;
    }

    @Override
    public void tick() {
        if (targetMate == null) {
            stop();
            return;
        }
        dragon.getLookControl().setLookAt(targetMate, 10f, dragon.getYawRotationSpeed());
        dragon.getNavigation().moveTo(targetMate, 0.75f);
        spawnBabyDelay++;
        if (spawnBabyDelay >= adjustedTickDelay(60) && dragon.distanceTo(targetMate) < dragon.getBbWidth() * 2) {
            dragon.spawnDragonEgg(dragon, breedable.hatchTime());
            dragon.resetLove();
            targetMate.resetLove();
            this.success = true;
            stop();
            return;
        }
    }

    @Override
    public void stop() {
        if (this.success && this.dragon != null && this.dragon.isAlive()) {
            this.dragon.setBreedingCooldown(this.breedable.getMaxBreedingCooldown());
            this.dragon.setBreedingCount(this.dragon.getBreedingCount()+1);
        }
        if (this.success && this.targetMate != null && this.targetMate.isAlive()) {
            this.targetMate.setBreedingCooldown(this.breedable.getMaxBreedingCooldown());
            this.targetMate.setBreedingCount(this.targetMate.getBreedingCount()+1);
        }
        targetMate = null;
        spawnBabyDelay = 0;
        mateSearchCooldown = MATE_SEARCH_INTERVAL;
    }

    @Nullable
    protected WRDragonEntity getNearbyMate() {

        List<? extends WRDragonEntity> list = dragon.level.getNearbyEntities(dragon.getClass(), PARTNER_TARGETING, dragon, dragon.getBoundingBox().inflate(dragon.getBbWidth() * 12));

        WRDragonEntity closestMate = list.stream()
            .filter(potentialDragonMate -> dragon.canMate(potentialDragonMate))
            .min(Comparator.comparingDouble(dragon::distanceToSqr))
            .orElse(null);

        return closestMate;
    }
}