/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.partner;

import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.core.WorldRendererAdapter;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticSnoxh
extends CosmeticRenderer<CosmeticSnoxhData> {
    public static final int ID = 32;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticSnoxhData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        int minColor = Math.min(cosmeticData.getBrightness(), 0);
        int maxColor = cosmeticData.getBrightness();
        float animation = ((float)minColor + (float)Math.abs(Math.cos(tickValue / 30.0f) * (double)(maxColor - minColor))) / 255.0f;
        int brightness = 2;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        if (entityIn.isSneaking()) {
            double sneakRotate = (double)entityIn.rotationPitch / 70000.0;
            sneakRotate = Math.max(0.0, sneakRotate);
            GlStateManager.translate(0.0, 0.062, 0.0);
            GlStateManager.translate(0.0, sneakRotate, -sneakRotate);
        }
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.0625, 0.0625, 0.06);
        GlStateManager.translate(-4.0, -8.0, -4.17);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_SNOXH_EYE);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 772, 1, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        int x2 = cosmeticData.getX();
        int y2 = cosmeticData.getY();
        int width = cosmeticData.getWidth();
        int height = cosmeticData.getHeight();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableLighting();
        boolean overlappingLeft = x2 + width - 1 == 3;
        int i2 = 0;
        while (i2 < 2) {
            int b2 = 0;
            while (b2 < 2) {
                if (!(i2 != 0 ? !cosmeticData.isLeftVisible() : !cosmeticData.isRightVisible())) {
                    this.renderGlowingBorder(i2 == 0 ? x2 : 8 - x2 - width, y2, width, height, cosmeticData.getColor(), animation, overlappingLeft, i2 == 0);
                }
                ++b2;
            }
            GlStateManager.translate(0.0, 0.0, -0.01);
            ++i2;
        }
        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderGlowingBorder(int x2, int y2, int width, int height, Color color, float alpha, boolean overlapping, boolean isLeftSide) {
        float r2 = (float)color.getRed() / 255.0f;
        float g2 = (float)color.getGreen() / 255.0f;
        float b2 = (float)color.getBlue() / 255.0f;
        float middleAlpha = 0.4f;
        float radius = 0.6f;
        float basis = 0.0f;
        float middleLeft = (float)x2 + 0.0f;
        float middleTop = (float)y2 + 0.0f;
        float middleRight = (float)(x2 + width) - 0.0f;
        float middleBottom = (float)(y2 + height) - 0.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.drawGradient(middleLeft, middleTop - 0.6f, 0.0f, middleRight, middleTop - 0.6f, 0.0f, middleRight, middleTop, alpha, middleLeft, middleTop, alpha, r2, g2, b2);
        this.drawGradient(middleRight, middleTop, alpha, middleRight + 0.6f, middleTop, 0.0f, middleRight + 0.6f, middleBottom, 0.0f, middleRight, middleBottom, alpha, r2, g2, b2);
        this.drawGradient(middleLeft, middleTop, alpha, middleLeft - 0.6f, middleTop, 0.0f, middleLeft - 0.6f, middleBottom, 0.0f, middleLeft, middleBottom, alpha, r2, g2, b2);
        this.drawGradient(middleLeft, middleBottom + 0.6f, 0.0f, middleRight, middleBottom + 0.6f, 0.0f, middleRight, middleBottom, alpha, middleLeft, middleBottom, alpha, r2, g2, b2);
        this.drawGradient(middleLeft, middleTop, 0.4f, middleRight, middleTop, 0.4f, middleRight, middleBottom, 0.4f, middleLeft, middleBottom, 0.4f, r2, g2, b2);
        double cornerRadiusWidth = (double)0.6f * Math.cos(Math.toRadians(45.0));
        double cornerRadiusHeight = (double)0.6f * Math.sin(Math.toRadians(45.0));
        double lowCornerRadiusWidth = (double)0.6f * Math.cos(Math.toRadians(22.5));
        double lowCornerRadiusHeight = (double)0.6f * Math.sin(Math.toRadians(22.5));
        double highCornerRadiusWidth = (double)0.6f * Math.cos(Math.toRadians(67.5));
        double highCornerRadiusHeight = (double)0.6f * Math.sin(Math.toRadians(67.5));
        if (!overlapping || !(isLeftSide ? !overlapping : overlapping)) {
            this.drawGradient(middleLeft - 0.6f, middleTop, 0.0f, (double)middleLeft - lowCornerRadiusWidth, (double)middleTop - lowCornerRadiusHeight, 0.0f, (double)middleLeft - cornerRadiusWidth, (double)middleTop - cornerRadiusHeight, 0.0f, middleLeft, middleTop, alpha, r2, g2, b2);
            this.drawGradient((double)middleLeft - cornerRadiusWidth, (double)middleTop - cornerRadiusHeight, 0.0f, (double)middleLeft - highCornerRadiusWidth, (double)middleTop - highCornerRadiusHeight, 0.0f, middleLeft, middleTop - 0.6f, 0.0f, middleLeft, middleTop, alpha, r2, g2, b2);
        }
        if (!overlapping || !(isLeftSide ? overlapping : !overlapping)) {
            this.drawGradient(middleRight + 0.6f, middleTop, 0.0f, (double)middleRight + lowCornerRadiusWidth, (double)middleTop - lowCornerRadiusHeight, 0.0f, (double)middleRight + cornerRadiusWidth, (double)middleTop - cornerRadiusHeight, 0.0f, middleRight, middleTop, alpha, r2, g2, b2);
            this.drawGradient((double)middleRight + cornerRadiusWidth, (double)middleTop - cornerRadiusHeight, 0.0f, (double)middleRight + highCornerRadiusWidth, (double)middleTop - highCornerRadiusHeight, 0.0f, middleRight, middleTop - 0.6f, 0.0f, middleRight, middleTop, alpha, r2, g2, b2);
        }
        if (!overlapping || !(isLeftSide ? !overlapping : overlapping)) {
            this.drawGradient(middleLeft - 0.6f, middleBottom, 0.0f, (double)middleLeft - lowCornerRadiusWidth, (double)middleBottom + lowCornerRadiusHeight, 0.0f, (double)middleLeft - cornerRadiusWidth, (double)middleBottom + cornerRadiusHeight, 0.0f, middleLeft, middleBottom, alpha, r2, g2, b2);
            this.drawGradient((double)middleLeft - cornerRadiusWidth, (double)middleBottom + cornerRadiusHeight, 0.0f, (double)middleLeft - highCornerRadiusWidth, (double)middleBottom + highCornerRadiusHeight, 0.0f, middleLeft, middleBottom + 0.6f, 0.0f, middleLeft, middleBottom, alpha, r2, g2, b2);
        }
        if (!overlapping || !(isLeftSide ? overlapping : !overlapping)) {
            this.drawGradient(middleRight + 0.6f, middleBottom, 0.0f, (double)middleRight + lowCornerRadiusWidth, (double)middleBottom + lowCornerRadiusHeight, 0.0f, (double)middleRight + cornerRadiusWidth, (double)middleBottom + cornerRadiusHeight, 0.0f, middleRight, middleBottom, alpha, r2, g2, b2);
            this.drawGradient((double)middleRight + cornerRadiusWidth, (double)middleBottom + cornerRadiusHeight, 0.0f, (double)middleRight + highCornerRadiusWidth, (double)middleBottom + highCornerRadiusHeight, 0.0f, middleRight, middleBottom + 0.6f, 0.0f, middleRight, middleBottom, alpha, r2, g2, b2);
        }
        tessellator.draw();
    }

    private void drawGradient(double x1, double y1, float a1, double x2, double y2, float a2, double x3, double y3, float a3, double x4, double y4, float a4, float r2, float g2, float b2) {
        WorldRendererAdapter worldrenderer = LabyModCore.getWorldRenderer();
        worldrenderer.pos(x1, y1, 0.0).tex(1.0, 1.0).color(r2, g2, b2, a1).endVertex();
        worldrenderer.pos(x2, y2, 0.0).tex(1.0, 1.0).color(r2, g2, b2, a2).endVertex();
        worldrenderer.pos(x3, y3, 0.0).tex(1.0, 1.0).color(r2, g2, b2, a3).endVertex();
        worldrenderer.pos(x4, y4, 0.0).tex(1.0, 1.0).color(r2, g2, b2, a4).endVertex();
    }

    @Override
    public int getCosmeticId() {
        return 32;
    }

    @Override
    public String getCosmeticName() {
        return "Snoxh Eyes";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticSnoxhData
    extends CosmeticData {
        private int x = 1;
        private int y = 4;
        private int width = 2;
        private int height = 1;
        private Color color = Color.WHITE;
        private boolean leftVisible = true;
        private boolean rightVisible = true;
        private int brightness = 120;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            Exception exception = null;
            try {
                this.x = Integer.parseInt(data[0]);
                this.y = Integer.parseInt(data[1]);
                this.width = Integer.parseInt(data[2]);
                this.height = Integer.parseInt(data[3]);
                this.leftVisible = Integer.parseInt(data[4]) == 1;
                this.rightVisible = Integer.parseInt(data[5]) == 1;
                this.color = Color.decode("#" + data[6]);
                this.brightness = Integer.parseInt(data[7]);
            }
            catch (Exception e2) {
                exception = e2;
            }
            this.x = Math.min(this.x, 7);
            this.y = Math.min(this.y, 7);
            this.width = Math.min(this.width, 4);
            this.height = Math.min(this.height, 4);
            this.x = Math.max(this.x, 0);
            this.y = Math.max(this.y, 0);
            this.width = Math.max(this.width, 1);
            this.height = Math.max(this.height, 1);
            this.brightness = Math.min(this.brightness, 160);
            this.brightness = Math.max(this.brightness, 0);
            if (exception != null) {
                throw exception;
            }
        }

        @Override
        public void init(User user) {
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public Color getColor() {
            return this.color;
        }

        public boolean isLeftVisible() {
            return this.leftVisible;
        }

        public boolean isRightVisible() {
            return this.rightVisible;
        }

        public int getBrightness() {
            return this.brightness;
        }
    }
}

