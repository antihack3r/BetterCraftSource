/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.DefinedSettings;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModAddonsGui;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.SettingsCategory;
import net.labymod.settings.elements.CategorySettingsElement;
import net.labymod.settings.elements.ColorPickerCheckBoxBulkElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.support.DashboardConnector;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class LabyModSettingsGui
extends GuiScreen {
    private final Scrollbar scrollbar = new Scrollbar(1);
    private final GuiScreen lastScreen;
    private final List<CategorySettingsElement> buttonCategoryElements = new ArrayList<CategorySettingsElement>();
    private final List<SettingsElement> tempElementsStored;
    private final ArrayList<SettingsElement> path;
    private final DashboardConnector dashboardConnector;
    private ModTextField searchField;
    private SettingsElement mouseOverElement;
    private List<SettingsElement> listedElementsStored = new ArrayList<SettingsElement>();
    private GuiButton buttonGoBack;
    private double preScrollPos = 0.0;
    private boolean hoverUpdateButton;
    private boolean closed = false;
    private boolean skipDrawDescription = false;

    public LabyModSettingsGui(GuiScreen lastScreen) {
        this.tempElementsStored = new ArrayList<SettingsElement>();
        this.path = new ArrayList();
        this.dashboardConnector = new DashboardConnector();
        this.lastScreen = lastScreen;
        this.buttonCategoryElements.add(new CategorySettingsElement(new SettingsCategory("settings_category_ingame_gui").setIcon("ingame_gui.png"), new CategorySettingsElement.ClickedCallback(){

            @Override
            public void clicked(SettingsCategory category) {
                Minecraft.getMinecraft().displayGuiScreen(new LabyModModuleEditorGui(LabyModSettingsGui.this));
            }
        }));
        this.buttonCategoryElements.add(new CategorySettingsElement(new SettingsCategory("settings_category_addons").setIcon("addons.png"), new CategorySettingsElement.ClickedCallback(){

            @Override
            public void clicked(SettingsCategory category) {
                Minecraft.getMinecraft().displayGuiScreen(new LabyModAddonsGui(LabyModSettingsGui.this));
            }
        }));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.doQuery(null);
        this.buttonList.clear();
        this.searchField = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 126, 50, 252, 16);
        this.searchField.setPlaceHolder(LanguageManager.translate("search_textbox_placeholder"));
        this.scrollbar.setPosition(width / 2 + 122, 80, width / 2 + 122 + 4, height - 35);
        this.scrollbar.setSpeed(20);
        this.scrollbar.update(this.listedElementsStored.size());
        this.scrollbar.init();
        this.buttonList.add(new GuiButton(5, width / 2 - 100, height - 25, LanguageManager.translate("button_save_changes")));
        this.buttonGoBack = new GuiButton(6, width / 2 - 120, 48, 20, 20, "<");
        this.buttonList.add(this.buttonGoBack);
        this.buttonGoBack.visible = !this.path.isEmpty();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        LabyMod.getMainConfig().save();
    }

    private void doQuery(String query) {
        this.tempElementsStored.clear();
        if (this.path.isEmpty()) {
            for (SettingsCategory settingsCategory : DefinedSettings.getCategories()) {
                this.queryCategory(settingsCategory, query);
            }
        } else {
            SettingsElement currentOpenElement = this.path.get(this.path.size() - 1);
            this.tempElementsStored.addAll(currentOpenElement.getSubSettings().getElements());
        }
        this.listedElementsStored = this.tempElementsStored;
    }

    private void queryCategory(SettingsCategory settingsCategory, String query) {
        ArrayList<SettingsElement> elementsToAdd = new ArrayList<SettingsElement>();
        for (SettingsElement element : settingsCategory.getSettings().getElements()) {
            if (query == null || settingsCategory.getTitle().toLowerCase().contains(query) || this.isSettingElement(query, element)) {
                elementsToAdd.add(element);
            }
            if (query == null || query.isEmpty() || element.getSubSettings() == null || element.getSubSettings().getElements().isEmpty()) continue;
            for (SettingsElement subElement : element.getSubSettings().getElements()) {
                if (!this.isSettingElement(query, subElement)) continue;
                elementsToAdd.add(subElement);
            }
        }
        if (!elementsToAdd.isEmpty()) {
            this.tempElementsStored.add(new HeaderElement(settingsCategory.getTitle()));
        }
        for (SettingsElement element : elementsToAdd) {
            this.tempElementsStored.add(element);
        }
        for (SettingsCategory subCategory : settingsCategory.getSubList()) {
            this.queryCategory(subCategory, query);
        }
    }

    private boolean isSettingElement(String query, SettingsElement settingsElement) {
        return settingsElement.getDisplayName().toLowerCase().contains(query) || settingsElement.getDescriptionText() != null && settingsElement.getDescriptionText().toLowerCase().contains(query);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawAutoDimmedBackground(this.scrollbar.getScrollY());
        this.drawSettingsList(mouseX, mouseY, height - 30);
        draw.drawOverlayBackground(0, 75);
        draw.drawGradientShadowTop(75.0, 0.0, width);
        draw.drawOverlayBackground(height - 30, height);
        draw.drawGradientShadowBottom(height - 30, 0.0, width);
        this.drawHeadButtons(mouseX, mouseY);
        this.scrollbar.draw(mouseX, mouseY);
        if (!this.path.isEmpty()) {
            SettingsElement currentOpenElement = this.path.get(this.path.size() - 1);
            draw.drawString(currentOpenElement.getDisplayName(), width / 2 - 95, 55.0);
            if (currentOpenElement instanceof ControlElement) {
                ControlElement control = (ControlElement)currentOpenElement;
                ControlElement.IconData iconData = control.getIconData();
                if (iconData.hastextureIcon()) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(iconData.gettextureIcon());
                    LabyMod.getInstance().getDrawUtils().drawTexture(width / 2 + 100, 50.0, 256.0, 256.0, 16.0, 16.0);
                } else if (iconData.hasMaterialIcon()) {
                    LabyMod.getInstance().getDrawUtils().drawItem(iconData.getMaterialIcon().createItemStack(), width / 2 + 100, 50.0, null);
                }
            }
        } else {
            this.searchField.drawTextBox();
        }
        if (!this.skipDrawDescription) {
            this.drawDescriptions(mouseX, mouseY, 75, height - 30);
        }
        this.drawUpdateNotification(mouseX, mouseY);
        this.dashboardConnector.renderIcon(width - 15, 15, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawUpdateNotification(int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_UPDATE);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.hoverUpdateButton = mouseX > 5 && mouseX < 20 && mouseY > height - 20 && mouseY < height - 20 + 15;
        int add2 = this.hoverUpdateButton ? 1 : 0;
        draw.drawTexture(5 - add2, height - 20 - add2, 255.0, 255.0, 15 + add2 * 2, 15 + add2 * 2);
        boolean updateAvailable = LabyMod.getInstance().getUpdater().isUpdateAvailable();
        if (updateAvailable) {
            draw.drawString(LanguageManager.translate("update_available"), 25.0, height - 20 + 4);
            if (this.hoverUpdateButton) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, LanguageManager.translate("reinstall_version", LabyMod.getInstance().getUpdater().getLatestVersionString()));
            }
        } else if (this.hoverUpdateButton) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, LanguageManager.translate("reinstall"));
        }
    }

    private void drawHeadButtons(int mouseX, int mouseY) {
        int midX = width / 2;
        int buttonLength = width / (this.buttonCategoryElements.size() + 1);
        int buttonHeight = 25;
        int spaceLength = width / (this.buttonCategoryElements.size() + 2) / 20;
        if (buttonLength > 123) {
            buttonLength = 123;
        }
        if (buttonLength < 70) {
            buttonLength = 70;
        }
        if (spaceLength > 50) {
            spaceLength = 50;
        }
        int totalLength = (buttonLength + spaceLength) * this.buttonCategoryElements.size() - spaceLength;
        int posX = midX - totalLength / 2;
        int posY = 15;
        for (CategorySettingsElement element : this.buttonCategoryElements) {
            element.draw(posX, 15, posX + buttonLength, 40, mouseX, mouseY);
            posX += buttonLength + spaceLength;
        }
    }

    private void drawSettingsList(int mouseX, int mouseY, int maxY) {
        this.mouseOverElement = null;
        this.skipDrawDescription = false;
        double totalEntryHeight = 0.0;
        int zLevel = 0;
        while (zLevel < 2) {
            double posY = 80.0 + this.scrollbar.getScrollY();
            int midX = width / 2;
            int elementLength = 120;
            totalEntryHeight = 0.0;
            for (SettingsElement element : this.listedElementsStored) {
                if ((!(element instanceof DropDownElement) || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 0 || (element instanceof DropDownElement || element instanceof ColorPickerCheckBoxBulkElement) && zLevel == 1) {
                    if (element instanceof DropDownElement) {
                        ((DropDownElement)element).getDropDownMenu().setMaxY(maxY);
                        if (((DropDownElement)element).getDropDownMenu().isOpen()) {
                            this.skipDrawDescription = true;
                        }
                    }
                    element.draw(midX - 120, (int)posY, midX + 120, (int)posY + element.getEntryHeight(), mouseX, mouseY);
                    if (element.isMouseOver()) {
                        this.mouseOverElement = element;
                    }
                }
                posY += (double)(element.getEntryHeight() + 1);
                totalEntryHeight += (double)(element.getEntryHeight() + 1);
            }
            ++zLevel;
        }
        this.scrollbar.setEntryHeight(totalEntryHeight / (double)this.listedElementsStored.size());
        this.scrollbar.update(this.listedElementsStored.size());
    }

    private void drawDescriptions(int mouseX, int mouseY, int minY, int maxY) {
        for (SettingsElement element : this.listedElementsStored) {
            if (!element.isMouseOver() || mouseY <= minY || mouseY >= maxY) continue;
            element.drawDescription(mouseX, mouseY, width);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 5) {
            this.closed = true;
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
        if (button.id == 6 && !this.path.isEmpty()) {
            this.path.remove(this.path.size() - 1);
            this.initGui();
            this.scrollbar.setScrollY(this.preScrollPos);
            this.preScrollPos = 0.0;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ControlElement element3;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.closed) {
            return;
        }
        if (this.hoverUpdateButton) {
            try {
                LabyMod.getInstance().getUpdater().downloadUpdaterFile();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            LabyMod.getInstance().getUpdater().addUpdaterHook();
            Minecraft.getMinecraft().shutdown();
            return;
        }
        for (SettingsElement element : this.listedElementsStored) {
            if (!(element instanceof DropDownElement) || !((DropDownElement)element).onClickDropDown(mouseX, mouseY, mouseButton)) continue;
            return;
        }
        for (CategorySettingsElement element2 : this.buttonCategoryElements) {
            element2.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (SettingsElement element : this.listedElementsStored) {
            element.unfocus(mouseX, mouseY, mouseButton);
            if (!element.isMouseOver()) continue;
            element.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (this.mouseOverElement != null && this.mouseOverElement instanceof ControlElement && (element3 = (ControlElement)this.mouseOverElement).hasSubList() && element3.getButtonAdvanced().hovered && element3.getButtonAdvanced().enabled) {
            element3.getButtonAdvanced().playPressSound(this.mc.getSoundHandler());
            this.path.add(element3);
            this.preScrollPos = this.scrollbar.getScrollY();
            this.scrollbar.setScrollY(0.0);
            this.initGui();
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        this.dashboardConnector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (SettingsElement element : this.listedElementsStored) {
            element.mouseRelease(mouseX, mouseY, state);
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (SettingsElement element : this.listedElementsStored) {
            element.mouseClickMove(mouseX, mouseY, clickedMouseButton);
        }
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (SettingsElement element : this.listedElementsStored) {
            boolean cancel = element instanceof KeyElement && ((KeyElement)element).getTextField().isFocused();
            element.keyTyped(typedChar, keyCode);
            if (!cancel) continue;
            return;
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
        if (this.path.isEmpty() && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            this.doQuery(this.searchField.getText().toLowerCase());
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.searchField.updateCursorCounter();
    }
}

