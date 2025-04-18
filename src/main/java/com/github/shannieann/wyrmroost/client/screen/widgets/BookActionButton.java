/*package com.github.shannieann.wyrmroost.client.screen.widgets;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.items.book.TarragonTomeItem;
import com.github.shannieann.wyrmroost.items.book.action.BookAction;
import com.github.shannieann.wyrmroost.network.BookActionPacket;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BookActionButton extends AbstractButton
{
    public final DragonControlScreen screen;
    public final BookAction action;
    public final LerpedFloat focusTime = LerpedFloat.unit();
    public boolean wasHovered = false;

    public BookActionButton(DragonControlScreen screen, BookAction action, int xIn, int yIn, Component msg)
    {
        super(xIn, yIn, 100, 20, msg);
        this.screen = screen;
        this.action = action;
    }

    @Override
    public void onPress()
    {
        Player player = Minecraft.getInstance().player;
        ItemStack stack = ModUtils.getHeldStack(player, WRItems.TARRAGON_TOME.get());
        if (stack != null)
        {
            TarragonTomeItem.setAction(action, player, stack);
            Wyrmroost.NETWORK.sendToServer(new BookActionPacket(action));
        }
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
    {
        if (visible = !screen.showAccessories())
            super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        if (wasHovered != isHovered) onFocusedChanged(wasHovered = isHovered);

        float time = 0.5f * partialTicks; // adjust speed for framerate
        focusTime.add(isHovered? time : -time);
        float amount = focusTime.get(partialTicks) * 6;
        drawCenteredString(ms,
                Minecraft.getInstance().font,
                getMessage().getString(),
                x + width / 2,
                (y + (height - 8) / 2) - (int) amount,
                (int) Mth.lerp(amount, 0xffffff, 0xfffd8a));
    }

    @Override
    protected void onFocusedChanged(boolean focusing)
    {
        if (focusing)
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS, -1f));
    }

    @Override
    public void playDownSound(SoundManager sounds)
    {
        sounds.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f, 1f));
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }
}*/