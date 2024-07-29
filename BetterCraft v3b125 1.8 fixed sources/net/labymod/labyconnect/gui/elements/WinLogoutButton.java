/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.minecraft.client.gui.GuiButton;

public class WinLogoutButton
extends WindowElement<GuiFriendsLayout> {
    public WinLogoutButton(GuiFriendsLayout chatLayout) {
        super(chatLayout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        boolean paddingWidth = false;
        int dragLineWidth = 2;
        buttonlist.add(new GuiButton(4, left + 0, top + (bottom - top - 20) / 2 + 1, right - left - 0 - 2, 20, LanguageManager.translate("button_logout")));
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 4) {
            LabyMod.getInstance().getLabyConnect().setForcedLogout(true);
            LabyMod.getInstance().getLabyConnect().getClientConnection().disconnect(false);
            ((GuiFriendsLayout)this.layout).initGui();
        }
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
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void updateScreen() {
    }
}

