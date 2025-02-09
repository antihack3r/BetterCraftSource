/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;
import net.labymod.core.asm.LabyModTransformer;
import net.labymod.main.ModTextures;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

public class CustomLoadingScreen {
    private static CustomLoadingScreen instance;
    private Minecraft mc;
    private ResourceLocation resourceMojangLogo;
    private ResourceLocation resourceLabymodBanner;
    private TextureManager textureManager;
    private DrawUtils drawUtils;
    private int renderedFrames = 0;
    private String[] steps;

    public static void renderInstance() {
        if (instance == null) {
            instance = new CustomLoadingScreen(Minecraft.getMinecraft(), Minecraft.getMinecraft().getTextureManager());
        }
        instance.drawSplashScreen();
    }

    public CustomLoadingScreen(Minecraft mc2, TextureManager textureManager) {
        this.mc = mc2;
        this.textureManager = textureManager;
        this.drawUtils = new DrawUtils();
        FontRenderer fontRenderer = new FontRenderer(mc2.gameSettings, new ResourceLocation("textures/font/ascii.png"), textureManager, false);
        fontRenderer.setUnicodeFlag(mc2.isUnicode());
        fontRenderer.setBidiFlag(mc2.getLanguageManager().isCurrentLanguageBidirectional());
        ((IReloadableResourceManager)mc2.getResourceManager()).registerReloadListener(fontRenderer);
        this.drawUtils.setFontRenderer(fontRenderer);
        this.loadResource();
    }

    private void loadResource() {
        try {
            Class<?> clazz = this.mc.getClass();
            Field field = clazz.getDeclaredField(LabyModTransformer.getMappingImplementation().getMCDefaultResourcePack());
            field.setAccessible(true);
            DefaultResourcePack mcDefaultResourcePack = (DefaultResourcePack)field.get(this.mc);
            InputStream inputstreamMojang = mcDefaultResourcePack.getInputStream(ModTextures.TITLE_MOJANG_BANNER);
            this.resourceMojangLogo = this.textureManager.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstreamMojang)));
            InputStream inputstreamLabyModBanner = mcDefaultResourcePack.getInputStream(ModTextures.TITLE_LABYMOD_BANNER);
            this.resourceLabymodBanner = this.textureManager.getDynamicTextureLocation("logo_lm_banner", new DynamicTexture(ImageIO.read(inputstreamLabyModBanner)));
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/minecraft/labymod/data/steps.titles");
            DataInputStream dis = new DataInputStream(inputStream);
            this.steps = new String[dis.readInt()];
            int i2 = 0;
            while (i2 < this.steps.length) {
                byte[] bytes = new byte[dis.readInt()];
                dis.read(bytes);
                this.steps[i2] = new String(bytes);
                ++i2;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void drawSplashScreen() {
        ++this.renderedFrames;
        if (this.steps == null) {
            return;
        }
        int width = this.drawUtils.getWidth();
        int height = this.drawUtils.getHeight();
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, width, height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.drawUtils.drawRectangle(0, 0, width, height, -1);
        this.textureManager.bindTexture(this.resourceMojangLogo);
        Gui.drawModalRectWithCustomSizedTexture((width - 256) / 2, (height - 256) / 2, 0.0f, 0.0f, 256, 256, 256.0f, 256.0f);
        double percent = 100.0 / (double)this.steps.length * (double)this.renderedFrames;
        this.drawProgressbar((double)width / 2.0 - 100.0, height / 2 + 30, 200.0, 12.0, percent);
        this.textureManager.bindTexture(this.resourceLabymodBanner);
        int bannerLength = 140;
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 70, height / 2 + 45 + 10, 0.0f, 0.0f, 140, 30, 140.0f, 30.0f);
        String text = this.renderedFrames < this.steps.length ? this.steps[this.renderedFrames] : "";
        this.drawUtils.getFontRenderer().drawString(text, (width - this.drawUtils.getStringWidth(text)) / 2, height / 2 + 45, ModColor.toRGB(0, 0, 0, 255));
        try {
            this.mc.updateDisplay();
        }
        catch (NullPointerException error) {
            error.printStackTrace();
        }
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
    }

    private void drawProgressbar(double x2, double y2, double width, double height, double percent) {
        if (percent > 100.0) {
            percent = 100.0;
        }
        this.drawRect(x2 - 1.0, y2 - 1.0, x2 + width + 1.0, y2 + height + 1.0, ModColor.toRGB(0, 0, 0, 255));
        this.drawRect(x2, y2, x2 + width, y2 + height, ModColor.toRGB(200, 200, 200, 255));
        this.drawRect(x2, y2, (int)(x2 + width / 100.0 * percent), y2 + height, ModColor.toRGB(0, 143, 232, 255));
        this.drawUtils.drawCenteredString(String.valueOf(ModColor.cl("f")) + (int)percent + "%", x2 + width / 2.0, y2 + height / 2.0 - 4.0);
    }

    private void drawRect(double left, double top, double right, double bottom, int color) {
        Gui.drawRect((int)left, (int)top, (int)right, (int)bottom, color);
    }
}

