package com.github.shannieann.wyrmroost.entities.dragon.ai.goals;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class WRSitGoal extends SitWhenOrderedToGoal
{
    private final WRDragonEntity dragon;

    public WRSitGoal(WRDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    public boolean canUse()
    {
        if (!dragon.isTame()) return false;
        if (dragon.isInWaterOrBubble() && dragon.getMobType() != MobType.WATER) return false;
        if (!dragon.isOnGround() && !dragon.isUsingFlyingNavigator()) return false;
        LivingEntity owner = dragon.getOwner();
        if (owner == null) return true;
        return (dragon.distanceToSqr(owner) > 144d || owner.getLastHurtByMob() == null) && super.canUse();
    }

    @Override
    public void tick()
    {
        if (dragon.isUsingFlyingNavigator()) // get to ground first
        {
            if (dragon.getNavigation().isDone())
            {
                BlockPos pos = findLandingPos();
                dragon.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.05);
            }
        }
        else dragon.setOrderedToSit(true);
    }

    private BlockPos findLandingPos()
    {
        Random rand = dragon.getRandom();

        // get current entity position
        BlockPos.MutableBlockPos ground = dragon.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, dragon.blockPosition()).mutable();

        // make sure the y value is suitable
        if (ground.getY() <= 0 || ground.getY() > dragon.getY() || !dragon.level.getBlockState(ground.below()).getMaterial().isSolid())
            ground.setY((int) dragon.getY() - 5);

        // add some variance
        int followRange = Mth.floor(dragon.getAttributeValue(Attributes.FOLLOW_RANGE));
        int ox = followRange - rand.nextInt(followRange) * 2;
        int oz = followRange - rand.nextInt(followRange) * 2;
        ground.setX(ox);
        ground.setZ(oz);

        return ground;
    }
}
