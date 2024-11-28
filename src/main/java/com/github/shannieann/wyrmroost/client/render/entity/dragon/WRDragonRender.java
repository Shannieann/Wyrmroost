package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public abstract class WRDragonRender<T extends WRDragonEntity> extends GeoEntityRenderer<T> {
    protected final String mainBoneName;
    protected Float defaultXRot = 0.0f;

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
        if (animatable.dragonCanFly())
            handleXRotation(model, animatable);
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    private void handleXRotation(GeoModel model, T animatable){
        // Sets the default rotation that the dragon should be at.
        // For example, the Royal Red's main bone is at 6 degrees by default, so without this it would always be slightly tilted downward.
        if (defaultXRot == null && !mainBoneName.isEmpty()) {
            defaultXRot = modelProvider.getBone(mainBoneName).getRotationX();
        }

        // Set rotations based on stuff in WRDragonEntity class
        // For flying
        if (model.getBone(mainBoneName).isPresent()) {
            GeoBone bone = model.getBone(mainBoneName).get();

            final float newRot = defaultXRot + (animatable.getDragonXRotation()/57);
            bone.setRotationX(newRot);

        }
    }
}
