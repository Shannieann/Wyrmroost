package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry.*;

public final class WyrmroostItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wyrmroost.MODID);


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    // =============
    //  SAL BUSH
    // =============
    //TODO: Extract to data_map when porting
    public static final DeferredItem<BlockItem> SAL_BUSH_LOG_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_log", SAL_BUSH_LOG);
    public static final DeferredItem<BlockItem> SAL_BUSH_WOOD_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_wood", SAL_BUSH_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_SAL_BUSH_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_sal_bush_log", STRIPPED_SAL_BUSH_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_SAL_BUSH_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_sal_bush_wood", STRIPPED_SAL_BUSH_WOOD);
    public static final DeferredItem<BlockItem> SAL_BUSH_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_planks", SAL_BUSH_PLANKS);
    public static final DeferredItem<BlockItem> SAL_BUSH_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_stairs", SAL_BUSH_STAIRS);
    public static final DeferredItem<BlockItem> SAL_BUSH_SLAB_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_slab", SAL_BUSH_SLAB);
    public static final DeferredItem<BlockItem> SAL_BUSH_FENCE_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_fence", SAL_BUSH_FENCE);
    public static final DeferredItem<BlockItem> SAL_BUSH_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_fence_gate", SAL_BUSH_FENCE_GATE);
    public static final DeferredItem<BlockItem> SAL_BUSH_DOOR_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_door", SAL_BUSH_DOOR);
    public static final DeferredItem<BlockItem> SAL_BUSH_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_trapdoor", SAL_BUSH_TRAPDOOR);
    public static final DeferredItem<BlockItem> SAL_BUSH_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_pressure_plate", SAL_BUSH_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> SAL_BUSH_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_button", SAL_BUSH_BUTTON);
    public static final DeferredItem<BlockItem> SAL_BUSH_SHELF_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_shelf", SAL_BUSH_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> SAL_BUSH_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_leaves", SAL_BUSH_LEAVES);
    public static final DeferredItem<BlockItem> SAL_BUSH_VINE_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_vine", SAL_BUSH_VINE);
    public static final DeferredItem<BlockItem> SAL_BUSH_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("sal_bush_sapling", SAL_BUSH_SAPLING);

    private WyrmroostItemRegistry() {}
}
