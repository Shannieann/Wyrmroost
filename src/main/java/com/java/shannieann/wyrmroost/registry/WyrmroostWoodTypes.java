package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class WyrmroostWoodTypes {
    public static final BlockSetType SAL_BUSH_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":sal_bush"));
    public static final WoodType SAL_BUSH = WoodType.register(new WoodType(Wyrmroost.MODID + "_sal_bush", SAL_BUSH_SET));
    public static final BlockSetType KALIBURN_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":kaliburn"));
    public static final WoodType KALIBURN = WoodType.register(new WoodType(Wyrmroost.MODID + "_kaliburn", KALIBURN_SET));
    public static final BlockSetType DRAGONSEYE_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":dragonseye"));
    public static final WoodType DRAGONSEYE = WoodType.register(new WoodType(Wyrmroost.MODID + "_dragonseye", DRAGONSEYE_SET));
    public static final BlockSetType DRAGONWOOD_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":dragonwood"));
    public static final WoodType DRAGONWOOD = WoodType.register(new WoodType(Wyrmroost.MODID + "_dragonwood", DRAGONWOOD_SET));
    public static final BlockSetType SHIMARIN_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":shimarin"));
    public static final WoodType SHIMARIN = WoodType.register(new WoodType(Wyrmroost.MODID + "_shimarin", SHIMARIN_SET));
    public static final BlockSetType BRUME_PINE_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":brume_pine"));
    public static final WoodType BRUME_PINE = WoodType.register(new WoodType(Wyrmroost.MODID + "_brume_pine", BRUME_PINE_SET));
    public static final BlockSetType CANAR_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":canar"));
    public static final WoodType CANAR = WoodType.register(new WoodType(Wyrmroost.MODID + "_canar", CANAR_SET));
    public static final BlockSetType SUREN_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":suren"));
    public static final WoodType SUREN = WoodType.register(new WoodType(Wyrmroost.MODID + "_suren", SUREN_SET));
    public static final BlockSetType HYLAN_SEDGE_SET = BlockSetType.register(new BlockSetType(Wyrmroost.MODID + ":hylan_sedge"));
    public static final WoodType HYLAN_SEDGE = WoodType.register(new WoodType(Wyrmroost.MODID + "_hylan_sedge", HYLAN_SEDGE_SET));

    private WyrmroostWoodTypes() {}
}
