/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.manager.SignManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiSignSearch
extends GuiScreen {
    private GuiScreen lastScreen;
    private ModTextField fieldSearch;
    private ModTextField fieldBlacklist;
    private CheckBox checkBoxAdvanced;
    private CheckBox checkBoxFilterFullServer;
    private CheckBox checkBoxNightmode;

    public GuiSignSearch(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + (SignManager.getSignSearchSettings().isUseAdvancedOptions() ? 90 : 50), "Done"));
        this.fieldSearch = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 100, height / 4 + 20, 200, 20);
        this.fieldSearch.setBlackBox(false);
        this.fieldSearch.setText(SignManager.getSignSearchSettings().getSearchString());
        this.fieldSearch.setPlaceHolder(String.valueOf(LanguageManager.translate("search_on_signs")) + "..");
        this.fieldSearch.setFocused(false);
        this.fieldSearch.setMaxStringLength(256);
        this.fieldBlacklist = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 20, height / 4 + 50, 120, 20);
        this.fieldBlacklist.setBlackBox(false);
        this.fieldBlacklist.setText(SignManager.getSignSearchSettings().getBlacklistString());
        this.fieldBlacklist.setPlaceHolder(String.valueOf(LanguageManager.translate("blacklist")) + "..");
        this.fieldBlacklist.setVisible(SignManager.getSignSearchSettings().isUseAdvancedOptions());
        this.fieldBlacklist.setMaxStringLength(256);
        this.checkBoxAdvanced = new CheckBox(LanguageManager.translate("button_advanced"), SignManager.getSignSearchSettings().isUseAdvancedOptions() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, width / 2 + 100 + 5, height / 4 + 20, 20, 20);
        this.checkBoxAdvanced.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                SignManager.getSignSearchSettings().setUseAdvancedOptions(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
                GuiSignSearch.this.initGui();
            }
        });
        this.checkBoxFilterFullServer = new CheckBox(LanguageManager.translate("filter_full_servers"), SignManager.getSignSearchSettings().isFilterFullServer() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, width / 2 - 100 + 5, height / 4 + 50, 20, 20);
        this.checkBoxFilterFullServer.setVisible(SignManager.getSignSearchSettings().isUseAdvancedOptions());
        this.checkBoxFilterFullServer.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                SignManager.getSignSearchSettings().setFilterFullServer(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
                GuiSignSearch.this.initGui();
            }
        });
        this.checkBoxFilterFullServer.setDescription(LanguageManager.translate("filter_full_servers_description"));
        this.checkBoxNightmode = new CheckBox(LanguageManager.translate("filter_empty_servers"), SignManager.getSignSearchSettings().isFilterEmptyServer() ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, width / 2 - 100 + 20 + 15 + 5, height / 4 + 50, 20, 20);
        this.checkBoxNightmode.setVisible(SignManager.getSignSearchSettings().isUseAdvancedOptions());
        this.checkBoxNightmode.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                SignManager.getSignSearchSettings().setFilterEmptyServer(accepted == CheckBox.EnumCheckBoxValue.ENABLED);
                GuiSignSearch.this.initGui();
            }
        });
        this.checkBoxNightmode.setDescription(LanguageManager.translate("filter_empty_servers_description"));
        SignManager.getSignSearchSettings().update();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.fieldSearch.drawTextBox();
        this.fieldBlacklist.drawTextBox();
        this.checkBoxAdvanced.drawCheckbox(mouseX, mouseY);
        this.checkBoxFilterFullServer.drawCheckbox(mouseX, mouseY);
        this.checkBoxNightmode.drawCheckbox(mouseX, mouseY);
        draw.drawCenteredString(LanguageManager.translate("title_sign_search"), width / 2, height / 4);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_SIGNSEARCH);
        LabyMod.getInstance().getDrawUtils().drawTexture(this.fieldSearch.xPosition - 22, this.fieldSearch.yPosition, 255.0, 255.0, 20.0, 20.0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (!this.fieldSearch.isFocused() && !this.fieldBlacklist.isFocused()) {
            this.fieldSearch.setFocused(true);
        }
        if (this.fieldSearch.textboxKeyTyped(typedChar, keyCode)) {
            SignManager.getSignSearchSettings().setSearchString(this.fieldSearch.getText());
        }
        if (this.fieldBlacklist.textboxKeyTyped(typedChar, keyCode)) {
            SignManager.getSignSearchSettings().setBlacklistString(this.fieldBlacklist.getText());
        }
        SignManager.getSignSearchSettings().update();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.fieldSearch.mouseClicked(mouseX, mouseY, mouseButton);
        this.fieldBlacklist.mouseClicked(mouseX, mouseY, mouseButton);
        this.checkBoxAdvanced.mouseClicked(mouseX, mouseY, mouseButton);
        this.checkBoxFilterFullServer.mouseClicked(mouseX, mouseY, mouseButton);
        this.checkBoxNightmode.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.fieldSearch.updateCursorCounter();
        this.fieldBlacklist.updateCursorCounter();
    }
}

