package com.github.shannieann.wyrmroost.entities.dragon.helpers.ai;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.util.Mafs;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyerWanderGoal extends WaterAvoidingRandomStrollGoal
{
    private final WRDragonEntity dragon;

    public FlyerWanderGoal(WRDragonEntity dragon, double speed, float probability)
    {
        super(dragon, speed, probability);
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));

        this.dragon = dragon;
    }

    public FlyerWanderGoal(WRDragonEntity dragon, double speed)
    {
        this(dragon, speed, 0.001f);
    }

    @Override
    public boolean canUse()
    {
        if (dragon.isInSittingPose()) return false;
        if (dragon.canBeControlledByRider()) return false;
        if (!dragon.isFlying() && dragon.hasEntityDataAccessor(WRDragonEntity.SLEEPING) && !dragon.level.isDay()) return false;
        Vec3 vec3d = getPosition();
        if (vec3d != null)
        {
            this.wantedX = vec3d.x;
            this.wantedY = vec3d.y;
            this.wantedZ = vec3d.z;
            this.forceTrigger = false;
            return true;
        }

        return false;
    }

    @Override
    public Vec3 getPosition()
    {
        Vec3 position = null;

        if (dragon.isFlying() || (!dragon.isLeashed() && dragon.getRandom().nextFloat() <= probability + 0.02))
        {
            if ((dragon.hasEntityDataAccessor(WRDragonEntity.SLEEPING) && !dragon.level.isDay()) || dragon.getRandom().nextFloat() <= probability)
                position = LandRandomPos.getPos(dragon, 20, 25);
            else
            {
                Vec3 vec3d = dragon.getLookAngle();
                if (!dragon.isWithinRestriction())
                    vec3d = Vec3.atLowerCornerOf(dragon.getRestrictCenter()).subtract(dragon.position()).normalize();

                position = AirRandomPos.getPosTowards(dragon, 50, 30, 10, vec3d, Mafs.PI / 2);
            }
            if (position != null && position.y > dragon.getY() + dragon.getBbHeight() && !dragon.isFlying()) dragon.setFlying(true);
        }
        return position == null? super.getPosition() : position;
    }

}
