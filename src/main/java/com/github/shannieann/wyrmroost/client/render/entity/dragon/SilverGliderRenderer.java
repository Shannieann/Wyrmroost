package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.GeckoSilverGliderModel;
import com.github.shannieann.wyrmroost.entities.dragon.SilverGliderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SilverGliderRenderer<T extends SilverGliderEntity> extends GeoEntityRenderer<T> {
    public SilverGliderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoSilverGliderModel<>());
    }

}
