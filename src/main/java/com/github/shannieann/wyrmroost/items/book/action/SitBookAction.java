package com.github.shannieann.wyrmroost.items.book.action;

import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.items.book.TarragonTomeItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SitBookAction implements BookAction
{
    @Override
    public void onSelected(WRDragonEntity dragon, Player player, ItemStack stack)
    {
        dragon.setOrderedToSit(!dragon.isInSittingPose());
        TarragonTomeItem.setAction(BookActions.DEFAULT, player, stack);
    }

    @Override
    public String getTranslateKey(@Nullable WRDragonEntity dragon)
    {
        if (dragon != null && dragon.isInSittingPose()) return TRANSLATE_PATH + "sit.come";
        return TRANSLATE_PATH + "sit.stay";
    }
}
