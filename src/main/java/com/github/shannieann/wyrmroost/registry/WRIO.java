package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.screen.NewTarragonTomeScreen;
import com.github.shannieann.wyrmroost.client.screen.RideableDragonInventoryScreen;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import com.github.shannieann.wyrmroost.containers.RideableDragonInventoryContainer;
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

    public static final RegistryObject<MenuType<NewTarragonTomeContainer>> TARRAGON_TOME = register("tarragon_tome", NewTarragonTomeContainer::factory);
    public static final RegistryObject<MenuType<RideableDragonInventoryContainer>> RIDEABLE_DRAGON_INVENTORY = register("rideable_dragon_inventory", RideableDragonInventoryContainer::fromNetwork);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory)
    {
        return REGISTRY.register(name, () -> IForgeMenuType.create(factory));
    }
    public static void screenSetup()
    {
        MenuScreens.register(TARRAGON_TOME.get(), NewTarragonTomeScreen::new);
        MenuScreens.register(RIDEABLE_DRAGON_INVENTORY.get(), RideableDragonInventoryScreen::new);
    }
}