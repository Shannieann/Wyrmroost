package com.github.shannieann.wyrmroost.client.model;

/*import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class ModelAnimator
{
    public static final ModelAnimator INSTANCE = new ModelAnimator();

    private int tempTick = 0;
    private int prevTempTick;
    private float partialTicks;
    private IAnimatable entity;
    private final HashMap<WRModelPart, BoxPosCache> boxPosCache = new HashMap<>();
    private final HashMap<WRModelPart, BoxPosCache> prevPosCache = new HashMap<>();

    private ModelAnimator()
    {
    }

    public IAnimatable getEntity()
    {
        return entity;
    }

    public <T extends IAnimatable, M extends Model> void tick(T entity, M model, float partialTicks)
    {
        this.tempTick = prevTempTick = 0;
        this.entity = entity;
        this.partialTicks = partialTicks;
        boxPosCache.clear();
        prevPosCache.clear();

        Animation current = entity.getAnimation();
        if (current != IAnimatable.NO_ANIMATION) current.animate(model);
    }

    public ModelAnimator startKeyframe(int duration)
    {
        prevTempTick = tempTick;
        tempTick += duration;
        return this;
    }

    public ModelAnimator setStaticKeyframe(int duration)
    {
        startKeyframe(duration);
        endKeyframe(true);
        return this;
    }

    public void resetKeyframe(int duration)
    {
        startKeyframe(duration);
        endKeyframe();
    }

    public ModelAnimator rotate(WRModelPart box, float x, float y, float z)
    {
        getPosCache(box).addRotation(x, y, z);
        return this;
    }

    public ModelAnimator move(WRModelPart box, float x, float y, float z)
    {
        getPosCache(box).addOffset(x, y, z);
        return this;
    }

    private BoxPosCache getPosCache(WRModelPart box)
    {
        return boxPosCache.computeIfAbsent(box, b -> new BoxPosCache());
    }

    public void endKeyframe()
    {
        endKeyframe(false);
    }

    private void endKeyframe(boolean stationary)
    {
        int animationTick = entity.getAnimationTick();
        if (animationTick >= prevTempTick && animationTick < tempTick)
        {
            if (stationary)
            {
                for (Map.Entry<WRModelPart, BoxPosCache> entry : prevPosCache.entrySet())
                {
                    ModelPart box = entry.getKey();
                    BoxPosCache cache = entry.getValue();
                    box.xRot += cache.getRotationX();
                    box.yRot += cache.getRotationY();
                    box.zRot += cache.getRotationZ();
                    box.x += cache.getOffsetX();
                    box.y += cache.getOffsetY();
                    box.z += cache.getOffsetZ();
                }
            }
            else
            {
                float tick = ((float) (animationTick - prevTempTick) + partialTicks) / (tempTick - prevTempTick);
                float inc = Mth.sin(tick * Mafs.PI / 2f);
                float dec = 1f - inc;

                for (Map.Entry<WRModelPart, BoxPosCache> entry : prevPosCache.entrySet())
                {
                    ModelPart box = entry.getKey();
                    BoxPosCache cache = entry.getValue();
                    box.xRot += dec * cache.getRotationX();
                    box.yRot += dec * cache.getRotationY();
                    box.zRot += dec * cache.getRotationZ();
                    box.x += dec * cache.getOffsetX();
                    box.y += dec * cache.getOffsetY();
                    box.z += dec * cache.getOffsetZ();
                }

                for (Map.Entry<WRModelPart, BoxPosCache> entry : boxPosCache.entrySet())
                {
                    ModelPart box = entry.getKey();
                    BoxPosCache cache = entry.getValue();
                    box.xRot += inc * cache.getRotationX();
                    box.yRot += inc * cache.getRotationY();
                    box.zRot += inc * cache.getRotationZ();
                    box.x += inc * cache.getOffsetX();
                    box.y += inc * cache.getOffsetY();
                    box.z += inc * cache.getOffsetZ();
                }
            }
        }

        if (!stationary)
        {
            prevPosCache.clear();
            prevPosCache.putAll(boxPosCache);
            boxPosCache.clear();
        }
    }
}
*/