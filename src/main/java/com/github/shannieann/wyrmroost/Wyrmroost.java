package com.github.shannieann.wyrmroost;

import com.github.shannieann.wyrmroost.network.*;
import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.events.CommonEvents;
import com.github.shannieann.wyrmroost.registry.*;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Wyrmroost.MOD_ID)
public class Wyrmroost
{
    public static final String MOD_ID = "wyrmroost";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    public static final SimpleChannel NETWORK;

    public Wyrmroost()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        CommonEvents.init();
        if (WRModUtils.isClient()) {
            ClientEvents.init();
        }
        //bus.register(CapabilityEvent.class);
        WREntityTypes.REGISTRY.register(bus);
        WREntityTypes.Attributes.REGISTRY.register(bus);
        WRBlocks.REGISTRY.register(bus);
        //WRBlockEntities.REGISTRY.register(bus);
        WRItems.REGISTRY.register(bus);
        //ToDo: Check dedicated server crash
        bus.addGenericListener(Item.class, WRItems::registerItemProperties);

        WRIO.REGISTRY.register(bus);
        WRSounds.REGISTRY.register(bus);
        //WRWorld.Features.REGISTRY.register(bus);
        WRParticles.REGISTRY.register(bus);
        WREffects.REGISTRY.register(bus);
        //WRFluids.REGISTRY.register(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, WRServerConfig.SERVER_CONFIG);
    }

    public static ResourceLocation id(String path)
    {
        return new ResourceLocation(MOD_ID, path);
    }

    static {
        final String PROTOCOL_VERSION = "1.0";
        final SimpleChannel network = NetworkRegistry.ChannelBuilder
                .named(id("network"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();

        int index = 0;
        //network.messageBuilder(AnimationPacket.class, index, NetworkDirection.PLAY_TO_CLIENT).encoder(AnimationPacket::encode).decoder(AnimationPacket::new).consumer(AnimationPacket::handle).add();
        network.messageBuilder(KeybindHandler.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(KeybindHandler::encode).decoder(KeybindHandler::new).consumer(KeybindHandler::handle).add();
        network.messageBuilder(RenameEntityPacket.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(RenameEntityPacket::encode).decoder(RenameEntityPacket::new).consumer(RenameEntityPacket::handle).add();
        network.messageBuilder(BookActionPacket.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(BookActionPacket::encode).decoder(BookActionPacket::new).consumer(BookActionPacket::handle).add();
        network.messageBuilder(SGGlidePacket.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(SGGlidePacket::encode).decoder(SGGlidePacket::new).consumer(SGGlidePacket::handle).add();
        network.messageBuilder(AddPassengerPacket.class, ++index, NetworkDirection.PLAY_TO_CLIENT).encoder(AddPassengerPacket::encode).decoder(AddPassengerPacket::new).consumer(AddPassengerPacket::handle).add();
        network.messageBuilder(PacketKey.class, ++index, NetworkDirection.PLAY_TO_SERVER).encoder(PacketKey::encode).decoder(PacketKey::new).consumer(PacketKey::handle).add();
        NETWORK = network;
    }
}
