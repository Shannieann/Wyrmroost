package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Avoid the nearest player in range without using TargetingConditions.forCombat(),
 * so creative/peaceful/invulnerable players are still detected (unlike vanilla AvoidEntityGoal).
 */
public class WRAvoidPlayerGoal extends Goal {

    protected final PathfinderMob mob;
    protected final float maxDist;
    protected final double walkSpeedModifier;
    protected final double sprintSpeedModifier;
    protected final Predicate<Player> shouldAvoid;
    protected final PathNavigation pathNav;

    protected Player toAvoid;
    protected Path path;
    /** Exposed so subclasses (e.g. Silver Glider) can use escape pos for flying away. */
    protected Vec3 escapePos;

    public WRAvoidPlayerGoal(PathfinderMob mob, float maxDist, double walkSpeedModifier, double sprintSpeedModifier, Predicate<Player> shouldAvoid) {
        this.mob = mob;
        this.maxDist = maxDist;
        this.walkSpeedModifier = walkSpeedModifier;
        this.sprintSpeedModifier = sprintSpeedModifier;
        this.shouldAvoid = shouldAvoid;
        this.pathNav = mob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        AABB aabb = mob.getBoundingBox().inflate(maxDist, 3.0, maxDist);
        List<Player> players = mob.level.getEntitiesOfClass(Player.class, aabb);
        Player nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        for (Player p : players) {
            if (!p.isAlive() || !shouldAvoid.test(p)) {
                continue;
            }
            double distSq = mob.distanceToSqr(p.getX(), p.getY(), p.getZ());
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = p;
            }
        }
        this.toAvoid = nearest;
        if (toAvoid == null) {
            return false;
        }
        this.escapePos = DefaultRandomPos.getPosAway(mob, 16, 7, toAvoid.position());
        if (escapePos == null) {
            return false;
        }
        if (toAvoid.distanceToSqr(escapePos.x, escapePos.y, escapePos.z) < toAvoid.distanceToSqr(mob)) {
            return false;
        }
        this.path = pathNav.createPath(escapePos.x, escapePos.y, escapePos.z, 0);
        return path != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !pathNav.isDone();
    }

    @Override
    public void start() {
        pathNav.moveTo(path, walkSpeedModifier);
    }

    @Override
    public void stop() {
        toAvoid = null;
        path = null;
        escapePos = null;
    }

    @Override
    public void tick() {
        if (toAvoid != null && mob.distanceToSqr(toAvoid) < 49.0) {
            mob.getNavigation().setSpeedModifier(sprintSpeedModifier);
        } else {
            mob.getNavigation().setSpeedModifier(walkSpeedModifier);
        }
    }
}