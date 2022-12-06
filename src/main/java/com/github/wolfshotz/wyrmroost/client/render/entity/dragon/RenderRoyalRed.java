package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.dragon.ModelRoyalRed;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderRoyalRed extends GeoEntityRenderer<RoyalRedEntity> {
    public RenderRoyalRed(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelRoyalRed());
        //TODO: Shadow?
    }
    //TODO: Scale upwards based on age
}