package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.screen.NewTarragonTomeScreen;
import com.github.shannieann.wyrmroost.containers.NewTarragonTomeContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WRIO
{
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, Wyrmroost.MOD_ID);

    public static final RegistryObject<MenuType<NewTarragonTomeContainer>> TARRAGON_TOME = REGISTRY.register("tarragon_tome",
            () -> new MenuType<>(NewTarragonTomeContainer::new));

    public static void screenSetup()
    {
        MenuScreens.register(TARRAGON_TOME.get(), NewTarragonTomeScreen::new);
    }
}