package com.github.shannieann.wyrmroost.events;

import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.LazySpawnEggItem;
import com.github.shannieann.wyrmroost.item.base.ArmorBase;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.github.shannieann.wyrmroost.world.WREntitySpawning;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Reflection is shit and we shouldn't use it
 * - Some communist coding wyrmroost 2020
 * <p>
 * Manually add listeners
 */
//ToDo: Correct class, organize
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
        for (EntityType<?> entry : WRModUtils.getRegistryEntries(WREntityTypes.REGISTRY)) {
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


    // =====================
    //      Forge Bus
    // =====================

    public static void debugStick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand(event.getHand());

        if (stack.getItem() != Items.STICK || !stack.getHoverName().getString().equals("Debug Stick")) {
            return;
        }

        // Clip entities in range and get the hit result
        EntityHitResult entityHitResult = WRMathsUtility.clipEntities(player, 50, 1, null);
        if (entityHitResult == null) {
            return;
        }

        // Cancel the event and mark as successful interaction
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        Entity entity = entityHitResult.getEntity();
        entity.refreshDimensions();

        // Handle dragon-specific logic
        if (entity instanceof WRDragonEntity dragon) {
            if (dragon instanceof EntityButterflyLeviathan) {

            }

            // Future dragon-related actions can go here
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
}