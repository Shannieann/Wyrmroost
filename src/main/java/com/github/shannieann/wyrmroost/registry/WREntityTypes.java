package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.*;
import com.github.shannieann.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.shannieann.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import com.github.shannieann.wyrmroost.entities.projectile.GeodeTippedArrowEntity;
import com.github.shannieann.wyrmroost.entities.projectile.SoulCrystalEntity;
import com.github.shannieann.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND;

@SuppressWarnings("unchecked")
public class WREntityTypes<E extends Entity> extends EntityType<E>
{
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Wyrmroost.MOD_ID);

    public static final RegistryObject<EntityType<EntityLesserDesertwyrm>> LESSER_DESERTWYRM =
            creature("lesser_desertwyrm", EntityLesserDesertwyrm::new)
                    .size(0.6f, 0.2f)
                    .attributes(EntityLesserDesertwyrm::getAttributeSupplier)
                    .spawnPlacement(ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLesserDesertwyrm::getSpawnPlacement)
                    .spawnEgg(0xD6BCBC, 0xDEB6C7)
                    .dragonEgg(new DragonEggProperties(0.15f, 0.1f, 10000))
                    .packetInterval(5)
                    .build();

    public static final RegistryObject<EntityType<EntityOverworldDrake>> OVERWORLD_DRAKE =
            creature("overworld_drake", EntityOverworldDrake::new)
                    .size(2.376f, 2.58f)
                    .attributes(EntityOverworldDrake::getAttributeSupplier)
                    .spawnPlacement()
                    .spawnEgg(0x788716, 0x3E623E)
                    .dragonEgg(new DragonEggProperties(0.35f, 0.6f, 18000))
                    .trackingRange(10)
                    .build();
    public static final RegistryObject<EntityType<EntitySilverGlider>> SILVER_GLIDER =
            creature("silver_glider", EntitySilverGlider::new)
                    .size(1.5f, 0.75f)
                    .attributes(EntitySilverGlider::getAttributeSupplier)
                    .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySilverGlider::getSpawnPlacement)
                    .spawnEgg(0xC8C8C8, 0xC4C4C4)
                    .dragonEgg(new DragonEggProperties(0.2f, 0.35f, 12000))
                    .trackingRange(8)
                    .build();

    public static final RegistryObject<EntityType<EntityRoostStalker>> ROOST_STALKER =
            creature("roost_stalker", EntityRoostStalker::new)
                    .size(0.65f, 0.5f)
                    .attributes(EntityRoostStalker::getAttributeSupplier)
                    .spawnPlacement()
                    .spawnEgg(0x52100D, 0x959595)
                    .dragonEgg(new DragonEggProperties(0.175f, 0.3f, 6000))
                    .build();

    public static final RegistryObject<EntityType<EntityButterflyLeviathan>> BUTTERFLY_LEVIATHAN =
            ofGroup("butterfly_leviathan", EntityButterflyLeviathan::new,MobCategory.CREATURE)
                    .size(5.0f, 5.0f)
                    .attributes(EntityButterflyLeviathan::getAttributeSupplier)
                    .packetInterval(2)
                    .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.OCEAN_FLOOR_WG, EntityButterflyLeviathan::getSpawnPlacement)
                    .spawnEgg(0x17283C, 0x7A6F5A)
                    .dragonEgg(new DragonEggProperties(0.5f, 0.8f, 40000).setConditions(Entity::isInWater))
                    .trackingRange(8)
                    .build();

    /*
    public static final RegistryObject<EntityType<DragonFruitDrakeEntity>> DRAGON_FRUIT_DRAKE = creature("dragon_fruit_drake", DragonFruitDrakeEntity::new)
            .size(1.5f, 1.9f)
            .attributes(DragonFruitDrakeEntity::getAttributeSupplier)
            .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DragonFruitDrakeEntity::getSpawnPlacement)
            .spawnEgg(0xe05c9a, 0x788716)
            .dragonEgg(new DragonEggProperties(0.25f, 0.35f, 9600))
            .renderModel(() -> DragonFruitDrakeModel::new)
            .build();

     */

    /*
    public static final RegistryObject<EntityType<EntityCanariWyvern>> CANARI_WYVERN = creature("canari_wyvern", EntityCanariWyvern::new)
            .size(0.65f, 0.85f)
            .attributes(EntityCanariWyvern::getAttributeSupplier)
            .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, WRDragonEntity::canFlyerSpawn)
            .spawnEgg(0x1D1F28, 0x492E0E)
            .dragonEgg(new DragonEggProperties(0.175f, 0.275f, 6000).setConditions(c -> c.level.getBlockState(c.blockPosition().below()).getBlock() == Blocks.JUNGLE_LEAVES))
            .renderModel(() -> CanariWyvernModel::new)
            .build();

     */




    public static final RegistryObject<EntityType<EntityRoyalRed>> ROYAL_RED = creature("royal_red", EntityRoyalRed::new)
            .size(3f, 3.9f)
            .attributes(EntityRoyalRed::getAttributeSupplier)
            .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, WRDragonEntity::canFlyerSpawn)
            .spawnEgg(0x8a0900, 0x0)
            .dragonEgg(new DragonEggProperties(0.45f, 0.7f, 72000))
            .fireImmune()
            .trackingRange(11)
            .build();
/*
    public static final RegistryObject<EntityType<CoinDragonEntity>> COIN_DRAGON = creature("coin_dragon", CoinDragonEntity::new)
            .size(0.35f, 0.435f)
            .renderModel(() -> CoinDragonModel::new)
            .spawnPlacement()
            .attributes(CoinDragonEntity::getAttributeSupplier)
            .trackingRange(2)
            .build();

    public static final RegistryObject<EntityType<AlpineEntity>> ALPINE = creature("alpine", AlpineEntity::new)
            .size(2f, 2f)
            .attributes(AlpineEntity::getAttributeSupplier)
            .spawnPlacement(SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, WRDragonEntity::canFlyerSpawn)
            .spawnEgg(0xe3f8ff, 0xa8e9ff)
            .dragonEgg(new DragonEggProperties(0.35f, 0.55f, 12000))
            .renderModel(() -> AlpineModel::new)
            .trackingRange(9)
            .build();
*/
    public static final RegistryObject<EntityType<DragonEggEntity>> DRAGON_EGG = WREntityTypes.<DragonEggEntity>ofGroup("dragon_egg", DragonEggEntity::new, MobCategory.MISC)
            .noSummon()
            .clientFactory(DragonEggEntity::new)
            .trackingRange(4)
            .packetInterval(5)
            .build();

    public static final RegistryObject<EntityType<GeodeTippedArrowEntity>> GEODE_TIPPED_ARROW = WREntityTypes.<GeodeTippedArrowEntity>ofGroup("geode_tipped_arrow", GeodeTippedArrowEntity::new, MobCategory.MISC)
            .size(0.5f, 0.5f)
            //.renderer(() -> GeodeTippedArrowRenderer::new)
            .clientFactory(GeodeTippedArrowEntity::new)
            .trackingRange(4)
            .packetInterval(10)
            .build();

    public static final RegistryObject<EntityType<FireBreathEntity>> FIRE_BREATH = WREntityTypes.<FireBreathEntity>ofGroup("fire_breath", FireBreathEntity::new, MobCategory.MISC)
            .size(0.75f, 0.75f)
            .noSave()
            .noSummon()
            .packetInterval(10)
            .build();

    public static final RegistryObject<EntityType<EffectLightningSphere>> LIGHTNING_SPHERE =
            WREntityTypes.<EffectLightningSphere>ofGroup("lightning_sphere", EffectLightningSphere::new, MobCategory.MISC)
            .size(1.0f, 1.0f)
            .noSave()
            .noSummon()
            .packetInterval(10)
            .build();
/*
    public static final RegistryObject<EntityType<WindGustEntity>> WIND_GUST = WREntityTypes.<WindGustEntity>ofGroup("wind_gust", WindGustEntity::new, MobCategory.MISC)
            .size(4, 4)
            .renderer(() -> EmptyRenderer::new)
            .noSave()
            .noSummon()
            .packetInterval(10)
            .build();
*/
    public static final RegistryObject<EntityType<SoulCrystalEntity>> SOUL_CRYSTAL = WREntityTypes.<SoulCrystalEntity>ofGroup("soul_crystal", SoulCrystalEntity::new, MobCategory.MISC)
            .size(0.25f, 0.25f)
            .trackingRange(4)
            .packetInterval(10)
            .build();

    @Nullable public final Supplier<AttributeSupplier> attributes;
    @Nullable public final SpawnPlacementEntry<E> spawnPlacement;
    @Nullable public final DragonEggProperties eggProperties;

    public WREntityTypes(EntityType.EntityFactory<E> factory, MobCategory group, boolean serialize, boolean summon, boolean fireImmune, boolean spawnsFarFromPlayer, ImmutableSet<Block> immuneTo, EntityDimensions size, int trackingRange, int tickRate, Predicate<EntityType<?>> velocityUpdateSupplier, ToIntFunction<EntityType<?>> trackingRangeSupplier, ToIntFunction<EntityType<?>> updateIntervalSupplier, BiFunction<PlayMessages.SpawnEntity, Level, E> customClientFactory, Supplier<AttributeSupplier> attributes, SpawnPlacementEntry<E> spawnPlacement, DragonEggProperties props)
    {
        super(factory, group, serialize, summon, fireImmune, spawnsFarFromPlayer, immuneTo, size, trackingRange, tickRate, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
        this.attributes = attributes;
        this.spawnPlacement = spawnPlacement;
        this.eggProperties = props;
    }


    private static <T extends Entity> Builder<T> creature(String name, EntityType.EntityFactory<T> factory)
    {
        return new Builder<>(name, factory, MobCategory.CREATURE);
    }

    private static <T extends Entity> Builder<T> ofGroup(String name, EntityType.EntityFactory<T> factory, MobCategory group)
    {
        return new Builder<>(name, factory, group);
    }

    public static class Attributes
    {
        public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Wyrmroost.MOD_ID);

        public static final RegistryObject<Attribute> PROJECTILE_DAMAGE = ranged("generic.projectileDamage", 2d, 0, 2048d);

        private static RegistryObject<Attribute> ranged(String name, double defaultValue, double min, double max)
        {
            return register(name.toLowerCase().replace('.', '_'), () -> new RangedAttribute("attribute.name." + name, defaultValue, min, max));
        }

        private static RegistryObject<Attribute> register(String name, Supplier<Attribute> attribute)
        {
            return REGISTRY.register(name, attribute);
        }
    }

    public static class Tags
    {
        public static final TagKey<EntityType<?>> SOUL_BEARERS = bind("soul_bearers");

        private static TagKey<EntityType<?>> bind(String name)
        {
            return EntityTypeTags.create(Wyrmroost.MOD_ID + ":" + name);
        }
    }

    private static class Builder<T extends Entity>
    {
        private final String name;
        private final EntityType.EntityFactory<T> factory;
        private final MobCategory category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int trackingRange = 5;
        private int packetInterval = 3;
        private boolean updatesVelocity = true;
        private EntityDimensions size = EntityDimensions.scalable(0.6f, 1.8f);
        private DragonEggProperties dragonEggProperties;
        private Supplier<AttributeSupplier> attributes = null;
        private SpawnPlacementEntry<T> spawnPlacement;
        private BiFunction<PlayMessages.SpawnEntity, Level, T> customClientFactory;
        private RegistryObject<EntityType<T>> registered;

        private Builder(String name, EntityType.EntityFactory<T> factory, MobCategory group)
        {
            this.name = name;
            this.factory = factory;
            this.category = group;
            this.canSpawnFarFromPlayer = group == MobCategory.CREATURE || group == MobCategory.MISC;
        }

        private Builder<T> size(float width, float height)
        {
            this.size = EntityDimensions.scalable(width, height);
            return this;
        }

        private Builder<T> noSummon()
        {
            this.summon = false;
            return this;
        }

        private Builder<T> noSave()
        {
            this.serialize = false;
            return this;
        }

        private Builder<T> fireImmune()
        {
            this.fireImmune = true;
            return this;
        }

        private Builder<T> immuneTo(Block... blocks)
        {
            this.immuneTo = ImmutableSet.copyOf(blocks);
            return this;
        }

        private Builder<T> canSpawnFarFromPlayer()
        {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        private Builder<T> trackingRange(int trackingRange)
        {
            this.trackingRange = trackingRange;
            return this;
        }

        private Builder<T> packetInterval(int rate)
        {
            this.packetInterval = rate;
            return this;
        }

        private Builder<T> noVelocityUpdates()
        {
            this.updatesVelocity = false;
            return this;
        }

        private Builder<T> clientFactory(BiFunction<PlayMessages.SpawnEntity, Level, T> clientFactory)
        {
            this.customClientFactory = clientFactory;
            return this;
        }

        private Builder<T> spawnEgg(int primColor, int secColor)
        {
            WRItems.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(() -> (EntityType<? extends Mob>) registered.get(), primColor, secColor,WRItems.builder()));
            return this;
        }



        // needs to be supplier to accomadate for attributes deferred registered
        private Builder<T> attributes(Supplier<AttributeSupplier.Builder> map)
        {
            this.attributes = () -> map.get().build();
            return this;
        }

        private <F extends Mob> Builder<T> spawnPlacement(SpawnPlacements.Type spawnPlacementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<F> predicate)
        {
            this.spawnPlacement = new SpawnPlacementEntry<T>(spawnPlacementType, heightmapType, ((SpawnPlacements.SpawnPredicate<T>) predicate));
            return this;
        }

        private Builder<T> spawnPlacement()
        {
            return spawnPlacement(ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkMobSpawnRules);
        }

        private Builder<T> dragonEgg(DragonEggProperties props)
        {
            this.dragonEggProperties = props;
            return this;
        }

        private RegistryObject<EntityType<T>> build()
        {
            registered = REGISTRY.register(name, () -> new WREntityTypes<>(factory, category, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, size, trackingRange, packetInterval, t -> updatesVelocity, t -> trackingRange, t -> packetInterval, customClientFactory, attributes, spawnPlacement, dragonEggProperties));
            return registered;
        }
    }

    private static class SpawnPlacementEntry<E extends Entity>
    {
        final Heightmap.Types heightMap;
        final SpawnPlacements.Type placement;
        final SpawnPlacements.SpawnPredicate<E> predicate;

        SpawnPlacementEntry(SpawnPlacements.Type placement, Heightmap.Types heightMap, SpawnPlacements.SpawnPredicate<E> predicate) {
            this.placement = placement;
            this.heightMap = heightMap;
            this.predicate = predicate;
        }
    }
}
