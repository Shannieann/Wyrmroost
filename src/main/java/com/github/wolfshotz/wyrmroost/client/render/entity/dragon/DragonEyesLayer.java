package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public abstract class DragonEyesLayer<T extends TameableDragonEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public DragonEyesLayer(RenderLayerParent<T, M> p_116981_) {
        super(p_116981_);
    }

    public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, T dragon, float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
        VertexConsumer vertexconsumer = p_116984_.getBuffer(this.renderType(dragon));
        this.getParentModel().renderToBuffer(p_116983_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public abstract RenderType renderType(T dragon);
}