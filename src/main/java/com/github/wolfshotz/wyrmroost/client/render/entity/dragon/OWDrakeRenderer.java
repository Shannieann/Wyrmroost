package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.GeckoOWDrakeModel;
import com.github.wolfshotz.wyrmroost.entities.dragon.OverworldDrakeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OWDrakeRenderer<T extends OverworldDrakeEntity> extends GeoEntityRenderer<T> {
    public OWDrakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoOWDrakeModel<>());
    }

}
