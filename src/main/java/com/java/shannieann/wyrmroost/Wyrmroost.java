package com.java.shannieann.wyrmroost;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.java.shannieann.wyrmroost.registry.WyrmroostCreativeTab;
import com.java.shannieann.wyrmroost.registry.WyrmroostFeatureRegistry;
import com.java.shannieann.wyrmroost.registry.WyrmroostItemRegistry;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import static com.java.shannieann.wyrmroost.registry.WyrmroostItemRegistry.*;

@Mod(Wyrmroost.MODID)
public class Wyrmroost {
    public static final String MODID = "wyrmroost";
    public static final Logger LOGGER = LogUtils.getLogger();


    public Wyrmroost(IEventBus modEventBus, ModContainer modContainer) {
        WyrmroostBlockRegistry.register(modEventBus);
        WyrmroostItemRegistry.register(modEventBus);
        WyrmroostFeatureRegistry.register(modEventBus);
        WyrmroostCreativeTab.register(modEventBus);
        modEventBus.addListener(Wyrmroost::addBlockEntityBlocks);
        //TODO: Log
        LOGGER.info("Wyrmroost: Testing...");
    }

    private static void addBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityTypes.SHELF, WyrmroostBlockRegistry.SAL_BUSH_SHELF.get());
    }

}
