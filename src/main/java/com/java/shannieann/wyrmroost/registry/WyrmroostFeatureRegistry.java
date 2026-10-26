package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import com.java.shannieann.wyrmroost.world.feature.FeatureSalBush;
import com.java.shannieann.wyrmroost.world.feature.FeatureKaliburn;
import com.java.shannieann.wyrmroost.world.feature.FeatureDragonsEye;
import com.java.shannieann.wyrmroost.world.feature.FeatureDragonwood;
import com.java.shannieann.wyrmroost.world.feature.FeatureShimarin;
import com.java.shannieann.wyrmroost.world.feature.FeatureBrumePine;
import com.java.shannieann.wyrmroost.world.feature.FeatureCanar;
import com.java.shannieann.wyrmroost.world.feature.FeatureSuren;
import com.java.shannieann.wyrmroost.world.feature.FeatureHylanSedge;

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
    public static final DeferredHolder<Feature<?>, FeatureKaliburn> KALIBURN =
            FEATURES.register("kaliburn", () -> new FeatureKaliburn(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureDragonsEye> DRAGONSEYE =
            FEATURES.register("dragonseye", () -> new FeatureDragonsEye(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureDragonwood> DRAGONWOOD =
            FEATURES.register("dragonwood", () -> new FeatureDragonwood(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureShimarin> SHIMARIN =
            FEATURES.register("shimarin", () -> new FeatureShimarin(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureBrumePine> BRUME_PINE =
            FEATURES.register("brume_pine", () -> new FeatureBrumePine(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureCanar> CANAR =
            FEATURES.register("canar", () -> new FeatureCanar(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureSuren> SUREN =
            FEATURES.register("suren", () -> new FeatureSuren(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, FeatureHylanSedge> HYLAN_SEDGE =
            FEATURES.register("hylan_sedge", () -> new FeatureHylanSedge(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }

    private WyrmroostFeatureRegistry() {}
}
