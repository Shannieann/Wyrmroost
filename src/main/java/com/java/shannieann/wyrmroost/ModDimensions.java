package com.java.shannieann.wyrmroost;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

/**
 * Keys pointing at the JSON files in data/dimensionmod/.
 * Not used yet; they're here so later code (portals, teleporting, datagen)
 * can refer to the dimension without hard-coding strings.
 */
public final class ModDimensions {
    // data/dimensionmod/dimension/wyrmdim.json
    public static final ResourceKey<LevelStem> MY_DIMENSION =
            ResourceKey.create(Registries.LEVEL_STEM, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "my_dimension"));

    // data/dimensionmod/dimension_type/wyrmdim_dimension_type.json
    public static final ResourceKey<DimensionType> MY_DIMENSION_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "my_dimension_type"));

    private ModDimensions() {}
}
