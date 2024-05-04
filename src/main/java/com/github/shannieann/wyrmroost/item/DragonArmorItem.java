package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.UUID;

public class DragonArmorItem extends Item
{
    public static final UUID ARMOR_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private final int dmgReduction, enchantability;

    public DragonArmorItem(int dmgReduction, int enchantability)
    {
        super(WRItems.builder().stacksTo(1));
        this.dmgReduction = dmgReduction;
        this.enchantability = enchantability;
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantability;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.ALL_DAMAGE_PROTECTION;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return true;
    }

    public double getDmgReduction()
    {
        return dmgReduction;
    }

    public static double getDmgReduction(ItemStack stack)
    {
        Item item = stack.getItem();
        if (!(item instanceof DragonArmorItem))
            throw new AssertionError("uhh this isn't an armor: " + item.getRegistryName());

        return ((DragonArmorItem) item).getDmgReduction() + EnchantmentHelper.getEnchantments(stack).getOrDefault(Enchantments.ALL_DAMAGE_PROTECTION, 0);
    }

    public static class Dyeable extends DragonArmorItem implements DyeableLeatherItem
    {
        public Dyeable(int dmgReduction, int enchantability)
        {
            super(dmgReduction, enchantability);
        }
    }
}
