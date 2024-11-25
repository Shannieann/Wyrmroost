package com.github.shannieann.wyrmroost.config;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeMod;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class WRServerConfig {
    //TODO: LANG FILE
    public static ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final Server SERVER;
    private static final String LANG_PREFIX = "config." + Wyrmroost.MOD_ID + ".";
    private static final Predicate<Object> STRING_PREDICATE = s -> s instanceof String;

    static {
        SERVER = new Server(SERVER_BUILDER);

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Server {
        private Server(final ForgeConfigSpec.Builder builder) {
            WYRMROOST_OPTIONS = new WyrmroostOptionsConfig(builder);
            GRIEFING = new GriefingConfig(builder);
            ENTITIES = new EntitiesConfig(builder);
        }
        public final WyrmroostOptionsConfig WYRMROOST_OPTIONS;
        public final GriefingConfig GRIEFING;
        public final EntitiesConfig ENTITIES;

    }

    public static class WyrmroostOptionsConfig {
        public final ForgeConfigSpec.BooleanValue debugMode;

        WyrmroostOptionsConfig(ForgeConfigSpec.Builder builder ) {
            builder.push("wyrmroost_options");
            this.debugMode = builder
                    .comment("Debug Mode - Do NOT enable unless directed by a Wyrmroost Developer")
                    .translation(LANG_PREFIX+"debug_mode")
                    .define("enable_debug_mode", false);
            builder.pop();
        }
    }

    public static class GriefingConfig {
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
            this.fireSpread = builder
                    .comment("If true dragon fire will spread",
                            "Note: not all dragons set fire.")
                    .translation(LANG_PREFIX + "dragon_griefing")
                    .define("fire_spread", false);
            builder.pop();
        }
        public final ForgeConfigSpec.BooleanValue respectMobGriefing;
        public final ForgeConfigSpec.BooleanValue dragonGriefing;
        public final ForgeConfigSpec.BooleanValue fireSpread;
    }

    public static class EntitiesConfig {
        EntitiesConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("entities");
            //ALPINE = new Alpine(builder);
            BUTTERFLY_LEVIATHAN = new ButterflyLeviathan(builder);
            //CANARI_WYVERN = new CanariWyvern(builder);
            //COIN_DRAGON = new CoinDragon(builder);
            //DRAGONFRUIT_DRAKE = new DragonfruitDrake(builder);
            //LESSER_DESERTWYRM = new LesserDesertwyrm(builder);
            //OVERWORLD_DRAKE = new OverworldDrake(builder);
            ROOSTSTALKER = new Rooststalker(builder);
            //ROYAL_RED = new RoyalRed(builder);
            //SILVER_GLIDER = new SilverGlider(builder);
            builder.pop();
        }

        //public final Alpine ALPINE;
        public final ButterflyLeviathan BUTTERFLY_LEVIATHAN;
        //public final CanariWyvern CANARI_WYVERN;
        //public final CoinDragon COIN_DRAGON;
        //public final DragonfruitDrake DRAGONFRUIT_DRAKE;
        //public final LesserDesertwyrm LESSER_DESERTWYRM;
        //public final OverworldDrake OVERWORLD_DRAKE;
        public final Rooststalker ROOSTSTALKER;
        //public final RoyalRed ROYAL_RED;
        //public final SilverGlider SILVER_GLIDER;
    }

    public static class SpawningConfig {
        SpawningConfig(final ForgeConfigSpec.Builder builder, int spawnRate, int minGroupSize, int maxGroupSize, List<? extends String> biomeTypes, List<? extends String> biomeWhitelist) {
            builder.push("spawning");
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
            builder.pop();
        }

        public final ForgeConfigSpec.IntValue spawnRate;
        public final ForgeConfigSpec.IntValue minGroupSize;
        public final ForgeConfigSpec.IntValue maxGroupSize;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeTypes;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist;
    }


    public static class DragonAttributesConfig {
        DragonAttributesConfig(final ForgeConfigSpec.Builder builder, int maxHealth, int attackDamge, int homeRadius) {
            builder.push("attributes");
            this.maxHealth = builder
                    .comment("The maximum health value of the dragon")
                    .translation(LANG_PREFIX+"max_health")
                    .defineInRange("max_health",maxHealth,1, Integer.MAX_VALUE);
            this.attackDamage = builder
                    .comment("The base  attack damage value of the dragon")
                    .translation(LANG_PREFIX+"attack_damage")
                    .defineInRange("attack_damage",attackDamge,1, Integer.MAX_VALUE);
            this.homeRadius = builder
                    .comment("The radius (not diameter!) of how far dragons can travel from their home points")
                    .translation(LANG_PREFIX + "home_radius")
                    .defineInRange("home_radius", homeRadius, 6, 1024);
            builder.pop();
        }
        public final ForgeConfigSpec.IntValue maxHealth;
        public final ForgeConfigSpec.IntValue attackDamage;
        public final ForgeConfigSpec.IntValue homeRadius;
    }


    public static class DragonBreedingConfig {
        DragonBreedingConfig(final ForgeConfigSpec.Builder builder, int breedLimit, int hatchTime, int ageProgress) {
            builder.push("breeding");
            this.breedLimit = builder
                    .comment("Breed limit for each dragon. This determines how many times a certain dragon can breed",
                            "Set to 0 to disable breeding")
                    .translation(LANG_PREFIX + "breed_limits")
                    .defineInRange("breed_limits", breedLimit, 0, Integer.MAX_VALUE);
            this.hatchTime = builder
                    .comment("Hatch time for the dragon egg, in seconds")
                    .translation(LANG_PREFIX + "hatch_time")
                    .defineInRange("hatch_time", hatchTime, 0, Integer.MAX_VALUE);
            this.ageProgress = builder
                    .comment("Percentage by which a baby's age will progress towards adult, each minute",
                            "If set to 10, it will progress by 10% each minute, meaning it will take 10 minutes to reach adult (100%)")
                    .translation(LANG_PREFIX + "age_progress")
                    .defineInRange("age_progress", ageProgress, 10, 100);
            builder.pop();
        }
        public final ForgeConfigSpec.IntValue breedLimit;

        public final ForgeConfigSpec.IntValue hatchTime;
        public final ForgeConfigSpec.IntValue ageProgress;

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
                    Collections.singletonList(""));
            dragonAttributesConfig = new DragonAttributesConfig(builder,
                    180,
                    10,
                    2
            );
            dragonBreedingConfig = new DragonBreedingConfig(builder,
                      2,
                    1600,
                    10);
            builder.pop();
        }

        public final SpawningConfig spawningConfig;
        public final DragonAttributesConfig dragonAttributesConfig;
        public final DragonBreedingConfig dragonBreedingConfig;
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

    public static class Rooststalker {
        Rooststalker(final ForgeConfigSpec.Builder builder) {
            builder.push("rooststalker");
            spawningConfig = new SpawningConfig(builder,
                    5,
                    2,
                    10,
                    Collections.singletonList(""),
                    Collections.singletonList("minecraft:plains"));
            dragonAttributesConfig = new DragonAttributesConfig(builder,
                    16,
                    2,
                    2
            );
            dragonBreedingConfig = new DragonBreedingConfig(builder,
                    5,
                    600,
                    20);
            builder.pop();
        }

        public final SpawningConfig spawningConfig;
        public final DragonAttributesConfig dragonAttributesConfig;
        public final DragonBreedingConfig dragonBreedingConfig;
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
