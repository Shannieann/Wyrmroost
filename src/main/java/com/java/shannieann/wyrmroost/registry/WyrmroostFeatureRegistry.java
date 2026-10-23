package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import com.java.shannieann.wyrmroost.world.feature.FeatureSalBush;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public final class WyrmroostFeatureRegistry {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURE_TYPES =
            DeferredRegister.create(Registries.FEATURE_TYPE, Wyrmroost.MODID);

    // Becomes the type id "wyrmroost:sal_bush"
    public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<FeatureSalBush>> SAL_BUSH =
            FEATURE_TYPES.register("sal_bush", () -> FeatureSalBush.CODEC);

    public static void register(IEventBus modEventBus) {
        FEATURE_TYPES.register(modEventBus);
    }

    private WyrmroostFeatureRegistry() {}
}