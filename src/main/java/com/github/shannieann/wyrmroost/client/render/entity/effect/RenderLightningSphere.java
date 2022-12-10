package com.github.shannieann.wyrmroost.client.render.entity.effect;

import com.github.shannieann.wyrmroost.client.model.entity.effect.ModelLightningSphere;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

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
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        stackIn.scale(0.125F*animatable.tickCount, 0.125F*animatable.tickCount, 0.125F*animatable.tickCount);
    }
}

