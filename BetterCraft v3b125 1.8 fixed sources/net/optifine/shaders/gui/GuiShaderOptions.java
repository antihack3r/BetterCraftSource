/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionScreen;
import net.optifine.shaders.gui.GuiButtonShaderOption;
import net.optifine.shaders.gui.GuiSliderShaderOption;

public class GuiShaderOptions
extends GuiScreenOF {
    private GuiScreen prevScreen;
    protected String title = "Shader Options";
    private GameSettings settings;
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderShaderOptions());
    private String screenName = null;
    private String screenText = null;
    private boolean changed = false;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings) {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public GuiShaderOptions(GuiScreen guiscreen, GameSettings gamesettings, String screenName) {
        this(guiscreen, gamesettings);
        this.screenName = screenName;
        if (screenName != null) {
            this.screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.shaderOptionsTitle", new Object[0]);
        int i2 = 100;
        int j2 = 0;
        int k2 = 30;
        int l2 = 20;
        int i1 = 120;
        int j1 = 20;
        int k1 = Shaders.getShaderPackColumns(this.screenName, 2);
        ShaderOption[] ashaderoption = Shaders.getShaderPackOptions(this.screenName);
        if (ashaderoption != null) {
            int l1 = MathHelper.ceiling_double_int((double)ashaderoption.length / 9.0);
            if (k1 < l1) {
                k1 = l1;
            }
            int i22 = 0;
            while (i22 < ashaderoption.length) {
                ShaderOption shaderoption = ashaderoption[i22];
                if (shaderoption != null && shaderoption.isVisible()) {
                    int j22 = i22 % k1;
                    int k22 = i22 / k1;
                    int l22 = Math.min(width / k1, 200);
                    j2 = (width - l22 * k1) / 2;
                    int i3 = j22 * l22 + 5 + j2;
                    int j3 = k2 + k22 * l2;
                    int k3 = l22 - 10;
                    String s2 = GuiShaderOptions.getButtonText(shaderoption, k3);
                    GuiButtonShaderOption guibuttonshaderoption = Shaders.isShaderPackOptionSlider(shaderoption.getName()) ? new GuiSliderShaderOption(i2 + i22, i3, j3, k3, j1, shaderoption, s2) : new GuiButtonShaderOption(i2 + i22, i3, j3, k3, j1, shaderoption, s2);
                    guibuttonshaderoption.enabled = shaderoption.isEnabled();
                    this.buttonList.add(guibuttonshaderoption);
                }
                ++i22;
            }
        }
        this.buttonList.add(new GuiButton(201, width / 2 - i1 - 20, height / 6 + 168 + 11, i1, j1, I18n.format("controls.reset", new Object[0])));
        this.buttonList.add(new GuiButton(200, width / 2 + 20, height / 6 + 168 + 11, i1, j1, I18n.format("gui.done", new Object[0])));
    }

    public static String getButtonText(ShaderOption so2, int btnWidth) {
        String s2 = so2.getNameText();
        if (so2 instanceof ShaderOptionScreen) {
            ShaderOptionScreen shaderoptionscreen = (ShaderOptionScreen)so2;
            return String.valueOf(s2) + "...";
        }
        FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;
        int i2 = fontrenderer.getStringWidth(": " + Lang.getOff()) + 5;
        while (fontrenderer.getStringWidth(s2) + i2 >= btnWidth && s2.length() > 0) {
            s2 = s2.substring(0, s2.length() - 1);
        }
        String s1 = so2.isChanged() ? so2.getValueColor(so2.getValue()) : "";
        String s22 = so2.getValueText(so2.getValue());
        return String.valueOf(s2) + ": " + s1 + s22;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiButtonShaderOption) {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
                if (shaderoption instanceof ShaderOptionScreen) {
                    String s2 = shaderoption.getName();
                    GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, this.settings, s2);
                    this.mc.displayGuiScreen(guishaderoptions);
                    return;
                }
                if (GuiShaderOptions.isShiftKeyDown()) {
                    shaderoption.resetValue();
                } else if (guibuttonshaderoption.isSwitchable()) {
                    shaderoption.nextValue();
                }
                this.updateAllButtons();
                this.changed = true;
            }
            if (guibutton.id == 201) {
                ShaderOption[] ashaderoption = Shaders.getChangedOptions(Shaders.getShaderPackOptions());
                int i2 = 0;
                while (i2 < ashaderoption.length) {
                    ShaderOption shaderoption1 = ashaderoption[i2];
                    shaderoption1.resetValue();
                    this.changed = true;
                    ++i2;
                }
                this.updateAllButtons();
            }
            if (guibutton.id == 200) {
                if (this.changed) {
                    Shaders.saveShaderPackOptions();
                    this.changed = false;
                    Shaders.uninit();
                }
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    @Override
    protected void actionPerformedRightClick(GuiButton btn) {
        if (btn instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)btn;
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            if (GuiShaderOptions.isShiftKeyDown()) {
                shaderoption.resetValue();
            } else if (guibuttonshaderoption.isSwitchable()) {
                shaderoption.prevValue();
            }
            this.updateAllButtons();
            this.changed = true;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.changed) {
            Shaders.saveShaderPackOptions();
            this.changed = false;
            Shaders.uninit();
        }
    }

    private void updateAllButtons() {
        for (GuiButton guibutton : this.buttonList) {
            if (!(guibutton instanceof GuiButtonShaderOption)) continue;
            GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
            if (shaderoption instanceof ShaderOptionProfile) {
                ShaderOptionProfile shaderoptionprofile = (ShaderOptionProfile)shaderoption;
                shaderoptionprofile.updateProfile();
            }
            guibuttonshaderoption.displayString = GuiShaderOptions.getButtonText(shaderoption, guibuttonshaderoption.getButtonWidth());
            guibuttonshaderoption.valueChanged();
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float f2) {
        this.drawDefaultBackground();
        if (this.screenText != null) {
            GuiShaderOptions.drawCenteredString(this.fontRendererObj, this.screenText, width / 2, 15, 0xFFFFFF);
        } else {
            GuiShaderOptions.drawCenteredString(this.fontRendererObj, this.title, width / 2, 15, 0xFFFFFF);
        }
        super.drawScreen(x2, y2, f2);
        this.tooltipManager.drawTooltips(x2, y2, this.buttonList);
    }
}

