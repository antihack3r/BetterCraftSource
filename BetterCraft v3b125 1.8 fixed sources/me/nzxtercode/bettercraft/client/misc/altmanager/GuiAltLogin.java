/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiPasswordField;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountLoginThread;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiAltLogin
extends GuiScreen {
    private GuiTextField email;
    private GuiPasswordField password;
    private AccountLoginThread loginThread;
    private GuiScreen parent;

    public GuiAltLogin(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 116 + 12, "Back"));
        this.email = new GuiTextField(0, this.fontRendererObj, width / 2 - 100, 70, 200, 20);
        this.email.setMaxStringLength(Integer.MAX_VALUE);
        this.email.setFocused(true);
        this.password = new GuiPasswordField(this.fontRendererObj, width / 2 - 100, 120, 200, 20);
        this.password.setMaxStringLength(Integer.MAX_VALUE);
    }

    @Override
    public void keyTyped(char character, int keyCode) throws IOException {
        this.email.textboxKeyTyped(character, keyCode);
        this.password.textboxKeyTyped(character, keyCode);
        if (keyCode == 15) {
            this.email.setFocused(!this.email.isFocused());
            this.password.setFocused(!this.password.isFocused());
        }
        if (keyCode == 28) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.email.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (this.email.getText().isEmpty()) break;
                if (this.email.getText().contains(":")) {
                    String[] split = this.email.getText().split(":");
                    Account account1 = new Account(split[0], split[1], split[0]);
                    this.loginThread = new AccountLoginThread(account1.getEmail(), account1.getPassword());
                    this.loginThread.start();
                }
                Account account = new Account(this.email.getText(), this.password.getText(), this.email.getText());
                this.loginThread = new AccountLoginThread(account.getEmail(), account.getPassword());
                this.loginThread.start();
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiAltLogin.drawCenteredString(this.mc.fontRendererObj, "Direct Login", width / 2, 20, -1);
        GuiAltLogin.drawCenteredString(this.mc.fontRendererObj, String.valueOf(EnumChatFormatting.GRAY.toString()) + "Username / Email", width / 2 - 60, 57, -1);
        this.email.drawTextBox();
        GuiAltLogin.drawCenteredString(this.mc.fontRendererObj, String.valueOf(EnumChatFormatting.GRAY.toString()) + "Password", width / 2 - 75, 107, -1);
        this.password.drawTextBox();
        GuiAltLogin.drawCenteredString(this.mc.fontRendererObj, this.loginThread == null ? String.valueOf(EnumChatFormatting.GRAY.toString()) + "Waiting for login..." : this.loginThread.getStatus(), width / 2, 35, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

