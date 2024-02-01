package com.github.shannieann.wyrmroost.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WRTextureSheetParticle extends TextureSheetParticle {
    public WRTextureSheetParticle(ClientLevel level, double xPos, double yPos, double zPos, double xMotion, double yMotion, double zMotion) {
        super(level, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
        this.xd = xMotion;
        this.yd = yMotion;
        this.zd = zMotion;

        //Setup previous values
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }


    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    @Override
    public void tick() {
        //Update particle values
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        //If particle has extended its lifetime, remove
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Vec3 cameraPos = renderInfo.getPosition();

        //Position based on partialTicks
        float xLerp = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float yLerp = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y());
        float zLerp = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());

        //Instantiate vertexVectorArray for later positioning vertices
        Vector3f[] vertexVectorArray = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};

        //Correctly position vertices
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = vertexVectorArray[i];
            vector3f.add(xLerp, yLerp, zLerp);
            float U0 = this.getU0();
            float U1 = this.getU1();
            float V0 = this.getV0();
            float V1 = this.getV1();
            int lightColor = this.getLightColor(partialTicks);

            //Construct sheet
            buffer.vertex(vertexVectorArray[0].x(), vertexVectorArray[0].y(), vertexVectorArray[0].z()).uv(U1, V1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            buffer.vertex(vertexVectorArray[1].x(), vertexVectorArray[1].y(), vertexVectorArray[1].z()).uv(U1, V0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            buffer.vertex(vertexVectorArray[2].x(), vertexVectorArray[2].y(), vertexVectorArray[2].z()).uv(U0, V0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
            buffer.vertex(vertexVectorArray[3].x(), vertexVectorArray[3].y(), vertexVectorArray[3].z()).uv(U0, V1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(lightColor).endVertex();
        }

    }

    //Getter methods
    public float getAge() {
        return age;
    }

    public double getPosX() {
        return x;
    }

    public void setPosX(double posX) {
        setPos(posX, this.y, this.z);
    }

    public double getPosY() {
        return y;
    }

    public void setPosY(double posY) {
        setPos(this.x, posY, this.z);
    }

    public double getPosZ() {
        return z;
    }

    public void setPosZ(double posZ) {
        setPos(this.x, this.y, posZ);
    }

    public double getMotionX() {
        return xd;
    }

    public void setMotionX(double motionX) {
        this.xd = motionX;
    }

    public double getMotionY() {
        return yd;
    }

    public void setMotionY(double motionY) {
        this.yd = motionY;
    }

    public double getMotionZ() {
        return zd;
    }

    public void setMotionZ(double motionZ) {
        this.zd = motionZ;
    }

    public Vec3 getPrevPos() {
        return new Vec3(xo, yo, zo);
    }

    public double getPrevPosX() {
        return xo;
    }

    public double getPrevPosY() {
        return yo;
    }

    public double getPrevPosZ() {
        return zo;
    }

    public Level getWorld() {
        return level;
    }

    public static class WRParticleFactory implements ParticleProvider<ParticleOptions> {
        private final SpriteSet spriteSet;


        public WRParticleFactory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(ParticleOptions pType, ClientLevel level, double xPos, double yPos, double zPos, double xSpeed, double ySpeed, double zSpeed) {
            WRTextureSheetParticle particle = new WRTextureSheetParticle(level, xPos, yPos, zPos, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(spriteSet);
            return particle;
        }
    }

    public static void spawnParticle(Level world, ParticleOptions options, double x, double y, double z, double motionX, double motionY, double motionZ, boolean faceCamera, double yaw, double pitch, double roll, double faceCameraAngle, double scale, double r, double g, double b, double a, double drag, double duration, boolean emissive, boolean canCollide) {
        world.addParticle(options, x, y, z, motionX, motionY, motionZ);
    }
}