package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelRoyalRed extends AnimatedGeoModel<RoyalRedEntity>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/royal_red/royal_red.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/royal_red.png");
    //TODO: Uniform variant numbering
    private static final ResourceLocation TEXTURE_RESOURCE_1 = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/royal_red.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/royal_red/royal_red.json");

    //TODO: All model variants
    @Override
    public ResourceLocation getModelLocation(RoyalRedEntity object) {
        return MODEL_RESOURCE;
    }

    //TODO: All texture variants
    @Override
    public ResourceLocation getTextureLocation(RoyalRedEntity object) {
        if (object.getVariant() == 1) {
            return TEXTURE_RESOURCE_1;
        }
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RoyalRedEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    //TODO: Is this necessary?
    /*
    @Override
    public void setCustomAnimations(T animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("neck");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }
    }
    */
}