package com.github.shannieann.wyrmroost.client.render.entity.effect;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.model.entity.effect.ModelLightningSphere;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import javax.annotation.Nullable;

public class RenderLightningSphere extends GeoProjectilesRenderer<EffectLightningSphere> {
    public RenderLightningSphere(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelLightningSphere());
        //TODO: Shadow?
        this.shadowRadius = 0.0F;
    }

    @Override
    public void renderEarly(EffectLightningSphere animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        float scalingFactor = 25.0F;
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        stackIn.scale(0.125F*(animatable.tickCount*scalingFactor), 0.125F*(animatable.tickCount*scalingFactor), 0.125F*(animatable.tickCount*scalingFactor));

    }

    @Override
    public RenderType getRenderType(EffectLightningSphere animatable, float partialTick, PoseStack poseStack,
                                     @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight,
                                     ResourceLocation texture) {
        return RenderType.entityTranslucent(new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/effect/lightning_sphere/lightning_sphere.png"));
    }
}

