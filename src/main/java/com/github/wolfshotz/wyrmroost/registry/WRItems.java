package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.*;
import com.github.wolfshotz.wyrmroost.items.base.ArmorBase;
import com.github.wolfshotz.wyrmroost.items.base.ArmorMaterials;
import com.github.wolfshotz.wyrmroost.items.base.ToolMaterials;
//import com.github.wolfshotz.wyrmroost.items.book.TarragonTomeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class WRItems
{
    static final CreativeModeTab MAIN_ITEM_GROUP = new CreativeModeTab("wyrmroost")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(PURPLE_GEODE.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items)
        {
            super.fillItemList(items);
            if (WRConfig.DEBUG_MODE.get())
                items.add(new ItemStack(Items.STICK).setHoverName(new TextComponent("Debug Stick")));
        }
    };

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Wyrmroost.MOD_ID);

    //public static final RegistryObject<Item> LDWYRM = register("desert_wyrm", LDWyrmItem::new);
    public static final RegistryObject<Item> DRAGON_EGG = register("dragon_egg", DragonEggItem::new);
    public static final RegistryObject<Item> SOUL_CRYSTAL = register("soul_crystal", SoulCrystalItem::new);
    //public static final RegistryObject<Item> TARRAGON_TOME = register("tarragon_tome", TarragonTomeItem::new);
    //public static final RegistryObject<Item> COIN_DRAGON = register("coin_dragon", CoinDragonItem::new);
    public static final RegistryObject<Item> TRUMPET = register("trumpet", TrumpetItem::new);

    public static final RegistryObject<Item> BLUE_GEODE = register("blue_geode");
    public static final RegistryObject<Item> RED_GEODE = register("red_geode");
    public static final RegistryObject<Item> PURPLE_GEODE = register("purple_geode");
    public static final RegistryObject<Item> PLATINUM_INGOT = register("platinum_ingot");
    public static final RegistryObject<Item> DRAKE_BACKPLATE = register("drake_backplate");

    public static final RegistryObject<Item> BLUE_GEODE_SWORD = register("blue_geode_sword", () -> new SwordItem(ToolMaterials.BLUE_GEODE, 5, -2.4f, builder()));
    public static final RegistryObject<Item> BLUE_GEODE_PICKAXE = register("blue_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.BLUE_GEODE, 3, -2.8f, builder()));
    public static final RegistryObject<Item> BLUE_GEODE_AXE = register("blue_geode_axe", () -> new AxeItem(ToolMaterials.BLUE_GEODE, 7.5f, -3f, builder()));
    public static final RegistryObject<Item> BLUE_GEODE_SHOVEL = register("blue_geode_shovel", () -> new ShovelItem(ToolMaterials.BLUE_GEODE, 3.5f, -3f, builder()));
    public static final RegistryObject<Item> BLUE_GEODE_HOE = register("blue_geode_hoe", () -> new HoeItem(ToolMaterials.BLUE_GEODE, 1, 2.5f, builder()));
    public static final RegistryObject<Item> BLUE_GEODE_HELMET = register("blue_geode_helmet", () -> new ArmorBase(ArmorMaterials.BLUE_GEODE, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> BLUE_GEODE_CHESTPLATE = register("blue_geode_chestplate", () -> new ArmorBase(ArmorMaterials.BLUE_GEODE, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> BLUE_GEODE_LEGGINGS = register("blue_geode_leggings", () -> new ArmorBase(ArmorMaterials.BLUE_GEODE, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> BLUE_GEODE_BOOTS = register("blue_geode_boots", () -> new ArmorBase(ArmorMaterials.BLUE_GEODE, EquipmentSlot.FEET));
    public static final RegistryObject<Item> BLUE_GEODE_ARROW = register("blue_geode_tipped_arrow", () -> new GeodeTippedArrowItem(3));

    public static final RegistryObject<Item> RED_GEODE_SWORD = register("red_geode_sword", () -> new SwordItem(ToolMaterials.RED_GEODE, 6, -2.4f, builder()));
    public static final RegistryObject<Item> RED_GEODE_PICKAXE = register("red_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.RED_GEODE, 4, -2.8f, builder()));
    public static final RegistryObject<Item> RED_GEODE_AXE = register("red_geode_axe", () -> new AxeItem(ToolMaterials.RED_GEODE, 8f, -3f, builder()));
    public static final RegistryObject<Item> RED_GEODE_SHOVEL = register("red_geode_shovel", () -> new ShovelItem(ToolMaterials.RED_GEODE, 4f, -3f, builder()));
    public static final RegistryObject<Item> RED_GEODE_HOE = register("red_geode_hoe", () -> new HoeItem(ToolMaterials.RED_GEODE, 2, 0, builder()));
    public static final RegistryObject<Item> RED_GEODE_HELMET = register("red_geode_helmet", () -> new ArmorBase(ArmorMaterials.RED_GEODE, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> RED_GEODE_CHESTPLATE = register("red_geode_chestplate", () -> new ArmorBase(ArmorMaterials.RED_GEODE, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> RED_GEODE_LEGGINGS = register("red_geode_leggings", () -> new ArmorBase(ArmorMaterials.RED_GEODE, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> RED_GEODE_BOOTS = register("red_geode_boots", () -> new ArmorBase(ArmorMaterials.RED_GEODE, EquipmentSlot.FEET));
    public static final RegistryObject<Item> RED_GEODE_ARROW = register("red_geode_tipped_arrow", () -> new GeodeTippedArrowItem(3.5));

    public static final RegistryObject<Item> PURPLE_GEODE_SWORD = register("purple_geode_sword", () -> new SwordItem(ToolMaterials.PURPLE_GEODE, 8, -2.4f, builder()));
    public static final RegistryObject<Item> PURPLE_GEODE_PICKAXE = register("purple_geode_pickaxe", () -> new PickaxeItem(ToolMaterials.PURPLE_GEODE, 6, -3f, builder()));
    public static final RegistryObject<Item> PURPLE_GEODE_AXE = register("purple_geode_axe", () -> new AxeItem(ToolMaterials.PURPLE_GEODE, 10f, -2.9f, builder()));
    public static final RegistryObject<Item> PURPLE_GEODE_SHOVEL = register("purple_geode_shovel", () -> new ShovelItem(ToolMaterials.PURPLE_GEODE, 6.5f, -2.7f, builder()));
    public static final RegistryObject<Item> PURPLE_GEODE_HOE = register("purple_geode_hoe", () -> new HoeItem(ToolMaterials.PURPLE_GEODE, 0, 4f, builder()));
    public static final RegistryObject<Item> PURPLE_GEODE_HELMET = register("purple_geode_helmet", () -> new ArmorBase(ArmorMaterials.PURPLE_GEODE, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> PURPLE_GEODE_CHESTPLATE = register("purple_geode_chestplate", () -> new ArmorBase(ArmorMaterials.PURPLE_GEODE, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> PURPLE_GEODE_LEGGINGS = register("purple_geode_leggings", () -> new ArmorBase(ArmorMaterials.PURPLE_GEODE, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> PURPLE_GEODE_BOOTS = register("purple_geode_boots", () -> new ArmorBase(ArmorMaterials.PURPLE_GEODE, EquipmentSlot.FEET));
    public static final RegistryObject<Item> PURPLE_GEODE_ARROW = register("purple_geode_tipped_arrow", () -> new GeodeTippedArrowItem(4));

    public static final RegistryObject<Item> PLATINUM_SWORD = register("platinum_sword", () -> new SwordItem(ToolMaterials.PLATINUM, 5, -2.4f, builder()));
    public static final RegistryObject<Item> PLATINUM_PICKAXE = register("platinum_pickaxe", () -> new PickaxeItem(ToolMaterials.PLATINUM, 3, -2.8f, builder()));
    public static final RegistryObject<Item> PLATINUM_AXE = register("platinum_axe", () -> new AxeItem(ToolMaterials.PLATINUM, 7.5f, -2.8f, builder()));
    public static final RegistryObject<Item> PLATINUM_SHOVEL = register("platinum_shovel", () -> new ShovelItem(ToolMaterials.PLATINUM, 3f, -3f, builder()));
    public static final RegistryObject<Item> PLATINUM_HOE = register("platinum_hoe", () -> new HoeItem(ToolMaterials.PLATINUM, 0, -1, builder()));
    public static final RegistryObject<Item> PLATINUM_HELMET = register("platinum_helmet", () -> new ArmorBase(ArmorMaterials.PLATINUM, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> PLATINUM_CHESTPLATE = register("platinum_chestplate", () -> new ArmorBase(ArmorMaterials.PLATINUM, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> PLATINUM_LEGGINGS = register("platinum_leggings", () -> new ArmorBase(ArmorMaterials.PLATINUM, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> PLATINUM_BOOTS = register("platinum_boots", () -> new ArmorBase(ArmorMaterials.PLATINUM, EquipmentSlot.FEET));
    public static final RegistryObject<Item> DRAKE_HELMET = register("drake_helmet", () -> new DrakeArmorItem(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DRAKE_CHESTPLATE = register("drake_chestplate", () -> new DrakeArmorItem(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DRAKE_LEGGINGS = register("drake_leggings", () -> new DrakeArmorItem(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DRAKE_BOOTS = register("drake_boots", () -> new DrakeArmorItem(EquipmentSlot.FEET));

    public static final RegistryObject<Item> RAW_LOWTIER_MEAT = register("raw_lowtier_meat", food(1, 0.1f).meat());
    public static final RegistryObject<Item> RAW_COMMON_MEAT = register("raw_common_meat", food(3, 0.3f).meat());
    public static final RegistryObject<Item> RAW_APEX_MEAT = register("raw_apex_meat", food(5, 0.45f).meat());
    public static final RegistryObject<Item> RAW_BEHEMOTH_MEAT = register("raw_behemoth_meat", food(7, 0.7f).meat());
    public static final RegistryObject<Item> COOKED_LOWTIER_MEAT = register("cooked_lowtier_meat", food(3, 0.7f).meat());
    public static final RegistryObject<Item> COOKED_COMMON_MEAT = register("cooked_common_meat", food(8, 0.8f).meat());
    public static final RegistryObject<Item> COOKED_APEX_MEAT = register("cooked_apex_meat", food(16, 1f).meat());
    public static final RegistryObject<Item> COOKED_BEHEMOTH_MEAT = register("cooked_behemoth_meat", food(20, 2f).meat());
    public static final RegistryObject<Item> COOKED_MINUTUS = register("cooked_desertwyrm", food(10, 1f).meat());
    public static final RegistryObject<Item> JEWELLED_APPLE = register("jewelled_apple", food(8, 0.9f).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.GLOWING, 800), 1f).effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 800), 1f).effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 6000, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 800), 1f));


    public static final RegistryObject<Item> LEATHER_DRAGON_ARMOR = register("leather_dragon_armor", () -> new DragonArmorItem.Dyeable(2, net.minecraft.world.item.ArmorMaterials.LEATHER.getEnchantmentValue()));
    public static final RegistryObject<Item> IRON_DRAGON_ARMOR = register("iron_dragon_armor", () -> new DragonArmorItem(4, net.minecraft.world.item.ArmorMaterials.IRON.getEnchantmentValue()));
    public static final RegistryObject<Item> PALTINUM_DRAGON_ARMOR = register("platinum_dragon_armor", () -> new DragonArmorItem(5, ArmorMaterials.PLATINUM.getEnchantmentValue()));
    public static final RegistryObject<Item> GOLD_DRAGON_ARMOR = register("gold_dragon_armor", () -> new DragonArmorItem(6, net.minecraft.world.item.ArmorMaterials.GOLD.getEnchantmentValue()));
    public static final RegistryObject<Item> DIAMOND_DRAGON_ARMOR = register("diamond_dragon_armor", () -> new DragonArmorItem(8, net.minecraft.world.item.ArmorMaterials.DIAMOND.getEnchantmentValue()));
    public static final RegistryObject<Item> NETHERITE_DRAGON_ARMOR = register("netherite_dragon_armor", () -> new DragonArmorItem(10, net.minecraft.world.item.ArmorMaterials.DIAMOND.getEnchantmentValue()));

    static RegistryObject<Item> register(String name, Supplier<Item> item)
    {
        return REGISTRY.register(name, item);
    }

    static RegistryObject<Item> register(String name, FoodProperties.Builder food)
    {
        return register(name, () -> new Item(builder().food(food.build())));
    }

    static RegistryObject<Item> register(String name)
    {
        return REGISTRY.register(name, () -> new Item(builder()));
    }

    public static Properties builder()
    {
        return new Properties().tab(MAIN_ITEM_GROUP);
    }

    static FoodProperties.Builder food(int nutrition, float saturation)
    {
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
    }

    /*public static class Tags
    {
        public static final Tag<Item> GEMS_GEODE = forge("gems/geodes");
        public static final Tag<Item> DRAGON_MEATS = tag("dragon_meats");
        public static final Tag<Item> INGOTS_PLATINUM = forge("ingots/platinum");

        private static Tag<Item> tag(String path)
        {
            return ItemTags.bind(Wyrmroost.MOD_ID + ":" + path);
        }

        private static Tag<Item> forge(String path)
        {
            return ItemTags.bind("forge:" + path);
        }
    }*/
}