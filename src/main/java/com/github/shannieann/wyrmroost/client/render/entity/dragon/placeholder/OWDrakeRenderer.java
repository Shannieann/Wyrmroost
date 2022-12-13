package com.github.shannieann.wyrmroost.client.render.entity.dragon.placeholder;

import com.github.shannieann.wyrmroost.client.model.entity.GeckoOWDrakeModel;
import com.github.shannieann.wyrmroost.entities.dragon.EntityOverworldDrake;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OWDrakeRenderer<T extends EntityOverworldDrake> extends GeoEntityRenderer<T> {
    public OWDrakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoOWDrakeModel<>());
    }

}
