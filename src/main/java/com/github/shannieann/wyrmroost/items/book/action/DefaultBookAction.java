package com.github.shannieann.wyrmroost.items.book.action;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.render.RenderHelper;
import com.github.shannieann.wyrmroost.client.screen.TarragonTomeScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.shannieann.wyrmroost.items.book.TarragonTomeItem;
import com.github.shannieann.wyrmroost.util.Mafs;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;


public class DefaultBookAction implements BookAction
{
    @Override
    public InteractionResult rightClick(@Nullable TameableDragonEntity dragon, Player player, ItemStack stack)
    {
        boolean client = player.getLevel().isClientSide();
        if (dragon != null && !player.getLevel().isClientSide())
        {
            BookContainer.open((ServerPlayer) player, dragon, stack);
        }
        else if ((dragon = clip(player)) != null)
        {
            TarragonTomeItem.bind(dragon, stack);
            if (client)
            {
                ModUtils.playLocalSound(player.getLevel(), player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.75f, 2f);
                ModUtils.playLocalSound(player.getLevel(), player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.75f, 1f);
            }
        }
        else if (client) TarragonTomeScreen.open();

        return InteractionResult.CONSUME;
    }

    @Override
    public void render(@Nullable TameableDragonEntity dragon, PoseStack ms, float partialTicks)
    {
        if (dragon == null && (dragon = clip(ClientEvents.getPlayer())) != null)
            RenderHelper.renderEntityOutline(dragon,
                    255,
                    255,
                    255,
                    (int) (Mth.cos((dragon.tickCount + partialTicks) * 0.2f) * 35 + 45));
    }

    @Nullable
    private TameableDragonEntity clip(Player player)
    {
        EntityHitResult ertr = Mafs.clipEntities(player, 40, 0.75, e -> e instanceof TameableDragonEntity && ((TameableDragonEntity) e).isOwnedBy(player));
        return ertr != null? (TameableDragonEntity) ertr.getEntity() : null;
    }

    @Override
    public String getTranslateKey(@Nullable TameableDragonEntity dragon)
    {
        return TRANSLATE_PATH + "default";
    }
}
