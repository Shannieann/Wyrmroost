package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RenderButterflyLeviathan extends WRDragonRender<EntityButterflyLeviathan> {
    public RenderButterflyLeviathan(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelButterflyLeviathan());
        this.addLayer(new ModelButterflyLeviathan.ButterflyLeviathanActivatedLayer<>(this));
    }

    @Override
    public RenderType getRenderType(EntityButterflyLeviathan animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void renderEarly(EntityButterflyLeviathan animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        //ToDO: Update all other creatures based on age progress, with the same or similar methods
        //ToDo: Test model and texture swap
        float ageProgressScale = animatable.getScale();
        /*
        if (!animatable.isAdult()) {
            //scale factor 1, for baby model
        } else {
            //scale factor 2, for adult model
        }

         */
        stackIn.scale(2.4F * ageProgressScale, 2.4F * ageProgressScale, 2.4F * ageProgressScale);
    }
}