package com.java.shannieann.wyrmroost.block.plant.ground;

import net.minecraft.util.StringRepresentable;


public enum TriplePart implements StringRepresentable {
    LOWER("lower"),
    MIDDLE("middle"),
    UPPER("upper");

    private final String name;

    TriplePart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
