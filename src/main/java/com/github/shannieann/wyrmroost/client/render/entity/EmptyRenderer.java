package com.github.shannieann.wyrmroost.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T>
{
    public EmptyRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return null;
    }
}
