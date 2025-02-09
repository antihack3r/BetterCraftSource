/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.io.IOException;
import net.labymod.api.LabyModAddon;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.PreviewRenderer;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class StringElement
extends ControlElement {
    private String currentValue;
    private Consumer<String> changeListener;
    private ModTextField textField;
    private Consumer<String> callback;
    private boolean hoverExpandButton = false;

    public StringElement(String displayName, final String configEntryName, ControlElement.IconData iconData) {
        super(displayName, configEntryName, iconData);
        if (!configEntryName.isEmpty()) {
            try {
                this.currentValue = (String)ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
        if (this.currentValue == null) {
            this.currentValue = "";
        }
        this.changeListener = new Consumer<String>(){

            @Override
            public void accept(String accepted) {
                try {
                    ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), accepted);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (StringElement.this.callback != null) {
                    StringElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public StringElement(String displayName, final LabyModAddon addon, ControlElement.IconData iconData, final String attribute, String currentValue) {
        super(displayName, iconData);
        if (currentValue == null) {
            currentValue = "";
        }
        this.currentValue = currentValue;
        this.changeListener = new Consumer<String>(){

            @Override
            public void accept(String accepted) {
                addon.getConfig().addProperty(attribute, accepted);
                addon.loadConfig();
                if (StringElement.this.callback != null) {
                    StringElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public StringElement(String displayName, ControlElement.IconData iconData, String currentValue, Consumer<String> changeListener) {
        super(displayName, iconData);
        this.currentValue = currentValue;
        this.changeListener = changeListener;
        this.createTextfield();
    }

    public StringElement(String configEntryName, ControlElement.IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public void createTextfield() {
        this.updateValue();
        this.textField.setCursorPositionEnd();
        this.textField.setFocused(false);
    }

    private void updateValue() {
        this.textField.setText(this.currentValue == null ? " " : this.currentValue);
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        if (this.textField == null) {
            return;
        }
        this.textField.yPosition = y2 + 1;
        this.textField.drawTextBox();
        LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_EXPAND);
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
        super.unfocus(mouseX, mouseY, mouseButton);
        if (this.hoverExpandButton) {
            this.hoverExpandButton = false;
            Minecraft.getMinecraft().displayGuiScreen(new ExpandedStringElementGui(this.textField, Minecraft.getMinecraft().currentScreen, new Consumer<ModTextField>(){

                @Override
                public void accept(ModTextField accepted) {
                    StringElement.this.textField.setText(accepted.getText());
                    StringElement.this.textField.setFocused(true);
                    StringElement.this.textField.setCursorPosition(accepted.getCursorPosition());
                    StringElement.this.textField.setSelectionPos(accepted.getSelectionEnd());
                    StringElement.this.changeListener.accept(StringElement.this.textField.getText());
                }
            }));
        }
        this.textField.setFocused(false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
            this.changeListener.accept(this.textField.getText());
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public StringElement maxLength(int maxLength) {
        this.textField.setMaxStringLength(maxLength);
        return this;
    }

    public StringElement addCallback(Consumer<String> callback) {
        this.callback = callback;
        return this;
    }

    public class ExpandedStringElementGui
    extends GuiScreen {
        private GuiScreen backgroundScreen;
        private Consumer<ModTextField> callback;
        private ModTextField preField;
        private ModTextField expandedField;

        public ExpandedStringElementGui(ModTextField preField, GuiScreen backgroundScreen, Consumer<ModTextField> callback) {
            this.backgroundScreen = backgroundScreen;
            this.callback = callback;
            this.preField = preField;
        }

        @Override
        public void initGui() {
            super.initGui();
            GuiScreen.width = width;
            GuiScreen.height = height;
            if (this.backgroundScreen instanceof LabyModModuleEditorGui) {
                PreviewRenderer.getInstance().init(ExpandedStringElementGui.class);
            }
            this.expandedField = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 150, height / 4 + 45, 300, 20);
            this.expandedField.setMaxStringLength(this.preField.getMaxStringLength());
            this.expandedField.setFocused(true);
            this.expandedField.setText(this.preField.getText());
            this.expandedField.setCursorPosition(this.preField.getCursorPosition());
            this.expandedField.setSelectionPos(this.preField.getSelectionEnd());
            this.buttonList.add(new GuiButton(1, width / 2 - 50, height / 4 + 85, 100, 20, LanguageManager.translate("button_done")));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
            ExpandedStringElementGui.drawRect(0, 0, width, height, Integer.MIN_VALUE);
            ExpandedStringElementGui.drawRect(width / 2 - 165, height / 4 + 35, width / 2 + 165, height / 4 + 120, Integer.MIN_VALUE);
            this.expandedField.drawTextBox();
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.expandedField.mouseClicked(mouseX, mouseY, mouseButton);
            this.callback.accept(this.expandedField);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }
            if (this.expandedField.textboxKeyTyped(typedChar, keyCode)) {
                this.callback.accept(this.expandedField);
            }
        }

        @Override
        public void updateScreen() {
            this.backgroundScreen.updateScreen();
            this.expandedField.updateCursorCounter();
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            if (button.id == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }
        }

        public GuiScreen getBackgroundScreen() {
            return this.backgroundScreen;
        }
    }
}

