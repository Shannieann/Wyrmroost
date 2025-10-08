package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

import java.util.function.Function;

public class DragonEyesLayer<T extends WRDragonEntity> extends AbstractLayerGeo<T> {

    private final Function<T, Boolean> shouldRender;
    @Override
    public RenderType getRenderType(ResourceLocation textureLocation) {
        return RenderType.eyes(textureLocation);
    }

    public DragonEyesLayer(GeoEntityRenderer<T> entityRendererIn,
                           Function<T, ResourceLocation> modelResource,
                           Function<T, ResourceLocation> textureResource,
                            Function<T, Boolean> shouldRender) {
        super(entityRendererIn, modelResource, textureResource);
        this.shouldRender = shouldRender;
    }

    /*
     * Old render code, before Koala made the eyes glowy
     *
     *  RenderType cameo = getRenderType(getEntityTexture(entityLivingBaseIn));
        matrixStackIn.pushPose();
        this.getRenderer().render(this.getEntityModel().getModel(MODEL_RESOURCE), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
                bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        matrixStackIn.popPose();
    */

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!shouldRender.apply(dragon)) {
            return;
        }

        ResourceLocation location;

        if (dragon.getSleeping()) {
            location = dragon.getClosedEyesTexture();
        } else {
            location = funcGetCurrentTexture.apply(dragon);
        }

        reRenderCurrentModelInRenderer(dragon, partialTicks, matrixStackIn, bufferIn, packedLightIn,
                RenderType.eyes(location));
    }

}