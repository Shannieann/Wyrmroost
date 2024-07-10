package com.github.shannieann.wyrmroost.client.render.entity.dragon.placeholder;

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
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OWDrakeRenderer<T extends EntityOverworldDrake> extends GeoEntityRenderer<T> {
    public OWDrakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelOverworldDrake<>());
        this.addLayer(new DragonArmorAndSaddleLayer<>(this, true));
        this.addLayer(new DragonChestLayer<>(this));
        this.addLayer(new DragonRiderLayer<>(this));

    }

    @Override
    public RenderType getRenderType(T animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);

    }
}
