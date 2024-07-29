/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.AddonInfoManager;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.gui.elements.CheckBox;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.main.listeners.AddonRecommendationListener;
import net.labymod.settings.LabyModAddonsGui;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiAddonRecommendation
extends GuiScreen {
    private List<RecommendedAddon> addons;
    private GuiButton buttonDone;
    private CheckBox checkBox;

    public GuiAddonRecommendation(List<RecommendedAddon> addons) {
        this.addons = addons;
    }

    @Override
    public void initGui() {
        super.initGui();
        boolean hasRequired = false;
        for (RecommendedAddon addon : this.addons) {
            OnlineAddonInfo info = addon.getOnlineAddonInfo();
            if (info == null || !addon.isRequired()) continue;
            hasRequired = true;
        }
        if (hasRequired) {
            this.buttonList.add(new GuiButton(1, width / 2 - 100 - 5, height - 50, 100, 20, LanguageManager.translate("button_cancel")));
            this.buttonDone = new GuiButton(0, width / 2 + 5, height - 50, 100, 20, LanguageManager.translate("button_done"));
            this.buttonList.add(this.buttonDone);
        } else {
            this.buttonDone = new GuiButton(0, width / 2 - 100, height - 50, LanguageManager.translate("button_done"));
            this.buttonList.add(this.buttonDone);
        }
        this.checkBox = new CheckBox("", CheckBox.EnumCheckBoxValue.DISABLED, null, 0, height - 80, 20, 20);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int amount = 0;
        boolean hasRequired = false;
        boolean allInstalled = true;
        for (RecommendedAddon addon : this.addons) {
            OnlineAddonInfo info = addon.getOnlineAddonInfo();
            if (info == null) continue;
            ++amount;
            if (!addon.isRequired()) continue;
            hasRequired = true;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int width = 300;
        int x2 = draw.getWidth() / 2 - 150;
        int y2 = draw.getHeight() / 2 - 42 * amount / 2;
        String translationKey = hasRequired ? "addon_recommendation_title_required" : "addon_recommendation_title_optional";
        ServerData serverData = LabyMod.getInstance().getCurrentServerData();
        String ip2 = serverData == null || serverData.getIp() == null ? "<server>" : serverData.getIp();
        String title = String.format(String.valueOf(ModColor.cl('a')) + LanguageManager.translate(translationKey), String.valueOf(ModColor.cl('E')) + ip2 + ModColor.cl('a'));
        draw.drawCenteredString(title, draw.getWidth() / 2, y2 - 20);
        for (RecommendedAddon addon2 : this.addons) {
            OnlineAddonInfo info2 = addon2.getOnlineAddonInfo();
            if (info2 == null) continue;
            info2.getAddonElement().draw(x2, y2, x2 + 300, y2 + 40, mouseX, mouseY, false);
            if (AddonLoader.hasInstalled(addon2.getOnlineAddonInfo())) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_CHECKBOX);
                draw.drawTexture(x2 - 25, y2 + 10, 255.0, 255.0, 20.0, 20.0);
            } else if (addon2.isRequired()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_EXCLAMATION);
                draw.drawTexture(x2 - 20, y2 + 10, 127.0, 255.0, 10.0, 20.0);
                allInstalled = false;
            } else {
                allInstalled = false;
            }
            if (addon2.isRequired()) {
                draw.drawString(String.valueOf(ModColor.cl('c')) + ModColor.cl('l') + LanguageManager.translate("addon_recommendation_required"), x2 + 300 + 5, y2 + 15);
            }
            y2 += 42;
        }
        if (!hasRequired) {
            String checkBoxLabel = LanguageManager.translate("addon_recommendation_ignore");
            this.checkBox.setX(draw.getWidth() / 2 - draw.getStringWidth(checkBoxLabel) / 2 - 25);
            this.checkBox.drawCheckbox(mouseX, mouseY);
            draw.drawCenteredString(checkBoxLabel, draw.getWidth() / 2, draw.getHeight() - 74);
        }
        boolean bl2 = this.buttonDone.enabled = !hasRequired || allInstalled;
        if (hasRequired && LabyModAddonsGui.isRestartRequired()) {
            this.buttonDone.displayString = LanguageManager.translate("button_restart");
            this.buttonDone.enabled = true;
        } else {
            this.buttonDone.displayString = LanguageManager.translate("button_done");
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        AddonRecommendationListener.sendMissingStatus(this.addons, true);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        boolean hasRequired = false;
        for (RecommendedAddon addon : this.addons) {
            OnlineAddonInfo info = addon.getOnlineAddonInfo();
            if (info == null || !addon.isRequired()) continue;
            hasRequired = true;
        }
        switch (button.id) {
            case 0: {
                boolean trusted;
                ServerData currentServer = LabyMod.getInstance().getCurrentServerData();
                boolean bl2 = trusted = currentServer != null && currentServer.getIp() != null && this.checkBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED;
                if (trusted) {
                    ArrayList<String> list = new ArrayList<String>(Arrays.asList(LabyMod.getSettings().ignoredAddonRecommendationServers));
                    String profileAddress = ModUtils.getProfileNameByIp(currentServer.getIp());
                    if (trusted && !list.contains(profileAddress)) {
                        list.add(profileAddress);
                        String[] array = new String[list.size()];
                        list.toArray(array);
                        LabyMod.getSettings().ignoredAddonRecommendationServers = array;
                        LabyMod.getMainConfig().save();
                    }
                }
                if (hasRequired && LabyModAddonsGui.isRestartRequired()) {
                    Minecraft.getMinecraft().shutdown();
                    break;
                }
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            }
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (RecommendedAddon addon : this.addons) {
            OnlineAddonInfo info = addon.getOnlineAddonInfo();
            if (info == null) continue;
            info.getAddonElement().mouseClicked(mouseX, mouseY, mouseButton);
        }
        boolean hasRequired = false;
        for (RecommendedAddon addon2 : this.addons) {
            OnlineAddonInfo info2 = addon2.getOnlineAddonInfo();
            if (info2 == null || !addon2.isRequired()) continue;
            hasRequired = true;
        }
        if (!hasRequired) {
            this.checkBox.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public static class RecommendedAddon {
        private UUID uuid;
        private boolean required;
        private OnlineAddonInfo onlineAddonInfo;

        public void bindAddon(AddonInfoManager manager) {
            AddonInfo addonInfo = manager.getAddonInfoMap().get(this.uuid);
            if (addonInfo instanceof OnlineAddonInfo) {
                this.onlineAddonInfo = (OnlineAddonInfo)addonInfo;
            }
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public boolean isRequired() {
            return this.required;
        }

        public OnlineAddonInfo getOnlineAddonInfo() {
            return this.onlineAddonInfo;
        }

        @ConstructorProperties(value={"uuid", "required", "onlineAddonInfo"})
        public RecommendedAddon(UUID uuid, boolean required, OnlineAddonInfo onlineAddonInfo) {
            this.uuid = uuid;
            this.required = required;
            this.onlineAddonInfo = onlineAddonInfo;
        }
    }
}

