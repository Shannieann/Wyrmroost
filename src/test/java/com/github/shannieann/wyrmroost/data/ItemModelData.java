package com.github.shannieann.wyrmroost.data;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
class ItemModelData extends ItemModelProvider
{
    private final List<Item> REGISTERED = new ArrayList<>();

    ItemModelData(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, Wyrmroost.MOD_ID, fileHelper);
    }

    void manualOverrides()
    {
        final ModelFile itemGenerated = uncheckedModel(mcLoc("item/generated"));
        final ModelFile ister = uncheckedModel("builtin/entity");

        REGISTERED.add(WRItems.TARRAGON_TOME.get().asItem());
        getBuilder("desert_wyrm_alive").parent(itemGenerated).texture("layer0", resource("desert_wyrm_alive"));
        //item(WRItems.LDWYRM.get()).override().predicate(Wyrmroost.id("is_alive"), 1f).model(uncheckedModel(resource("desert_wyrm_alive")));

        //getBuilderFor(WRItems.TARRAGON_TOME.get()).parent(ister).guiLight(BlockModel.GuiLight.FRONT);
        //item(WRBlocks.HOARFROST.get(), modLoc("block/hoarfrost_spines"));
        //final ItemModelBuilder cdBuilder = item(WRItems.COIN_DRAGON.get());
        for (int i = 1; i < 5; i++)
        {
            String path = "coin_dragon" + i;
            ResourceLocation rl = resource(path);
            getBuilder(path)
                    .parent(itemGenerated)
                    .texture("layer0", rl);
            /*cdBuilder.override()
                    .predicate(CoinDragonItem.VARIANT_OVERRIDE, i)
                    .model(uncheckedModel(rl));*/
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void registerModels()
    {
        manualOverrides();

        /*for (WoodGroup w : WoodGroup.registry())
        {
            ResourceLocation texture = fromBlockTexture(w.getPlanks());
            customInventoryItem(w.getFence(), texture, "fence");
            customInventoryItem(w.getButton(), texture, "button");
        }

        for (StoneGroup s : StoneGroup.registry())
        {
            ResourceLocation texture = fromBlockTexture(s.getStone());
            if (s.wall != null) customInventoryItem(s.getWall(), "wall", texture, "wall");
            if (s.button != null) customInventoryItem(s.getButton(), texture, "button");
        }*/

        final ModelFile spawnEggTemplate = uncheckedModel(mcLoc("item/template_spawn_egg"));
        final Set<Item> registered = WRModUtils.getRegistryEntries(WRItems.REGISTRY);

        REGISTERED.forEach(registered::remove);
        for (Item item : registered)
        {
            if (item instanceof SpawnEggItem) getBuilderFor(item).parent(spawnEggTemplate);
            else if (item instanceof BlockItem)
            {
                Block block = ((BlockItem) item).getBlock();
                ResourceLocation registry = item.getRegistryName();
                String path = registry.getPath();

                if (block instanceof TrapDoorBlock) path += "_bottom";
                if (block instanceof BushBlock || block instanceof CropBlock || block instanceof VineBlock || block instanceof LadderBlock || block instanceof CoralPlantBlock)
                {
                    item(item, Wyrmroost.id("block/" + block.getRegistryName().getPath()));
                    continue;
                }
                if (block instanceof DoorBlock || block instanceof StandingSignBlock)
                {
                    item(block);
                    continue;
                }
                if (block.defaultBlockState().hasProperty(BlockStateProperties.LAYERS))
                {
                    getBuilderFor(block).parent(uncheckedModel(fromBlockTexture(block) + "_height2"));
                    continue;
                }

                getBuilderFor(item).parent(uncheckedModel(registry.getNamespace() + ":block/" + path));
            }
            else item(item);
        }
    }

    @Override
    public String getName()
    {
        return "Wyrmroost Item Models";
    }

    private static ResourceLocation resource(String path)
    {
        return Wyrmroost.id("item/" + path);
    }

    private ItemModelBuilder item(ItemLike item)
    {
        return item(item, resource(item.asItem().getRegistryName().getPath()));
    }

    private ItemModelBuilder item(ItemLike item, ResourceLocation path)
    {
        ItemModelBuilder builder = getBuilderFor(item).parent(uncheckedModel(item instanceof TieredItem ? "item/handheld" : "item/generated"));

        // texture
        if (existingFileHelper.exists(path, PackType.CLIENT_RESOURCES, ".png", "textures"))
            builder.texture("layer0", path);
        else
            Wyrmroost.LOG.warn("Missing Texture for Item: {}, model will not be registered.", item.asItem().getRegistryName());

        return builder;
    }

    private ItemModelBuilder customInventoryItem(ItemLike item, ResourceLocation texture, String parent)
    {
        return customInventoryItem(item, "texture", texture, parent);
    }

    private ItemModelBuilder customInventoryItem(ItemLike item, String textureKey, ResourceLocation texture, String parent)
    {
        return getBuilderFor(item)
                .parent(uncheckedModel("block/" + parent + "_inventory"))
                .texture(textureKey, texture);
    }

    private ItemModelBuilder getBuilderFor(ItemLike item)
    {
        REGISTERED.add(item.asItem());
        return getBuilder(item.asItem().getRegistryName().getPath());
    }

    private ResourceLocation fromBlockTexture(Block block)
    {
        ResourceLocation reg = block.getRegistryName();
        return new ResourceLocation(reg.getNamespace() + ":block/" + reg.getPath());
    }

    private static ModelFile.UncheckedModelFile uncheckedModel(ResourceLocation path)
    {
        return new ModelFile.UncheckedModelFile(path);
    }

    private static ModelFile.UncheckedModelFile uncheckedModel(String path)
    {
        return new ModelFile.UncheckedModelFile(path);
    }
}
