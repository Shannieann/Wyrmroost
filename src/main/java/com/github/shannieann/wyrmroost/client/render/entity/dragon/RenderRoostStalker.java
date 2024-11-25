package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRooststalker;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRooststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderRooststalker extends WRDragonRender<EntityRooststalker> {
        public RenderRooststalker(EntityRendererProvider.Context renderManager) {
            super(renderManager, new ModelRooststalker());
            this.addLayer(new ModelRooststalker.RooststalkerMouthItemLayer<>(this));
            this.addLayer(new ModelRooststalker.RooststalkerEyesLayer<>(this));

        }

    @Override
    public void renderLate(EntityRooststalker animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        poseStack.scale(0.625f, 0.625f, 0.625f);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }
}


