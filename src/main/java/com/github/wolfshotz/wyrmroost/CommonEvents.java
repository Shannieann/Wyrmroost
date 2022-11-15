package com.github.wolfshotz.wyrmroost;

import com.github.wolfshotz.wyrmroost.client.model.entity.RoostStalkerModel;
import com.github.wolfshotz.wyrmroost.client.render.entity.dragon.RoostStalkerRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.dragon.RoostStalkerRenderer2;
import com.github.wolfshotz.wyrmroost.items.LazySpawnEggItem;
import com.github.wolfshotz.wyrmroost.items.base.ArmorBase;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.registry.WRWorld;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.world.MobSpawnManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

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
        bus.addListener(CommonEvents::registerRenderers);
        //bus.addListener(WRConfig::loadConfig);
        //bus.addGenericListener(Item.class, CommonEvents::remap);

        //forgeBus.addListener(CommonEvents::debugStick);
        forgeBus.addListener(CommonEvents::onChangeEquipment);
        forgeBus.addListener(CommonEvents::loadLoot);
        //forgeBus.addListener(VillagerHelper::addWandererTrades);
        //forgeBus.addListener(CommonEvents::preCropGrowth);
        //forgeBus.addListener(EventPriority.HIGH, WRWorld::onBiomeLoad);
        forgeBus.addListener(((FMLServerAboutToStartEvent e) -> MobSpawnManager.close()));
    }

    // ====================
    //       Mod Bus
    // ====================

    public static void commonSetup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() ->
        {
            LazySpawnEggItem.addEggsToMap();

            //for (EntityType<?> entry : ModUtils.getRegistryEntries(WREntities.REGISTRY))
             //   if (entry instanceof WREntities) ((WREntities<?>) entry).callBack();

            //for (WRBlocks.BlockExtension extension : WRBlocks.EXTENSIONS) extension.callBack();
            //WRBlocks.EXTENSIONS.clear();
        });
    }

    @SuppressWarnings("unchecked")
    public static void bindEntityAttributes(EntityAttributeCreationEvent event) {
        for (EntityType<?> entry : ModUtils.getRegistryEntries(WREntities.REGISTRY)) {
            if (entry instanceof WREntities) {
                WREntities<LivingEntity> e = (WREntities<LivingEntity>) entry;
                if (e.attributes != null){
                    event.put(e, e.attributes.get());
                }
            }
        }
    }

    //public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
   //     event.registerLayerDefinition(RoostStalkerModel.LAYER_LOCATION, RoostStalkerModel::createBodyLayer);
   // }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(WREntities.ROOSTSTALKER.get(), RoostStalkerRenderer2::new);
    }

    /*@Deprecated  todo: remove in 1.17
   public static void remap(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings(Wyrmroost.MOD_ID)) {
            if (mapping.key.equals(Wyrmroost.id("dragon_staff"))) mapping.remap(WRItems.TARRAGON_TOME.get());
        }
    }*/

    // =====================
    //      Forge Bus
    // =====================

    /*public static void debugStick(PlayerInteractEvent.RightClickItem event)
    {
        if (!WRConfig.DEBUG_MODE.get()) return;
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (stack.getItem() != Items.STICK || !stack.getHoverName().getString().equals("Debug Stick"))
            return;
        EntityHitResult ertr = Mafs.clipEntities(event.getPlayer(), 50, 1, null);
        if (ertr != null)
        {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            Entity entity = ertr.getEntity();
            entity.refreshDimensions();

            if (!(entity instanceof TameableDragonEntity)) return;
            TameableDragonEntity dragon = (TameableDragonEntity) entity;

            if (player.isShiftKeyDown()) dragon.tame(true, player);
            else
            {
                if (dragon.level.isClientSide) /*DebugScreen.open(dragon); AnimateScreen.open(dragon);
            }
        }
    }*/

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
}

   /* public static void preCropGrowth(BlockEvent.CropGrowEvent.Pre event)
    {
        LevelAccessor level = event.getWorld();
        BlockPos pos = event.getPos();
        if (level.getBiomeName(pos).get() == WRWorld.FROST_CREVASSE && level.getBrightness(LightLayer.BLOCK, pos) <= 11)
            event.setResult(Event.Result.DENY);
    }
}*/
