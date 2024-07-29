/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderEnumShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.gui.GuiButtonDownloadShaders;
import net.optifine.shaders.gui.GuiButtonEnumShaderOption;
import net.optifine.shaders.gui.GuiShaderOptions;
import net.optifine.shaders.gui.GuiSlotShaders;
import org.lwjgl.Sys;

public class GuiShaders
extends GuiScreenOF {
    protected GuiScreen parentGui;
    protected String screenTitle = "Shaders";
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderEnumShaderOptions());
    private int updateTimer = -1;
    private GuiSlotShaders shaderList;
    private boolean saved = false;
    private static float[] QUALITY_MULTIPLIERS = new float[]{0.5f, 0.6f, 0.6666667f, 0.75f, 0.8333333f, 0.9f, 1.0f, 1.1666666f, 1.3333334f, 1.5f, 1.6666666f, 1.8f, 2.0f};
    private static String[] QUALITY_MULTIPLIER_NAMES = new String[]{"0.5x", "0.6x", "0.66x", "0.75x", "0.83x", "0.9x", "1x", "1.16x", "1.33x", "1.5x", "1.66x", "1.8x", "2x"};
    private static float QUALITY_MULTIPLIER_DEFAULT = 1.0f;
    private static float[] HAND_DEPTH_VALUES = new float[]{0.0625f, 0.125f, 0.25f};
    private static String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
    private static float HAND_DEPTH_DEFAULT = 0.125f;
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;

    public GuiShaders(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
        this.parentGui = par1GuiScreen;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("of.options.shadersTitle", new Object[0]);
        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }
        int i2 = 120;
        int j2 = 20;
        int k2 = width - i2 - 10;
        int l2 = 30;
        int i1 = 20;
        int j1 = width - i2 - 20;
        this.shaderList = new GuiSlotShaders(this, j1, height, l2, height - 50, 16);
        this.shaderList.registerScrollButtons(7, 8);
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, k2, 0 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, k2, 1 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, k2, 2 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, k2, 3 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, k2, 4 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, k2, 5 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, k2, 6 * i1 + l2, i2, j2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, k2, 7 * i1 + l2, i2, j2));
        int k1 = Math.min(150, j1 / 2 - 10);
        int l1 = j1 / 4 - k1 / 2;
        int i22 = height - 25;
        this.buttonList.add(new GuiButton(201, l1, i22, k1 - 22 + 1, j2, Lang.get("of.options.shaders.shadersFolder")));
        this.buttonList.add(new GuiButtonDownloadShaders(210, l1 + k1 - 22 - 1, i22));
        this.buttonList.add(new GuiButton(202, j1 / 4 * 3 - k1 / 2, height - 25, k1, j2, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(203, k2, height - 25, i2, j2, Lang.get("of.options.shaders.shaderOptions")));
        this.updateButtons();
    }

    public void updateButtons() {
        boolean flag = Config.isShaders();
        for (GuiButton guibutton : this.buttonList) {
            if (guibutton.id == 201 || guibutton.id == 202 || guibutton.id == 210 || guibutton.id == EnumShaderOption.ANTIALIASING.ordinal()) continue;
            guibutton.enabled = flag;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.shaderList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        this.actionPerformed(button, false);
    }

    @Override
    protected void actionPerformedRightClick(GuiButton button) {
        this.actionPerformed(button, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void actionPerformed(GuiButton button, boolean rightClick) {
        if (!button.enabled) return;
        if (!(button instanceof GuiButtonEnumShaderOption)) {
            if (rightClick) return;
            switch (button.id) {
                case 201: {
                    switch (GuiShaders.getOSType()) {
                        case 1: {
                            String s2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderPacksDir.getAbsolutePath());
                            try {
                                Runtime.getRuntime().exec(s2);
                                return;
                            }
                            catch (IOException ioexception) {
                                ioexception.printStackTrace();
                                break;
                            }
                        }
                        case 2: {
                            try {
                                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderPacksDir.getAbsolutePath()});
                                return;
                            }
                            catch (IOException ioexception1) {
                                ioexception1.printStackTrace();
                            }
                        }
                    }
                    boolean flag = false;
                    try {
                        Class<?> oclass1 = Class.forName("java.awt.Desktop");
                        Object object1 = oclass1.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                        oclass1.getMethod("browse", URI.class).invoke(object1, new File(this.mc.mcDataDir, "shaderpacks").toURI());
                    }
                    catch (Throwable throwable1) {
                        throwable1.printStackTrace();
                        flag = true;
                    }
                    if (!flag) return;
                    Config.dbg("Opening via system class!");
                    Sys.openURL("file://" + Shaders.shaderPacksDir.getAbsolutePath());
                    return;
                }
                case 202: {
                    Shaders.storeConfig();
                    this.saved = true;
                    this.mc.displayGuiScreen(this.parentGui);
                    return;
                }
                case 203: {
                    GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, Config.getGameSettings());
                    Config.getMinecraft().displayGuiScreen(guishaderoptions);
                    return;
                }
                case 210: {
                    try {
                        Class<?> oclass = Class.forName("java.awt.Desktop");
                        Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                        oclass.getMethod("browse", URI.class).invoke(object, new URI("http://optifine.net/shaderPacks"));
                    }
                    catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                default: {
                    this.shaderList.actionPerformed(button);
                    return;
                }
            }
        }
        GuiButtonEnumShaderOption guibuttonenumshaderoption = (GuiButtonEnumShaderOption)button;
        switch (guibuttonenumshaderoption.getEnumShaderOption()) {
            case ANTIALIASING: {
                Shaders.nextAntialiasingLevel(!rightClick);
                if (this.hasShiftDown()) {
                    Shaders.configAntialiasingLevel = 0;
                }
                Shaders.uninit();
                break;
            }
            case NORMAL_MAP: {
                boolean bl2 = Shaders.configNormalMap = !Shaders.configNormalMap;
                if (this.hasShiftDown()) {
                    Shaders.configNormalMap = true;
                }
                Shaders.uninit();
                this.mc.scheduleResourcesRefresh();
                break;
            }
            case SPECULAR_MAP: {
                boolean bl3 = Shaders.configSpecularMap = !Shaders.configSpecularMap;
                if (this.hasShiftDown()) {
                    Shaders.configSpecularMap = true;
                }
                Shaders.uninit();
                this.mc.scheduleResourcesRefresh();
                break;
            }
            case RENDER_RES_MUL: {
                Shaders.configRenderResMul = this.getNextValue(Shaders.configRenderResMul, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_DEFAULT, !rightClick, this.hasShiftDown());
                Shaders.uninit();
                Shaders.scheduleResize();
                break;
            }
            case SHADOW_RES_MUL: {
                Shaders.configShadowResMul = this.getNextValue(Shaders.configShadowResMul, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_DEFAULT, !rightClick, this.hasShiftDown());
                Shaders.uninit();
                Shaders.scheduleResizeShadow();
                break;
            }
            case HAND_DEPTH_MUL: {
                Shaders.configHandDepthMul = this.getNextValue(Shaders.configHandDepthMul, HAND_DEPTH_VALUES, HAND_DEPTH_DEFAULT, !rightClick, this.hasShiftDown());
                Shaders.uninit();
                break;
            }
            case OLD_HAND_LIGHT: {
                Shaders.configOldHandLight.nextValue(!rightClick);
                if (this.hasShiftDown()) {
                    Shaders.configOldHandLight.resetValue();
                }
                Shaders.uninit();
                break;
            }
            case OLD_LIGHTING: {
                Shaders.configOldLighting.nextValue(!rightClick);
                if (this.hasShiftDown()) {
                    Shaders.configOldLighting.resetValue();
                }
                Shaders.updateBlockLightLevel();
                Shaders.uninit();
                this.mc.scheduleResourcesRefresh();
                break;
            }
            case TWEAK_BLOCK_DAMAGE: {
                Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                break;
            }
            case CLOUD_SHADOW: {
                Shaders.configCloudShadow = !Shaders.configCloudShadow;
                break;
            }
            case TEX_MIN_FIL_B: {
                Shaders.configTexMinFilN = Shaders.configTexMinFilS = (Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3);
                button.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                ShadersTex.updateTextureMinMagFilter();
                break;
            }
            case TEX_MAG_FIL_N: {
                Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                button.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                ShadersTex.updateTextureMinMagFilter();
                break;
            }
            case TEX_MAG_FIL_S: {
                Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                button.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                ShadersTex.updateTextureMinMagFilter();
                break;
            }
            case SHADOW_CLIP_FRUSTRUM: {
                Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                button.displayString = "ShadowClipFrustrum: " + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
                ShadersTex.updateTextureMinMagFilter();
            }
        }
        guibuttonenumshaderoption.updateButtonText();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (!this.saved) {
            Shaders.storeConfig();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.shaderList.drawScreen(mouseX, mouseY, partialTicks);
        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }
        GuiShaders.drawCenteredString(this.fontRendererObj, String.valueOf(this.screenTitle) + " ", width / 2, 15, 0xFFFFFF);
        String s2 = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
        int i2 = this.fontRendererObj.getStringWidth(s2);
        if (i2 < width - 5) {
            GuiShaders.drawCenteredString(this.fontRendererObj, s2, width / 2, height - 40, 0x808080);
        } else {
            this.drawString(this.fontRendererObj, s2, 5, height - 40, 0x808080);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        --this.updateTimer;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void drawCenteredString(String text, int x2, int y2, int color) {
        GuiShaders.drawCenteredString(this.fontRendererObj, text, x2, y2, color);
    }

    public static String toStringOnOff(boolean value) {
        String s2 = Lang.getOn();
        String s1 = Lang.getOff();
        return value ? s2 : s1;
    }

    public static String toStringAa(int value) {
        return value == 2 ? "FXAA 2x" : (value == 4 ? "FXAA 4x" : Lang.getOff());
    }

    public static String toStringValue(float val, float[] values, String[] names) {
        int i2 = GuiShaders.getValueIndex(val, values);
        return names[i2];
    }

    private float getNextValue(float val, float[] values, float valDef, boolean forward, boolean reset) {
        if (reset) {
            return valDef;
        }
        int i2 = GuiShaders.getValueIndex(val, values);
        if (forward) {
            if (++i2 >= values.length) {
                i2 = 0;
            }
        } else if (--i2 < 0) {
            i2 = values.length - 1;
        }
        return values[i2];
    }

    public static int getValueIndex(float val, float[] values) {
        int i2 = 0;
        while (i2 < values.length) {
            float f2 = values[i2];
            if (f2 >= val) {
                return i2;
            }
            ++i2;
        }
        return values.length - 1;
    }

    public static String toStringQuality(float val) {
        return GuiShaders.toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
    }

    public static String toStringHandDepth(float val) {
        return GuiShaders.toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
    }

    public static int getOSType() {
        String s2 = System.getProperty("os.name").toLowerCase();
        return s2.contains("win") ? 1 : (s2.contains("mac") ? 2 : (s2.contains("solaris") ? 3 : (s2.contains("sunos") ? 3 : (s2.contains("linux") ? 4 : (s2.contains("unix") ? 4 : 0)))));
    }

    public boolean hasShiftDown() {
        return GuiShaders.isShiftKeyDown();
    }
}

