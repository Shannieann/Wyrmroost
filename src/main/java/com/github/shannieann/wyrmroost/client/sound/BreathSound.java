package com.github.shannieann.wyrmroost.client.sound;

/*import com.github.shannieann.wyrmroost.entities.dragon.EntityRoyalRed;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class BreathSound extends AbstractTickableSoundInstance
{
    private final EntityRoyalRed dragon;

    public BreathSound(EntityRoyalRed dragon)
    {
        super(WRSounds.FIRE_BREATH.get(), SoundSource.PLAYERS);
        this.dragon = dragon;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1f;
    }

    @Override
    public void tick()
    {
        float tick = dragon.breathTimer.get();
        if (!dragon.isAlive() || tick == 0)
        {
            stop();
            return;
        }
        volume = tick * dragon.getSoundVolume();
        Vec3 mouth = dragon.getApproximateMouthPos();
        x = (float) mouth.x;
        y = (float) mouth.y;
        z = (float) mouth.z;
    }

    public static void play(EntityRoyalRed dragon)
    {
        Minecraft.getInstance().getSoundManager().play(new BreathSound(dragon));
    }
}
*/