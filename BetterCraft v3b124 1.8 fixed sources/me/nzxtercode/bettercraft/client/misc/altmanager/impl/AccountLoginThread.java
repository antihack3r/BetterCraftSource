/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager.impl;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAltManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAlteningLogin;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.AltService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class AccountLoginThread
extends Thread {
    private String email;
    private String password;
    public static boolean unknownBoolean1;
    private String status = "Waiting for login...";

    public AccountLoginThread(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public void run() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiAlteningLogin || GuiDisconnected.useTheAltening) {
            AltService.getInstance().switchToTheAltening();
            unknownBoolean1 = false;
            GuiDisconnected.useTheAltening = false;
        } else if (unknownBoolean1) {
            try {
                AltService.getInstance().switchService(AltService.EnumAltService.MOJANG);
            }
            catch (NoSuchFieldException e2) {
                System.out.println("Couldnt switch to modank altservice");
            }
            catch (IllegalAccessException e3) {
                System.out.println("Couldnt switch to modank altservice -2");
            }
        }
        if (this.password == null || this.password.isEmpty()) {
            Minecraft.getMinecraft();
            Minecraft.session = new Session(this.email, "", "", "mojang");
            this.status = "Logged in as " + EnumChatFormatting.RED.toString() + this.email;
            return;
        }
        unknownBoolean1 = true;
        this.status = "Logging in...";
        try {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            MicrosoftAuthResult result = this.email.contains("@") ? authenticator.loginWithCredentials(this.email, this.password) : new MicrosoftAuthResult(new MinecraftProfile("-", this.email, new MinecraftProfile.MinecraftSkin[0]), "-", "-", "-", "-");
            Session session = new Session(result.getProfile().getName(), result.getProfile().getId().toString(), result.getAccessToken(), this.email.contains("@") ? "mojang" : "legacy");
            Minecraft.getMinecraft();
            Minecraft.session = session;
            Account account = AccountManager.getInstance().getAccountByEmail(this.email);
            account = account == null ? new Account(this.email, this.password, session.getUsername()) : account;
            account.setName(session.getUsername());
            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiAlteningLogin) && !(Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected)) {
                AccountManager.getInstance().setLastAlt(account);
            }
            AccountManager.getInstance().save();
            GuiAltManager.getInstance().currentAccount = account;
            if (unknownBoolean1) {
                this.status = String.format("Logged in as %s", (Object)((Object)EnumChatFormatting.RED) + account.getName());
            }
        }
        catch (Exception exception) {
            this.status = exception.getMessage();
        }
    }

    public String getStatus() {
        return this.status;
    }
}

