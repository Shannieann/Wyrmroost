package com.github.shannieann.wyrmroost.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;


public final class WRMathsUtility {
    private WRMathsUtility()
    {/* good try */}

    public static final float PI = (float) Math.PI;

    /**
     * Returns a new pseudo random double value constrained to the values of {@code (-1.0d)} and {@code (1.0d)}
     */
    public static double nextDouble(Random rand) {
        return 2 * rand.nextDouble() - 1;
    }

    /**
     * Returns a new pseudo random inteeger value constrained to the values of the negation of {@code bounds}
     * and postive {@code bounds}
     */
    public static int nextInt(Random rand, int bounds) {
        bounds = bounds * 2 - 1;
        return rand.nextInt(bounds) - (bounds / 2);
    }

    /**
     * Returns a new pseudo random degree angle value constrained to the values of the negation of -180 and + 180
     */
    public static int generateRandomDegAngle() {
        Random random = new Random();
        return random.nextInt(361) - 180; // Generates a number between -180 and +180
    }

    /**
     * Given a yaw angle FOR A REFERENCE SYSTEM and an offset X and Z,
     * this method will return a new vector with X and Z coordinates rotated by that yaw angle
     * See: https://postimg.cc/9DkSnKSr
     */

    public static Vec3 rotateXZVectorByYawAngle(float yawAngleDEG, double xOffset, double zOffset)
    {
        return new Vec3(xOffset, 0, zOffset).yRot(-yawAngleDEG * (PI / 180f));
    }

    /**
     * Get the angle between 2 sources
     * <p>
     * TODO: Adjust so that the angle is closest to 0 in the SOUTH direction!, currently it is only doing it for east!
     */
    public static double getAngle(double sourceX, double sourceZ, double targetX, double targetZ)
    {
        return Mth.atan2(targetZ - sourceZ, targetX - sourceX) * 180 / Math.PI + 180;
    }

    public static double getAngle(Entity source, Entity target)
    {
        return Mth.atan2(target.getZ() - source.getZ(), target.getX() - source.getX()) * (180 / Math.PI) + 180;
    }

    /**
     * Clamped (0-1) Linear Interpolation (Float version)
     */
    public static float linTerp(float a, float b, float x)
    {
        if (x <= 0) return a;
        if (x >= 1) return b;
        return a + x * (b - a);
    }

    @Nullable
    public static EntityHitResult clipEntities(Entity shooter, double range, @Nullable Predicate<Entity> filter)
    {
        return clipEntities(shooter, range, 0, filter);
    }

    @Nullable
    public static EntityHitResult clipEntities(Entity shooter, double range, double hitRadius, @Nullable Predicate<Entity> filter)
    {
        Vec3 eyes = shooter.getEyePosition(1f);
        Vec3 end = eyes.add(shooter.getLookAngle().multiply(range, range, range));

        Entity result = null;
        double distance = range * range;
        for (Entity entity : shooter.level.getEntities(shooter, shooter.getBoundingBox().inflate(range), filter))
        {
            Optional<Vec3> opt = entity.getBoundingBox().inflate(hitRadius).clip(eyes, end);
            if (opt.isPresent())
            {
                double dist = eyes.distanceToSqr(opt.get());
                if (dist < distance)
                {
                    result = entity;
                    distance = dist;
                }
            }
        }

        return result == null? null : new EntityHitResult(result);
    }
}