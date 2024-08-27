package com.github.shannieann.wyrmroost.client.render.entity.dragon_egg;

import com.github.shannieann.wyrmroost.client.model.entity.dragonegg.ModelDragonEgg;
import com.github.shannieann.wyrmroost.entity.dragon_egg.WRDragonEggEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderDragonEgg extends GeoEntityRenderer<WRDragonEggEntity> {
    public RenderDragonEgg(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelDragonEgg());

    }

}