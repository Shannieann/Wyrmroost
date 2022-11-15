package com.github.wolfshotz.wyrmroost.registry;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.screen.DragonControlScreen;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class WRIO
{
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, Wyrmroost.MOD_ID);

    public static final RegistryObject<MenuType<BookContainer>> DRAGON_STAFF = register("dragon_staff", BookContainer::factory);

    public static <T extends Container> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> factory)
    {
        return REGISTRY.register(name, () -> IForgeContainerType.create(factory));
    }
    public static void screenSetup()
    {
        MenuScreens.register(DRAGON_STAFF.get(), DragonControlScreen::new);
    }
}*/