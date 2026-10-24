package com.java.shannieann.wyrmroost;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;
import com.java.shannieann.wyrmroost.registry.WyrmroostCreativeTab;
import com.java.shannieann.wyrmroost.registry.WyrmroostFeatureRegistry;
import com.java.shannieann.wyrmroost.registry.WyrmroostItemRegistry;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Wyrmroost.MODID)
public class Wyrmroost {
    public static final String MODID = "wyrmroost";
    public static final Logger LOGGER = LogUtils.getLogger();


    public Wyrmroost(IEventBus modEventBus, ModContainer modContainer) {
        WyrmroostBlockRegistry.register(modEventBus);
        WyrmroostItemRegistry.register(modEventBus);
        WyrmroostFeatureRegistry.register(modEventBus);
        WyrmroostCreativeTab.register(modEventBus);
        //TODO: Log
        LOGGER.info("Wyrmroost: Testing...");
    }



}
