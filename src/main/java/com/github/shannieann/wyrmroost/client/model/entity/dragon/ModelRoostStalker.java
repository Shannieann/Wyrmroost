package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ModelRoostStalker extends AnimatedGeoModel<RoostStalkerEntity> {
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/roost_stalker/roost_stalker.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker.png");
    private static final ResourceLocation TEXTURE_RESOURCE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_sp.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/roost_stalker/roost_stalker.json");

    @Override
    public ResourceLocation getModelLocation(RoostStalkerEntity object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(RoostStalkerEntity object) {
        switch (object.getVariant()){
            case "special" -> { return TEXTURE_RESOURCE_SPECIAL;}
            default -> {return  TEXTURE_RESOURCE;}
        }
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RoostStalkerEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    public static class RoostStalkerEyesLayer<T extends RoostStalkerEntity> extends DragonEyesLayer<T> {

        private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes.png");
        private static final ResourceLocation EYES_TEXTURE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes_sp.png");

        public RoostStalkerEyesLayer(IGeoRenderer<T> entityRendererIn) {
            super(entityRendererIn, ModelRoostStalker.MODEL_RESOURCE);
        }


        @Override
        protected ResourceLocation getEntityTexture(T entityIn) {
            if (entityIn.isSleeping()) return BLANK_EYES;
            switch (entityIn.getVariant()){
                case "special" -> {return EYES_TEXTURE_SPECIAL;}
                default -> {return EYES_TEXTURE;}
            }
        }

    }
}


    //TODO: EYES
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
