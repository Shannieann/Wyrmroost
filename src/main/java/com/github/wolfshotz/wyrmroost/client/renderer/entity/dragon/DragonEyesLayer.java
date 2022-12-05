package com.github.wolfshotz.wyrmroost.client.renderer.entity.dragon;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public abstract class DragonEyesLayer<T extends RoostStalkerEntity> extends GeoLayerRenderer<T> {

    protected static final ResourceLocation BLANK_EYES = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/blank_eyes.png");

    public DragonEyesLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }
}
