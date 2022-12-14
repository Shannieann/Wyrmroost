package com.github.shannieann.wyrmroost.mixins;


import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// For camera movements while riding a dragon
@Mixin(LevelRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(at = @At("HEAD"), method = "renderLevel")
    public void renderWorld(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        ClientEvents.onWorldRender(minecraft, pPoseStack);
    }

    @Inject(at = @At("HEAD"), method = "prepareCullFrustum")
    public void frustumCull(PoseStack pPoseStack, Vec3 pCameraPos, Matrix4f pProjectionMatrix, CallbackInfo ci){
        ClientEvents.onWorldRender(minecraft, pPoseStack);
    }
}
