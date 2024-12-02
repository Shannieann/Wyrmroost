package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelOverworldDrake;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonArmorAndSaddleLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonChestLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonRiderLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderOverworldDrake<T extends EntityOverworldDrake> extends WRDragonRender<T> {
    public RenderOverworldDrake(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelOverworldDrake<>());
        this.addLayer(new DragonArmorAndSaddleLayer<>(this, true));
        this.addLayer(new DragonChestLayer<>(this));
        this.addLayer(new DragonRiderLayer<>(this));

    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);

    }
    @Override
    public void renderEarly(T animatable, PoseStack stackIn, float ticks,
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
