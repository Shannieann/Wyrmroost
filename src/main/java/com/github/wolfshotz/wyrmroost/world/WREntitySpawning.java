package com.github.wolfshotz.wyrmroost.world;

import com.github.wolfshotz.wyrmroost.config.WRServerConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntityTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class WREntitySpawning {
    public static void registerSpawnPlacementTypes() {
        SpawnPlacements.register(WREntityTypes.LESSER_DESERTWYRM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LesserDesertwyrmEntity::getSpawnPlacement);
    }

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        if (biomeName == null) return;
        ResourceKey<Biome> biomeKey = ResourceKey.create(ForgeRegistries.Keys.BIOMES, biomeName);
        if (WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.spawnRate.get() > 0 && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.biomeTypes, WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.biomeWhitelist, biomeKey)) {
            registerEntityWorldSpawn(WREntityTypes.LESSER_DESERTWYRM.get(), WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig, MobCategory.CREATURE, event);
        }
    }


    public static boolean isBiomeInConfig(ForgeConfigSpec.ConfigValue<List<? extends String>> biomeTypes, ForgeConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist, ResourceKey<Biome> biomeKey) {
        ResourceLocation biomeName = biomeKey.location();
        if (biomeWhitelist.get().contains(biomeName.toString())) {
            return true;
        }

        Set<BiomeCombo> biomeCombos = new HashSet<>();
        for (String biomeComboString : biomeTypes.get()) {
            BiomeCombo biomeCombo = new BiomeCombo(biomeComboString);
            biomeCombos.add(biomeCombo);
        }
        for (BiomeCombo biomeCombo : biomeCombos) {
            if (biomeCombo.acceptsBiome(biomeKey)) return true;
        }
        return false;
    }

    private static void registerEntityWorldSpawn (EntityType < ? > entity, WRServerConfig.SpawningConfig spawnConfig, MobCategory classification, BiomeLoadingEvent event){
        event.getSpawns().getSpawner(classification).add(new MobSpawnSettings.SpawnerData(entity, spawnConfig.spawnRate.get(), spawnConfig.minGroupSize.get(), spawnConfig.maxGroupSize.get()));
    }

    private static class BiomeCombo {
        BiomeDictionary.Type[] neededTypes;
        boolean[] inverted;
        private BiomeCombo(String biomeComboString) {
            String[] typeStrings = biomeComboString.toUpperCase().replace(" ", "").split(",");
            neededTypes = new BiomeDictionary.Type[typeStrings.length];
            inverted = new boolean[typeStrings.length];
            for (int i = 0; i < typeStrings.length; i++) {
                if (typeStrings[i].length() == 0) {
                    continue;
                }
                inverted[i] = typeStrings[i].charAt(0) == '!';
                String name = typeStrings[i].replace("!", "");
                Collection<BiomeDictionary.Type> allTypes = BiomeDictionary.Type.getAll();
                BiomeDictionary.Type neededType = BiomeDictionary.Type.getType(name);
                if (!allTypes.contains(neededType)) System.out.println("Wyrmroost config warning: No biome dictionary type with name '" + name + "'. Unable to check for type.");
                neededTypes[i] = neededType;
            }
        }

        private boolean acceptsBiome(ResourceKey<Biome> biome) {
            Set<BiomeDictionary.Type> thisTypes = BiomeDictionary.getTypes(biome);
            for (int i = 0; i < neededTypes.length; i++) {
                if (neededTypes[i] == null) continue;
                if (!inverted[i]) {
                    if (!thisTypes.contains(neededTypes[i])) {
                        return false;
                    }
                }
                else {
                    if (thisTypes.contains(neededTypes[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
