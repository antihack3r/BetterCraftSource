/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountLoginThread;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.AlteningAlt;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.TheAltening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiAlteningLogin
extends GuiScreen {
    private GuiScreen parent;
    public static AccountLoginThread thread;
    public static GuiTextField token;
    public static GuiTextField key;

    public GuiAlteningLogin(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, width / 2 + 2, height / 4 + 145, 98, 20, "Back"));
        this.buttonList.add(new GuiButton(-1, width / 2 - 100, height / 4 + 70, 98, 20, "Login"));
        this.buttonList.add(new GuiButton(4, width / 2 - 100, height / 4 + 95, "Generate (Web)"));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Generate and Login (Key)"));
        this.buttonList.add(new GuiButton(3, width / 2 + 2, height / 4 + 70, 98, 20, "Last Alt"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 145, 98, 20, "Multiplayer"));
        token = new GuiTextField(height / 4 + 24, this.mc.fontRendererObj, width / 2 - 98, height / 4 - 8, 196, 20);
        token.setMaxStringLength(Integer.MAX_VALUE);
        key = new GuiTextField(height / 4 + 22, this.mc.fontRendererObj, width / 2 - 98, height / 4 + 38, 196, 20);
        key.setMaxStringLength(Integer.MAX_VALUE);
        if (AccountManager.getInstance().getAlteningKey() != null) {
            key.setText(AccountManager.getInstance().getAlteningKey());
        }
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case -1: {
                if (token.getText().isEmpty()) {
                    return;
                }
                AccountManager.getInstance().setAlteningKey(key.getText());
                AccountManager.getInstance().setLastAlteningAlt(token.getText());
                thread = new AccountLoginThread(token.getText().replaceAll(" ", ""), "gaymer");
                thread.run();
                AccountManager.getInstance().save();
                break;
            }
            case 0: {
                if (key.getText().isEmpty()) {
                    return;
                }
                try {
                    TheAltening theAltening = new TheAltening(key.getText());
                    AlteningAlt account = theAltening.generateAccount(theAltening.getUser());
                    token.setText(Objects.requireNonNull(account).getToken());
                    AccountManager.getInstance().save();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                if (token.getText().isEmpty()) break;
                AccountManager.getInstance().setAlteningKey(key.getText());
                AccountManager.getInstance().setLastAlteningAlt(token.getText());
                thread = new AccountLoginThread(token.getText().replaceAll(" ", ""), "gaymer");
                thread.run();
                AccountManager.getInstance().save();
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 2: {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            }
            case 3: {
                if (key.getText().isEmpty() || AccountManager.getInstance().getLastAlteningAlt() == null) {
                    return;
                }
                AccountManager.getInstance().setAlteningKey(key.getText());
                thread = new AccountLoginThread(AccountManager.getInstance().getLastAlteningAlt().replaceAll(" ", ""), "gaymer");
                thread.run();
                AccountManager.getInstance().save();
                break;
            }
            case 4: {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI("https://fastalts.net/"));
                    break;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        if (character == '\t' && token.isFocused()) {
            token.setFocused(token.isFocused());
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        if (character == '\t' && GuiAlteningLogin.key.isFocused()) {
            GuiAlteningLogin.key.setFocused(GuiAlteningLogin.key.isFocused());
        }
        token.textboxKeyTyped(character, key);
        GuiAlteningLogin.key.textboxKeyTyped(character, key);
    }

    @Override
    public void updateScreen() {
        token.updateCursorCounter();
        key.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        token.mouseClicked(x2, y2, button);
        key.mouseClicked(x2, y2, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.getInstance().editConfig("Alterning", json -> json.add("apikey", new JsonPrimitive(key.getText())));
    }

    @Override
    public void drawScreen(int x2, int y2, float z2) {
        this.drawDefaultBackground();
        ScaledResolution sr2 = new ScaledResolution(this.mc);
        token.drawTextBox();
        key.drawTextBox();
        GuiAlteningLogin.drawCenteredString(this.mc.fontRendererObj, "The Altening Login", width / 2, sr2.getScaledHeight() / 4 - 50, 0xFFFFFF);
        GuiAlteningLogin.drawCenteredString(this.fontRendererObj, String.valueOf(EnumChatFormatting.RED.toString()) + Minecraft.session.getUsername(), width / 2, sr2.getScaledHeight() / 4 - 35, 0xFFFFFF);
        GuiAlteningLogin.drawCenteredString(this.fontRendererObj, "Token", width / 2 - 85, height / 4 - 20, 0xA0A0A0);
        GuiAlteningLogin.drawCenteredString(this.fontRendererObj, "Apikey", width / 2 - 85, height / 4 + 25, 0xA0A0A0);
        super.drawScreen(x2, y2, z2);
    }
}

