package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelButterflyLeviathan;
import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderButterflyLeviathan extends GeoEntityRenderer<ButterflyLeviathanEntity> {
    public RenderButterflyLeviathan(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelButterflyLeviathan());
        //TODO: Shadow?
    }
    //TODO: Scale upwards based on age
}