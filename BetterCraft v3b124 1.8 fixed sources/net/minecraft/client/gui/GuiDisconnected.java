/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.gui.GuiProtocolSelector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAltManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAlteningLogin;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountLoginThread;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.AlteningAlt;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.TheAltening;
import me.nzxtercode.bettercraft.client.utils.ProxyUtils;
import me.nzxtercode.bettercraft.client.utils.TextAnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected
extends GuiScreen {
    public static ServerData serverData;
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private int field_175353_i;
    private String[] animationText;
    public static boolean useTheAltening;
    private String status = "What do you want to do?";
    private final GuiScreen parent;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
        this.parent = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        if (this.field_175353_i > height || height / 2 + this.field_175353_i / 2 + 101 > height) {
            this.multilineMessage = Arrays.asList("To large message");
            this.field_175353_i = this.fontRendererObj.FONT_HEIGHT;
        }
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + this.field_175353_i / 2 + 81, I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(1, width / 2 + 103, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, 90, 20, "Altmanager"));
        this.buttonList.add(new GuiButton(2, width / 2 + 103, height / 2 + this.field_175353_i / 2 + 33, 90, 20, "Set Banned"));
        this.buttonList.add(new GuiButton(4, width / 2 + 103, height / 2 + this.field_175353_i / 2 + 57, 90, 20, "Remove Alt"));
        this.buttonList.add(new GuiButton(5, width / 2 - 100, height / 2 + this.field_175353_i / 2 + 33, 98, 20, "Relog not banned"));
        this.buttonList.add(new GuiButton(6, width / 2 + 2, height / 2 + this.field_175353_i / 2 + 33, 98, 20, "Relog banned"));
        this.buttonList.add(new GuiButton(8, width / 2 - 100, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Relog"));
        this.buttonList.add(new GuiButton(7, width / 2 + 2, height / 2 + this.field_175353_i / 2 + 57, 98, 20, "Relog The Altening"));
        this.buttonList.add(new GuiButton(9, width / 2 - 100, height / 2 + this.field_175353_i / 2 + 57, 98, 20, "Relog with Proxy"));
        this.buttonList.add(new GuiButton(10, width / 2 + 103, height / 2 + this.field_175353_i / 2 + 81, 90, 20, ViaLoadingBase.getInstance().getTargetVersion().getName()));
        this.animationText = new String[this.multilineMessage.size()];
        this.multilineMessage.forEach(message -> TextAnimationUtils.writeTextAnimation(message, 20L, value -> {
            String string2 = this.animationText[this.multilineMessage.indexOf((Object)string)] = value;
        }, null));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        }
        if (button.id == 0 || button.id == 1) {
            Minecraft.getMinecraft().setServerData(null);
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(GuiAltManager.getInstance());
        }
        if (button.id == 2) {
            if (GuiAltManager.getInstance().currentAccount == null) {
                this.status = "You are not using any alt...";
                return;
            }
            GuiAltManager.getInstance().currentAccount.setBanned(true);
            this.status = "The account has been set to &obanned.";
        }
        if (button.id == 4) {
            if (GuiAltManager.getInstance().currentAccount == null) {
                this.status = "You are not using any alt...";
                return;
            }
            if (GuiAltManager.getInstance().loginThread != null) {
                GuiAltManager.getInstance().loginThread = null;
            }
            AccountManager.getInstance().getAccounts().remove(GuiAltManager.getInstance().currentAccount);
            AccountManager.getInstance().save();
            AccountManager.getInstance().setLastAlt(null);
            GuiAltManager.getInstance().currentAccount = null;
            this.status = "The alt has been removed succesfully.";
        }
        if (button.id == 5 || button.id == 6) {
            ArrayList<Account> registry = button.id == 5 ? AccountManager.getInstance().getNotBannedAccounts() : AccountManager.getInstance().getAccounts();
            Random random = new Random();
            if (registry.size() == 0) {
                this.status = "You don't have any account eligible for this.";
                return;
            }
            Account randomAlt = registry.get(random.nextInt(registry.size()));
            String user2 = randomAlt.getEmail();
            String pass2 = randomAlt.getPassword();
            GuiAltManager.getInstance().currentAccount = randomAlt;
            try {
                GuiAltManager.getInstance().loginThread = new AccountLoginThread(user2, pass2);
                GuiAltManager.getInstance().loginThread.start();
                AccountManager.getInstance().save();
                if (serverData != null) {
                    this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, serverData));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (button.id == 7) {
            if (AccountManager.getInstance().getAlteningKey() == null) {
                this.status = "No TheAltening key...";
                return;
            }
            useTheAltening = true;
            try {
                TheAltening theAltening = new TheAltening(AccountManager.getInstance().getAlteningKey());
                AlteningAlt account = theAltening.generateAccount(theAltening.getUser());
                if (!Objects.requireNonNull(account).getToken().isEmpty()) {
                    AccountManager.getInstance().setAlteningKey(AccountManager.getInstance().getAlteningKey());
                    AccountManager.getInstance().setLastAlteningAlt(Objects.requireNonNull(account).getToken());
                    GuiAlteningLogin.thread = new AccountLoginThread(Objects.requireNonNull(account).getToken().replaceAll(" ", ""), "nig");
                    GuiAlteningLogin.thread.start();
                    AccountManager.getInstance().save();
                }
                if (serverData != null) {
                    this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, serverData));
                }
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        if (button.id == 8 && serverData != null) {
            this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, serverData));
        }
        if (button.id == 9 && serverData != null && !GuiScreenServerList.proxy.getText().isEmpty()) {
            ProxyUtils.setProxy(GuiScreenServerList.isEnabled ? null : ProxyUtils.getProxyFromString(GuiScreenServerList.proxy.getText()));
            GuiScreenServerList.isEnabled = !GuiScreenServerList.isEnabled;
            this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, serverData));
        }
        if (button.id == 10) {
            this.mc.displayGuiScreen(new GuiProtocolSelector(this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiDisconnected.drawCenteredString(this.fontRendererObj, this.status, width / 2, 10, 0xFFFFFF);
        GuiDisconnected.drawCenteredString(this.fontRendererObj, this.reason, width / 2, height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 0xAAAAAA);
        int i2 = height / 2 - this.field_175353_i / 2;
        if (this.multilineMessage != null) {
            if (GuiUISettings.enabledAnimations[2]) {
                String[] stringArray = this.animationText;
                int n2 = this.animationText.length;
                int n3 = 0;
                while (n3 < n2) {
                    String s2 = stringArray[n3];
                    GuiDisconnected.drawCenteredString(this.fontRendererObj, s2, width / 2, i2, 0xFFFFFF);
                    i2 += this.fontRendererObj.FONT_HEIGHT;
                    ++n3;
                }
            } else {
                for (String s3 : this.multilineMessage) {
                    GuiDisconnected.drawCenteredString(this.fontRendererObj, s3, width / 2, i2, 0xFFFFFF);
                    i2 += this.fontRendererObj.FONT_HEIGHT;
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

