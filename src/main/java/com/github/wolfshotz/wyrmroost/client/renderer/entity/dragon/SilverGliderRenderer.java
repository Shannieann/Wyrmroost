package com.github.wolfshotz.wyrmroost.client.renderer.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoSilverGliderModel;
import com.github.wolfshotz.wyrmroost.entities.dragon.SilverGliderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SilverGliderRenderer<T extends SilverGliderEntity> extends GeoEntityRenderer<T> {
    public SilverGliderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoSilverGliderModel<>());
    }

}
