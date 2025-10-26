package com.github.shannieann.wyrmroost.world;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class WREntitySpawning {
    public static void registerSpawnPlacementTypes() {
//        SpawnPlacements.register(WREntityTypes.LESSER_DESERTWYRM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLesserDesertwyrm::getSpawnPlacement);
    }

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        if (biomeName == null) return;
        //TODO: Alphabetical Order
        ResourceKey<Biome> biomeKey = ResourceKey.create(ForgeRegistries.Keys.BIOMES, biomeName);
        /*
        if (WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.spawningConfig.spawnRate.get() > 0 && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.spawningConfig.biomeTypes, WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.spawningConfig.biomeWhitelist, biomeKey)) {
            registerEntityWorldSpawn(WREntityTypes.SILVER_GLIDER.get(), WRServerConfig.SERVER.ENTITIES.SILVER_GLIDER.spawningConfig, MobCategory.CREATURE, event);
        }

        /*

        if (WRServerConfig.SERVER.ENTITIES.ROYAL_RED.spawningConfig.spawnRate.get() > 0 && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.ROYAL_RED.spawningConfig.biomeTypes, WRServerConfig.SERVER.ENTITIES.ROYAL_RED.spawningConfig.biomeWhitelist, biomeKey)) {
            registerEntityWorldSpawn(WREntityTypes.ROYAL_RED.get(), WRServerConfig.SERVER.ENTITIES.ROYAL_RED.spawningConfig, MobCategory.CREATURE, event);
        }
        */

        //Butterfly Leviathan
        if (WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.spawningConfig.spawnRate.get() > 0
                && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.spawningConfig.biomeTypes,
                WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.spawningConfig.biomeWhitelist, biomeKey)) {

            registerEntityWorldSpawn(WREntityTypes.BUTTERFLY_LEVIATHAN.get(),
                WRServerConfig.SERVER.ENTITIES.BUTTERFLY_LEVIATHAN.spawningConfig,
                MobCategory.CREATURE, event);
        }

        if (WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.spawningConfig.spawnRate.get() > 0
                && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.spawningConfig.biomeTypes,
                WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.spawningConfig.biomeWhitelist, biomeKey)) {

            registerEntityWorldSpawn(WREntityTypes.CANARI_WYVERN.get(),
                WRServerConfig.SERVER.ENTITIES.CANARI_WYVERN.spawningConfig,
                MobCategory.CREATURE, event);
        }

        //Lesser DesertWyrm
        if (WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.spawnRate.get() > 0
                && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.biomeTypes,
                WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig.biomeWhitelist, biomeKey)) {
            registerEntityWorldSpawn(WREntityTypes.LESSER_DESERTWYRM.get(),
                WRServerConfig.SERVER.ENTITIES.LESSER_DESERTWYRM.spawningConfig,
                MobCategory.CREATURE, event);
        }

        //Overworld Drake
        if (WRServerConfig.SERVER.ENTITIES.OVERWORLD_DRAKE.spawningConfig.spawnRate.get() > 0
                && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.OVERWORLD_DRAKE.spawningConfig.biomeTypes,
                WRServerConfig.SERVER.ENTITIES.OVERWORLD_DRAKE.spawningConfig.biomeWhitelist, biomeKey)) {

            registerEntityWorldSpawn(WREntityTypes.OVERWORLD_DRAKE.get(),
                WRServerConfig.SERVER.ENTITIES.OVERWORLD_DRAKE.spawningConfig,
                MobCategory.CREATURE, event);
        }

        //Rooststalker
        if (WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.spawningConfig.spawnRate.get() > 0
                && isBiomeInConfig(WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.spawningConfig.biomeTypes,
                WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.spawningConfig.biomeWhitelist, biomeKey)) {

            registerEntityWorldSpawn(WREntityTypes.ROOSTSTALKER.get(),
                WRServerConfig.SERVER.ENTITIES.ROOSTSTALKER.spawningConfig,
                MobCategory.CREATURE, event);
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
