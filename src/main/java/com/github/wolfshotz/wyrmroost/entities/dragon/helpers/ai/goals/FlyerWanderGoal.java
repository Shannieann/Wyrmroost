package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FlyerWanderGoal extends WaterAvoidingRandomStrollGoal
{
    private final TameableDragonEntity dragon;

    public FlyerWanderGoal(TameableDragonEntity dragon, double speed, float probability)
    {
        super(dragon, speed, probability);
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));

        this.dragon = dragon;
    }

    public FlyerWanderGoal(TameableDragonEntity dragon, double speed)
    {
        this(dragon, speed, 0.001f);
    }

    @Override
    public boolean canUse()
    {
        if (dragon.isInSittingPose()) return false;
        if (dragon.canBeControlledByRider()) return false;
        if (!dragon.isFlying()) return false;
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
            if ((dragon.hasDataParameter(TameableDragonEntity.SLEEPING) && !dragon.level.isDay()) || dragon.getRandom().nextFloat() <= probability)
                position = LandRandomPos.getPos(dragon, 20, 25);
            else
            {
                Vec3 vec3d = dragon.getLookAngle();
                if (!dragon.isWithinRestriction())
                    vec3d = Vec3.atLowerCornerOf(dragon.getRestrictCenter()).subtract(dragon.position()).normalize();

                int yOffset = dragon.getAltitude() > 40? 10 : 0;
                position = AirAndWaterRandomPos.getPos(dragon, 50, 30, vec3d.z, Mafs.PI / 2, 10, yOffset);
            }
            if (position != null && position.y > dragon.getY() + dragon.getBbHeight() && !dragon.isFlying()) dragon.setFlying(true);
        }

        return position == null? super.getPosition() : position;
}

