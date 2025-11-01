package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

import java.util.function.Function;

// Currently only used for silver glider (currently never used? Implement when moving to azurelib?)
public class DragonGlowLayer<T extends WRDragonEntity> extends AbstractLayerGeo<T> {

    private final Function<T, Boolean> shouldRender;

    @Override
    public RenderType getRenderType(ResourceLocation textureLocation) {
        return RenderType.eyes(textureLocation); // I guess this is emissive (glow) and works for things besides eyes?
    }

    public DragonGlowLayer(GeoEntityRenderer<T> entityRendererIn,
                           Function<T, ResourceLocation> modelResource,
                           Function<T, ResourceLocation> textureResource,
                            Function<T, Boolean> shouldRender) {
        super(entityRendererIn, modelResource, textureResource);
        this.shouldRender = shouldRender;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!shouldRender.apply(dragon)) {
            return;
        }

        ResourceLocation location = funcGetCurrentTexture.apply(dragon);
        System.out.println("Glow texture: " + location);

        // Get the appropriate render type for this texture
        RenderType renderType = getRenderType(location);

        // Render the model with the correct texture and render type
        reRenderCurrentModelInRenderer(dragon, partialTicks, matrixStackIn, bufferIn, packedLightIn, renderType);
    }

}