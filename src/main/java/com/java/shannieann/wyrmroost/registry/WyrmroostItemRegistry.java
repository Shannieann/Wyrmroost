package com.java.shannieann.wyrmroost.registry;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
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

    private static UnaryOperator<Item.Properties> compost(ResourceKey<ContextIntProvider> layers) {
        return properties -> properties.component(DataComponents.COMPOSTABLE, new Compostable(layers));
    }

    // =============
    //  SAL BUSH
    // =============
    public static final DeferredItem<BlockItem> SAL_BUSH_LOG_ITEM = blockItem("sal_bush_log", SAL_BUSH_LOG, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_WOOD_ITEM = blockItem("sal_bush_wood", SAL_BUSH_WOOD, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> STRIPPED_SAL_BUSH_LOG_ITEM = blockItem("stripped_sal_bush_log", STRIPPED_SAL_BUSH_LOG, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_PLANKS_ITEM = blockItem("sal_bush_planks", SAL_BUSH_PLANKS, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_STAIRS_ITEM = blockItem("sal_bush_stairs", SAL_BUSH_STAIRS, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_SLAB_ITEM = blockItem("sal_bush_slab", SAL_BUSH_SLAB, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_SLABS));
    public static final DeferredItem<BlockItem> SAL_BUSH_FENCE_ITEM = blockItem("sal_bush_fence", SAL_BUSH_FENCE, properties -> properties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_SHELF_ITEM = blockItem("sal_bush_shelf", SAL_BUSH_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY).cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS));
    public static final DeferredItem<BlockItem> SAL_BUSH_LEAVES_ITEM = blockItem("sal_bush_leaves", SAL_BUSH_LEAVES, compost(ContextIntProviders.COMPOSTABLE_LOW));
    public static final DeferredItem<BlockItem> SAL_BUSH_SAPLING_ITEM = blockItem("sal_bush_sapling", SAL_BUSH_SAPLING,
            properties -> compost(ContextIntProviders.COMPOSTABLE_LOW).apply(properties).cookingFuel(ContextIntProviders.COOKING_TIME_DRY_PLANTS));

    private WyrmroostItemRegistry() {}
}
