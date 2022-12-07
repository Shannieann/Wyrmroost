package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.particle.PetalParticle;
import com.github.shannieann.wyrmroost.client.particle.data.ColoredParticleData;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class WRParticles<T extends ParticleOptions> extends ParticleType<T>
{
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Wyrmroost.MOD_ID);
    public static final RegistryObject<ParticleType<ColoredParticleData>> PETAL = register("petal", false, ColoredParticleData::codec, ColoredParticleData::read, () -> PetalParticle::new);

    private final Codec<T> codec;
    private final Supplier<BetterParticleFactory<T>> factory;

    public WRParticles(boolean alwaysShow, Function<ParticleType<T>, Codec<T>> codec, ParticleOptions.Deserializer<T> deserializer, Supplier<BetterParticleFactory<T>> factory)
    {
        super(alwaysShow, deserializer);
        this.codec = codec.apply(this);
        this.factory = factory;
    }

    public WRParticles(boolean alwaysShow, ParticleType<T> wrapped, Supplier<BetterParticleFactory<T>> factory)
    {
        this(alwaysShow, t -> wrapped.codec(), wrapped.getDeserializer(), factory);
    }

    @Override
    public Codec<T> codec()
    {
        return codec;
    }

    public void bake()
    {
        ClientEvents.getClient().particleEngine.register(this, sprite -> ((d, w, x, y, z, xS, yS, zS) ->
                factory.get().create(d, w, sprite, x, y, z, xS, yS, zS)));
    }

    public static RegistryObject<ParticleType<SimpleParticleType>> basic(String name, boolean alwaysShow, Supplier<BetterParticleFactory<SimpleParticleType>> factory)
    {
        return REGISTRY.register(name, () -> new WRParticles<>(alwaysShow, new SimpleParticleType(false), factory));
    }

    public static <T extends ParticleOptions> RegistryObject<ParticleType<T>> register(String name, boolean alwaysShow, Function<ParticleType<T>, Codec<T>> codec, IReader<T> reader, Supplier<BetterParticleFactory<T>> factory)
    {
        return REGISTRY.register(name, () -> new WRParticles<>(alwaysShow, codec, reader, factory));
    }

    public interface BetterParticleFactory<T extends ParticleOptions>
    {
        Particle create(T data, ClientLevel world, SpriteSet spriteSheet, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
    }

    public interface IReader<T extends ParticleOptions> extends ParticleOptions.Deserializer<T>
    {
        @Override
        T fromCommand(ParticleType<T> type, StringReader reader) throws CommandSyntaxException;

        @Override
        default T fromNetwork(ParticleType<T> type, FriendlyByteBuf buffer)
        {
            return buffer.readWithCodec(type.codec());
        }
    }
}
