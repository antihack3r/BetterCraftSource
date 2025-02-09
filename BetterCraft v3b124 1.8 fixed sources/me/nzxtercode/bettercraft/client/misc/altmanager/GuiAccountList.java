/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import me.nzxtercode.bettercraft.client.misc.altmanager.GuiAltManager;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.Account;
import me.nzxtercode.bettercraft.client.misc.altmanager.impl.AccountManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class GuiAccountList
extends GuiSlot {
    public int selected = -1;
    private GuiAltManager parent;

    public GuiAccountList(GuiAltManager parent) {
        super(Minecraft.getMinecraft(), GuiAltManager.width, GuiAltManager.height, 36, GuiAltManager.height - 56, 40);
        this.parent = parent;
    }

    @Override
    public int getSize() {
        return AccountManager.getInstance().getAccounts().size();
    }

    @Override
    public void elementClicked(int i2, boolean b2, int i1, int i22) {
        this.selected = i2;
        if (b2) {
            this.parent.login(this.getAccount(i2));
        }
    }

    @Override
    protected boolean isSelected(int i2) {
        return i2 == this.selected;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int i2, int i1, int i22, int i3, int i4, int i5) {
        Account account = this.getAccount(i2);
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(minecraft);
        FontRenderer fontRenderer = minecraft.fontRendererObj;
        int x2 = i1 + 2;
        int y2 = i22;
        if (y2 >= scaledResolution.getScaledHeight() || y2 < 0) {
            return;
        }
        GL11.glTranslated(x2, y2, 0.0);
        this.drawFace(account.getName(), 0, 6, 24, 24);
        fontRenderer.drawStringWithShadow(account.getName(), 30.0f, 6.0f, -1);
        fontRenderer.drawStringWithShadow(account.getEmail(), 30.0f, 6 + fontRenderer.FONT_HEIGHT + 2, -1);
        GL11.glTranslated(-x2, -y2, 0.0);
    }

    public Account getAccount(int i2) {
        return AccountManager.getInstance().getAccounts().get(i2);
    }

    private void drawFace(String name, int x2, int y2, int w2, int h2) {
        try {
            AbstractClientPlayer.getDownloadImageSkin(AbstractClientPlayer.getLocationSkin(name), name).loadTexture(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().getTextureManager().bindTexture(AbstractClientPlayer.getLocationSkin(name));
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawModalRectWithCustomSizedTexture(x2, y2, 24.0f, 24.0f, w2, h2, 192.0f, 192.0f);
            Gui.drawModalRectWithCustomSizedTexture(x2, y2, 120.0f, 24.0f, w2, h2, 192.0f, 192.0f);
            GL11.glDisable(3042);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void removeSelected() {
        if (this.selected == -1) {
            return;
        }
        AccountManager.getInstance().getAccounts().remove(this.getAccount(this.selected));
        AccountManager.getInstance().save();
    }

    public Account getSelectedAccount() {
        return this.getAccount(this.selected);
    }
}

