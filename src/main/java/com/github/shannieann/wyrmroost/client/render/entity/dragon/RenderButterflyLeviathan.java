package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelButterflyLeviathan;
import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderButterflyLeviathan extends GeoEntityRenderer<ButterflyLeviathanEntity> {
    public RenderButterflyLeviathan(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelButterflyLeviathan());
        //TODO: Shadow?
    }
    //TODO: Scale upwards based on age

    /*
    @Override
    public void renderEarly(ButterflyLeviathanEntity animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);

        stackIn.scale(0.2F,0.2F,0.2F);


     */
}
