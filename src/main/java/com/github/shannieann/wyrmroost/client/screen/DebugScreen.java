package com.github.shannieann.wyrmroost.client.screen;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class DebugScreen extends Screen
{
    public final WRDragonEntity dragon;
    private boolean paused = true;

    public DebugScreen(WRDragonEntity dragon)
    {
        super(new TextComponent("debug_screen"));

        this.dragon = dragon;
    }

    @Override
    protected void init()
    {
        addRenderableWidget(new Button(0, 0, 50, 20, new TextComponent("Pause Game"), b -> paused = !paused));

        /*Animation[] animations = dragon.getAnimations();
        if (animations != null && animations.length > 0)
            for (int i = 0; i < animations.length; i++)
            {
                Animation animation = animations[i];
                addButton(new Button((i * 50) + (width / 2) - (animations.length * 25), 200, 50, 12, new TextComponent("Anim: " + i), b ->
                {
                    dragon.setAnimation(animation);
                    onClose();
                }));
            }*/
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        String gender = dragon.isMale()? "male" : "female";

        drawCenteredString(ms, font, dragon.getDisplayName().getString(), (width / 2), 15, 0xffffff);
        drawCenteredString(ms, font, "isSleeping: " + dragon.isSleeping(), (width / 2) + 50, 50, 0xffffff);
        drawCenteredString(ms, font, "isTamed: " + dragon.isTame(), (width / 2) - 50, 50, 0xffffff);
        drawCenteredString(ms, font, "isSitting: " + dragon.isInSittingPose(), (width / 2) - 50, 75, 0xffffff);
        drawCenteredString(ms, font, "isFlying: " + dragon.isFlying(), (width / 2) + 50, 75, 0xffffff);
        drawCenteredString(ms, font, "variant: " + dragon.getVariant(), (width / 2) - 50, 100, 0xffffff);
        drawCenteredString(ms, font, "gender: " + gender, (width / 2) + 50, 100, 0xffffff);
        drawCenteredString(ms, font, "health: " + dragon.getHealth() + " / " + dragon.getMaxHealth(), (width / 2) - 50, 125, 0xffffff);
        drawCenteredString(ms, font, "noAI: " + dragon.isNoAi(), (width / 2) + 50, 125, 0xffffff);
        drawCenteredString(ms, font, "position: " + dragon.position(), (width / 2), 150, 0xffffff);
        drawCenteredString(ms, font, "motion: " + dragon.getDeltaMovement(), (width / 2), 175, 0xffffff);
    }

    @Override
    public boolean isPauseScreen()
    {
        return paused;
    }

    public static void open(WRDragonEntity dragon)
    {
        Minecraft.getInstance().setScreen(new DebugScreen(dragon));
    }
}
