/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.labymod.core.LabyModCore;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.main.LabyMod;
import net.labymod.main.LabyModForge;
import net.labymod.main.Source;
import net.labymod.splash.SplashLoader;
import net.labymod.splash.splashdates.SplashDate;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class ModGuiMainMenu
extends GuiScreen
implements GuiYesNoCallback {
    public static final String field_96138_a;
    private static final Logger logger;
    private static final Random RANDOM;
    private static final ResourceLocation splashTexts;
    private static final ResourceLocation minecraftTitleTextures;
    private static final ResourceLocation[] titlePanoramaPaths;
    private final Object threadLock;
    private final float updateCounter;
    private final boolean vanillaForge;
    private String splashText;
    private GuiButton buttonResetDemo;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;
    private long startDebugClicking;

    static {
        logger = LogManager.getLogger();
        RANDOM = new Random();
        splashTexts = new ResourceLocation("texts/splashes.txt");
        minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
        titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
        field_96138_a = "Please click " + (Object)((Object)EnumChatFormatting.UNDERLINE) + "here" + (Object)((Object)EnumChatFormatting.RESET) + " for more information.";
    }

    public ModGuiMainMenu() {
        block18: {
            this.threadLock = new Object();
            this.startDebugClicking = -1L;
            this.vanillaForge = !LabyModForge.isForge();
            this.openGLWarning2 = field_96138_a;
            this.splashText = "missingno";
            BufferedReader bufferedreader = null;
            try {
                try {
                    String s2;
                    ArrayList<String> list = Lists.newArrayList();
                    bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
                    while ((s2 = bufferedreader.readLine()) != null) {
                        if ((s2 = s2.trim()).isEmpty()) continue;
                        list.add(s2);
                    }
                    if (!list.isEmpty()) {
                        do {
                            this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
                        } while (this.splashText.hashCode() == 125780783);
                    }
                }
                catch (IOException iOException) {
                    if (bufferedreader != null) {
                        try {
                            bufferedreader.close();
                        }
                        catch (IOException iOException2) {}
                    }
                    break block18;
                }
            }
            catch (Throwable throwable) {
                if (bufferedreader != null) {
                    try {
                        bufferedreader.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                throw throwable;
            }
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    @Override
    public void updateScreen() {
        ++this.panoramaTimer;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
        Consumer<SplashLoader> consumer = new Consumer<SplashLoader>(){

            @Override
            public void accept(SplashLoader accepted) {
                SplashDate[] splashDateArray = SplashLoader.getLoader().getEntries().getSplashDates();
                int n2 = splashDateArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    SplashDate date = splashDateArray[n3];
                    if (calendar.get(2) + 1 == date.getMonth() && calendar.get(5) == date.getDay()) {
                        ModGuiMainMenu.this.splashText = date.getDisplayString();
                    }
                    ++n3;
                }
            }
        };
        SplashLoader loader = SplashLoader.getLoader();
        if (loader.getEntries() == null) {
            loader.setLoadListener(consumer);
        } else {
            consumer.accept(loader);
        }
        int j2 = height / 4 + 48;
        if (this.mc.isDemo()) {
            this.addDemoButtons(j2, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(j2, 24);
        }
        this.buttonList.add(new GuiButton(0, width / 2 - 100, j2 + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, width / 2 + 2, j2 + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, width / 2 - 124, j2 + 72 + 12));
        Object object = this.threadLock;
        synchronized (object) {
            this.field_92023_s = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.openGLWarning1);
            this.field_92024_r = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.openGLWarning2);
            int k2 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (width - k2) / 2;
            this.field_92021_u = ((GuiButton)this.buttonList.get((int)0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k2;
            this.field_92019_w = this.field_92021_u + 24;
        }
        this.mc.setConnectedToRealms(false);
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        boolean quickPlayEnabled;
        this.buttonList.add(new GuiButton(1, width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
        String quickPlay = this.mc.gameSettings.lastServer;
        String realms = "Minecraft Realms";
        boolean bl2 = quickPlayEnabled = LabyMod.getSettings().quickPlay && quickPlay != null && !quickPlay.isEmpty();
        if (quickPlayEnabled && quickPlay.length() > 16) {
            quickPlay = quickPlay.substring(0, 16);
        }
        if (quickPlayEnabled && !this.vanillaForge) {
            this.buttonList.add(new GuiButton(6, width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, quickPlay));
        } else if (!quickPlayEnabled && !this.vanillaForge) {
            this.buttonList.add(new GuiButton(14, width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("Minecraft Realms", new Object[0])));
        } else if (quickPlayEnabled) {
            this.buttonList.add(new GuiButton(6, width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, quickPlay));
            this.buttonList.add(new GuiButton(14, width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("Minecraft Realms", new Object[0])));
        } else {
            this.buttonList.add(new GuiButton(14, width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("Minecraft Realms", new Object[0])));
        }
        if (!this.vanillaForge) {
            this.buttonList.add(new GuiButton(125, width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods", new Object[0])));
        }
    }

    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList.add(new GuiButton(11, width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
        this.buttonResetDemo = new GuiButton(12, width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0]));
        this.buttonList.add(this.buttonResetDemo);
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
        if (worldinfo == null) {
            this.buttonResetDemo.enabled = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        ISaveFormat isaveformat;
        WorldInfo worldinfo;
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new ModGuiMultiplayer(this));
        }
        if (button.id == 14) {
            this.switchToRealms();
        }
        if (button.id == 4) {
            this.mc.shutdown();
        }
        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }
        if (button.id == 12 && (worldinfo = (isaveformat = this.mc.getSaveLoader()).getWorldInfo("Demo_World")) != null) {
            GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
            this.mc.displayGuiScreen(guiyesno);
        }
        if (button.id == 125 && !this.vanillaForge) {
            try {
                this.mc.displayGuiScreen((GuiScreen)Class.forName("net.minecraftforge.fml.client.GuiModList").getConstructor(GuiScreen.class).newInstance(this));
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (button.id == 6) {
            LabyMod.getInstance().connectToServer(this.mc.gameSettings.lastServer);
        }
    }

    private void switchToRealms() {
        RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(this);
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (result && id2 == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id2 == 13) {
            if (result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                }
                catch (Throwable throwable) {
                    logger.error("Couldn't open link", throwable);
                }
            }
            this.mc.displayGuiScreen(this);
        }
    }

    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0f, 1.0f, 0.05f, 10.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i2 = 8;
        int j2 = 0;
        while (j2 < i2 * i2) {
            GlStateManager.pushMatrix();
            float f2 = ((float)(j2 % i2) / (float)i2 - 0.5f) / 64.0f;
            float f22 = ((float)(j2 / i2) / (float)i2 - 0.5f) / 64.0f;
            float f3 = 0.0f;
            GlStateManager.translate(f2, f22, 0.0f);
            GlStateManager.rotate(LabyModCore.getMath().sin(((float)this.panoramaTimer + p_73970_3_) / 400.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(-((float)this.panoramaTimer + p_73970_3_) * 0.1f, 0.0f, 1.0f, 0.0f);
            int k2 = 0;
            while (k2 < 6) {
                GlStateManager.pushMatrix();
                if (k2 == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (k2 == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                if (k2 == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (k2 == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (k2 == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                this.mc.getTextureManager().bindTexture(titlePanoramaPaths[k2]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l2 = 255 / (j2 + 1);
                worldrenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, l2).endVertex();
                worldrenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, l2).endVertex();
                worldrenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, l2).endVertex();
                worldrenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, l2).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
                ++k2;
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
            ++j2;
        }
        worldrenderer.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    private void rotateAndBlurSkybox(float p_73968_1_) {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i2 = 3;
        int j2 = 0;
        while (j2 < i2) {
            float f2 = 1.0f / (float)(j2 + 1);
            int k2 = width;
            int l2 = height;
            float f22 = (float)(j2 - i2 / 2) / 256.0f;
            worldrenderer.pos(k2, l2, this.zLevel).tex(0.0f + f22, 1.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(k2, 0.0, this.zLevel).tex(1.0f + f22, 1.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(0.0, 0.0, this.zLevel).tex(1.0f + f22, 0.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(0.0, l2, this.zLevel).tex(0.0f + f22, 0.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            ++j2;
        }
        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = width > height ? 120.0f / (float)width : 120.0f / (float)height;
        float f22 = (float)height * f2 / 256.0f;
        float f3 = (float)width * f2 / 256.0f;
        int i2 = width;
        int j2 = height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, j2, this.zLevel).tex(0.5f - f22, 0.5f + f3).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(i2, j2, this.zLevel).tex(0.5f - f22, 0.5f - f3).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(i2, 0.0, this.zLevel).tex(0.5f + f22, 0.5f - f3).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(0.0, 0.0, this.zLevel).tex(0.5f + f22, 0.5f + f3).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        int i2 = 274;
        int j2 = width / 2 - 137;
        int k2 = 30;
        this.drawGradientRect(0, 0, width, height, -2130706433, 0xFFFFFF);
        this.drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        if ((double)this.updateCounter < 1.0E-4) {
            this.drawTexturedModalRect(j2 + 0, 30, 0, 0, 99, 44);
            this.drawTexturedModalRect(j2 + 99, 30, 129, 0, 27, 44);
            this.drawTexturedModalRect(j2 + 99 + 26, 30, 126, 0, 3, 44);
            this.drawTexturedModalRect(j2 + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.drawTexturedModalRect(j2 + 155, 30, 0, 45, 155, 44);
        } else {
            this.drawTexturedModalRect(j2 + 0, 30, 0, 0, 155, 44);
            this.drawTexturedModalRect(j2 + 155, 30, 0, 45, 155, 44);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2 + 90, 70.0f, 0.0f);
        GlStateManager.rotate(-20.0f, 0.0f, 0.0f, 1.0f);
        float f2 = 1.8f - LabyModCore.getMath().abs(LabyModCore.getMath().sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0f * (float)Math.PI * 2.0f) * 0.1f);
        f2 = f2 * 100.0f / (float)(LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.splashText) + 32);
        GlStateManager.scale(f2, f2, f2);
        ModGuiMainMenu.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), this.splashText, 0, -8, -256);
        GlStateManager.popMatrix();
        String s2 = "Minecraft " + Source.ABOUT_MC_VERSION;
        if (this.mc.isDemo()) {
            s2 = String.valueOf(s2) + " Demo";
        }
        String dc2 = this.startDebugClicking != -1L && this.startDebugClicking + 1000L < System.currentTimeMillis() ? ModColor.cl("e") : "";
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(dc2) + "LabyMod " + "3.6.6" + " ", 2, height - 10, -1);
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), s2, 2, height - 20, -1);
        String s22 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), "Copyright Mojang AB. Do not distribute!", width - LabyModCore.getMinecraft().getFontRenderer().getStringWidth("Copyright Mojang AB. Do not distribute!") - 2, height - 10, -1);
        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
            ModGuiMainMenu.drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 0x55200000);
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), this.openGLWarning2, (width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get((int)0)).yPosition - 12, -1);
        }
        SplashLoader.getLoader().render(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        SplashLoader.getLoader().onClick(mouseX, mouseY);
        this.startDebugClicking = mouseX < 1500 && mouseY > LabyMod.getInstance().getDrawUtils().getHeight() - 20 && mouseButton == 0 ? System.currentTimeMillis() : -1L;
        Object object = this.threadLock;
        synchronized (object) {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink((GuiYesNoCallback)this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (mouseX < 1500 && mouseY > LabyMod.getInstance().getDrawUtils().getHeight() - 20 && state == 0 && this.startDebugClicking != -1L && this.startDebugClicking + 1000L < System.currentTimeMillis()) {
            Debug.openDebugConsole();
        }
        this.startDebugClicking = -1L;
    }
}

