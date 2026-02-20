package com.github.shannieann.wyrmroost.events;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.screen.RideableDragonInventoryScreen;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import com.github.shannieann.wyrmroost.network.DrakeJumpPacket;
import com.github.shannieann.wyrmroost.network.OpenRideableDragonInventoryPacket;
import com.github.shannieann.wyrmroost.network.PacketKey;
import com.github.shannieann.wyrmroost.client.render.RenderHelper;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.*;
import com.github.shannieann.wyrmroost.client.render.entity.dragon_egg.RenderDragonEgg;
import com.github.shannieann.wyrmroost.client.render.entity.effect.RenderLightningNova;
import com.github.shannieann.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.shannieann.wyrmroost.client.render.entity.projectile.GeodeTippedArrowRenderer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.WRLazySpawnEggItem;
import com.github.shannieann.wyrmroost.registry.*;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;

public class ClientEvents {
    public static Set<UUID> dragonRiders = new HashSet<>();
    public static boolean keybindFlight = true;

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(ClientEvents::clientSetup);
        modBus.addListener(ClientEvents::stitchTextures);
        modBus.addListener(ClientEvents::itemColors);
        modBus.addListener(ClientEvents::bakeParticles);
        modBus.addListener(ClientEvents::registerRenderers);

        forgeBus.addListener(RenderHelper::renderWorld);
        forgeBus.addListener(RenderHelper::renderOverlay);
        forgeBus.addListener(RenderHelper::renderEntities);
        forgeBus.addListener(ClientEvents::setupCameraPerspective);
        forgeBus.addListener(ClientEvents::preLivingRender);
        forgeBus.addListener(ClientEvents::onRenderWorldLast);
        forgeBus.addListener(ClientEvents::dragonRidingFOV);
        forgeBus.addListener(ClientEvents::onKeyInput);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        WRKeybind.registerKeys();
        WRIO.screenSetup();
    }

    private static void bakeParticles(ParticleFactoryRegisterEvent event) {
        for (ParticleType<?> entry : WRModUtils.getRegistryEntries(WRParticles.REGISTRY))
            if (entry instanceof WRParticles<?>) ((WRParticles<?>) entry).bake();
    }

    private static void stitchTextures(TextureStitchEvent.Pre evt) {
        if (evt.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS)
            evt.addSprite(BreathWeaponRenderer.BLUE_FIRE);
    }

    private static void itemColors(ColorHandlerEvent.Item evt) {
        ItemColors handler = evt.getItemColors();
        ItemColor eggFunc = (stack, tintIndex) -> ((WRLazySpawnEggItem<?>) stack.getItem()).getColor(tintIndex);
        for (WRLazySpawnEggItem<?> e : WRLazySpawnEggItem.SPAWN_EGGS) handler.register(eggFunc, e);

        handler.register((stack, index) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack), WRItems.LEATHER_DRAGON_ARMOR.get());
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(WREntityTypes.ALPINE_DRAGON.get(), RenderAlpineDragon::new);
        event.registerEntityRenderer(WREntityTypes.BUTTERFLY_LEVIATHAN.get(), RenderButterflyLeviathan::new);
        event.registerEntityRenderer(WREntityTypes.CANARI_WYVERN.get(), RenderCanariWyvern::new);
        event.registerEntityRenderer(WREntityTypes.COIN_DRAGON.get(), RenderCoinDragon::new);
        event.registerEntityRenderer(WREntityTypes.LESSER_DESERTWYRM.get(), RenderLesserDesertwyrm::new);
        event.registerEntityRenderer(WREntityTypes.OVERWORLD_DRAKE.get(), RenderOverworldDrake::new);
        event.registerEntityRenderer(WREntityTypes.ROOSTSTALKER.get(), RenderRooststalker::new);
        event.registerEntityRenderer(WREntityTypes.ROYAL_RED.get(), RenderRoyalRed::new);
        event.registerEntityRenderer(WREntityTypes.SILVER_GLIDER.get(), RenderSilverGlider::new);

        event.registerEntityRenderer(WREntityTypes.SOUL_CRYSTAL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WREntityTypes.GEODE_TIPPED_ARROW.get(), GeodeTippedArrowRenderer::new);
        event.registerEntityRenderer(WREntityTypes.DRAGON_EGG.get(), RenderDragonEgg::new);
        event.registerEntityRenderer(WREntityTypes.FIRE_BREATH.get(), BreathWeaponRenderer::new);
        event.registerEntityRenderer(WREntityTypes.LIGHTNING_NOVA.get(), RenderLightningNova::new);
    }

    private static void cancelIfRidingDragon(RenderLivingEvent event) {
        Entity entity = event.getEntity().getVehicle();
        if (entity instanceof WRDragonEntity) {
            if (dragonRiders.contains(event.getEntity().getUUID())) event.setCanceled(true); // Don't render the real player if they're riding a dragon
            CameraType camera = getClient().options.getCameraType();
            if (getClient().player == event.getEntity() && camera == CameraType.FIRST_PERSON) event.setCanceled(true); // Don't render the "fake" player if the player is in 1st person
        }
    }
    private static void preLivingRender(RenderLivingEvent.Pre event) {
        cancelIfRidingDragon(event);
    }


    private static void setupCameraPerspective(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = getClient();
        Entity entity = mc.player.getVehicle();

        // Return early if the entity is not a WRDragonEntity
        if (!(entity instanceof WRDragonEntity dragon)) return;

        CameraType view = mc.options.getCameraType();

        // Handle camera setup based on view type
        if (view != CameraType.FIRST_PERSON) {
            // Third person camera setup
            //These methods belong to the dragon class
            dragon.setupThirdPersonCamera(view == CameraType.THIRD_PERSON_BACK, event, mc.player);
        } else {
            // First person camera setup
            setupFirstPersonCamera(event, dragon, mc.player);
        }
    }

    private static void setupFirstPersonCamera(EntityViewRenderEvent.CameraSetup event, WRDragonEntity dragon, Player player) {
        UUID playerUUID = player.getUUID();

        // Get camera rotation from dragon's bone values
        float xRot = -dragon.cameraRotVector.x();
        float yRot = dragon.cameraRotVector.y();
        float zRot = dragon.cameraRotVector.z();

        // Get bone position for the player
        Vector3d bonePos = dragon.cameraBonePos.get(playerUUID);
        if (bonePos != null) {
            // Adjust bone position for the camera Y offset
            Vec3 adjustedBonePos = new Vec3(bonePos.x, bonePos.y + dragon.getMountCameraYOffset(), bonePos.z);

            // Set camera position based on adjusted bone position
            Vec3 cameraPos = event.getCamera().getPosition();
            event.getCamera().setPosition(cameraPos.add(adjustedBonePos)); // Avoid using move() for consistent behavior
        }

        // Set camera rotations based on dragon's bone rotation values
        event.setPitch(xRot + event.getPitch());
        event.setYaw(yRot + event.getYaw());
        event.setRoll(zRot + event.getRoll());
    }

    public static double performCollisionCalculations(double maximumInitialDistance, Entity entity, Player player) {
        Camera camera = getClient().gameRenderer.getMainCamera();
        Vector3f lookVector = camera.getLookVector();
        double cameraDistance = maximumInitialDistance;
        Vec3 startingCameraPosition = camera.getPosition().add(
                lookVector.x() * cameraDistance,
                lookVector.y() * cameraDistance,
                lookVector.z() * cameraDistance
        );

        // Array of vectors defining a cube for offset positions
        Vec3[] offsets = {
                new Vec3(-0.1F, -0.1F, -0.1F),
                new Vec3( 0.1F, -0.1F, -0.1F),
                new Vec3(-0.1F,  0.1F, -0.1F),
                new Vec3( 0.1F,  0.1F, -0.1F),
                new Vec3(-0.1F, -0.1F,  0.1F),
                new Vec3( 0.1F, -0.1F,  0.1F),
                new Vec3(-0.1F,  0.1F,  0.1F),
                new Vec3( 0.1F,  0.1F,  0.1F),
        };

        for (Vec3 offset : offsets) {
            // Offset the start position for each cube corner around the camera
            Vec3 startPoint = startingCameraPosition.add(offset);

            // Calculate the endPoint based on the view direction and maximum distance
            Vec3 endPoint = camera.getPosition().add(
                    lookVector.x() * -cameraDistance,
                    lookVector.y() * -cameraDistance,
                    lookVector.z() * -cameraDistance
            );;

            // Clip context from startPoint to endPoint
            HitResult rtr = entity.level.clip(new ClipContext(startPoint, endPoint, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));

            // Debug prints for startPoint, endPoint, and hit result
            if (rtr.getType() == HitResult.Type.MISS) {
            }
            if (rtr.getType() == HitResult.Type.BLOCK) {
            }
            if (rtr.getType() == HitResult.Type.ENTITY) {
            }


            if (rtr.getType() != HitResult.Type.MISS) {
                // Update cameraDistance if a closer collision is detected
                double collisionDistance=rtr.getLocation().distanceTo(startPoint);

                if (collisionDistance <-cameraDistance && collisionDistance <1.0) {
                    cameraDistance = -collisionDistance;
                } else if (collisionDistance <Math.abs(cameraDistance)) {
                    cameraDistance = -collisionDistance;
                }
            }
        }
        return cameraDistance;
    }
    
    // TODO maybe change FOV during flight, like if a dragon is diving for example?
    // Remove the sprint fov change when you're on a dragon, it doesn't do anything in the first place.
    // Also, this opens the door for us to change fov in certain circumstances (see above)
    // This would probably be a client config option, along with camera rotations.
    private static void dragonRidingFOV(EntityViewRenderEvent.FieldOfView event){
        LocalPlayer player = getClient().player;
        if (player == null || !(player.getVehicle() instanceof WRDragonEntity)) return;
        double fov = event.getFOV();
        event.setFOV(fov);
    }


    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static ClientLevel getLevel() {
        return getClient().level;
    }

    public static Player getPlayer() {
        return getClient().player;
    }

    public static Vec3 getProjectedView() {
        return getClient().gameRenderer.getMainCamera().getPosition();
    }

    public static float getPartialTicks() {
        return getClient().getFrameTime();
    }

    public static void onKeyInput(TickEvent.ClientTickEvent event) {
        Minecraft game = Minecraft.getInstance();
        if (game.player == null) return;
        if (ClientEvents.KEY_TEST.isDown()) {
            Wyrmroost.NETWORK.sendToServer(new PacketKey());
        }
        // Donkey-like: when riding tamed OWD, inventory key opens drake inventory instead of player inventory
        Entity vehicle = game.player.getVehicle();
        if (vehicle instanceof EntityOverworldDrake drake && drake.isTame() && game.options.keyInventory.consumeClick()) {
            OpenRideableDragonInventoryPacket.send();
        }
        // Donkey-like jump when riding OWD (when screen is not open - screen has its own jump handling)
        if (game.screen instanceof RideableDragonInventoryScreen) return;
        if (vehicle instanceof EntityOverworldDrake drake && drake.canJump()) {
            boolean jumpDown = game.options.keyJump.isDown();
            if (jumpDown) {
                if (!owdJumpKeyWasDown) owdJumpCharge = 0f;
                owdJumpKeyWasDown = true;
                owdJumpCharge = Math.min(owdJumpCharge + 0.05f, 1f);
            } else {
                if (owdJumpKeyWasDown && owdJumpCharge > 0f) {
                    DrakeJumpPacket.send((int) (owdJumpCharge * 100f));
                }
                owdJumpKeyWasDown = false;
                owdJumpCharge = 0f;
            }
        } else {
            owdJumpKeyWasDown = false;
            owdJumpCharge = 0f;
        }
    }

    private static float owdJumpCharge;
    private static boolean owdJumpKeyWasDown;

    public static void onRenderWorldLast(RenderLevelStageEvent event) {

        // Check if the current stage is AFTER_SOLID_BLOCKS
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 viewPosition = camera.getPosition();
        PoseStack matrixStack = event.getPoseStack();

        // Prepare the pose stack for rendering
        matrixStack.pushPose();
        matrixStack.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);

        // Retrieve nearby EntityButterflyLeviathan entities
        List<EntityButterflyLeviathan> entityList = mc.level.getEntitiesOfClass(EntityButterflyLeviathan.class, new AABB(mc.player.getOnPos()).inflate(20));

        // Render attack boxes for each butterfly leviathan entity
        if (!entityList.isEmpty()) {
            renderAttackBoxes(entityList, matrixStack, mc);
        }

        // Clean up the pose stack and buffer
        matrixStack.popPose();
        mc.renderBuffers().bufferSource().endBatch();
    }

    private static void renderAttackBoxes(List<EntityButterflyLeviathan> entityList, PoseStack matrixStack, Minecraft mc) {
        for (EntityButterflyLeviathan entity : entityList) {
            List<AABB> attackBoxes = entity.generateAttackBoxes();

            // Render each attack box with distinct colors
            LevelRenderer.renderLineBox(matrixStack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(0), 1, 0, 0, 1); // Red
            LevelRenderer.renderLineBox(matrixStack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(1), 0, 1, 0, 1); // Green
            LevelRenderer.renderLineBox(matrixStack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(2), 0, 0, 1, 1); // Blue
        }
    }
    public static final KeyMapping KEY_TEST = new KeyMapping("key.test",  GLFW.GLFW_KEY_J, "key.wyrmroost.category");
}
