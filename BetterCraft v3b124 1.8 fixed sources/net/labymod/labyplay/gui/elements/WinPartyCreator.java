/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.gui.elements;

import java.util.List;
import net.labymod.gui.elements.GuiTextboxPrompt;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class WinPartyCreator
extends WindowElement<GuiPlayLayout> {
    public WinPartyCreator(GuiPlayLayout layout) {
        super(layout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        boolean margin = false;
        int padding = 6;
        String invitePlayer = "Invite player";
        int invitePlayerWidth = this.draw.getStringWidth("Invite player") + 12;
        buttonlist.add(new GuiButton(1, left + 0, top + 18, invitePlayerWidth, 20, "Invite player"));
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        boolean paddingX = true;
        int paddingY = 5;
        this.draw.drawString("Create new party", this.left + 1, this.top + 5);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiTextboxPrompt(Minecraft.getMinecraft().currentScreen, "Player to invite:", "Invite", "Cancel", "", new Consumer<String>(){

                @Override
                public void accept(String username) {
                    if (!username.isEmpty()) {
                        ((GuiPlayLayout)WinPartyCreator.this.layout).getPartySystem().invitePlayer(username);
                    }
                }
            }));
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
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

