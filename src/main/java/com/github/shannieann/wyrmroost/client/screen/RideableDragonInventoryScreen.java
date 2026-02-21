package com.github.shannieann.wyrmroost.client.screen;

import com.github.shannieann.wyrmroost.containers.RideableDragonInventoryContainer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import com.github.shannieann.wyrmroost.entity.dragon.WRRideableDragonEntity;
import com.github.shannieann.wyrmroost.network.DrakeJumpPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.util.Mth;

/** This file is Temu vanilla horse code */
public class RideableDragonInventoryScreen extends AbstractContainerScreen<RideableDragonInventoryContainer> {

    private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");

    private final WRRideableDragonEntity rideableDragon;
    private float jumpCharge;
    private boolean wasJumpKeyDown;

    public RideableDragonInventoryScreen(RideableDragonInventoryContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.rideableDragon = menu.getRideableDragon();
        this.imageWidth = 176;
        this.imageHeight = rideableDragon.isChested() ? 222 : 166;
    }

    @Override
    protected void init() {
        super.init();
    }

    private void updateJumpCharge() {
        boolean jumpDown = minecraft != null && minecraft.options.keyJump.isDown();
        if (jumpDown && rideableDragon instanceof EntityOverworldDrake && ((EntityOverworldDrake) rideableDragon).canJump()) {
            if (!wasJumpKeyDown) {
                jumpCharge = 0f;
            }
            wasJumpKeyDown = true;
            jumpCharge = Mth.clamp(jumpCharge + 0.05f, 0f, 1f);
        } else {
            if (wasJumpKeyDown && jumpCharge > 0f) {
                int power = (int) (jumpCharge * 100f);
                if (power > 0) {
                    DrakeJumpPacket.send(power);
                }
            }
            wasJumpKeyDown = false;
            jumpCharge = 0f;
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        updateJumpCharge();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, HORSE_INVENTORY_LOCATION);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

        if (rideableDragon instanceof EntityOverworldDrake && ((EntityOverworldDrake) rideableDragon).canJump()) {
            int barX = x + 20;
            int barY = y + imageHeight - 20;
            int barW = 18;
            int barH = 18;
            fill(poseStack, barX, barY, barX + barW, barY + barH, 0xFF555555);
            if (jumpCharge > 0f) {
                int fillW = (int) (barW * jumpCharge);
                if (fillW > 0) {
                    fill(poseStack, barX, barY, barX + fillW, barY + barH, 0xFF00AA00);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyJump.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null && minecraft.options.keyJump.matches(keyCode, scanCode)) {
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 0x404040);
    }
}
