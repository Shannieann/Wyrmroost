package com.github.shannieann.wyrmroost;

import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.items.LazySpawnEggItem;
import com.github.shannieann.wyrmroost.items.base.ArmorBase;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.github.shannieann.wyrmroost.world.WREntitySpawning;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

/**
 * Reflection is shit and we shouldn't use it
 * - Some communist coding wyrmroost 2020
 * <p>
 * Manually add listeners
 */
public class CommonEvents {


    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(CommonEvents::commonSetup);
        bus.addListener(CommonEvents::bindEntityAttributes);
        //bus.addListener(CommonEvents::registerLayers);
        //bus.addListener(WRConfig::loadConfig);
        //bus.addGenericListener(Item.class, CommonEvents::remap);

        forgeBus.addListener(CommonEvents::debugStick);
        forgeBus.addListener(CommonEvents::onChangeEquipment);
        forgeBus.addListener(CommonEvents::loadLoot);
        forgeBus.addListener(CommonEvents::onBiomeLoading);
        forgeBus.addListener(CommonEvents::onEntityMountEvent);
        forgeBus.addListener(CommonEvents::onRenderWorldLast);
        forgeBus.addListener(CommonEvents::onPlayerTick);

        //forgeBus.addListener(VillagerHelper::addWandererTrades);
        //forgeBus.addListener(CommonEvents::preCropGrowth);
        //forgeBus.addListener(EventPriority.HIGH, WRWorld::onBiomeLoad);
        //forgeBus.addListener(((ServerAboutToStartEvent e) -> MobSpawnManager.close()));
    }

    // ====================
    //       Mod Bus
    // ====================

    public static void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() ->
        {
            LazySpawnEggItem.addEggsToMap();

            //for (EntityType<?> entry : ModUtils.getRegistryEntries(WREntityTypes.REGISTRY))
            //   if (entry instanceof WREntityTypes) ((WREntityTypes<?>) entry).callBack();

            //for (WRBlocks.BlockExtension extension : WRBlocks.EXTENSIONS) extension.callBack();
            //WRBlocks.EXTENSIONS.clear();
            WREntitySpawning.registerSpawnPlacementTypes();
        });
    }

    @SuppressWarnings("unchecked")
    public static void bindEntityAttributes(EntityAttributeCreationEvent event) {
        for (EntityType<?> entry : ModUtils.getRegistryEntries(WREntityTypes.REGISTRY)) {
            if (entry instanceof WREntityTypes) {
                WREntityTypes<LivingEntity> e = (WREntityTypes<LivingEntity>) entry;
                if (e.attributes != null) {
                    event.put(e, e.attributes.get());
                }
            }
        }
    }

    //public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
    //     event.registerLayerDefinition(RoostStalkerModel.LAYER_LOCATION, RoostStalkerModel::createBodyLayer);
    // }

    /*@Deprecated  todo: remove in 1.17
   public static void remap(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings(Wyrmroost.MOD_ID)) {
            if (mapping.key.equals(Wyrmroost.id("tarragon_tome"))) mapping.remap(WRItems.TARRAGON_TOME.get());
        }
    }*/

    // =====================
    //      Forge Bus
    // =====================

    public static void debugStick(PlayerInteractEvent.RightClickItem event) {
        if (!WRConfig.DEBUG_MODE.get()) return;
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (stack.getItem() != Items.STICK || !stack.getHoverName().getString().equals("Debug Stick"))
            return;
        EntityHitResult ertr = WRMathsUtility.clipEntities(event.getPlayer(), 50, 1, null);
        if (ertr != null) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            Entity entity = ertr.getEntity();
            entity.refreshDimensions();

            if (!(entity instanceof WRDragonEntity)) return;
            WRDragonEntity dragon = (WRDragonEntity) entity;

            /*
            if (player.isShiftKeyDown()) dragon.tame(true, player);
            else
            {
                if (dragon.level.isClientSide) DebugScreen.open(dragon); AnimateScreen.open(dragon);
            }
            
             */
        }
    }

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        WREntitySpawning.onBiomeLoading(event);
    }

    public static void onChangeEquipment(LivingEquipmentChangeEvent event) {
        ArmorBase initial;
        if (event.getTo().getItem() instanceof ArmorBase) initial = (ArmorBase) event.getTo().getItem();
        else if (event.getFrom().getItem() instanceof ArmorBase) initial = (ArmorBase) event.getFrom().getItem();
        else return;

        LivingEntity entity = event.getEntityLiving();
        initial.applyFullSetBonus(entity, ArmorBase.hasFullSet(entity));
    }

    public static void loadLoot(LootTableLoadEvent event) {
        if (event.getName().equals(BuiltInLootTables.ABANDONED_MINESHAFT))
            event.getTable().addPool(LootPool.lootPool()
                    .name("coin_dragon_inject")
                    //.add(CoinDragonItem.getLootEntry())
                    .build());
    }

    public static void onEntityMountEvent(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof WRDragonEntity) {
            if (event.isMounting() && event.getEntityBeingMounted() instanceof Minecart || event.getEntityBeingMounted() instanceof Boat) {
                event.setCanceled(true);
            }
        }
    }

    public static void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            Player player = (Player) event.getEntityLiving();
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof WRDragonEntity) {
                float vehicleYaw = ((WRDragonEntity)vehicle).getYRot();
                float yaw = player.yRot;
                float maxDifference = 1;
                if (yaw > vehicleYaw+maxDifference) {
                    yaw = vehicleYaw+maxDifference;
                } else if (yaw < vehicleYaw-maxDifference) {
                    yaw = vehicleYaw-maxDifference;
                }
                player.yRot = yaw;
            }
        }
    }


    public static void onRenderWorldLast(RenderLevelStageEvent event) {

        if (WRConfig.DEBUG_MODE.get()) {
            RenderLevelStageEvent.Stage stage = event.getStage();
            if (stage == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
                Minecraft mc = Minecraft.getInstance();
                Camera camera = mc.gameRenderer.getMainCamera();
                Vec3 viewPosition = camera.getPosition();
                PoseStack matrix_stack = event.getPoseStack();
                matrix_stack.pushPose();
                matrix_stack.translate(-viewPosition.x, -viewPosition.y, -viewPosition.z);
                List<EntityButterflyLeviathan> entityList = mc.level.getEntitiesOfClass(EntityButterflyLeviathan.class, new AABB(mc.player.getOnPos()).inflate(20));
                if (!entityList.isEmpty()) {
                    for (int i = 0; i<entityList.size(); i++) {
                        List<AABB> attackBoxes = entityList.get(i).generateAttackBoxes();
                        LevelRenderer.renderLineBox(matrix_stack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(0), 1,0,0,1);
                        LevelRenderer.renderLineBox(matrix_stack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(1), 0,1,0,1);
                        LevelRenderer.renderLineBox(matrix_stack, mc.renderBuffers().bufferSource().getBuffer(RenderType.lines()), attackBoxes.get(2), 0,0,1,1);
                    }
                    matrix_stack.popPose();
                    mc.renderBuffers().bufferSource().endBatch();
                }
            }
        }
    }
}