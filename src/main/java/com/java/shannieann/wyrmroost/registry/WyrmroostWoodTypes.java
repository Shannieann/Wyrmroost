package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class WyrmroostWoodTypes {
    public static final BlockSetType SAL_BUSH_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":sal_bush"));
    public static final WoodType SAL_BUSH = WoodType.register(new WoodType(Wyrmroost.MODID + "_sal_bush", SAL_BUSH_SET));

    private WyrmroostWoodTypes() {}
}
