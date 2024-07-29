/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiTagsAdd
extends GuiScreen {
    private GuiScreen lastScreen;
    private String editName;
    private String storedName = "";
    private String storedTag = "";
    private String prevName;
    private ModTextField fieldMinecraftName;
    private ModTextField fieldTagName;
    private GuiButton buttonDone;
    private String renderHeadName;

    public GuiTagsAdd(GuiScreen lastScreen, String editName) {
        this.lastScreen = lastScreen;
        this.editName = editName;
        this.prevName = editName;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.fieldMinecraftName = new ModTextField(-1, LabyMod.getInstance().getDrawUtils().fontRenderer, width / 2 - 100, height / 2 - 50, 200, 20);
        this.fieldMinecraftName.setMaxStringLength(32);
        this.fieldTagName = new ModTextField(-1, LabyMod.getInstance().getDrawUtils().fontRenderer, width / 2 - 100, height / 2 - 5, 200, 20);
        this.fieldTagName.setColorBarEnabled(true);
        this.fieldTagName.setMaxStringLength(32);
        this.buttonDone = new GuiButton(0, width / 2 + 3, height / 2 + 35, 98, 20, LanguageManager.translate("button_add"));
        this.buttonList.add(this.buttonDone);
        this.buttonList.add(new GuiButton(1, width / 2 - 101, height / 2 + 35, 98, 20, LanguageManager.translate("button_cancel")));
        this.buttonDone.enabled = false;
        if (this.editName != null) {
            this.storedName = this.editName;
            this.renderHeadName = this.editName;
            String foundTag = TagManager.getConfigManager().getSettings().getTags().get(this.editName);
            if (foundTag != null) {
                this.storedTag = foundTag;
                this.buttonDone.enabled = true;
            }
            this.editName = null;
        }
        this.fieldMinecraftName.setText(this.storedName);
        this.fieldTagName.setText(this.storedTag);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 15) {
            if (this.fieldMinecraftName.isFocused()) {
                this.fieldTagName.setFocused(true);
                this.fieldMinecraftName.setFocused(false);
                this.renderHeadName = this.storedName;
            } else if (this.fieldTagName.isFocused()) {
                this.fieldMinecraftName.setFocused(true);
                this.fieldTagName.setFocused(false);
            }
        }
        if (this.fieldMinecraftName.textboxKeyTyped(typedChar, keyCode)) {
            this.storedName = this.fieldMinecraftName.getText();
        }
        if (this.fieldTagName.textboxKeyTyped(typedChar, keyCode)) {
            this.storedTag = this.fieldTagName.getText();
        }
        this.buttonDone.enabled = !this.storedName.isEmpty() && !this.storedTag.isEmpty();
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean flag = this.fieldMinecraftName.isFocused();
        this.fieldMinecraftName.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.fieldTagName.mouseClicked(mouseX, mouseY, mouseButton)) {
            this.storedTag = this.fieldTagName.getText();
            if (flag && !this.fieldMinecraftName.isFocused()) {
                this.renderHeadName = this.storedName;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0: {
                if (this.prevName != null) {
                    TagManager.getConfigManager().getSettings().getTags().remove(this.prevName);
                }
                TagManager.getConfigManager().getSettings().getTags().put(this.storedName, this.storedTag);
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                break;
            }
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.fieldMinecraftName.updateCursorCounter();
        this.fieldTagName.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.fieldMinecraftName.drawTextBox();
        this.fieldTagName.drawTextBox();
        this.fieldTagName.drawColorBar(mouseX, mouseY);
        if (this.renderHeadName != null && !this.renderHeadName.contains(" ") && this.renderHeadName.length() <= 16) {
            LabyMod.getInstance().getDrawUtils().drawPlayerHead(this.renderHeadName, this.fieldMinecraftName.xPosition - this.fieldMinecraftName.height - 4, this.fieldMinecraftName.yPosition, this.fieldMinecraftName.height);
        }
        LabyMod.getInstance().getDrawUtils().drawString(String.valueOf(LanguageManager.translate("minecraft_name")) + ":", width / 2 - 100, height / 2 - 65);
        LabyMod.getInstance().getDrawUtils().drawString(String.valueOf(LanguageManager.translate("custom_tag_for_player")) + ":", width / 2 - 100, height / 2 - 20);
        String displayString = this.storedTag.replaceAll("&", ModColor.getCharAsString());
        int strl = LabyMod.getInstance().getDrawUtils().getStringWidth(ModColor.removeColor(displayString));
        if (strl > 1) {
            GuiTagsAdd.drawRect(width / 2 - strl / 2 - 2, height / 2 - 92, width / 2 + strl / 2 + 2, height / 2 - 80, Integer.MIN_VALUE);
            LabyMod.getInstance().getDrawUtils().drawCenteredString(displayString, width / 2, height / 2 - 90);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

