package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Random;

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
            if ((dragon.hasDataParameter(TameableDragonEntity.SLEEPING) && !dragon.level.isDay()) || dragon.getRandom().nextFloat() <= probability) {
                BlockPos bPos = RandomPos.generateRandomPosTowardDirection(dragon, 20, new Random(), new BlockPos(dragon.getPosition(0.0f)));
                position = new Vec3(bPos.getX(), 0, bPos.getZ());
            }
            else
            {
                Vec3 vec3d = dragon.getLookAngle();
                if (!dragon.isWithinRestriction())
                    vec3d = Vec3.atLowerCornerOf(dragon.getRestrictCenter()).subtract(dragon.position()).normalize();

                int yOffset = dragon.getAltitude() > 40? 10 : 0;
                BlockPos bPos2 = RandomPos.generateRandomPosTowardDirection(dragon, 50, new Random(), new BlockPos(dragon.getPosition(0.0f)));
                position = new Vec3(bPos2.getX(), bPos2.getY(), bPos2.getZ());
            }
            if (position != null && position.y > dragon.getY() + dragon.getBbHeight() && !dragon.isFlying()) dragon.setFlying(true);
        }

        return position == null? super.getPosition() : position;
    }
}
