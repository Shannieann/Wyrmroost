package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.blocks.tile.WRSignBlockEntity;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class WRBlockEntities
{
    public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Wyrmroost.MOD_ID);
    public static final Map<RegistryObject<TileEntityType<?>>, Supplier<Function<TileEntityRendererDispatcher, TileEntityRenderer<? super TileEntity>>>> RENDERERS = new HashMap<>();

    public static final RegistryObject<TileEntityType<?>> CUSTOM_SIGN = register("sign", WRSignBlockEntity::new, () -> SignTileEntityRenderer::new, () -> setOf(WRBlocks.OSERI_WOOD.getSign(), WRBlocks.OSERI_WOOD.getWallSign()));

    public static RegistryObject<TileEntityType<?>> register(String name, Supplier<? extends TileEntity> factory, Supplier<Set<Block>> blocks)
    {
        return register(name, factory, null, blocks);
    }

    public static RegistryObject<TileEntityType<?>> register(String name, Supplier<? extends TileEntity> factory, @Nullable Supplier<Function<TileEntityRendererDispatcher, TileEntityRenderer<?>>> renderer, Supplier<Set<Block>> blocks)
    {
        RegistryObject<TileEntityType<?>> delegate = REGISTRY.register(name, () -> new TileEntityType<>(factory, blocks.get(), null));
        if (renderer != null) RENDERERS.put(delegate, ModUtils.cast(renderer));
        return delegate;
    }

    private static Set<Block> setOf(Block... blocks)
    {
        return ImmutableSet.copyOf(blocks);
    }
}