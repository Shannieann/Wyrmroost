package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.item.base.ArmorBase;
import com.github.shannieann.wyrmroost.item.base.ArmorMaterials;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class DrakeArmorItem extends ArmorBase
{
    private static final UUID KB_RESISTANCE_ID = UUID.fromString("eaa010aa-299d-4c76-9f02-a1283c9e890b");
    private static final AttributeModifier KB_RESISTANCE = new AttributeModifier(KB_RESISTANCE_ID, "Drake armor knockback resistance", 10, AttributeModifier.Operation.ADDITION);

    public DrakeArmorItem(EquipmentSlot equipType)
    {
        super(ArmorMaterials.DRAKE, equipType);
    }

    @Override
    public void applyFullSetBonus(LivingEntity entity, boolean hasFullSet)
    {
        AttributeInstance attribute = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (attribute.hasModifier(KB_RESISTANCE)) attribute.removeModifier(KB_RESISTANCE);
        if (hasFullSet) attribute.addTransientModifier(KB_RESISTANCE);
    }

    @Override
    protected boolean hasDescription()
    {
        return true;
    }
}
