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

    // =============
    //  KALIBURN
    // =============
    public static final DeferredItem<BlockItem> KALIBURN_LOG_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_log", KALIBURN_LOG);
    public static final DeferredItem<BlockItem> KALIBURN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_wood", KALIBURN_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_KALIBURN_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_kaliburn_log", STRIPPED_KALIBURN_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_KALIBURN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_kaliburn_wood", STRIPPED_KALIBURN_WOOD);
    public static final DeferredItem<BlockItem> KALIBURN_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_planks", KALIBURN_PLANKS);
    public static final DeferredItem<BlockItem> KALIBURN_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_stairs", KALIBURN_STAIRS);
    public static final DeferredItem<BlockItem> KALIBURN_SLAB_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_slab", KALIBURN_SLAB);
    public static final DeferredItem<BlockItem> KALIBURN_FENCE_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_fence", KALIBURN_FENCE);
    public static final DeferredItem<BlockItem> KALIBURN_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_fence_gate", KALIBURN_FENCE_GATE);
    public static final DeferredItem<BlockItem> KALIBURN_DOOR_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_door", KALIBURN_DOOR);
    public static final DeferredItem<BlockItem> KALIBURN_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_trapdoor", KALIBURN_TRAPDOOR);
    public static final DeferredItem<BlockItem> KALIBURN_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_pressure_plate", KALIBURN_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> KALIBURN_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_button", KALIBURN_BUTTON);
    public static final DeferredItem<BlockItem> KALIBURN_SHELF_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_shelf", KALIBURN_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> KALIBURN_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_leaves", KALIBURN_LEAVES);
    public static final DeferredItem<BlockItem> KALIBURN_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("kaliburn_sapling", KALIBURN_SAPLING);

    // =============
    //  DRAGONSEYE
    // =============
    public static final DeferredItem<BlockItem> DRAGONSEYE_LOG_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_log", DRAGONSEYE_LOG);
    public static final DeferredItem<BlockItem> DRAGONSEYE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_wood", DRAGONSEYE_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_DRAGONSEYE_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_dragonseye_log", STRIPPED_DRAGONSEYE_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_DRAGONSEYE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_dragonseye_wood", STRIPPED_DRAGONSEYE_WOOD);
    public static final DeferredItem<BlockItem> DRAGONSEYE_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_planks", DRAGONSEYE_PLANKS);
    public static final DeferredItem<BlockItem> DRAGONSEYE_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_stairs", DRAGONSEYE_STAIRS);
    public static final DeferredItem<BlockItem> DRAGONSEYE_SLAB_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_slab", DRAGONSEYE_SLAB);
    public static final DeferredItem<BlockItem> DRAGONSEYE_FENCE_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_fence", DRAGONSEYE_FENCE);
    public static final DeferredItem<BlockItem> DRAGONSEYE_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_fence_gate", DRAGONSEYE_FENCE_GATE);
    public static final DeferredItem<BlockItem> DRAGONSEYE_DOOR_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_door", DRAGONSEYE_DOOR);
    public static final DeferredItem<BlockItem> DRAGONSEYE_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_trapdoor", DRAGONSEYE_TRAPDOOR);
    public static final DeferredItem<BlockItem> DRAGONSEYE_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_pressure_plate", DRAGONSEYE_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DRAGONSEYE_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_button", DRAGONSEYE_BUTTON);
    public static final DeferredItem<BlockItem> DRAGONSEYE_SHELF_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_shelf", DRAGONSEYE_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> DRAGONSEYE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_leaves", DRAGONSEYE_LEAVES);
    public static final DeferredItem<BlockItem> DRAGONSEYE_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("dragonseye_sapling", DRAGONSEYE_SAPLING);

    // =============
    //  DRAGONWOOD
    // =============
    public static final DeferredItem<BlockItem> DRAGONWOOD_LOG_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_log", DRAGONWOOD_LOG);
    public static final DeferredItem<BlockItem> DRAGONWOOD_BUNDLE_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_bundle", DRAGONWOOD_BUNDLE);
    public static final DeferredItem<BlockItem> STRIPPED_DRAGONWOOD_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_dragonwood_log", STRIPPED_DRAGONWOOD_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_DRAGONWOOD_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_dragonwood_wood", STRIPPED_DRAGONWOOD_WOOD);
    public static final DeferredItem<BlockItem> DRAGONWOOD_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_planks", DRAGONWOOD_PLANKS);
    public static final DeferredItem<BlockItem> DRAGONWOOD_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_stairs", DRAGONWOOD_STAIRS);
    public static final DeferredItem<BlockItem> DRAGONWOOD_SLAB_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_slab", DRAGONWOOD_SLAB);
    public static final DeferredItem<BlockItem> DRAGONWOOD_FENCE_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_fence", DRAGONWOOD_FENCE);
    public static final DeferredItem<BlockItem> DRAGONWOOD_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_fence_gate", DRAGONWOOD_FENCE_GATE);
    public static final DeferredItem<BlockItem> DRAGONWOOD_DOOR_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_door", DRAGONWOOD_DOOR);
    public static final DeferredItem<BlockItem> DRAGONWOOD_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_trapdoor", DRAGONWOOD_TRAPDOOR);
    public static final DeferredItem<BlockItem> DRAGONWOOD_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_pressure_plate", DRAGONWOOD_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DRAGONWOOD_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_button", DRAGONWOOD_BUTTON);
    public static final DeferredItem<BlockItem> DRAGONWOOD_SHELF_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_shelf", DRAGONWOOD_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> DRAGONWOOD_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_leaves", DRAGONWOOD_LEAVES);
    public static final DeferredItem<BlockItem> DRAGONWOOD_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("dragonwood_sapling", DRAGONWOOD_SAPLING);

    // =============
    //  SHIMARIN
    // =============
    public static final DeferredItem<BlockItem> SHIMARIN_LOG_ITEM = ITEMS.registerSimpleBlockItem("shimarin_log", SHIMARIN_LOG);
    public static final DeferredItem<BlockItem> SHIMARIN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("shimarin_wood", SHIMARIN_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_SHIMARIN_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_shimarin_log", STRIPPED_SHIMARIN_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_SHIMARIN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_shimarin_wood", STRIPPED_SHIMARIN_WOOD);
    public static final DeferredItem<BlockItem> SHIMARIN_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("shimarin_planks", SHIMARIN_PLANKS);
    public static final DeferredItem<BlockItem> SHIMARIN_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("shimarin_stairs", SHIMARIN_STAIRS);
    public static final DeferredItem<BlockItem> SHIMARIN_SLAB_ITEM = ITEMS.registerSimpleBlockItem("shimarin_slab", SHIMARIN_SLAB);
    public static final DeferredItem<BlockItem> SHIMARIN_FENCE_ITEM = ITEMS.registerSimpleBlockItem("shimarin_fence", SHIMARIN_FENCE);
    public static final DeferredItem<BlockItem> SHIMARIN_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("shimarin_fence_gate", SHIMARIN_FENCE_GATE);
    public static final DeferredItem<BlockItem> SHIMARIN_DOOR_ITEM = ITEMS.registerSimpleBlockItem("shimarin_door", SHIMARIN_DOOR);
    public static final DeferredItem<BlockItem> SHIMARIN_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("shimarin_trapdoor", SHIMARIN_TRAPDOOR);
    public static final DeferredItem<BlockItem> SHIMARIN_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("shimarin_pressure_plate", SHIMARIN_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> SHIMARIN_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("shimarin_button", SHIMARIN_BUTTON);
    public static final DeferredItem<BlockItem> SHIMARIN_SHELF_ITEM = ITEMS.registerSimpleBlockItem("shimarin_shelf", SHIMARIN_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> SHIMARIN_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("shimarin_leaves", SHIMARIN_LEAVES);
    public static final DeferredItem<BlockItem> SHIMARIN_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("shimarin_sapling", SHIMARIN_SAPLING);

    // =============
    //  BRUME PINE
    // =============
    public static final DeferredItem<BlockItem> BRUME_PINE_LOG_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_log", BRUME_PINE_LOG);
    public static final DeferredItem<BlockItem> BRUME_PINE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_wood", BRUME_PINE_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_BRUME_PINE_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_brume_pine_log", STRIPPED_BRUME_PINE_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_BRUME_PINE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_brume_pine_wood", STRIPPED_BRUME_PINE_WOOD);
    public static final DeferredItem<BlockItem> BRUME_PINE_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_planks", BRUME_PINE_PLANKS);
    public static final DeferredItem<BlockItem> BRUME_PINE_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_stairs", BRUME_PINE_STAIRS);
    public static final DeferredItem<BlockItem> BRUME_PINE_SLAB_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_slab", BRUME_PINE_SLAB);
    public static final DeferredItem<BlockItem> BRUME_PINE_FENCE_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_fence", BRUME_PINE_FENCE);
    public static final DeferredItem<BlockItem> BRUME_PINE_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_fence_gate", BRUME_PINE_FENCE_GATE);
    public static final DeferredItem<BlockItem> BRUME_PINE_DOOR_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_door", BRUME_PINE_DOOR);
    public static final DeferredItem<BlockItem> BRUME_PINE_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_trapdoor", BRUME_PINE_TRAPDOOR);
    public static final DeferredItem<BlockItem> BRUME_PINE_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_pressure_plate", BRUME_PINE_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> BRUME_PINE_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_button", BRUME_PINE_BUTTON);
    public static final DeferredItem<BlockItem> BRUME_PINE_SHELF_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_shelf", BRUME_PINE_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> BRUME_PINE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_leaves", BRUME_PINE_LEAVES);
    public static final DeferredItem<BlockItem> BRUME_PINE_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("brume_pine_sapling", BRUME_PINE_SAPLING);

    // =============
    //  CANAR
    // =============
    public static final DeferredItem<BlockItem> CANAR_LOG_ITEM = ITEMS.registerSimpleBlockItem("canar_log", CANAR_LOG);
    public static final DeferredItem<BlockItem> CANAR_WOOD_ITEM = ITEMS.registerSimpleBlockItem("canar_wood", CANAR_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_CANAR_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_canar_log", STRIPPED_CANAR_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_CANAR_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_canar_wood", STRIPPED_CANAR_WOOD);
    public static final DeferredItem<BlockItem> CANAR_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("canar_planks", CANAR_PLANKS);
    public static final DeferredItem<BlockItem> CANAR_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("canar_stairs", CANAR_STAIRS);
    public static final DeferredItem<BlockItem> CANAR_SLAB_ITEM = ITEMS.registerSimpleBlockItem("canar_slab", CANAR_SLAB);
    public static final DeferredItem<BlockItem> CANAR_FENCE_ITEM = ITEMS.registerSimpleBlockItem("canar_fence", CANAR_FENCE);
    public static final DeferredItem<BlockItem> CANAR_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("canar_fence_gate", CANAR_FENCE_GATE);
    public static final DeferredItem<BlockItem> CANAR_DOOR_ITEM = ITEMS.registerSimpleBlockItem("canar_door", CANAR_DOOR);
    public static final DeferredItem<BlockItem> CANAR_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("canar_trapdoor", CANAR_TRAPDOOR);
    public static final DeferredItem<BlockItem> CANAR_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("canar_pressure_plate", CANAR_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> CANAR_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("canar_button", CANAR_BUTTON);
    public static final DeferredItem<BlockItem> CANAR_SHELF_ITEM = ITEMS.registerSimpleBlockItem("canar_shelf", CANAR_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> CANAR_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("canar_leaves", CANAR_LEAVES);
    public static final DeferredItem<BlockItem> CANAR_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("canar_sapling", CANAR_SAPLING);

    // =============
    //  SUREN
    // =============
    public static final DeferredItem<BlockItem> SUREN_LOG_ITEM = ITEMS.registerSimpleBlockItem("suren_log", SUREN_LOG);
    public static final DeferredItem<BlockItem> SUREN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("suren_wood", SUREN_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_SUREN_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_suren_log", STRIPPED_SUREN_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_SUREN_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_suren_wood", STRIPPED_SUREN_WOOD);
    public static final DeferredItem<BlockItem> SUREN_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("suren_planks", SUREN_PLANKS);
    public static final DeferredItem<BlockItem> SUREN_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("suren_stairs", SUREN_STAIRS);
    public static final DeferredItem<BlockItem> SUREN_SLAB_ITEM = ITEMS.registerSimpleBlockItem("suren_slab", SUREN_SLAB);
    public static final DeferredItem<BlockItem> SUREN_FENCE_ITEM = ITEMS.registerSimpleBlockItem("suren_fence", SUREN_FENCE);
    public static final DeferredItem<BlockItem> SUREN_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("suren_fence_gate", SUREN_FENCE_GATE);
    public static final DeferredItem<BlockItem> SUREN_DOOR_ITEM = ITEMS.registerSimpleBlockItem("suren_door", SUREN_DOOR);
    public static final DeferredItem<BlockItem> SUREN_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("suren_trapdoor", SUREN_TRAPDOOR);
    public static final DeferredItem<BlockItem> SUREN_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("suren_pressure_plate", SUREN_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> SUREN_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("suren_button", SUREN_BUTTON);
    public static final DeferredItem<BlockItem> SUREN_SHELF_ITEM = ITEMS.registerSimpleBlockItem("suren_shelf", SUREN_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> SUREN_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("suren_leaves", SUREN_LEAVES);
    public static final DeferredItem<BlockItem> SUREN_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("suren_sapling", SUREN_SAPLING);

    // =============
    //  HYLAN SEDGE
    // =============
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_LOG_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_log", HYLAN_SEDGE_LOG);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_wood", HYLAN_SEDGE_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_HYLAN_SEDGE_LOG_ITEM = ITEMS.registerSimpleBlockItem("stripped_hylan_sedge_log", STRIPPED_HYLAN_SEDGE_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_HYLAN_SEDGE_WOOD_ITEM = ITEMS.registerSimpleBlockItem("stripped_hylan_sedge_wood", STRIPPED_HYLAN_SEDGE_WOOD);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_PLANKS_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_planks", HYLAN_SEDGE_PLANKS);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_STAIRS_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_stairs", HYLAN_SEDGE_STAIRS);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_SLAB_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_slab", HYLAN_SEDGE_SLAB);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_FENCE_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_fence", HYLAN_SEDGE_FENCE);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_FENCE_GATE_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_fence_gate", HYLAN_SEDGE_FENCE_GATE);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_DOOR_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_door", HYLAN_SEDGE_DOOR);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_TRAPDOOR_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_trapdoor", HYLAN_SEDGE_TRAPDOOR);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_PRESSURE_PLATE_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_pressure_plate", HYLAN_SEDGE_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_BUTTON_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_button", HYLAN_SEDGE_BUTTON);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_SHELF_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_shelf", HYLAN_SEDGE_SHELF,
            properties -> properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_LEAVES_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_leaves", HYLAN_SEDGE_LEAVES);
    public static final DeferredItem<BlockItem> HYLAN_SEDGE_SAPLING_ITEM = ITEMS.registerSimpleBlockItem("hylan_sedge_sapling", HYLAN_SEDGE_SAPLING);

    private WyrmroostItemRegistry() {}
}
