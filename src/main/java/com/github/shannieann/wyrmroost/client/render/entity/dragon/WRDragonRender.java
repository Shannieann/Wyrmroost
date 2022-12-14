package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public abstract class WRDragonRender<T extends WRDragonEntity> extends GeoEntityRenderer<T> {
    protected final String mainBoneName;
    protected float defaultXRot = 0.0f;
    private boolean rotIsSet = false;
    /**
    * @param mainBoneName isn't necessary yet for anything that doesn't use setDragonXRotation
     */
    public WRDragonRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider, String mainBoneName) {
        super(renderManager, modelProvider);
        this.mainBoneName = mainBoneName;
        //if (mainBoneName != null) defaultXRot = modelProvider.getBone(mainBoneName).getRotationX(); This isn't possible to do in constructor because GeckoLib only collects bones on first render
    }


    @Override
    public void render(GeoModel model, T animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!rotIsSet && !mainBoneName.isEmpty()) {
            defaultXRot = modelProvider.getBone(mainBoneName).getRotationX();
            rotIsSet = true;
        }

        poseStack.pushPose();
        if (model.getBone(mainBoneName).isPresent()) {
            model.getBone(mainBoneName).get().setRotationX(defaultXRot + (animatable.getDragonXRotation()/90));
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }
}
