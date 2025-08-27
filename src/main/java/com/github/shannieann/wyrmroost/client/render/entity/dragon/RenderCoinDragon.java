package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCoinDragon;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCoinDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderCoinDragon extends WRDragonRender<EntityCoinDragon> {
    // TODO: add coin dragon eyes
    // private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/coin_dragon/coin_dragon_eyes.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/blank_eyes.png");

    public RenderCoinDragon(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCoinDragon());
        // TODO: Add this back in when eye layer is made
        // this.addLayer(new DragonEyesLayer<>(this,
        //         (entity) -> EYES_TEXTURE,
        //         getGeoModelProvider()::getModelLocation,
        //         (entity) -> true));
    }

    @Override
    public void renderLate(EntityCoinDragon animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
