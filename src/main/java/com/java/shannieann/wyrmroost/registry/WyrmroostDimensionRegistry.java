package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;


public final class WyrmroostDimensionRegistry {
    //TODO: Real terrain, the flat layer is only for testing trees
    public static final ResourceKey<LevelStem> WYRMDIM = ResourceKey.create(Registries.LEVEL_STEM, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "wyrmdim"));
    public static final ResourceKey<DimensionType> WYRMDIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "wyrmdim_dimension_type"));

    private WyrmroostDimensionRegistry() {}
}
