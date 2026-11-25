package com.java.shannieann.wyrmroost.block.plant.ground;

import net.minecraft.util.StringRepresentable;


public enum MatShape implements StringRepresentable {
    CENTRE("centre"),
    EDGE("edge"),
    CORNER("corner");

    private final String name;

    MatShape(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
