package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.EntityButterflyLeviathan;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelButterflyLeviathan extends AnimatedGeoModel<EntityButterflyLeviathan>
{
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/butterfly_leviathan/butterfly_leviathan.json");

    //TODO: All model variants
    @Override
    public ResourceLocation getModelLocation(EntityButterflyLeviathan object) {
        String modelLocation = "geo/entity/dragon/butterfly_leviathan/butterfly_leviathan_" + ((object.isAdult() ? "adult" : "child") + ".geo.json");
        return new ResourceLocation(Wyrmroost.MOD_ID, modelLocation);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityButterflyLeviathan object) {
        String textureLocation = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_" + object.getVariant() + "_" + object.getGender() +".png";
        return new ResourceLocation(Wyrmroost.MOD_ID, textureLocation);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityButterflyLeviathan animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(EntityButterflyLeviathan animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        float rotationYawMultiplier = 1.4F;
        float rotationPitchMultiplier = 10.0F;

        //TODO: Head pitch conflicts with dynamic pitch, enable head yaw only
        /*
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }
        */

        float setPitchValue;
        if (animatable.isUsingSwimmingNavigator()) {
            if (!animatable.getBreaching()) {
                setPitchValue = (animatable.currentPitchRadians+(animatable.targetPitchRadians-animatable.currentPitchRadians)*animationEvent.getPartialTick());
            } else {
                setPitchValue = animatable.currentPitchRadians;
            }
            this.getAnimationProcessor().getBone("ibody1").setRotationX(-setPitchValue);
        }

        if (animatable.isUsingSwimmingNavigator() && !animatable.getBreaching()) {
            //deltaYaw operations
            float setYawValue = animatable.prevSetYaw+(animatable.setYaw-animatable.prevSetYaw)*animationEvent.getPartialTick();
            this.getAnimationProcessor().getBone("ibody2").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail1").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail2").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail3").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail4").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail5").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail7").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("itail6").setRotationY(setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("ineck1").setRotationY(-setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("ineck2").setRotationY(-setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("ineck3").setRotationY(-setYawValue * rotationYawMultiplier);
            this.getAnimationProcessor().getBone("ineck4").setRotationY(-setYawValue * rotationYawMultiplier);
            //deltaPitch operations
            float setExtremityPitchValue = animatable.prevSetExtremityPitch+(animatable.setExtremityPitch-animatable.prevSetExtremityPitch)*animationEvent.getPartialTick();
            this.getAnimationProcessor().getBone("ineck1").setRotationX(-setExtremityPitchValue * rotationPitchMultiplier);
            this.getAnimationProcessor().getBone("itail1").setRotationX(-setExtremityPitchValue * rotationPitchMultiplier);
        }
    }
}