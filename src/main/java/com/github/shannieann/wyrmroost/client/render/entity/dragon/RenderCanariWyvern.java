package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCanariWyvern;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCanariWyvern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RenderCanariWyvern extends WRDragonRender<EntityCanariWyvern> {

    public RenderCanariWyvern(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCanariWyvern());
    }

    // Hopefully this fixes face culling issue?
    @Override
    public RenderType getRenderType(EntityCanariWyvern animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
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


