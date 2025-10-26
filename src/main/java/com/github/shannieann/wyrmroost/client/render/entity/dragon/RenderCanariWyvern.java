package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCanariWyvern;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCanariWyvern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderCanariWyvern extends WRDragonRender<EntityCanariWyvern> {

    public RenderCanariWyvern(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCanariWyvern());
    }

    @Override
    // TODO: Add aging. This currently does nothing.
    public void renderLate(EntityCanariWyvern animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        //float ageProgressScale = animatable.getScale();

        //poseStack.scale(0.625f*ageProgressScale, 0.625f*ageProgressScale, 0.625f*ageProgressScale);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }
}


