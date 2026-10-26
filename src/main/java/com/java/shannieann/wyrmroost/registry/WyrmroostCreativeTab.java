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
            event.accept(KALIBURN_LOG_ITEM);
            event.accept(KALIBURN_WOOD_ITEM);
            event.accept(KALIBURN_PLANKS_ITEM);
            event.accept(KALIBURN_STAIRS_ITEM);
            event.accept(KALIBURN_SLAB_ITEM);
            event.accept(KALIBURN_FENCE_ITEM);
            event.accept(KALIBURN_FENCE_GATE_ITEM);
            event.accept(KALIBURN_DOOR_ITEM);
            event.accept(KALIBURN_TRAPDOOR_ITEM);
            event.accept(KALIBURN_PRESSURE_PLATE_ITEM);
            event.accept(KALIBURN_BUTTON_ITEM);
            event.accept(DRAGONSEYE_LOG_ITEM);
            event.accept(DRAGONSEYE_WOOD_ITEM);
            event.accept(DRAGONSEYE_PLANKS_ITEM);
            event.accept(DRAGONSEYE_STAIRS_ITEM);
            event.accept(DRAGONSEYE_SLAB_ITEM);
            event.accept(DRAGONSEYE_FENCE_ITEM);
            event.accept(DRAGONSEYE_FENCE_GATE_ITEM);
            event.accept(DRAGONSEYE_DOOR_ITEM);
            event.accept(DRAGONSEYE_TRAPDOOR_ITEM);
            event.accept(DRAGONSEYE_PRESSURE_PLATE_ITEM);
            event.accept(DRAGONSEYE_BUTTON_ITEM);
            event.accept(DRAGONWOOD_LOG_ITEM);
            event.accept(DRAGONWOOD_BUNDLE_ITEM);
            event.accept(DRAGONWOOD_PLANKS_ITEM);
            event.accept(DRAGONWOOD_STAIRS_ITEM);
            event.accept(DRAGONWOOD_SLAB_ITEM);
            event.accept(DRAGONWOOD_FENCE_ITEM);
            event.accept(DRAGONWOOD_FENCE_GATE_ITEM);
            event.accept(DRAGONWOOD_DOOR_ITEM);
            event.accept(DRAGONWOOD_TRAPDOOR_ITEM);
            event.accept(DRAGONWOOD_PRESSURE_PLATE_ITEM);
            event.accept(DRAGONWOOD_BUTTON_ITEM);
            event.accept(SHIMARIN_LOG_ITEM);
            event.accept(SHIMARIN_WOOD_ITEM);
            event.accept(SHIMARIN_PLANKS_ITEM);
            event.accept(SHIMARIN_STAIRS_ITEM);
            event.accept(SHIMARIN_SLAB_ITEM);
            event.accept(SHIMARIN_FENCE_ITEM);
            event.accept(SHIMARIN_FENCE_GATE_ITEM);
            event.accept(SHIMARIN_DOOR_ITEM);
            event.accept(SHIMARIN_TRAPDOOR_ITEM);
            event.accept(SHIMARIN_PRESSURE_PLATE_ITEM);
            event.accept(SHIMARIN_BUTTON_ITEM);
            event.accept(BRUME_PINE_LOG_ITEM);
            event.accept(BRUME_PINE_WOOD_ITEM);
            event.accept(BRUME_PINE_PLANKS_ITEM);
            event.accept(BRUME_PINE_STAIRS_ITEM);
            event.accept(BRUME_PINE_SLAB_ITEM);
            event.accept(BRUME_PINE_FENCE_ITEM);
            event.accept(BRUME_PINE_FENCE_GATE_ITEM);
            event.accept(BRUME_PINE_DOOR_ITEM);
            event.accept(BRUME_PINE_TRAPDOOR_ITEM);
            event.accept(BRUME_PINE_PRESSURE_PLATE_ITEM);
            event.accept(BRUME_PINE_BUTTON_ITEM);
            event.accept(CANAR_LOG_ITEM);
            event.accept(CANAR_WOOD_ITEM);
            event.accept(CANAR_PLANKS_ITEM);
            event.accept(CANAR_STAIRS_ITEM);
            event.accept(CANAR_SLAB_ITEM);
            event.accept(CANAR_FENCE_ITEM);
            event.accept(CANAR_FENCE_GATE_ITEM);
            event.accept(CANAR_DOOR_ITEM);
            event.accept(CANAR_TRAPDOOR_ITEM);
            event.accept(CANAR_PRESSURE_PLATE_ITEM);
            event.accept(CANAR_BUTTON_ITEM);
            event.accept(SUREN_LOG_ITEM);
            event.accept(SUREN_WOOD_ITEM);
            event.accept(SUREN_PLANKS_ITEM);
            event.accept(SUREN_STAIRS_ITEM);
            event.accept(SUREN_SLAB_ITEM);
            event.accept(SUREN_FENCE_ITEM);
            event.accept(SUREN_FENCE_GATE_ITEM);
            event.accept(SUREN_DOOR_ITEM);
            event.accept(SUREN_TRAPDOOR_ITEM);
            event.accept(SUREN_PRESSURE_PLATE_ITEM);
            event.accept(SUREN_BUTTON_ITEM);
            event.accept(HYLAN_SEDGE_LOG_ITEM);
            event.accept(HYLAN_SEDGE_WOOD_ITEM);
            event.accept(HYLAN_SEDGE_PLANKS_ITEM);
            event.accept(HYLAN_SEDGE_STAIRS_ITEM);
            event.accept(HYLAN_SEDGE_SLAB_ITEM);
            event.accept(HYLAN_SEDGE_FENCE_ITEM);
            event.accept(HYLAN_SEDGE_FENCE_GATE_ITEM);
            event.accept(HYLAN_SEDGE_DOOR_ITEM);
            event.accept(HYLAN_SEDGE_TRAPDOOR_ITEM);
            event.accept(HYLAN_SEDGE_PRESSURE_PLATE_ITEM);
            event.accept(HYLAN_SEDGE_BUTTON_ITEM);
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(SAL_BUSH_LOG_ITEM);
            event.accept(SAL_BUSH_LEAVES_ITEM);
            event.accept(SAL_BUSH_SAPLING_ITEM);
            event.accept(SAL_BUSH_VINE_ITEM);
            event.accept(KALIBURN_LOG_ITEM);
            event.accept(KALIBURN_LEAVES_ITEM);
            event.accept(KALIBURN_SAPLING_ITEM);
            event.accept(DRAGONSEYE_LOG_ITEM);
            event.accept(DRAGONSEYE_LEAVES_ITEM);
            event.accept(DRAGONSEYE_SAPLING_ITEM);
            event.accept(DRAGONWOOD_LOG_ITEM);
            event.accept(DRAGONWOOD_LEAVES_ITEM);
            event.accept(DRAGONWOOD_SAPLING_ITEM);
            event.accept(SHIMARIN_LOG_ITEM);
            event.accept(SHIMARIN_LEAVES_ITEM);
            event.accept(SHIMARIN_SAPLING_ITEM);
            event.accept(BRUME_PINE_LOG_ITEM);
            event.accept(BRUME_PINE_LEAVES_ITEM);
            event.accept(BRUME_PINE_SAPLING_ITEM);
            event.accept(CANAR_LOG_ITEM);
            event.accept(CANAR_LEAVES_ITEM);
            event.accept(CANAR_SAPLING_ITEM);
            event.accept(SUREN_LOG_ITEM);
            event.accept(SUREN_LEAVES_ITEM);
            event.accept(SUREN_SAPLING_ITEM);
            event.accept(HYLAN_SEDGE_LOG_ITEM);
            event.accept(HYLAN_SEDGE_LEAVES_ITEM);
            event.accept(HYLAN_SEDGE_SAPLING_ITEM);
            //TODO: Stripped logs and wood for every tree except Sal Bush
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(SAL_BUSH_SHELF_ITEM);
            event.accept(KALIBURN_SHELF_ITEM);
            event.accept(DRAGONSEYE_SHELF_ITEM);
            event.accept(DRAGONWOOD_SHELF_ITEM);
            event.accept(SHIMARIN_SHELF_ITEM);
            event.accept(BRUME_PINE_SHELF_ITEM);
            event.accept(CANAR_SHELF_ITEM);
            event.accept(SUREN_SHELF_ITEM);
            event.accept(HYLAN_SEDGE_SHELF_ITEM);
        }
    }

    private WyrmroostCreativeTab() {}
}