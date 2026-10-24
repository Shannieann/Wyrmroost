package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import com.java.shannieann.wyrmroost.block.BlockSalBushSapling;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WyrmroostBlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Wyrmroost.MODID);

    // =============
    //  SAL BUSH WOOD
    // =============
    //TODO: Ladders

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        modEventBus.addListener(WyrmroostBlockRegistry::addBlockEntityBlocks);
        modEventBus.addListener(WyrmroostBlockRegistry::addFlammableBlocks);
    }

    private static final MapColor SAL_BUSH_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor SAL_BUSH_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> SAL_BUSH_LOG = BLOCKS.registerBlock("sal_bush_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SAL_BUSH_WOOD_COLOUR : SAL_BUSH_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> SAL_BUSH_WOOD = BLOCKS.registerBlock("sal_bush_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SAL_BUSH_BARK_COLOUR : SAL_BUSH_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SAL_BUSH_LOG = BLOCKS.registerBlock("stripped_sal_bush_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SAL_BUSH_WOOD_COLOUR : SAL_BUSH_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SAL_BUSH_WOOD = BLOCKS.registerBlock("stripped_sal_bush_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SAL_BUSH_WOOD_COLOUR : SAL_BUSH_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> SAL_BUSH_PLANKS = BLOCKS.registerBlock("sal_bush_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> SAL_BUSH_STAIRS = BLOCKS.registerBlock("sal_bush_stairs",
            properties -> new StairBlock(SAL_BUSH_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> SAL_BUSH_SLAB = BLOCKS.registerBlock("sal_bush_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> SAL_BUSH_FENCE = BLOCKS.registerBlock("sal_bush_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> SAL_BUSH_FENCE_GATE = BLOCKS.registerBlock("sal_bush_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.SAL_BUSH, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> SAL_BUSH_DOOR = BLOCKS.registerBlock("sal_bush_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.SAL_BUSH_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> SAL_BUSH_TRAPDOOR = BLOCKS.registerBlock("sal_bush_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.SAL_BUSH_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> SAL_BUSH_PRESSURE_PLATE = BLOCKS.registerBlock("sal_bush_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.SAL_BUSH_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> SAL_BUSH_BUTTON = BLOCKS.registerBlock("sal_bush_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.SAL_BUSH_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> SAL_BUSH_SHELF = BLOCKS.registerBlock("sal_bush_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SAL_BUSH_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> SAL_BUSH_LEAVES = BLOCKS.registerBlock("sal_bush_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<VineBlock> SAL_BUSH_VINE = BLOCKS.registerBlock("sal_bush_vine",
            VineBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).replaceable().noCollision().randomTicks().strength(0.2F).sound(SoundType.GRASS)
                    .ignitedByLava().pushReaction(PushReaction.DESTROY));
    //TODO: Sapling behaviour! See Shan conv on Disc
    public static final DeferredBlock<BlockSalBushSapling> SAL_BUSH_SAPLING = BLOCKS.registerBlock("sal_bush_sapling",
            BlockSalBushSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SAL_BUSH_SAPLING = BLOCKS.registerBlock("potted_sal_bush_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SAL_BUSH_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    //TODO: Confirm Shelf Behaviour
    private static void addBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SHELF, SAL_BUSH_SHELF.get());
    }

    // Fire spread and burn chances, the same values vanilla uses for cherry wood and vines
    // (26.1 has no data map for this; setFlammable is opened in META-INF/accesstransformer.cfg)
    private static void addFlammableBlocks(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FireBlock fire = (FireBlock) Blocks.FIRE;
            fire.setFlammable(SAL_BUSH_LOG.get(), 5, 5);
            fire.setFlammable(SAL_BUSH_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_SAL_BUSH_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_SAL_BUSH_WOOD.get(), 5, 5);
            fire.setFlammable(SAL_BUSH_PLANKS.get(), 5, 20);
            fire.setFlammable(SAL_BUSH_STAIRS.get(), 5, 20);
            fire.setFlammable(SAL_BUSH_SLAB.get(), 5, 20);
            fire.setFlammable(SAL_BUSH_FENCE.get(), 5, 20);
            fire.setFlammable(SAL_BUSH_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(SAL_BUSH_SHELF.get(), 30, 20);
            fire.setFlammable(SAL_BUSH_LEAVES.get(), 30, 60);
            fire.setFlammable(SAL_BUSH_VINE.get(), 15, 100);
        });
    }

    private WyrmroostBlockRegistry() {}
}