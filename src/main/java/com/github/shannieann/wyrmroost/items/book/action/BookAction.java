package com.github.shannieann.wyrmroost.items.book.action;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nullable;

public interface BookAction
{
    String TRANSLATE_PATH = "item.wyrmroost.tarragon_tome.action.";

    default InteractionResult clickBlock(@Nullable WRDragonEntity dragon, UseOnContext context)
    {
        return InteractionResult.PASS;
    }

    default InteractionResult rightClick(@Nullable WRDragonEntity dragon, Player player, ItemStack stack)
    {
        return InteractionResult.PASS;
    }

    default void onSelected(@Nullable WRDragonEntity dragon, Player player, ItemStack stack)
    {
    }

    default void render(@Nullable WRDragonEntity dragon, PoseStack ms, float partialTicks)
    {
    }

    String getTranslateKey(@Nullable WRDragonEntity dragon);

    default TranslatableComponent getTranslation(@Nullable WRDragonEntity dragon)
    {
        return new TranslatableComponent(getTranslateKey(dragon));
    }
}