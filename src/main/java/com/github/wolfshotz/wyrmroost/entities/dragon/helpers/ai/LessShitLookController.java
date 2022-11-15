package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class LessShitLookController extends LookControl
{
    private final TameableDragonEntity dragon;
    private boolean stopLooking;

    public LessShitLookController(TameableDragonEntity dragon)
    {
        super(dragon);
        this.dragon = dragon;
    }

    @Override
    public void tick()
    {
        super.tick();
        stopLooking = false;
    }

    @Override
    public void setLookAt(double x, double y, double z, float speed, float maxAngle)
    {
        if (!stopLooking) super.setLookAt(x, y, z, speed, maxAngle);
    }

    @Override
    protected Optional<Float> getXRotD()
    {
        Vec3 mouthPos = dragon.getApproximateMouthPos();
        double x = wantedX - mouthPos.x();
        double y = wantedY - mob.getEyeY();
        double z = wantedZ - mouthPos.z();
        double sqrt = Mth.sqrt((float) (x * x + z * z));
        return Optional.of((float) (-(Mth.atan2(y, sqrt) * (double)(180f / Mafs.PI))));
    }

    public void stopLooking()
    {
        this.stopLooking = true;
        this.hasWanted = false;
    }
}
