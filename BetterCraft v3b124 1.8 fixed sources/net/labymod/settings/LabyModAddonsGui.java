/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.AddonInfoManager;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.api.LabyModAddon;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.AddonElement;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class LabyModAddonsGui
extends GuiScreen {
    private final GuiScreen lastScreen;
    private final Scrollbar scrollbar = new Scrollbar(1);
    private GuiButton buttonDone;
    private GuiButton buttonBack;
    private GuiButton buttonWarningExitGame;
    private GuiButton buttonWarningRestartLater;
    private ModTextField fieldSearch;
    private boolean displayRestartWarning = false;
    private AddonElement openedAddonSettings = null;
    private AddonElement mouseOverAddonEntry;
    private SettingsElement mouseOverElement;
    private List<SettingsElement> listedElementsStored = new ArrayList<SettingsElement>();
    private final List<SettingsElement> tempElementsStored = new ArrayList<SettingsElement>();
    private final ArrayList<SettingsElement> path = new ArrayList();
    private double preScrollPos = 0.0;
    private EnumSortingState selectedSortingState = EnumSortingState.TRENDING;
    private EnumSortingState hoveredSortingState = null;
    private List<AddonInfo> sortedAddonInfoList = AddonInfoManager.getInstance().getAddonInfoList();
    private int[] enabledFilter = null;

    public LabyModAddonsGui(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
        AddonInfoManager.getInstance().init();
        if (!AddonLoader.getAddons().isEmpty()) {
            this.selectedSortingState = EnumSortingState.INSTALLED;
        }
        if (AddonInfoManager.getInstance().isLoaded()) {
            this.handleCheckBox(false, 0, 0, 0);
        }
    }

    public static boolean isRestartRequired() {
        for (LabyModAddon labyModAddon : AddonLoader.getAddons()) {
            if (labyModAddon.about == null || !labyModAddon.about.deleted) continue;
            return true;
        }
        for (AddonInfo addonInfo : AddonInfoManager.getInstance().getAddonInfoList()) {
            AddonElement addonElement = addonInfo.getAddonElement();
            if (addonElement.getLastActionState() != AddonInfo.AddonActionState.INSTALL) continue;
            return true;
        }
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.doQuery();
        this.sortAddonInfoList();
        this.scrollbar.setPosition(width / 2 + 152, 80, width / 2 + 152 + 4, height - 35);
        this.scrollbar.setSpeed(20);
        this.scrollbar.init();
        this.buttonDone = new GuiButton(0, width / 2 + 50, height - 25, 100, 20, LanguageManager.translate("button_done"));
        this.buttonList.add(this.buttonDone);
        this.buttonBack = new GuiButton(1, width / 2 - 100, 50, 22, 20, "<");
        this.buttonList.add(this.buttonBack);
        this.buttonWarningRestartLater = new GuiButton(2, width / 2 - 95, height / 2 + 20, 90, 20, LanguageManager.translate("button_restart_later"));
        this.buttonList.add(this.buttonWarningRestartLater);
        this.buttonWarningExitGame = new GuiButton(3, width / 2 + 5, height / 2 + 20, 90, 20, LanguageManager.translate("button_exit_game"));
        this.buttonList.add(this.buttonWarningExitGame);
        this.fieldSearch = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 150, 35, 300, 14);
        this.fieldSearch.setBlackBox(false);
        this.fieldSearch.setPlaceHolder(LanguageManager.translate("search_textbox_placeholder"));
        Keyboard.enableRepeatEvents(true);
        if (this.isInSubSettings()) {
            for (SettingsElement settingsElement : this.listedElementsStored) {
                settingsElement.init();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        for (LabyModAddon addon : AddonLoader.getAddons()) {
            addon.saveConfig();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawAutoDimmedBackground(this.scrollbar.getScrollY());
        this.drawElementList(mouseX, mouseY);
        if (!AddonInfoManager.getInstance().isLoaded() || AddonInfoManager.getInstance().getAddonInfoList().isEmpty()) {
            draw.drawCenteredString(LanguageManager.translate("button_no_addons_available", Source.ABOUT_MC_VERSION), width / 2, height / 2);
        }
        draw.drawOverlayBackground(0, 75);
        draw.drawGradientShadowTop(75.0, 0.0, width);
        draw.drawOverlayBackground(height - 30, height);
        draw.drawGradientShadowBottom(height - 30, 0.0, width);
        this.scrollbar.draw(mouseX, mouseY);
        this.buttonDone.enabled = !this.displayRestartWarning;
        this.buttonBack.visible = this.openedAddonSettings != null;
        this.buttonBack.enabled = !this.displayRestartWarning;
        this.buttonWarningExitGame.visible = this.displayRestartWarning;
        this.buttonWarningRestartLater.visible = this.displayRestartWarning;
        draw.drawCenteredString(LanguageManager.translate("button_labymod_addons"), width / 2, 20.0);
        if (AddonInfoManager.getInstance().isLoaded()) {
            if (!this.isInSubSettings()) {
                this.drawSortingTabs(mouseX, mouseY);
            } else {
                draw.drawString(this.openedAddonSettings.getAddonInfo().getName(), width / 2 - 100 + 30, 55.0);
                this.openedAddonSettings.drawIcon(width / 2 + 100 - 20, 50, 20, 20);
            }
        }
        if (AddonInfoManager.getInstance().isLoaded()) {
            this.drawCategoryFilterMenu(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.displayRestartWarning) {
            draw.drawIngameBackground();
            draw.drawRectangle(width / 2 - 100 - 2, height / 2 - 50 - 2, width / 2 + 100 + 2, height / 2 + 50 + 2, Integer.MIN_VALUE);
            draw.drawRectangle(width / 2 - 100, height / 2 - 50, width / 2 + 100, height / 2 + 50, ModColor.toRGB(20, 20, 20, 144));
            LabyModCore.getMinecraft().drawButton(this.buttonWarningRestartLater, mouseX, mouseY);
            LabyModCore.getMinecraft().drawButton(this.buttonWarningExitGame, mouseX, mouseY);
            draw.drawCenteredString(LanguageManager.translate("warning_title"), width / 2, height / 2 - 44);
            List<String> list = draw.listFormattedStringToWidth(LanguageManager.translate("warning_content"), 190);
            int lineY = 0;
            for (String line : list) {
                draw.drawCenteredString(String.valueOf(ModColor.cl("4")) + line, width / 2, height / 2 - 30 + lineY);
                lineY += 10;
            }
        }
        if (this.fieldSearch != null && !this.isInSubSettings()) {
            this.fieldSearch.drawTextBox();
        }
        if (this.openedAddonSettings != null) {
            this.openedAddonSettings.onRenderPreview(mouseX, mouseY, partialTicks);
        }
    }

    private void drawCategoryFilterMenu(int mouseX, int mouseY) {
        int x2 = width / 2 - 145;
        int y2 = height - 20;
        CheckBox[] checkBoxArray = AddonInfoManager.getInstance().getCategorieCheckboxList();
        int n2 = checkBoxArray.length;
        int n3 = 0;
        while (n3 < n2) {
            CheckBox checkBox = checkBoxArray[n3];
            checkBox.setX(x2);
            checkBox.setY(y2);
            checkBox.drawCheckbox(mouseX, mouseY);
            x2 += 25;
            ++n3;
        }
    }

    private void doQuery() {
        this.tempElementsStored.clear();
        if (this.openedAddonSettings != null) {
            if (this.path.isEmpty()) {
                this.tempElementsStored.addAll(this.openedAddonSettings.getSubSettings());
            } else {
                SettingsElement currentOpenElement = this.path.get(this.path.size() - 1);
                this.tempElementsStored.addAll(currentOpenElement.getSubSettings().getElements());
            }
        }
        this.listedElementsStored = this.tempElementsStored;
    }

    private void drawSortingTabs(int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int tabX = width / 2 - 150;
        int tabY = 63;
        this.hoveredSortingState = null;
        if (AddonInfoManager.getInstance().isLoaded()) {
            EnumSortingState[] enumSortingStateArray = EnumSortingState.values();
            int n2 = enumSortingStateArray.length;
            int n3 = 0;
            while (n3 < n2) {
                EnumSortingState sortingState = enumSortingStateArray[n3];
                boolean selected = this.selectedSortingState == sortingState;
                int tabLen = draw.getStringWidth(sortingState.getDisplayName()) + 6 + (selected ? 6 : 0);
                String prefix = "";
                if (sortingState == EnumSortingState.INSTALLED) {
                    tabX = width / 2 + 150 - tabLen;
                    prefix = ModColor.cl(selected ? "6" : "8");
                }
                this.drawSingleTab(sortingState, prefix, tabX, 63, tabLen, selected, mouseX, mouseY);
                tabX += tabLen + 1;
                ++n3;
            }
        }
    }

    private void drawSingleTab(EnumSortingState sortingState, String prefix, int tabX, int tabY, int length, boolean selected, int mouseX, int mouseY) {
        boolean hovered;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int tabHeight = 12;
        boolean bl2 = hovered = mouseX > tabX && mouseX < tabX + length && mouseY > tabY && mouseY < tabY + 12;
        int animate = selected || hovered ? (selected ? 2 : 1) : 0;
        int tabMid = tabX + length / 2;
        draw.drawRectangle(tabX, tabY - animate * 2, tabX + length, tabY + 12, ModColor.toRGB(5, 5, 5, 140));
        draw.drawRectBorder(tabX, tabY - animate * 2, tabX + length, tabY + 12, ModColor.toRGB(5, 5, 5, 140), 1.0);
        if (selected) {
            draw.drawRectangle(tabX + 1, tabY - animate * 2 + 1, tabX + length - 1, tabY + 12, ModColor.toRGB(55, 55, 55, 65));
        }
        draw.drawCenteredString(String.valueOf(selected || hovered ? ModColor.cl("f") : ModColor.cl("8")) + prefix + sortingState.getDisplayName(), tabMid, tabY + 2 - animate);
        if (hovered) {
            this.hoveredSortingState = sortingState;
        }
    }

    private void drawElementList(int mouseX, int mouseY) {
        double totalEntryHeight = 0.0;
        this.mouseOverAddonEntry = null;
        this.mouseOverElement = null;
        boolean canRenderDescription = true;
        int count = 0;
        if (this.isInSubSettings()) {
            int zLevel = 0;
            while (zLevel < 2) {
                totalEntryHeight = 0.0;
                double listY = 80.0 + this.scrollbar.getScrollY();
                this.listedElementsStored = Lists.reverse(this.listedElementsStored);
                for (SettingsElement settingsElement : this.listedElementsStored) {
                    listY += (double)(settingsElement.getEntryHeight() + 2);
                    totalEntryHeight += (double)(settingsElement.getEntryHeight() + 2);
                }
                for (SettingsElement settingsElement : this.listedElementsStored) {
                    listY -= (double)(settingsElement.getEntryHeight() + 2);
                    totalEntryHeight -= (double)(settingsElement.getEntryHeight() + 2);
                    if ((settingsElement instanceof DropDownElement && !(settingsElement instanceof ColorPickerCheckBoxBulkElement) || zLevel != 0) && (!(settingsElement instanceof DropDownElement) && !(settingsElement instanceof ColorPickerCheckBoxBulkElement) || zLevel != 1)) continue;
                    settingsElement.draw(width / 2 - 100, (int)listY, width / 2 + 100, (int)(listY + (double)settingsElement.getEntryHeight()), mouseX, mouseY);
                    if (canRenderDescription && settingsElement instanceof DropDownElement) {
                        boolean bl2 = canRenderDescription = !((DropDownElement)settingsElement).getDropDownMenu().isOpen();
                    }
                    if (!settingsElement.isMouseOver()) continue;
                    this.mouseOverElement = settingsElement;
                }
                for (SettingsElement settingsElement : this.listedElementsStored) {
                    listY += (double)(settingsElement.getEntryHeight() + 2);
                    totalEntryHeight += (double)(settingsElement.getEntryHeight() + 2);
                }
                this.listedElementsStored = Lists.reverse(this.listedElementsStored);
                ++zLevel;
            }
            count = this.listedElementsStored.size();
        } else {
            boolean ranked = this.selectedSortingState.isRanked();
            int rank = 1;
            double listY2 = 80.0 + this.scrollbar.getScrollY();
            for (AddonInfo addonInfo : this.sortedAddonInfoList) {
                boolean visible;
                boolean bl3 = visible = this.enabledFilter == null;
                if (!visible) {
                    int[] nArray = this.enabledFilter;
                    int n2 = this.enabledFilter.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        int i2 = nArray[n3];
                        if (i2 == addonInfo.getCategory()) {
                            visible = true;
                        }
                        ++n3;
                    }
                }
                if (!visible) continue;
                boolean hasInstalled = AddonLoader.hasInstalled(addonInfo);
                if (this.selectedSortingState == EnumSortingState.VERIFIED && (this.fieldSearch == null || this.fieldSearch.getText().isEmpty()) && (!(addonInfo instanceof OnlineAddonInfo) || !((OnlineAddonInfo)addonInfo).isVerified()) || (this.selectedSortingState == EnumSortingState.INSTALLED ? !hasInstalled : !(addonInfo instanceof OnlineAddonInfo) && (this.fieldSearch == null || this.fieldSearch.getText().isEmpty()))) continue;
                AddonElement addonElement = addonInfo.getAddonElement();
                addonElement.canHover(mouseY >= 75 && mouseY <= height - 30).draw(width / 2 - 150, (int)listY2, width / 2 + 150, (int)(listY2 + (double)addonElement.getEntryHeight()), mouseX, mouseY);
                if (ranked) {
                    String prefix = "8";
                    switch (rank) {
                        case 1: {
                            prefix = "e";
                            break;
                        }
                        case 2: {
                            prefix = "7";
                            break;
                        }
                        case 3: {
                            prefix = "6";
                        }
                    }
                    LabyMod.getInstance().getDrawUtils().drawRightString(String.valueOf(ModColor.cl(prefix)) + rank + ".", width / 2 - 152, listY2 + 3.0, 0.7);
                }
                if (addonElement.isMouseOver()) {
                    this.mouseOverAddonEntry = addonInfo.getAddonElement();
                }
                listY2 += (double)(addonElement.getEntryHeight() + 2);
                totalEntryHeight += (double)(addonElement.getEntryHeight() + 2);
                ++rank;
            }
            count = AddonInfoManager.getInstance().getAddonInfoList().size();
        }
        if (canRenderDescription) {
            this.drawDescriptions(mouseX, mouseY, 80, height - 30);
        }
        this.scrollbar.setEntryHeight(totalEntryHeight / (double)count);
        this.scrollbar.update(count);
    }

    private void drawDescriptions(int mouseX, int mouseY, int minY, int maxY) {
        for (SettingsElement element : this.listedElementsStored) {
            if (!element.isMouseOver() || mouseY <= minY || mouseY >= maxY) continue;
            element.drawDescription(mouseX, mouseY, width);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ControlElement element4;
        block11: {
            block10: {
                super.mouseClicked(mouseX, mouseY, mouseButton);
                if (this.displayRestartWarning) {
                    return;
                }
                if (this.hoveredSortingState != null) {
                    this.selectedSortingState = this.hoveredSortingState;
                    this.sortAddonInfoList();
                }
                this.handleCheckBox(true, mouseX, mouseY, mouseButton);
                if (this.fieldSearch != null) {
                    this.fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (this.openedAddonSettings != null) {
                    this.openedAddonSettings.onMouseClickedPreview(mouseX, mouseY, mouseButton);
                }
                if (mouseY < 75 || mouseY > height - 30) {
                    return;
                }
                if (!this.isInSubSettings()) break block10;
                this.unfocusSubListTextfields(mouseX, mouseY, mouseButton);
                for (SettingsElement element : this.listedElementsStored) {
                    if (element instanceof DropDownElement && ((DropDownElement)element).onClickDropDown(mouseX, mouseY, mouseButton)) {
                        return;
                    }
                    if (!(element instanceof ColorPickerCheckBoxBulkElement) || !((ColorPickerCheckBoxBulkElement)element).onClickBulkEntry(mouseX, mouseY, mouseButton)) continue;
                    return;
                }
                if (this.mouseOverElement == null) break block11;
                boolean flag = true;
                if (this.mouseOverElement instanceof ControlElement) {
                    ControlElement controlElement = (ControlElement)this.mouseOverElement;
                }
                if (!flag) break block11;
                this.mouseOverElement.mouseClicked(mouseX, mouseY, mouseButton);
                break block11;
            }
            for (AddonInfo addonInfo : AddonInfoManager.getInstance().getAddonInfoList()) {
                AddonElement element3 = addonInfo.getAddonElement();
                element3.mouseClicked(mouseX, mouseY, mouseButton);
                if (!element3.isHoverSubSettingsButton()) continue;
                this.openedAddonSettings = this.mouseOverAddonEntry;
                this.initGui();
            }
        }
        if (this.mouseOverElement != null && this.mouseOverElement instanceof ControlElement && (element4 = (ControlElement)this.mouseOverElement).hasSubList() && element4.getButtonAdvanced().hovered && element4.getButtonAdvanced().enabled) {
            element4.getButtonAdvanced().playPressSound(this.mc.getSoundHandler());
            this.path.add(element4);
            this.preScrollPos = this.scrollbar.getScrollY();
            this.scrollbar.setScrollY(0.0);
            this.initGui();
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    private void unfocusSubListTextfields(int mouseX, int mouseY, int mouseButton) {
        for (SettingsElement moduleElement : this.listedElementsStored) {
            moduleElement.unfocus(mouseX, mouseY, mouseButton);
        }
    }

    private void sortAddonInfoList() {
        String query;
        String string = query = this.fieldSearch == null ? null : this.fieldSearch.getText().toLowerCase().replaceAll(" ", "");
        if (query != null && query.isEmpty()) {
            query = null;
        }
        ArrayList<AddonInfo> sortedAddonInfoList = new ArrayList<AddonInfo>();
        for (AddonInfo addonInfo : AddonInfoManager.getInstance().getAddonInfoList()) {
            if (query != null && !this.match(addonInfo.getName(), query) && !this.match(addonInfo.getDescription(), query) && !this.match(addonInfo.getAuthor(), query)) continue;
            sortedAddonInfoList.add(addonInfo);
        }
        final int index = this.selectedSortingState.ordinal();
        Collections.sort(sortedAddonInfoList, new Comparator<AddonInfo>(){

            @Override
            public int compare(AddonInfo a2, AddonInfo b2) {
                int aId = a2 instanceof OnlineAddonInfo ? ((OnlineAddonInfo)a2).getSorting()[index] : 0;
                int bId = b2 instanceof OnlineAddonInfo ? ((OnlineAddonInfo)b2).getSorting()[index] : 0;
                return aId - bId;
            }
        });
        this.sortedAddonInfoList = sortedAddonInfoList;
    }

    private boolean match(String s1, String s2) {
        return s1.toLowerCase().replaceAll(" ", "").contains(s2);
    }

    private void handleCheckBox(boolean handleMouseClick, int mouseX, int mouseY, int mouseButton) {
        if (!AddonInfoManager.getInstance().isLoaded()) {
            return;
        }
        CheckBox[] checkBoxList = AddonInfoManager.getInstance().getCategorieCheckboxList();
        this.enabledFilter = new int[checkBoxList.length];
        int index = 0;
        CheckBox[] checkBoxArray = checkBoxList;
        int n2 = checkBoxList.length;
        int n3 = 0;
        while (n3 < n2) {
            CheckBox checkBox = checkBoxArray[n3];
            if (handleMouseClick) {
                checkBox.mouseClicked(mouseX, mouseY, mouseButton);
            }
            this.enabledFilter[index] = checkBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED ? index + 1 : 0;
            ++index;
            ++n3;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.isInSubSettings()) {
            for (SettingsElement settingsElement : this.listedElementsStored) {
                settingsElement.mouseRelease(mouseX, mouseY, state);
            }
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.isInSubSettings()) {
            for (SettingsElement settingsElement : this.listedElementsStored) {
                settingsElement.mouseClickMove(mouseX, mouseY, clickedMouseButton);
            }
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
        if (this.isInSubSettings()) {
            for (SettingsElement settingsElement : this.listedElementsStored) {
                if (!(settingsElement instanceof DropDownElement)) continue;
                ((DropDownElement)settingsElement).onScrollDropDown();
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.fieldSearch != null) {
            this.fieldSearch.updateCursorCounter();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button == this.buttonDone && !this.displayRestartWarning) {
            if (LabyModAddonsGui.isRestartRequired()) {
                this.displayRestartWarning = true;
            } else {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            }
        }
        if (this.path.isEmpty()) {
            if (button == this.buttonBack && !this.displayRestartWarning) {
                this.openedAddonSettings = null;
            }
        } else {
            this.path.remove(this.path.size() - 1);
            this.initGui();
            this.scrollbar.setScrollY(this.preScrollPos);
            this.preScrollPos = 0.0;
        }
        if (button == this.buttonWarningExitGame && this.displayRestartWarning) {
            this.mc.shutdown();
        }
        if (button == this.buttonWarningRestartLater && this.displayRestartWarning) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.isInSubSettings()) {
            for (SettingsElement settingsElement : this.listedElementsStored) {
                boolean cancel = settingsElement instanceof KeyElement && ((KeyElement)settingsElement).getTextField().isFocused();
                settingsElement.keyTyped(typedChar, keyCode);
                if (!cancel) continue;
                return;
            }
            super.keyTyped(typedChar, keyCode);
        }
        if (this.fieldSearch != null && !this.isInSubSettings()) {
            this.fieldSearch.textboxKeyTyped(typedChar, keyCode);
            this.sortAddonInfoList();
        }
        if (keyCode == 1) {
            if (!this.displayRestartWarning && LabyModAddonsGui.isRestartRequired()) {
                this.displayRestartWarning = true;
            } else {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            }
        }
    }

    private boolean isInSubSettings() {
        return this.openedAddonSettings != null;
    }

    public static enum EnumSortingState {
        TRENDING(LanguageManager.translate("sortingtab_trending"), true),
        TOP(LanguageManager.translate("sortingtab_top"), true),
        LATEST(LanguageManager.translate("sortingtab_latest"), false),
        VERIFIED(LanguageManager.translate("sortingtab_featured"), false),
        INSTALLED(LanguageManager.translate("sortingtab_installed"), false);

        private final String displayName;
        private final boolean ranked;

        private EnumSortingState(String displayName, boolean ranked) {
            this.displayName = displayName;
            this.ranked = ranked;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isRanked() {
            return this.ranked;
        }
    }
}

