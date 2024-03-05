package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRoostStalker;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRoostStalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderRoostStalker extends WRDragonRender<EntityRoostStalker> {
        public RenderRoostStalker(EntityRendererProvider.Context renderManager) {
            super(renderManager, new ModelRoostStalker());
            // TODO readd glowing eyes. Will need to create textures separating the eyes for this.
            // TODO Me and sniffity discussed separating patterns and colors before the hiatus.
            // However, this wouldn't really work since each pattern has different colors based on the skin color...
            // So... maybe we make a black pattern texture and select the colors in code?
            // Idk it'll be a tough issue
            // Cause otherwise we need a separate texture for each pattern and color combo, which would probably be way too many.
            //this.addLayer(new ModelRoostStalker.RoostStalkerEyesLayer<>(this));
            this.addLayer(new ModelRoostStalker.RoostStalkerMouthItemLayer<>(this));
            //TODO: Shadow?
            this.shadowRadius = 0.0F;
        }

    @Override
    public void renderLate(EntityRoostStalker animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        poseStack.scale(0.625f, 0.625f, 0.625f);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }
}


