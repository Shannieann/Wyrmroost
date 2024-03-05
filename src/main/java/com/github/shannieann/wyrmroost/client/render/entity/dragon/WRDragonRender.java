package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public abstract class WRDragonRender<T extends WRDragonEntity> extends GeoEntityRenderer<T> {
    protected final String mainBoneName;
    protected float defaultXRot = 0.0f;
    private boolean rotIsSet = false;

    public WRDragonRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider){
        this(renderManager,modelProvider, "");
    }
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

        poseStack.pushPose();
        handleXRotation(poseStack, model, animatable);
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    @Override
    public void renderLate(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float scale = animatable.getScale();
        poseStack.scale(scale, scale, scale);
        super.renderLate(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void handleXRotation(PoseStack poseStack, GeoModel model, T animatable){
        // Sets the default rotation that the dragon should be at.
        // For example, the Royal Red's main bone is at 6 degrees by default, so without this it would always be slightly tilted downward.
        if (!rotIsSet && !mainBoneName.isEmpty()) {
            defaultXRot = modelProvider.getBone(mainBoneName).getRotationX();
            rotIsSet = true;
        }

        // Set rotations based on stuff in WRDragonEntity class
        // For flying
        if (model.getBone(mainBoneName).isPresent()) {
            GeoBone bone = model.getBone(mainBoneName).get();
            bone.setRotationX(defaultXRot + (animatable.getDragonXRotation()/57));
            animatable.cameraRotVector = new Vector3f((defaultXRot + animatable.getDragonXRotation()), bone.getRotationY(), bone.getRotationZ());
        }
    }
}
