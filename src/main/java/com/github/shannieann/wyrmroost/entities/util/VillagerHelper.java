package com.github.shannieann.wyrmroost.entities.util;

/*import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.WandererTradesEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class VillagerHelper
{
    public static void addWandererTrades(WandererTradesEvent evt)
    {
        List<VillagerTrades.ItemListing> list = evt.getGenericTrades();

        list.add(cdForItems(WRItems.BLUE_GEODE.get(), 12, 1, 3));
        list.add(cdForItems(WRItems.RED_GEODE.get(), 6, 1, 4));
        list.add(cdForItems(WRItems.PURPLE_GEODE.get(), 3, 1, 5));
        list.add(cdForItems(WRItems.TRUMPET.get(), 1, 4, 2));
        list.add(cdForItems(WRItems.JEWELLED_APPLE.get(), 2, 3, 1));
        list.add(new ItemsForItemsTrade(new ItemStack(Items.EMERALD, 6), new ItemStack(WRItems.BLUE_GEODE.get(), 4), 4, 1, 10));
    }

    private static VillagerTrades.ItemListing cdForItems(ItemStack selling, int maxUses, int xp)
    {
        return new ItemsForItemsTrade(new ItemStack(WRItems.COIN_DRAGON.get()), selling, maxUses, xp, 0);
    }

    private static VillagerTrades.ItemListing cdForItems(Item item, int count, int maxUses, int xp)
    {
        return cdForItems(new ItemStack(item, count), maxUses, xp);
    }

    private static class ItemsForItemsTrade implements VillagerTrades.ItemListing
    {
        private final ItemStack buying1, buying2, selling;
        private final int maxUses, xp;
        private final float priceMultiplier;

        public ItemsForItemsTrade(ItemStack buying1, ItemStack buying2, ItemStack selling, int maxUses, int xp, float priceMultiplier)
        {
            this.buying1 = buying1;
            this.buying2 = buying2;
            this.selling = selling;
            this.maxUses = maxUses;
            this.xp = xp;
            this.priceMultiplier = priceMultiplier;
        }

        public ItemsForItemsTrade(ItemStack buying1, ItemStack selling, int maxUses, int xp, float priceMultiplier)
        {
            this(buying1, ItemStack.EMPTY, selling, maxUses, xp, priceMultiplier);
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity p_221182_1_, Random p_221182_2_)
        {
            return new MerchantOffer(buying1, buying2, selling, maxUses, xp, priceMultiplier);
        }
    }
}*/
