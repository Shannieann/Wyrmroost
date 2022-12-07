package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WRIO
{
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, Wyrmroost.MOD_ID);

    public static final RegistryObject<MenuType<BookContainer>> TARRAGON_TOME = register("tarragon_tome", BookContainer::factory);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory)
    {
        return REGISTRY.register(name, () -> IForgeMenuType.create(factory));
    }
    public static void screenSetup()
    {
        MenuScreens.register(TARRAGON_TOME.get(), DragonControlScreen::new);
    }
}