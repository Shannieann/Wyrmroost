package com.github.shannieann.wyrmroost.item.base;

import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;


public enum ArmorMaterials implements ArmorMaterial {
    BLUE_GEODE(new int[] {3, 5, 7, 3}, 1f, 31, 25, SoundEvents.ARMOR_EQUIP_DIAMOND, WRItems.BLUE_GEODE),
    RED_GEODE(new int[] {3, 6, 8, 3}, 2.5f, 32, 25, SoundEvents.ARMOR_EQUIP_DIAMOND, WRItems.RED_GEODE),
    PURPLE_GEODE(new int[] {4, 7, 10, 4}, 4f, 45, 28, 0, SoundEvents.ARMOR_EQUIP_DIAMOND, WRItems.RED_GEODE, Rarity.RARE),
    PLATINUM(new int[] {2, 5, 7, 2}, 0.1f, 20, 10, SoundEvents.ARMOR_EQUIP_IRON, WRItems.PLATINUM_INGOT),
    DRAKE(new int[] {3, 6, 8, 3}, 1.2f, 32, 9, 0, SoundEvents.ARMOR_EQUIP_LEATHER, WRItems.DRAKE_BACKPLATE, Rarity.UNCOMMON);

    private static final int[] DURABILITY_ARRAY = new int[] {13, 15, 16, 11};
    private final int durability, enchantability, knockbackResistance;
    private final int[] defense; // boots[0], legs[1], chest[2], helm[3]
    private final float toughness;
    private final SoundEvent sound;
    private final Lazy<Ingredient> repairMaterial;
    private final Rarity rarity;

    ArmorMaterials(int[] dmgReduction, float toughness, int durability, int enchantability, SoundEvent sound, Supplier<Item> repairMaterial) {
        this(dmgReduction, toughness, durability, enchantability, 0, sound, repairMaterial, Rarity.COMMON);
    }

    ArmorMaterials(int[] dmgReduction, float toughness, int durability, int enchantability, int knockbackResistance, SoundEvent sound, Supplier<Item> repairMaterial, Rarity rarity) {
        this.durability = durability;
        this.defense = dmgReduction;
        this.enchantability = enchantability;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.sound = sound;
        this.repairMaterial = Lazy.of(() -> Ingredient.of(repairMaterial.get()));
        this.rarity = rarity;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return DURABILITY_ARRAY[slot.getIndex()] * durability;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return defense[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }

    @Override
    public String getName() {
        return toString().toLowerCase();
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
