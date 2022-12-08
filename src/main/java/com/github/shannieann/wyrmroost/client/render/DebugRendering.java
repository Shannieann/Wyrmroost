package com.github.shannieann.wyrmroost.client.render;

/*import com.github.shannieann.wyrmroost.Wyrmroost;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DebugRendering
{
    public static Shape now;

    public static void render(PoseStack ms, float partialTicks)
    {
        if (now != null)
        {
            Vec3 pos = now.position;
            RenderHelper.drawShape(ms, now.shape, pos.x, pos.y, pos.z, now.color);
            if (--now.ticks <= 0) now = null;
        }
    }

    public static void shape(VoxelShape shape, Vec3 pos, int argb, int ticks)
    {
        Wyrmroost.LOG.info("Rendering at {} for {} ticks", pos, ticks);
        now = new Shape(shape, pos, argb, ticks);
    }

    public static void box(AABB aabb, int argb, int ticks)
    {
        shape(Shapes.create(aabb), Vec3.ZERO, argb, ticks);
    }

    public static void conjoined(int color, int ticks, BlockPos... cubes)
    {
        if (cubes.length == 0) return;

        VoxelShape shape = Shapes.block();
        BlockPos initial = cubes[0];
        for (int i = 1; i < cubes.length; ++i)
        {
            BlockPos cube = cubes[i];
            BlockPos offset = cube.subtract(initial);
            shape = Shapes.or(shape, Shapes.block().move(offset.getX(), offset.getY(), offset.getZ()));
        }
        shape(shape, Vec3.atLowerCornerOf(initial), color, ticks);
    }

    private static class Shape
    {
        final VoxelShape shape;
        final Vec3 position;
        final int color;
        int ticks;

        public Shape(VoxelShape shape, Vec3 position, int color, int ticks)
        {
            this.shape = shape;
            this.position = position;
            this.color = color;
            this.ticks = ticks;
        }
    }
}*/
