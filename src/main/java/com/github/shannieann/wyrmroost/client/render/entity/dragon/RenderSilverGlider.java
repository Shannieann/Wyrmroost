package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelSilverGlider;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonGlowLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderSilverGlider extends WRDragonRender<EntitySilverGlider> {

    public RenderSilverGlider(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelSilverGlider());
        /* Glowing layers like eyes are currently super broken
         *         this.addLayer(new DragonEyesLayer<>(this,
                (entity) -> entity.getBehaviorEyesTexture(),
                getGeoModelProvider()::getModelLocation,
                (entity) -> true));
         */
        /*        this.addLayer(new DragonGlowLayer<>(this,
                (entity) -> entity.getGlowTexture(),
                getGeoModelProvider()::getModelLocation,
                (entity) -> entity.getGlowTexture() != null)); */
    }
}


