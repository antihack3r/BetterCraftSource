/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer
extends TileEntitySpecialRenderer<TileEntityBanner> {
    private static final Map<String, TimedBannerTexture> DESIGNS = Maps.newHashMap();
    private static final ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");
    private ModelBanner bannerModel = new ModelBanner();

    @Override
    public void renderTileEntityAt(TileEntityBanner te2, double x2, double y2, double z2, float partialTicks, int destroyStage) {
        boolean flag = te2.getWorld() != null;
        boolean flag1 = !flag || te2.getBlockType() == Blocks.standing_banner;
        int i2 = flag ? te2.getBlockMetadata() : 0;
        long j2 = flag ? te2.getWorld().getTotalWorldTime() : 0L;
        GlStateManager.pushMatrix();
        float f2 = 0.6666667f;
        if (flag1) {
            GlStateManager.translate((float)x2 + 0.5f, (float)y2 + 0.75f * f2, (float)z2 + 0.5f);
            float f1 = (float)(i2 * 360) / 16.0f;
            GlStateManager.rotate(-f1, 0.0f, 1.0f, 0.0f);
            this.bannerModel.bannerStand.showModel = true;
        } else {
            float f22 = 0.0f;
            if (i2 == 2) {
                f22 = 180.0f;
            }
            if (i2 == 4) {
                f22 = 90.0f;
            }
            if (i2 == 5) {
                f22 = -90.0f;
            }
            GlStateManager.translate((float)x2 + 0.5f, (float)y2 - 0.25f * f2, (float)z2 + 0.5f);
            GlStateManager.rotate(-f22, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0f, -0.3125f, -0.4375f);
            this.bannerModel.bannerStand.showModel = false;
        }
        BlockPos blockpos = te2.getPos();
        float f3 = (float)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + (float)j2 + partialTicks;
        this.bannerModel.bannerSlate.rotateAngleX = (-0.0125f + 0.01f * MathHelper.cos(f3 * (float)Math.PI * 0.02f)) * (float)Math.PI;
        GlStateManager.enableRescaleNormal();
        ResourceLocation resourcelocation = this.func_178463_a(te2);
        if (resourcelocation != null) {
            this.bindTexture(resourcelocation);
            GlStateManager.pushMatrix();
            GlStateManager.scale(f2, -f2, -f2);
            this.bannerModel.renderBanner();
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private ResourceLocation func_178463_a(TileEntityBanner bannerObj) {
        String s2 = bannerObj.getPatternResourceLocation();
        if (s2.isEmpty()) {
            return null;
        }
        TimedBannerTexture tileentitybannerrenderer$timedbannertexture = DESIGNS.get(s2);
        if (tileentitybannerrenderer$timedbannertexture == null) {
            if (DESIGNS.size() >= 256) {
                long i2 = System.currentTimeMillis();
                Iterator<String> iterator = DESIGNS.keySet().iterator();
                while (iterator.hasNext()) {
                    String s1 = iterator.next();
                    TimedBannerTexture tileentitybannerrenderer$timedbannertexture1 = DESIGNS.get(s1);
                    if (i2 - tileentitybannerrenderer$timedbannertexture1.systemTime <= 60000L) continue;
                    Minecraft.getMinecraft().getTextureManager().deleteTexture(tileentitybannerrenderer$timedbannertexture1.bannerTexture);
                    iterator.remove();
                }
                if (DESIGNS.size() >= 256) {
                    return null;
                }
            }
            List<TileEntityBanner.EnumBannerPattern> list1 = bannerObj.getPatternList();
            List<EnumDyeColor> list = bannerObj.getColorList();
            ArrayList<String> list2 = Lists.newArrayList();
            for (TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern : list1) {
                list2.add("textures/entity/banner/" + tileentitybanner$enumbannerpattern.getPatternName() + ".png");
            }
            tileentitybannerrenderer$timedbannertexture = new TimedBannerTexture();
            tileentitybannerrenderer$timedbannertexture.bannerTexture = new ResourceLocation(s2);
            Minecraft.getMinecraft().getTextureManager().loadTexture(tileentitybannerrenderer$timedbannertexture.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, list2, list));
            DESIGNS.put(s2, tileentitybannerrenderer$timedbannertexture);
        }
        tileentitybannerrenderer$timedbannertexture.systemTime = System.currentTimeMillis();
        return tileentitybannerrenderer$timedbannertexture.bannerTexture;
    }

    static class TimedBannerTexture {
        public long systemTime;
        public ResourceLocation bannerTexture;

        private TimedBannerTexture() {
        }
    }
}
