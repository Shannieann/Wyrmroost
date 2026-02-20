package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCoinDragon;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCoinDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RenderCoinDragon extends WRDragonRender<EntityCoinDragon> {

    public RenderCoinDragon(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCoinDragon());
    }

    // Hopefully this fixes face culling issue?
    @Override
    public RenderType getRenderType(EntityCoinDragon animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void renderLate(EntityCoinDragon animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
