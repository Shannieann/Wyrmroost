package com.github.wolfshotz.wyrmroost.world;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MobSpawnManager
{
    public static final Path CONFIG_FILE_PATH = FMLPaths.GAMEDIR.get().resolve("config/" + Wyrmroost.MOD_ID + "-mob-spawns.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Multimap<Biome.BiomeCategory, Record> BY_CATEGORY = ArrayListMultimap.create();
    private static final Multimap<ResourceLocation, Record> BY_BIOME = ArrayListMultimap.create();
    private static boolean initalized;

    public static Collection<Record> getSpawnList(Biome.BiomeCategory category, ResourceLocation biome)
    {
        Collection<Record> records;
        if ((records = BY_CATEGORY.get(category)) != null)
        {
            records.removeIf(r -> r.biomeBlacklist.contains(biome));
            return records;
        }
        if ((records = BY_BIOME.get(biome)) != null)
            return records;
        return Collections.emptyList();
    }

    public static void load()
    {
        if (initalized) return;

        initalized = true;
        Wyrmroost.LOG.info("Loading Biome mob spawn entries...");
        try
        {
            Path p = CONFIG_FILE_PATH;
            JsonElement json;
            if (!Files.exists(p))
            {
                Files.createFile(p);
                json = Record.CODEC
                        .encodeStart(JsonOps.INSTANCE, defaultList())
                        .getOrThrow(false, Wyrmroost.LOG::error);
                JsonWriter writer = new JsonWriter(Files.newBufferedWriter(p));
                writer.jsonValue(GSON.toJson(json));
                writer.close();
            }
            else
            {
                BufferedReader reader = Files.newBufferedReader(p);
                json = GSON.fromJson(reader, JsonElement.class);
                reader.close();
            }

            if (json == null)
            {
                Wyrmroost.LOG.error("Could not load Wyrmroost mob spawn data as it is null or empty.");
                return;
            }

            parse(json);
            Wyrmroost.LOG.info("Biome mob spawn entries successfully deserialized.");
        }
        catch (Exception e)
        {
            Wyrmroost.LOG.error("Could not load Wyrmroost mob spawn data. Something went horrifically wrong...", e);
        }
    }

    public static void close()
    {
        initalized = false;
        BY_CATEGORY.clear();
        BY_BIOME.clear();
    }

    private static void parse(JsonElement json)
    {
        List<Record> result = Record.CODEC
                .decode(JsonOps.INSTANCE, json)
                .map(Pair::getFirst)
                .getOrThrow(false, Wyrmroost.LOG::error);

        for (Record record : result)
        {
            record.category.ifPresent(category -> BY_CATEGORY.put(category, record));
            record.biomes.forEach(b -> BY_BIOME.put(b, record));
        }
    }

    private static List<Record> defaultList()
    {
        return ImmutableList.of(
                //rec(WREntities.LESSER_DESERTWYRM, 3, 1, 3, Biome.BiomeCategory.DESERT),
                rec(WREntities.ROOSTSTALKER.get(), 6, 2, 5, Biome.BiomeCategory.PLAINS),
                rec(WREntities.ROOSTSTALKER.get(), 7, 1, 4, Biome.BiomeCategory.FOREST)
                //rec(WREntities.OVERWORLD_DRAKE, 5, 1, 3, Biome.BiomeCategory.PLAINS),
                //rec(WREntities.OVERWORLD_DRAKE, 4, 1, 5, Biome.BiomeCategory.SAVANNA),
                //rec(WREntities.SILVER_GLIDER, 20, 2, 6, Biome.BiomeCategory.OCEAN),
                //rec(WREntities.SILVER_GLIDER, 10, 1, 4, Biome.BiomeCategory.BEACH),
                //rec(WREntities.DRAGON_FRUIT_DRAKE, 25, 3, 6, Biome.BiomeCategory.JUNGLE)
        );
    }

    private static Record rec(EntityType<?> entity, int weight, int minCount, int maxCount, Biome.BiomeCategory category)
    {
        return rec(entity, weight, minCount, maxCount, category, MobCategory.CREATURE);
    }

    private static Record rec(EntityType<?> entity, int weight, int minCount, int maxCount, Biome.BiomeCategory category, MobCategory classification)
    {
        return new Record(entity, Optional.of(category), ImmutableList.of(), ImmutableSet.of(), classification, weight, minCount, maxCount);
    }

    public static class Record
    {
        public static final Codec<List<Record>> CODEC = Codec.list(RecordCodecBuilder.create(o -> o.group(
                Registry.ENTITY_TYPE_REGISTRY.fieldOf("entity_type").forGetter(e -> e.entity),
                Biome.BiomeCategory.CODEC.optionalFieldOf("biome_category").forGetter(e -> e.category),
                ResourceLocation.CODEC.listOf().optionalFieldOf("biomes").xmap(e -> e.orElse(ImmutableList.of()), Optional::of).forGetter(e -> e.biomes),
                ResourceLocation.CODEC.listOf().optionalFieldOf("biome_blacklist").xmap(e -> e.map(ImmutableSet::copyOf).orElse(ImmutableSet.of()), s -> Optional.of(ImmutableList.copyOf(s))).forGetter(e -> e.biomeBlacklist),
                MobCategory.CODEC.optionalFieldOf("classification").xmap(p -> p.orElse(MobCategory.CREATURE), Optional::ofNullable).forGetter(e -> e.classification),
                Codec.INT.fieldOf("weight").forGetter(e -> e.weight),
                Codec.INT.fieldOf("min_count").forGetter(e -> e.minCount),
                Codec.INT.fieldOf("max_count").forGetter(e -> e.maxCount)
        ).apply(o, Record::new)));

        public final EntityType<?> entity;
        public final Optional<Biome.BiomeCategory> category;
        public final List<ResourceLocation> biomes;
        public final ImmutableSet<ResourceLocation> biomeBlacklist; // for boosted performance during "contains" spam
        public final MobCategory classification;
        public final int weight;
        public final int minCount;
        public final int maxCount;

        public Record(EntityType<?> entity, Optional<Biome.BiomeCategory> category, List<ResourceLocation> biomes, ImmutableSet<ResourceLocation> biomeBlacklist, MobCategory classification, int weight, int minCount, int maxCount)
        {
            this.entity = entity;
            this.category = category;
            this.biomes = biomes;
            this.biomeBlacklist = biomeBlacklist;
            this.classification = classification;
            this.weight = weight;
            this.minCount = minCount;
            this.maxCount = maxCount;

            if (!category.isPresent() && !biomes.isEmpty())
                throw new IllegalArgumentException("Must have defined a biome category OR a list of biomes. Both is empty...");
        }
    }
}
*/