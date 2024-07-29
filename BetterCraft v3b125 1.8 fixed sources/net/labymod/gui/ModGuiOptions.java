/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.GuiShaderSelection;
import net.labymod.gui.ModGuiIngameMenu;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModSettingsGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

public class ModGuiOptions
extends GuiOptions {
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");

    public ModGuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_) {
        super(p_i1046_1_, p_i1046_2_);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(205, width / 2 + 5, height / 6 + 24 - 6, 150, 20, LanguageManager.translate("settings_title")));
        if (!MC18 && LabyMod.getSettings().betterShaderSelection) {
            this.buttonList.add(new GuiButton(8675309, width / 2 - 155, height / 6 + 48 - 6 - 24, 150, 20, "Super Secret Settings..."));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            if (LabyModCore.getMinecraft().getPlayer() != null) {
                this.mc.displayGuiScreen(new ModGuiIngameMenu());
            } else {
                this.mc.displayGuiScreen(new GuiMainMenu());
            }
            return;
        }
        if (button.id == 8675309 && LabyMod.getSettings().betterShaderSelection) {
            this.mc.displayGuiScreen(new GuiShaderSelection(this));
            return;
        }
        if (button.id == 205) {
            this.mc.displayGuiScreen(new LabyModSettingsGui(this));
            return;
        }
        super.actionPerformed(button);
    }
}

