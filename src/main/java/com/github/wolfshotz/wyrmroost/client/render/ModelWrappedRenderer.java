package com.github.wolfshotz.wyrmroost.client.render;

// I'm removing this maybe temporarily for the port. This just doesn't port over well, also I'm probably soft-rewriting a lot of other things
// Also having the renderer and model separate just feels cleaner to me but idk, I'm probably gonna bring this back at some point.

/*
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.function.Supplier;

/**
 * Purpose of this class is to remove the need for creating a different "renderers" for each and every different entity model.
 * Achieved through making similar methods within a "wrapper model."
 * For example, instead of getting the texture from the renderer, we instead do it in the model.
 * */
/*
public class ModelWrappedRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M>
{
    public ModelWrappedRenderer(EntityRenderDispatcher manager, M model)
    {
        super(manager, model, 0f);

        addLayer(new RenderLayer<T, M>(this) // rendering overlays such as glowing eyes, armors, etc.
        {
            @Override
            public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yaw, float pitch)
            {
                model.postProcess(entity, ms, buffer, light, limbSwing, limbSwingAmount, age, yaw, pitch, partialTicks);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityRenderer<T> factory(Supplier<Supplier<EntityModel<T>>> model)
    {
        return m -> (EntityRenderer<? super T>) new ModelWrappedRenderer<>(m, (EntityModel<Mob>) model.get().get());
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light)
    {
        this.shadowRadius = model.getShadowRadius(entity);
        super.render(entity, yaw, partialTicks, ms, buffer, light);
    }

    @Override
    protected void scale(T entity, PoseStack ms, float partialTicks)
    {
        model.scale(entity, ms, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return model.getTexture(entity);
    }
}*/
