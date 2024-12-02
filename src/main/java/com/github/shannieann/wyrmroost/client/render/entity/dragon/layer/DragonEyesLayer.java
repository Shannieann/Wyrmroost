package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRooststalker;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;

import java.util.function.Function;

public class DragonEyesLayer<T extends WRDragonEntity> extends AbstractLayerGeo<T> {

    // TODO make blinking/closing eyes not use this... instead use offset eyes in the model (might need to ask modelers for this)
    public static final ResourceLocation BLANK_EYES = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/blank_eyes.png");

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

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!shouldRender.apply(entityLivingBaseIn)) return;

        ResourceLocation location;
        if (entityLivingBaseIn.isSleeping()){
            location = BLANK_EYES;
        } else location = funcGetCurrentTexture.apply(entityLivingBaseIn);

        reRenderCurrentModelInRenderer(entityLivingBaseIn, partialTicks, matrixStackIn, bufferIn, packedLightIn,
                RenderType.eyes(location));
    }

}