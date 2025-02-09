/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAccountList;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAddAlt;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAltLogin;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAlteningLogin;
import me.nzxtercode.bettercraft.client.misc.altmanager.GuiSessionLogin;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountLoginThread;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.thealtening.AltService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;

public class GuiAltManager
extends GuiScreen {
    private static GuiScreen parent;
    private static final GuiAltManager INSTANCE;
    private GuiAccountList accountList;
    public Account currentAccount;
    public AccountLoginThread loginThread;
    private String status = "Waiting for login...";

    static {
        INSTANCE = new GuiAltManager(parent);
    }

    public GuiAltManager(GuiScreen parent) {
        GuiAltManager.parent = parent;
    }

    public static GuiAltManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void initGui() {
        this.accountList = new GuiAccountList(this);
        this.accountList.registerScrollButtons(7, 8);
        this.accountList.elementClicked(-1, false, 0, 0);
        this.buttonList.add(new GuiButton(0, width / 2 + 150, height - 24, 50, 20, "Back"));
        this.buttonList.add(new GuiButton(10, width / 2 - 200, height - 48, 50, 20, "Session"));
        this.buttonList.add(new GuiButton(2, width / 2 - 25, height - 24, 50, 20, "Remove"));
        this.buttonList.add(new GuiButton(3, width / 2 - 115, height - 24, 50, 20, "Add"));
        this.buttonList.add(new GuiButton(4, width / 2 - 25, height - 48, 50, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(5, width / 2 + 4 + 60, height - 48, 50, 20, "Import"));
        this.buttonList.add(new GuiButton(6, width / 2 - 200, height - 24, 50, 20, "TheAltening"));
        this.buttonList.add(new GuiButton(7, width / 2 + 4 + 60, height - 24, 50, 20, "Random Alt"));
        this.buttonList.add(new GuiButton(8, width / 2 - 115, height - 48, 50, 20, "Last Alt"));
        this.buttonList.add(new GuiButton(9, width / 2 + 150, height - 48, 50, 20, "Clear"));
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.drawDefaultBackground();
        this.accountList.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        if (this.loginThread != null) {
            this.status = this.loginThread.getStatus();
        }
        if (AltService.getInstance().getCurrentService().equals((Object)AltService.EnumAltService.MOJANG)) {
            GuiAltManager.drawCenteredString(this.mc.fontRendererObj, this.status, scaledResolution.getScaledWidth() / 2, 6, -3158065);
            GuiAltManager.drawCenteredString(this.mc.fontRendererObj, "Accounts: " + EnumChatFormatting.RED.toString() + AccountManager.getInstance().getAccounts().size(), width / 2, 20, -1);
        } else {
            GuiAltManager.drawCenteredString(this.mc.fontRendererObj, "Logged in with " + EnumChatFormatting.RED.toString() + "TheAltening", width / 2, 20, -1);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.accountList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                if (this.loginThread != null && this.loginThread.getStatus().contains("Logging in")) break;
                this.mc.displayGuiScreen(new GuiMainMenu());
                break;
            }
            case 1: {
                if (this.accountList.selected == -1) {
                    return;
                }
                this.loginThread = new AccountLoginThread(this.accountList.getSelectedAccount().getEmail(), this.accountList.getSelectedAccount().getPassword());
                this.loginThread.start();
                break;
            }
            case 2: {
                this.accountList.removeSelected();
                this.accountList.selected = -1;
                break;
            }
            case 3: {
                if (this.loginThread != null) {
                    this.loginThread = null;
                }
                this.mc.displayGuiScreen(new GuiAddAlt(this));
                break;
            }
            case 4: {
                if (this.loginThread != null) {
                    this.loginThread = null;
                }
                this.mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            }
            case 6: {
                this.mc.displayGuiScreen(new GuiAlteningLogin(this));
                break;
            }
            case 7: {
                Random random;
                if (AccountManager.getInstance().getAccounts().size() == 0) {
                    return;
                }
                ArrayList<Account> registry = AccountManager.getInstance().getAccounts();
                Account randomAlt = registry.get((random = new Random()).nextInt(AccountManager.getInstance().getAccounts().size()));
                if (randomAlt.isBanned()) {
                    return;
                }
                this.currentAccount = randomAlt;
                this.login(randomAlt);
                break;
            }
            case 5: {
                JFrame frame = new JFrame("Import alts");
                JFileChooser chooser = new JFileChooser();
                frame.add(chooser);
                frame.pack();
                int returnVal = chooser.showOpenDialog(frame);
                if (returnVal == 0) {
                    frame.dispatchEvent(new WindowEvent(frame, 201));
                    try {
                        for (String line : Files.readAllLines(Paths.get(chooser.getSelectedFile().getPath(), new String[0]))) {
                            if (line.contains(":")) {
                                String[] parts = line.split(":");
                                Account account = new Account(parts[0], parts[1], parts[0]);
                                AccountManager.getInstance().getAccounts().add(account);
                                continue;
                            }
                            break;
                        }
                    }
                    catch (MalformedInputException e2) {
                        e2.printStackTrace();
                        this.status = "There has been an error importing the alts.";
                    }
                }
                AccountManager.getInstance().save();
                break;
            }
            case 8: {
                if (AccountManager.getInstance().getLastAlt() == null) {
                    return;
                }
                this.loginThread = new AccountLoginThread(AccountManager.getInstance().getLastAlt().getEmail(), AccountManager.getInstance().getLastAlt().getPassword());
                this.loginThread.start();
                break;
            }
            case 9: {
                if (AccountManager.getInstance().getAccounts().isEmpty()) {
                    return;
                }
                AccountManager.getInstance().getAccounts().clear();
                break;
            }
            case 10: {
                this.mc.displayGuiScreen(new GuiSessionLogin(this));
            }
        }
    }

    public void login(Account account) {
        this.loginThread = new AccountLoginThread(account.getEmail(), account.getPassword());
        this.loginThread.start();
    }

    public static void drawEntityOnScreen(int posX, int posY, float scale, float yawRotate, float pitchRotate, EntityLivingBase ent) {
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.translate(posX, posY, 50.0f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(pitchRotate, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(yawRotate, 0.0f, 1.0f, 0.0f);
        float f2 = ent.renderYawOffset;
        float f3 = ent.rotationYaw;
        float f4 = ent.rotationPitch;
        float f5 = ent.prevRotationYawHead;
        float f6 = ent.rotationYawHead;
        RenderHelper.enableStandardItemLighting();
        ent.renderYawOffset = (float)Math.atan(yawRotate / 40.0f);
        ent.rotationYaw = (float)Math.atan(yawRotate / 40.0f);
        ent.rotationPitch = -((float)Math.atan(0.0)) * 20.0f;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        try {
            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.setPlayerViewY(180.0f);
            rendermanager.setRenderShadow(false);
            rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, true);
            rendermanager.setRenderShadow(true);
        }
        finally {
            ent.renderYawOffset = f2;
            ent.rotationYaw = f3;
            ent.rotationPitch = f4;
            ent.prevRotationYawHead = f5;
            ent.rotationYawHead = f6;
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.translate(0.0f, 0.0f, 20.0f);
        }
    }
}

