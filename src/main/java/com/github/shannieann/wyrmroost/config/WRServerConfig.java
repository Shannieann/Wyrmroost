package com.github.shannieann.wyrmroost.config;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class WRServerConfig {
    //TODO: LANG FILE
    //TODO: Config attributes
    public static ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final Server SERVER;
    private static final String LANG_PREFIX = "config." + Wyrmroost.MOD_ID + ".";
    private static final Predicate<Object> STRING_PREDICATE = s -> s instanceof String;
    private static final List<String> BREED_LIMIT_DEFAULTS = ImmutableList.of("butterfly_leviathan:1", "royal_red:2");

    static {
        SERVER = new Server(SERVER_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Server {
        private Server(final ForgeConfigSpec.Builder builder) {
            WYRMROOST_OPTIONS = new WyrmroostOptionsConfig(builder);
            DRAGON_OPTIONS = new DragonOptionsConfig(builder);
            GRIEFING = new GriefingConfig(builder);
            ENTITIES = new EntitiesConfig(builder);
        }
        public final WyrmroostOptionsConfig WYRMROOST_OPTIONS;
        public final DragonOptionsConfig DRAGON_OPTIONS;
        public final GriefingConfig GRIEFING;
        public final EntitiesConfig ENTITIES;

    }
    public static class WyrmroostOptionsConfig {
        public final ForgeConfigSpec.BooleanValue debugMode;

        WyrmroostOptionsConfig(ForgeConfigSpec.Builder builder ) {
            builder.push("wyrmroost_otions");
            this.debugMode = builder
                    .comment("Debug Mode - Do NOT enable unless directed by a Wyrmroost Developer")
                    .translation(LANG_PREFIX+"debug_mode")
                    .define("enable_debug_mode", false);
        }

    }

    public static class DragonOptionsConfig {
        public final ForgeConfigSpec.DoubleValue fireSpread;
        public final ForgeConfigSpec.IntValue homeRadius;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> breedLimits;


        DragonOptionsConfig(ForgeConfigSpec.Builder builder) {
            builder.push("dragon_options");
            this.fireSpread = builder
                    .comment("Base Flammability or spread of fire from Dragon Fire Breath",
                            "A value of 0 completely disables fire block damage.")
                    .translation(LANG_PREFIX + "breath_fire_spread")
                    .defineInRange("breath_fire_spread", 0.8, 0, 1);
            this.homeRadius = builder
                    .comment("The radius (not diameter!) of how far dragons can travel from their home points")
                    .translation(LANG_PREFIX + "home_radius")
                    .defineInRange("home_radius", 16, 6, 1024);
            this.breedLimits = builder
                    .comment("Breed limit for each dragon. This determines how many times a certain dragon can breed.",
                            "Leaving this blank ( `[]` ) will disable the functionality.")
                    .translation(LANG_PREFIX + "breed_limits")
                    .defineList("breed_limits", () -> BREED_LIMIT_DEFAULTS, e -> e instanceof String);
            builder.pop();
        }
    }

    public static class GriefingConfig {
        public final ForgeConfigSpec.BooleanValue respectMobGriefing;
        public final ForgeConfigSpec.BooleanValue dragonGriefing;


        GriefingConfig(ForgeConfigSpec.Builder builder) {
            builder.push("griefing");
            this.respectMobGriefing = builder
                    .comment("If true, dragons will respect the Minecraft Mob Griefing Gamerule.",
                            "Otherwise, they will follow the `dragon_griefing` config rule")
                    .translation(LANG_PREFIX + "respect_mob_griefing")
                    .define("respect_mob_griefing", true);
            this.dragonGriefing = builder
                    .comment("If true and not respecting mob griefing rules (`respect_mob_griefing`),",
                            "Allow dragons to destroy blocks.",
                            "Note: not all dragons destroy blocks and not all are as destructive as the next.")
                    .translation(LANG_PREFIX + "dragon_griefing")
                    .define("dragon_griefing", false);
            builder.pop();
        }
    }

    public static class EntitiesConfig {
        EntitiesConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("entities");
            ALPINE = new Alpine(builder);
            BUTTERFLY_LEVIATHAN = new ButterflyLeviathan(builder);
            CANARI_WYVERN = new CanariWyvern(builder);
            COIN_DRAGON = new CoinDragon(builder);
            DRAGONFRUIT_DRAKE = new DragonfruitDrake(builder);
            LESSER_DESERTWYRM = new LesserDesertwyrm(builder);
            OVERWORLD_DRAKE = new OverworldDrake(builder);
            ROOST_STALKER = new RoostStalker(builder);
            ROYAL_RED = new RoyalRed(builder);
            SILVER_GLIDER = new SilverGlider(builder);
            builder.pop();
        }

        public final Alpine ALPINE;
        public final ButterflyLeviathan BUTTERFLY_LEVIATHAN;
        public final CanariWyvern CANARI_WYVERN;
        public final CoinDragon COIN_DRAGON;
        public final DragonfruitDrake DRAGONFRUIT_DRAKE;
        public final LesserDesertwyrm LESSER_DESERTWYRM;
        public final OverworldDrake OVERWORLD_DRAKE;
        public final RoostStalker ROOST_STALKER;
        public final RoyalRed ROYAL_RED;
        public final SilverGlider SILVER_GLIDER;
    }

    public static class SpawningConfig {
        SpawningConfig(final ForgeConfigSpec.Builder builder, int spawnRate, int minGroupSize, int maxGroupSize, List<? extends String> biomeTypes, List<? extends String> biomeWhitelist) {
            this.spawnRate = builder
                    .comment("Spawn Rates will be proportional to this value. Set to 0 to disable spawning")
                    .translation(LANG_PREFIX + "spawn_rate")
                    .defineInRange("spawn_rate", spawnRate, 0, Integer.MAX_VALUE);
            this.minGroupSize = builder
                    .comment("Minimum number of this entity that will spawn in each group")
                    .translation(LANG_PREFIX + "min_group_size")
                    .defineInRange("min_group_size", minGroupSize, 1, Integer.MAX_VALUE);
            this.maxGroupSize = builder
                    .comment("Maximum number of this entity that will spawn in each group")
                    .translation(LANG_PREFIX + "max_group_size")
                    .defineInRange("max_group_size", maxGroupSize, 1, Integer.MAX_VALUE);
            this.biomeTypes = builder
                    .comment("Allow spawns in these biome types")
                    .translation(LANG_PREFIX + "biome_types")
                    .defineList("biome_type", biomeTypes, STRING_PREDICATE);
            this.biomeWhitelist = builder
                    .comment("Allow spawns in these biomes. Ignores the biome types specified above.")
                    .translation(LANG_PREFIX + "biome_whitelist")
                    .defineList("biome_whitelist", biomeWhitelist, STRING_PREDICATE);
        }

        public final ForgeConfigSpec.IntValue spawnRate;
        public final ForgeConfigSpec.IntValue minGroupSize;
        public final ForgeConfigSpec.IntValue maxGroupSize;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeTypes;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist;
    }

    public static class Alpine {
        Alpine(final ForgeConfigSpec.Builder builder) {
            builder.push("alpine");
            spawningConfig = new SpawningConfig(builder,
                    2,
                    1,
                    3,
                    Collections.singletonList("HILLS"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class ButterflyLeviathan {
        ButterflyLeviathan(final ForgeConfigSpec.Builder builder) {
            builder.push("butterfly_leviathan");
            spawningConfig = new SpawningConfig(builder,
                    2,
                    1,
                    3,
                    Collections.singletonList("OCEAN"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class CanariWyvern {
        CanariWyvern(final ForgeConfigSpec.Builder builder) {
            builder.push("canari_wyvern");
            spawningConfig = new SpawningConfig(builder,
                    2,
                    1,
                    3,
                    Collections.singletonList("SWAMP"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class CoinDragon {
        CoinDragon(final ForgeConfigSpec.Builder builder) {
            builder.push("coin_dragon");
            spawningConfig = new SpawningConfig(builder,
                    2,
                    1,
                    3,
                    Collections.singletonList(""),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }
    public static class DragonfruitDrake {
        DragonfruitDrake(final ForgeConfigSpec.Builder builder) {
            builder.push("dragonfruit_drake");
            spawningConfig = new SpawningConfig(builder,
                    20,
                    3,
                    6,
                    Collections.singletonList("JUNGLE"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class LesserDesertwyrm {
        LesserDesertwyrm(final ForgeConfigSpec.Builder builder) {
            builder.push("lesser_desertwyrm");
            spawningConfig = new SpawningConfig(builder,
                    3,
                    1,
                    3,
                    Collections.singletonList("SANDY,DRY,HOT"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class OverworldDrake {
        OverworldDrake(final ForgeConfigSpec.Builder builder) {
            builder.push("overworld_drake");
            spawningConfig = new SpawningConfig(builder,
                    4,
                    1,
                    4,
                    Arrays.asList("SAVANNA", "PLAINS"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class RoostStalker {
        RoostStalker(final ForgeConfigSpec.Builder builder) {
            builder.push("roost_stalker");
            spawningConfig = new SpawningConfig(builder,
                    7,
                    1,
                    4,
                    Arrays.asList("FOREST", "PLAINS"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class RoyalRed {
        RoyalRed(final ForgeConfigSpec.Builder builder) {
            builder.push("royal_red");
            spawningConfig = new SpawningConfig(builder,
                    2,
                    1,
                    3,
                    Collections.singletonList("HILLS"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }

    public static class SilverGlider {
        SilverGlider(final ForgeConfigSpec.Builder builder) {
            builder.push("silver_glider");
            spawningConfig = new SpawningConfig(builder,
                    15,
                    1,
                    6,
                    Arrays.asList("OCEAN", "BEACH"),
                    Collections.singletonList("")
            );
            builder.pop();
        }
        public final SpawningConfig spawningConfig;
    }
}
