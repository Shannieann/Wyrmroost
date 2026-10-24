package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import com.java.shannieann.wyrmroost.world.feature.FeatureSalBush;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public final class WyrmroostFeatureRegistry {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Wyrmroost.MODID);

    public static final DeferredHolder<Feature<?>, FeatureSalBush> SAL_BUSH =
            FEATURES.register("sal_bush", () -> new FeatureSalBush(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }

    private WyrmroostFeatureRegistry() {}
}
