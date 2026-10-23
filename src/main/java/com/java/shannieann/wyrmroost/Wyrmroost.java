package com.java.shannieann.wyrmroost;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// Must match mod_id in gradle.properties (and therefore neoforge.mods.toml)
@Mod(Wyrmroost.MODID)
public class Wyrmroost {
    public static final String MODID = "wyrmroost";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Wyrmroost(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Wyrmroost: Testing...");
    }
}
