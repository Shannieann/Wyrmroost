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

        float setPitchValue = animatable.prevSetPitch+(animatable.setPitch-animatable.prevSetPitch)*animationEvent.getPartialTick();
        if (animatable.isSwimming() && !animatable.level.getBlockState(animatable.blockPosition().below()).canOcclude()) {
            setPitchValue = Mth.clamp(setPitchValue, -0.785F,0.785F);
            (this.getAnimationProcessor().getBone("body1")).setRotationX(setPitchValue);
        }

        if (animatable.isSwimming()) {
            float setYawValue = animatable.prevSetYaw+(animatable.setYaw-animatable.prevSetYaw)*animationEvent.getPartialTick();
            (this.getAnimationProcessor().getBone("body2")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail1")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail2")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail3")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail4")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail5")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail7")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("itail6")).setRotationY(setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("ineck1")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("ineck2")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("ineck3")).setRotationY(-setYawValue * rotationMultiplier);
            (this.getAnimationProcessor().getBone("ineck4")).setRotationY(-setYawValue * rotationMultiplier);
        }
    }
}