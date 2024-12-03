package com.github.shannieann.wyrmroost.item.base;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Helper class used to help register playerArmor items
 */
public class ArmorBase extends ArmorItem {
    public ArmorBase(ArmorMaterials material, EquipmentSlot equipType)
    {
        super(material, equipType, WRItems.builder().rarity(material.getRarity()));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        int layer = slot == EquipmentSlot.LEGS? 2 : 1;
        return Wyrmroost.MOD_ID + ":textures/models/armor/" + this.material.getName() + "_layer_" + layer + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flags) {
        super.appendHoverText(stack, level, lines, flags);
        lines.add(new TranslatableComponent("item.wyrmroost.armors.set", new TranslatableComponent("item.wyrmroost.armors." + material.getName()).withStyle(((ArmorMaterials) material).getRarity().color)));

        if (hasDescription())
        {
            lines.add(new TextComponent(""));
            lines.add(new TranslatableComponent(String.format("item.wyrmroost.armors.%s.desc", material.getName().toLowerCase())));
        }
    }

    protected boolean hasDescription() {
        return false;
    }

    public void applyFullSetBonus(LivingEntity entity, boolean hasFullSet) {
    }

    public static boolean hasFullSet(LivingEntity entity) {
        ArmorMaterial prev = null;
        for (ItemStack stack : entity.getArmorSlots())
        {
            if (stack.getItem() instanceof ArmorItem)
            {
                ArmorMaterial now = ((ArmorItem) stack.getItem()).getMaterial();
                if (now == prev || prev == null)
                {
                    prev = now;
                    continue;
                }
            }
            return false;
        }
        return true;
    }
}
