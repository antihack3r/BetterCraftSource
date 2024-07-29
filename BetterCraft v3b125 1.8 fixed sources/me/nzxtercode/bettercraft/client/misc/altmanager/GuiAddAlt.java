/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiPasswordField;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiAddAlt
extends GuiScreen {
    private GuiTextField email;
    private GuiPasswordField password;
    private GuiScreen parent;

    public GuiAddAlt(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 + 12, "Add"));
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
                if (!this.email.getText().isEmpty()) {
                    Account account = new Account(this.email.getText(), this.password.getText(), this.email.getText());
                    AccountManager.getInstance().getAccounts().add(account);
                    AccountManager.getInstance().save();
                }
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.email.updateCursorCounter();
        this.password.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiAddAlt.drawCenteredString(this.mc.fontRendererObj, "Add Alt", width / 2, 20, -1);
        GuiAddAlt.drawCenteredString(this.mc.fontRendererObj, String.valueOf(EnumChatFormatting.GRAY.toString()) + "Username / Email", width / 2 - 60, 57, -1);
        this.email.drawTextBox();
        GuiAddAlt.drawCenteredString(this.mc.fontRendererObj, String.valueOf(EnumChatFormatting.GRAY.toString()) + "Password", width / 2 - 75, 107, -1);
        this.password.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

