package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers;

import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity.NavigationType;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;

// Currently only usable for silver glider. If we want to make it usable for more dragons, we need to
// add the IS_FLOCKING synced entity data to WRDragonEntity or have multiple "entity" options or cast based on dragon type.
public class WRFlockFlyInCirclesGoal extends AnimatedGoal
{
    private Vec3 currTargetPos;
    private ArrayList<Vec3> flockPosList;
    private int currentPointIndex;
    private int animationTimeTicks;
    private int radius;

    private EntitySilverGlider dragon;
    private EntitySilverGlider leader;

    private int findClosestDragonTimer;

    public WRFlockFlyInCirclesGoal(EntitySilverGlider dragon, int radius, int animationTimeTicks)
    {
        super(dragon);
        this.dragon = dragon;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        this.currTargetPos = null;
        this.currentPointIndex = 0;
        this.flockPosList = null;
        this.leader = null;
        this.radius = radius;
        this.findClosestDragonTimer = 0;
        this.animationTimeTicks = animationTimeTicks;
    }

    @Override
    public boolean canUse() {
        boolean canFlockFly = dragon != null // For some reason sometimes this happens
                && dragon.getRandom().nextInt(100) < 2
                && (! dragon.isInOverrideAnimation() || dragon.getOverrideAnimation() == "float") && dragon.getTarget() == null
                && ! dragon.isTame() && ! dragon.isLeashed();

        if (canFlockFly) {
            leader = findNearbyFlockLeader();
            if (leader == null) { // Always check this again to make sure nothing goes out of synce when world is reopened
                leader = dragon;
                dragon.setIsFlocking(2); // No leader found - you're the leader now
            }
            else {
                dragon.setIsFlocking(1); // Follower
            }
        }
        return canFlockFly;
    }

    private EntitySilverGlider findNearbyFlockLeader() {
        for (EntitySilverGlider glider : dragon.level.getEntitiesOfClass(dragon.getClass(), dragon.getBoundingBox().inflate(radius), entity -> entity != dragon)) {
            if (glider.getIsFlocking() == 2) {
                return glider;
            }
        }
        return null;
    }

    @Override
    public void start() {
        dragon.getNavigation().stop();
        if (dragon.isOnGround() && dragon.canLiftOff()) {
            dragon.setDeltaMovement(0, 0.2, 0);
            dragon.setNavigator(NavigationType.FLYING);
        }

        if (dragon.getIsFlocking() == 1) {
            // Follower - don't generate points
            return;
        }

        // Generate 8 points in a circle, centered on the silver glider's current location
        flockPosList = new ArrayList<>();
        double centerX = dragon.getX();
        double centerZ = dragon.getZ();
        double targetY = dragon.getY();

        // If altitude is less than 10, adjust the y-value to make altitude 10
        if (dragon.getAltitude() < 10) {
            targetY = dragon.getY() + (10 - dragon.getAltitude());
        }
        else if (dragon.getAltitude() > 20) {
            targetY = dragon.getY() + (20 - dragon.getAltitude());
        }

        // Generate 8 points in a circle
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            // Skip any points that aren't air
            BlockPos pos = new BlockPos(centerX + offsetX, targetY, centerZ + offsetZ);
            if (! dragon.level.getBlockState(pos).getMaterial().isSolid() && ! dragon.level.getBlockState(pos).getMaterial().isLiquid()) {
                flockPosList.add(new Vec3(centerX + offsetX, targetY, centerZ + offsetZ));
            }
        }

        if (flockPosList.size() == 0) {
            leader = null; // abort goal
            return;
        }

        // Set the first point as the current target
        currentPointIndex = 0;
        currTargetPos = flockPosList.get(currentPointIndex);
        super.start("takeoff", AnimatedGoal.LOOP, animationTimeTicks);
    }

    @Override
    public void tick()
    {
        // Still doing takeoff animation, don't do anything else yet
        if (! super.canContinueToUse()) {
            super.stop();
        }
        else {
            super.tick();
        }

        // Looks natural if we only start moving up halfway through takeoff animation
        // The code below follower/leader block makes dragon fly up if on ground,
        // so we don't have to worry about takeoff animation completing on the ground
        if (super.canContinueToUse() && super.elapsedTime < animationTimeTicks/2) {
            this.dragon.setDeltaMovement(Vec3.ZERO);
            this.dragon.setNavigator(NavigationType.GROUND);
            return;
        }
        
        // Follower - follow the leader
        if (dragon.getIsFlocking() == 1)
        {
            if (leader == null || ! leader.isAlive() || leader.getIsFlocking() != 2) {
                // Leader stopped being a leader or stopped being. Stop flocking
                dragon.setIsFlocking(0);
                return;
            }

            double distanceToLeader = dragon.distanceTo(leader);
            dragon.getLookControl().setLookAt(leader, 30, 30);

            // Catch up to leader if far away
            if (distanceToLeader > radius) {
                dragon.getNavigation().moveTo(leader, 1.4); // Far away, catch up
            }
            else if (distanceToLeader > radius / 2) {
                dragon.getNavigation().moveTo(leader, 1.25);
            }
            // Boids algorithm is super finicky about separation and starting distance, and getting the dragon to face the right direction is tough
            // Having them just follow the leader is way easier to implement and looks basically the same
            else
            {
                dragon.getNavigation().moveTo(leader, 1.15);
            }
        }
        // Leader - follow the circular path
        else
        {
            if (currTargetPos == null || flockPosList == null) {
                // Sometimes happens when canContinueToUse() returns false
                return;
            }

            if (dragon.getNavigation().isDone()) {
                dragon.getNavigation().moveTo(currTargetPos.x, currTargetPos.y, currTargetPos.z, 1);
            }
            dragon.getLookControl().setLookAt(currTargetPos.x, currTargetPos.y, currTargetPos.z);

            // Check if we've reached the current target point (within 4 blocks, close enough)
            if (dragon.position().distanceTo(currTargetPos) < 4) {
                // Switch to the next point in the circle
                currentPointIndex = (currentPointIndex + 1) % flockPosList.size();
                currTargetPos = flockPosList.get(currentPointIndex);
            }
        }

        // Apply some movement refinement for both followers and leaders

        Vec3 deltaMovement = dragon.getDeltaMovement();

        // They keep landing for some reason... push them up if they get too close to the ground
        // This also pushes them up to start
        if (this.dragon.getAltitude() < 1.5 * this.dragon.getFlightThreshold()) {
            Vec3 direction = deltaMovement.normalize();
            direction = new Vec3(direction.x, 1, direction.z);
            dragon.setDeltaMovement(direction.scale(0.3));
        }

        // They try to dive if above the target and overshoot a lot. Prevent them from diving
        if (deltaMovement.y <= -0.3) {
            dragon.setDeltaMovement(new Vec3(deltaMovement.x, -0.2, deltaMovement.z));
        }

        // Avoid lag, don't calculate closest dragon every tick
        findClosestDragonTimer++;
        if (findClosestDragonTimer > 10) {
            findClosestDragonTimer = 0;
        } else {
            return;
        }

        WRDragonEntity closestDragon = null;
        double closestDistance = Double.MAX_VALUE;

        // Find the closest dragon within 4 blocks
        for (WRDragonEntity nearby : dragon.level.getEntitiesOfClass(
            dragon.getClass(), 
                dragon.getBoundingBox().inflate(4.0), 
                entity -> entity != dragon)) {

            double distance = dragon.distanceTo(nearby);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestDragon = nearby;
            }
        }

        // If a dragon was found, move away from it a little
        if (closestDragon != null) {
            // Calculate direction vector from the closest dragon to this dragon
            Vec3 avoidanceDirection = dragon.position().subtract(closestDragon.position()).normalize();
            
            // Apply a small push away from the other dragon
            dragon.setDeltaMovement(
                dragon.getDeltaMovement().add(avoidanceDirection.scale(0.1))
            );
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return leader != null && leader.isAlive() && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isPassenger();
    }

    @Override
    public void stop() {
        // Store the dragon reference before nulling it
        EntitySilverGlider dragonRef = this.dragon;

        // Null out member variables
        this.currTargetPos = null;
        this.flockPosList = null;
        this.currentPointIndex = 0;

        // Reset flocking state if dragon still exists
        if (dragonRef != null) {
            dragonRef.setIsFlocking(0);
        }

        // Finally null out the dragon reference
        this.dragon = null;
        this.leader = null;
    }
}
