/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.PreviewRenderer;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.CategoryModuleEditorElement;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

public class LabyModModuleEditorGui
extends GuiScreen {
    public static final String GLOBAL_PROFILE;
    public static final String SINGLEPLAYER_PROFILE;
    public static String selectedProfile;
    private final GuiScreen lastScreen;
    private final Scrollbar scrollbar;
    private final List<SettingsElement> path = new ArrayList<SettingsElement>();
    private final long lightUpSelectedElement;
    private boolean mouseClickSplitScreen = false;
    private SettingsElement mouseOverElement;
    private BooleanElement masterElement;
    private GuiButton buttonGoBack;
    private ModTextField textFieldSearch;
    private String searchedString = "";
    private ControlElement selectedControlElement;
    private DropDownMenu<EnumModuleEditorScale> scalingDropdown;
    private DropDownMenu<String> profilesDropdown;
    private boolean hoverResetButton = false;

    static {
        selectedProfile = null;
        GLOBAL_PROFILE = LanguageManager.translate("global_profile");
        SINGLEPLAYER_PROFILE = LanguageManager.translate("singleplayer_profile");
    }

    public LabyModModuleEditorGui(GuiScreen lastScreen) {
        this.lightUpSelectedElement = 0L;
        this.lastScreen = lastScreen;
        if (LabyMod.getSettings().moduleEditorZoom == -1.0) {
            LabyMod.getSettings().moduleEditorZoom = 33.333333333333336;
        }
        this.scrollbar = new Scrollbar(24);
    }

    @Override
    public void onGuiClosed() {
        PreviewRenderer.getInstance().kill();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        PreviewRenderer.getInstance().init(LabyModModuleEditorGui.class);
        this.initList(true);
    }

    private void initList(boolean scrollUp) {
        this.buttonList.clear();
        this.scrollbar.setPosition(LabyMod.getSettings().moduleEditorSplitX - 8, 0, LabyMod.getSettings().moduleEditorSplitX - 4, height - 31);
        this.scrollbar.setSpeed(10);
        if (scrollUp) {
            this.scrollbar.setScrollY(0.0);
        }
        this.updateScrollbarValues(4);
        this.scrollbar.init();
        boolean displayBackButton = this.path.size() != 0;
        this.textFieldSearch = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), displayBackButton ? 60 : 5, 35, LabyMod.getSettings().moduleEditorSplitX - (displayBackButton ? 69 : 14), 14);
        this.textFieldSearch.setText(this.searchedString);
        if (!displayBackButton) {
            this.textFieldSearch.setFocused(true);
        }
        this.textFieldSearch.setCursorPositionEnd();
        this.textFieldSearch.setPlaceHolder(LanguageManager.translate("search_textbox_placeholder"));
        this.buttonList.add(new GuiButton(5, 5, height - 25, LabyMod.getSettings().moduleEditorSplitX - 10, 20, LanguageManager.translate("button_done")));
        this.scalingDropdown = new DropDownMenu<EnumModuleEditorScale>("", width - 75, height - 12 - 5, 70, 12).fill(EnumModuleEditorScale.values());
        this.scalingDropdown.setSelected(EnumModuleEditorScale.getByZoom(LabyMod.getSettings().moduleEditorZoom));
        this.scalingDropdown.setMaxY(height);
        this.scalingDropdown.setEntryDrawer(new DropDownMenu.DropDownEntryDrawer(){

            @Override
            public void draw(Object object, int x2, int y2, String trimmedEntry) {
                LabyMod.getInstance().getDrawUtils().drawString(((EnumModuleEditorScale)((Object)object)).getDisplayName(), x2, y2);
            }
        });
        this.profilesDropdown = new DropDownMenu<String>("", LabyMod.getSettings().moduleEditorSplitX + 5, height - 12 - 5, 150, 12);
        this.profilesDropdown.addOption(GLOBAL_PROFILE);
        String currentServer = null;
        if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            currentServer = ModUtils.getProfileNameByIp(Minecraft.getMinecraft().getCurrentServerData().serverIP);
            this.profilesDropdown.addOption(currentServer);
        }
        if (Minecraft.getMinecraft().isSingleplayer()) {
            this.profilesDropdown.addOption(SINGLEPLAYER_PROFILE);
        }
        this.profilesDropdown.setSelected(selectedProfile == null ? GLOBAL_PROFILE : (selectedProfile.equals("singleplayer") ? SINGLEPLAYER_PROFILE : selectedProfile));
        this.profilesDropdown.setMaxY(height);
        this.buttonGoBack = new GuiButton(7, 5, 47, 20, 20, "<");
        this.buttonList.add(this.buttonGoBack);
        this.checkSplitscreenBorder();
        super.initGui();
        if (this.path.size() != 0) {
            for (SettingsElement module : this.getCurrentSelectedSubList().getSubSettings().getElements()) {
                module.init();
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
            return;
        }
        if (this.textFieldSearch.textboxKeyTyped(typedChar, keyCode)) {
            this.searchedString = this.textFieldSearch.getText();
            this.scrollbar.update(this.getFoundModules().size());
        }
        if (this.selectedControlElement != null) {
            this.selectedControlElement.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 5) {
            this.mc.displayGuiScreen(this.lastScreen);
        }
        if (button.id == this.buttonGoBack.id) {
            this.searchedString = "";
            if (this.path.size() != 0) {
                this.path.remove(this.path.size() - 1);
            }
            this.initGui();
        }
        super.actionPerformed(button);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ControlElement element3;
        if (this.scalingDropdown.onClick(mouseX, mouseY, mouseButton) && this.scalingDropdown.getSelected() != null && !this.scalingDropdown.isOpen()) {
            double scaling;
            LabyMod.getSettings().moduleEditorZoom = scaling = (double)this.scalingDropdown.getSelected().getScaling() / 3.0 * 100.0;
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            width = scaledResolution.getScaledWidth();
            height = scaledResolution.getScaledHeight();
            this.initGui();
            return;
        }
        if (this.profilesDropdown.onClick(mouseX, mouseY, mouseButton) && this.profilesDropdown.getSelected() != null && !this.profilesDropdown.isOpen()) {
            selectedProfile = this.profilesDropdown.getSelected();
            if (selectedProfile != null && selectedProfile.equals(GLOBAL_PROFILE)) {
                selectedProfile = null;
            }
            Minecraft.getMinecraft().displayGuiScreen(new LabyModModuleEditorGui(this.lastScreen));
            return;
        }
        List<SettingsElement> foundModules = this.getFoundModules();
        for (SettingsElement element : foundModules) {
            if (element instanceof DropDownElement && ((DropDownElement)element).onClickDropDown(mouseX, mouseY, mouseButton)) {
                return;
            }
            if (!(element instanceof ColorPickerCheckBoxBulkElement) || !((ColorPickerCheckBoxBulkElement)element).onClickBulkEntry(mouseX, mouseY, mouseButton)) continue;
            return;
        }
        this.textFieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
        this.unfocusSubListTextfields(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        if (this.mouseOverElement != null) {
            boolean flag = true;
            if (this.mouseOverElement instanceof ControlElement) {
                ControlElement controlElement = (ControlElement)this.mouseOverElement;
            }
            if (this.mouseOverElement instanceof CategoryModuleEditorElement) {
                this.path.add(this.mouseOverElement);
            }
            if (flag) {
                this.mouseOverElement.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (this.mouseOverElement instanceof ControlElement) {
                this.selectedControlElement = (ControlElement)this.mouseOverElement;
            }
            this.updateScrollbarValues(4);
        }
        if (this.mouseOverElement != null && this.mouseOverElement instanceof ControlElement && (element3 = (ControlElement)this.mouseOverElement).hasSubList() && element3.getButtonAdvanced().hovered && element3.getButtonAdvanced().enabled) {
            element3.getButtonAdvanced().playPressSound(this.mc.getSoundHandler());
            this.path.add(element3);
            this.initGui();
        }
        if (mouseButton == 0 && mouseX > LabyMod.getSettings().moduleEditorSplitX - 5 && mouseX < LabyMod.getSettings().moduleEditorSplitX + 2 && mouseY > 45 && mouseY < height - 30) {
            this.mouseClickSplitScreen = true;
        }
        if (this.hoverResetButton) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                @Override
                public void confirmClicked(boolean flag, int id2) {
                    Minecraft.getMinecraft().displayGuiScreen(new LabyModModuleEditorGui(LabyModModuleEditorGui.this.lastScreen));
                }
            }, LanguageManager.translate(selectedProfile == null ? LanguageManager.translate("reset_modules_global") : LanguageManager.translate("reset_modules_profile", selectedProfile)), LanguageManager.translate("reset_modules_undone"), LanguageManager.translate("button_no"), LanguageManager.translate("button_yes"), 0));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void unfocusSubListTextfields(int mouseX, int mouseY, int mouseButton) {
        if (this.path.size() != 0) {
            for (SettingsElement moduleElement : this.getCurrentSelectedSubList().getSubSettings().getElements()) {
                moduleElement.unfocus(mouseX, mouseY, mouseButton);
            }
        }
    }

    private SettingsElement getCurrentSelectedSubList() {
        return this.path.size() == 0 ? null : this.path.get(this.path.size() - 1);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        if (mouseButton == 0) {
            this.mouseClickSplitScreen = false;
        }
        if (this.mouseOverElement != null) {
            this.mouseOverElement.mouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        if (this.mouseClickSplitScreen) {
            LabyMod.getSettings().moduleEditorSplitX = mouseX;
            this.checkSplitscreenBorder();
            this.initGui();
            this.textFieldSearch.setFocused(false);
        }
        if (this.mouseOverElement != null) {
            this.mouseOverElement.mouseClickMove(mouseX, mouseY, clickedMouseButton);
        }
    }

    private void checkSplitscreenBorder() {
        if (LabyMod.getSettings().moduleEditorSplitX < 180) {
            LabyMod.getSettings().moduleEditorSplitX = 180;
        }
        if (LabyMod.getSettings().moduleEditorSplitX > 300) {
            LabyMod.getSettings().moduleEditorSplitX = 300;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean hoverSplitscreen;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        int scrollBarListY = 4;
        LabyMod.getInstance().getDrawUtils().drawDimmedOverlayBackground(0, 4, width, height - 30);
        int left = LabyMod.getSettings().moduleEditorSplitX - 1;
        int top = 30;
        int right = width - 1;
        int bottom = height - 31;
        List<SettingsElement> foundModules = this.getFoundModules();
        Collections.reverse(foundModules);
        double totalHeight = 0.0;
        for (SettingsElement element : foundModules) {
            totalHeight += (double)(element.getEntryHeight() + 1);
        }
        double moduleListStartY = (double)(32 + (this.path.size() == 0 ? 20 : 0)) + this.scrollbar.getScrollY();
        this.scrollbar.draw(mouseX, mouseY);
        this.renderModuleList(foundModules, moduleListStartY, totalHeight, mouseX, mouseY);
        draw.drawOverlayBackground(0, 30);
        draw.drawOverlayBackground(bottom, height);
        draw.drawOverlayBackground(0, 0, LabyMod.getSettings().moduleEditorSplitX - 4, 29);
        draw.drawGradientShadowTop(30.0, 0.0, LabyMod.getSettings().moduleEditorSplitX - 1);
        draw.drawGradientShadowBottom(bottom, 0.0, LabyMod.getSettings().moduleEditorSplitX - 1);
        boolean bl2 = hoverSplitscreen = mouseX > LabyMod.getSettings().moduleEditorSplitX - 5 && mouseX < LabyMod.getSettings().moduleEditorSplitX + 1 && mouseY > 45 && mouseY < height - 30;
        if (hoverSplitscreen || this.mouseClickSplitScreen) {
            draw.drawRectangle(LabyMod.getSettings().moduleEditorSplitX - 2, 30, LabyMod.getSettings().moduleEditorSplitX - 1, height - 31, Integer.MAX_VALUE);
            draw.drawCenteredString("|||", mouseX + 1, mouseY - 3);
        }
        draw.drawRightString(LanguageManager.translate("labymod_gui_editor"), width - 10, 12.0);
        if (this.path.size() != 0) {
            SettingsElement element2 = this.getCurrentSelectedSubList();
            int posY = 6;
            List<String> list = draw.listFormattedStringToWidth(element2.getDisplayName(), LabyMod.getSettings().moduleEditorSplitX - 33 - 5 - 15, 3);
            int fontHeight = 16 - list.size() * 3;
            double listY = 12.0;
            for (String line : list) {
                draw.drawString(line, 33.0, listY - (double)(list.size() * fontHeight / 2) + (double)(fontHeight / 2));
                listY += (double)fontHeight;
            }
            ControlElement.IconData masterIconData = null;
            boolean cfr_ignored_0 = element2 instanceof ControlElement;
            if (element2 instanceof CategoryModuleEditorElement) {
                masterIconData = ((CategoryModuleEditorElement)element2).getIconData();
            }
            if (masterIconData != null) {
                if (masterIconData.hastextureIcon()) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(masterIconData.gettextureIcon());
                    LabyMod.getInstance().getDrawUtils().drawTexture(LabyMod.getSettings().moduleEditorSplitX - 22, 9.0, 256.0, 256.0, 16.0, 16.0);
                } else if (masterIconData.hasMaterialIcon()) {
                    LabyMod.getInstance().getDrawUtils().drawItem(masterIconData.getMaterialIcon().createItemStack(), LabyMod.getSettings().moduleEditorSplitX - 22, 9.0, null);
                }
            }
            LabyModCore.getMinecraft().setButtonYPosition(this.buttonGoBack, 6);
        } else if (this.path.size() == 0) {
            this.textFieldSearch.drawTextBox();
            int masterY = 4;
            this.masterElement.setHoverable(true).setSelected(this.selectedControlElement == null).draw(5, 4, LabyMod.getSettings().moduleEditorSplitX - 4, 26, mouseX, mouseY);
        }
        this.buttonGoBack.visible = this.path.size() != 0;
        int tabX = left + 3;
        int tabY = 18;
        draw.drawGradientShadowBottom(30.0, 0.0, width);
        draw.drawGradientShadowTop(bottom, 0.0, width);
        draw.drawString(LanguageManager.translate("preview_zoom"), width - 75, height - 26, 0.75);
        this.scalingDropdown.draw(mouseX, mouseY);
        draw.drawString(LanguageManager.translate("module_profiles"), LabyMod.getSettings().moduleEditorSplitX + 5, height - 26, 0.75);
        this.profilesDropdown.draw(mouseX, mouseY);
        int resetX = LabyMod.getSettings().moduleEditorSplitX + 5 + this.profilesDropdown.getWidth() + 5;
        this.hoverResetButton = mouseX > resetX && mouseX < resetX + 16 && mouseY > height - 19 && mouseY < height - 19 + 16;
        draw.bindTexture(ModTextures.BUTTON_RESET);
        draw.drawTexture(resetX, height - 19, 0.0, this.hoverResetButton ? 127.5 : 0.0, 255.0, 127.5, 16.0, 16.0);
        if (this.hoverResetButton) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, selectedProfile == null ? LanguageManager.translate("reset_modules_description") : LanguageManager.translate("delete_profile_description", selectedProfile));
        }
        if (this.profilesDropdown.isMouseOver(mouseX, mouseY)) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, LanguageManager.translate("module_profiles_description"));
        }
        this.renderDescriptions(foundModules, moduleListStartY, totalHeight, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void updateScrollbarValues(int scrollBarListY) {
        List<SettingsElement> foundModules = this.getFoundModules();
        double totalHeight = 0.0;
        for (SettingsElement element : foundModules) {
            totalHeight += (double)(element.getEntryHeight() + 1);
        }
        this.scrollbar.setEntryHeight(totalHeight / (double)foundModules.size());
        this.scrollbar.update(foundModules.size());
        this.scrollbar.setPosTop(scrollBarListY + 25 + 2 + (this.path.size() == 0 ? 24 : 0));
        this.scrollbar.setSpeed((int)this.scrollbar.getEntryHeight() / 2);
    }

    private void renderModuleList(List<SettingsElement> foundModules, double listY, double totalHeight, int mouseX, int mouseY) {
        SettingsElement mouseOverElement = null;
        int zLevel = 0;
        while (zLevel < 2) {
            int x2 = 5;
            double y2 = listY + totalHeight;
            int maxX = LabyMod.getSettings().moduleEditorSplitX - (this.scrollbar.isHidden() ? 5 : 10);
            for (SettingsElement element : foundModules) {
                boolean inYRange;
                int nextSetting = element.getEntryHeight();
                y2 -= (double)(nextSetting + 1);
                boolean bl2 = inYRange = (double)mouseY > listY && mouseY < height - 30;
                if ((!(element instanceof DropDownElement) || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 0 || (element instanceof DropDownElement || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 1) {
                    if (element instanceof ControlElement) {
                        ControlElement controlElement = (ControlElement)element;
                        boolean selected = this.selectedControlElement != null && this.selectedControlElement == controlElement;
                        controlElement.draw(5, (int)y2, maxX, (int)y2 + nextSetting, mouseX, mouseY);
                        if (this.selectedControlElement != null && this.selectedControlElement.equals(controlElement) && this.lightUpSelectedElement + 1000L > System.currentTimeMillis()) {
                            int alpha = (int)((System.currentTimeMillis() - this.lightUpSelectedElement) * -1L + 1000L) / 4;
                            LabyMod.getInstance().getDrawUtils().drawRectBorder(5.0, y2, maxX, y2 + (double)nextSetting, ModColor.toRGB(200, 20, 20, alpha), 1.0);
                        }
                    }
                    if (element instanceof CategoryModuleEditorElement) {
                        CategoryModuleEditorElement categoryElement = (CategoryModuleEditorElement)element;
                        categoryElement.draw(5, (int)y2, maxX, (int)y2 + nextSetting, mouseX, mouseY);
                    }
                }
                if (!element.isMouseOver() || !inYRange || zLevel != 1) continue;
                mouseOverElement = element;
            }
            ++zLevel;
        }
        if (this.masterElement.isMouseOver() && this.path.size() == 0) {
            mouseOverElement = this.masterElement;
        }
        this.mouseOverElement = mouseOverElement;
    }

    private void renderDescriptions(List<SettingsElement> foundModules, double listY, double totalHeight, int mouseX, int mouseY) {
        for (SettingsElement element : foundModules) {
            if (!element.isMouseOver() || !((double)mouseY > listY) || !((double)mouseY < listY + totalHeight)) continue;
            element.drawDescription(mouseX, mouseY, width);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.path.size() != 0) {
            for (SettingsElement module : this.getCurrentSelectedSubList().getSubSettings().getElements()) {
                module.updateScreen();
            }
        }
        this.textFieldSearch.updateCursorCounter();
    }

    private List<SettingsElement> getFoundModules() {
        boolean isMasterOpen = this.path.size() != 0 && this.path.get(0) == this.masterElement;
        boolean b2 = false;
        if (this.textFieldSearch == null || this.textFieldSearch.getText().replaceAll(" ", "").isEmpty() ? this.path.size() >= 2 : this.path.size() >= 1) {
            // empty if block
        }
        boolean isSubSettingOpen = b2 = true;
        if (this.path.size() != 0) {
            ArrayList<SettingsElement> list = new ArrayList<SettingsElement>();
            for (SettingsElement element : this.getCurrentSelectedSubList().getSubSettings().getElements()) {
                boolean isCurrentTab = isMasterOpen || isSubSettingOpen;
                boolean cfr_ignored_0 = element instanceof ControlElement;
            }
            return list;
        }
        String searchValue = this.textFieldSearch.getText().toLowerCase();
        searchValue.replace(" ", "").isEmpty();
        ArrayList<SettingsElement> list2 = new ArrayList<SettingsElement>();
        boolean isCurrentTab2 = isMasterOpen || isSubSettingOpen;
        return list2;
    }

    public GuiScreen getLastScreen() {
        return this.lastScreen;
    }

    public static enum EnumModuleEditorScale {
        SMALL(LanguageManager.translate("scaletype_small"), 1),
        NORMAL(LanguageManager.translate("scaletype_normal"), 2),
        LARGE(LanguageManager.translate("scaletype_large"), 3),
        AUTO(LanguageManager.translate("scaletype_auto"), 4);

        private final String displayName;
        private final int scaling;

        private EnumModuleEditorScale(String displayName, int scaling) {
            this.displayName = displayName;
            this.scaling = scaling;
        }

        public static EnumModuleEditorScale getByScaling(int guiScale) {
            EnumModuleEditorScale[] enumModuleEditorScaleArray = EnumModuleEditorScale.values();
            int n2 = enumModuleEditorScaleArray.length;
            int n3 = 0;
            while (n3 < n2) {
                EnumModuleEditorScale emes = enumModuleEditorScaleArray[n3];
                if (emes.getScaling() >= guiScale) {
                    return emes;
                }
                ++n3;
            }
            return NORMAL;
        }

        public static EnumModuleEditorScale getByZoom(double zoom) {
            double customScaleFactor = 1.0 + zoom * 0.03;
            EnumModuleEditorScale[] enumModuleEditorScaleArray = EnumModuleEditorScale.values();
            int n2 = enumModuleEditorScaleArray.length;
            int n3 = 0;
            while (n3 < n2) {
                EnumModuleEditorScale emes = enumModuleEditorScaleArray[n3];
                if ((long)emes.getScaling() == Math.round(customScaleFactor) - 1L) {
                    return emes;
                }
                ++n3;
            }
            return NORMAL;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public int getScaling() {
            return this.scaling;
        }
    }
}

