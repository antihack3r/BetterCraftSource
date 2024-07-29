/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.awt.Color;
import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiAccountDirectLogin
extends GuiScreen {
    private final GuiScreen lastScreen;
    private final boolean saveAccount;
    private ModTextField fieldUsername;
    private ModTextField fieldPassword;
    private GuiButton buttonLogin;
    private boolean displayError = false;
    private long shakingError = 0L;
    private String renderHeadName;

    public GuiAccountDirectLogin(GuiScreen lastScreen, boolean saveAccount) {
        this.lastScreen = lastScreen;
        this.saveAccount = saveAccount;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.fieldUsername = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 110, height / 2 - 60, 220, 20);
        this.fieldPassword = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 110, height / 2 - 10, 220, 20);
        this.fieldPassword.setPasswordBox(true);
        this.fieldUsername.setMaxStringLength(90000);
        this.fieldPassword.setMaxStringLength(90000);
        this.buttonList.add(new GuiButton(1, width / 2 - 110, height / 2 + 30, 100, 20, LanguageManager.translate("button_cancel")));
        this.buttonLogin = new GuiButton(2, width / 2, height / 2 + 30, 110, 20, "");
        this.buttonList.add(this.buttonLogin);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.fieldUsername.drawTextBox();
        this.fieldPassword.drawTextBox();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawString(String.valueOf(LanguageManager.translate("username_email")) + ":", width / 2 - 110, height / 2 - 75);
        draw.drawString(String.valueOf(LanguageManager.translate("password")) + ":", width / 2 - 110, height / 2 - 25);
        String offlineString = !this.fieldUsername.getText().isEmpty() && this.fieldPassword.getText().isEmpty() ? String.valueOf(ModColor.cl("8")) + " (" + ModColor.cl("c") + LanguageManager.translate("offline") + ModColor.cl("8") + ")" : "";
        this.buttonLogin.displayString = String.valueOf(LanguageManager.translate(this.saveAccount ? "button_add" : "button_login")) + offlineString;
        boolean bl2 = this.buttonLogin.enabled = !this.fieldUsername.getText().isEmpty();
        if (this.displayError) {
            draw.drawRectangle(0, 0, width, 16, Color.RED.getRGB());
            String errorMessage = LabyMod.getInstance().getAccountManager().getLastErrorMessage();
            int shaking = (int)(System.currentTimeMillis() % 10L) - 5;
            if (this.shakingError + 1000L < System.currentTimeMillis()) {
                shaking = 0;
            }
            draw.drawCenteredString(errorMessage, width / 2 + shaking, 4.0);
        }
        if (this.renderHeadName != null && !this.renderHeadName.contains("@")) {
            LabyMod.getInstance().getDrawUtils().drawPlayerHead(this.renderHeadName, this.fieldUsername.xPosition - this.fieldUsername.height - 4, this.fieldUsername.yPosition, this.fieldUsername.height);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                break;
            }
            case 2: {
                this.login();
            }
        }
    }

    private void login() {
        String username = this.fieldUsername.getText();
        String password = this.fieldPassword.getText();
        Consumer<Boolean> result = new Consumer<Boolean>(){

            @Override
            public void accept(Boolean result) {
                if (result.booleanValue()) {
                    Minecraft.getMinecraft().displayGuiScreen(GuiAccountDirectLogin.this.lastScreen);
                } else {
                    GuiAccountDirectLogin.this.displayError = true;
                    GuiAccountDirectLogin.this.shakingError = System.currentTimeMillis();
                }
            }
        };
        if (password.isEmpty()) {
            LabyMod.getInstance().getAccountManager().getAccountLoginHandler().handleOfflineLogin(username, this.saveAccount, result);
        } else {
            LabyMod.getInstance().getAccountManager().getAccountLoginHandler().handleDirectLogin(username, password, this.saveAccount, result);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean flag = this.fieldUsername.isFocused();
        this.fieldUsername.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.fieldPassword.mouseClicked(mouseX, mouseY, mouseButton) && flag && !this.fieldUsername.isFocused()) {
            this.renderHeadName = this.fieldUsername.getText();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            return;
        }
        if (keyCode == 28 && this.buttonLogin.enabled) {
            this.actionPerformed(this.buttonLogin);
        }
        if (keyCode == 15) {
            boolean focusUsername = this.fieldUsername.isFocused();
            boolean focusPassword = this.fieldPassword.isFocused();
            this.fieldUsername.setFocused(focusPassword);
            this.fieldPassword.setFocused(focusUsername);
            if (this.fieldPassword.isFocused()) {
                this.renderHeadName = this.fieldUsername.getText();
            }
        }
        this.fieldUsername.textboxKeyTyped(typedChar, keyCode);
        this.fieldPassword.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.fieldUsername.updateCursorCounter();
        this.fieldPassword.updateCursorCounter();
    }
}

