package com.github.shannieann.wyrmroost.client.screen;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec2;


public class NewTarragonTomeScreen extends AbstractContainerScreen<NewTarragonTomeContainer> {
    private final WRDragonEntity dragon;
    private final NewTarragonTomeContainer menu;
    private static final ResourceLocation TEXTURE = Wyrmroost.id("textures/gui/container/dragon_inventory.png");
    private static final ResourceLocation CHEST_TEXTURE = Wyrmroost.id("textures/gui/container/dragon_inventory_chest.png");
    private static final ResourceLocation DEPICTION_TEXTURE = Wyrmroost.id("textures/gui/container/dragon_depictions.png");
    public NewTarragonTomeScreen(NewTarragonTomeContainer container, Inventory playerInv, Component unused) { // Final argument is needed because of the supplier in WRIO

        super(container, playerInv, new TranslatableComponent("gui.wyrmroost.inventory_title"));
        this.leftPos = 0;
        this.topPos = 0;
        this.titleLabelX = 40;
        this.titleLabelY = 6;
        this.imageWidth = 176;
        this.imageHeight = 165;
        dragon = container.dragon;
        this.menu = container;
    }

    private Vec2 offsetCache;

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        // First we create the base of the ui
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (menu.hasChestSlot() && menu.hasChestEquipped()){
            RenderSystem.setShaderTexture(0, CHEST_TEXTURE);
            blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, 222);
        } else {
            RenderSystem.setShaderTexture(0, TEXTURE);
            blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        }


        // Then we add the backgrounds of the accessory slots
        if (menu.hasSaddleSlot()){
            addAccessorySlot(pPoseStack, menu.saddleSlot.x, menu.saddleSlot.y, menu.saddleSlot.hasItem(), 19);
        }
        if (menu.hasArmorSlot()){
            addAccessorySlot(pPoseStack, menu.armorSlot.x, menu.armorSlot.y, menu.armorSlot.hasItem(), 36);
        }
        if (menu.hasChestSlot()){
            addAccessorySlot(pPoseStack, menu.chestSlot.x, menu.chestSlot.y, menu.chestSlot.hasItem(), 53);
        }
        if (menu.hasExtraSlot()){
            addAccessorySlot(pPoseStack, menu.extraSlot.x, menu.extraSlot.y, menu.extraSlot.hasItem(), 70);
        }

        // Switch to the depictions texture
        RenderSystem.setShaderTexture(0, DEPICTION_TEXTURE);
        final int xPixelsBetweenDepictions = 130, yPixelsBetweenDepictions = 56;

        // Then add the depiction based on the dragon.

        Vec2 pos = (offsetCache == null)? offsetCache = dragon.getTomeDepictionOffset(): offsetCache;
        blit(pPoseStack, this.leftPos + 25, this.topPos + 17, pos.x * xPixelsBetweenDepictions, pos.y * yPixelsBetweenDepictions, 126, 54, 256, 512);
    }

    private void addAccessorySlot(PoseStack pPoseStack, int x, int y, boolean slotHasItem, int textureY){
        blit(pPoseStack, this.leftPos + x, this.topPos + y, 195, 1, 16, 16, 256, 256);
        if (!slotHasItem) blit(pPoseStack, this.leftPos + x, this.topPos + y, 195, textureY, 16, 16, 256, 256);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack); // Makes the background dark
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    // FOR BUTTONS LATER

    /*@Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ExtendedButton(xPos, yPos, width, height, title, btn -> {
            // Do stuff when you click the button
        }));
    }*/



    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, this.title, this.titleLabelX, this.titleLabelY, 0x404040); // Add title "Dragon Inventory"
    }
}
