package com.github.shannieann.wyrmroost.client.screen;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
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
    private final int xPixelsBetweenDepictions = 130, yPixelsBetweenDepictions = 56;
    private static final ResourceLocation TEXTURE = Wyrmroost.id("textures/gui/container/dragon_inventory.png");
    private static final ResourceLocation DEPICTION_TEXTURE = Wyrmroost.id("textures/gui/container/dragon_depictions.png");
    public NewTarragonTomeScreen(NewTarragonTomeContainer container, Inventory playerInv, Component unused) {

        super(container, playerInv, new TranslatableComponent("gui.wyrmroost.inventory_title"));
        this.leftPos = 0;
        this.topPos = 0;
        this.titleLabelX = 40;
        this.titleLabelY = 6;
        this.imageWidth = 176;
        this.imageHeight = 165;
        dragon = container.dragon;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);

        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.setShaderTexture(0, DEPICTION_TEXTURE);

        Vec2 pos = dragon.getTomeDepictionOffset();
        blit(pPoseStack, this.leftPos + 25, this.topPos + 17, pos.x * xPixelsBetweenDepictions, pos.y * yPixelsBetweenDepictions, 126, 54, 256, 512);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
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
        this.font.draw(pPoseStack, this.title, this.titleLabelX, this.titleLabelY, 0x404040);
    }
}
