package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelAlpineDragon;
import com.github.shannieann.wyrmroost.entity.dragon.EntityAlpineDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderAlpineDragon extends WRDragonRender<EntityAlpineDragon> {

    public RenderAlpineDragon(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelAlpineDragon<>());
    }


    @Override
    public void renderEarly(EntityAlpineDragon animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        //ToDO: Update all other creatures based on age progress, with the same or similar methods
        //ToDo: Test model and texture swap
        float ageProgressScale = animatable.getScale();
        stackIn.scale(ageProgressScale,  ageProgressScale, ageProgressScale);
    }
}






