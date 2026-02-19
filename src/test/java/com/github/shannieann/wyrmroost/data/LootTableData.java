package com.github.shannieann.wyrmroost.data;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.shannieann.wyrmroost.registry.WRBlocks.*;


class LootTableData extends LootTableProvider
{
    LootTableData(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return ImmutableList.of(Pair.of(BlookLoot::new, LootContextParamSets.BLOCK), Pair.of(Entities::new, LootContextParamSets.ENTITY));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker)
    {
    }

    private static class BlookLoot extends BlockLoot
    {
        public final Map<Block, LootTable.Builder> lootTables = new HashMap<>();

        void manualOverrides()
        {
            ore(BLUE_GEODE_ORE.get(), WRItems.BLUE_GEODE.get());
            ore(RED_GEODE_ORE.get(), WRItems.RED_GEODE.get());
            ore(PURPLE_GEODE_ORE.get(), WRItems.PURPLE_GEODE.get());

            /*silkTouch(MULCH.get(), Blocks.DIRT);
            silkTouch(FROSTED_GRASS.get(), Blocks.DIRT);
            leaves(BLUE_OSERI_LEAVES.get(), BLUE_OSERI_SAPLING.get());
            leaves(GOLD_OSERI_LEAVES.get(), GOLD_OSERI_SAPLING.get());
            leaves(PINK_OSERI_LEAVES.get(), PINK_OSERI_SAPLING.get());
            leaves(PURPLE_OSERI_LEAVES.get(), PURPLE_OSERI_SAPLING.get());
            leaves(WHITE_OSERI_LEAVES.get(), WHITE_OSERI_SAPLING.get());

            silkTouch(FORAH_STONE.getStone(), FORAH_COBBLESTONE.getStone());
            silkTouch(ABERYTE_STONE.get(), ABERYTE_COBBLESTONE.getStone());

            add(CANIS_ROOT.get(), applyExplosionDecay(CANIS_ROOT.get(), LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(CANIS_ROOT.get()))).withPool(LootPool.lootPool().when(BlockStateProperty.hasBlockStateProperties(CANIS_ROOT.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CarrotBlock.AGE, 3))).add(ItemLootEntry.lootTableItem(CANIS_ROOT.get()).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));*/
        }

        @Override
        protected void addTables()
        {
            manualOverrides();

            // All blocks that have not been given special treatment above, drop themselves!
            for (Block block : getKnownBlocks())
            {
                /*if (block instanceof PetalsBlock || block instanceof VineBlock || block instanceof AbstractPlantBlock || (block instanceof BushBlock && !(block instanceof SaplingBlock)))
                    add(block, BlockLootTables::createShearsOnlyDrop);
                else if (block instanceof DoorBlock) add(block, BlockLootTables::createDoorTable);
                else if (block instanceof AbstractCoralPlantBlock) dropWhenSilkTouch(block);
                else
                {*/
                    ResourceLocation lootTable = block.getLootTable();
                    boolean notInheriting = lootTable.getPath().replace("blocks/", "").equals(block.getRegistryName().getPath());
                    if (!lootTables.containsKey(block) && lootTable != BuiltInLootTables.EMPTY && notInheriting)
                        dropSelf(block);
                //}
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return WRModUtils.getRegistryEntries(REGISTRY);
        }

        private void leaves(Block leaves, Block sapling)
        {
            add(leaves, createOakLeavesDrops(leaves, sapling, 0.05f, 0.0625f, 0.083333336f, 0.1f));
        }

        private void ore(Block ore, Item output)
        {
            add(ore, createOreDrop(ore, output));
        }

        private void silkTouch(Block block, ItemLike orElse)
        {
            add(block, createSingleItemTableWithSilkTouch(block, orElse));
        }

        @Override
        protected void add(Block blockIn, LootTable.Builder table)
        {
            super.add(blockIn, table);
            lootTables.put(blockIn, table);
        }
    }

    private static class Entities extends EntityLoot
    {
        private static final LootItemConditionalFunction.Builder<?> ON_FIRE_SMELT = SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build())));

        private final Map<EntityType<?>, LootTable.Builder> lootTables = new HashMap<>();

        /**
         * Our way is much neater and cooler anyway. fuck mojang
         */
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer)
        {
            addTables();

            for (EntityType<?> entity : getKnownEntities())
            {
                if (!lootTables.containsKey(entity))
                {
                    if (entity.getCategory() == MobCategory.MISC) continue;
                    throw new IllegalArgumentException(String.format("Missing Loottable for entry: '%s'", entity.getRegistryName()));
                }
                consumer.accept(entity.getDefaultLootTable(), lootTables.remove(entity));
            }
        }

        @Override
        protected Iterable<EntityType<?>> getKnownEntities()
        {
            return WRModUtils.getRegistryEntries(WREntityTypes.REGISTRY);
        }

        /**
         * @param types the types to register an empty loot tables
         * @deprecated SHOULD ONLY USE THIS WHEN AN ENTITY ABSOLUTELY DOES NOT HAVE ONE, OR IN TESTING!
         */
        @Deprecated
        public void registerEmptyTables(EntityType<?>... types)
        {
            for (EntityType<?> type : types)
            {
                Wyrmroost.LOG.warn("Registering EMPTY Loottable for: '{}'", type.getRegistryName());
                add(type, LootTable.lootTable());
            }
        }

        @Override
        protected void add(EntityType<?> type, LootTable.Builder table)
        {
            lootTables.put(type, table);
        }

        @Override
        protected void addTables()
        {
            add(WREntityTypes.LESSER_DESERTWYRM.get(), LootTable.lootTable().withPool(singleRollPool().add(item(WRItems.LDWYRM.get(), 1).apply(ON_FIRE_SMELT))));

            add(WREntityTypes.OVERWORLD_DRAKE.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(item(Items.LEATHER, 5, 10).apply(looting(1, 4))))
                    .withPool(singleRollPool().add(meat(WRItems.RAW_COMMON_MEAT.get(), 1, 7, 3, 9)))
                    .withPool(singleRollPool().add(item(WRItems.DRAKE_BACKPLATE.get(), 1)).when(LootItemKilledByPlayerCondition.killedByPlayer()).apply(looting(2, 3))));

            add(WREntityTypes.ROOSTSTALKER.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_LOWTIER_MEAT.get(), 0, 2, 1, 2)))
                    .withPool(singleRollPool().add(item(Items.GOLD_NUGGET, 0, 2))));

            //add(WREntityTypes.DRAGON_FRUIT_DRAKE.get(), LootTable.lootTable().withPool(singleRollPool().add(item(Items.APPLE, 0, 6))));

            add(WREntityTypes.CANARI_WYVERN.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_COMMON_MEAT.get(), 0, 2, 1, 2)))
                    .withPool(singleRollPool().add(item(Items.FEATHER, 1, 4).apply(looting(2, 6)))));

            add(WREntityTypes.SILVER_GLIDER.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_LOWTIER_MEAT.get(), 0, 3, 1, 3))));

            add(WREntityTypes.BUTTERFLY_LEVIATHAN.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_APEX_MEAT.get(), 6, 10, 2, 4)))
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(1, 4)).add(item(Items.SEA_PICKLE, 0, 2).apply(looting(1, 2))).add(item(Items.SEAGRASS, 4, 14)).add(item(Items.KELP, 16, 24)))
                    .withPool(singleRollPool().add(item(Items.HEART_OF_THE_SEA, 1).when(LootItemRandomChanceCondition.randomChance(0.1f))).add(item(Items.NAUTILUS_SHELL, 1).when(LootItemRandomChanceCondition.randomChance(0.15f)))));

            add(WREntityTypes.ROYAL_RED.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_APEX_MEAT.get(), 4, 8, 3, 5))));

            add(WREntityTypes.COIN_DRAGON.get(), LootTable.lootTable().withPool(singleRollPool()
                    .add(item(Items.GOLD_NUGGET, 1))));

            add(WREntityTypes.ALPINE_DRAGON.get(), LootTable.lootTable()
                    .withPool(singleRollPool().add(meat(WRItems.RAW_COMMON_MEAT.get(), 3, 7, 2, 6)))
                    .withPool(singleRollPool().add(item(Items.FEATHER, 3, 10).apply(looting(3, 11)))));
        }

        private static LootingEnchantFunction.Builder looting(float min, float max)
        {
            return LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(min, max));
        }

        private static LootItem.Builder<?> item(ItemLike itemIn, float minIn, float maxIn)
        {
            return LootItem.lootTableItem(itemIn).apply(SetItemCountFunction.setCount(UniformGenerator.between(minIn, maxIn)));
        }

        private static LootItem.Builder<?> item(ItemLike itemIn, int amount)
        {
            return LootItem.lootTableItem(itemIn).apply(SetItemCountFunction.setCount(ConstantValue.exactly(amount)));
        }

        private static LootPool.Builder singleRollPool()
        {
            return LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        }

        private static LootItem.Builder<?> meat(ItemLike itemIn, int minAmount, int maxAmount, int lootingMin, int lootingMax)
        {
            return item(itemIn, minAmount, maxAmount).apply(ON_FIRE_SMELT).apply(looting(lootingMin, lootingMax));
        }
    }
}
