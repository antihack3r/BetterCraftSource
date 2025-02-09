/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticSnoxhEyes
extends CosmeticBase {
    SnoxhModel snoxhModel;

    public CosmeticSnoxhEyes(RenderPlayer playerRenderer) {
        super(playerRenderer);
        this.snoxhModel = new SnoxhModel(playerRenderer);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.snoxhModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public int getId() {
        return 19;
    }

    class SnoxhModel
    extends CosmeticModelBase {
        private int shadowOffset;
        private ResourceLocation resourceLocation;
        private ResourceLocation resourceLocation2;

        public SnoxhModel(RenderPlayer player) {
            super(player);
            this.shadowOffset = -6942612;
            this.resourceLocation = new ResourceLocation("client/cosmetic/mask.png");
            this.resourceLocation2 = new ResourceLocation("client/cosmetic/eye.png");
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float tickValue, float firstRotationX, float secondRotationX, float scale) {
            long i2 = Minecraft.getMinecraft().thePlayer.getUniqueID().getLeastSignificantBits() / 1000L;
            int j2 = 132;
            int k2 = 244;
            double d0 = (double)j2 + Math.abs(Math.cos(tickValue / 30.0f) * (double)(k2 - j2));
            int l2 = 699;
            int i1 = (int)((double)i2 / (double)this.shadowOffset) - l2;
            int j1 = 2;
            double d1 = 0.9;
            int k1 = (int)d0;
            double d2 = 0.3;
            float f2 = (float)(0.0 + d1 / (double)i1 * Math.cos(Math.toRadians(45.0))) / (float)i1;
            float f1 = (float)(0.0 + d1 / (double)i1 * Math.sin(Math.toRadians(45.0))) / (float)i1;
            float f22 = (float)(0.0 + d1 / (double)i1 * Math.cos(Math.toRadians(22.5))) / (float)i1;
            float f3 = (float)(0.0 + d1 / (double)i1 * Math.sin(Math.toRadians(22.5))) / (float)i1;
            GlStateManager.pushMatrix();
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0, 0.25, 0.0);
            }
            GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(0.8, 0.8, 0.8);
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.color(0.8f, 0.8f, 0.8f);
            double d3 = 0.1;
            GlStateManager.scale(d3 * (double)i1, d3 * (double)i1, d3 * (double)i1);
            GlStateManager.translate(-0.32 / d3, -0.635 / d3, -0.315 / d3);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.popMatrix();
            double d4 = 0.1;
            GlStateManager.scale(d4, d4, d4);
            GlStateManager.translate(-0.15 / d4, -0.275 / d4, -0.323 / d4);
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation2);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 772, 1, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrendereradapter = tessellator.getWorldRenderer();
            int l1 = 0;
            while (l1 < 2) {
                GlStateManager.translate(l1 * 3, 0.0f, 0.0f);
                worldrendereradapter.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i22 = 0;
                while (i22 < j1) {
                    worldrendereradapter.pos(d2 + 0.0, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2 + d1, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f22, f3, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f2, f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + 0.0, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2 + (double)f2, f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f3, f22, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + 0.0, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(-d1 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f22) - d2, f3, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f2) - d2, f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos((double)(-f2) - d2, f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f3) - d2, f22, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + 0.0, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2 + d1, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f22, -f3, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f2, -f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + 0.0, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2 + (double)f2, -f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + (double)f3, -f22, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2 + 0.0, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(-d1 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f22) - d2, -f3, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f2) - d2, -f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos((double)(-f2) - d2, -f1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos((double)(-f3) - d2, -f22, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(0.0 - d2, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(-d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2, -d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, k1).endVertex();
                    worldrendereradapter.pos(-d2, 0.0, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(-d2, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    worldrendereradapter.pos(d2, d1, 0.0).tex(1.0, 1.0).color(k1, k1, k1, 0).endVertex();
                    ++i22;
                }
                tessellator.draw();
                ++l1;
            }
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}

