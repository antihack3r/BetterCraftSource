// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import shadersmod.client.Shaders;
import optifine.Config;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntityBeacon;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon>
{
    public static final ResourceLocation TEXTURE_BEACON_BEAM;
    
    static {
        TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");
    }
    
    @Override
    public void func_192841_a(final TileEntityBeacon p_192841_1_, final double p_192841_2_, final double p_192841_4_, final double p_192841_6_, final float p_192841_8_, final int p_192841_9_, final float p_192841_10_) {
        this.renderBeacon(p_192841_2_, p_192841_4_, p_192841_6_, p_192841_8_, p_192841_1_.shouldBeamRender(), p_192841_1_.getBeamSegments(), (double)p_192841_1_.getWorld().getTotalWorldTime());
    }
    
    public void renderBeacon(final double p_188206_1_, final double p_188206_3_, final double p_188206_5_, final double p_188206_7_, final double p_188206_9_, final List<TileEntityBeacon.BeamSegment> p_188206_11_, final double p_188206_12_) {
        if (p_188206_9_ > 0.0 && p_188206_11_.size() > 0) {
            if (Config.isShaders()) {
                Shaders.beginBeacon();
            }
            GlStateManager.alphaFunc(516, 0.1f);
            this.bindTexture(TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM);
            if (p_188206_9_ > 0.0) {
                GlStateManager.disableFog();
                int i = 0;
                for (int j = 0; j < p_188206_11_.size(); ++j) {
                    final TileEntityBeacon.BeamSegment tileentitybeacon$beamsegment = p_188206_11_.get(j);
                    renderBeamSegment(p_188206_1_, p_188206_3_, p_188206_5_, p_188206_7_, p_188206_9_, p_188206_12_, i, tileentitybeacon$beamsegment.getHeight(), tileentitybeacon$beamsegment.getColors());
                    i += tileentitybeacon$beamsegment.getHeight();
                }
                GlStateManager.enableFog();
            }
            if (Config.isShaders()) {
                Shaders.endBeacon();
            }
        }
    }
    
    public static void renderBeamSegment(final double x, final double y, final double z, final double partialTicks, final double textureScale, final double totalWorldTime, final int yOffset, final int height, final float[] colors) {
        renderBeamSegment(x, y, z, partialTicks, textureScale, totalWorldTime, yOffset, height, colors, 0.2, 0.25);
    }
    
    public static void renderBeamSegment(final double x, final double y, final double z, final double partialTicks, final double textureScale, final double totalWorldTime, final int yOffset, final int height, final float[] colors, final double beamRadius, final double glowRadius) {
        final int i = yOffset + height;
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        final double d0 = totalWorldTime + partialTicks;
        final double d2 = (height < 0) ? d0 : (-d0);
        final double d3 = MathHelper.frac(d2 * 0.2 - MathHelper.floor(d2 * 0.1));
        final float f = colors[0];
        final float f2 = colors[1];
        final float f3 = colors[2];
        double d4 = d0 * 0.025 * -1.5;
        double d5 = 0.5 + Math.cos(d4 + 2.356194490192345) * beamRadius;
        double d6 = 0.5 + Math.sin(d4 + 2.356194490192345) * beamRadius;
        double d7 = 0.5 + Math.cos(d4 + 0.7853981633974483) * beamRadius;
        double d8 = 0.5 + Math.sin(d4 + 0.7853981633974483) * beamRadius;
        double d9 = 0.5 + Math.cos(d4 + 3.9269908169872414) * beamRadius;
        double d10 = 0.5 + Math.sin(d4 + 3.9269908169872414) * beamRadius;
        double d11 = 0.5 + Math.cos(d4 + 5.497787143782138) * beamRadius;
        double d12 = 0.5 + Math.sin(d4 + 5.497787143782138) * beamRadius;
        double d13 = 0.0;
        double d14 = 1.0;
        double d15 = -1.0 + d3;
        final double d16 = height * textureScale * (0.5 / beamRadius) + d15;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + d5, y + i, z + d6).tex(1.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d5, y + yOffset, z + d6).tex(1.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d7, y + yOffset, z + d8).tex(0.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d7, y + i, z + d8).tex(0.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d11, y + i, z + d12).tex(1.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d11, y + yOffset, z + d12).tex(1.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d9, y + yOffset, z + d10).tex(0.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d9, y + i, z + d10).tex(0.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d7, y + i, z + d8).tex(1.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d7, y + yOffset, z + d8).tex(1.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d11, y + yOffset, z + d12).tex(0.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d11, y + i, z + d12).tex(0.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d9, y + i, z + d10).tex(1.0, d16).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d9, y + yOffset, z + d10).tex(1.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d5, y + yOffset, z + d6).tex(0.0, d15).color(f, f2, f3, 1.0f).endVertex();
        bufferbuilder.pos(x + d5, y + i, z + d6).tex(0.0, d16).color(f, f2, f3, 1.0f).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(false);
        d4 = 0.5 - glowRadius;
        d5 = 0.5 - glowRadius;
        d6 = 0.5 + glowRadius;
        d7 = 0.5 - glowRadius;
        d8 = 0.5 - glowRadius;
        d9 = 0.5 + glowRadius;
        d10 = 0.5 + glowRadius;
        d11 = 0.5 + glowRadius;
        d12 = 0.0;
        d13 = 1.0;
        d14 = -1.0 + d3;
        d15 = height * textureScale + d14;
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x + d4, y + i, z + d5).tex(1.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d4, y + yOffset, z + d5).tex(1.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d6, y + yOffset, z + d7).tex(0.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d6, y + i, z + d7).tex(0.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d10, y + i, z + d11).tex(1.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d10, y + yOffset, z + d11).tex(1.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d8, y + yOffset, z + d9).tex(0.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d8, y + i, z + d9).tex(0.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d6, y + i, z + d7).tex(1.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d6, y + yOffset, z + d7).tex(1.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d10, y + yOffset, z + d11).tex(0.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d10, y + i, z + d11).tex(0.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d8, y + i, z + d9).tex(1.0, d15).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d8, y + yOffset, z + d9).tex(1.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d4, y + yOffset, z + d5).tex(0.0, d14).color(f, f2, f3, 0.125f).endVertex();
        bufferbuilder.pos(x + d4, y + i, z + d5).tex(0.0, d15).color(f, f2, f3, 0.125f).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
    
    @Override
    public boolean isGlobalRenderer(final TileEntityBeacon te) {
        return true;
    }
}
