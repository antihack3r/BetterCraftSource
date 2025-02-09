// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class ParticleExplosionLarge extends Particle
{
    private static final ResourceLocation EXPLOSION_TEXTURE;
    private static final VertexFormat VERTEX_FORMAT;
    private int life;
    private final int lifeTime;
    private final TextureManager theRenderEngine;
    private final float size;
    
    static {
        EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
        VERTEX_FORMAT = new VertexFormat().addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
    }
    
    protected ParticleExplosionLarge(final TextureManager renderEngine, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double p_i1213_9_, final double p_i1213_11_, final double p_i1213_13_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
        this.theRenderEngine = renderEngine;
        this.lifeTime = 6 + this.rand.nextInt(4);
        final float f = this.rand.nextFloat() * 0.6f + 0.4f;
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;
        this.size = 1.0f - (float)p_i1213_9_ * 0.5f;
    }
    
    @Override
    public void renderParticle(final BufferBuilder worldRendererIn, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        final int i = (int)((this.life + partialTicks) * 15.0f / this.lifeTime);
        if (i <= 15) {
            this.theRenderEngine.bindTexture(ParticleExplosionLarge.EXPLOSION_TEXTURE);
            final float f = i % 4 / 4.0f;
            final float f2 = f + 0.24975f;
            final float f3 = i / 4 / 4.0f;
            final float f4 = f3 + 0.24975f;
            final float f5 = 2.0f * this.size;
            final float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - ParticleExplosionLarge.interpPosX);
            final float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - ParticleExplosionLarge.interpPosY);
            final float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - ParticleExplosionLarge.interpPosZ);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();
            worldRendererIn.begin(7, ParticleExplosionLarge.VERTEX_FORMAT);
            worldRendererIn.pos(f6 - rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 - rotationYZ * f5 - rotationXZ * f5).tex(f2, f4).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldRendererIn.pos(f6 - rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 - rotationYZ * f5 + rotationXZ * f5).tex(f2, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldRendererIn.pos(f6 + rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 + rotationYZ * f5 + rotationXZ * f5).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
            worldRendererIn.pos(f6 + rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 + rotationYZ * f5 - rotationXZ * f5).tex(f, f4).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0f).lightmap(0, 240).normal(0.0f, 1.0f, 0.0f).endVertex();
            Tessellator.getInstance().draw();
            GlStateManager.enableLighting();
        }
    }
    
    @Override
    public int getBrightnessForRender(final float p_189214_1_) {
        return 61680;
    }
    
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.life;
        if (this.life == this.lifeTime) {
            this.setExpired();
        }
    }
    
    @Override
    public int getFXLayer() {
        return 3;
    }
    
    public static class Factory implements IParticleFactory
    {
        @Override
        public Particle createParticle(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new ParticleExplosionLarge(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
