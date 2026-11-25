package com.java.shannieann.wyrmroost.block.plant.water;

import net.minecraft.util.StringRepresentable;


public enum LarnSegment implements StringRepresentable {
    STALK("stalk"),
    TRANSITION("transition"),
    BLEACHED("bleached");

    private final String name;

    LarnSegment(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
