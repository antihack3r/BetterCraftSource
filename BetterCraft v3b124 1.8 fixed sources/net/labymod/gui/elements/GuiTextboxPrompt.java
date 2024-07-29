/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiTextboxPrompt
extends GuiScreen {
    private ModTextField field;
    private GuiScreen backgroundScreen;
    private String title;
    private String textSubmit;
    private String textCancel;
    private String content;
    private Consumer<String> callback;
    private GuiButton buttonSave;
    private GuiButton buttonCancel;
    private boolean passwordField = false;

    public GuiTextboxPrompt(GuiScreen backgroundScreen, String title, String textSubmit, String textCancel, String content, Consumer<String> callback) {
        this.backgroundScreen = backgroundScreen;
        this.title = title;
        this.textSubmit = textSubmit;
        this.textCancel = textCancel;
        this.content = content;
        this.callback = callback;
    }

    public GuiTextboxPrompt setIsPassword(boolean passwordField) {
        this.passwordField = passwordField;
        return this;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiScreen.width = width;
        GuiScreen.height = height;
        this.buttonList.clear();
        this.field = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 100, height / 2, 200, 20);
        this.field.setPasswordBox(this.passwordField);
        this.field.setText(this.content);
        this.field.setFocused(true);
        this.field.setMaxStringLength(60);
        this.field.setCursorPositionEnd();
        this.buttonSave = new GuiButton(1, width / 2 + 10, height / 2 + 25, 90, 20, this.textSubmit);
        this.buttonList.add(this.buttonSave);
        this.buttonCancel = new GuiButton(2, width / 2 - 100, height / 2 + 25, 90, 20, this.textCancel);
        this.buttonList.add(this.buttonCancel);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
        LabyMod.getInstance().getDrawUtils().drawIngameBackground();
        this.field.setFocused(true);
        this.field.drawTextBox();
        this.buttonSave.enabled = !this.field.getText().isEmpty();
        LabyMod.getInstance().getDrawUtils().drawCenteredString(this.title, width / 2, height / 2 - 25);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.field.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == this.buttonSave.id) {
            this.callback.accept(this.field.getText());
            Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
        }
        if (button.id == this.buttonCancel.id) {
            Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.actionPerformed(this.buttonCancel);
            return;
        }
        if (keyCode == 28) {
            this.actionPerformed(this.buttonSave);
            return;
        }
        super.keyTyped(typedChar, keyCode);
        this.field.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field.mouseClicked(mouseX, mouseY, mouseButton);
    }
}

