package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;

import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class WRRidePlayerGoal extends AnimatedGoal {

    private final WRDragonEntity dragon;
    private Player owner;

    private final boolean isSilverGlider;

    private String animationName;
    private String animation2Name;
    private String animation3Name;

    public WRRidePlayerGoal(WRDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
        this.owner = (Player) dragon.getOwner();

        this.isSilverGlider = dragon instanceof EntitySilverGlider;
        this.animationName = "sit";

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    public WRRidePlayerGoal(WRDragonEntity dragon, String animationName, String animation2Name, String animation3Name) {
        super(dragon);
        this.dragon = dragon;
        this.owner = (Player) dragon.getOwner();

        this.isSilverGlider = dragon instanceof EntitySilverGlider;
        this.animationName = animationName;
        this.animation2Name = animation2Name;
        this.animation3Name = animation3Name;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK, Goal.Flag.TARGET));
    }

    public boolean canUse() {
        return dragon != null && dragon.isRidingPlayer() && dragon.getOwner() != null && ((Player) dragon.getOwner()).isAlive();
    }

    @Override
    public void start() {
        super.start(animationName, AnimatedGoal.LOOP, 0);
    }

    @Override
    public void tick() {
        this.owner = (Player) dragon.getOwner(); // For some reason it always says owner is null unless we refresh owner every tick?
        if (owner == null || ! owner.isAlive()) {
            return;
        }

        if (isSilverGlider) {
            if (dragon.isAdult() && owner.isFallFlying())  {
                if (owner.jumping && dragon.getAnimation() != animation3Name) {
                    super.start(animation3Name, AnimatedGoal.LOOP, 0);
                } else if (dragon.getAnimation() != animation2Name) {
                    super.start(animation2Name, AnimatedGoal.LOOP, 0);
                }
            }
            else if (dragon.getAnimation() != animationName) {
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
        super.stop();
        dragon.stopRidingPlayer();

        owner = null;

        animationName = null;
        animation2Name = null;
        animation3Name = null;
    }

}
