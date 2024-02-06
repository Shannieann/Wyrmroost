package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.EntityButterflyLeviathan;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ModelButterflyLeviathan extends AnimatedGeoModel<EntityButterflyLeviathan>
{
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/butterfly_leviathan/butterfly_leviathan.json");
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID,"geo/entity/dragon/butterfly_leviathan/butterfly_leviathan.geo.json");

    //TODO: Conduits
    @Override
    public ResourceLocation getModelLocation(EntityButterflyLeviathan object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityButterflyLeviathan object) {
        int variant = object.getVariant();
        String variantString;
        switch (variant) {
            case -1 -> variantString = "base_special";
            case 0 -> variantString = "base0";
            case 1 -> variantString = "base1";
            default -> variantString = "base0";
        }
        String textureLocation = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_"+variantString+".png";
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


        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
            if (!animatable.isUsingSwimmingNavigator()) {
                head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            }
        }


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
    public static class ButterflyLeviathanActivatedLayer<T extends EntityButterflyLeviathan> extends GeoLayerRenderer<T> {

        private static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/butterfly_leviathan/blank.png");

        public ButterflyLeviathanActivatedLayer(IGeoRenderer<T> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        protected ResourceLocation getEntityTexture(T entityIn) {
            int variant = entityIn.getVariant();
            String variantString;
            if (entityIn.canPerformLightningAttack()) {
                switch (variant) {
                    case -1 -> variantString = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_special_activated.png";
                    case 0 -> variantString = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_base0_activated.png";
                    case 1 -> variantString = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_base1_activated.png";
                    default -> variantString = "textures/entity/dragon/butterfly_leviathan/butterfly_leviathan_base0_activated.png";
                }
                return new ResourceLocation(Wyrmroost.MOD_ID,variantString);
            }
            return BLANK_TEXTURE;
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            RenderType cameo = getRenderType(getEntityTexture(entityLivingBaseIn));
            matrixStackIn.pushPose();
            this.getRenderer().render(this.getEntityModel().getModel(MODEL_RESOURCE), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                    bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            matrixStackIn.popPose();
        }

    }
}