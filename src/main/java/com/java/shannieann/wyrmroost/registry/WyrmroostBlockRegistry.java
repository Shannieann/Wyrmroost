package com.java.shannieann.wyrmroost.registry;

import com.java.shannieann.wyrmroost.Wyrmroost;
import com.java.shannieann.wyrmroost.block.BlockSalBushSapling;
import com.java.shannieann.wyrmroost.block.BlockKaliburnLeaves;
import com.java.shannieann.wyrmroost.block.BlockKaliburnLog;
import com.java.shannieann.wyrmroost.block.BlockKaliburnSapling;
import com.java.shannieann.wyrmroost.block.BlockDragonsEyeSapling;
import com.java.shannieann.wyrmroost.block.BlockDragonwoodLog;
import com.java.shannieann.wyrmroost.block.BlockDragonwoodSapling;
import com.java.shannieann.wyrmroost.block.BlockShimarinSapling;
import com.java.shannieann.wyrmroost.block.BlockSulphuricFire;
import com.java.shannieann.wyrmroost.block.BlockBrumePineSapling;
import com.java.shannieann.wyrmroost.block.BlockCanarSapling;
import com.java.shannieann.wyrmroost.block.BlockSurenSapling;
import com.java.shannieann.wyrmroost.block.BlockHylanSedgeSapling;

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

    // =============
    //  KALIBURN WOOD
    // =============
    private static final MapColor KALIBURN_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor KALIBURN_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<BlockKaliburnLog> KALIBURN_LOG = BLOCKS.registerBlock("kaliburn_log",
            BlockKaliburnLog::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? KALIBURN_WOOD_COLOUR : KALIBURN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<RotatedPillarBlock> KALIBURN_WOOD = BLOCKS.registerBlock("kaliburn_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? KALIBURN_BARK_COLOUR : KALIBURN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_KALIBURN_LOG = BLOCKS.registerBlock("stripped_kaliburn_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? KALIBURN_WOOD_COLOUR : KALIBURN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_KALIBURN_WOOD = BLOCKS.registerBlock("stripped_kaliburn_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? KALIBURN_WOOD_COLOUR : KALIBURN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<Block> KALIBURN_PLANKS = BLOCKS.registerBlock("kaliburn_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<StairBlock> KALIBURN_STAIRS = BLOCKS.registerBlock("kaliburn_stairs",
            properties -> new StairBlock(KALIBURN_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<SlabBlock> KALIBURN_SLAB = BLOCKS.registerBlock("kaliburn_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    public static final DeferredBlock<FenceBlock> KALIBURN_FENCE = BLOCKS.registerBlock("kaliburn_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> KALIBURN_FENCE_GATE = BLOCKS.registerBlock("kaliburn_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.KALIBURN, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> KALIBURN_DOOR = BLOCKS.registerBlock("kaliburn_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.KALIBURN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> KALIBURN_TRAPDOOR = BLOCKS.registerBlock("kaliburn_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.KALIBURN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> KALIBURN_PRESSURE_PLATE = BLOCKS.registerBlock("kaliburn_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.KALIBURN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> KALIBURN_BUTTON = BLOCKS.registerBlock("kaliburn_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.KALIBURN_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> KALIBURN_SHELF = BLOCKS.registerBlock("kaliburn_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(KALIBURN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).strength(2.0F, 3.0F));
    public static final DeferredBlock<BlockKaliburnLeaves> KALIBURN_LEAVES = BLOCKS.registerBlock("kaliburn_leaves",
            BlockKaliburnLeaves::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).replaceable().noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<BlockKaliburnSapling> KALIBURN_SAPLING = BLOCKS.registerBlock("kaliburn_sapling",
            BlockKaliburnSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_KALIBURN_SAPLING = BLOCKS.registerBlock("potted_kaliburn_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, KALIBURN_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<BlockSulphuricFire> SULPHURIC_FIRE = BLOCKS.registerBlock("sulphuric_fire",
            BlockSulphuricFire::new, () -> BlockBehaviour.Properties.of()
                    .replaceable().noCollision().instabreak().lightLevel(state -> 10).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));

    // =============
    //  DRAGONSEYE WOOD
    // =============
    private static final MapColor DRAGONSEYE_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor DRAGONSEYE_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> DRAGONSEYE_LOG = BLOCKS.registerBlock("dragonseye_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONSEYE_WOOD_COLOUR : DRAGONSEYE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> DRAGONSEYE_WOOD = BLOCKS.registerBlock("dragonseye_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONSEYE_BARK_COLOUR : DRAGONSEYE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_DRAGONSEYE_LOG = BLOCKS.registerBlock("stripped_dragonseye_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONSEYE_WOOD_COLOUR : DRAGONSEYE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_DRAGONSEYE_WOOD = BLOCKS.registerBlock("stripped_dragonseye_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONSEYE_WOOD_COLOUR : DRAGONSEYE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> DRAGONSEYE_PLANKS = BLOCKS.registerBlock("dragonseye_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> DRAGONSEYE_STAIRS = BLOCKS.registerBlock("dragonseye_stairs",
            properties -> new StairBlock(DRAGONSEYE_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> DRAGONSEYE_SLAB = BLOCKS.registerBlock("dragonseye_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> DRAGONSEYE_FENCE = BLOCKS.registerBlock("dragonseye_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> DRAGONSEYE_FENCE_GATE = BLOCKS.registerBlock("dragonseye_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.DRAGONSEYE, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> DRAGONSEYE_DOOR = BLOCKS.registerBlock("dragonseye_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.DRAGONSEYE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> DRAGONSEYE_TRAPDOOR = BLOCKS.registerBlock("dragonseye_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.DRAGONSEYE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> DRAGONSEYE_PRESSURE_PLATE = BLOCKS.registerBlock("dragonseye_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.DRAGONSEYE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> DRAGONSEYE_BUTTON = BLOCKS.registerBlock("dragonseye_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.DRAGONSEYE_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> DRAGONSEYE_SHELF = BLOCKS.registerBlock("dragonseye_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONSEYE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> DRAGONSEYE_LEAVES = BLOCKS.registerBlock("dragonseye_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockDragonsEyeSapling> DRAGONSEYE_SAPLING = BLOCKS.registerBlock("dragonseye_sapling",
            BlockDragonsEyeSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_DRAGONSEYE_SAPLING = BLOCKS.registerBlock("potted_dragonseye_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DRAGONSEYE_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  DRAGONWOOD
    // =============
    //TODO: Stripped log texture
    //TODO: Keep stripped dragonwood wood or swap it for a stripped bundle?
    private static final MapColor DRAGONWOOD_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor DRAGONWOOD_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<BlockDragonwoodLog> DRAGONWOOD_LOG = BLOCKS.registerBlock("dragonwood_log",
            BlockDragonwoodLog::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONWOOD_WOOD_COLOUR : DRAGONWOOD_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava().noOcclusion());
    public static final DeferredBlock<RotatedPillarBlock> DRAGONWOOD_BUNDLE = BLOCKS.registerBlock("dragonwood_bundle",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONWOOD_BARK_COLOUR : DRAGONWOOD_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<BlockDragonwoodLog> STRIPPED_DRAGONWOOD_LOG = BLOCKS.registerBlock("stripped_dragonwood_log",
            BlockDragonwoodLog::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONWOOD_WOOD_COLOUR : DRAGONWOOD_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava().noOcclusion());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_DRAGONWOOD_WOOD = BLOCKS.registerBlock("stripped_dragonwood_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? DRAGONWOOD_WOOD_COLOUR : DRAGONWOOD_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> DRAGONWOOD_PLANKS = BLOCKS.registerBlock("dragonwood_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> DRAGONWOOD_STAIRS = BLOCKS.registerBlock("dragonwood_stairs",
            properties -> new StairBlock(DRAGONWOOD_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> DRAGONWOOD_SLAB = BLOCKS.registerBlock("dragonwood_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> DRAGONWOOD_FENCE = BLOCKS.registerBlock("dragonwood_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> DRAGONWOOD_FENCE_GATE = BLOCKS.registerBlock("dragonwood_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.DRAGONWOOD, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> DRAGONWOOD_DOOR = BLOCKS.registerBlock("dragonwood_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.DRAGONWOOD_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> DRAGONWOOD_TRAPDOOR = BLOCKS.registerBlock("dragonwood_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.DRAGONWOOD_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> DRAGONWOOD_PRESSURE_PLATE = BLOCKS.registerBlock("dragonwood_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.DRAGONWOOD_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> DRAGONWOOD_BUTTON = BLOCKS.registerBlock("dragonwood_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.DRAGONWOOD_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> DRAGONWOOD_SHELF = BLOCKS.registerBlock("dragonwood_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(DRAGONWOOD_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> DRAGONWOOD_LEAVES = BLOCKS.registerBlock("dragonwood_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockDragonwoodSapling> DRAGONWOOD_SAPLING = BLOCKS.registerBlock("dragonwood_sapling",
            BlockDragonwoodSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_DRAGONWOOD_SAPLING = BLOCKS.registerBlock("potted_dragonwood_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, DRAGONWOOD_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  SHIMARIN WOOD
    // =============
    //TODO: Door, trapdoor and stripped log textures
    private static final MapColor SHIMARIN_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor SHIMARIN_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> SHIMARIN_LOG = BLOCKS.registerBlock("shimarin_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SHIMARIN_WOOD_COLOUR : SHIMARIN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> SHIMARIN_WOOD = BLOCKS.registerBlock("shimarin_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SHIMARIN_BARK_COLOUR : SHIMARIN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SHIMARIN_LOG = BLOCKS.registerBlock("stripped_shimarin_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SHIMARIN_WOOD_COLOUR : SHIMARIN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SHIMARIN_WOOD = BLOCKS.registerBlock("stripped_shimarin_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SHIMARIN_WOOD_COLOUR : SHIMARIN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> SHIMARIN_PLANKS = BLOCKS.registerBlock("shimarin_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> SHIMARIN_STAIRS = BLOCKS.registerBlock("shimarin_stairs",
            properties -> new StairBlock(SHIMARIN_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> SHIMARIN_SLAB = BLOCKS.registerBlock("shimarin_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> SHIMARIN_FENCE = BLOCKS.registerBlock("shimarin_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> SHIMARIN_FENCE_GATE = BLOCKS.registerBlock("shimarin_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.SHIMARIN, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> SHIMARIN_DOOR = BLOCKS.registerBlock("shimarin_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.SHIMARIN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> SHIMARIN_TRAPDOOR = BLOCKS.registerBlock("shimarin_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.SHIMARIN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> SHIMARIN_PRESSURE_PLATE = BLOCKS.registerBlock("shimarin_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.SHIMARIN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> SHIMARIN_BUTTON = BLOCKS.registerBlock("shimarin_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.SHIMARIN_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> SHIMARIN_SHELF = BLOCKS.registerBlock("shimarin_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SHIMARIN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> SHIMARIN_LEAVES = BLOCKS.registerBlock("shimarin_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockShimarinSapling> SHIMARIN_SAPLING = BLOCKS.registerBlock("shimarin_sapling",
            BlockShimarinSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SHIMARIN_SAPLING = BLOCKS.registerBlock("potted_shimarin_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SHIMARIN_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  BRUME PINE WOOD
    // =============
    //TODO: Door, trapdoor and stripped log textures
    private static final MapColor BRUME_PINE_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor BRUME_PINE_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> BRUME_PINE_LOG = BLOCKS.registerBlock("brume_pine_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? BRUME_PINE_WOOD_COLOUR : BRUME_PINE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> BRUME_PINE_WOOD = BLOCKS.registerBlock("brume_pine_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? BRUME_PINE_BARK_COLOUR : BRUME_PINE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_BRUME_PINE_LOG = BLOCKS.registerBlock("stripped_brume_pine_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? BRUME_PINE_WOOD_COLOUR : BRUME_PINE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_BRUME_PINE_WOOD = BLOCKS.registerBlock("stripped_brume_pine_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? BRUME_PINE_WOOD_COLOUR : BRUME_PINE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> BRUME_PINE_PLANKS = BLOCKS.registerBlock("brume_pine_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> BRUME_PINE_STAIRS = BLOCKS.registerBlock("brume_pine_stairs",
            properties -> new StairBlock(BRUME_PINE_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> BRUME_PINE_SLAB = BLOCKS.registerBlock("brume_pine_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> BRUME_PINE_FENCE = BLOCKS.registerBlock("brume_pine_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> BRUME_PINE_FENCE_GATE = BLOCKS.registerBlock("brume_pine_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.BRUME_PINE, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> BRUME_PINE_DOOR = BLOCKS.registerBlock("brume_pine_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.BRUME_PINE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> BRUME_PINE_TRAPDOOR = BLOCKS.registerBlock("brume_pine_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.BRUME_PINE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> BRUME_PINE_PRESSURE_PLATE = BLOCKS.registerBlock("brume_pine_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.BRUME_PINE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> BRUME_PINE_BUTTON = BLOCKS.registerBlock("brume_pine_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.BRUME_PINE_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> BRUME_PINE_SHELF = BLOCKS.registerBlock("brume_pine_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(BRUME_PINE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> BRUME_PINE_LEAVES = BLOCKS.registerBlock("brume_pine_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockBrumePineSapling> BRUME_PINE_SAPLING = BLOCKS.registerBlock("brume_pine_sapling",
            BlockBrumePineSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_BRUME_PINE_SAPLING = BLOCKS.registerBlock("potted_brume_pine_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, BRUME_PINE_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  CANAR WOOD
    // =============
    private static final MapColor CANAR_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor CANAR_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> CANAR_LOG = BLOCKS.registerBlock("canar_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? CANAR_WOOD_COLOUR : CANAR_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> CANAR_WOOD = BLOCKS.registerBlock("canar_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? CANAR_BARK_COLOUR : CANAR_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CANAR_LOG = BLOCKS.registerBlock("stripped_canar_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? CANAR_WOOD_COLOUR : CANAR_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CANAR_WOOD = BLOCKS.registerBlock("stripped_canar_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? CANAR_WOOD_COLOUR : CANAR_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> CANAR_PLANKS = BLOCKS.registerBlock("canar_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> CANAR_STAIRS = BLOCKS.registerBlock("canar_stairs",
            properties -> new StairBlock(CANAR_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> CANAR_SLAB = BLOCKS.registerBlock("canar_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> CANAR_FENCE = BLOCKS.registerBlock("canar_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> CANAR_FENCE_GATE = BLOCKS.registerBlock("canar_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.CANAR, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> CANAR_DOOR = BLOCKS.registerBlock("canar_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.CANAR_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> CANAR_TRAPDOOR = BLOCKS.registerBlock("canar_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.CANAR_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> CANAR_PRESSURE_PLATE = BLOCKS.registerBlock("canar_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.CANAR_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> CANAR_BUTTON = BLOCKS.registerBlock("canar_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.CANAR_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> CANAR_SHELF = BLOCKS.registerBlock("canar_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(CANAR_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> CANAR_LEAVES = BLOCKS.registerBlock("canar_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockCanarSapling> CANAR_SAPLING = BLOCKS.registerBlock("canar_sapling",
            BlockCanarSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_CANAR_SAPLING = BLOCKS.registerBlock("potted_canar_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CANAR_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  SUREN WOOD
    // =============
    private static final MapColor SUREN_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor SUREN_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> SUREN_LOG = BLOCKS.registerBlock("suren_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SUREN_WOOD_COLOUR : SUREN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> SUREN_WOOD = BLOCKS.registerBlock("suren_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SUREN_BARK_COLOUR : SUREN_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SUREN_LOG = BLOCKS.registerBlock("stripped_suren_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SUREN_WOOD_COLOUR : SUREN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SUREN_WOOD = BLOCKS.registerBlock("stripped_suren_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? SUREN_WOOD_COLOUR : SUREN_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> SUREN_PLANKS = BLOCKS.registerBlock("suren_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> SUREN_STAIRS = BLOCKS.registerBlock("suren_stairs",
            properties -> new StairBlock(SUREN_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> SUREN_SLAB = BLOCKS.registerBlock("suren_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> SUREN_FENCE = BLOCKS.registerBlock("suren_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> SUREN_FENCE_GATE = BLOCKS.registerBlock("suren_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.SUREN, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> SUREN_DOOR = BLOCKS.registerBlock("suren_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.SUREN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> SUREN_TRAPDOOR = BLOCKS.registerBlock("suren_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.SUREN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> SUREN_PRESSURE_PLATE = BLOCKS.registerBlock("suren_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.SUREN_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> SUREN_BUTTON = BLOCKS.registerBlock("suren_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.SUREN_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> SUREN_SHELF = BLOCKS.registerBlock("suren_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(SUREN_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> SUREN_LEAVES = BLOCKS.registerBlock("suren_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockSurenSapling> SUREN_SAPLING = BLOCKS.registerBlock("suren_sapling",
            BlockSurenSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_SUREN_SAPLING = BLOCKS.registerBlock("potted_suren_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, SUREN_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    // =============
    //  HYLAN SEDGE WOOD
    // =============
    //TODO: Door, trapdoor and stripped log textures
    private static final MapColor HYLAN_SEDGE_WOOD_COLOUR = MapColor.WOOD;
    private static final MapColor HYLAN_SEDGE_BARK_COLOUR = MapColor.PODZOL;

    public static final DeferredBlock<RotatedPillarBlock> HYLAN_SEDGE_LOG = BLOCKS.registerBlock("hylan_sedge_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? HYLAN_SEDGE_WOOD_COLOUR : HYLAN_SEDGE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> HYLAN_SEDGE_WOOD = BLOCKS.registerBlock("hylan_sedge_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? HYLAN_SEDGE_BARK_COLOUR : HYLAN_SEDGE_BARK_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_HYLAN_SEDGE_LOG = BLOCKS.registerBlock("stripped_hylan_sedge_log",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? HYLAN_SEDGE_WOOD_COLOUR : HYLAN_SEDGE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_HYLAN_SEDGE_WOOD = BLOCKS.registerBlock("stripped_hylan_sedge_wood",
            RotatedPillarBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? HYLAN_SEDGE_WOOD_COLOUR : HYLAN_SEDGE_WOOD_COLOUR)
                    .instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<Block> HYLAN_SEDGE_PLANKS = BLOCKS.registerBlock("hylan_sedge_planks",
            Block::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<StairBlock> HYLAN_SEDGE_STAIRS = BLOCKS.registerBlock("hylan_sedge_stairs",
            properties -> new StairBlock(HYLAN_SEDGE_PLANKS.get().defaultBlockState(), properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<SlabBlock> HYLAN_SEDGE_SLAB = BLOCKS.registerBlock("hylan_sedge_slab",
            SlabBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava());
    public static final DeferredBlock<FenceBlock> HYLAN_SEDGE_FENCE = BLOCKS.registerBlock("hylan_sedge_fence",
            FenceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava().forceSolidOn());
    public static final DeferredBlock<FenceGateBlock> HYLAN_SEDGE_FENCE_GATE = BLOCKS.registerBlock("hylan_sedge_fence_gate",
            properties -> new FenceGateBlock(WyrmroostWoodTypes.HYLAN_SEDGE, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().strength(2.0F, 3.0F));
    public static final DeferredBlock<DoorBlock> HYLAN_SEDGE_DOOR = BLOCKS.registerBlock("hylan_sedge_door",
            properties -> new DoorBlock(WyrmroostWoodTypes.HYLAN_SEDGE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<TrapDoorBlock> HYLAN_SEDGE_TRAPDOOR = BLOCKS.registerBlock("hylan_sedge_trapdoor",
            properties -> new TrapDoorBlock(WyrmroostWoodTypes.HYLAN_SEDGE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(3.0F).noOcclusion());
    public static final DeferredBlock<PressurePlateBlock> HYLAN_SEDGE_PRESSURE_PLATE = BLOCKS.registerBlock("hylan_sedge_pressure_plate",
            properties -> new PressurePlateBlock(WyrmroostWoodTypes.HYLAN_SEDGE_SET, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).ignitedByLava().forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ButtonBlock> HYLAN_SEDGE_BUTTON = BLOCKS.registerBlock("hylan_sedge_button",
            properties -> new ButtonBlock(WyrmroostWoodTypes.HYLAN_SEDGE_SET, 30, properties), () -> BlockBehaviour.Properties.of()
                    .noCollision().strength(0.5F).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<ShelfBlock> HYLAN_SEDGE_SHELF = BLOCKS.registerBlock("hylan_sedge_shelf",
            ShelfBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(HYLAN_SEDGE_WOOD_COLOUR).instrument(NoteBlockInstrument.BASS).sound(SoundType.SHELF).ignitedByLava().strength(2.0F, 3.0F));
    public static final DeferredBlock<TintedParticleLeavesBlock> HYLAN_SEDGE_LEAVES = BLOCKS.registerBlock("hylan_sedge_leaves",
            properties -> new TintedParticleLeavesBlock(0.01F, properties), () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> entityType == EntityType.OCELOT || entityType == EntityType.PARROT).isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY)
                    .isRedstoneConductor((state, level, pos) -> false));
    public static final DeferredBlock<BlockHylanSedgeSapling> HYLAN_SEDGE_SAPLING = BLOCKS.registerBlock("hylan_sedge_sapling",
            BlockHylanSedgeSapling::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollision().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<FlowerPotBlock> POTTED_HYLAN_SEDGE_SAPLING = BLOCKS.registerBlock("potted_hylan_sedge_sapling",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, HYLAN_SEDGE_SAPLING, properties), () -> BlockBehaviour.Properties.of()
                    .instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));

    //TODO: Confirm Shelf Behaviour
    //TODO: Shelf textures for every tree
    private static void addBlockEntityBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SHELF,
                SAL_BUSH_SHELF.get(),
                KALIBURN_SHELF.get(),
                DRAGONSEYE_SHELF.get(),
                DRAGONWOOD_SHELF.get(),
                SHIMARIN_SHELF.get(),
                BRUME_PINE_SHELF.get(),
                CANAR_SHELF.get(),
                SUREN_SHELF.get(),
                HYLAN_SEDGE_SHELF.get());
    }

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
            fire.setFlammable(DRAGONSEYE_LOG.get(), 5, 5);
            fire.setFlammable(DRAGONSEYE_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_DRAGONSEYE_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_DRAGONSEYE_WOOD.get(), 5, 5);
            fire.setFlammable(DRAGONSEYE_PLANKS.get(), 5, 20);
            fire.setFlammable(DRAGONSEYE_STAIRS.get(), 5, 20);
            fire.setFlammable(DRAGONSEYE_SLAB.get(), 5, 20);
            fire.setFlammable(DRAGONSEYE_FENCE.get(), 5, 20);
            fire.setFlammable(DRAGONSEYE_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(DRAGONSEYE_SHELF.get(), 30, 20);
            fire.setFlammable(DRAGONSEYE_LEAVES.get(), 30, 60);
            fire.setFlammable(DRAGONWOOD_LOG.get(), 5, 5);
            fire.setFlammable(DRAGONWOOD_BUNDLE.get(), 5, 5);
            fire.setFlammable(STRIPPED_DRAGONWOOD_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_DRAGONWOOD_WOOD.get(), 5, 5);
            fire.setFlammable(DRAGONWOOD_PLANKS.get(), 5, 20);
            fire.setFlammable(DRAGONWOOD_STAIRS.get(), 5, 20);
            fire.setFlammable(DRAGONWOOD_SLAB.get(), 5, 20);
            fire.setFlammable(DRAGONWOOD_FENCE.get(), 5, 20);
            fire.setFlammable(DRAGONWOOD_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(DRAGONWOOD_SHELF.get(), 30, 20);
            fire.setFlammable(DRAGONWOOD_LEAVES.get(), 30, 60);
            fire.setFlammable(SHIMARIN_LOG.get(), 5, 5);
            fire.setFlammable(SHIMARIN_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_SHIMARIN_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_SHIMARIN_WOOD.get(), 5, 5);
            fire.setFlammable(SHIMARIN_PLANKS.get(), 5, 20);
            fire.setFlammable(SHIMARIN_STAIRS.get(), 5, 20);
            fire.setFlammable(SHIMARIN_SLAB.get(), 5, 20);
            fire.setFlammable(SHIMARIN_FENCE.get(), 5, 20);
            fire.setFlammable(SHIMARIN_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(SHIMARIN_SHELF.get(), 30, 20);
            fire.setFlammable(SHIMARIN_LEAVES.get(), 30, 60);
            fire.setFlammable(BRUME_PINE_LOG.get(), 5, 5);
            fire.setFlammable(BRUME_PINE_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_BRUME_PINE_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_BRUME_PINE_WOOD.get(), 5, 5);
            fire.setFlammable(BRUME_PINE_PLANKS.get(), 5, 20);
            fire.setFlammable(BRUME_PINE_STAIRS.get(), 5, 20);
            fire.setFlammable(BRUME_PINE_SLAB.get(), 5, 20);
            fire.setFlammable(BRUME_PINE_FENCE.get(), 5, 20);
            fire.setFlammable(BRUME_PINE_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(BRUME_PINE_SHELF.get(), 30, 20);
            fire.setFlammable(BRUME_PINE_LEAVES.get(), 30, 60);
            fire.setFlammable(CANAR_LOG.get(), 5, 5);
            fire.setFlammable(CANAR_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_CANAR_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_CANAR_WOOD.get(), 5, 5);
            fire.setFlammable(CANAR_PLANKS.get(), 5, 20);
            fire.setFlammable(CANAR_STAIRS.get(), 5, 20);
            fire.setFlammable(CANAR_SLAB.get(), 5, 20);
            fire.setFlammable(CANAR_FENCE.get(), 5, 20);
            fire.setFlammable(CANAR_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(CANAR_SHELF.get(), 30, 20);
            fire.setFlammable(CANAR_LEAVES.get(), 30, 60);
            fire.setFlammable(SUREN_LOG.get(), 5, 5);
            fire.setFlammable(SUREN_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_SUREN_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_SUREN_WOOD.get(), 5, 5);
            fire.setFlammable(SUREN_PLANKS.get(), 5, 20);
            fire.setFlammable(SUREN_STAIRS.get(), 5, 20);
            fire.setFlammable(SUREN_SLAB.get(), 5, 20);
            fire.setFlammable(SUREN_FENCE.get(), 5, 20);
            fire.setFlammable(SUREN_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(SUREN_SHELF.get(), 30, 20);
            fire.setFlammable(SUREN_LEAVES.get(), 30, 60);
            fire.setFlammable(HYLAN_SEDGE_LOG.get(), 5, 5);
            fire.setFlammable(HYLAN_SEDGE_WOOD.get(), 5, 5);
            fire.setFlammable(STRIPPED_HYLAN_SEDGE_LOG.get(), 5, 5);
            fire.setFlammable(STRIPPED_HYLAN_SEDGE_WOOD.get(), 5, 5);
            fire.setFlammable(HYLAN_SEDGE_PLANKS.get(), 5, 20);
            fire.setFlammable(HYLAN_SEDGE_STAIRS.get(), 5, 20);
            fire.setFlammable(HYLAN_SEDGE_SLAB.get(), 5, 20);
            fire.setFlammable(HYLAN_SEDGE_FENCE.get(), 5, 20);
            fire.setFlammable(HYLAN_SEDGE_FENCE_GATE.get(), 5, 20);
            fire.setFlammable(HYLAN_SEDGE_SHELF.get(), 30, 20);
            fire.setFlammable(HYLAN_SEDGE_LEAVES.get(), 30, 60);
        });
    }

    private WyrmroostBlockRegistry() {}
}