/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Lang;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;

public class GuiAnimationSettingsOF
extends GuiScreen {
    private GuiScreen prevScreen;
    protected String title;
    private GameSettings settings;
    private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.ANIMATED_WATER, GameSettings.Options.ANIMATED_LAVA, GameSettings.Options.ANIMATED_FIRE, GameSettings.Options.ANIMATED_PORTAL, GameSettings.Options.ANIMATED_REDSTONE, GameSettings.Options.ANIMATED_EXPLOSION, GameSettings.Options.ANIMATED_FLAME, GameSettings.Options.ANIMATED_SMOKE, GameSettings.Options.VOID_PARTICLES, GameSettings.Options.WATER_PARTICLES, GameSettings.Options.RAIN_SPLASH, GameSettings.Options.PORTAL_PARTICLES, GameSettings.Options.POTION_PARTICLES, GameSettings.Options.DRIPPING_WATER_LAVA, GameSettings.Options.ANIMATED_TERRAIN, GameSettings.Options.ANIMATED_TEXTURES, GameSettings.Options.FIREWORK_PARTICLES, GameSettings.Options.PARTICLES};

    public GuiAnimationSettingsOF(GuiScreen guiscreen, GameSettings gamesettings) {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.animationsTitle", new Object[0]);
        this.buttonList.clear();
        int i2 = 0;
        while (i2 < enumOptions.length) {
            GameSettings.Options gamesettings$options = enumOptions[i2];
            int j2 = width / 2 - 155 + i2 % 2 * 160;
            int k2 = height / 6 + 21 * (i2 / 2) - 12;
            if (!gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j2, k2, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
            } else {
                this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j2, k2, gamesettings$options));
            }
            ++i2;
        }
        this.buttonList.add(new GuiButton(210, width / 2 - 155, height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOn")));
        this.buttonList.add(new GuiButton(211, width / 2 - 155 + 80, height / 6 + 168 + 11, 70, 20, Lang.get("of.options.animation.allOff")));
        this.buttonList.add(new GuiOptionButton(200, width / 2 + 5, height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiOptionButton) {
                this.settings.setOptionValue(((GuiOptionButton)guibutton).returnEnumOptions(), 1);
                guibutton.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(guibutton.id));
            }
            if (guibutton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }
            if (guibutton.id == 210) {
                this.mc.gameSettings.setAllAnimations(true);
            }
            if (guibutton.id == 211) {
                this.mc.gameSettings.setAllAnimations(false);
            }
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            this.setWorldAndResolution(this.mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float f2) {
        this.drawDefaultBackground();
        GuiAnimationSettingsOF.drawCenteredString(this.fontRendererObj, this.title, width / 2, 15, 0xFFFFFF);
        super.drawScreen(x2, y2, f2);
    }
}

