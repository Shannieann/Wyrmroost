package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRoyalRed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ModelRoyalRed extends AnimatedGeoModel<EntityRoyalRed>
{
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/royal_red/royal_red.json");

    @Override
    public ResourceLocation getModelLocation(EntityRoyalRed object) {
        String modelLocation = "geo/entity/dragon/royal_red/royal_red_" + ((object.isAdult() ? "adult" : "child") + ".geo.json");
        return new ResourceLocation(Wyrmroost.MOD_ID, modelLocation);
    }
    //TODO: Different set of textures for babies
    @Override
    public ResourceLocation getTextureLocation(EntityRoyalRed object) {
        String textureLocation = "textures/entity/dragon/royal_red/royal_red_" + object.getVariant() + "_" + object.getGender() + "_" + (object.isUsingFlyingNavigator() && !object.isDiving()? "fly" : "land") + ".png";
        return new ResourceLocation(Wyrmroost.MOD_ID, textureLocation);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityRoyalRed animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(EntityRoyalRed animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }
    }

}