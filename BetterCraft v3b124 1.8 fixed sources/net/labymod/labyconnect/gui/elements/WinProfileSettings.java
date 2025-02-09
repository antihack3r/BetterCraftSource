/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.main.DefinedSettings;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.gui.GuiButton;

public class WinProfileSettings
extends WindowElement<GuiFriendsLayout> {
    private Scrollbar scrollbar;

    public WinProfileSettings(GuiFriendsLayout chatLayout) {
        super(chatLayout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        this.scrollbar = new Scrollbar(0);
        this.scrollbar.init();
        this.scrollbar.setSpeed(15);
        this.scrollbar.setPosition(this.right - 4, this.top + 2, this.right, this.bottom - 2);
        this.updateGuiElements();
    }

    private void updateGuiElements() {
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        try {
            List<SettingsElement> allElements = DefinedSettings.getChatSetingsCategory().getSettings().getElements();
            double totalEntryHeight = 0.0;
            int zLevel = 0;
            while (zLevel < 2) {
                double posY = (double)(this.top + 3) + this.scrollbar.getScrollY();
                int maxX = this.scrollbar.isHidden() ? this.right - 1 : this.right - 6;
                totalEntryHeight = 0.0;
                for (SettingsElement element : allElements) {
                    if ((!(element instanceof DropDownElement) || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 0 || (element instanceof DropDownElement || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 1) {
                        if (element instanceof DropDownElement) {
                            ((DropDownElement)element).getDropDownMenu().setMaxY(this.bottom);
                        }
                        element.draw(this.left + 1, (int)posY, maxX, (int)(posY + (double)element.getEntryHeight()), mouseX, mouseY);
                    }
                    posY += (double)(element.getEntryHeight() + 1);
                    totalEntryHeight += (double)(element.getEntryHeight() + 1);
                    if (zLevel != 1 || !element.isMouseOver() || mouseY <= this.top || mouseY >= this.bottom) continue;
                    element.drawDescription(mouseX, mouseY, LabyMod.getInstance().getDrawUtils().getWidth());
                }
                ++zLevel;
            }
            this.scrollbar.setEntryHeight(totalEntryHeight / (double)allElements.size());
            this.scrollbar.update(allElements.size());
            this.scrollbar.draw();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    public void handleMouseInput() {
        if (this.isMouseOver()) {
            this.scrollbar.mouseInput();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        List<SettingsElement> allElements = DefinedSettings.getChatSetingsCategory().getSettings().getElements();
        for (SettingsElement element : allElements) {
            if (!(element instanceof DropDownElement) || !((DropDownElement)element).onClickDropDown(mouseX, mouseY, mouseButton)) continue;
            return false;
        }
        for (SettingsElement element : allElements) {
            element.unfocus(mouseX, mouseY, mouseButton);
            if (!element.isMouseOver()) continue;
            element.mouseClicked(mouseX, mouseY, mouseButton);
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (SettingsElement settingElement : DefinedSettings.getChatSetingsCategory().getSettings().getElements()) {
            settingElement.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    public boolean isScrolledToTop() {
        return this.scrollbar.getScrollY() == 0.0;
    }

    @Override
    public void updateScreen() {
    }
}

