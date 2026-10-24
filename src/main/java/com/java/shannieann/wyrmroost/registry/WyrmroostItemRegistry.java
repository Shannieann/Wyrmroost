package com.java.shannieann.wyrmroost.registry;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry.*;

public final class WyrmroostItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wyrmroost.MODID);


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<BlockItem> blockItem(String name, Supplier<? extends Block> block, UnaryOperator<Item.Properties> properties) {
        return ITEMS.registerSimpleBlockItem(name, block, properties);
    }

    // =============
    //  SAL BUSH
    // =============
    public static final DeferredItem<BlockItem> SAL_BUSH_LOG_ITEM = blockItem("sal_bush_log", SAL_BUSH_LOG, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_WOOD_ITEM = blockItem("sal_bush_wood", SAL_BUSH_WOOD, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> STRIPPED_SAL_BUSH_LOG_ITEM = blockItem("stripped_sal_bush_log", STRIPPED_SAL_BUSH_LOG, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> STRIPPED_SAL_BUSH_WOOD_ITEM = blockItem("stripped_sal_bush_wood", STRIPPED_SAL_BUSH_WOOD, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_PLANKS_ITEM = blockItem("sal_bush_planks", SAL_BUSH_PLANKS, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_STAIRS_ITEM = blockItem("sal_bush_stairs", SAL_BUSH_STAIRS, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_SLAB_ITEM = blockItem("sal_bush_slab", SAL_BUSH_SLAB, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_SLABS));
    public static final DeferredItem<BlockItem> SAL_BUSH_FENCE_ITEM = blockItem("sal_bush_fence", SAL_BUSH_FENCE, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_FENCE_GATE_ITEM = blockItem("sal_bush_fence_gate", SAL_BUSH_FENCE_GATE, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_DOOR_ITEM = blockItem("sal_bush_door", SAL_BUSH_DOOR, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE));
    public static final DeferredItem<BlockItem> SAL_BUSH_TRAPDOOR_ITEM = blockItem("sal_bush_trapdoor", SAL_BUSH_TRAPDOOR, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_PRESSURE_PLATE_ITEM = blockItem("sal_bush_pressure_plate", SAL_BUSH_PRESSURE_PLATE, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_BUTTON_ITEM = blockItem("sal_bush_button", SAL_BUSH_BUTTON, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL));
    public static final DeferredItem<BlockItem> SAL_BUSH_SHELF_ITEM = blockItem("sal_bush_shelf", SAL_BUSH_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY).cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_LEAVES_ITEM = blockItem("sal_bush_leaves", SAL_BUSH_LEAVES, properties -> properties.component(DataComponents.COMPOSTABLE, new Compostable(ContextIntProviders.COMPOSTABLE_LOW)));
    public static final DeferredItem<BlockItem> SAL_BUSH_VINE_ITEM = blockItem("sal_bush_vine", SAL_BUSH_VINE, properties -> properties.component(DataComponents.COMPOSTABLE, new Compostable(ContextIntProviders.COMPOSTABLE_LOW_MEDIUM)));
    public static final DeferredItem<BlockItem> SAL_BUSH_SAPLING_ITEM = blockItem("sal_bush_sapling", SAL_BUSH_SAPLING,
            properties -> properties.component(DataComponents.COMPOSTABLE, new Compostable(ContextIntProviders.COMPOSTABLE_LOW)).cookingFuel(ContextIntProviders.COOKING_TIME_DRY_PLANTS));

    private WyrmroostItemRegistry() {}
}