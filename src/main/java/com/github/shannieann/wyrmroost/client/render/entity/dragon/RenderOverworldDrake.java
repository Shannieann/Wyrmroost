package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelOverworldDrake;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonArmorAndSaddleLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonChestLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonRiderLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RenderOverworldDrake extends WRDragonRender<EntityOverworldDrake> {

    public RenderOverworldDrake(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelOverworldDrake<>());
        this.addLayer(new DragonArmorAndSaddleLayer<>(this, true));
        this.addLayer(new DragonChestLayer<>(this));
        this.addLayer(new DragonRiderLayer<>(this));
    }

    @Override
    public RenderType getRenderType(EntityOverworldDrake animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    // TODO: Add aging. This currently does nothing.
    public void renderLate(EntityOverworldDrake animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        //float ageProgressScale = animatable.getScale();

        //poseStack.scale(0.625f*ageProgressScale, 0.625f*ageProgressScale, 0.625f*ageProgressScale);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }

    @Override
    public void renderEarly(EntityOverworldDrake animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        //ToDO: Update all other creatures based on age progress, with the same or similar methods
        //ToDo: Test model and texture swap
        //float ageProgressScale = animatable.getScale();
        /*
        if (!animatable.isAdult()) {
            //scale factor 1, for baby model
        } else {
            //scale factor 2, for adult model
        }

         */
        //stackIn.scale(ageProgressScale,ageProgressScale,ageProgressScale);
    }
}
