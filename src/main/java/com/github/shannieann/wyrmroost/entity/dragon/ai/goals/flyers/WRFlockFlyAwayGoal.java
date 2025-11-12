package com.github.shannieann.wyrmroost.entity.dragon.ai.goals.flyers;

import com.github.shannieann.wyrmroost.entity.dragon.EntityCanariWyvern;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity.NavigationType;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.AnimatedGoal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

// Currently only usable for Canari wyvern
// Only makes sense for dragons that spend most time on ground
// To make usable for others, may need to have multiple "entity" options or cast based on dragon type.
public class WRFlockFlyAwayGoal extends AnimatedGoal
{

    private final EntityCanariWyvern dragon;
    private int minDistance;
    private int maxDistance;
    private boolean abortGoal;
    private int giveUpTimer;

    private int animationTimeTicks;

    public WRFlockFlyAwayGoal(EntityCanariWyvern dragon, int minDistance, int maxDistance, int animationTimeTicks) {
        super(dragon);
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        this.dragon = dragon;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.abortGoal = false;
        this.giveUpTimer = 0;
        this.animationTimeTicks = animationTimeTicks;
    }

    @Override
    public boolean canUse() {
        // Very low chance of triggering a flock fly away as the leader. This seems to be about every ~30 seconds.
        boolean canFlockFlyLeader = dragon.getRandom().nextInt(10000) < 8
                                    && dragon.getFlockingX() == 0 && dragon.getFlockingY() == 0 && dragon.getFlockingZ() == 0
                                    && dragon.isOnGround() && ! dragon.isInOverrideAnimation() && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed()   ;  
        // In start(), the leader will set the flock target. Followers just need to have such a position set. They can be woken up from sleep or interrupted mid-idle or mid-attack.
        boolean canFlockFlyFollower = dragon.getFlockingX() != 0 && dragon.getFlockingY() != 0 && dragon.getFlockingZ() != 0
                                    && dragon.isOnGround() && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
        return canFlockFlyLeader || canFlockFlyFollower;
    }

    @Override
    public void start() {

        if (dragon.getFlockingX() == 0 && dragon.getFlockingY() == 0 && dragon.getFlockingZ() == 0) {
            // This one is the leader: find a fly away position and set it for everyone in flock
            setLeaderFlyAwayPos();
            if (abortGoal) {
                return;
            }
            setFollowerFlyAwayPos();
        }

        this.dragon.clearAI();
        this.dragon.setOverrideAnimation(""); // force quit idles/special animations
        this.dragon.setSleeping(false);

        this.dragon.getJumpControl().jump();
        this.dragon.setNavigator(NavigationType.FLYING);
        this.dragon.getNavigation().createPath(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
        if (this.dragon.getNavigation().getPath() == null) {
            this.dragon.getNavigation().moveTo(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
        }
        super.start("takeoff", AnimatedGoal.PLAY_ONCE, animationTimeTicks);
    }

    private void setLeaderFlyAwayPos() {

        Vec3 pos = null;

        for (int i = 0; i < 10; i++) { // Make ten attempts to find position between min/max range before giving up
            pos = LandRandomPos.getPos(this.dragon, this.maxDistance, 20);
            if (pos != null && this.dragon.distanceToSqr(pos) > this.minDistance * this.minDistance) {
                break;
            } else {
                pos = null;
            }
        }

        if (pos == null) {
            this.abortGoal = true;
            return;
        }

        this.dragon.setFlockingX(((int)pos.x));
        this.dragon.setFlockingY((int)pos.y);
        this.dragon.setFlockingZ((int)pos.z);
    }

    private void setFollowerFlyAwayPos() {

        int flyAwayX = this.dragon.getFlockingX();
        int flyAwayY = this.dragon.getFlockingY();
        int flyAwayZ = this.dragon.getFlockingZ();

        for (EntityCanariWyvern mob : this.dragon.level.getEntitiesOfClass(EntityCanariWyvern.class, this.dragon.getBoundingBox().inflate(this.dragon.getRestrictRadius()), entity -> entity != this.dragon)) {
            // Followers get a random position within 3 horizontal blocks of leader, unless they already have one
            if (mob.getFlockingX() == 0 && mob.getFlockingY() == 0 && mob.getFlockingZ() == 0) {
                mob.setFlockingX(flyAwayX + dragon.getRandom().nextInt(7) - 4);
                mob.setFlockingY(flyAwayY);
                mob.setFlockingZ(flyAwayZ + dragon.getRandom().nextInt(7) - 4);
            }
        }
    }

    @Override
    public void tick() {

        // Still doing takeoff animation, don't do anything else yet
        if (!super.canContinueToUse()) {
            super.stop();
        } else {
            super.tick();
        }

        // Looks natural if we only start moving up halfway through takeoff animation
        // The code below follower/leader block makes dragon fly up if on ground,
        // so we don't have to worry about takeoff animation completing on the ground
        if (super.canContinueToUse() &&super.elapsedTime < animationTimeTicks / 2) {
            this.dragon.setDeltaMovement(Vec3.ZERO);
            this.dragon.setNavigator(NavigationType.GROUND);
            return;
        }

        this.giveUpTimer++;

        if (this.dragon.getNavigation().isDone() && ! isNearFlyAwayPos(false)) {
            this.dragon.getNavigation().createPath(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
            if (this.dragon.getNavigation().getPath() == null) {
                this.dragon.getNavigation().moveTo(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ(), 1);
            }
        }

        // They keep landing for some reason... make them fly unless they're pretty close to target
        if (! isNearFlyAwayPos(true) && this.dragon.getAltitude() < 1.5*this.dragon.getFlightThreshold()) {
            Vec3 direction = new Vec3(this.dragon.getFlockingX(), this.dragon.getFlockingY(), this.dragon.getFlockingZ()).subtract(this.dragon.position()).normalize();
            direction = new Vec3(direction.x, 1, direction.z);
            this.dragon.setDeltaMovement(direction.scale(0.3));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return ! this.abortGoal && this.giveUpTimer < 600 && ! isNearFlyAwayPos(false) && dragon.getFlockingX() != 0 && dragon.getFlockingY() != 0 && dragon.getFlockingZ() != 0
            && dragon.getTarget() == null && ! dragon.isTame() && ! dragon.isLeashed();
    }

    private boolean isNearFlyAwayPos(boolean loosenRequirements) {
        if (loosenRequirements) {
            return dragon.distanceToSqr(dragon.getFlockingX(), dragon.getFlockingY(), dragon.getFlockingZ()) < 1.5*dragon.getRestrictRadius();
        }
        return dragon.distanceToSqr(dragon.getFlockingX(), dragon.getFlockingY(), dragon.getFlockingZ()) < dragon.getRestrictRadius();
    }

    @Override
    public void stop() {
        this.dragon.getNavigation().stop();
        this.dragon.setFlockingX(0);
        this.dragon.setFlockingY(0);
        this.dragon.setFlockingZ(0);

        this.abortGoal = false;
        this.giveUpTimer = 0;
    }
}
