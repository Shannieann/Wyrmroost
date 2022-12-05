package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.github.wolfshotz.wyrmroost.registry.WREntities.*;

public class TagData
{
    // note block tags need to run before item tags
    static void provide(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        BlockData blockGen = new BlockData(gen, fileHelper);
        gen.addProvider(blockGen);
        gen.addProvider(new ItemData(gen, blockGen, fileHelper));
        gen.addProvider(new EntityData(gen, fileHelper));
        //gen.addProvider(new FluidData(gen, fileHelper));
    }

    private static class ItemData extends ItemTagsProvider
    {
        private final BlockData blockProvider;

        public ItemData(DataGenerator gen, BlockData blockGen, ExistingFileHelper fileHelper)
        {
            super(gen, blockGen, Wyrmroost.MOD_ID, fileHelper);
            this.blockProvider = blockGen;
        }

        @Override
        protected void addTags()
        {
            WRBlocks.Tags.ITEM_BLOCK_TAGS.forEach(this::copy);
            blockProvider.itemClones.forEach(this::copy);

            tag(Tags.Items.EGGS).add(WRItems.DRAGON_EGG.get());

            tag(ItemTags.PIGLIN_LOVED).add(WRItems.GOLD_DRAGON_ARMOR.get());

            tag(Tags.Items.GEMS).addTag(WRItems.Tags.GEMS_GEODE);
            tag(WRItems.Tags.GEMS_GEODE).add(WRItems.BLUE_GEODE.get(), WRItems.RED_GEODE.get(), WRItems.PURPLE_GEODE.get());

            tag(WRItems.Tags.DRAGON_MEATS).add(WRItems.RAW_LOWTIER_MEAT.get(), WRItems.COOKED_LOWTIER_MEAT.get(), WRItems.RAW_COMMON_MEAT.get(), WRItems.COOKED_COMMON_MEAT.get(), WRItems.RAW_APEX_MEAT.get(), WRItems.COOKED_APEX_MEAT.get(), WRItems.RAW_BEHEMOTH_MEAT.get(), WRItems.COOKED_BEHEMOTH_MEAT.get());

            tag(Tags.Items.INGOTS).addTag(WRItems.Tags.INGOTS_PLATINUM);
            tag(WRItems.Tags.INGOTS_PLATINUM).add(WRItems.PLATINUM_INGOT.get());

            tag(ItemTags.ARROWS).add(WRItems.BLUE_GEODE_ARROW.get(), WRItems.RED_GEODE_ARROW.get(), WRItems.PURPLE_GEODE_ARROW.get());

            tag(ItemTags.BEACON_PAYMENT_ITEMS).addTag(WRItems.Tags.GEMS_GEODE);
        }
    }

    private static class BlockData extends BlockTagsProvider
    {
        final Map<TagKey<Block>, TagKey<Item>> itemClones = new HashMap<>();

        public BlockData(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper)
        {
            super(generatorIn, Wyrmroost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags()
        {
            for (Block block : ModUtils.getRegistryEntries(WRBlocks.REGISTRY))
            {
                if (block instanceof SnowyDirtBlock)
                {
                    tag(BlockTags.DIRT).add(block);

                    if (block instanceof GrassBlock)
                    {
                        tag(BlockTags.ENDERMAN_HOLDABLE).add(block);
                        tag(BlockTags.BAMBOO_PLANTABLE_ON).add(block);
                        tag(BlockTags.VALID_SPAWN).add(block);
                    }
                }
                else if (block instanceof LeavesBlock) cloneToItem(BlockTags.LEAVES, ItemTags.LEAVES).add(block);
                else if (block instanceof SaplingBlock) cloneToItem(BlockTags.SAPLINGS, ItemTags.SAPLINGS).add(block);
                else if (block instanceof VineBlock || block instanceof LadderBlock)
                    tag(BlockTags.CLIMBABLE).add(block);
                else if (block instanceof TallFlowerBlock)
                    cloneToItem(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS).add(block);
                else if (block instanceof CropBlock)
                    tag(BlockTags.CROPS).add(block);
                else if (block instanceof CoralPlantBlock)
                    tag(BlockTags.CORAL_PLANTS).add(block);
            }

            //tag(Tags.Blocks.STONE).add(WRBlocks.ASH_STONE.get(), WRBlocks.CHISELED_ASH_STONE.get(), WRBlocks.CUT_ASH_STONE.get(),
            //        WRBlocks.FORAH_STONE.getStone());

            //cloneToItem(BlockTags.SAND, ItemTags.SAND).add(WRBlocks.ASH_BLOCK.get(), WRBlocks.FINE_ASH.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).addTags(WRBlocks.Tags.ORES_GEODE, WRBlocks.Tags.ORES_PLATINUM);

            tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(WRBlocks.PURPLE_GEODE_ORE.get());
            tag(BlockTags.NEEDS_DIAMOND_TOOL).add(WRBlocks.RED_GEODE_ORE.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(WRBlocks.BLUE_GEODE_ORE.get());

            tag(BlockTags.DRAGON_IMMUNE).add(WRBlocks.PURPLE_GEODE_ORE.get());
            //cloneToItem(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS).add(WRBlocks.CREVASSE_COTTON.get());

            ore(WRBlocks.Tags.ORES_GEODE, WRBlocks.BLUE_GEODE_ORE.get(), WRBlocks.RED_GEODE_ORE.get(), WRBlocks.PURPLE_GEODE_ORE.get());
            ore(WRBlocks.Tags.ORES_PLATINUM, WRBlocks.PLATINUM_ORE.get());

            storageBlocks(WRBlocks.Tags.STORAGE_BLOCKS_GEODE, WRBlocks.BLUE_GEODE_BLOCK.get(), WRBlocks.RED_GEODE_BLOCK.get(), WRBlocks.PURPLE_GEODE_BLOCK.get());
            storageBlocks(WRBlocks.Tags.STORAGE_BLOCKS_PLATINUM, WRBlocks.PLATINUM_BLOCK.get());

            /*woodGroup(WRBlocks.OSERI_WOOD, WRBlocks.Tags.OSERI_LOGS, true);
            woodGroup(WRBlocks.SAL_WOOD, WRBlocks.Tags.SAL_LOGS, true);
            woodGroup(WRBlocks.PRISMARINE_CORIN_WOOD, WRBlocks.Tags.PRISMARINE_CORIN_LOGS, false);
            woodGroup(WRBlocks.SILVER_CORIN_WOOD, WRBlocks.Tags.SILVER_CORIN_LOGS, false);
            woodGroup(WRBlocks.TEAL_CORIN_WOOD, WRBlocks.Tags.TEAL_CORIN_LOGS, false);
            woodGroup(WRBlocks.RED_CORIN_WOOD, WRBlocks.Tags.RED_CORIN_LOGS, false);
            woodGroup(WRBlocks.DYING_CORIN_WOOD, WRBlocks.Tags.DYING_CORIN_LOGS, false);

            for (StoneGroup s : StoneGroup.registry()) if (s.wall != null) tag(BlockTags.WALLS).add(s.getWall());*/
        }

        private void ore(TagKey<Block> oreTag, Block... ores)
        {
            tag(oreTag).add(ores);
            cloneToItem(Tags.Blocks.ORES, Tags.Items.ORES).addTag(oreTag);
        }

        private void storageBlocks(TagKey<Block> tag, Block... blocks)
        {
            tag(tag).add(blocks);
            cloneToItem(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS).addTag(tag);
            tag(BlockTags.BEACON_BASE_BLOCKS).addTag(tag);
        }

        /*private void woodGroup(WoodGroup group, ITag.INamedTag<Block> logTag, boolean flammable)
        {
            tag(logTag).add(group.getLog(), group.getStrippedLog(), group.getWood(), group.getStrippedWood());

            cloneToItem(BlockTags.PLANKS, ItemTags.PLANKS).add(group.getPlanks());
            cloneToItem(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS).add(group.getButton());
            cloneToItem(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS).add(group.getDoor());
            cloneToItem(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS).add(group.getStairs());
            cloneToItem(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS).add(group.getSlab());
            cloneToItem(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES).add(group.getFence());
            cloneToItem(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES).add(group.getPressurePlate());
            cloneToItem(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS).add(group.getTrapDoor());
            tag(BlockTags.FENCE_GATES).add(group.getFenceGate());
            tag(BlockTags.STANDING_SIGNS).add(group.getSign());
            tag(BlockTags.WALL_SIGNS).add(group.getWallSign());

            if (flammable) tag(BlockTags.LOGS_THAT_BURN).addTag(logTag);
            else
            {
                tag(BlockTags.NON_FLAMMABLE_WOOD).addTag(logTag)
                        .add(group.getPlanks(),
                                group.getButton(),
                                group.getDoor(),
                                group.getStairs(),
                                group.getSlab(),
                                group.getFence(),
                                group.getFenceGate(),
                                group.getPressurePlate(),
                                group.getTrapDoor(),
                                group.getSign(),
                                group.getWallSign(),
                                group.getBookshelf(),
                                group.getLadder());
            }
        }*/

        /**
         * For use with vanilla tags for ease of generating
         */
        private TagAppender cloneToItem(TagKey<Block> blockTag, TagKey<Item> itemTag)
        {
            itemClones.put(blockTag, itemTag);
            return tag(blockTag);
        }
    }

    private static class EntityData extends EntityTypeTagsProvider
    {
        private EntityData(DataGenerator generatorIn, ExistingFileHelper helper)
        {
            super(generatorIn, Wyrmroost.MOD_ID, helper);
        }

        @Override
        protected void addTags()
        {
            bindSoulBearers();

            tag(EntityTypeTags.ARROWS).add(GEODE_TIPPED_ARROW.get());
        }

        private void bindSoulBearers()
        {
            TagsProvider.TagAppender builder = tag(WREntities.Tags.SOUL_BEARERS);

            // wyrmroost
            builder.add(OVERWORLD_DRAKE.get(),
                    SILVER_GLIDER.get(),
                    ROOSTSTALKER.get(),
                    //BUTTERFLY_LEVIATHAN.get(),
                    //DRAGON_FRUIT_DRAKE.get(),
                    CANARI_WYVERN.get(),
                    ROYAL_RED.get());
                    //ALPINE.get());

            // dragonmounts
            for (String s : new String[]{"aether", "ender", "fire", "forest", "ghost", "ice", "nether", "water"})
                builder.addOptional(new ResourceLocation("dragonmounts", s + "_dragon"));

            // ice and fire
            for (String s : new String[]{"fire", "ice", "lightning"})
                builder.addOptional(new ResourceLocation("iceandfire", s + "_dragon"));
        }
    }

    /*private static class FluidData extends FluidTagsProvider
    {
        public FluidData(DataGenerator gen, ExistingFileHelper fileHelper)
        {
            super(gen, Wyrmroost.MOD_ID, fileHelper);
        }

        @Override
        protected void addTags()
        {
            sourced(FluidTags.WATER, WRFluids.BRINE.get());
        }

        public void sourced(ITag.INamedTag<Fluid> tag, FlowingFluid... fluids)
        {
            Builder<Fluid> builder = tag(tag);
            for (FlowingFluid fluid : fluids)
            {
                builder.add(fluid, fluid.getFlowing());
            }
        }
    }*/
}
