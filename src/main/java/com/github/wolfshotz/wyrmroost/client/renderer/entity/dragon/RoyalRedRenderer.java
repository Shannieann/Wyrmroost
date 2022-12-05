package com.github.wolfshotz.wyrmroost.client.renderer.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoRoyalRedModel;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RoyalRedRenderer<T extends RoyalRedEntity> extends GeoEntityRenderer<T> {
    public RoyalRedRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoRoyalRedModel<>());
    }

}
