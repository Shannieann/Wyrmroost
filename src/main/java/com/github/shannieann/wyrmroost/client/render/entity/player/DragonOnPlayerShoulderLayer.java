package com.github.shannieann.wyrmroost.client.render.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class DragonOnPlayerShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {

    public DragonOnPlayerShoulderLayer(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
        super(pRenderer);
    }

    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pNetHeadYaw, pHeadPitch, true);
        this.render(pMatrixStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pNetHeadYaw, pHeadPitch, false);
     }

    private void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pNetHeadYaw, float pHeadPitch, boolean pLeftShoulder)
    {
        CompoundTag tag = pLeftShoulder ? pLivingEntity.getShoulderEntityLeft() : pLivingEntity.getShoulderEntityRight();
        String dragonSpecies = (tag.getString("id")); // Should be filled by setEntityOnShoulder in Canari Wyvern, Silver Glider, and other classes that ride player
        System.out.println("Found entity on shoulder with id: " + tag.getString("id") + ". Is on left shoulder: " + pLeftShoulder);

        if (! dragonSpecies.isEmpty()) {

            // Be very safe when setting passenger in case data is not synced everywhere at the same time
            Entity passenger = null;
            if (!pLivingEntity.getPassengers().isEmpty()) {
                if (pLeftShoulder && pLivingEntity.getPassengers().size() > 0) {
                    passenger = pLivingEntity.getPassengers().get(0);
                } else if (!pLeftShoulder && pLivingEntity.getPassengers().size() > 1) {
                    passenger = pLivingEntity.getPassengers().get(1);
                }
            }

            if (passenger == null) {
                return;
            }

            pMatrixStack.pushPose();

            switch (dragonSpecies) {
                case "CanariWyvern": // Can have up to 2: 1 on each shoulder.
                    pMatrixStack.translate(pLeftShoulder ? 0.4000000059604645 : -0.4000000059604645, pLivingEntity.isCrouching() ? -1.2999999523162842 : -1.5, 0.0);
                    EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                    // Use 0 for x/y/z since translation is handled by pose stack
                    dispatcher.render(passenger, 0.0, 0.0, 0.0, pNetHeadYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
                    break;

                case "SilverGlider": // Can only have 1. In code, set as "left shoulder", but is displayed on head. Can't mix and match with canari anyways.
                    // TODO: Maybe have it hang on back?
                    break;

                default:
                    throw new IllegalArgumentException("Unknown dragon species: " + dragonSpecies);
            }
            pMatrixStack.popPose();
        }
    }

}

