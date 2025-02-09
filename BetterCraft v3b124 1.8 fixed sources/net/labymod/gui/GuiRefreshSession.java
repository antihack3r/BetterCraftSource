/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.util.List;
import net.labymod.account.LauncherProfile;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;

public class GuiRefreshSession
extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiButton refreshButton;
    private boolean displayError;
    private long cooldown;

    public GuiRefreshSession(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.cooldown = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, width / 2 - 155, height / 2 + 10, 125, 20, I18n.format("gui.toMenu", new Object[0])));
        this.refreshButton = new GuiButton(2, width / 2 - 25, height / 2 + 10, 180, 20, "");
        this.buttonList.add(this.refreshButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
        }
        if (button.id == 2) {
            this.cooldown = System.currentTimeMillis();
            boolean validToken = Minecraft.getMinecraft().getSession().getToken() != null && Minecraft.getMinecraft().getSession().getToken().length() > 2;
            final Consumer<Boolean> setSessionCallback = new Consumer<Boolean>(){

                @Override
                public void accept(Boolean accepted) {
                    if (accepted.booleanValue()) {
                        GuiRefreshSession.this.mc.displayGuiScreen(new GuiConnecting(GuiRefreshSession.this.parentScreen, Minecraft.getMinecraft(), Minecraft.getMinecraft().getCurrentServerData()));
                    } else {
                        GuiRefreshSession.this.displayError = true;
                    }
                }
            };
            final LauncherProfile launcherProfile = LabyMod.getInstance().getAccountManager().getAccount(LabyMod.getInstance().getPlayerUUID());
            if (launcherProfile != null && validToken) {
                launcherProfile.refresh(new Consumer<Boolean>(){

                    @Override
                    public void accept(Boolean accepted) {
                        if (accepted.booleanValue()) {
                            LabyMod.getInstance().getAccountManager().getAccountLoginHandler().setSession(launcherProfile.buildSession(), setSessionCallback);
                        } else {
                            GuiRefreshSession.this.displayError = true;
                        }
                    }
                });
            } else {
                LabyMod.getInstance().getAccountManager().getAccountLoginHandler().setSession(launcherProfile.buildSession(), setSessionCallback);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int cooldownCount;
        this.drawBackground(0);
        List<String> list = LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth(LanguageManager.translate("refresh_session"), width / 3, 4);
        int yS = 0;
        for (String s2 : list) {
            LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(ModColor.cl("c")) + s2, width / 2, height / 2 - 40 + yS);
            yS += 10;
        }
        if (this.displayError) {
            List<String> lines = LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth(LabyMod.getInstance().getAccountManager().getLastErrorMessage(), width / 2);
            int y2 = height / 2 - 70;
            for (String line : lines) {
                LabyModCore.getMinecraft().getFontRenderer().drawStringWithShadow(String.valueOf(ModColor.cl("4")) + line, width / 2 - LabyModCore.getMinecraft().getFontRenderer().getStringWidth(line) / 2, y2, 0xFFFFFF);
                y2 += 10;
            }
        }
        String cooldownCountdown = (cooldownCount = (int)(this.cooldown + 5000L - System.currentTimeMillis()) / 1000) > 0 ? " (" + cooldownCount + ")" : "";
        this.refreshButton.displayString = String.valueOf(LanguageManager.translate("refresh_session_refresh_try")) + cooldownCountdown;
        this.refreshButton.enabled = cooldownCount <= 0;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

