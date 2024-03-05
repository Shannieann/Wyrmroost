package com.github.shannieann.wyrmroost.client.render.entity.dragon.placeholder;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelCanariWyvern;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCanariWyvern;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CanariWyvernRenderer<T extends EntityCanariWyvern> extends GeoEntityRenderer<T> {
    public CanariWyvernRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelCanariWyvern<>());
    }

}
