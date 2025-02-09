// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;

public class ParticleExplosionHuge extends Particle
{
    private int timeSinceStart;
    private final int maximumTime = 8;
    
    protected ParticleExplosionHuge(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double p_i1214_8_, final double p_i1214_10_, final double p_i1214_12_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
    }
    
    @Override
    public void renderParticle(final BufferBuilder worldRendererIn, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
    }
    
    @Override
    public void onUpdate() {
        for (int i = 0; i < 6; ++i) {
            final double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            final double d2 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            final double d3 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d2, d3, this.timeSinceStart / 8.0f, 0.0, 0.0, new int[0]);
        }
        ++this.timeSinceStart;
        if (this.timeSinceStart == 8) {
            this.setExpired();
        }
    }
    
    @Override
    public int getFXLayer() {
        return 1;
    }
    
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new ParticleExplosionHuge(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
