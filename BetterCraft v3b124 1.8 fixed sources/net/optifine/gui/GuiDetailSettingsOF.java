/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;

public class GuiDetailSettingsOF
extends GuiScreen {
    private GuiScreen prevScreen;
    protected String title;
    private GameSettings settings;
    private static GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.CLOUDS, GameSettings.Options.CLOUD_HEIGHT, GameSettings.Options.TREES, GameSettings.Options.RAIN, GameSettings.Options.SKY, GameSettings.Options.STARS, GameSettings.Options.SUN_MOON, GameSettings.Options.SHOW_CAPES, GameSettings.Options.FOG_FANCY, GameSettings.Options.FOG_START, GameSettings.Options.TRANSLUCENT_BLOCKS, GameSettings.Options.HELD_ITEM_TOOLTIPS, GameSettings.Options.DROPPED_ITEMS, GameSettings.Options.ENTITY_SHADOWS, GameSettings.Options.VIGNETTE, GameSettings.Options.ALTERNATE_BLOCKS, GameSettings.Options.SWAMP_COLORS, GameSettings.Options.SMOOTH_BIOMES};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiDetailSettingsOF(GuiScreen guiscreen, GameSettings gamesettings) {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.detailsTitle", new Object[0]);
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
        this.buttonList.add(new GuiButton(200, width / 2 - 100, height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
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
        }
    }

    @Override
    public void drawScreen(int x2, int y2, float f2) {
        this.drawDefaultBackground();
        GuiDetailSettingsOF.drawCenteredString(this.fontRendererObj, this.title, width / 2, 15, 0xFFFFFF);
        super.drawScreen(x2, y2, f2);
        this.tooltipManager.drawTooltips(x2, y2, this.buttonList);
    }
}

