package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.shannieann.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ModelButterflyLeviathan extends AnimatedGeoModel<ButterflyLeviathanEntity>
{
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/butterfly_leviathan/butterfly_leviathan.json");

    //TODO: All model variants
    @Override
    public ResourceLocation getModelLocation(ButterflyLeviathanEntity object) {
        String modelLocation = "geo/entity/dragon/butterfly_leviathan/butterfly_leviathan_" + ((object.isAdult() ? "adult" : "child") + ".geo.json");
        return new ResourceLocation(Wyrmroost.MOD_ID, modelLocation);
    }

    @Override
    public ResourceLocation getTextureLocation(ButterflyLeviathanEntity object) {
        String textureLocation = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_" + object.getVariant() + "_" + object.getGender() +".png";
        return new ResourceLocation(Wyrmroost.MOD_ID, textureLocation);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ButterflyLeviathanEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(ButterflyLeviathanEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);

        IBone head = this.getAnimationProcessor().getBone("head");
        float rotationMultiplier = 5.0F;
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }

        float setPitchValue = animatable.prevRotationPitch+(animatable.rotationPitch-animatable.prevRotationPitch)*animationEvent.getPartialTick();
        if (animatable.isSwimming() && !animatable.level.getBlockState(animatable.blockPosition().below()).canOcclude()) {
            setPitchValue = Mth.clamp(setPitchValue, -0.785F,0.785F);
            (this.getAnimationProcessor().getBone("body1")).setRotationX(setPitchValue);
        }

        if (animatable.isSwimming()) {
            float setYawValue = animatable.prevSetYaw+(animatable.setYaw-animatable.prevSetYaw)*animationEvent.getPartialTick();
            (this.getAnimationProcessor().getBone("body2")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail1")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail2")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail3")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail4")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail5")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail7")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("tail6")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("neck1")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("neck2")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("neck3")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("neck4")).setRotationY(-setYawValue * rotationMultiplier);
        }
    }
}