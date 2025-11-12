package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;
import javax.annotation.Nullable;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class WRTemptGoal extends Goal {
   private static final TargetingConditions TEMP_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
   private final TargetingConditions targetingConditions;
   protected final PathfinderMob mob;
   private final double speedModifier;
   private double px;
   private double py;
   private double pz;
   private double pRotX;
   private double pRotY;
   @Nullable
   protected Player player;
   private int calmDown;
   private boolean isRunning;
   private final Ingredient items;
   private final boolean canScare;

   public WRTemptGoal(WRDragonEntity pMob, double pSpeedModifier, Ingredient pItems, boolean pCanScare) {
      this.mob = pMob;
      this.speedModifier = pSpeedModifier;
      this.items = pItems;
      this.canScare = pCanScare;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
   }

    public boolean canUse() {
        if (((TamableAnimal)this.mob).isTame() || this.mob.getTarget() != null) {
            return false;
        }
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.mob.level.getNearestPlayer(this.targetingConditions, this.mob);
            return this.player != null;
        }
   }

   protected boolean canScare() {
      return this.canScare;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   private boolean shouldFollow(LivingEntity luringEntity) {
      return this.items.test(luringEntity.getMainHandItem()) || this.items.test(luringEntity.getOffhandItem());
   }

   public void start() {
      this.px = this.player.getX();
      this.py = this.player.getY();
      this.pz = this.player.getZ();
      this.pRotX = (double)this.player.getXRot();
      this.pRotY = (double)this.player.getYRot();
      this.isRunning = true;
      System.out.println("WRTemptGoal start: starting to tempt player ");
   }

   public boolean canContinueToUse() {
      if (this.canScare()) {
         if (this.mob.distanceToSqr(this.player) < 36.0) {
            if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002) {
               System.out.println("WRTemptGoal canContinueToUse: player moved, stopping");
               return false;
            }

            if (Math.abs((double)this.player.getXRot() - this.pRotX) > 10.0 || Math.abs((double)this.player.getYRot() - this.pRotY) > 10.0) {
               System.out.println("WRTemptGoal canContinueToUse: player xRot = " + this.player.getXRot() + " pRotX = " + this.pRotX);
               System.out.println("WRTemptGoal canContinueToUse: player yRot = " + this.player.getYRot() + " pRotY = " + this.pRotY);
               System.out.println("WRTemptGoal canContinueToUse: player rotated too fast, stopping");
               return false;
            }
         } else {
            this.px = this.player.getX();
            this.py = this.player.getY();
            this.pz = this.player.getZ();
         }

         this.pRotX = (double)this.player.getXRot();
         this.pRotY = (double)this.player.getYRot();
      }

      return this.canUse();
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
      if (this.mob.distanceToSqr(this.player) < 6.25) {
         this.mob.getNavigation().stop();
      } else {
         this.mob.getNavigation().moveTo(this.player, this.speedModifier);
      }
   }

   public void stop() {
      this.player = null;
      this.mob.getNavigation().stop();
      this.calmDown = reducedTickDelay(100);
      this.isRunning = false;
      System.out.println("WRTemptGoal stop: stopped tempting player ");
   }

}
