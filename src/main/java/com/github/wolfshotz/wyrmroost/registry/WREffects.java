package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.projectile.DragonProjectileEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class WREffects extends MobEffect
{
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Wyrmroost.MOD_ID);

    public static final RegistryObject<MobEffect> SOUL_WEAKNESS = register("soul_weakness", () -> new WREffects(MobEffectCategory.HARMFUL, 0x007375));

    private static RegistryObject<MobEffect> register(String name, Supplier<MobEffect> sup)
    {
        return REGISTRY.register(name, sup);
    }

    // used for exposure because the constructor is protected
    public WREffects(MobEffectCategory category, int color)
    {
        super(category, color);
    }
}
