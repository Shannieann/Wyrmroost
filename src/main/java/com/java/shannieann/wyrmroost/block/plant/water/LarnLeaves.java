package com.java.shannieann.wyrmroost.block.plant.water;

import net.minecraft.util.StringRepresentable;


public enum LarnLeaves implements StringRepresentable {
    NONE("none"),
    SMALL("small"),
    LARGE("large");

    private final String name;

    LarnLeaves(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
