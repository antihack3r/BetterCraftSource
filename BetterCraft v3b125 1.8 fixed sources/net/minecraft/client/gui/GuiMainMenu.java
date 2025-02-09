/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import me.nzxtercode.bettercraft.client.gui.GuiPortScanner;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAltManager;
import me.nzxtercode.bettercraft.client.misc.irc.GuiIRC;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javapluginapi.team.JavaPluginApi;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu
extends GuiScreen
implements GuiYesNoCallback {
    private static final AtomicInteger field_175373_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    private float updateCounter;
    private String splashText;
    private GuiButton buttonResetDemo;
    private int panoramaTimer;
    private DynamicTexture viewportTexture;
    private boolean field_175375_v;
    private final Object threadLock;
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + (Object)((Object)EnumChatFormatting.UNDERLINE) + "here" + (Object)((Object)EnumChatFormatting.RESET) + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;
    private GuiButton realmsButton;
    private boolean field_183502_L;
    private GuiScreen field_183503_M;
    private GuiButton modButton;
    private GuiScreen modUpdateNotification;

    public GuiMainMenu() {
        block18: {
            this.field_175375_v = true;
            this.threadLock = new Object();
            this.openGLWarning2 = field_96138_a;
            this.field_183502_L = false;
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

    private boolean func_183501_a() {
        return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.field_183503_M != null;
    }

    @Override
    public void updateScreen() {
        ++this.panoramaTimer;
        if (this.func_183501_a()) {
            this.field_183503_M.updateScreen();
        }
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
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
        int i2 = 24;
        int j2 = height / 4 + 48;
        if (this.mc.isDemo()) {
            this.addDemoButtons(j2, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(j2, 24);
        }
        int buttonHeight = Math.min(height / 20, 20);
        this.buttonList.add(new GuiButton(0, 5, height / 2 - (buttonHeight + 2), width / 5, buttonHeight, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, 5, height / 2 + 2, width / 5, buttonHeight, "Altmanager"));
        this.buttonList.add(new GuiButton(8, 5, height / 2 + buttonHeight + 6, width / 5, buttonHeight, "Chat"));
        this.buttonList.add(new GuiButton(7, 5, height / 2 + buttonHeight * 2 + 10, width / 5, buttonHeight, "Settings"));
        this.buttonList.add(new GuiButton(20, 5, height / 2 + buttonHeight * 3 + 14, width / 5, buttonHeight, "Changelog"));
        Object object = this.threadLock;
        synchronized (object) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k2 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (width - k2) / 2;
            this.field_92021_u = ((GuiButton)this.buttonList.get((int)0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + k2;
            this.field_92019_w = this.field_92021_u + 24;
        }
        this.mc.setConnectedToRealms(false);
        if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.field_183502_L) {
            RealmsBridge realmsbridge = new RealmsBridge();
            this.field_183503_M = realmsbridge.getNotificationScreen(this);
            this.field_183502_L = true;
        }
        if (this.func_183501_a()) {
            this.field_183503_M.setGuiSize(width, height);
            this.field_183503_M.initGui();
        }
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        int buttonHeight = Math.min(height / 20, 20);
        this.buttonList.add(new GuiButton(1, 5, height / 2 - (buttonHeight * 3 + 10), width / 5, buttonHeight, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, 5, height / 2 - (buttonHeight * 2 + 6), width / 5, buttonHeight, I18n.format("menu.multiplayer", new Object[0])));
        if (Reflector.GuiModList_Constructor.exists()) {
            this.realmsButton = new GuiButton(14, width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online", new Object[0]).replace("Minecraft", "").trim());
            this.buttonList.add(this.realmsButton);
            this.modButton = new GuiButton(6, width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("fml.menu.mods", new Object[0]));
            this.buttonList.add(this.modButton);
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
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 14 && this.realmsButton.visible) {
            this.switchToRealms();
        }
        if (button.id == 4) {
            this.mc.displayGuiScreen(new GuiAltManager(this));
        }
        if (button.id == 7) {
            this.mc.displayGuiScreen(new GuiUISettings(this));
        }
        if (button.id == 8) {
            this.mc.displayGuiScreen(new GuiIRC(this));
        }
        if (button.id == 20) {
            this.mc.displayGuiScreen(new GuiPortScanner(this));
        }
        if (button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
            this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
        }
        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }
        if (button.id == 12 && (worldinfo = (isaveformat = this.mc.getSaveLoader()).getWorldInfo("Demo_World")) != null) {
            GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
            this.mc.displayGuiScreen(guiyesno);
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
        int j2 = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            j2 = custompanoramaproperties.getBlur1();
        }
        int k2 = 0;
        while (k2 < j2) {
            GlStateManager.pushMatrix();
            float f2 = ((float)(k2 % i2) / (float)i2 - 0.5f) / 64.0f;
            float f1 = ((float)(k2 / i2) / (float)i2 - 0.5f) / 64.0f;
            float f22 = 0.0f;
            GlStateManager.translate(f2, f1, f22);
            GlStateManager.rotate(MathHelper.sin(((float)this.panoramaTimer + p_73970_3_) / 400.0f) * 25.0f + 20.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(-((float)this.panoramaTimer + p_73970_3_) * 0.1f, 0.0f, 1.0f, 0.0f);
            int l2 = 0;
            while (l2 < 6) {
                GlStateManager.pushMatrix();
                if (l2 == 1) {
                    GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l2 == 2) {
                    GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l2 == 3) {
                    GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
                }
                if (l2 == 4) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                if (l2 == 5) {
                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                }
                ResourceLocation[] aresourcelocation = titlePanoramaPaths;
                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }
                this.mc.getTextureManager().bindTexture(aresourcelocation[l2]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i1 = 255 / (k2 + 1);
                float f3 = 0.0f;
                worldrenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, i1).endVertex();
                worldrenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
                ++l2;
            }
            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
            ++k2;
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
        int j2 = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            j2 = custompanoramaproperties.getBlur2();
        }
        int k2 = 0;
        while (k2 < j2) {
            float f2 = 1.0f / (float)(k2 + 1);
            int l2 = width;
            int i1 = height;
            float f1 = (float)(k2 - i2 / 2) / 256.0f;
            worldrenderer.pos(l2, i1, this.zLevel).tex(0.0f + f1, 1.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(l2, 0.0, this.zLevel).tex(1.0f + f1, 1.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(0.0, 0.0, this.zLevel).tex(1.0f + f1, 0.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            worldrenderer.pos(0.0, i1, this.zLevel).tex(0.0f + f1, 0.0).color(1.0f, 1.0f, 1.0f, f2).endVertex();
            ++k2;
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
        int i2 = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
        if (custompanoramaproperties != null) {
            i2 = custompanoramaproperties.getBlur3();
        }
        int j2 = 0;
        while (j2 < i2) {
            this.rotateAndBlurSkybox(p_73971_3_);
            this.rotateAndBlurSkybox(p_73971_3_);
            ++j2;
        }
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = width > height ? 120.0f / (float)width : 120.0f / (float)height;
        float f3 = (float)height * f2 / 256.0f;
        float f1 = (float)width * f2 / 256.0f;
        int k2 = width;
        int l2 = height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, l2, this.zLevel).tex(0.5f - f3, 0.5f + f1).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(k2, l2, this.zLevel).tex(0.5f - f3, 0.5f - f1).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(k2, 0.0, this.zLevel).tex(0.5f + f3, 0.5f - f1).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        worldrenderer.pos(0.0, 0.0, this.zLevel).tex(0.5f + f3, 0.5f + f1).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
        tessellator.draw();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.pushMatrix();
        GL11.glEnable(3042);
        ScaledResolution sr1 = new ScaledResolution(this.mc);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("client/gui/symbolclient.png"));
        Gui.drawModalRectWithCustomSizedTexture(sr1.getScaledWidth() / 2 - 80 + (int)ThreadLocalRandom.current().nextDouble(2.5), sr1.getScaledHeight() / 2 - 100 + (int)ThreadLocalRandom.current().nextDouble(2.5), 1.0f, 1.0f, 160, 160, 160.0f, 160.0f);
        StringBuilder stringBuilder = new StringBuilder("Plugin/s Loaded: ");
        JavaPluginApi.getInstance();
        this.drawString(this.fontRendererObj, stringBuilder.append(String.valueOf(JavaPluginApi.loader.map.size())).toString(), 2, height - 10, ColorUtils.rainbowEffect());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Object object = this.threadLock;
        synchronized (object) {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink((GuiYesNoCallback)this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
        if (this.func_183501_a()) {
            this.field_183503_M.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.field_183503_M != null) {
            this.field_183503_M.onGuiClosed();
        }
    }
}

