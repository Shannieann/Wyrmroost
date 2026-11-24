package com.java.shannieann.wyrmroost.world.feature.tree;

import java.lang.Math;


public final class TreeRegions {

    // =============
    //  PARAMETERS
    // =============

    //LAYOUT
    private static final int REGION_CHUNKS = 3;
    private static final int GRID_WIDTH = 5;
    private static final int GRID_DEPTH = 2;

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
    public static final int OSERI = 9;

    //SHOWCASE
    private static final int ALL_TREES = -1;
    private static final int ONLY_TREE = OSERI;

    //TODO: Remove once the trees have their own biomes
    public static boolean isRegionFor(int chunkX, int chunkZ, int tree) {
        if (ONLY_TREE != ALL_TREES) {
            return tree == ONLY_TREE;
        }
        int regionX = Math.floorMod(Math.floorDiv(chunkX, REGION_CHUNKS), GRID_WIDTH);
        int regionZ = Math.floorMod(Math.floorDiv(chunkZ, REGION_CHUNKS), GRID_DEPTH);
        return regionX + regionZ * GRID_WIDTH == tree;
    }

    private TreeRegions() {}
}