package com.github.shannieann.wyrmroost.client.render;

import com.github.shannieann.wyrmroost.WRConfig;
import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.book.TarragonTomeItem;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

import java.util.OptionalDouble;

public class RenderHelper extends RenderType
{
    // == [Render Types] ==

    private static final RenderType TRANSPARENT = create("transparent_color", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState
            .builder()
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));

    public static final RenderType CUTOUT_TRANSLUSCENT = create("cutout_transluscent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState
            .builder()
            .setShaderState(ShaderStateShard.RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER)
            .setLightmapState(LIGHTMAP)
            .setTextureState(TextureStateShard.NO_TEXTURE)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
            .createCompositeState(true));

    @SuppressWarnings("ConstantConditions")
    private RenderHelper()
    {
        super(null, null, null, 0, false, false, null, null); // dummy
    }

    public static RenderType getAdditiveGlow(ResourceLocation locationIn)
    {
        return create("glow_additive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                .setTextureState(new TextureStateShard(locationIn, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .createCompositeState(false));
    }

    public static RenderType getTranslucentGlow(ResourceLocation texture)
    {
        return create("glow_transluscent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder()
                .setTextureState(new TextureStateShard(texture, false, false))
                .setCullState(NO_CULL)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false));
    }

    public static RenderType getThiccLines(double thickness)
    {
        return create("thickened_lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, CompositeState.builder()
                .setLineState(new LineStateShard(OptionalDouble.of(thickness)))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false));
    }

    // == [Rendering] ==

    private static final ResourceLocation GUI_ICONS = Wyrmroost.id("textures/gui/overlay/icons.png");

    public static void renderWorld(RenderLevelLastEvent evt)
    {
        PoseStack ms = evt.getPoseStack();
        float partialTicks = evt.getPartialTick();

        ms.pushPose();

        //if (WRConfig.DEBUG_MODE.get()) DebugRendering.render(ms, partialTicks);
        renderBook(ms, partialTicks);

        ms.popPose();
    }

    public static void renderOverlay(RenderGameOverlayEvent evt)
    {
        if (evt.getType() == RenderGameOverlayEvent.ElementType.CHAT)
        {
            Entity vehicle = ClientEvents.getPlayer().getVehicle();
            if (vehicle instanceof WRDragonEntity && ((WRDragonEntity) vehicle).isUsingFlyingNavigator())
            {
                ClientEvents.getClient().textureManager.bindForSetup(GUI_ICONS);
                int y = ClientEvents.getClient().getWindow().getScreenHeight() / 2 - 24;
                int yOff = ClientEvents.keybindFlight? 24 : 0;
                GuiComponent.blit(evt.getMatrixStack(), 0, y, 0, yOff, 24, 24, 64, 64);
            }
        }
    }

    public static void drawShape(PoseStack ms, VertexConsumer buffer, VoxelShape shape, double x, double y, double z, int argb)
    {
        Matrix4f matrix = ms.last().pose();
        float alpha = ((argb >> 24) & 0xFF) / 255f;
        float red = ((argb >> 16) & 0xFF) / 255f;
        float green = ((argb >> 8) & 0xFF) / 255f;
        float blue = (argb & 0xFF) / 255f;
        shape.forAllEdges((x1, y1, z1, x2, y2, z2) ->
        {
            buffer.vertex(matrix, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z)).color(red, green, blue, alpha).endVertex();
            buffer.vertex(matrix, (float) (x2 + x), (float) (y2 + y), (float) (z2 + z)).color(red, green, blue, alpha).endVertex();
        });
    }

    public static void drawShape(PoseStack ms, VoxelShape shape, double x, double y, double z, int argb)
    {
        MultiBufferSource.BufferSource impl = getRenderBuffer();
        Vec3 view = ClientEvents.getProjectedView();
        float viewX = (float) (x - view.x);
        float viewY = (float) (y - view.y);
        float viewZ = (float) (z - view.z);

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> counterClockwiseCuboid(ms.last().pose(),
                impl.getBuffer(TRANSPARENT),
                (float) (x1 + viewX),
                (float) (y1 + viewY),
                (float) (z1 + viewZ),
                (float) (x2 + viewX),
                (float) (y2 + viewY),
                (float) (z2 + viewZ),
                ((argb >> 24) & 0xFF) / 255f,
                ((argb >> 8) & 0xFF) / 255f,
                (argb & 0xFF) / 255f,
                ((argb >> 24) & 0xFF) / 255f));
        drawShape(ms, impl.getBuffer(lines()), shape, viewX, viewY, viewZ, 0xFF000000);

        impl.endBatch();
    }

    public static void drawBlockPos(PoseStack ms, BlockPos pos, double lineThickness, int argb, boolean getShape)
    {
        MultiBufferSource.BufferSource impl = getRenderBuffer();
        Vec3 view = ClientEvents.getProjectedView();
        ClientLevel level = ClientEvents.getLevel();
        drawShape(ms,
                impl.getBuffer(getThiccLines(lineThickness)),
                getShape? level.getBlockState(pos).getShape(level, pos) : Shapes.block(),
                pos.getX() - view.x, pos.getY() - view.y, pos.getZ() - view.z,
                argb);
        ms.pushPose();
        impl.endLastBatch();
        //impl.endBatch();
    }

    public static void counterClockwiseCuboid(Matrix4f matrix, VertexConsumer buffer, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, float red, float green, float blue, float alpha)
    {
        buffer.vertex(matrix, fromX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, fromY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, fromZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, fromX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, toZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, toX, toY, fromZ).color(red, green, blue, alpha).endVertex();
    }

    private static final Object2IntMap<Entity> ENTITY_OUTLINE_MAP = new Object2IntOpenHashMap<>(1);

    public static void renderEntityOutline(Entity entity, int red, int green, int blue, int alpha)
    {
        ENTITY_OUTLINE_MAP.put(entity, ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF)));
    }

    public static MultiBufferSource.BufferSource getRenderBuffer()
    {
        return ClientEvents.getClient().renderBuffers().bufferSource();
    }

    private static final Matrix4f flipX = Matrix4f.createScaleMatrix(-1, 1, 1);
    private static final Matrix3f flipXNormal = new Matrix3f(flipX);

    public static void mirrorX(PoseStack matrixStack)
    {
        matrixStack.last().pose().multiply(flipX);
        matrixStack.last().normal().multiplyBackward(flipXNormal);
        matrixStack.last().normal().mul(flipXNormal);
    }

    // todo: find a better, shaders friendly way to do this
    public static void renderEntities(RenderLivingEvent.Pre<? super LivingEntity, ?> event)
    {
        LivingEntity entity = event.getEntity();
        PoseStack ms = event.getPoseStack();
        LivingEntityRenderer<? super LivingEntity, ?> renderer = event.getRenderer();
        float partialTicks = event.getPartialTick();

        int color = ENTITY_OUTLINE_MAP.removeInt(entity);
        if (color != 0)
        {
            event.setCanceled(true);

            Minecraft mc = ClientEvents.getClient();
            OutlineBufferSource buffer = mc.renderBuffers().outlineBufferSource();
            float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());

            buffer.setColor((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF);
            renderer.render(entity, yaw, partialTicks, ms, buffer, 15728640);
            buffer.endOutlineBatch();
        }
    }

    private static void renderBook(PoseStack ms, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ItemStack stack = WRModUtils.getHeldStack(player, WRItems.TARRAGON_TOME.get());
        if (stack == null) return;
        WRDragonEntity dragon = TarragonTomeItem.getBoundDragon(mc.level, stack);
        TarragonTomeItem.getAction(stack).render(dragon, ms, partialTicks);
        if (dragon == null) return;

        if (WRConfig.RENDER_OUTLINES.get())
        {
            renderEntityOutline(dragon, 0, 255, 255, (int) (Mth.cos((dragon.tickCount + partialTicks) * 0.2f) * 35 + 45));
            LivingEntity target = dragon.getTarget();
            if (target != null) renderEntityOutline(target, 255, 0, 0, 100);
        }
        BlockPos pos = dragon.getHomePos();
        if (pos != null)
            RenderHelper.drawBlockPos(ms, pos, 4, 0xff0000ff, false);
    }
}

