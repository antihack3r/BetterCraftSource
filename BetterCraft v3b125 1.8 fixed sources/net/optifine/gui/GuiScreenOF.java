/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF
extends GuiScreen {
    protected void actionPerformedRightClick(GuiButton button) throws IOException {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        GuiButton guibutton;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 && (guibutton = GuiScreenOF.getSelectedButton(mouseX, mouseY, this.buttonList)) != null && guibutton.enabled) {
            guibutton.playPressSound(this.mc.getSoundHandler());
            this.actionPerformedRightClick(guibutton);
        }
    }

    public static GuiButton getSelectedButton(int x2, int y2, List<GuiButton> listButtons) {
        int i2 = 0;
        while (i2 < listButtons.size()) {
            GuiButton guibutton = listButtons.get(i2);
            if (guibutton.visible) {
                int j2 = GuiVideoSettings.getButtonWidth(guibutton);
                int k2 = GuiVideoSettings.getButtonHeight(guibutton);
                if (x2 >= guibutton.xPosition && y2 >= guibutton.yPosition && x2 < guibutton.xPosition + j2 && y2 < guibutton.yPosition + k2) {
                    return guibutton;
                }
            }
            ++i2;
        }
        return null;
    }
}

