/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support;

import net.labymod.labyconnect.LabyConnect;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Session;

public class DashboardConnector {
    private long lastRequest = -1L;
    private boolean hoverIcon = false;

    public void renderIcon(int x2, int y2, int mouseX, int mouseY) {
        Session session = Minecraft.getMinecraft().getSession();
        LabyConnect labyConnect = LabyMod.getInstance().getLabyConnect();
        if (session == null || session.getProfile() == null || !labyConnect.isOnline() || !labyConnect.getClientConnection().isPinAvailable()) {
            return;
        }
        int iconSize = 20;
        int gearSize = 13;
        this.hoverIcon = mouseX > x2 - 10 && mouseX < x2 + 10 && mouseY > y2 - 10 && mouseY < y2 + 10;
        int hoverAnimation = this.hoverIcon ? 1 : 0;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (this.isOnCooldown()) {
            GlStateManager.color(0.5f, 0.5f, 0.5f);
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }
        draw.drawPlayerHead(session.getProfile(), x2 - 10 - hoverAnimation, y2 - 10 - hoverAnimation, 20 + hoverAnimation * 2);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_ADVANCED);
        draw.drawTexture(x2 - hoverAnimation, y2 - hoverAnimation, 255.0, 255.0, 13 + hoverAnimation * 2, 13 + hoverAnimation * 2);
        if (this.hoverIcon) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, LanguageManager.translate("open_dashboard_website"));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.hoverIcon) {
            this.openDashboard();
        }
    }

    private boolean isOnCooldown() {
        return System.currentTimeMillis() - this.lastRequest < 5000L;
    }

    public void openDashboard() {
        LabyConnect labyConnect = LabyMod.getInstance().getLabyConnect();
        if (!labyConnect.isOnline() || !labyConnect.getClientConnection().isPinAvailable()) {
            LabyMod.getInstance().openWebpage("http://www.labymod.net/dashboard", false);
        } else if (!this.isOnCooldown()) {
            this.lastRequest = System.currentTimeMillis();
            labyConnect.getClientConnection().requestPin(new Consumer<String>(){

                @Override
                public void accept(String pin) {
                    LabyMod.getInstance().openWebpage(String.format("http://www.labymod.net/key/?id=%s&pin=%s", LabyMod.getInstance().getPlayerUUID(), pin), false);
                }
            });
        }
    }
}

