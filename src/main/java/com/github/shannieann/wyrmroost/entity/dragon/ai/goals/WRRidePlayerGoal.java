package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WRRidePlayerGoal extends AnimatedGoal {

    private final WRDragonEntity dragon;
    private Player owner;

    // Player head is 0.5 blocks wide, so from -0.25 to 0.25 for coords centered at player position
    // For default yrot=0 player is facing south, so movement to the player's left (east) is positive, and to the player's right (west) is negative
    // We rotate to account for yrot later
    private final double leftOffset; // position 1
    private final double rightOffset; // position 3

    private final boolean isSilverGlider;

    private String animationName;
    private String specialAnimationName;

    public WRRidePlayerGoal(WRDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
        this.owner = (Player) dragon.getOwner();

        this.leftOffset = 0.3;
        this.rightOffset = -0.3;

        this.isSilverGlider = dragon instanceof EntitySilverGlider;
        this.animationName = "sit";

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    public WRRidePlayerGoal(WRDragonEntity dragon, double leftOffset, double rightOffset) {
        super(dragon);
        this.dragon = dragon;
        this.owner = (Player) dragon.getOwner();

        this.leftOffset = leftOffset;
        this.rightOffset = rightOffset;

        this.isSilverGlider = dragon instanceof EntitySilverGlider;
        this.animationName = "sit";

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    public WRRidePlayerGoal(WRDragonEntity dragon, String animationName, String specialAnimationName) {
        super(dragon);
        this.dragon = dragon;
        this.owner = (Player) dragon.getOwner();

        this.leftOffset = 0.3;
        this.rightOffset = -0.3;

        this.isSilverGlider = dragon instanceof EntitySilverGlider;
        this.animationName = animationName;
        this.specialAnimationName = specialAnimationName;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    public boolean canUse() {
        return dragon != null && dragon.isRidingPlayer() && dragon.getOwner() != null && ((Player) dragon.getOwner()).isAlive();
    }

    @Override
    public void start() {
        super.start(animationName, AnimatedGoal.LOOP, 0);
        System.out.println("Starting ride player goal");
    }

    @Override
    public void tick() {
        System.out.println("Current pos: " + dragon.getX() + ", " + dragon.getY() + ", " + dragon.getZ());
        this.owner = (Player) dragon.getOwner(); // For some reason it always says owner is null unless we refresh owner every tick?
        if (owner == null || ! owner.isAlive()) {
            System.out.println("Owner is null or dead");
            return;
        }

        if (isSilverGlider) {
            if (owner.isFallFlying() && dragon.getAnimation() != specialAnimationName) {
                super.start(specialAnimationName, AnimatedGoal.LOOP, 0);
            }
            else if (!owner.isFallFlying() && dragon.getAnimation() != animationName) {
                super.start(animationName, AnimatedGoal.LOOP, 0);
            }
        }
        super.tick();
    }

    @Override
    public boolean canContinueToUse() {
        // All riding checks handled by WRDragonEntity tick() method (may be overridden by child)
        // Need to check if dragon is null for some reason or it crashes on dismount
        return dragon != null && dragon.isRidingPlayer();
    }

    @Override
    public void stop() {
        System.out.println("Stopping ride player goal");
        super.stop();
        dragon.stopRidingPlayer();

        owner = null;

        animationName = null;
        specialAnimationName = null;
    }

}
