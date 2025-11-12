package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.tags.FluidTags;

public class WRAnimatedFloatGoal extends AnimatedGoal
{

   private String animationName;

   // TODO: Check for caustic swamp nasty water, light water, etc?
   public WRAnimatedFloatGoal(WRDragonEntity dragon, String animationName) {
      super(dragon);
      this.setFlags(EnumSet.of(Flag.JUMP));
      entity.getNavigation().setCanFloat(true);
      this.animationName = animationName;
   }

   public WRAnimatedFloatGoal(WRDragonEntity dragon) {
      super(dragon);
      this.setFlags(EnumSet.of(Flag.JUMP));
      entity.getNavigation().setCanFloat(true);
      this.animationName = "float";
   }

   @Override
   public void start() {
      super.start(animationName, AnimatedGoal.LOOP, 0);
   }

   // Cannot be used when fully immersed. Just makes dragon stay at surface, if anything pushes down it will stop goal

   @Override
   public boolean canUse() {
      return entity.getAltitude() < 1.0f
         && (entity.isInWater() || entity.isInLava()) 
         && entity.getDepth() < 1.1F;
   }

/*
   @Override
   public boolean canUse() {
      return entity.getAltitude() < 0.75f
         && ((entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() && entity.getFluidHeight(FluidTags.WATER) < entity.getBbHeight())
            || (entity.isInLava() && entity.getFluidHeight(FluidTags.LAVA) > entity.getFluidJumpThreshold() && entity.getFluidHeight(FluidTags.LAVA) < entity.getBbHeight()));
   }
*/

   @Override
   public boolean canContinueToUse() {
      return entity.getAltitude() < 1.0f
         && (entity.isInWater() || entity.isInLava()) 
         && entity.getDepth() < 1.1F;
   }

   // Don't jump like in vanilla, just float on the water
   @Override
   public void tick() {
      if (((entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold())
      || (entity.isInLava() && entity.getFluidHeight(FluidTags.LAVA) > entity.getFluidJumpThreshold()))
      && entity.getDeltaMovement().y() < 0.2f) // prevent dragon from getting launched into the sky
      {
          entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.05f, 0));
      }
   }

   @Override
   public void stop() {
      super.stop();
   }
}
