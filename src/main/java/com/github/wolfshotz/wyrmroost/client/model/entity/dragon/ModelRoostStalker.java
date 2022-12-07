package com.github.wolfshotz.wyrmroost.client.model.entity.dragon;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.entity.dragon.DragonEyesLayer;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
        if (object.getVariant() == -1) {
            return TEXTURE_RESOURCE_SPECIAL;
        }
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RoostStalkerEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    public static class RoostStalkerEyesLayer<T extends RoostStalkerEntity> extends DragonEyesLayer<T> {

        private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes.png");
        private static final ResourceLocation EYES_TEXTURE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes_sp.png");

        public RoostStalkerEyesLayer(IGeoRenderer<T> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public RenderType getRenderType(ResourceLocation textureLocation) {
            return RenderType.eyes(textureLocation);
        }

        @Override
        protected ResourceLocation getEntityTexture(T entityIn) {
            if (entityIn.isSleeping()) return BLANK_EYES;
            if (entityIn.getVariant() == -1) {
                return EYES_TEXTURE_SPECIAL;
            }
            return EYES_TEXTURE;
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            RenderType cameo = getRenderType(getEntityTexture(entityLivingBaseIn));
            matrixStackIn.pushPose();
            //TODO: Scale model by age
            //TODO: Light up model?
            this.getRenderer().render(this.getEntityModel().getModel(MODEL_RESOURCE), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                    bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            matrixStackIn.popPose();
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
