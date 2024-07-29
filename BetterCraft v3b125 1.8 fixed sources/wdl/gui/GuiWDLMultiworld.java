/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import wdl.gui.Utils;

public class GuiWDLMultiworld
extends GuiScreen {
    private final MultiworldCallback callback;
    private GuiButton multiworldEnabledBtn;
    private boolean enableMultiworld = false;
    private int infoBoxWidth;
    private int infoBoxHeight;
    private int infoBoxX;
    private int infoBoxY;
    private List<String> infoBoxLines;

    public GuiWDLMultiworld(MultiworldCallback callback) {
        this.callback = callback;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        String multiworldMessage = String.valueOf(I18n.format("wdl.gui.multiworld.descirption.requiredWhen", new Object[0])) + "\n\n" + I18n.format("wdl.gui.multiworld.descirption.whatIs", new Object[0]);
        this.infoBoxWidth = 320;
        this.infoBoxLines = Utils.wordWrap(multiworldMessage, this.infoBoxWidth - 20);
        this.infoBoxHeight = this.fontRendererObj.FONT_HEIGHT * (this.infoBoxLines.size() + 1) + 40;
        this.infoBoxX = width / 2 - this.infoBoxWidth / 2;
        this.infoBoxY = height / 2 - this.infoBoxHeight / 2;
        this.multiworldEnabledBtn = new GuiButton(1, width / 2 - 100, this.infoBoxY + this.infoBoxHeight - 30, this.getMultiworldEnabledText());
        this.buttonList.add(this.multiworldEnabledBtn);
        this.buttonList.add(new GuiButton(100, width / 2 - 155, height - 29, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(101, width / 2 + 5, height - 29, 150, 20, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            this.toggleMultiworldEnabled();
        } else if (button.id == 100) {
            this.callback.onCancel();
        } else if (button.id == 101) {
            this.callback.onSelect(this.enableMultiworld);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Utils.drawBorder(32, 32, 0, 0, height, width);
        GuiWDLMultiworld.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.multiworld.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        GuiWDLMultiworld.drawRect(this.infoBoxX, this.infoBoxY, this.infoBoxX + this.infoBoxWidth, this.infoBoxY + this.infoBoxHeight, -1342177280);
        int x2 = this.infoBoxX + 10;
        int y2 = this.infoBoxY + 10;
        for (String s2 : this.infoBoxLines) {
            this.drawString(this.fontRendererObj, s2, x2, y2, 0xFFFFFF);
            y2 += this.fontRendererObj.FONT_HEIGHT;
        }
        GuiWDLMultiworld.drawRect(this.multiworldEnabledBtn.xPosition - 2, this.multiworldEnabledBtn.yPosition - 2, this.multiworldEnabledBtn.xPosition + this.multiworldEnabledBtn.getButtonWidth() + 2, this.multiworldEnabledBtn.yPosition + 20 + 2, -65536);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void toggleMultiworldEnabled() {
        this.enableMultiworld = !this.enableMultiworld;
        this.multiworldEnabledBtn.displayString = this.getMultiworldEnabledText();
    }

    private String getMultiworldEnabledText() {
        return I18n.format("wdl.gui.multiworld." + this.enableMultiworld, new Object[0]);
    }

    public static interface MultiworldCallback {
        public void onCancel();

        public void onSelect(boolean var1);
    }
}

