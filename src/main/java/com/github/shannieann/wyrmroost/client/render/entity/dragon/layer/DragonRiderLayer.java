package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.*;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

// MAJOR inspiration from Ice and Fire's rendering for this class. Thanks Alex!

public class DragonRiderLayer<T extends WRDragonEntity> extends GeoLayerRenderer<T> {
    protected final GeoModelProvider provider;
    protected GeoModel model;

    public DragonRiderLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        this.provider = this.entityRenderer.getGeoModelProvider();
    }

    // The "real" player is hidden in ClientEvents#hidePlayerWhenOnDragon
    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (dragon.isBaby() || provider == null){
            return;
        }
        matrixStackIn.pushPose();
        model = provider.getModel(provider.getModelLocation(dragon));

        if (!dragon.getPassengers().isEmpty()) {
            int passengerIndex = 1;
            // Go through all the passengers.
            for (Entity passenger : dragon.getPassengers()) {
                // To be honest I kind of forget why we remove the passenger before doing everything else but I know it's important to do.
                ClientEvents.dragonRiders.remove(passenger.getUUID());
                //float riderYaw = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTicks;
                float riderYaw = dragon.getYRot();
                translateToBody(matrixStackIn, model, passengerIndex, dragon, passenger); // TODO maybe make this only activate on needed frames? EDIT: Probably not, each animation is different and it wouldn't be worth it
                matrixStackIn.translate(0.0, -0.6f, 0.0);
                matrixStackIn.pushPose();
                matrixStackIn.mulPose(new Quaternion(Vector3f.YP, riderYaw + 180, true));
                renderEntity(passenger, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.popPose();
                passengerIndex++;
                ClientEvents.dragonRiders.add(passenger.getUUID());
            }
        }
        matrixStackIn.popPose();
    }

    //ToDo: Verify rider2 bones on each dragon
    protected void translateToBody(PoseStack stack, GeoModel model, int passengerIndex, T dragon, Entity passenger) {
        if (model.getBone("rider" + passengerIndex).isEmpty()) {
            throw new ReportedException(CrashReport.forThrowable(new Throwable(), "Dragon should have a bone named 'rider" + passengerIndex + "' to have a rider layer!"));
        }

        // Get the rider bone, which should be present if the passenger is able to get to this spot.
        GeoBone bone = model.getBone("rider" + passengerIndex).get();
        Vector3d modelPos = bone.getModelPosition();
        // Scale by 1/16 to get from block bench coordinates to minecraft coordinates.
        modelPos.scale(0.0625f);
        // Translate the player accordingly
        stack.translate(modelPos.x, modelPos.y, modelPos.z);



        Matrix4f rot = bone.getModelRotationMat();
        // Get the bone's rotation and scale accordingly.
        stack.mulPoseMatrix(rot);


        if (model.getBone("cameraPos"+passengerIndex).isEmpty()){
            throw new ReportedException(CrashReport.forThrowable(new Throwable(), "Dragon should have a bone named 'cameraPos" + passengerIndex + "' to have a rider layer!"));
        }

        // Get the camera position placeholder bone for this passenger
        bone = model.getBone("cameraPos" + passengerIndex).get();
        // Store it in the dragon so ClientEvents can use it in the camera event.
        dragon.cameraBonePos.put(passenger.getUUID(), bone.getLocalPosition());

        // Create a 4d vector, then transform it with the camera's rotation matrix.
        // This gives us a close-enough estimation of how the camera should be rotating/moving.
        Vector4f vec = new Vector4f(1f, 1f, 1f, 1f);
        vec.transform(bone.getModelRotationMat());

        // Minus one because one is the default value of the vector if there was no rotation.
        dragon.cameraRotVector.set(vec.x() - 1f +  dragon.getDragonXRotation(), vec.y() - 1f, vec.z() - 1f);
    }




    private <E extends Entity> void renderEntity(E entityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLight) {
        EntityRenderer<? super E> render;
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();

        render = manager.getRenderer(entityIn);
        matrixStack.pushPose();
        try {
            render.render(entityIn, entityIn.yRotO, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new ReportedException(CrashReport.forThrowable(throwable1, "Rendering entity in world"));
        }
        matrixStack.popPose();
    }
}
