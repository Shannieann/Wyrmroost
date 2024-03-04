package com.github.shannieann.wyrmroost.datagen;

import com.github.shannieann.wyrmroost.registry.WRBlocks;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class WRBlockLootTableProvider extends BlockLoot {

    @Override
    protected void addTables(){
        this.dropSelf(WRBlocks.BLUE_GEODE_BLOCK.get());
        this.dropSelf(WRBlocks.RED_GEODE_BLOCK.get());
        this.dropSelf(WRBlocks.PURPLE_GEODE_BLOCK.get());

        this.add(WRBlocks.PURPLE_GEODE_ORE.get(), (block) -> createOreDrop(WRBlocks.PURPLE_GEODE_ORE.get(), WRItems.PURPLE_GEODE.get()));
        this.add(WRBlocks.RED_GEODE_ORE.get(), (block) -> createOreDrop(WRBlocks.RED_GEODE_ORE.get(), WRItems.PURPLE_GEODE.get()));
        this.add(WRBlocks.BLUE_GEODE_ORE.get(), (block) -> createOreDrop(WRBlocks.BLUE_GEODE_ORE.get(), WRItems.PURPLE_GEODE.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks(){
        return WRBlocks.REGISTRY.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
