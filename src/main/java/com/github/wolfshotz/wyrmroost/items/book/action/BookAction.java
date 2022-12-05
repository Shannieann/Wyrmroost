package com.github.wolfshotz.wyrmroost.items.book.action;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
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

    default InteractionResult clickBlock(@Nullable TameableDragonEntity dragon, UseOnContext context)
    {
        return InteractionResult.PASS;
    }

    default InteractionResult rightClick(@Nullable TameableDragonEntity dragon, Player player, ItemStack stack)
    {
        return InteractionResult.PASS;
    }

    default void onSelected(@Nullable TameableDragonEntity dragon, Player player, ItemStack stack)
    {
    }

    default void render(@Nullable TameableDragonEntity dragon, PoseStack ms, float partialTicks)
    {
    }

    String getTranslateKey(@Nullable TameableDragonEntity dragon);

    default TranslatableComponent getTranslation(@Nullable TameableDragonEntity dragon)
    {
        return new TranslatableComponent(getTranslateKey(dragon));
    }
}