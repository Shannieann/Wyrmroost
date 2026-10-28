package com.java.shannieann.wyrmroost.world.feature;

import java.lang.Math;


public final class TreeRegions {

    // =============
    //  PARAMETERS
    // =============

    //LAYOUT
    private static final int REGION_CHUNKS = 3;
    private static final int GRID_SIZE = 3;

    //TREES
    public static final int SAL_BUSH = 0;
    public static final int KALIBURN = 1;
    public static final int DRAGONWOOD = 2;
    public static final int DRAGONSEYE = 3;
    public static final int CANAR = 4;
    public static final int SUREN = 5;
    public static final int HYLAN_SEDGE = 6;
    public static final int SHIMARIN = 7;
    public static final int BRUME_PINE = 8;

    //TODO: Remove once the trees have their own biomes
    public static boolean isRegionFor(int chunkX, int chunkZ, int tree) {
        int regionX = Math.floorMod(Math.floorDiv(chunkX, REGION_CHUNKS), GRID_SIZE);
        int regionZ = Math.floorMod(Math.floorDiv(chunkZ, REGION_CHUNKS), GRID_SIZE);
        return regionX + regionZ * GRID_SIZE == tree;
    }

    private TreeRegions() {}
}
