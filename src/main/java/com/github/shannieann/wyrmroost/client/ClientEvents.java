package com.github.shannieann.wyrmroost.client;

//import com.github.shannieann.wyrmroost.client.render.TarragonTomeRenderer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.render.RenderHelper;
import com.github.shannieann.wyrmroost.client.render.entity.DragonEggRenderer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.*;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.placeholder.OWDrakeRenderer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.placeholder.SilverGliderRenderer;
import com.github.shannieann.wyrmroost.client.render.entity.effect.RenderLightningSphere;
import com.github.shannieann.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.shannieann.wyrmroost.client.render.entity.projectile.GeodeTippedArrowRenderer;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.items.LazySpawnEggItem;
import com.github.shannieann.wyrmroost.registry.*;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import software.bernie.shadowed.eliotlash.mclib.utils.MathHelper;

import java.util.*;

/**
 * EventBus listeners on CLIENT distribution
 * Also a client helper class because yes.
 */
@SuppressWarnings("unused")
public class ClientEvents
{
    public static Set<UUID> dragonRiders = new HashSet<>();
    public static boolean keybindFlight = true;


    public static Vector3f left;

    public static void init()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(ClientEvents::clientSetup);
        modBus.addListener(ClientEvents::stitchTextures);
        modBus.addListener(ClientEvents::itemColors);
        modBus.addListener(ClientEvents::bakeParticles);
        modBus.addListener(ClientEvents::registerRenderers);
        //bus.addListener(ClientEvents::bakeModels);

        forgeBus.addListener(RenderHelper::renderWorld);
        forgeBus.addListener(RenderHelper::renderOverlay);
        forgeBus.addListener(RenderHelper::renderEntities);
        forgeBus.addListener(ClientEvents::cameraPerspective);
        forgeBus.addListener(ClientEvents::preLivingRender);
        //forgeBus.addListener(ClientEvents::postLivingRender);
        forgeBus.addListener(ClientEvents::onKeyInput);

        //WRDimensionRenderInfo.init();
    }

    // ====================
    //       Mod Bus
    // ====================

    private static void clientSetup(final FMLClientSetupEvent event)
    {
        WRKeybind.registerKeys();

        /*ThinLogBlock.setCutoutRendering(WRBlocks.DYING_CORIN_WOOD);
        ThinLogBlock.setCutoutRendering(WRBlocks.RED_CORIN_WOOD);
        ThinLogBlock.setCutoutRendering(WRBlocks.TEAL_CORIN_WOOD);
        ThinLogBlock.setCutoutRendering(WRBlocks.SILVER_CORIN_WOOD);
        ThinLogBlock.setCutoutRendering(WRBlocks.PRISMARINE_CORIN_WOOD);*/
        WRIO.screenSetup();
        event.enqueueWork(() ->
        {

            //WoodType.values().filter(w -> w.name().contains(Wyrmroost.MOD_ID)).forEach(Atlases::addWoodType);

            //for (TileEntityType<?> entry : ModUtils.getRegistryEntries(WRBlockEntities.REGISTRY))
                //if (entry instanceof WRBlockEntities<?>) ((WRBlockEntities<?>) entry).callBack();
        });
    }

    private static void bakeParticles(ParticleFactoryRegisterEvent event)
    {
        for (ParticleType<?> entry : ModUtils.getRegistryEntries(WRParticles.REGISTRY))
            if (entry instanceof WRParticles<?>) ((WRParticles<?>) entry).bake();
    }

    private static void stitchTextures(TextureStitchEvent.Pre evt)
    {
        if (evt.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS)
            evt.addSprite(BreathWeaponRenderer.BLUE_FIRE);
    }

    private static void itemColors(ColorHandlerEvent.Item evt)
    {
        ItemColors handler = evt.getItemColors();
        ItemColor eggFunc = (stack, tintIndex) -> ((LazySpawnEggItem<?>) stack.getItem()).getColor(tintIndex);
        for (LazySpawnEggItem<?> e : LazySpawnEggItem.SPAWN_EGGS) handler.register(eggFunc, e);

        handler.register((stack, index) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack), WRItems.LEATHER_DRAGON_ARMOR.get());
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(WREntityTypes.ROYAL_RED.get(), RenderRoyalRed::new);
        event.registerEntityRenderer(WREntityTypes.BUTTERFLY_LEVIATHAN.get(), RenderButterflyLeviathan::new);

        event.registerEntityRenderer(WREntityTypes.LESSER_DESERTWYRM.get(), RenderLesserDeserwyrm::new);
        event.registerEntityRenderer(WREntityTypes.ROOST_STALKER.get(), RenderRoostStalker::new);

        //event.registerEntityRenderer(WREntityTypes.ROOST_STALKER.get(), RoostStalkerRenderer2::new);
        //event.registerEntityRenderer(WREntityTypes.CANARI_WYVERN.get(), CanariWyvernRenderer::new);
        event.registerEntityRenderer(WREntityTypes.SILVER_GLIDER.get(), SilverGliderRenderer::new);
        event.registerEntityRenderer(WREntityTypes.OVERWORLD_DRAKE.get(), OWDrakeRenderer::new);


        event.registerEntityRenderer(WREntityTypes.SOUL_CRYSTAL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WREntityTypes.GEODE_TIPPED_ARROW.get(), GeodeTippedArrowRenderer::new);
        event.registerEntityRenderer(WREntityTypes.DRAGON_EGG.get(), DragonEggRenderer::new);
        event.registerEntityRenderer(WREntityTypes.FIRE_BREATH.get(), BreathWeaponRenderer::new);
        event.registerEntityRenderer(WREntityTypes.LIGHTNING_SPHERE.get(), RenderLightningSphere::new);
    }
    // =====================
    //      Forge Bus
    // =====================



    private static void cancelIfRidingDragon(RenderLivingEvent event){
        Entity entity = event.getEntity().getVehicle();
        if (entity instanceof WRDragonEntity dragon) {
            if (dragonRiders.contains(event.getEntity().getUUID())) event.setCanceled(true); // Don't render the real player if they're riding a dragon
            CameraType camera = getClient().options.getCameraType();
            if (getClient().player == event.getEntity() && camera == CameraType.FIRST_PERSON) event.setCanceled(true); // Don't render the "fake" player if the player is in 1st person
        }
    }
    private static void preLivingRender(RenderLivingEvent.Pre event){
        cancelIfRidingDragon(event);
    }




    // todo for some reason getting the position of the dragon causes jittering? My guess is positions aren't 100% synced from client to server, so this is the best solution i could think of
    // other than creating a data accessor in WRDragonEntity that holds the actual position... which may be worth doing for accuracy? Also not that resource-intensive. idk
    private static void cameraPerspective(EntityViewRenderEvent.CameraSetup event)
    {
        Minecraft mc = getClient();
        Entity entity = mc.player.getVehicle();
        if (!(entity instanceof WRDragonEntity dragon)) return;
        CameraType view = mc.options.getCameraType();
        // Third person camera views
        if (view != CameraType.FIRST_PERSON)
            dragon.setMountCameraAngles(view == CameraType.THIRD_PERSON_BACK, event);
        else{ // 1st person
            // Set camera rotations based on bone values set in dragon renderer
            UUID uuid = mc.player.getUUID();
            float xRot = -dragon.cameraRotVector.x();
            float yRot = dragon.cameraRotVector.y();
            float zRot = dragon.cameraRotVector.z();
            Vector3d bonePos = dragon.cameraBonePos.get(uuid);
            Vec3 dragonPos = dragon.position();
            if (bonePos != null) {
                Vec3 vecBonePos = new Vec3(bonePos.x, bonePos.y-5.7F, bonePos.z);
                Vec3 vecBonePosMoving = new Vec3(bonePos.x, bonePos.y, bonePos.z); // todo remove this negative y offset and replace it with a better solution to move the camera. Maybe the above solution?
                // Set camera position

                //Sniffity: Previous method was forcing the camera position to update to the Dragon's position.
                //This essentially caused the camera to no longer transition smoothly from position A to B if the dragon was moving
                //It would instead jump from position A to B..
                //Getting the camera position and adding an offset on top of that is a safer way of doing things.
                //On vecBonePos, we adjust the Y offset, which will always compensate for the camera being too high up...
                //Last thing to do is properly moving the camera slightly backwards alongside the player-aligned x-axis
                //This prevents the camera from being too far "ahead" of the dragon...
                Vec3 cameraPos = event.getCamera().getPosition();
                event.getCamera().setPosition(cameraPos.add(vecBonePos)); // Not using move() here because it does weird stuff when you look around... (center of rotation messed up)
                event.getCamera().move(-calcCameraDistance(1.0, dragon), 0, 0);

            }

            // Allows for complete alteration of camera rotations for some sick clips
            event.setPitch(xRot + event.getPitch());
            event.setYaw(yRot + event.getYaw());
            event.setRoll(zRot + event.getRoll());
        }
    }

    public static double calcCameraDistance(double startingDistance, Entity vehicle) {
        Camera info = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 position = info.getPosition().add(0, 1, 0);
        Vector3f view = info.getLookVector();

        for (int i = 0; i < 8; ++i) {
            float f = (float) ((i & 1) * 2 - 1);
            float f1 = (float) ((i >> 1 & 1) * 2 - 1);
            float f2 = (float) ((i >> 2 & 1) * 2 - 1);
            f = f * 0.1F;
            f1 = f1 * 0.1F;
            f2 = f2 * 0.1F;
            Vec3 vector3d = position.add((double) f, (double) f1, (double) f2);
            Vec3 vector3d1 = new Vec3(position.x - (double) view.x() * startingDistance + (double) f + (double) f2, position.y - (double) view.y() * startingDistance + (double) f1, position.z - (double) view.z() * startingDistance + (double) f2);
            HitResult raytraceresult = vehicle.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, vehicle));
            if (raytraceresult.getType() != HitResult.Type.MISS) {
                double d0 = raytraceresult.getLocation().distanceTo(position);
                if (d0 < startingDistance) {
                    //Note: These values, which prevent camera clipping, may need some tweaks.
                    if (d0<0.2){
                        startingDistance = d0-3.0F;
                    } else {
                        startingDistance = d0-1.5F;
                    }
                }
            }
        }
        return startingDistance;
    }


    // =====================

    // for class loading issues
    public static Minecraft getClient()
    {
        return Minecraft.getInstance();
    }

    public static ClientLevel getLevel()
    {
        return getClient().level;
    }

    public static Player getPlayer()
    {
        return getClient().player;
    }

    public static Vec3 getProjectedView()
    {
        return getClient().gameRenderer.getMainCamera().getPosition();
    }

    public static float getPartialTicks()
    {
        return getClient().getFrameTime();
    }


    public static double getViewCollision(double wanted, Entity entity)
    {

        Camera info = getClient().gameRenderer.getMainCamera();
        Vec3 position = info.getPosition();
        Vector3f forwards = info.getLookVector();
        for (int i = 0; i < 8; ++i)
        {
            float f = (float) ((i & 1) * 2 - 1);
            float f1 = (float) ((i >> 1 & 1) * 2 - 1);
            float f2 = (float) ((i >> 2 & 1) * 2 - 1);
            f = f * 0.1F;
            f1 = f1 * 0.1F;
            f2 = f2 * 0.1F;
            Vec3 vector3d = position.add(f, f1, f2);
            Vec3 vector3d1 = new Vec3(position.x - forwards.x() * wanted + f + f2, position.y - forwards.y() * wanted + f1, position.z - forwards.z() * wanted + f2);
            HitResult rtr = entity.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (rtr.getType() != HitResult.Type.MISS)
            {
                double distance = rtr.getLocation().distanceTo(position);
                if (distance < wanted) wanted = distance;
            }
        }

        return wanted;
    }

    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        Minecraft game = Minecraft.getInstance();
        if (game.player != null) {
                if (ClientEvents.KEY_TEST.isDown()) {
                    Wyrmroost.NETWORK.sendToServer(new PacketKey());
                }
        }
    }

    public static final KeyMapping KEY_TEST = new KeyMapping("key.test",  GLFW.GLFW_KEY_J, "key.wyrmroost.category");

}
