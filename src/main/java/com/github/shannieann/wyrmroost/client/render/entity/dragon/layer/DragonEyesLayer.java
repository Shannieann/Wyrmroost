package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRooststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public abstract class DragonEyesLayer<T extends EntityRooststalker> extends GeoLayerRenderer<T> {

    // TODO make blinking/closing eyes not use this... instead use offset eyes in the model (might need to ask modelers for this)
    protected static final ResourceLocation BLANK_EYES = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/blank_eyes.png");
    protected final ResourceLocation MODEL_RESOURCE;

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation) {
        return RenderType.eyes(textureLocation);
    }

    public DragonEyesLayer(IGeoRenderer<T> entityRendererIn, ResourceLocation resource) {
        super(entityRendererIn);
        this.MODEL_RESOURCE = resource;
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