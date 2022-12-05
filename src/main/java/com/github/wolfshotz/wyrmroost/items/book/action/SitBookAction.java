package com.github.wolfshotz.wyrmroost.items.book.action;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.items.book.TarragonTomeItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SitBookAction implements BookAction
{
    @Override
    public void onSelected(TameableDragonEntity dragon, Player player, ItemStack stack)
    {
        dragon.setOrderedToSit(!dragon.isInSittingPose());
        TarragonTomeItem.setAction(BookActions.DEFAULT, player, stack);
    }

    @Override
    public String getTranslateKey(@Nullable TameableDragonEntity dragon)
    {
        if (dragon != null && dragon.isInSittingPose()) return TRANSLATE_PATH + "sit.come";
        return TRANSLATE_PATH + "sit.stay";
    }
}
