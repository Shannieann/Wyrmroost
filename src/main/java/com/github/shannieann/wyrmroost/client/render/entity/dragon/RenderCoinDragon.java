package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCoinDragon;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCoinDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderCoinDragon extends WRDragonRender<EntityCoinDragon> {

    public RenderCoinDragon(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCoinDragon());
    }

    @Override
    public void renderLate(EntityCoinDragon animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
