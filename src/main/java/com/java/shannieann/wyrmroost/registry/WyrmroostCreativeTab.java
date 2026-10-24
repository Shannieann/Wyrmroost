package com.java.shannieann.wyrmroost.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import static com.java.shannieann.wyrmroost.registry.WyrmroostItemRegistry.*;

public final class WyrmroostCreativeTab {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(WyrmroostCreativeTab::addCreativeTabItems);
    }

    private static void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(SAL_BUSH_LOG_ITEM);
            event.accept(SAL_BUSH_WOOD_ITEM);
            event.accept(STRIPPED_SAL_BUSH_LOG_ITEM);
            event.accept(STRIPPED_SAL_BUSH_WOOD_ITEM);
            event.accept(SAL_BUSH_PLANKS_ITEM);
            event.accept(SAL_BUSH_STAIRS_ITEM);
            event.accept(SAL_BUSH_SLAB_ITEM);
            event.accept(SAL_BUSH_FENCE_ITEM);
            event.accept(SAL_BUSH_FENCE_GATE_ITEM);
            event.accept(SAL_BUSH_DOOR_ITEM);
            event.accept(SAL_BUSH_TRAPDOOR_ITEM);
            event.accept(SAL_BUSH_PRESSURE_PLATE_ITEM);
            event.accept(SAL_BUSH_BUTTON_ITEM);
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(SAL_BUSH_LOG_ITEM);
            event.accept(SAL_BUSH_LEAVES_ITEM);
            event.accept(SAL_BUSH_SAPLING_ITEM);
            event.accept(SAL_BUSH_VINE_ITEM);
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(SAL_BUSH_SHELF_ITEM);
        }
    }

    private WyrmroostCreativeTab() {}
}