package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRooststalker;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRooststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderRooststalker extends WRDragonRender<EntityRooststalker> {

    public RenderRooststalker(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelRooststalker());
        this.addLayer(new DragonEyesLayer<>(this,
                (entity) -> entity.getBehaviorEyesTexture(),
                getGeoModelProvider()::getModelLocation,
                (entity) -> true));
        this.addLayer(new ModelRooststalker.RooststalkerMouthItemLayer<>(this));
    }

    @Override
    public void renderLate(EntityRooststalker animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        //float ageProgressScale = animatable.getScale();

        //poseStack.scale(0.625f*ageProgressScale, 0.625f*ageProgressScale, 0.625f*ageProgressScale);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }
}


