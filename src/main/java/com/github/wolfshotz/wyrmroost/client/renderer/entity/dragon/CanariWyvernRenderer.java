package com.github.wolfshotz.wyrmroost.client.renderer.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoCanariWyvernModel;
import com.github.wolfshotz.wyrmroost.entities.dragon.CanariWyvernEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CanariWyvernRenderer<T extends CanariWyvernEntity> extends GeoEntityRenderer<T> {
    public CanariWyvernRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoCanariWyvernModel<>());
    }

}
