package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoRoostStalkerModel;
import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoRoostStalkerModel.RoostStalkerEyesLayer;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RoostStalkerRenderer2<T extends RoostStalkerEntity> extends GeoEntityRenderer<T> {
    public RoostStalkerRenderer2(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoRoostStalkerModel<>());
        this.addLayer(new RoostStalkerEyesLayer<>(this));
    }

}
