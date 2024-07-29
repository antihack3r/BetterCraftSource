/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tabs;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.tools.autotext.AutoTextKeyBinds;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChatAutoText
extends GuiChatCustom {
    private Scrollbar scrollbar = new Scrollbar(15);
    private AutoTextKeyBinds.AutoText selectedAutoText;
    private GuiTextField textFieldMessage;
    private GuiTextField textFieldAutoTextKeyCode;
    private GuiTextField textFieldAutoTextServerAddress;
    private boolean markMessageRed = false;
    private boolean markKeybindRed = false;
    private boolean canScroll;

    public GuiChatAutoText(String defaultText) {
        super(defaultText);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.scrollbar.setPosition(width - 6, height - 196, width - 5, height - 20);
        this.scrollbar.update(LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds().size());
        this.scrollbar.setSpeed(10);
        this.scrollbar.setEntryHeight(10.0);
        this.textFieldMessage = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldAutoTextKeyCode = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldAutoTextServerAddress = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, -100, 110, 10);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.canScroll) {
            this.scrollbar.mouseInput();
            int i2 = Mouse.getEventDWheel();
            if (i2 != 0) {
                this.mc.ingameGUI.getChatGUI().resetScroll();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.scrollbar.calc();
        GuiChatAutoText.drawRect(width - 150, height - 200, width - 2, height - 16, Integer.MIN_VALUE);
        this.canScroll = mouseX > width - 150 && mouseX < width - 2 && mouseY > height - 150 && mouseY < height - 16;
        int row = 0;
        for (AutoTextKeyBinds.AutoText component : LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds()) {
            boolean hover;
            double posY = (double)(height - 195 + row * 10) + this.scrollbar.getScrollY();
            ++row;
            if (!(posY >= (double)(height - 200)) || posY > (double)(height - 25)) continue;
            boolean bl2 = hover = this.selectedAutoText == null && mouseX > width - 150 + 1 && mouseX < width - 2 - 1 && (double)mouseY > posY - 1.0 && (double)mouseY < posY + 9.0;
            if (hover || this.selectedAutoText == component) {
                GuiChatAutoText.drawRect(width - 150 + 1, (int)posY - 1, width - 2 - 1, (int)posY + 9, hover ? ModColor.toRGB(100, 200, 200, 100) : Integer.MAX_VALUE);
            }
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), LabyMod.getInstance().getDrawUtils().trimStringToWidth("[" + ModColor.cl("a") + (component.isKeyShift() ? "SHIFT+" : "") + (component.isKeyAlt() ? "ALT+" : "") + (component.isKeyCtrl() ? "CTRL+" : "") + Keyboard.getKeyName(component.getKeyCode()) + ModColor.cl("r") + "] " + component.getMessage(), 130), width - 145, (int)posY, Integer.MAX_VALUE);
            if (this.selectedAutoText != null) continue;
            boolean hoverX = mouseX > width - 12 - 1 && mouseX < width - 12 + 7 && (double)mouseY > posY && (double)mouseY < posY + 8.0;
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl(hoverX ? "c" : "4")) + "\u2718", width - 12, (int)posY, Integer.MAX_VALUE);
        }
        if (!this.scrollbar.isHidden()) {
            GuiChatAutoText.drawRect(width - 6, height - 145, width - 5, height - 20, Integer.MIN_VALUE);
            GuiChatAutoText.drawRect(width - 7, (int)this.scrollbar.getTop(), width - 4, (int)(this.scrollbar.getTop() + this.scrollbar.getBarLength()), Integer.MAX_VALUE);
        }
        if (this.selectedAutoText == null) {
            boolean hover2 = mouseX > width - 165 && mouseX < width - 152 && mouseY > height - 200 && mouseY < height - 200 + 13;
            GuiChatAutoText.drawRect(width - 165, height - 200, width - 152, height - 200 + 13, hover2 ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            GuiChatAutoText.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), "+", width - 158, height - 197, hover2 ? ModColor.toRGB(50, 220, 120, 210) : Integer.MAX_VALUE);
        } else {
            GuiChatAutoText.drawRect(width - 270, height - 200, width - 152, height - 16, Integer.MIN_VALUE);
            int relX = width - 270;
            int relY = height - 200;
            this.drawElementTextField("message", this.textFieldMessage, relX, relY, mouseX, mouseY);
            this.drawElementTextField("key", this.textFieldAutoTextKeyCode, relX, relY + 23, mouseX, mouseY);
            this.drawElementCheckBox("with_shift", this.selectedAutoText.isKeyShift(), relX, relY + 46, mouseX, mouseY);
            this.drawElementCheckBox("with_alt", this.selectedAutoText.isKeyAlt(), relX, relY + 46 + 11, mouseX, mouseY);
            this.drawElementCheckBox("with_ctrl", this.selectedAutoText.isKeyCtrl(), relX, relY + 46 + 22, mouseX, mouseY);
            this.drawElementCheckBox("send_instantly", !this.selectedAutoText.isSendNotInstantly(), relX, relY + 46 + 33, mouseX, mouseY);
            this.drawElementCheckBox("server_bound", this.selectedAutoText.isServerBound(), relX, relY + 46 + 44, mouseX, mouseY);
            if (this.selectedAutoText.isServerBound()) {
                this.drawElementTextField("server_address", this.textFieldAutoTextServerAddress, relX, relY + 46 + 55, mouseX, mouseY);
            }
            boolean hoverCancel = mouseX > width - 268 && mouseX < width - 213 && mouseY > height - 30 && mouseY < height - 18;
            boolean hoverSave = mouseX > width - 210 && mouseX < width - 154 && mouseY > height - 30 && mouseY < height - 18;
            GuiChatAutoText.drawRect(width - 268, height - 30, width - 213, height - 18, hoverCancel ? ModColor.toRGB(200, 100, 100, 200) : Integer.MAX_VALUE);
            GuiChatAutoText.drawRect(width - 210, height - 30, width - 154, height - 18, hoverSave ? ModColor.toRGB(100, 200, 100, 200) : Integer.MAX_VALUE);
            GuiChatAutoText.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("button_cancel"), width - 248 + 22 - 14, height - 30 + 2, Integer.MAX_VALUE);
            GuiChatAutoText.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("button_save"), width - 200 + 23 - 4, height - 30 + 2, Integer.MAX_VALUE);
            this.textFieldMessage.drawTextBox();
            this.textFieldAutoTextKeyCode.drawTextBox();
            if (this.selectedAutoText != null && this.selectedAutoText.isServerBound()) {
                this.textFieldAutoTextServerAddress.drawTextBox();
            }
        }
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("ingame_chat_tab_autotext"), width - 150, height - 210, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        if (this.selectedAutoText == null && mouseX > width - 165 && mouseX < width - 152 && mouseY > height - 200 && mouseY < height - 200 + 13) {
            this.loadAutoText(new AutoTextKeyBinds.AutoText("", false, false, false, -1, false, false, ""));
        }
        if (this.selectedAutoText == null) {
            int row = 0;
            AutoTextKeyBinds.AutoText todoDelete = null;
            for (AutoTextKeyBinds.AutoText component : LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds()) {
                double posY = (double)(height - 195 + row * 10) + this.scrollbar.getScrollY();
                ++row;
                if (!(posY >= (double)(height - 200)) || posY > (double)(height - 25)) continue;
                if (mouseX > width - 12 - 1 && mouseX < width - 12 + 7 && (double)mouseY > posY && (double)mouseY < posY + 8.0) {
                    todoDelete = component;
                    continue;
                }
                if (mouseX <= width - 150 + 1 || mouseX >= width - 2 - 1 || (double)mouseY <= posY - 1.0 || (double)mouseY >= posY + 9.0) continue;
                this.loadAutoText(component);
            }
            if (todoDelete != null) {
                LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds().remove(todoDelete);
                LabyMod.getInstance().getChatToolManager().saveTools();
            }
        } else {
            boolean hoverSave;
            int relX = width - 270;
            int relY = height - 200;
            if (this.isHoverElementCheckbox("with_alt", this.selectedAutoText.isKeyAlt(), relX, relY + 46 + 11, mouseX, mouseY)) {
                this.selectedAutoText.setKeyAlt(!this.selectedAutoText.isKeyAlt());
            }
            if (this.isHoverElementCheckbox("with_shift", this.selectedAutoText.isKeyShift(), relX, relY + 46, mouseX, mouseY)) {
                this.selectedAutoText.setKeyShift(!this.selectedAutoText.isKeyShift());
            }
            if (this.isHoverElementCheckbox("with_ctrl", this.selectedAutoText.isKeyCtrl(), relX, relY + 46 + 22, mouseX, mouseY)) {
                this.selectedAutoText.setKeyCtrl(!this.selectedAutoText.isKeyCtrl());
            }
            if (this.isHoverElementCheckbox("send_instantly", this.selectedAutoText.isSendNotInstantly(), relX, relY + 46 + 33, mouseX, mouseY)) {
                this.selectedAutoText.setSendNotInstantly(!this.selectedAutoText.isSendNotInstantly());
            }
            if (this.isHoverElementCheckbox("server_bound", this.selectedAutoText.isServerBound(), relX, relY + 46 + 44, mouseX, mouseY)) {
                this.selectedAutoText.setServerBound(!this.selectedAutoText.isServerBound());
                if (this.selectedAutoText.isServerBound() && this.textFieldAutoTextServerAddress.getText().isEmpty()) {
                    String value = "";
                    ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
                    value = serverData == null && Minecraft.getMinecraft().isSingleplayer() ? "singleplayer" : serverData.serverIP;
                    this.textFieldAutoTextServerAddress.setText(ModUtils.getProfileNameByIp(value));
                }
            }
            boolean hoverCancel = mouseX > width - 268 && mouseX < width - 213 && mouseY > height - 30 && mouseY < height - 18;
            boolean bl2 = hoverSave = mouseX > width - 210 && mouseX < width - 154 && mouseY > height - 30 && mouseY < height - 18;
            if (hoverCancel) {
                this.selectedAutoText = null;
            }
            if (hoverSave) {
                this.selectedAutoText.setMessage(this.textFieldMessage.getText());
                this.selectedAutoText.setServerAddress(this.textFieldAutoTextServerAddress.getText());
                if (this.selectedAutoText.getMessage().replaceAll(" ", "").isEmpty() || this.selectedAutoText.getKeyCode() == -1) {
                    this.markMessageRed = this.textFieldMessage.getText().replaceAll(" ", "").isEmpty();
                    boolean bl3 = this.markKeybindRed = this.selectedAutoText.getKeyCode() == -1;
                }
                if (!this.markMessageRed && !this.markKeybindRed) {
                    if (!LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds().contains(this.selectedAutoText)) {
                        LabyMod.getInstance().getChatToolManager().getAutoTextKeyBinds().add(this.selectedAutoText);
                    }
                    LabyMod.getInstance().getChatToolManager().saveTools();
                    this.selectedAutoText = null;
                    this.initGui();
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.selectedAutoText != null && this.textFieldMessage.isFocused()) {
            this.textFieldMessage.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 15) {
                this.textFieldMessage.setFocused(false);
                this.textFieldAutoTextKeyCode.setFocused(true);
            }
            return;
        }
        if (this.selectedAutoText != null && this.textFieldAutoTextServerAddress.isFocused()) {
            this.textFieldAutoTextServerAddress.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 15) {
                this.textFieldAutoTextServerAddress.setFocused(false);
                this.textFieldMessage.setFocused(true);
            }
            return;
        }
        if (this.selectedAutoText != null && keyCode != 56 && keyCode != 29 && keyCode != 42 && this.textFieldAutoTextKeyCode.isFocused()) {
            this.textFieldAutoTextKeyCode.setFocused(false);
            this.textFieldAutoTextKeyCode.setText(Keyboard.getKeyName(keyCode));
            this.selectedAutoText.setKeyCode(keyCode);
            this.selectedAutoText.setKeyAlt(Keyboard.isKeyDown(56));
            this.selectedAutoText.setKeyCtrl(Keyboard.isKeyDown(29));
            this.selectedAutoText.setKeyShift(Keyboard.isKeyDown(42));
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        this.textFieldMessage.mouseClicked(mouseX, mouseY, mouseButton);
        this.textFieldAutoTextKeyCode.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.selectedAutoText != null && this.selectedAutoText.isServerBound()) {
            this.textFieldAutoTextServerAddress.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private void drawElementTextField(String prefix, GuiTextField textField, int x2, int y2, int mouseX, int mouseY) {
        prefix = String.valueOf(LanguageManager.translate("autotext_" + prefix)) + ":";
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), prefix, x2 + 2, y2 + 2, Integer.MAX_VALUE);
        GuiChatAutoText.drawRect(x2 + 2, y2 + 12, x2 + 2 + 114, y2 + 12 + 10, this.markMessageRed && textField != null && textField.equals(this.textFieldMessage) || this.markKeybindRed && textField != null && textField.equals(this.textFieldAutoTextKeyCode) ? ModColor.toRGB(200, 100, 100, 200) : Integer.MAX_VALUE);
        if (textField == null) {
            return;
        }
        LabyModCore.getMinecraft().setTextFieldXPosition(textField, x2 + 2);
        LabyModCore.getMinecraft().setTextFieldYPosition(textField, y2 + 13);
        textField.setEnableBackgroundDrawing(false);
        textField.setMaxStringLength(120);
    }

    private void drawElementCheckBox(String text, boolean check, int x2, int y2, int mouseX, int mouseY) {
        boolean hover = this.isHoverElementCheckbox(text, check, x2, y2, mouseX, mouseY);
        text = LanguageManager.translate("autotext_" + text);
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), text, x2 + 2, y2 + 2, Integer.MAX_VALUE);
        GuiChatAutoText.drawRect((x2 += LabyModCore.getMinecraft().getFontRenderer().getStringWidth(text) + 2) + 3, y2 + 1, x2 + 12, y2 + 10, hover ? 2147483547 : Integer.MAX_VALUE);
        if (!check) {
            return;
        }
        GuiChatAutoText.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl("a")) + "\u2714", x2 + 8, y2 + 1, Integer.MAX_VALUE);
    }

    private boolean isHoverElementCheckbox(String text, boolean check, int x2, int y2, int mouseX, int mouseY) {
        text = LanguageManager.translate("autotext_" + text);
        return mouseX > (x2 += LabyModCore.getMinecraft().getFontRenderer().getStringWidth(text) + 2) + 3 && mouseX < x2 + 12 && mouseY > y2 + 1 && mouseY < y2 + 10;
    }

    private void loadAutoText(AutoTextKeyBinds.AutoText autoText) {
        if (autoText == null) {
            return;
        }
        this.selectedAutoText = autoText;
        this.markMessageRed = false;
        this.markKeybindRed = false;
        this.textFieldMessage.setText(autoText.getMessage());
        this.textFieldAutoTextServerAddress.setText(autoText.getServerAddress() == null ? "" : autoText.getServerAddress());
        this.textFieldAutoTextKeyCode.setText(autoText.getKeyCode() == -1 ? "" : Keyboard.getKeyName(autoText.getKeyCode()));
    }
}

