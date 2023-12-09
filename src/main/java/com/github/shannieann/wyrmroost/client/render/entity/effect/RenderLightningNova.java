package com.github.shannieann.wyrmroost.client.render.entity.effect;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.model.effect.ModelLightningNova;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningNova;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

import javax.annotation.Nullable;

public class RenderLightningNova extends GeoProjectilesRenderer<EffectLightningNova> {
    public RenderLightningNova(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelLightningNova());
        //TODO: Shadow?
        this.shadowRadius = 0.0F;
    }

    @Override
    public void renderEarly(EffectLightningNova animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        float scalingFactor = 25.0F;
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);
        //ToDo: Temporarily disabled scaled
        //stackIn.scale(0.125F*(animatable.tickCount*scalingFactor), 0.125F*(animatable.tickCount*scalingFactor), 0.125F*(animatable.tickCount*scalingFactor));
    }

    private static final ResourceLocation[] TEXTURE_RESOURCE = new ResourceLocation[] {
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_1.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_2.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_3.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_4.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_5.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_6.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_7.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_8.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_9.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_10.png")
    };

    @Override
    public RenderType getRenderType(EffectLightningNova animatable, float partialTick, PoseStack poseStack,
                                    @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight,
                                    ResourceLocation texture) {
        int index = animatable.tickCount % TEXTURE_RESOURCE.length;
        return RenderType.entityTranslucent(TEXTURE_RESOURCE[index]);
    }
}