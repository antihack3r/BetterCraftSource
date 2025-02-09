/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiControls
extends GuiScreen {
    private static final GameSettings.Options[] optionsArr = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};
    private GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    private GameSettings options;
    public KeyBinding buttonId = null;
    public long time;
    private GuiKeyBindingList keyBindingList;
    private GuiButton buttonReset;

    public GuiControls(GuiScreen screen, GameSettings settings) {
        this.parentScreen = screen;
        this.options = settings;
    }

    @Override
    public void initGui() {
        this.keyBindingList = new GuiKeyBindingList(this, this.mc);
        this.buttonList.add(new GuiButton(200, width / 2 - 155, height - 29, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonReset = new GuiButton(201, width / 2 - 155 + 160, height - 29, 150, 20, I18n.format("controls.resetAll", new Object[0]));
        this.buttonList.add(this.buttonReset);
        this.screenTitle = I18n.format("controls.title", new Object[0]);
        int i2 = 0;
        GameSettings.Options[] optionsArray = optionsArr;
        int n2 = optionsArr.length;
        int n3 = 0;
        while (n3 < n2) {
            GameSettings.Options gamesettings$options = optionsArray[n3];
            if (gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), width / 2 - 155 + i2 % 2 * 160, 18 + 24 * (i2 >> 1), gamesettings$options));
            } else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), width / 2 - 155 + i2 % 2 * 160, 18 + 24 * (i2 >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
            }
            ++i2;
            ++n3;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.keyBindingList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 201) {
            KeyBinding[] keyBindingArray = this.mc.gameSettings.keyBindings;
            int n2 = this.mc.gameSettings.keyBindings.length;
            int n3 = 0;
            while (n3 < n2) {
                KeyBinding keybinding = keyBindingArray[n3];
                keybinding.setKeyCode(keybinding.getKeyCodeDefault());
                ++n3;
            }
            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (button.id < 100 && button instanceof GuiOptionButton) {
            this.options.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttonId != null) {
            this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.buttonId != null) {
            if (keyCode == 1) {
                this.options.setOptionKeyBinding(this.buttonId, 0);
            } else if (keyCode != 0) {
                this.options.setOptionKeyBinding(this.buttonId, keyCode);
            } else if (typedChar > '\u0000') {
                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }
            this.buttonId = null;
            this.time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        GuiControls.drawCenteredString(this.fontRendererObj, this.screenTitle, width / 2, 8, 0xFFFFFF);
        boolean flag = true;
        KeyBinding[] keyBindingArray = this.options.keyBindings;
        int n2 = this.options.keyBindings.length;
        int n3 = 0;
        while (n3 < n2) {
            KeyBinding keybinding = keyBindingArray[n3];
            if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
                flag = false;
                break;
            }
            ++n3;
        }
        this.buttonReset.enabled = !flag;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

