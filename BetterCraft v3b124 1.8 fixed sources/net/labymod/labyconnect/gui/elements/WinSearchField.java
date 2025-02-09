/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.SmallDropDownMenu;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.minecraft.client.gui.GuiButton;

public class WinSearchField
extends WindowElement<GuiFriendsLayout> {
    private ModTextField fieldSearch;
    private SmallDropDownMenu buttonSortOptions;

    public WinSearchField(GuiFriendsLayout chatLayout) {
        super(chatLayout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        int paddingHeight = 5;
        boolean paddingWidth = false;
        int dragLineWidth = 2;
        int spaceBetween = 5;
        int buttonWidth = 30;
        this.fieldSearch = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), left + 30 + 0 + 5, top + 5, right - left - 30 - 0 - 2 - 5, bottom - top - 10);
        this.fieldSearch.setBlackBox(false);
        this.buttonSortOptions = new SmallDropDownMenu(left + 0, top + (bottom - top - 20) / 2, 30, 20);
        this.buttonSortOptions.addDropDownEntry(LanguageManager.translate("chat_sort_all"));
        this.buttonSortOptions.addDropDownEntry(LanguageManager.translate("online"));
        this.buttonSortOptions.addDropDownEntry(LanguageManager.translate("chat_sort_latest"));
        this.buttonSortOptions.setSelectedOptionIndex(LabyMod.getSettings().friendSortType);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        this.fieldSearch.drawTextBox();
        this.buttonSortOptions.renderButton(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
        int entryCode = this.buttonSortOptions.onClick(mouseX, mouseY);
        if (entryCode >= 0) {
            LabyMod.getSettings().friendSortType = entryCode;
            LabyMod.getInstance().getLabyConnect().sortFriendList(entryCode);
            return true;
        }
        return false;
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.fieldSearch.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    @Override
    public void updateScreen() {
        this.fieldSearch.updateCursorCounter();
    }

    public ModTextField getFieldSearch() {
        return this.fieldSearch;
    }

    public SmallDropDownMenu getButtonSortOptions() {
        return this.buttonSortOptions;
    }
}

