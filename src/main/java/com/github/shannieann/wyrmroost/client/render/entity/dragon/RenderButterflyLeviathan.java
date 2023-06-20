package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelButterflyLeviathan;
import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRoostStalker;
import com.github.shannieann.wyrmroost.entities.dragon.EntityButterflyLeviathan;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderButterflyLeviathan extends WRDragonRender<EntityButterflyLeviathan> {
    public RenderButterflyLeviathan(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelButterflyLeviathan());
        this.addLayer(new ModelButterflyLeviathan.ButterflyLeviathanActivatedLayer<>(this));
        //TODO: Shadow?
    }

    @Override
    public RenderType getRenderType(EntityButterflyLeviathan animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    //TODO: REMOVE
    @Override
    protected int getBlockLightLevel(EntityButterflyLeviathan entityIn, BlockPos partialTicks) {
        return 15;
    }
    //TODO: Scale upwards based on age


    @Override
    public void renderEarly(EntityButterflyLeviathan animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
                            float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
                red, green, blue, partialTicks);

        stackIn.scale(2.4F, 2.4F, 2.4F);
    }
}