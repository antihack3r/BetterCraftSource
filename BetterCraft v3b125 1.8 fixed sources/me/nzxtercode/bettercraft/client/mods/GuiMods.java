/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.hud.HUDManager;
import me.nzxtercode.bettercraft.client.hud.IRender;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringUtils;

public class GuiMods
extends GuiScreen {
    public GuiScreen before;

    public GuiMods(GuiScreen screen) {
        this.before = screen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1337, width - 93, height - 23, 90, 20, "Back"));
        int index = 0;
        int x2 = 0;
        int y2 = 0;
        while (index < HUDManager.getInstance().getRegisteredRenderers().size()) {
            IRender renderer = HUDManager.getInstance().getRegisteredRenderers().get(index);
            if (renderer instanceof ModRender) {
                this.buttonList.add(new GuiButton(index, 3 + x2, 3 + y2, 98, 20, String.format("%s%s%s", Character.valueOf('\u00a7'), Character.valueOf(renderer.isEnabled() ? (char)'a' : 'c'), StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase(renderer.getClass().getSimpleName().replace("Mod", "").replaceAll("\\d+", "")), " "))));
                if (y2 + 56 >= height) {
                    y2 = -25;
                    x2 += 101;
                }
            }
            ++index;
            y2 += 25;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1337) {
            this.mc.displayGuiScreen(this.before);
        }
        int index = 0;
        while (index < HUDManager.getInstance().getRegisteredRenderers().size()) {
            IRender renderer = HUDManager.getInstance().getRegisteredRenderers().get(index);
            if (button.id == index) {
                ((ModRender)renderer).setEnabledState(!renderer.isEnabled());
                button.displayString = String.format("%s%s%s", Character.valueOf('\u00a7'), Character.valueOf(renderer.isEnabled() ? (char)'a' : 'c'), StringUtils.join((Object[])StringUtils.splitByCharacterTypeCamelCase(renderer.getClass().getSimpleName().replace("Mod", "").replaceAll("\\d+", "")), " "));
            }
            ++index;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

