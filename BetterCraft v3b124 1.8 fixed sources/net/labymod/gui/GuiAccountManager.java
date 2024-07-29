/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.account.LauncherProfile;
import net.labymod.gui.GuiAccountDirectLogin;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class GuiAccountManager
extends GuiScreen {
    private final GuiScreen lastScreen;
    private GuiButton buttonSwitch;
    private GuiButton buttonRemove;
    private LauncherProfile selectedProfile;
    private LauncherProfile hoverProfile;
    private Scrollbar scrollbar;
    private long lastLoginClick = 0L;
    private boolean displayError = false;

    public GuiAccountManager(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
        LabyMod.getInstance().getAccountManager().loadLauncherProfiles();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 180, height - 25, 60, 20, LanguageManager.translate("button_add")));
        this.buttonRemove = new GuiButton(1, width / 2 - 110, height - 25, 60, 20, LanguageManager.translate("button_remove"));
        this.buttonList.add(this.buttonRemove);
        this.buttonSwitch = new GuiButton(2, width / 2 - 40, height - 25, 60, 20, LanguageManager.translate("button_switch"));
        this.buttonList.add(this.buttonSwitch);
        this.buttonList.add(new GuiButton(3, width / 2 + 30, height - 25, 80, 20, LanguageManager.translate("button_direct_login")));
        this.buttonList.add(new GuiButton(4, width / 2 + 120, height - 25, 60, 20, LanguageManager.translate("button_cancel")));
        this.scrollbar = new Scrollbar(21);
        this.scrollbar.setSpeed(15);
        this.scrollbar.setPosition(width / 2 + 100, 35, width / 2 + 104, height - 35);
        this.scrollbar.update(LabyMod.getInstance().getAccountManager().getLauncherProfiles().size());
        this.displayError = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.drawBackground(0);
        draw.drawOverlayBackground(0, 30, width, height - 30, 32);
        List<LauncherProfile> launcherProfiles = LabyMod.getInstance().getAccountManager().getLauncherProfiles();
        this.hoverProfile = null;
        int entryWidth = 132;
        double y2 = 35.0 + this.scrollbar.getScrollY();
        int x2 = width / 2 - 66;
        for (LauncherProfile launcherProfile : launcherProfiles) {
            this.drawProfile(launcherProfile, x2, y2, mouseX, mouseY);
            y2 += 21.0;
        }
        draw.drawOverlayBackground(0, 30);
        draw.drawOverlayBackground(height - 30, height);
        draw.drawGradientShadowTop(30.0, 0.0, width);
        draw.drawGradientShadowBottom(height - 30, 0.0, width);
        draw.drawCenteredString(String.valueOf(LanguageManager.translate("title_account_manager")) + " (" + launcherProfiles.size() + " " + LanguageManager.translate(launcherProfiles.size() == 1 ? "account" : "accounts") + ")", width / 2, 12.0);
        if (this.displayError) {
            draw.drawCenteredString(String.valueOf(ModColor.cl("4")) + LabyMod.getInstance().getAccountManager().getLastErrorMessage(), width / 2, height - 30 - 12);
        }
        this.scrollbar.draw();
        this.buttonSwitch.enabled = this.selectedProfile != null;
        this.buttonRemove.enabled = this.selectedProfile != null;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawProfile(LauncherProfile launcherProfile, int x2, double y2, int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int entryWidth = 132;
        int entryHeight = 20;
        boolean loggedIn = launcherProfile.getGameProfile().getId().equals(LabyMod.getInstance().getPlayerUUID());
        boolean hover = mouseX > x2 && mouseX < x2 + 132 && (double)mouseY > y2 && (double)mouseY < y2 + 20.0;
        boolean selected = launcherProfile == this.selectedProfile;
        boolean offline = launcherProfile.getAccessToken().isEmpty();
        double selectAnimation = (double)(System.currentTimeMillis() - this.lastLoginClick) / 2.0;
        if (selectAnimation > 100.0) {
            selectAnimation = 100.0;
        }
        if (selected) {
            draw.drawRectBorder(x2, y2, x2 + 132, y2 + 20.0, Integer.MAX_VALUE, 1.0);
        } else if (hover) {
            draw.drawRectangle(x2, (int)y2, x2 + 132, (int)(y2 + 20.0), ModColor.toRGB(120, 120, 120, 100));
        }
        if (selected) {
            draw.drawRectangle(x2, (int)y2, x2 + 132, (int)(y2 + 20.0), ModColor.toRGB(120, 120, 120, 100 - (int)selectAnimation));
        }
        float headBrightness = selected || hover | loggedIn ? 1.0f : 0.7f;
        GlStateManager.color(headBrightness, headBrightness, headBrightness);
        LabyMod.getInstance().getDrawUtils().drawPlayerHead(launcherProfile.getGameProfile(), x2 + 2, (int)y2 + 2, 16);
        String namePrefix = offline ? ModColor.cl("c") : (loggedIn ? ModColor.cl("a") : "");
        String nameSuffix = offline ? String.valueOf(ModColor.cl("4")) + " (" + LanguageManager.translate("offline") + ")" : "";
        draw.drawString(String.valueOf(namePrefix) + launcherProfile.getGameProfile().getName() + nameSuffix, x2 + 2 + 16 + 4, y2 + 3.0);
        draw.drawString(String.valueOf(ModColor.cl("7")) + launcherProfile.getGameProfile().getId().toString(), x2 + 2 + 16 + 4, y2 + 3.0 + 10.0, 0.5);
        if (loggedIn) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_CHECKBOX);
            draw.drawTexture(x2 - 15, y2 + 5.0, 255.0, 255.0, 10.0, 10.0);
        }
        if (hover) {
            this.hoverProfile = launcherProfile;
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, launcherProfile.getMojangUsername());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseY < height - 30) {
            this.selectedProfile = this.hoverProfile;
        }
        if (this.selectedProfile != null && this.hoverProfile != null) {
            if (this.lastLoginClick + 200L > System.currentTimeMillis()) {
                this.actionPerformed(this.buttonSwitch);
            }
            this.lastLoginClick = System.currentTimeMillis();
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiAccountDirectLogin(this, true));
                break;
            }
            case 1: {
                ArrayList<LauncherProfile> list = new ArrayList<LauncherProfile>(LabyMod.getInstance().getAccountManager().getLauncherProfiles());
                list.remove(this.selectedProfile);
                LabyMod.getInstance().getAccountManager().setLauncherProfiles(list);
                this.selectedProfile = null;
                LabyMod.getInstance().getAccountManager().saveLauncherProfiles(new Consumer<Boolean>(){

                    @Override
                    public void accept(Boolean accepted) {
                        if (!accepted.booleanValue()) {
                            GuiAccountManager.this.displayError = true;
                        }
                    }
                });
                break;
            }
            case 2: {
                this.selectedProfile.login(new Consumer<Boolean>(){

                    @Override
                    public void accept(Boolean response) {
                        if (response.booleanValue()) {
                            Minecraft.getMinecraft().displayGuiScreen(GuiAccountManager.this.lastScreen);
                        } else {
                            GuiAccountManager.this.displayError = true;
                        }
                    }
                });
                break;
            }
            case 3: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiAccountDirectLogin(this, false));
                break;
            }
            case 4: {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}

